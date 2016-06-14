package functional.tests.core.Device.iOS;

import functional.tests.core.Find.Wait;
import functional.tests.core.Log.Log;
import functional.tests.core.OSUtils.FileSystem;
import functional.tests.core.OSUtils.OSUtils;
import functional.tests.core.Settings.Settings;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Simctl {

    private static void resetSimulatorSettings() {
        try {
            FileSystem.deletePath(System.getProperty("user.home") + "/Library/Preferences/com.apple.iphonesimulator.plist");
            Wait.sleep(1000);
            OSUtils.runProcess("defaults write ~/Library/Preferences/com.apple.iphonesimulator SimulatorWindowLastScale \"1\"");
            Wait.sleep(1000);
            Log.info("Global simulator settings restarted");
        } catch (IOException e) {
            Log.error("Failed to restart global simulator settings.");
        }
    }

    protected static List<String> getSimulatorsIdsByName(String deviceName) {
        String rowData = OSUtils.runProcess("xcrun simctl list devices");
        String[] rowList = rowData.split("\\r?\\n");

        List<String> list = new ArrayList<String>();

        for (String rowLine : rowList) {
            if ((rowLine.contains(deviceName)) && (rowLine.contains("(")) && (!rowLine.contains("unavailable"))) {
                String line = rowLine.substring(rowLine.indexOf('(') + 1, rowLine.indexOf(')')).replaceAll("\\s+", "");
                list.add(line);
            }
        }

        return list;
    }

    public static boolean checkIfSimulatorExists(String deviceName) {
        String rowDevices = OSUtils.runProcess("instruments -s");
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
        return found;
    }

    public static void deleteSimulator(String deviceName) {
        List<String> simulators = getSimulatorsIdsByName(deviceName);
        for (String sim : simulators) {
            Log.info("Delete " + deviceName + " simulator with id: " + sim);
            OSUtils.runProcess("xcrun simctl delete " + sim);
        }
    }

    public static String createSimulator(String simulatorName, String deviceType, String iOSVersion) {
        if (Settings.debug) {
            Log.info("[Debug mode] Do not reset sim settings.");
        } else {
            // Due to Xcode 7.1 issues screenshots of iOS9 devices are broken if device is not zoomed at 100%
            if (Settings.platformVersion.contains("9")) {
                // Restart simulator settings only in case images are available
                String path = Settings.screenshotResDir + File.separator + Settings.testAppImageFolder;
                if (FileSystem.exist(path)) {
                    Log.info("This test run will compare images. Reset simulator zoom.");
                    resetSimulatorSettings();
                } else {
                    Log.info("This test run will not compare images. Use existing simulator settings.");
                }
            }
        }

        Log.info("Create simulator with following command:");
        Log.info("xcrun simctl create \"" + simulatorName + "\" \"" + deviceType + "\" \"" + iOSVersion + "\"");
        String output = OSUtils.runProcess("xcrun simctl create \"" + simulatorName + "\" \"" + deviceType + "\" \"" + iOSVersion + "\"");
        return output;
    }

    public static void reinstallApp() {
        String uninstallCommand = "xcrun simctl uninstall booted " + Settings.packageId;
        String installCommand = "xcrun simctl install booted " + Settings.baseTestAppDir + File.separator + Settings.testAppName;
        OSUtils.runProcess(uninstallCommand);
        Wait.sleep(500);
        OSUtils.runProcess(installCommand);
        Wait.sleep(500);
        Log.info(Settings.packageId + " re installed.");
    }
}
