package functional.tests.core.settings;

import functional.tests.core.enums.DeviceType;
import functional.tests.core.enums.OSType;
import functional.tests.core.enums.PlatformType;
import functional.tests.core.exceptions.AppiumException;
import functional.tests.core.exceptions.DeviceException;
import functional.tests.core.exceptions.HostException;
import functional.tests.core.exceptions.MobilePlatformException;
import functional.tests.core.extensions.SystemExtension;
import functional.tests.core.log.LoggerBase;
import functional.tests.core.utils.Aapt;
import functional.tests.core.utils.OSUtils;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Doctor verify system and settings.
 * Executed before tests and do not run them if problem is found.
 */
public class Doctor {

    private static final LoggerBase LOGGER_BASE = LoggerBase.getLogger("Doctor");

    /**
     * Verify system is ready to run tests and settings are valid.
     *
     * @param settings Current settings.
     * @throws Exception When system is not ready to run tests or settings are invalid.
     */
    public static void check(Settings settings) throws Exception {
        try {
            verifyJava();
            verifyAndroidHome(settings);
            verifyAapt(settings);
            verifyMobileOS(settings);
            verifyDeviceType(settings);
            verifyTestAppPath(settings);
            verifyOSTypeAndMobilePlatform(settings);
            verifyXcrun(settings);
            verifyXConfig(settings);
            verifyIdeviceinstaller(settings);
            verifyAppium(settings);
            verifyTestAppProperties(settings);
            LOGGER_BASE.info("System and settings are OK.");
        } catch (Exception e) {
            LOGGER_BASE.error("System and settings are NOT OK.");
            LOGGER_BASE.fatal(e.getMessage());
            SystemExtension.interruptProcess("Check Setting  again");
        }
    }

    /**
     * Verify host OS has Java 1.8+.
     *
     * @throws Exception When Java is not available or Java version is < 1.8.
     */
    protected static void verifyJava() throws Exception {
        String version = System.getProperty("java.version");
        int pos = version.indexOf('.');
        pos = version.indexOf('.', pos + 1);
        double ver = Double.parseDouble(version.substring(0, pos));
        if (ver < 1.8) {
            String message = "Please use Java 1.8+. Current version is: " + version;
            throw new FileNotFoundException(message);
        }
    }


    /**
     * Verify ANDROID_HOME variable is set.
     *
     * @param settings Current settings.
     * @throws Exception When ANDROID_HOME is not set.
     */
    protected static void verifyAndroidHome(Settings settings) throws Exception {
        if (settings.platform == PlatformType.Andorid) {
            String androidHome = System.getenv("ANDROID_HOME");
            if (androidHome == null) {
                String error = "Please set ANDROID_HOME environment variable.";
                throw new Exception(error);
            }
        }
    }

    /**
     * Verify Android Asset Packaging Tool (aapt) is available on host os.
     *
     * @param settings Current settings.
     * @throws Exception When aapt is not available.
     */
    protected static void verifyAapt(Settings settings) throws Exception {
        if (settings.platform == PlatformType.Andorid) {
            Aapt aapt = new Aapt(settings);
            if (aapt.aaptPath == null) {
                String error = "Failed to find aapt! Please make sure Android build-tools are installed.";
                throw new Exception(error);
            }
        }
    }

    /**
     * Verify mobile operating system settings is OK.
     *
     * @param settings Current settings.
     * @throws MobilePlatformException When mobile OS is not Android or iOS.
     */
    protected static void verifyMobileOS(Settings settings) throws MobilePlatformException {
        if (settings.platform == PlatformType.Other) {
            String error = "Unknown mobile platform.";
            throw new MobilePlatformException(error);
        }
    }

    /**
     * Verify mobile operating system settings is OK.
     *
     * @param settings Current settings.
     * @throws DeviceException When mobile device type settings is not correct.
     */
    protected static void verifyDeviceType(Settings settings) throws DeviceException {
        if (settings.deviceType == DeviceType.Other) {
            String error = "Unknown device type.";
            throw new DeviceException(error);
        }
    }

    /**
     * Verify test application is available.
     *
     * @param settings Current settings.
     * @throws FileNotFoundException When test application is not found.
     */
    protected static void verifyTestAppPath(Settings settings) throws FileNotFoundException {
        File appDir = new File(Settings.BASE_TEST_APP_DIR);
        File app = new File(appDir, settings.testAppName);

        if (settings.deviceType == DeviceType.Simulator) {
            app = new File(appDir, settings.ios.testAppArchive);
        }

        if (!app.exists()) {
            String message = "Failed to find test app: " + app.getAbsolutePath();
            throw new FileNotFoundException(message);
        }
    }

    /**
     * Verify host OS is capable to automate specified mobile platform.
     *
     * @param settings Current settings.
     * @throws Exception Exception when host OS is not compatible with mobile OS.
     */
    protected static void verifyOSTypeAndMobilePlatform(Settings settings) throws Exception {
        if ((settings.platform == PlatformType.iOS) && (settings.os != OSType.MacOS)) {
            String error = "Can not run iOS tests on Windows and Linux";
            throw new Exception(error);
        }
    }

    /**
     * (Only for iOS) Verify xcrun is available on host OS.
     *
     * @param settings Current settings.
     * @throws Exception When xcrun is not available.
     */
    protected static void verifyXcrun(Settings settings) throws Exception {
        if (settings.platform == PlatformType.iOS) {
            String output = OSUtils.runProcess("xcrun --version");
            if (!output.contains("xcrun version")) {
                String error = "xcrun is not available. Please install it. Error: " + output;
                throw new Exception(error);
            }
        }
    }

    /**
     * (Only for iOS physical devices) Verify ideviceinstaller is available on host OS.
     *
     * @param settings Current settings.
     * @throws Exception When ideviceinstaller is not available.
     */
    protected static void verifyIdeviceinstaller(Settings settings) throws Exception {
        if (settings.deviceType == DeviceType.iOS) {
            String output = OSUtils.runProcess("ideviceinstaller");
            if (!output.contains("Manage apps on iOS devices")) {
                String error = "Please install or repair ideviceinstaller. Error: " + output;
                throw new Exception(error);
            }
        }
    }

    /**
     * Verify Appium is available and Appium version match specified version in settings.
     *
     * @param settings Current settings.
     * @throws AppiumException When Appium is not found or version does not match specified in config file.
     */
    private static void verifyAppium(Settings settings) throws AppiumException {
        String message;
        String appiumVersion = OSUtils.runProcess("appium -v ").trim();
        if (appiumVersion.equals("") || appiumVersion.contains("not installed")) {
            message = "Appium version " +
                    settings.appiumVersion + " is NOT installed! " +
                    "Command \"appium -v\" returns \"" + appiumVersion + "\".";
            throw new AppiumException(message);
        } else if (!appiumVersion.contains(settings.appiumVersion)) {
            message = "Appium version " +
                    appiumVersion + " is NOT compatible with desired version " + settings.appiumVersion + "!";
            throw new AppiumException(message);
        }
    }

    /**
     * Verify signing settings for iOS real devices.
     *
     * @param settings Current settings.
     * @throws HostException When DEVELOPMENT_TEAM environment variable is not set.
     */
    private static void verifyXConfig(Settings settings) throws HostException {
        if (settings.deviceType == DeviceType.iOS) {
            String teamId = System.getenv("DEVELOPMENT_TEAM");
            if (teamId == null) {
                throw new HostException("DEVELOPMENT_TEAM environment variable does not exists!");
            }
            //TODO(dtopuzov): Verify xconfig file exists.
        }
    }

    /**
     * Verify test app related settings.
     *
     * @param settings Current settings.
     * @throws Exception Exception when something is wrong.
     */
    private static void verifyTestAppProperties(Settings settings) throws Exception {

        // Verify testAppFriendlyName
        if (settings.testAppFriendlyName == null) {
            throw new Exception("testAppFriendlyName can not be null.");
        }

        // Verify testAppImageFolder (this is only app name, not full path, so can't assert is exists)
        if (settings.testAppImageFolder == null) {
            throw new Exception("testAppImageFolder can not be null.");
        }
    }
}
