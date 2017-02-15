package functional.tests.core.settings;

import functional.tests.core.device.android.AndroidDevice;
import functional.tests.core.device.ios.IOSDevice;
import functional.tests.core.enums.DeviceType;
import functional.tests.core.enums.ImageVerificationType;
import functional.tests.core.enums.OSType;
import functional.tests.core.enums.PlatformType;
import functional.tests.core.exceptions.HostException;
import functional.tests.core.log.LoggerBase;
import functional.tests.core.utils.Aapt;
import functional.tests.core.utils.Archive;
import functional.tests.core.utils.FileSystem;
import functional.tests.core.utils.OSUtils;
import io.appium.java_client.remote.AutomationName;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.ScreenOrientation;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Settings.
 * Read settings from config file to Settings object.
 * Config file is specified via appConfig VM option in tests based on this framework.
 * For example: -DappConfig=resources/config/cuteness/cuteness.emu.default.api23.properties
 */
public class Settings {

    private static final LoggerBase LOGGER_BASE = LoggerBase.getLogger("Settings");

    private static final String APP_CONFIG_PATH = System.getProperty("appConfig");
    private static final String STORAGE_ENVIRONMENT_VARIABLE = "STORAGE";
    private static final String USER_DIR = System.getProperty("user.dir");

    private Aapt aapt;
    private String baseOutputDir;
    private Properties properties;

    public static final String BASE_RESOURCE_DIR = USER_DIR + File.separator + "resources";
    public static final String BASE_TEST_APP_DIR = USER_DIR + File.separator + "testapp";
    public static final String BASE_TEST_DATA_DIR = BASE_RESOURCE_DIR + File.separator + "testdata";
    public static final int DEFAULT_TAP_DURATION = 250;

    public static OSType os;

    public String appiumLogFile;
    public String baseLogDir;
    public String consoleLogDir;
    public String screenshotOutDir;
    public String screenshotResDir;

    //TODO(): Now we have isRealDevice at root level and in settings.ios and settings.android. Use only root level!
    public boolean isRealDevice;
    public boolean restartApp;
    public boolean debug;
    public boolean takeScreenShotAfterTest;
    public boolean logImageVerificationStatus;
    public int shortTimeout;
    public int defaultTimeout;
    public int deviceBootTimeout;
    public Double platformVersion;
    public String deviceId;
    public String deviceName;
    public String testAppName;
    public String testAppFriendlyName;
    public String packageId;
    public String appiumVersion;
    public String automationName;
    public String testAppImageFolder;
    public String appiumLogLevel;
    public boolean reuseDevice;
    public ScreenOrientation orientation;
    public PlatformType platform;
    public DeviceType deviceType;
    public ImageVerificationType imageVerificationType;
    public LoggerBase log;
    public SettingsIOS ios;
    public SettingsAndroid android;
    public boolean restartRealDevice;

    /**
     * Init settings.
     */
    public Settings() {
        this.baseOutputDir = USER_DIR + File.separator + "target" + File.separator + "surefire-reports";

        // Read properties file
        try {
            this.properties = this.readProperties();
        } catch (Exception e) {
            LOGGER_BASE.error(e.getMessage());
        }

        // Init Common Settings
        try {
            this.initSettings();
        } catch (Exception e) {
            LOGGER_BASE.error(e.getMessage());
        }

        // Init Platform Settings
        if (this.platform == PlatformType.Andorid) {
            this.android = this.initSettingsAndroid();
        } else if (this.platform == PlatformType.iOS) {
            try {
                this.ios = this.initSettingsIOS();
            } catch (IOException e) {
                LOGGER_BASE.error(e.getMessage());
            }
        }
    }

    /**
     * Init Android specific settings.
     *
     * @return Android settings.
     */
    public SettingsAndroid initSettingsAndroid() {
        // Aapt need so know OS Type.
        this.aapt = new Aapt(this);

        this.android = new SettingsAndroid();
        LOGGER_BASE.separatorAndroid();

        this.deviceId = this.properties.getProperty("udid");
        if (this.deviceId == null && this.deviceType == DeviceType.Emulator) {
            // Main port is 5 next two numbers comes from platform version and last one is like minor version * 2
            this.deviceId = AndroidDevice.generateDeviceId(this.platformVersion);
        }
        LOGGER_BASE.info("Device Id: " + this.deviceId);

        // Set testAppImageFolder
        String testAppName = this.testAppName.toLowerCase().replace("-release", "");
        this.testAppImageFolder = this.testAppName.substring(0, testAppName.indexOf(".")).toLowerCase();
        LOGGER_BASE.info("TestApp Images Folder: " + this.testAppImageFolder);

        this.packageId = this.aapt.getPackage();
        LOGGER_BASE.info("TestApp Package Id: " + this.packageId);

        this.android.defaultActivity = this.getDefaultActivity();
        LOGGER_BASE.info("Default Activity: " + this.android.defaultActivity);

        this.android.appWaitActivity = this.getAppWaitActivity();
        LOGGER_BASE.info("App Wait Activity: " + this.android.appWaitActivity);

        this.android.appWaitPackage = this.getAppWaitPackage();
        LOGGER_BASE.info("App Wait Package: " + this.android.appWaitPackage);

        this.testAppFriendlyName = this.aapt.getApplicationLabel(this);
        LOGGER_BASE.info("TestApp Friendly Name: " + this.testAppFriendlyName);

        if (this.deviceType == DeviceType.Emulator) {
            this.android.emulatorOptions = this.properties.getProperty("emulatorOptions");
            LOGGER_BASE.info("Emulator Options: " + this.android.emulatorOptions);

            this.android.emulatorCreateOptions = this.properties.getProperty("emulatorCreateOptions");
            LOGGER_BASE.info("Emulator Create Options: " + this.android.emulatorCreateOptions);
        }
        this.android.memoryMaxUsageLimit = this.getMemoryMaxUsageLimit();
        LOGGER_BASE.info("Memory Usage Max Limit: "
                + (this.android.memoryMaxUsageLimit > -1 ? this.android.memoryMaxUsageLimit : "not set"));

        this.android.appLaunchTimeLimit = this.getappLaunchTimeLimit();
        LOGGER_BASE.info("App Launch Time Limit: "
                + (this.android.appLaunchTimeLimit > -1 ? this.android.appLaunchTimeLimit : "not set"));

        // Set isRealDevice
        if (this.deviceType == DeviceType.Emulator) {
            this.isRealDevice = false;
        } else {
            this.isRealDevice = true;
        }

        this.android.isRealDevice = this.isRealDevice;
        LOGGER_BASE.separator();
        return this.android;
    }

    /**
     * Init iOS specific settings.
     *
     * @return iOS settings.
     */
    public SettingsIOS initSettingsIOS() throws IOException {
        this.ios = new SettingsIOS();
        LOGGER_BASE.separatorIOS();

        this.deviceId = this.properties.getProperty("udid");
        if (this.deviceId == null && !this.isRealDevice) {

            this.deviceId = IOSDevice.getDeviceUidid(this.deviceName);
        }

        LOGGER_BASE.info("Device Id: " + this.deviceId);

        this.ios.acceptAlerts = this.propertyToBoolean("acceptAlerts", false);
        LOGGER_BASE.info("Auto Accept Alerts: " + this.ios.acceptAlerts);

        // Set isRealDevice
        if (this.deviceType == DeviceType.Simulator) {
            this.isRealDevice = false;
            this.ios.testAppArchive = this.properties.getProperty("testAppArchive");
            LOGGER_BASE.info("TestApp Archive: " + this.ios.testAppArchive);
            this.testAppImageFolder = this.ios.testAppArchive.substring(0, this.ios.testAppArchive.indexOf("."));
            this.ios.simulatorType = this.properties.getProperty("simulatorType");
            LOGGER_BASE.info("Simulator Type: " + this.ios.simulatorType);

            this.extractApp();
        } else {
            this.isRealDevice = true;
            this.ios.xCode8ConfigFile = BASE_RESOURCE_DIR +
                    File.separator + "xcode" + File.separator + "xcode8config.xcconfig";

            this.testAppImageFolder = this.testAppName.replace(".ipa", "");
            this.setupDevelopmentTeam();
            LOGGER_BASE.info("xCode 8 config file. Initialized if it is real device " + this.ios.xCode8ConfigFile);
        }

        // TODO(dtopuzov): Find better way to get testAppFriendlyName.
        this.testAppFriendlyName = this.testAppImageFolder;
        LOGGER_BASE.info("TestApp Friendly Name: " + this.testAppFriendlyName);

        this.testAppImageFolder = this.testAppImageFolder.toLowerCase();
        LOGGER_BASE.info("TestApp Images Folder: " + this.testAppImageFolder);

        this.packageId = this.getIOSPackageId();
        LOGGER_BASE.info("TestApp Package Id: " + this.packageId);

        this.ios.isRealDevice = this.isRealDevice;
        LOGGER_BASE.separator();
        return this.ios;
    }

    /**
     * Init common settings.
     *
     * @throws HostException If host OS is unknown or not supported.
     * @throws IOException   When fail to create folders required for logs.
     */
    public void initSettings() throws HostException, IOException {
        this.os = this.getOSType();

        // Set locations and cleanup output folders
        this.setupLocations();

        this.platform = this.getPlatformType();
        this.deviceType = this.getDeviceType();

        this.restartRealDevice = this.propertyToBoolean("restartRealDevice", false);
        LOGGER_BASE.info("Restart real device:  " + this.restartRealDevice);

        this.appiumVersion = this.properties.getProperty("appiumVersion");
        this.deviceName = this.properties.getProperty("deviceName");
        this.platformVersion = Double.parseDouble(this.properties.getProperty("platformVersion").trim());
        this.testAppName = this.properties.getProperty("testAppName");
        this.appiumLogLevel = this.properties.getProperty("appiumLogLevel", "warn");
        this.logImageVerificationStatus = this.properties.getProperty("logImageVerificationStatus") != null ?
                new Boolean(this.properties.getProperty("logImageVerificationStatus")) : false;


        // Set reuse device
        this.reuseDevice = this.propertyToBoolean("reuseDevice", false);
        String reuseEnv = System.getenv("REUSE_DEVICE");
        if (reuseEnv != null) {
            if (reuseEnv.toLowerCase().contains("true")) {
                this.reuseDevice = true;
            }
        }

        // Set debug
        this.debug = this.propertyToBoolean("debug", false) ||
                java.lang.management.ManagementFactory
                        .getRuntimeMXBean().getInputArguments().toString().indexOf("jdwp") >= 0;

        // Set restartApp
        this.restartApp = this.propertyToBoolean("restartApp", false);

        // Set takeScreenShotAfterTest
        this.takeScreenShotAfterTest = this.propertyToBoolean("takeScreenShotAfterTest", false);

        // Set orientation
        this.orientation = this.getScreenOrientation();

        // Set image verification type
        this.imageVerificationType = this.getImageVerificationType();

        // Set automation name
        this.automationName = this.getAutomationName();

        // If defaultTimeout is not specified set it to 30 sec.
        this.defaultTimeout = this.convertPropertyToInt("defaultTimeout", 30);
        this.shortTimeout = this.defaultTimeout / 5;

        // If deviceBootTimeout is not specified set it equal to defaultTimeout
        this.deviceBootTimeout = this.convertPropertyToInt("deviceBootTimeout", 300);

        LOGGER_BASE.separator();
        LOGGER_BASE.info("OS Type: " + this.os);
        LOGGER_BASE.info("Mobile Platform: " + this.platform);
        LOGGER_BASE.info("Platform Version: " + this.platformVersion);
        LOGGER_BASE.info("Device Type: " + this.deviceType);
        LOGGER_BASE.info("Device Name: " + this.deviceName);
        LOGGER_BASE.info("Real device: " + this.isRealDevice);
        LOGGER_BASE.info("Restart app Between Tests: " + this.restartApp);
        LOGGER_BASE.info("Screen Orientation: " + this.orientation);
        LOGGER_BASE.info("Take Screenshot After Test: " + this.takeScreenShotAfterTest);
        LOGGER_BASE.info("Image Verification Type: " + this.imageVerificationType);
        LOGGER_BASE.info("Default Timeout: " + this.defaultTimeout);
        LOGGER_BASE.info("Device Boot Time: " + this.deviceBootTimeout);
        LOGGER_BASE.info("Base TestApp Path: " + BASE_TEST_APP_DIR);
        LOGGER_BASE.info("TestApp Name: " + this.testAppName);
        LOGGER_BASE.info("Restart App: " + String.valueOf(this.restartApp));
        LOGGER_BASE.info("Appium Version: " + this.appiumVersion);
        LOGGER_BASE.info("Automation Name: " + this.automationName);
        LOGGER_BASE.info("Log Output Folder: " + this.baseLogDir);
        LOGGER_BASE.info("Screenshot Output Folder: " + this.screenshotOutDir);
        LOGGER_BASE.info("Screenshot Resources Folder: " + this.screenshotResDir);
        LOGGER_BASE.info("TestData Base Folder: " + BASE_TEST_DATA_DIR);
        LOGGER_BASE.info("Appium Log File: " + this.appiumLogFile);
        LOGGER_BASE.info("Appium Log Level: " + this.appiumLogLevel);
        LOGGER_BASE.info("Debug: " + this.debug);
        LOGGER_BASE.info("ReuseDevice: " + this.reuseDevice);
        LOGGER_BASE.info("Log image verification status: " + this.logImageVerificationStatus);
    }

    /**
     * Extract test application.
     * For iOS Simulator test app (*.app) must be packaged in tgz archive.
     * This method will extract the archive.
     *
     * @throws IOException When fail extract tgz package.
     */
    private void extractApp() throws IOException {
        // Make sure no old app is available.
        FileSystem.deletePath(BASE_TEST_APP_DIR + File.separator + this.testAppName);

        // Extract archive.
        File tgzPath = new File(BASE_TEST_APP_DIR + File.separator + this.ios.testAppArchive);
        File dir = new File(BASE_TEST_APP_DIR);
        Archive.extractArchive(tgzPath, dir);
    }

    /**
     * Get screen orientation settings.
     *
     * * @return ScreenOrientation.
     */
    private ScreenOrientation getScreenOrientation() {
        String orientation = this.properties.getProperty("orientation", "none");
        if (orientation.toLowerCase().contains("port")) {
            return ScreenOrientation.PORTRAIT;
        } else if (orientation.toLowerCase().contains("land")) {
            return ScreenOrientation.LANDSCAPE;
        } else {
            return null;
        }
    }

    /**
     * Get default activity setting (automatically from apk file).
     *
     * @return default activity.
     */
    private String getDefaultActivity() {
        String appDefaultActivityString = this.aapt.getLaunchableActivity(this);
        return appDefaultActivityString;
    }

    /**
     * Get appWaitActivity setting.
     * If not specified by default it is equal to defaultActivity.
     * When Appium start app under test it will wait until appWaitActivity is loaded.
     *
     * @return default activity.
     */
    private String getAppWaitActivity() {
        String appWaitActivityString = this.properties.getProperty("appWaitActivity");
        if (appWaitActivityString == null) {
            this.android.appWaitActivity = this.android.defaultActivity;
        } else {
            this.android.appWaitActivity = appWaitActivityString;
        }
        return this.android.appWaitActivity;
    }

    /**
     * Get appWaitPackage setting.
     * If not specified by default it is equal to packageId.
     * When Appium start app under test it will wait until appWaitPackage is loaded.
     *
     * @return default activity.
     */
    private String getAppWaitPackage() {
        String appWaitPackageString = this.properties.getProperty("appWaitPackage");
        if (appWaitPackageString == null) {
            this.android.appWaitPackage = this.packageId;
        } else {
            this.android.appWaitPackage = appWaitPackageString;
        }
        return this.android.appWaitPackage;
    }

    /**
     * Get automation name setting.
     *
     * @return Name of automation technology.
     */
    private String getAutomationName() {
        String automationNameString = this.properties.getProperty("automationName");

        if (automationNameString != null) {
            this.automationName = automationNameString.trim();
        } else {
            if (this.platform == PlatformType.Andorid) {
                if (this.platformVersion <= 4.1) {
                    this.automationName = AutomationName.SELENDROID;
                } else if (this.platformVersion >= 7.0) {
                    // TODO(): Update to AutomationName.ANDROID_UIAUTOMATOR2 when Appium Client 5.0 is released.
                    this.automationName = "UIAutomator2";
                } else {
                    this.automationName = AutomationName.APPIUM;
                }
            }

            if (this.platform == PlatformType.iOS) {
                if (this.platformVersion < 10) {
                    this.automationName = AutomationName.APPIUM;
                } else {
                    this.automationName = AutomationName.IOS_XCUI_TEST;
                }
            }
        }

        return this.automationName;
    }

    /**
     * Get image verification type setting.
     * Default value: ImageVerificationType.Default
     *
     * @return ImageVerificationType value.
     */
    private ImageVerificationType getImageVerificationType() {
        String imageVerificationTypeString = this.properties.getProperty("imageVerificationType");
        ImageVerificationType type = ImageVerificationType.Default;

        if (imageVerificationTypeString != null) {
            if (imageVerificationTypeString.equalsIgnoreCase("firsttimecapture")) {
                type = ImageVerificationType.FirstTimeCapture;
            } else if (imageVerificationTypeString.equalsIgnoreCase("skip")) {
                type = ImageVerificationType.Skip;
            }
        }

        return type;
    }

    /**
     * Get type of host operating system.
     *
     * @throws HostException when operating system is unknown.
     * @retur OSType value.
     */
    private OSType getOSType() throws HostException {
        String osTypeString = System.getProperty("os.name", "generic").toLowerCase();
        if ((osTypeString.contains("mac")) || (osTypeString.contains("darwin"))) {
            return OSType.MacOS;
        } else if (osTypeString.contains("win")) {
            return OSType.Windows;
        } else if (osTypeString.contains("nux")) {
            return OSType.Linux;
        } else {
            LOGGER_BASE.fatal("Unknown host OS.");
            throw new HostException("Unknown host OS.");
        }
    }

    /**
     * Get storage settings.
     * Read from STORAGE environment variable.
     *
     * @return value of STORAGE environment variable
     */
    private String getStorage() {
        String env = System.getenv(STORAGE_ENVIRONMENT_VARIABLE);
        if (env == null) {
            LOGGER_BASE.info(String.format("LOCAL STORAGE %s", BASE_RESOURCE_DIR));
            return BASE_RESOURCE_DIR;
        } else {
            LOGGER_BASE.info(String.format("%s=%s", STORAGE_ENVIRONMENT_VARIABLE, env));
            return env;
        }
    }

    /**
     * Get iOS bundle identifier of app under test.
     *
     * @return iOS bundle identifier of app under test.
     */
    private String getIOSPackageId() {
        String result = null;
        String plistPath = this.getPlistPath();
        File file = new File(plistPath);

        if (file.exists()) {
            String command = "/usr/libexec/PlistBuddy -c 'Print CFBundleIdentifier' " + plistPath;
            result = OSUtils.runProcess(command).trim();
        } else {
            LOGGER_BASE.error("File " + plistPath + " does not exist.");
        }
        return result;
    }

    /**
     * Get path of Info.plist of iOS app under test.
     * Info.plist holds information for app under test.
     *
     * @return path to Info.plist
     */
    private String getPlistPath() {
        String plistPath = null;
        if (this.deviceType == DeviceType.Simulator) {
            plistPath = BASE_TEST_APP_DIR + File.separator + this.testAppName + File.separator + "Info.plist";
        } else if (this.deviceType == DeviceType.iOS) {
            String ipaPath = BASE_TEST_APP_DIR + File.separator + this.testAppName;
            OSUtils.runProcess("unzip -o " + ipaPath + " -d " + BASE_TEST_APP_DIR);
            String appName = OSUtils.runProcess("ls " + BASE_TEST_APP_DIR + File.separator + "Payload").trim();
            plistPath = BASE_TEST_APP_DIR +
                    File.separator + "Payload" + File.separator + appName + File.separator + "Info.plist";
        }
        return plistPath;
    }

    /**
     * Set location settings such as baseLogDir, consoleLogDir, screenshotOutDir, screenshotResDir and appiumLogFile.
     *
     * @throws IOException when fail to set locations.
     */
    private void setupLocations() throws IOException {
        this.baseLogDir = this.baseOutputDir + File.separator + "logs";
        this.consoleLogDir = this.baseLogDir + File.separator + "console";
        this.screenshotOutDir = this.baseOutputDir + File.separator + "screenshots";
        this.screenshotResDir = this.getStorage() + File.separator + "images";
        this.appiumLogFile = this.baseLogDir + File.separator + "appium.log";

        try {
            File baseScreenshotDirLocation = new File(this.screenshotOutDir);
            baseScreenshotDirLocation.mkdirs();
            FileUtils.cleanDirectory(baseScreenshotDirLocation);
        } catch (IOException e) {
            LOGGER_BASE.fatal("Failed to cleanup and create screenshot output folder.");
            throw new IOException(e);
        }

        try {
            File baseLogDirLocation = new File(this.baseLogDir);
            baseLogDirLocation.mkdirs();
            File consoleLogDirLocation = new File(this.consoleLogDir);
            consoleLogDirLocation.mkdirs();
            FileUtils.cleanDirectory(consoleLogDirLocation);
        } catch (IOException e) {
            LOGGER_BASE.fatal("Failed to cleanup and create logs folder.");
            throw new IOException(e);
        }
    }

    /**
     * Read properties from file to Properties object.
     *
     * @return Properties object.
     * @throws Exception When properties file is not found.
     */
    private Properties readProperties() throws Exception {
        String appConfigFile = USER_DIR + File.separator + APP_CONFIG_PATH;
        try {
            InputStream input = new FileInputStream(appConfigFile);
            Properties prop = new Properties();
            prop.load(input);
            return prop;
        } catch (Exception e) {
            LOGGER_BASE.fatal("Failed to read and init settings. Please check if " + appConfigFile + " exists.");
            throw new Exception(e);
        }
    }

    /**
     * Get mobile platform type.
     *
     * @return PlatformType value.
     */
    private PlatformType getPlatformType() {
        String platformTypeString = this.properties.getProperty("platformName");
        PlatformType platformType;
        if (platformTypeString.equalsIgnoreCase("Android")) {
            platformType = PlatformType.Andorid;
        } else if (platformTypeString.equalsIgnoreCase("iOS")) {
            platformType = PlatformType.iOS;
        } else {
            platformType = PlatformType.Other;
        }

        return platformType;
    }

    /**
     * Get device type (simulator, emulator, Android or iOS device).
     *
     * @return DeviceType value.
     */
    private DeviceType getDeviceType() {
        String deviceTypeString = this.properties.getProperty("deviceType");
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

    /**
     * Get max memory usage limit setting.
     *
     * @return max memory usage limit in kB.
     */
    private int getMemoryMaxUsageLimit() {
        String value = this.properties.getProperty("memoryMaxUsageLimit");
        if (value != "" && value != null) {
            return Integer.parseInt(value);
        } else {
            return -1;
        }
    }

    /**
     * Get launch timeout limit setting.
     *
     * @return launch timeout limit in milliseconds.
     */
    private int getappLaunchTimeLimit() {
        String value = this.properties.getProperty("appLaunchTimeLimit");
        if (value != "" && value != null) {
            return Integer.parseInt(value);
        } else {
            return -1;
        }
    }

    /**
     * Helper method that converts property to int.
     *
     * @param property as String.
     * @return Value of property as int.
     */
    private int convertPropertyToInt(String property, int defaultValue) {
        String defaultTimeoutString = this.properties.getProperty(property);
        if (defaultTimeoutString != null) {
            return Integer.valueOf(defaultTimeoutString);
        } else {
            return defaultValue;
        }
    }

    /**
     * Helper method that converts property to Boolean.
     *
     * @param property as String.
     * @return Value of property as Boolean.
     */
    private Boolean propertyToBoolean(String property, boolean defaultValue) {
        String value = this.properties.getProperty(property);
        if (value == null) {
            return defaultValue;
        }
        if (value.equalsIgnoreCase("true")) {
            return true;
        } else if (value.equalsIgnoreCase("false")) {
            return false;
        } else {
            return null;
        }
    }

    /**
     * Set developer team settings.
     * Required for signing of apps that run on physical iOS devices.
     */
    private void setupDevelopmentTeam() {
        File file = new File(this.ios.xCode8ConfigFile);
        FileSystem.ensureFolderExists(file.getParent());
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String teamId = System.getenv("DEVELOPMENT_TEAM");
        String fileContext = String.format("DEVELOPMENT_TEAM=%s\nCODE_SIGN_IDENTITY=iPhone Developer", teamId);

        try {
            FileUtils.write(file, fileContext);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
