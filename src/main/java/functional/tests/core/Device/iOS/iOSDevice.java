package functional.tests.core.Device.iOS;

import functional.tests.core.Enums.DeviceType;
import functional.tests.core.Exceptions.DeviceException;
import functional.tests.core.Log.Log;
import functional.tests.core.OSUtils.OSUtils;
import functional.tests.core.Settings.Settings;

import java.util.ArrayList;
import java.util.List;

public class iOSDevice {

    private static String simulatorGuid = null;

    private static void uninstallApp(String appId) {
        String uninstallResult = OSUtils.runProcess(true, "ideviceinstaller -u " + Settings.deviceId + " -U " + appId);
        if (uninstallResult.contains("Complete")) {
            Log.info(appId + " successfully uninstalled.");
        } else {
            Log.error("Failed to uninstall " + appId + ". Error: " + uninstallResult);
        }
    }

    private static List<String> getInstalledApps() {
        String rowData = OSUtils.runProcess(true, "ideviceinstaller -u " + Settings.deviceId + " -l");
        String trimData = rowData.replace("package:", "");
        String[] rowList = trimData.split("\\r?\\n");
        List<String> list = new ArrayList<>();
        for (String item : rowList) {
            if (item.contains(".") && item.contains("-")) {
                String rowAppId = item.replace(" ", "");
                String appId = rowAppId.split("-")[0];
                list.add(appId);
            }
        }
        return list;
    }

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
            for (String device : deviceList) {
                if (device.contains("iP")) {
                    Log.info(device);
                }
                if (device.contains(Settings.deviceName)) {
                    found = true;
                }
            }

            if (found) {
                Log.info("Simulator " + Settings.deviceName + " exists.");
            } else {
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
        List<String> installedApps = getInstalledApps();

        for (String appToUninstall : uninstallAppsList) {
            for (String appId : installedApps) {
                if (appId.contains(appToUninstall)) {
                    uninstallApp(appId);
                }
            }
        }
    }
}
