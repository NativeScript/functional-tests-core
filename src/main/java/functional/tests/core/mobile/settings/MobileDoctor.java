package functional.tests.core.mobile.settings;

import functional.tests.core.enums.DeviceType;
import functional.tests.core.enums.OSType;
import functional.tests.core.enums.PlatformType;
import functional.tests.core.exceptions.AppiumException;
import functional.tests.core.exceptions.DeviceException;
import functional.tests.core.exceptions.HostException;
import functional.tests.core.exceptions.MobilePlatformException;
import functional.tests.core.extensions.SystemExtension;
import functional.tests.core.log.LoggerBase;
import functional.tests.core.settings.Doctor;
import functional.tests.core.utils.Aapt;
import functional.tests.core.utils.OSUtils;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Doctor verify system and settings.
 * Executed before tests and do not run them if problem is found.
 */
public class MobileDoctor extends Doctor {

    private static final LoggerBase LOGGER_BASE = LoggerBase.getLogger("Doctor");
    private MobileSettings mobileSettings;

    public MobileDoctor(MobileSettings mobileSettings) {
        super(mobileSettings);
        this.mobileSettings = mobileSettings;
    }

    /**
     * Verify system is ready to run tests and mobileSettings are valid.
     *
     * @throws Exception When system is not ready to run tests or mobileSettings are invalid.
     */

    @Override
    public void check() throws Exception {
        super.check();
        try {
            verifyAppium(this.mobileSettings);
            verifyAndroidHome(this.mobileSettings);
            verifyAapt(this.mobileSettings);
            verifyMobileOS(this.mobileSettings);
            verifyDeviceType(this.mobileSettings);
            verifyTestAppPath(this.mobileSettings);
            verifyOSTypeAndMobilePlatform(this.mobileSettings);
            verifyXcrun(this.mobileSettings);
            verifyXConfig(this.mobileSettings);
            verifyIdeviceinstaller(this.mobileSettings);
            verifyAppium(this.mobileSettings);
            verifyTestAppProperties(this.mobileSettings);
            LOGGER_BASE.info("MobileSettings are OK.");
        } catch (Exception e) {
            LOGGER_BASE.error("MobileSettings are NOT OK.");
            LOGGER_BASE.fatal(e.getMessage());
            SystemExtension.interruptProcess("Check Setting  again");
        }
    }

    /**
     * Verify ANDROID_HOME variable is set.
     *
     * @param mobileSettings Current mobileSettings.
     * @throws Exception When ANDROID_HOME is not set.
     */
    protected static void verifyAndroidHome(MobileSettings mobileSettings) throws Exception {
        if (mobileSettings.platform == PlatformType.Android) {
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
     * @param mobileSettings Current mobileSettings.
     * @throws Exception When aapt is not available.
     */
    protected static void verifyAapt(MobileSettings mobileSettings) throws Exception {
        if (mobileSettings.platform == PlatformType.Android) {
            Aapt aapt = new Aapt(mobileSettings);
            if (aapt.aaptPath == null) {
                String error = "Failed to find aapt! Please make sure Android build-tools are installed.";
                throw new Exception(error);
            }
        }
    }

    /**
     * Verify mobile operating system mobileSettings is OK.
     *
     * @param mobileSettings Current mobileSettings.
     * @throws MobilePlatformException When mobile OS is not Android or iOS.
     */
    protected static void verifyMobileOS(MobileSettings mobileSettings) throws MobilePlatformException {
        if (mobileSettings.platform == PlatformType.Other) {
            String error = "Unknown mobile platform.";
            throw new MobilePlatformException(error);
        }
    }

    /**
     * Verify mobile operating system mobileSettings is OK.
     *
     * @param mobileSettings Current mobileSettings.
     * @throws DeviceException When mobile device type mobileSettings is not correct.
     */
    protected static void verifyDeviceType(MobileSettings mobileSettings) throws DeviceException {
        if (mobileSettings.deviceType == DeviceType.Other) {
            String error = "Unknown device type.";
            throw new DeviceException(error);
        }
    }

    /**
     * Verify test application is available.
     *
     * @param mobileSettings Current mobileSettings.
     * @throws FileNotFoundException When test application is not found.
     */
    protected static void verifyTestAppPath(MobileSettings mobileSettings) throws FileNotFoundException {
        File appDir = new File(MobileSettings.BASE_TEST_APP_DIR);
        File app = new File(appDir, mobileSettings.testAppFileName);

        if (mobileSettings.deviceType == DeviceType.Simulator) {
            app = new File(appDir, mobileSettings.ios.testAppArchive);
        }

        if (!app.exists()) {
            String message = "Failed to find test app: " + app.getAbsolutePath();
            throw new FileNotFoundException(message);
        }
    }

    /**
     * Verify host OS is capable to automate specified mobile platform.
     *
     * @param mobileSettings Current mobileSettings.
     * @throws Exception Exception when host OS is not compatible with mobile OS.
     */
    protected static void verifyOSTypeAndMobilePlatform(MobileSettings mobileSettings) throws Exception {
        if ((mobileSettings.platform == PlatformType.iOS) && (mobileSettings.os != OSType.MacOS)) {
            String error = "Can not run iOS tests on Windows and Linux";
            throw new Exception(error);
        }
    }

    /**
     * (Only for iOS) Verify xcrun is available on host OS.
     *
     * @param mobileSettings Current mobileSettings.
     * @throws Exception When xcrun is not available.
     */
    protected static void verifyXcrun(MobileSettings mobileSettings) throws Exception {
        if (mobileSettings.platform == PlatformType.iOS) {
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
     * @param mobileSettings Current mobileSettings.
     * @throws Exception When ideviceinstaller is not available.
     */
    protected static void verifyIdeviceinstaller(MobileSettings mobileSettings) throws Exception {
        if (mobileSettings.deviceType == DeviceType.iOS) {
            String output = OSUtils.runProcess("ideviceinstaller");
            if (!output.contains("Manage apps on iOS devices")) {
                String error = "Please install or repair ideviceinstaller. Error: " + output;
                throw new Exception(error);
            }
        }
    }

    /**
     * Verify Appium is available and Appium version match specified version in mobileSettings.
     *
     * @param mobileSettings Current mobileSettings.
     * @throws AppiumException When Appium is not found or version does not match specified in config file.
     */
    private static void verifyAppium(MobileSettings mobileSettings) throws AppiumException {
        String message;
        String appiumVersion = OSUtils.runProcess("appium -v ").trim();
        if (appiumVersion.equals("") || appiumVersion.contains("not installed")) {
            message = "Appium version " +
                    mobileSettings.appiumVersion + " is NOT installed! " +
                    "Command \"appium -v\" returns \"" + appiumVersion + "\".";
            throw new AppiumException(message);
        } else if (!appiumVersion.contains(mobileSettings.appiumVersion)) {
            message = "Appium version " +
                    appiumVersion + " is NOT compatible with desired version " + mobileSettings.appiumVersion + "!";
            throw new AppiumException(message);
        }
    }

    /**
     * Verify signing mobileSettings for iOS real devices.
     *
     * @param mobileSettings Current mobileSettings.
     * @throws HostException When DEVELOPMENT_TEAM environment variable is not set.
     */
    private static void verifyXConfig(MobileSettings mobileSettings) throws HostException {
        if (mobileSettings.deviceType == DeviceType.iOS) {
            String teamId = System.getenv("DEVELOPMENT_TEAM");
            if (teamId == null) {
                throw new HostException("DEVELOPMENT_TEAM environment variable does not exists!");
            }
            //TODO(dtopuzov): Verify xconfig file exists.
        }
    }

    /**
     * Verify test app related mobileSettings.
     *
     * @param mobileSettings Current mobileSettings.
     * @throws Exception Exception when something is wrong.
     */
    private static void verifyTestAppProperties(MobileSettings mobileSettings) throws Exception {

        // Verify testAppFriendlyName
        if (mobileSettings.testAppFriendlyName == null) {
            throw new Exception("testAppFriendlyName can not be null.");
        }

        // Verify testAppImageFolder (this is only app name, not full path, so can't assert is exists)
        if (mobileSettings.testAppImageFolder == null) {
            throw new Exception("testAppImageFolder can not be null.");
        }
    }
}
