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
            String rowDevices = OSUtils.runProcess(true, "instruments -s");
            String[] deviceList = rowDevices.split("\\r?\\n");

            boolean found = false;
            for(String device: deviceList){
                if (device.contains("iP")) {
                    Log.info(device);
                }
                if (device.contains(Settings.deviceName)){
                    found = true;
                }
            }

            if (found){
                Log.info("Simulator " + Settings.deviceName + " exists.");
            }
            else{
                String error = "Simulator " + Settings.deviceName + " does not exist.";
                Log.error(error);
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
