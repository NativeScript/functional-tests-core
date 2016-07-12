package functional.tests.core.Settings;

import functional.tests.core.Enums.DeviceType;
import functional.tests.core.Enums.OSType;
import functional.tests.core.Enums.PlatformType;
import functional.tests.core.Exceptions.UnknownOSException;
import functional.tests.core.Log.Log;
import functional.tests.core.OSUtils.Archive;
import functional.tests.core.OSUtils.FileSystem;
import functional.tests.core.OSUtils.OSUtils;
import functional.tests.core.Screenshot.VerificationType;
import io.appium.java_client.remote.AutomationName;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Settings {
    private static final String storageEvnironmentVariable = "STORAGE";
    private static final String userDir = System.getProperty("user.dir");
    private static final String appConfigPath = System.getProperty("appConfig");
    private static final String baseResourcesDir = userDir + File.separator + "resources";
    private static final String baseOutputDir = userDir + File.separator + "target" + File.separator + "surefire-reports";
    private static final String storageMachine = "mcsofnsbuild03";
    private static final String sharedFolder = "tns-images";
    private static Properties properties;

    public static OSType OS;
    public static PlatformType platform;
    public static DeviceType deviceType;
    public static boolean isRealDevice;
    public static boolean restartApp;
    public static boolean debug;
    public static boolean takeScreenShotAfterTest;
    public static boolean acceptAlerts;
    public static VerificationType imageVerificationType;
    public static boolean logImageVerificationResults;
    public static int shortTimeout;
    public static int defaultTimeout;
    public static int deviceBootTimeout;
    public static String platformVersion;
    public static String deviceId;
    public static String deviceName;
    public static String testAppName;
    public static String testAppFriendlyName;
    public static String packageId;
    public static String defaultActivity = "com.tns.NativeScriptActivity"; // This is hardcoded in runtime now
    public static String testAppArchive;
    public static String appiumVersion;
    public static String automationName;
    public static String emulatorOptions;
    public static String emulatorCreateOptions;
    public static String simulatorType;
    public static String baseLogDir;
    public static String consoleLogDir;
    public static String screenshotOutDir;
    public static String screenshotResDir;
    public static String testAppImageFolder;
    public static String appiumLogFile;
    public static String appiumLogLevel;
    public static final String baseTestAppDir = userDir + File.separator + "testapp";
    public static final String baseTestDataDir = baseResourcesDir + File.separator + "testdata";

    public static final int defaultTapDuration = 250;

    private static OSType getOSType() {
        OSType detectedOS;

        String OS = System.getProperty("os.name", "generic").toLowerCase();
        if ((OS.contains("mac")) || (OS.contains("darwin"))) {
            detectedOS = OSType.MacOS;
        } else if (OS.contains("win")) {
            detectedOS = OSType.Windows;
        } else if (OS.contains("nux")) {
            detectedOS = OSType.Linux;
        } else {
            detectedOS = OSType.Other;
        }

        return detectedOS;
    }

    public static String getStorage() {
        String env = System.getenv(storageEvnironmentVariable);
        if (env == null) {
            Log.info(String.format("LOCAL STORAGE %s", baseResourcesDir));
            return baseResourcesDir;
        } else {
            Log.info(String.format("%s=%s%n", storageEvnironmentVariable, env));
            return env;
        }
    }

    private static void setupLocations() throws IOException {
        baseLogDir = baseOutputDir + File.separator + "logs";
        consoleLogDir = baseLogDir + File.separator + "console";
        screenshotOutDir = baseOutputDir + File.separator + "screenshots";
        screenshotResDir = getStorage() + File.separator + "images";
        appiumLogFile = baseLogDir + File.separator + "appium.log";

        try {
            File baseScreenshotDirLocation = new File(screenshotOutDir);
            baseScreenshotDirLocation.mkdirs();
            FileUtils.cleanDirectory(baseScreenshotDirLocation);
        } catch (IOException e) {
            Log.fatal("Failed to cleanup and create screenshot output folder.");
            throw new IOException(e);
        }

        try {
            File baseLogDirLocation = new File(baseLogDir);
            baseLogDirLocation.mkdirs();
            File consoleLogDirLocation = new File(consoleLogDir);
            consoleLogDirLocation.mkdirs();
            FileUtils.cleanDirectory(consoleLogDirLocation);
        } catch (IOException e) {
            Log.fatal("Failed to cleanup and create logs folder.");
            throw new IOException(e);
        }
    }

    private static Properties readProperties() throws Exception {
        String appConfigFile = userDir + File.separator + appConfigPath;
        try {
            InputStream input = new FileInputStream(appConfigFile);
            Properties prop = new Properties();
            prop.load(input);
            return prop;
        } catch (Exception e) {
            Log.fatal("Failed to read and init settings. Please check if " + appConfigFile + " exists.");
            throw new Exception(e);
        }
    }

    private static PlatformType getPlatformType() {
        String platformTypeString = properties.getProperty("platformName");
        if (platformTypeString.equalsIgnoreCase("Android")) {
            return PlatformType.Andorid;
        } else if (platformTypeString.equalsIgnoreCase("iOS")) {
            return PlatformType.iOS;
        } else {
            return PlatformType.Other;
        }
    }

    private static DeviceType getDeviceType() {
        String deviceTypeString = properties.getProperty("deviceType");
        if (deviceTypeString.equalsIgnoreCase("android")) {
            return DeviceType.Android;
        } else if (deviceTypeString.equalsIgnoreCase("ios")) {
            return DeviceType.iOS;
        } else if (deviceTypeString.toLowerCase().contains("emu")) {
            return DeviceType.Emulator;
        } else if (deviceTypeString.toLowerCase().contains("sim")) {
            return DeviceType.Simulator;
        } else {
            return DeviceType.Other;
        }
    }

    private static String getPackageId() throws Exception {
        String appId = "";
        if (Settings.platform == PlatformType.Andorid) {
            String apptExecutableName = "aapt";
            if (Settings.OS == OSType.Windows) {
                apptExecutableName = "aapt.exe";
            }
            File root = new File(System.getenv("ANDROID_HOME") + File.separator + "build-tools");
            File aaptFile = OSUtils.find(root, apptExecutableName);
            if (aaptFile == null) {
                String error = "Failed to find aapt. It is requited for Android tests.";
                Log.fatal(error);
                throw new Exception(error);
            } else {
                String aaptPath = aaptFile.getAbsolutePath();
                String command = aaptPath + " dump badging " + Settings.baseTestAppDir + File.separator + Settings.testAppName;
                String result = OSUtils.runProcess(command);
                String[] list = result.split("\\r?\\n");
                for (String line : list) {
                    if (line.contains("package:")) {
                        appId = line.substring(line.indexOf("'") + 1);
                        appId = appId.substring(0, appId.indexOf("'"));
                    }
                }
            }
        } else if (Settings.deviceType == DeviceType.Simulator) {
            String plistPath = Settings.baseTestAppDir + File.separator + Settings.testAppName + File.separator + "Info.plist";
            File f = new File(plistPath);
            if (f.exists()) {
                String command = "/usr/libexec/PlistBuddy -c 'Print CFBundleIdentifier' " + plistPath;
                String result = OSUtils.runProcess(command);
                String[] list = result.split("\\r?\\n");
                for (String line : list) {
                    if (line.contains(".")) {
                        appId = line.trim();
                    }
                }
            } else {
                throw new Exception("File " + plistPath + " does not exist.");
            }
        } else if (Settings.deviceType == DeviceType.iOS) {
            String ipaPath = Settings.baseTestAppDir + File.separator + Settings.testAppName;
            OSUtils.runProcess("unzip -o " + ipaPath + " -d " + Settings.baseTestAppDir);
            String appName = OSUtils.runProcess("ls " + Settings.baseTestAppDir + File.separator + "Payload").trim().replace("\n", "").replace("\r", "");
            String plistPath = Settings.baseTestAppDir + File.separator + "Payload" + File.separator + appName + File.separator + "Info.plist";
            File f = new File(plistPath);
            if (f.exists()) {
                String command = "/usr/libexec/PlistBuddy -c 'Print CFBundleIdentifier' " + plistPath;
                String result = OSUtils.runProcess(command);
                String[] list = result.split("\\r?\\n");
                for (String line : list) {
                    if (line.contains(".")) {
                        appId = line.trim();
                    }
                }
                FileSystem.deletePath(Settings.baseTestAppDir + File.separator + "Payload");
            } else {
                throw new Exception("File " + plistPath + " does not exist.");
            }
        }
        return appId;
    }

    private static String getTestAppFriendlyName() throws Exception {
        String appId = "";
        if (Settings.platform == PlatformType.Andorid) {
            String apptExecutableName = "aapt";
            if (Settings.OS == OSType.Windows) {
                apptExecutableName = "aapt.exe";
            }
            File root = new File(System.getenv("ANDROID_HOME") + File.separator + "build-tools");
            File aaptFile = OSUtils.find(root, apptExecutableName);
            if (aaptFile == null) {
                String error = "Failed to find aapt. It is requited for Android tests.";
                Log.fatal(error);
                throw new Exception(error);
            } else {
                String aaptPath = aaptFile.getAbsolutePath();
                String command = aaptPath + " dump badging " + Settings.baseTestAppDir + File.separator + Settings.testAppName;
                String result = OSUtils.runProcess(command);
                String[] list = result.split("\\r?\\n");
                for (String line : list) {
                    if (line.contains("application-label:")) {
                        appId = line.substring(line.indexOf("'") + 1);
                        appId = appId.substring(0, appId.indexOf("'"));
                    }
                }
            }
        } else if (Settings.deviceType == DeviceType.Simulator) {
            // TODO: Implement it;
        } else if (Settings.deviceType == DeviceType.iOS) {
            // TODO: Implement it;
        }
        return appId;
    }

    public static void initSettings() throws Exception {

        // Set locations and cleanup output folders
        setupLocations();

        // Get current OS and verify it
        OS = getOSType();
        if ((OS == OSType.Other)) {
            Log.fatal("Unknown OS.");
            throw new UnknownOSException("Unknown OS.");
        }

        properties = readProperties();

        platform = getPlatformType();
        deviceType = getDeviceType();


        // Set isRealDevice
        if ((deviceType == DeviceType.Simulator) || (deviceType == DeviceType.Emulator)) {
            isRealDevice = false;
        } else {
            isRealDevice = true;
        }

        appiumVersion = properties.getProperty("appiumVersion");

        deviceId = properties.getProperty("udid");
        deviceName = properties.getProperty("deviceName");
        platformVersion = properties.getProperty("platformVersion");
        testAppName = properties.getProperty("testAppName");
        testAppArchive = properties.getProperty("testAppArchive");
        emulatorOptions = properties.getProperty("emulatorOptions");
        emulatorCreateOptions = properties.getProperty("emulatorCreateOptions");
        simulatorType = properties.getProperty("simulatorType");
        appiumLogLevel = properties.getProperty("appiumLogLevel");
        logImageVerificationResults = properties.getProperty("logImageVerificationResults") != null ?
                new Boolean(properties.getProperty("logImageVerificationResults")) : false;

        // Set restartApp
        String restartAppString = properties.getProperty("restartApp");
        restartApp = true;
        if (restartAppString != null) {
            restartApp = stringToBoolean(restartAppString);
        }

        // Set debug
        String debugString = properties.getProperty("debug");
        debug = false;
        if (debugString != null) {
            debug = stringToBoolean(debugString);
        }

        // Set acceptAlerts
        String acceptAlertsString = properties.getProperty("acceptAlerts");
        acceptAlerts = false;
        if (acceptAlertsString != null) {
            acceptAlerts = stringToBoolean(acceptAlertsString);
        }

        // Set takeScreenShotAfterTest
        String takeScreenShotAfterTestString = properties.getProperty("takeScreenShotAfterTest");
        takeScreenShotAfterTest = false;
        if (takeScreenShotAfterTestString != null) {
            takeScreenShotAfterTest = stringToBoolean(takeScreenShotAfterTestString);
        }

        // Set image verification type
        String imageVerificationTypeString = properties.getProperty("imageVerificationType");
        imageVerificationType = VerificationType.Default;
        if (imageVerificationTypeString != null) {
            if (imageVerificationTypeString.equalsIgnoreCase("justcapture")) {
                imageVerificationType = VerificationType.JustCapture;
            } else if (imageVerificationTypeString.equalsIgnoreCase("firsttimecapture")) {
                imageVerificationType = VerificationType.FirstTimeCapture;
            } else if (imageVerificationTypeString.equalsIgnoreCase("skip")) {
                imageVerificationType = VerificationType.Skip;
            } else {
                imageVerificationTypeString = "Default";
            }
        }

        // Set automation name
        String automationNameString = properties.getProperty("automationName");
        if (automationNameString == null) {
            automationName = AutomationName.APPIUM;
        } else if (automationNameString != null && automationNameString.equalsIgnoreCase("Appium")) {
            automationName = AutomationName.APPIUM;
        } else if (automationNameString != null && automationNameString.equalsIgnoreCase("Selendroid")) {
            automationName = AutomationName.SELENDROID;
        }

        // If defaultTimeout is not specified set it to 30 sec.
        String defaultTimeoutString = properties.getProperty("defaultTimeout");
        if (defaultTimeoutString != null) {
            defaultTimeout = Integer.valueOf(defaultTimeoutString);
        } else

        {
            defaultTimeout = 30;
        }

        shortTimeout = defaultTimeout / 5;

        // If deviceBootTimeout is not specified set it equal to defaultTimeout
        String deviceBootTimeoutString = properties.getProperty("deviceBootTimeout");
        if (deviceBootTimeoutString != null)

        {
            deviceBootTimeout = Integer.valueOf(deviceBootTimeoutString);
        } else

        {
            deviceBootTimeout = defaultTimeout;
        }


        // Extract test app (only for iOS Simulators)
        if (Settings.deviceType == DeviceType.Simulator) {
            // Delete existing extracted applications
            FileSystem.deletePath(Settings.baseTestAppDir + File.separator + Settings.testAppName);

            // Extact test app archive
            File tarFile = new File(Settings.baseTestAppDir + File.separator + Settings.testAppArchive);
            File dest = new File(Settings.baseTestAppDir);
            Archive.extractArchive(tarFile, dest);
        }

        // Verify setup is correct
        Doctor.verifyJava();
        Doctor.verifyAndroidHome();
        Doctor.verifyMobileOS();
        Doctor.verifyDeviceType();
        Doctor.verifyTestAppPath();
        Doctor.verifyOSTypeAndMobilePlatform();
        Doctor.verifyXcrun();
        Doctor.verifyIdeviceinstaller();

        // Set test app package id
        packageId = getPackageId();

        // Set test app package id
        testAppFriendlyName = getTestAppFriendlyName();

        // Set testAppImageFolder
        String testAppName = Settings.testAppName.toLowerCase().replace("-release", "");
        if (deviceType == DeviceType.Simulator) {
            testAppName = Settings.testAppArchive.toLowerCase().replace("-release", "");
        }
        testAppImageFolder = testAppName.substring(0, testAppName.indexOf("."));

        Log.separator();
        Log.info("Settings initialized properly:");
        Log.info("OS Type: " + OS);
        Log.info("Mobile Platform: " + platform);
        Log.info("Platform Version: " + platformVersion);
        Log.info("Device Type: " + deviceType);
        Log.info("Device Name: " + deviceName);
        Log.info("Real Device: " + isRealDevice);
        Log.info("Device Id: " + deviceId);
        Log.info("Restart App Between Tests: " + restartAppString);
        Log.info("Take Screenshot After Test: " + takeScreenShotAfterTestString);
        Log.info("Image Verification Type: " + imageVerificationTypeString);
        Log.info("Default Timeout: " + defaultTimeout);
        Log.info("Device Boot Time: " + deviceBootTimeout);
        Log.info("Base TestApp Path: " + baseTestAppDir);
        Log.info("TestApp Name: " + testAppName);
        Log.info("TestApp Friendly Name: " + testAppFriendlyName);
        Log.info("TestApp Package Id: " + packageId);
        Log.info("Restart App: " + String.valueOf(restartApp));
        Log.info("Appium Version: " + appiumVersion);
        Log.info("Automation Name: " + automationName);
        Log.info("Log Output Folder: " + baseLogDir);
        Log.info("Screenshot Output Folder: " + screenshotOutDir);
        Log.info("Screenshot Resources Folder: " + screenshotResDir);
        Log.info("TestApp Images Folder: " + testAppImageFolder);
        Log.info("TestData Base Folder: " + baseTestDataDir);
        Log.info("Appium Log File: " + appiumLogFile);
        Log.info("Appium Log Level: " + appiumLogLevel);
        Log.info("Debug: " + debugString);
        Log.info("(Android Only) Default Activity: " + defaultActivity);
        Log.info("(Android Only) Emulator Options: " + emulatorOptions);
        Log.info("(Android Only) Emulator Create Options: " + emulatorCreateOptions);
        Log.info("(iOS Only) Auto Accept Alerts: " + acceptAlertsString);
        Log.info("(iOS Simulator Only) Simulator Type: " + simulatorType);
        Log.info("(iOS Simulator Only) TestApp Archive: " + testAppArchive);
        Log.info("Should log image verification results: " + logImageVerificationResults);
        Log.separator();
    }

    private static Boolean stringToBoolean(String str) {
        if (str.equalsIgnoreCase("true")) {
            return true;
        } else if (str.equalsIgnoreCase("false")) {
            return false;
        } else {
            return null;
        }
    }
}
