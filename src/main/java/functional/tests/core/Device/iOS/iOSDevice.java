package functional.tests.core.Device.iOS;

import functional.tests.core.Enums.DeviceType;
import functional.tests.core.Exceptions.DeviceException;
import functional.tests.core.Log.Log;
import functional.tests.core.OSUtils.OSUtils;
import functional.tests.core.Settings.Settings;

import java.util.List;

/**
 * Created by Dimitar on 10/9/2015.
 */
public class iOSDevice {

    private static String simulatorGuid = null;

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
                Log.info("Simulator created: " + result);
            }

            // Verify simulator exists
            List<String> simulators = Simctl.getSimulatorsIdsByName(Settings.deviceName);
            if (simulators.size() == 1) {
                Log.info("Simulator " + Settings.deviceName + " exists.");
            } else if (simulators.size() > 1) {
                String error = "Ambiguous simulators. More than one simulator with name " + Settings.deviceName + " found.";
                Log.fatal(error);
                throw new DeviceException(error);
            } else if (simulators.size() == 0) {
                String error = "Simulator with name " + Settings.deviceName + " not found.";
                Log.fatal(error);
                throw new DeviceException(error);
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
