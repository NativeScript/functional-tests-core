package functional.tests.core.Settings;

import functional.tests.core.Device.Android.Adb;
import functional.tests.core.Device.iOS.iOSDevice;
import functional.tests.core.Enums.DeviceType;
import functional.tests.core.Enums.OSType;
import functional.tests.core.Enums.PlatformType;
import functional.tests.core.Exceptions.AppiumException;
import functional.tests.core.Exceptions.DeviceException;
import functional.tests.core.Exceptions.UnknownDeviceTypeException;
import functional.tests.core.Exceptions.UnknownPlatformException;
import functional.tests.core.Log.Log;
import functional.tests.core.OSUtils.OSUtils;
import org.testng.Assert;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

public class Doctor {

    public static void check() throws Exception {
        // Verify setup is correct
        Doctor.verifyJava();
        Doctor.verifyAndroidHome();
        Doctor.verifyMobileOS();
        Doctor.verifyDeviceType();
        Doctor.verifyTestAppPath();
        Doctor.verifyOSTypeAndMobilePlatform();
        Doctor.verifyXcrun();
        Doctor.verifyIdeviceinstaller();
        Doctor.verifyAppium();

        if (Settings.isRealDevice == true) {
            Doctor.verifyDevice();
        }
    }

    // Verify Java version
    protected static void verifyJava() throws Exception {
        String version = System.getProperty("java.version");
        int pos = version.indexOf('.');
        pos = version.indexOf('.', pos + 1);
        double ver = Double.parseDouble(version.substring(0, pos));
        if (ver < 1.8) {
            String message = "Please use Java 1.8+. Current version is: " + version;
            Log.fatal(message);
            throw new FileNotFoundException(message);
        }
    }

    // Verify app under test exists in testapp folder
    protected static void verifyTestAppPath() throws FileNotFoundException {
        File appDir = new File(Settings.baseTestAppDir);
        File app = new File(appDir, Settings.testAppName);

        if (Settings.deviceType == DeviceType.Simulator) {
            app = new File(appDir, Settings.testAppArchive);
        }

        if (!app.exists()) {
            String message = "Failed to find test app: " + app.getAbsolutePath();
            Log.fatal(message);
            throw new FileNotFoundException(message);
        }
    }

    // Verify OS and Mobile Platform
    protected static void verifyMobileOS() throws Exception {
        if ((Settings.platform == PlatformType.Other)) {
            String error = "Unknown mobile platform.";
            Log.fatal(error);
            throw new UnknownPlatformException(error);
        }
    }

    // Verify Device Type
    protected static void verifyDeviceType() throws Exception {
        if ((Settings.deviceType == DeviceType.Other)) {
            String error = "Unknown device type.";
            Log.fatal(error);
            throw new UnknownDeviceTypeException(error);
        }
    }

    // Verify Device
    protected static void verifyDevice() throws Exception {
        Boolean isAvailable = false;
        if (Settings.platform == PlatformType.Andorid) {
            List<String> devices = Adb.getDevices();
            for (String device : devices) {
                if (device.trim().contains(Settings.deviceId)) {
                    isAvailable = true;
                    break;
                }
            }
        } else if (Settings.platform == PlatformType.iOS) {
            List<String> devices = iOSDevice.getDevices();
            for (String device : devices) {
                if (device.trim().contains(Settings.deviceId)) {
                    isAvailable = true;
                    break;
                }
            }
        }

        if (isAvailable == false) {
            String error = "Device " + Settings.deviceId + " NOT available.";
            Log.fatal(error);
            throw new DeviceException(error);
        }
    }

    // Verify OS and Mobile Platform
    protected static void verifyOSTypeAndMobilePlatform() throws Exception {
        if ((Settings.platform == PlatformType.iOS) && (Settings.OS != OSType.MacOS)) {
            String error = "Can not run iOS tests on Windows and Linux";
            Log.fatal(error);
            throw new Exception(error);
        }
    }

    // Verify ANDROID_HOME path (only for Android test runs)
    protected static void verifyAndroidHome() throws Exception {
        if (Settings.platform == PlatformType.Andorid) {
            String androidHome = System.getenv("ANDROID_HOME");
            if (androidHome == null) {
                String error = "Please set ANDROID_HOME environment variable.";
                Log.fatal(error);
                throw new Exception(error);
            }
        }
    }

    // Verify xcrun is avalable (only for iOS)
    protected static void verifyXcrun() throws Exception {
        if (Settings.platform == PlatformType.iOS) {
            String output = OSUtils.runProcess("xcrun --version");
            if (!output.contains("xcrun version")) {
                String error = "xcrun is not available. Please install it. Error: " + output;
                throw new Exception(error);
            }
        }
    }

    // Verify ideviceinstaller is avalable (only for iOS real devices)
    protected static void verifyIdeviceinstaller() throws Exception {
        if (Settings.deviceType == DeviceType.iOS) {
            String output = OSUtils.runProcess("ideviceinstaller");
            if (!output.contains("Manage apps on iOS devices")) {
                String error = "Please install or repair ideviceinstaller. Error: " + output;
                throw new Exception(error);
            }
        }
    }

    private static void verifyAppium() throws AppiumException {
        String message;
        String appiumVersion = OSUtils.runProcess("appium -v ").trim();
        if (appiumVersion.equals("") || appiumVersion.contains("not installed")) {
            message = "Appium version " + Settings.appiumVersion + " is NOT installed! Command \"appium -v\" returns \"" + appiumVersion + "\".";
            Log.fatal(message);
            Assert.fail(message);
            // installAppium();
            return;
        } else if (!appiumVersion.contains(Settings.appiumVersion)) {
            message = "Appium version " + appiumVersion + " is NOT compatible with desired version " + Settings.appiumVersion + "!";
            Log.fatal(message);
            Assert.fail(message);
            // installAppium();
            return;
        } else {
            Log.warn("Appium version " + appiumVersion + " is compatible with desired version " + Settings.appiumVersion + "!");
        }
    }

//    // Update: if the appium version does not match, we will fail test execution while stabilize test infrastructure.
//    private static void installAppium() {
//        Log.warn("Installing Appium " + Settings.appiumVersion + " using npm install -g appium@" + Settings.appiumVersion + " -f!!!");
//        OSUtils.runProcess("npm install -g appium@" + Settings.appiumVersion + " -f");
//        Log.warn("Appium version " + Settings.appiumVersion + "is installed!!!");
//    }
}
