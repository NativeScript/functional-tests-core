package functional.tests.core.Device;

import functional.tests.core.Appium.Client;
import functional.tests.core.Device.Android.Adb;
import functional.tests.core.Device.Android.AndroidDevice;
import functional.tests.core.Device.iOS.iOSDevice;
import functional.tests.core.Enums.DeviceType;
import functional.tests.core.Enums.PlatformType;
import functional.tests.core.Exceptions.DeviceException;
import functional.tests.core.Exceptions.UnknownPlatformException;
import functional.tests.core.Find.Wait;
import functional.tests.core.Log.Log;
import functional.tests.core.OSUtils.Archive;
import functional.tests.core.OSUtils.FileSystem;
import functional.tests.core.Settings.Settings;
import org.openqa.selenium.logging.LogEntry;
import org.testng.Assert;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class BaseDevice {

    private static List<String> uninstallAppsList() {
        return Arrays.asList("org.nativescript", "com.telerik");
    }

    public static void initDevice() throws UnknownPlatformException, TimeoutException, InterruptedException, DeviceException {
        if (Settings.platform == PlatformType.Andorid) {
            try {
                AndroidDevice.initDevice();
            } catch (TimeoutException timeout) {
                Log.error("TimeoutException. Retry init device...");
                AndroidDevice.stopDevice();
                AndroidDevice.initDevice();
            }
        } else if (Settings.platform == PlatformType.iOS) {
            iOSDevice.initDevice();
        }
    }

    public static void stopDevice() throws UnknownPlatformException {
        if (Settings.platform == PlatformType.Andorid) {
            AndroidDevice.stopDevice();
        } else if (Settings.platform == PlatformType.iOS) {
            iOSDevice.stopDevice();
        }
    }

    public static void initTestApp() throws IOException {

        // Uninstall apps from real devices
        if (Settings.deviceType == DeviceType.Android) {
            AndroidDevice.uninstallApps(uninstallAppsList());
        } else if (Settings.deviceType == DeviceType.iOS) {
            iOSDevice.uninstallApps(uninstallAppsList());

            // iOS7 devices use old appium version
            // In older appium version installation of test app is not stable
            // installApp method will deploy with ideviceinstaller
            if (Settings.platformVersion.contains("7")) {
                iOSDevice.installApp(Settings.testAppName);
            }

        } else if (Settings.deviceType == DeviceType.Simulator) {
            // Delete existing extracted applications
            FileSystem.deletePath(Settings.baseTestAppDir + File.separator + Settings.testAppName);

            // Extact test app archive
            File tarFile = new File(Settings.baseTestAppDir + File.separator + Settings.testAppArchive);
            File dest = new File(Settings.baseTestAppDir);
            Archive.extractArchive(tarFile, dest);
        }
    }

    public static void stopTestApp() {
        if (Settings.deviceType == DeviceType.Android) {
            AndroidDevice.stopApps(uninstallAppsList());
        } else if (Settings.deviceType == DeviceType.iOS) {
            iOSDevice.stopApps(uninstallAppsList());
        }
    }

    public static void pushFile(String deviceId, String localPath, String remotePath) throws Exception {
        if (Settings.platform == PlatformType.Andorid) {
            Adb.pushFile(deviceId, localPath, remotePath);
        } else {
            throw new NotImplementedException();
        }
    }

    public static void cleanConsoleLog() {
        try {
            if (Settings.platform == PlatformType.Andorid) {
                Client.driver.manage().logs().get("logcat");
            } else {
                Client.driver.manage().logs().get("syslog");
                Client.driver.manage().logs().get("crashlog");
            }
        } catch (Exception e) {
            Log.warn("Failed to cleanup logs.");
        }
    }

    public static void writeConsoleLogToFile(String fileName) throws IOException {
        if (Settings.platform == PlatformType.Andorid) {
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
                Log.warn("Failed to get logcat.");
                e.printStackTrace();
            }
        } else {
            try {
                List<LogEntry> logEntries = Client.driver.manage().logs().get("syslog").getAll();
                if (logEntries.size() >= 1) {
                    String logLocation = Settings.consoleLogDir + File.separator + "syslog_" + fileName + ".log";
                    FileWriter writer = new FileWriter(logLocation, true);
                    for (LogEntry log : logEntries) {
                        writer.write(log.toString());
                        writer.write(System.lineSeparator());
                    }
                    writer.close();
                }
            } catch (Exception e) {
                Log.warn("Failed to get syslog.");
                e.printStackTrace();
            }

            try {
                List<LogEntry> logEntries = Client.driver.manage().logs().get("crashlog").getAll();
                if (logEntries.size() >= 1) {
                    String logLocation = Settings.consoleLogDir + File.separator + "crashlog_" + fileName + ".log";
                    FileWriter writer = new FileWriter(logLocation, true);
                    for (LogEntry log : logEntries) {
                        writer.write(log.toString());
                        writer.write(System.lineSeparator());
                    }
                    writer.close();
                }
            } catch (Exception e) {
                Log.warn("Failed to get crashlog.");
                e.printStackTrace();
            }
        }
    }

    public static void assertLogContains(String str) throws IOException {
        String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
        writeConsoleLogToFile(testName);
        String logContent;
        if (Settings.platform == PlatformType.Andorid) {
            logContent = FileSystem.readFile(Settings.consoleLogDir + File.separator + "logcat_" + testName + ".log");
        } else {
            logContent = FileSystem.readFile(Settings.consoleLogDir + File.separator + "syslog_" + testName + ".log");
        }
        Assert.assertTrue(logContent.contains(str), "The log does not contain '" + str + "'.");
        Log.info("The log contains '" + str + "'.");
    }

    public static void assertLogNotContains(String str) throws IOException {
        String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
        writeConsoleLogToFile(testName);
        String logContent;
        if (Settings.platform == PlatformType.Andorid) {
            logContent = FileSystem.readFile(Settings.consoleLogDir + File.separator + "logcat_" + testName + ".log");
        } else {
            logContent = FileSystem.readFile(Settings.consoleLogDir + File.separator + "syslog_" + testName + ".log");
        }
        Assert.assertFalse(logContent.contains(str), "The log contains '" + str + "'.");
        Log.info("The log does not contains '" + str + "'.");
    }

    public static boolean isAppRunning(String deviceId, String appId) {
        if (Settings.platform == PlatformType.Andorid) {
            return Adb.isAppRunning(deviceId, appId);
        } else {
            throw new NotImplementedException();
        }
    }

    public static void verifyAppRunning(String deviceId, String appId) {
        if (Settings.platform == PlatformType.Andorid) {
            Wait.sleep(10000);
            boolean isRuning = isAppRunning(deviceId, appId);
            if (isRuning) {
                Log.info("App " + appId + " is up and running.");
            } else {
                Log.logScreen("init", "First screen");
                Log.fatal("App " + appId + " is not running.");
                Assert.assertTrue(isRuning, "App " + appId + " is not running.");
            }
        } else {
            // TODO: Implement it
        }
    }
}
