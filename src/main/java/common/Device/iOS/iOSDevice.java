package common.Device.iOS;

import common.Device.Android.Adb;
import common.Enums.DeviceType;
import common.Exceptions.DeviceException;
import common.Log.Log;
import common.OSUtils.OSUtils;
import common.Settings.Settings;

import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Created by Dimitar on 10/9/2015.
 */
public class iOSDevice {

    public static String simulatorGuid = null;

    public static void initDevice() throws DeviceException {
        if (Settings.deviceType == DeviceType.Simulator) {
            // Delete simulator specified by settings
            Simctl.deleteSimulator(Settings.deviceName);

            // Create simulator specified by settings
            String result = Simctl.createSimulator(Settings.deviceName, Settings.simulatorType, Settings.platformVersion);
            if (result.toLowerCase().contains("error") || result.toLowerCase().contains("invalid")) {
                Log.fatal("Failed to create simulator. Error: " + result);
                throw new DeviceException("Failed to create simulator. Error: " + result);
            } else {
                simulatorGuid = result;
            }
        }
    }

    public static void stopDevice() {
        if (Settings.deviceType == DeviceType.Simulator) {
            OSUtils.stopProcess("iOS Simulator");
            OSUtils.stopProcess("Simulator");
            Log.info("iOS Simulator killed.");
        }
    }

    public static void stopApps(List<String> uninstallAppsList) {

    }

    public static void uninstallApps(List<String> uninstallAppsList) {

    }
}
