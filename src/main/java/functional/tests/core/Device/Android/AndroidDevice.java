package functional.tests.core.Device.Android;

import functional.tests.core.Appium.Client;
import functional.tests.core.Device.IDevice;
import functional.tests.core.Enums.DeviceType;
import functional.tests.core.Enums.PlatformType;
import functional.tests.core.Exceptions.AppiumException;
import functional.tests.core.Exceptions.DeviceException;
import functional.tests.core.Find.Wait;
import functional.tests.core.Log.Log;
import functional.tests.core.OSUtils.FileSystem;
import functional.tests.core.OSUtils.OSUtils;
import functional.tests.core.Settings.Settings;
import org.openqa.selenium.logging.LogEntry;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class AndroidDevice implements IDevice {

    private static boolean available(int port) {
        Log.debug("--------------Testing port " + port);
        Socket s = null;
        try {
            s = new Socket("localhost", port);

            // If the code makes it this far without an exception it means
            // something is using the port and has responded.
            Log.debug("--------------Port " + port + " is not available");
            return false;
        } catch (IOException e) {
            Log.debug("--------------Port " + port + " is available");
            return true;
        } finally {
            if (s != null) {
                try {
                    s.close();
                } catch (IOException e) {
                    throw new RuntimeException("You should handle this error.", e);
                }
            }
        }
    }

    @Override
    public List<String> getDevices() {
        return Adb.getDevices();
    }

    @Override
    public void installApp(String testAppName, String packageId) throws IOException {
        Adb.installApp(testAppName, packageId);
    }

    @Override
    public void initDevice() throws TimeoutException {
        try {
            // Device
            if (Settings.isRealDevice) {
                Adb.waitForDevice(Settings.deviceId, Settings.deviceBootTimeout);
            }
            // Emulator
            else {
                // Create
                if (Settings.emulatorCreateOptions != null) {
                    // If the emulator exists, it will do nothing
                    Adb.createEmulator(Settings.deviceName, Settings.emulatorCreateOptions);
                }

                // Start
                String port = Settings.deviceId.split("-")[1];
                Adb.startEmulator(Settings.deviceName, Integer.valueOf(port));

                // Wait
                Adb.waitUntilEmulatorBoot(Settings.deviceId, Settings.deviceBootTimeout);

                int waitToUnlock = 6000;
                // Unlock
                while (Adb.isLocked(Settings.deviceId) && waitToUnlock >= 0) {
                    Log.info("Device is locked. Unlocking it ...");
                    Adb.unlock(Settings.deviceId);
                    Wait.sleep(3000);
                    Log.info("Device locked: " + String.valueOf(Adb.isLocked(Settings.deviceId)));
                    waitToUnlock -= 3000;
                }

                if (Adb.isLocked(Settings.deviceId)) {
                    Log.info("Device is locked. We couldn't unlock it!!!");
                    OSUtils.getScreenshot("HostOS_Emulator_Failed_UnLock");
                }
            }
        } catch (TimeoutException timeout) {
            // Device
            if (Settings.isRealDevice) {
                String error = "Failed to find device " + Settings.deviceId +
                        " in " + String.valueOf(Settings.deviceBootTimeout) + " seconds.";
                Log.fatal(error);
                throw new TimeoutException(error);
            }
            // Emulator - if init device failed for emulator,
            // than recreate the emulator and try again
            else {
                try {
                    // Double timeout
                    Log.error("TimeoutException. Retrying init device ...");
                    Settings.deviceBootTimeout = Settings.deviceBootTimeout * 2;
                    Log.info("Device boot timeout changed to " + String.valueOf(Settings.deviceBootTimeout));
                    OSUtils.getScreenshot("HostOS_Emulator_Failed_ToBoot");

                    // Stop
                    this.stopDevice();

                    // Recreate
                    Adb.createEmulator(Settings.deviceName, Settings.emulatorCreateOptions, true);

                    // Start
                    String port = Settings.deviceId.split("-")[1];
                    Adb.startEmulator(Settings.deviceName, Integer.valueOf(port));

                    // Wait
                    Adb.waitUntilEmulatorBoot(Settings.deviceId, Settings.deviceBootTimeout);

                    // Unlock
                    if (Adb.isLocked(Settings.deviceId)) {
                        Log.info("Device is locked. Unlocking it ...");
                        Adb.unlock(Settings.deviceId);
                        Wait.sleep(3000);
                        Log.info("Device locked: " + String.valueOf(Adb.isLocked(Settings.deviceId)));
                    }
                } catch (TimeoutException secondTimeout) {
                    OSUtils.getScreenshot("HostOS_Emulator_Failed_ToBoot_After_Retry");
                    try {
                        throw secondTimeout;
                    } catch (TimeoutException e) {
                        e.printStackTrace();
                    }
                } catch (DeviceException e) {
                    e.printStackTrace();
                }
            }
        } catch (DeviceException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stopDevice() {
        if (Settings.deviceType == DeviceType.Emulator) {
            Adb.stopEmulator();
            // This is to fix the case when you have Androdi Studio 2 and previous CI stop build is force stopped
            int port = Integer.parseInt(Settings.deviceId.substring(Settings.deviceId.lastIndexOf("-") + 1));
            if (!available(port)) {
                Log.info("Port " + port + " in use.");
                Adb.stopAdb();
                Adb.startAdb();
            }
            if (!available(port)) {
                Log.fatal("Port " + port + " still in use. Most likely emulator will not start.");
            }
        }
    }

    @Override
    public void stopApps(List<String> uninstallAppsList) {
        List<String> installedApps = Adb.getInstalledApps();

        for (String appToUninstall : uninstallAppsList) {
            for (String appId : installedApps) {
                if (appId.contains(appToUninstall)) {
                    Log.info("Stop " + appId);
                    Adb.stopApp(appId);
                }
            }
        }
    }

    @Override
    public void uninstallApps(List<String> uninstallAppsList) {
        List<String> installedApps = Adb.getInstalledApps();

        for (String appToUninstall : uninstallAppsList) {
            for (String appId : installedApps) {
                if (appId.contains(appToUninstall)) {
                    Adb.uninstallApp(appId);
                }
            }
        }
    }

    @Override
    public String getContent(String testName) throws IOException {
        String logContent = FileSystem.readFile(Settings.consoleLogDir + File.separator + "logcat_" + testName + ".log");

        return logContent;
    }

    @Override
    public void writeConsoleLogToFile(String fileName) throws IOException {
        try {
            List<LogEntry> logEntries = Client.driver.manage().logs().get("logcat").getAll();
            String logLocation = Settings.consoleLogDir + File.separator + "logcat_" + fileName + ".log";
            FileWriter writer = new FileWriter(logLocation, true);
            for (LogEntry log : logEntries) {
                writer.write(log.toString());
                writer.write(System.lineSeparator());
            }
            writer.close();
        } catch (Exception e) {
            try {
                String logFromAndroid = Adb.runAdbCommand(Settings.deviceId, "logcat -d");
                String logLocation = Settings.consoleLogDir + File.separator + "logcat_" + fileName + ".log";
                FileWriter writer = new FileWriter(logLocation, true);
                writer.write(logFromAndroid);
                writer.close();
            } catch (Exception ex) {

                Log.error("Failed to get logcat with command: adb -s " + Settings.deviceId + " logcat -d");
                Log.error(ex.getMessage());
            }
            Log.warn("Failed to get logcat.");
            e.printStackTrace();
        }
    }

    @Override
    public void verifyAppRunning(String deviceId, String appId) throws AppiumException, IOException {
        int startUpTimeOut = 10;
        boolean isRunning = waitAppRunning(deviceId, appId, startUpTimeOut);
        if (!isRunning) {
            // Restart all and try again
            Client.driver.resetApp();
            Wait.sleep(startUpTimeOut);
            isRunning = waitAppRunning(deviceId, appId, startUpTimeOut);
            if (isRunning) {
                Log.info("App " + appId + " is up and running.");
            } else {
                Log.fatal("App " + appId + " is not running.");
                throw new AppiumException("App " + appId + " is not running.");
            }
        }
    }

    @Override
    public void pushFile(String deviceId, String localPath, String remotePath) throws Exception {
        Adb.pushFile(deviceId, localPath, remotePath);
    }

    @Override
    public void pullFile(String deviceId, String remotePath, String destinationFolder) throws Exception {
        Adb.pullFile(deviceId, remotePath, destinationFolder);
    }

    @Override
    public void pullFile(String deviceId, String remotePath) throws Exception {
        Adb.pullFile(deviceId, remotePath);
    }

    @Override
    public void cleanConsoleLog() {
        try {
            Client.driver.manage().logs().get("logcat");
        } catch (Exception e) {
            Log.warn("Failed to cleanup logs.");
        }
    }

    private boolean waitAppRunning(String deviceId, String appId, int timeOut) {
        int initTimeOut = 5;
        if (Settings.deviceName.toLowerCase().contains("arm")) {
            initTimeOut = initTimeOut * 10;
        }
        Wait.sleep(initTimeOut * 1000);
        long startTime = System.currentTimeMillis();
        boolean isRuning = false;
        while ((System.currentTimeMillis() - startTime) < timeOut * 1000) {
            isRuning = this.isAppRunning(deviceId, appId);
            if (isRuning) {
                Log.info("App " + appId + " is up and running.");
                break;
            } else {
                Log.info("App " + appId + " is not running. Wait for it...");
                Wait.sleep(1000);
            }
        }
        return isRuning;
    }

    @Override
    public String getStartupTime(String appId) throws IOException {
        Integer matchesSubstring = 0;
        String time, seconds, miliseconds;
        time = seconds = miliseconds = null;
        String[] logEntries = Adb.getAdbLog(Settings.deviceId).split("\\r?\\n");
        for (String line : logEntries) {
            // Displayed org.nativescript.nativescriptsdkexamplesng/com.tns.NativeScriptActivity: +19s259ms (total +2m58s7ms)
            if (line.contains("Displayed " + Settings.packageId) && !line.contains("(total")) {
                time = line;
                time = time.substring(time.indexOf("Displayed"));
                Log.info(time);

                time = time.substring(time.lastIndexOf("+"));
                time = time.replace(" ", "");
                matchesSubstring = this.countSubstring("s", time);

                // +222ms
                if (matchesSubstring == 1) {
                    miliseconds = time.substring(time.indexOf("+") + 1, time.indexOf("ms"));
                    if (miliseconds != null) {
                        time = miliseconds;
                    }
                }

                // +4s222ms
                if (matchesSubstring == 2) {
                    seconds = time.substring(time.indexOf("+") + 1, time.indexOf("s"));
                    miliseconds = time.substring(time.indexOf("s") + 1, time.indexOf("ms"));

                    // +4s22ms
                    if (miliseconds.length() == 2) {
                        miliseconds = "0".concat(miliseconds);
                    }

                    // +4s2ms
                    if (miliseconds.length() == 1) {
                        miliseconds = "00".concat(miliseconds);
                    }

                    if (seconds != null && miliseconds != null) {
                        time = seconds + miliseconds;
                    }
                }
                break;
            }
        }
        return time;
    }

    public int countSubstring(String subStr, String str) {
        return (str.length() - str.replace(subStr, "").length()) / subStr.length();
    }

    @Override
    public boolean isAppRunning(String deviceId, String appId) {
        if (Settings.platform == PlatformType.Andorid) {
            return Adb.isAppRunning(deviceId, appId);
        } else {
            throw new NotImplementedException();
        }
    }
}
