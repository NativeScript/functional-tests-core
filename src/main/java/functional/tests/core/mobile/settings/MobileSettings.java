package functional.tests.core.mobile.settings;

import functional.tests.core.enums.DeviceType;
import functional.tests.core.enums.PlatformType;
import functional.tests.core.log.LoggerBase;
import functional.tests.core.mobile.device.android.AndroidDevice;
import functional.tests.core.mobile.device.ios.IOSDevice;
import functional.tests.core.settings.Settings;
import functional.tests.core.utils.Aapt;
import functional.tests.core.utils.Archive;
import functional.tests.core.utils.FileSystem;
import functional.tests.core.utils.OSUtils;
import io.appium.java_client.remote.AutomationName;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.ScreenOrientation;

import java.io.File;
import java.io.IOException;

/**
 * MobileSettings.
 * Read settings from config file to MobileSettings object.
 * Config file is specified via appConfig VM option in tests based on this framework.
 * For example: -DappConfig=resources/config/cuteness/cuteness.emu.default.api23.properties
 */
public class MobileSettings extends Settings {

    private static LoggerBase LOGGER_BASE = LoggerBase.getLogger("MobileSettings");
    public boolean reuseDevice;
    public boolean restartRealDevice;
    public boolean isRealDevice;
    public Double platformVersion;
    public String packageId;
    public String deviceId;
    public String appiumVersion;
    public String automationName;
    public String appiumLogLevel;
    public String appiumLogFile;
    public String testAppFriendlyName;
    public ScreenOrientation orientation;
    public LoggerBase log;
    public SettingsIOS ios;
    public SettingsAndroid android;
    public DeviceType deviceType;
    private Aapt aapt;

    /**
     * Init settings.
     */
    public MobileSettings() {
        this.deviceId = this.properties.getProperty("udid");
        // Init Platform MobileSettings
        this.initSettings();
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
        return this.android;
    }

    /**
     * Init iOS specific settings.
     *
     * @return iOS settings.
     */
    public SettingsIOS initSettingsIOS() {
        this.ios = new SettingsIOS();
        LOGGER_BASE.separatorIOS();

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
        return this.ios;
    }

    /**
     * Init common settings.
     */
    public void initSettings() {
        super.initSettings();

        this.restartRealDevice = this.propertyToBoolean("restartRealDevice", false);
        this.platformVersion = Double.parseDouble(this.properties.getProperty("platformVersion").trim());
        this.appiumVersion = this.properties.getProperty("appiumVersion");
        this.automationName = this.getAutomationName();
        this.appiumLogLevel = this.properties.getProperty("appiumLogLevel", "warn");
        this.appiumLogFile = this.baseLogDir + File.separator + "appium.log";

        this.orientation = this.getScreenOrientation();
        // Set reuse device
        this.reuseDevice = this.propertyToBoolean("reuseDevice", false);
        String reuseEnv = System.getenv("REUSE_DEVICE");
        if (reuseEnv != null) {
            if (reuseEnv.toLowerCase().contains("true")) {
                this.reuseDevice = true;
            }
        }

        // Set orientation
        this.orientation = this.getScreenOrientation();
        // If defaultTimeout is not specified set it to 30 sec.
        this.defaultTimeout = this.convertPropertyToInt("defaultTimeout", 30);
        this.shortTimeout = this.defaultTimeout / 5;

        // If deviceBootTimeout is not specified set it equal to defaultTimeout
        this.deviceBootTimeout = this.convertPropertyToInt("deviceBootTimeout", 300);
        this.deviceType = this.getDeviceType();

        if (this.platform == PlatformType.Android) {
            LOGGER_BASE = LoggerBase.getLogger("AndroidSettings");
            this.android = this.initSettingsAndroid();
        } else if (this.platform == PlatformType.iOS) {
            LOGGER_BASE = LoggerBase.getLogger("IOSSettings");
            this.ios = this.initSettingsIOS();
        }

        LOGGER_BASE.info("Platform Version: " + this.platformVersion);
        LOGGER_BASE.info("Device Type: " + this.deviceType);
        LOGGER_BASE.info("Real device: " + this.isRealDevice);
        if (this.isRealDevice) {
            LOGGER_BASE.info("Restart real device:  " + this.restartRealDevice);
        }
        LOGGER_BASE.info("ReuseDevice: " + this.reuseDevice);
        LOGGER_BASE.info("Appium Version: " + this.appiumVersion);
        LOGGER_BASE.info("Appium Log File: " + this.appiumLogFile);
        LOGGER_BASE.info("Appium Log Level: " + this.appiumLogLevel);
        LOGGER_BASE.info("Automation Name: " + this.automationName);
        LOGGER_BASE.info("Restart app Between Tests: " + this.restartApp);
        if (this.orientation != null) {
            LOGGER_BASE.info("Screen Orientation: " + this.orientation);
        }
        LOGGER_BASE.separator();
    }

    /**
     * Extract test application.
     * For iOS Simulator test app (*.app) must be packaged in tgz archive.
     * This method will extract the archive.
     *
     * @throws IOException When fail extract tgz package.
     */
    private void extractApp() {
        // Make sure no old app is available.
        try {
            FileSystem.deletePath(BASE_TEST_APP_DIR + File.separator + this.testAppName);
            // Extract archive.
            File tgzPath = new File(BASE_TEST_APP_DIR + File.separator + this.ios.testAppArchive);
            File dir = new File(BASE_TEST_APP_DIR);
            Archive.extractArchive(tgzPath, dir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get screen orientation settings.
     * <p>
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
     * Get automation name setting.
     *
     * @return Name of automation technology.
     */
    private String getAutomationName() {
        String automationNameString = this.properties.getProperty("automationName");

        if (automationNameString != null) {
            this.automationName = automationNameString.trim();
        } else {
            if (this.platform == PlatformType.Android) {
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
}
