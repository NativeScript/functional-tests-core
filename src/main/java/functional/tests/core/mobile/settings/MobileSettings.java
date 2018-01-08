package functional.tests.core.mobile.settings;

import functional.tests.core.enums.DeviceType;
import functional.tests.core.enums.PlatformType;
import functional.tests.core.log.LoggerBase;
import functional.tests.core.mobile.device.android.AndroidDevice;
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

    private static LoggerBase loggerBase = LoggerBase.getLogger("MobileSettings");
    private Aapt aapt;
    String appiumVersion;
    public boolean restartRealDevice;
    public boolean isRealDevice;
    public Double platformVersion;
    public String packageId;
    public String deviceId;
    public String automationName;
    public String appiumLogLevel;
    public String appiumLogFile;
    public String testAppFriendlyName;
    public ScreenOrientation orientation;
    public LoggerBase log;
    public SettingsIOS ios;
    public SettingsAndroid android;
    public DeviceType deviceType;
    public boolean reuseDevice;

    /**
     * Init settings.
     */
    public MobileSettings() {
        this.deviceId = this.properties.getProperty("udid");
        this.initSettings();
    }

    /**
     * Init Android specific settings.
     *
     * @return Android settings.
     */
    private SettingsAndroid initSettingsAndroid() {
        // Aapt need so know OS Type.
        this.aapt = new Aapt(this);

        this.android = new SettingsAndroid();
        loggerBase.separatorAndroid();

        this.android.maxEmuCount = Integer.parseInt(OSUtils.getEnvironmentVariable("MAX_EMU_COUNT", "1"));
        loggerBase.info("Maximum number of parallel emulators: " + String.valueOf(this.android.maxEmuCount));

        if (this.deviceType == DeviceType.Emulator && (this.deviceId == "" || this.deviceId == null)) {
            // Main port is 5 next two numbers comes from platform version and last one is like minor version * 2
            this.deviceId = AndroidDevice.getEmulatorId(this.platformVersion);
        }
        loggerBase.info("Device Id: " + this.deviceId);

        // Set testAppName
        String name = this.testAppFileName.toLowerCase().replace("-release", "").replace("-debug", "");
        this.testAppName = this.testAppFileName.substring(0, name.indexOf(".")).toLowerCase();
        loggerBase.info("TestApp Name: " + this.testAppName);

        this.packageId = this.aapt.getPackage();
        loggerBase.info("TestApp Package Id: " + this.packageId);

        this.android.defaultActivity = this.getDefaultActivity();
        loggerBase.info("Default Activity: " + this.android.defaultActivity);

        this.android.appWaitActivity = this.getAppWaitActivity();
        loggerBase.info("App Wait Activity: " + this.android.appWaitActivity);

        this.android.appWaitPackage = this.getAppWaitPackage();
        loggerBase.info("App Wait Package: " + this.android.appWaitPackage);

        this.testAppFriendlyName = this.aapt.getApplicationLabel();
        loggerBase.info("TestApp Friendly Name: " + this.testAppFriendlyName);

        if (this.deviceType == DeviceType.Emulator) {
            this.android.emulatorOptions = this.properties.getProperty("emulatorOptions");
            loggerBase.info("Emulator Options: " + this.android.emulatorOptions);

            this.android.emulatorCreateOptions = this.properties.getProperty("emulatorCreateOptions");
            loggerBase.info("Emulator Create Options: " + this.android.emulatorCreateOptions);
        }
        this.android.memoryMaxUsageLimit = this.getMemoryMaxUsageLimit();
        loggerBase.info("Memory Usage Max Limit: "
                + (this.android.memoryMaxUsageLimit > -1 ? this.android.memoryMaxUsageLimit : "not set"));

        this.android.appLaunchTimeLimit = this.getappLaunchTimeLimit();
        loggerBase.info("App Launch Time Limit: "
                + (this.android.appLaunchTimeLimit > -1 ? this.android.appLaunchTimeLimit : "not set"));

        // Set isRealDevice
        this.isRealDevice = this.deviceType == DeviceType.Android;
        this.android.isRealDevice = this.isRealDevice;

        return this.android;
    }

    /**
     * Set the performance storage.
     */
    private void setPerfStorage() {
        this.perfDir = this.perfDir + File.separator + this.testAppName + File.separator + this.deviceName;
        loggerBase.info("Performance storage for device: " + this.perfDir);
    }

    /**
     * Init iOS specific settings.
     *
     * @return iOS settings.
     */
    private SettingsIOS initSettingsIOS() {
        this.ios = new SettingsIOS();
        loggerBase.separatorIOS();

        String wdaLocalPortAsString = System.getenv("WDA_LOCAL_PORT");
        this.ios.wdaLocalPort = wdaLocalPortAsString == null ? 0 : Integer.parseInt(wdaLocalPortAsString);
        if (this.ios.wdaLocalPort != 0) {
            this.log.info("WDA_LOCAL_PORT: " + this.ios.wdaLocalPort);
        }

        this.ios.maxSimCount = Integer.parseInt(OSUtils.getEnvironmentVariable("MAX_SIM_COUNT", "1"));
        loggerBase.info("Maximum number of parallel iOS Simulators: " + String.valueOf(this.ios.maxSimCount));

        if (this.deviceId == null && !this.isRealDevice) {
            this.deviceId = null;
        }
        this.log.info("Device Id: " + this.deviceId);

        this.ios.acceptAlerts = this.propertyToBoolean("acceptAlerts", false);
        loggerBase.info("Auto Accept Alerts: " + this.ios.acceptAlerts);

        // Set isRealDevice
        if (this.deviceType == DeviceType.Simulator) {
            this.isRealDevice = false;
            this.ios.testAppArchive = this.properties.getProperty("testAppArchive");
            loggerBase.info("TestApp Archive: " + this.ios.testAppArchive);
            this.testAppName = this.ios.testAppArchive.substring(0, this.ios.testAppArchive.indexOf("."));
            this.ios.simulatorType = this.properties.getProperty("simulatorType");
            loggerBase.info("Simulator Type: " + this.ios.simulatorType);

            this.extractApp();
        } else {
            this.isRealDevice = true;
            this.ios.xCode8ConfigFile = BASE_RESOURCE_DIR +
                    File.separator + "xcode" + File.separator + "xcode8config.xcconfig";

            this.testAppName = this.testAppFileName.replace(".ipa", "");
            this.setupDevelopmentTeam();
            loggerBase.info("xCode 8 config file. Initialized if it is real device " + this.ios.xCode8ConfigFile);
        }

        // TODO(dtopuzov): Find better way to get testAppFriendlyName.
        this.testAppFriendlyName = this.testAppName;
        loggerBase.info("TestApp Friendly Name: " + this.testAppFriendlyName);

        this.testAppName = this.testAppName.toLowerCase();
        loggerBase.info("TestApp Name: " + this.testAppName);

        this.packageId = this.getIOSPackageId();
        loggerBase.info("TestApp Package Id: " + this.packageId);

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

        // If defaultTimeout is not specified set it to 30 sec.
        this.defaultTimeout = this.convertPropertyToInt("defaultTimeout", 30);
        this.shortTimeout = this.defaultTimeout / 5;

        // If deviceBootTimeout is not specified set it equal to defaultTimeout
        this.deviceBootTimeout = this.convertPropertyToInt("deviceBootTimeout", 300);
        this.deviceType = this.getDeviceType();

        if (this.platform == PlatformType.Android) {
            loggerBase = LoggerBase.getLogger("AndroidSettings");
            this.android = this.initSettingsAndroid();
        } else if (this.platform == PlatformType.iOS) {
            loggerBase = LoggerBase.getLogger("IOSSettings");
            this.ios = this.initSettingsIOS();
        }

        String deviceToken = System.getenv("DEVICE_TOKEN");
        if (deviceToken != null && deviceToken != "") {
            this.log.info("DEVICE_TOKEN: " + deviceToken);
            this.deviceId = deviceToken;
            this.reuseDevice = true;
        }

        loggerBase.info("Device Id: " + this.deviceId);

        this.setPerfStorage();

        loggerBase.info("Platform Version: " + this.platformVersion);
        loggerBase.info("Device Type: " + this.deviceType);
        loggerBase.info("Real device: " + this.isRealDevice);
        if (this.isRealDevice) {
            loggerBase.info("Restart real device: " + this.restartRealDevice);
        }
        loggerBase.info("Appium Version: " + this.appiumVersion);
        loggerBase.info("Appium Log File: " + this.appiumLogFile);
        loggerBase.info("Appium Log Level: " + this.appiumLogLevel);
        loggerBase.info("Automation Name: " + this.automationName);
        loggerBase.info("Restart app Between Tests: " + this.restartApp);
        if (this.orientation != null) {
            loggerBase.info("Screen Orientation: " + this.orientation);
        }
        loggerBase.separator();
    }

    /**
     * Extract test application.
     * For iOS Simulator test app (*.app) must be packaged in tgz archive.
     * This method will extract the archive.
     */
    private void extractApp() {
        // Make sure no old app is available.
        try {
            FileSystem.deletePath(BASE_TEST_APP_DIR + File.separator + this.testAppFileName);
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
        return this.aapt.getLaunchableActivity();
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
            loggerBase.error("File " + plistPath + " does not exist.");
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
            plistPath = BASE_TEST_APP_DIR + File.separator + this.testAppFileName + File.separator + "Info.plist";
        } else if (this.deviceType == DeviceType.iOS) {
            String ipaPath = BASE_TEST_APP_DIR + File.separator + this.testAppFileName;
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
        if (value != null && value.equals("")) {
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
        if (value != null && value.equals("")) {
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
                    // TODO(dtopuzov): Update to AutomationName.ANDROID_UIAUTOMATOR2 migrate to Appium Client 5.0+.
                    this.automationName = "UIAutomator2";
                } else {
                    this.automationName = AutomationName.APPIUM;
                }
            }

            if (this.platform == PlatformType.iOS) {
                if (this.platformVersion >= 9.3) {
                    this.automationName = AutomationName.IOS_XCUI_TEST;
                } else {
                    this.automationName = AutomationName.APPIUM;
                }
            }
        }

        return this.automationName;
    }
}
