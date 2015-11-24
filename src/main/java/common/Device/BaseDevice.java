package common.Device;

import common.Device.Android.AndroidDevice;
import common.Device.iOS.iOSDevice;
import common.Enums.DeviceType;
import common.Enums.PlatformType;
import common.Exceptions.DeviceException;
import common.Exceptions.UnknownPlatformException;
import common.OSUtils.Archive;
import common.OSUtils.FileSystem;
import common.Settings.Settings;

import java.io.File;
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
            AndroidDevice.initDevice();
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
}
