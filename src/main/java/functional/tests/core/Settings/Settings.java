package functional.tests.core.Settings;

import functional.tests.core.Enums.DeviceType;
import functional.tests.core.Enums.OSType;
import functional.tests.core.Enums.PlatformType;
import functional.tests.core.Exceptions.UnknownOSException;
import functional.tests.core.Log.Log;
import functional.tests.core.OSUtils.OSUtils;
import functional.tests.core.Screenshot.VerificationType;
import io.appium.java_client.remote.AutomationName;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.Properties;

public class Settings {

    private static final String userDir = System.getProperty("user.dir");
    private static final String appConfigPath = System.getProperty("appConfig");
    private static final String baseResourcesDir = userDir + File.separator + "resources";
    private static final String baseOutputDir = userDir + File.separator + "target" + File.separator + "surefire-reports";

    private static Properties properties;

    public static OSType OS;
    public static PlatformType platform;
    public static DeviceType deviceType;
    public static boolean isRealDevice;
    public static boolean restartApp;
    public static boolean takeScreenShotAfterTest;
    public static VerificationType imageVerificationType;
    public static int shortTimeout;
    public static int defaultTimeout;
    public static int deviceBootTimeout;
    public static String platformVersion;
    public static String deviceId;
    public static String deviceName;
    public static String testAppName;
    public static String testAppPackageId;
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
    public static String appiumLogFile;
    public static String appiumLogLevel;
    public static final String baseTestAppDir = userDir + File.separator + "testapp";
    public static final String baseTestDataDir = baseResourcesDir + File.separator + "testdata";

    public static final int fastTapDuration = 100;
    public static final int defaultTapDuration = 250;
    public static final int slowTapDuration = 1000;

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

    private static void setupLocations() throws IOException {
        baseLogDir = baseOutputDir + File.separator + "logs";
        consoleLogDir = baseLogDir + File.separator + "console";
        screenshotOutDir = baseOutputDir + File.separator + "screenshots";
        screenshotResDir = baseResourcesDir + File.separator + "images";
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
        } else {
            // TODO: Implement it
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

        deviceId = properties.getProperty("udid");
        deviceName = properties.getProperty("deviceName");
        platformVersion = properties.getProperty("platformVersion");
        testAppName = properties.getProperty("testAppName");
        testAppArchive = properties.getProperty("testAppArchive");
        appiumVersion = properties.getProperty("appiumVersion");
        emulatorOptions = properties.getProperty("emulatorOptions");
        emulatorCreateOptions = properties.getProperty("emulatorCreateOptions");
        simulatorType = properties.getProperty("simulatorType");
        appiumVersion = properties.getProperty("appiumVersion");
        appiumLogLevel = properties.getProperty("appiumLogLevel");

        // Set restartApp
        String restartAppString = properties.getProperty("restartApp");
        restartApp = true;
        if (restartAppString != null) {
            if (restartAppString.equalsIgnoreCase("false")) {
                restartApp = false;
            }
        }

        // Set takeScreenShotAfterTest
        String takeScreenShotAfterTestString = properties.getProperty("takeScreenShotAfterTest");
        takeScreenShotAfterTest = true;
        if (takeScreenShotAfterTestString != null) {
            if (takeScreenShotAfterTestString.equalsIgnoreCase("false")) {
                takeScreenShotAfterTest = false;
            }
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
        if ((automationNameString != null) && (automationNameString.equalsIgnoreCase("selendroid")))

        {
            automationName = AutomationName.SELENDROID;
        } else

        {
            automationName = AutomationName.APPIUM;
        }

        // If defaultTimeout is not specified set it to 30 sec.
        String defaultTimeoutString = properties.getProperty("defaultTimeout");
        if (defaultTimeoutString != null)

        {
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

        // Set test app package id
        testAppPackageId = getPackageId();

        // Verify setup is correct
        Doctor.verifyJava();
        Doctor.verifyAndroidHome();
        Doctor.verifyMobileOS();
        Doctor.verifyDeviceType();
        Doctor.verifyTestAppPath();
        Doctor.verifyOSTypeAndMobilePlatform();
        Doctor.verifyXcrun();
        Doctor.verifyIdeviceinstaller();

        Log.separator();
        Log.info("Settings  initialized properly:");
        Log.info("OS Type: " + OS);
        Log.info("Mobile Platoform: " + platform);
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
        Log.info("TestApp Package Id: " + testAppPackageId);
        Log.info("TestApp Archive: " + testAppArchive);
        Log.info("Restart App: " + String.valueOf(restartApp));
        Log.info("Appium Version: " + appiumVersion);
        Log.info("Automation Name: " + automationName);
        Log.info("Emulator Options: " + emulatorOptions);
        Log.info("Emulator Create Options: " + emulatorCreateOptions);
        Log.info("Simulator Type: " + simulatorType);
        Log.info("Log Output Folder: " + baseLogDir);
        Log.info("Screenshot Output Folder: " + screenshotOutDir);
        Log.info("Screenshot Resources Folder: " + screenshotResDir);
        Log.info("TestData Base Folder: " + baseTestDataDir);
        Log.info("Appium Log File: " + appiumLogFile);
        Log.info("Appium Log File: " + appiumLogLevel);
        Log.separator();
    }
}
