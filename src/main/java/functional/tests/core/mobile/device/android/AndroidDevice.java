package functional.tests.core.mobile.device.android;

import functional.tests.core.enums.DeviceType;
import functional.tests.core.exceptions.DeviceException;
import functional.tests.core.exceptions.MobileAppException;
import functional.tests.core.log.LoggerBase;
import functional.tests.core.mobile.appium.Client;
import functional.tests.core.mobile.device.Device;
import functional.tests.core.mobile.device.IDevice;
import functional.tests.core.mobile.find.Wait;
import functional.tests.core.mobile.settings.MobileSettings;
import functional.tests.core.utils.FileSystem;
import functional.tests.core.utils.OSUtils;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.html5.Location;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * TODO(dtopuzov): Add docs for everything in this class.
 */
public class AndroidDevice implements IDevice {

    private static final LoggerBase LOGGER_BASE = LoggerBase.getLogger("AndroidDevice");
    public int maxUsedMemory = -1;
    public int appLaunchTime = -1;
    private Client client;
    private Adb adb;
    private MobileSettings settings;


    /**
     * Init Android device.
     *
     * @param client
     * @param settings
     */
    public AndroidDevice(Client client, MobileSettings settings) {
        this.client = client;
        this.settings = settings;
        this.adb = new Adb(this.settings);
    }

    /**
     * Generates device id for emulators based on Android version.
     * <p>
     * Algorithm:
     * Main port is 5 next two numbers comes from platform version and last one is like minor version * 2.
     *
     * @param platformVersion Android version (for example 6.0).
     * @return Device id (for example emulator-5600).
     */
    public static String generateDeviceId(double platformVersion) {
        // Main port is 5 next two numbers comes from platform version and last one is like minor version * 2
        String mainPort = "5";
        String platformVersionString = platformVersion + "";
        String port = String.format("emulator-%s%s", mainPort, platformVersionString.replace(".", ""));
        int platformVersionMinor = (int) ((platformVersion - (int) platformVersion) * 10);
        int endPort = platformVersionMinor * 2;
        port += endPort + "";
        return port;
    }

    @Override
    public String getName() {
        return this.settings.deviceName;
    }

    @Override
    public DeviceType getType() {
        return this.settings.deviceType;
    }

    @Override
    public String getId() {
        return this.settings.deviceId;
    }

    /**
     * TODO(): Add docs.
     *
     * @return
     */
    public int getMaxUsedMemory() {
        return this.maxUsedMemory;
    }

    /**
     * TODO(): Add docs.
     *
     * @param maxUsedMemory
     */
    public void setMaxUsedMemory(int maxUsedMemory) {
        this.maxUsedMemory = maxUsedMemory;
    }

    @Override
    public IDevice start() throws TimeoutException, DeviceException {

        if (this.getType() == DeviceType.Emulator) {
            this.startEmulator();
        }

        // Check if device is available
        if (this.settings.isRealDevice) {
            this.startRealDevice();
        }

        // Uninstall test apps
        if (!this.settings.debug) {
            this.uninstallApps(Device.uninstallAppsList());
        }

        // Handle error activity
        this.adb.closeErrorActivty(this.getId());

        // Start appium client (this will install app under test)
        this.client.initDriver();

        return this;
    }

    @Override
    public void stop() {
        if (this.getType() == DeviceType.Emulator) {
            if (this.settings.debug) {
                LOGGER_BASE.info("[DEBUG MODE] Skip emulator stop.");
            } else {
                this.adb.stopAllEmulators();
            }
        }
    }

    @Override
    public void installApp(String testAppName, String packageId) throws IOException {
        this.adb.installApp(testAppName, packageId);
    }

    @Override
    public void startApplication(String packageId) {
        this.adb.startApplication(packageId);
    }

    @Override
    public void restartApp() {
        String id = this.settings.packageId;
        String name = this.settings.testAppFriendlyName;
        LOGGER_BASE.info("Restarting " + name);
        this.adb.stopApplication(id);
        Wait.sleep(250);
        this.adb.startApplication(id);
        Wait.sleep(250);
        LOGGER_BASE.info("Restarted.");
    }

    @Override
    public void closeApp() {
        if (this.client.driver != null) {
            this.client.driver.closeApp();
        } else {
            LOGGER_BASE.error("Failed to close application.");
        }
    }

    @Override
    public void stopApps(List<String> uninstallAppsList) {
        List<String> installedApps = this.adb.getInstalledApps();

        for (String appToUninstall : uninstallAppsList) {
            for (String appId : installedApps) {
                if (appId.contains(appToUninstall)) {
                    LOGGER_BASE.info("Stop " + appId);
                    this.adb.stopApp(appId);
                }
            }
        }
    }

    @Override
    public void uninstallApps(List<String> uninstallAppsList) {
        List<String> installedApps = this.adb.getInstalledApps();

        for (String appToUninstall : uninstallAppsList) {
            for (String appId : installedApps) {
                if (appId.contains(appToUninstall)) {
                    this.adb.uninstallApp(appId);
                }
            }
        }
    }

    @Override
    public void verifyAppRunning(String packageId) throws MobileAppException {
        boolean isRunning = this.waitAppRunning(packageId, this.settings.defaultTimeout);
        if (!isRunning) {
            String error = "app " + packageId + " is not running.";
            LOGGER_BASE.fatal(error);
            throw new MobileAppException(error);
        }
    }

    @Override
    public boolean isAppRunning(String packageId) {
        return this.adb.isAppRunning(this.getId(), packageId);
    }

    private boolean waitAppRunning(String packageId, int timeOut) {
        long startTime = System.currentTimeMillis();
        boolean isRunning = false;
        while ((System.currentTimeMillis() - startTime) < timeOut * 1000) {
            isRunning = this.isAppRunning(packageId);
            if (isRunning) {
                LOGGER_BASE.info("app " + packageId + " is up and running.");
                break;
            } else {
                LOGGER_BASE.info("app " + packageId + " is not running. Wait for it...");
                Wait.sleep(1000);
            }
        }
        return isRunning;
    }

    @Override
    public void runAppInBackGround(int seconds) {
        // driver.runAppInBackground(seconds) do not work correct!
        // Please see this: https://github.com/appium/appium/issues/5176#issuecomment-238630864
        this.adb.runAdbCommand(this.getId(), "shell input keyevent 3");
        Wait.sleep(seconds * 1000);
        this.adb.runAdbCommand(this.getId(), "shell monkey -p " + this.settings.packageId + " -c android.intent.category.LAUNCHER 1");

        // Handle error activity hack for newer Android emulators.
        this.adb.closeErrorActivty(this.getId());

        // Additional notes: If UiAutomator2 is used on Api21 lines above will fail (only for Api21).
    }

    @Override
    public String getContent(String testName) throws IOException {
        return FileSystem.readFile(this.settings.consoleLogDir + File.separator + "logcat_" + testName + ".log");
    }

    @Override
    public void writeConsoleLogToFile(String fileName) throws IOException {
        try {
            if (this.adb.isAvailable(this.getId())) {
                String logFromAndroid = this.adb.runAdbCommand(this.getId(), "logcat -d");
                String logLocation = this.settings.consoleLogDir + File.separator + "logcat_" + fileName + ".log";
                FileWriter writer = new FileWriter(logLocation, true);
                writer.write(logFromAndroid);
                writer.close();
            } else {
                LOGGER_BASE.error("Failed to get console logs. Device " + this.getId() + " not found");
            }
        } catch (Exception ex) {
            LOGGER_BASE.error("Failed to get logcat with command: adb -s " + this.getId() + " logcat -d");
            LOGGER_BASE.error(ex.getMessage());
        }
    }

    @Override
    public void pushFile(String localPath, String remotePath) throws Exception {
        this.adb.pushFile(this.getId(), localPath, remotePath);
    }

    @Override
    public void pullFile(String remotePath, String destinationFolder) throws Exception {
        this.adb.pullFile(this.getId(), remotePath, destinationFolder);
    }

    @Override
    public void cleanConsoleLog() {
        try {
            this.client.driver.manage().logs().get("logcat");
        } catch (Exception e) {
            LOGGER_BASE.warn("Failed to cleanup logs.");
        }
    }

    @Override
    public String getStartupTime(String appId) {
        Integer matchesSubstring = 0;
        String time, seconds, miliseconds;
        time = seconds = miliseconds = null;
        String[] logEntries = this.adb.getAdbLog(this.getId()).split("\\r?\\n");
        for (String line : logEntries) {

            // Sample row adb output:
            // I/ActivityManager( 1053): Displayed org.nativescript.TestApp/com.tns.NativeScriptActivity: +18s985ms

            if (line.contains("Displayed " + this.settings.packageId) && !line.contains("(total")) {
                time = line;
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
                    } else {
                        time = "-1";
                    }
                }
            }
        }
        return time;
    }

    private int countSubstring(String subStr, String str) {
        return (str.length() - str.replace(subStr, "").length()) / subStr.length();
    }

    @Override
    public int getMemUsage(String appPackageId) {
        if (this.adb.isAvailable(this.getId()) || this.adb.checkIfEmulatorIsRunning(this.getId())) {
            String command = "shell dumpsys meminfo | grep " + appPackageId;
            String output = this.adb.runAdbCommand(this.getId(), command);
            if (output.contains(this.settings.packageId)) {
                String memString = output.split(":")[0].toLowerCase();
                memString = memString.replace("kb", "").replace(",", "").replace("k", "").trim();
                return Integer.parseInt(memString);
            } else {
                LOGGER_BASE.error("\"dumpsys meminfo\" command failed!");
                return 0;
            }
        } else {
            LOGGER_BASE.error("Device not available!");
            return 0;
        }

    }

    @Override
    public void logAppStartupTime(String packageId) {
        // Get logs for initial app startup
        try {
            // Get startup time
            String startTime = this.getStartupTime(packageId);
            this.appLaunchTime = Integer.parseInt(startTime);
            LOGGER_BASE.info(this.settings.testAppFriendlyName + " loaded in " + startTime + " ms.");
            this.writeConsoleLogToFile("init");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setLocation(Location location) {
        ((AndroidDriver) this.client.driver).setLocation(location);
    }

    @Override
    public void logPerfInfo() throws IOException {
        if (this.settings.isRealDevice) {
            String fileName = "perfInfo.csv";
            String localFilePath = this.settings.baseLogDir + File.separator + fileName;
            String storageFilePath = this.settings.perfDir + File.separator + fileName;

            StringBuilder sb = new StringBuilder();
            sb.append(this.settings.buildRunStartupTime + ",");
            sb.append(OSUtils.getHostName() + ",");
            sb.append(this.maxUsedMemory + ",");
            sb.append(this.appLaunchTime + ",");
            sb.append(this.getAppSize() + '\n');

            LOGGER_BASE.info("Maximum used memory: " + this.maxUsedMemory);
            LOGGER_BASE.info("Application launch time: " + this.appLaunchTime);
            LOGGER_BASE.info("Application size: " + this.getAppSize());

            String perfInfoLog = sb.toString();
            String rowHeader = "Timestamp,Hostname,Memory,Launch,AppSize" + System.lineSeparator();

            // local file
            FileSystem.writeCsvFile(localFilePath, perfInfoLog, rowHeader);

            // storage file
            FileSystem.writeCsvFile(storageFilePath, perfInfoLog, rowHeader);
        } else {
            LOGGER_BASE.info("Do not log perf info for emulators.");
        }
    }

    /**
     * Get file size of the aplication.
     *
     * @return File size of the application in kB.
     */
    private String getAppSize() {
        String appPath = this.settings.BASE_TEST_APP_DIR + File.separator + this.settings.testAppName;
        return String.valueOf(FileSystem.getFileSize(appPath));
    }

    /**
     * TODO(): Add docs.
     *
     * @param appPackage
     * @param appActivity
     */
    public void startActivity(String appPackage, String appActivity) {
        ((AndroidDriver) this.client.driver).startActivity(appPackage, appActivity);
    }

    private void startEmulator() throws DeviceException, TimeoutException {
        // Kill all emulators if reuseDevice is false.
        if (!this.settings.reuseDevice && !this.settings.debug) {
            this.stop();
        }
        if (this.adb.checkIfEmulatorIsRunning(this.getId())) {
            LOGGER_BASE.info(this.getId() + " is already running. Will reuse it!");
        } else {
            // Create emulator (if it does not exists)
            if (this.settings.android.emulatorCreateOptions != null) {
                this.adb.createEmulator(this.getName(), this.settings.android.emulatorCreateOptions);
            }

            // Start
            String port = this.getId().split("-")[1];
            this.adb.startEmulator(this.getName(), Integer.valueOf(port));

            // Wait until emulator boot
            this.adb.waitUntilEmulatorBoot(this.getId(), this.settings.deviceBootTimeout);

            // Unlock
            int waitToUnlock = 6000;
            while (this.adb.isLocked(this.getId()) && waitToUnlock >= 0) {
                LOGGER_BASE.info("device is locked. Unlocking it ...");
                this.adb.unlock(this.getId());
                Wait.sleep(3000);
                LOGGER_BASE.info("device locked: " + String.valueOf(this.adb.isLocked(this.getId())));
                waitToUnlock -= 3000;
            }

            if (this.adb.isLocked(this.getId())) {
                LOGGER_BASE.info("device is locked. We couldn't unlock it!!!");
                OSUtils.getScreenshot("HostOS_Emulator_Failed_UnLock", this.settings);
            }
        }
    }

    private void startRealDevice() throws TimeoutException, DeviceException {
        boolean isDeviceAvailable = this.adb.isAvailable(this.getId());
        if (!isDeviceAvailable && this.settings.debug) {
            AndroidDevice.LOGGER_BASE.error(String.format("Device %s is not connected!!!", this.getId()));
            this.settings.deviceId = this.adb.findConnectedDeviceViaUsb();
            AndroidDevice.LOGGER_BASE.error(String.format("Connect to %s device!!!", this.getId()));
        } else if (isDeviceAvailable) {
            this.adb.waitForDevice(this.getId(), this.settings.shortTimeout);
        } else {
            throw new DeviceException(String.format("Device %s is not connected!!!", this.getId()));
        }
    }
}
