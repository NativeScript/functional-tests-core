package functional.tests.core.Device.iOS;

import functional.tests.core.Find.Wait;
import functional.tests.core.Log.Log;
import functional.tests.core.OSUtils.FileSystem;
import functional.tests.core.OSUtils.OSUtils;
import functional.tests.core.Settings.Settings;

import java.io.IOException;
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

    public static void deleteSimulator(String deviceName) {
        List<String> simulators = getSimulatorsIdsByName(deviceName);
        for (String sim : simulators) {
            Log.info("Delete " + deviceName + " simulator with id: " + sim);
            OSUtils.runProcess("xcrun simctl delete " + sim);
        }
    }

    public static String createSimulator(String simulatorName, String deviceType, String iOSVersion) {

        // Due to Xcode 7.1 issues screenshots of iOS9 devices are broken if device is not zoomed at 100%
        if (Settings.platformVersion.contains("9")) {
            resetSimulatorSettings();
        }

        Log.info("Create simulator with following command:");
        Log.info("xcrun simctl create \"" + simulatorName + "\" \"" + deviceType + "\" \"" + iOSVersion + "\"");
        String output = OSUtils.runProcess("xcrun simctl create \"" + simulatorName + "\" \"" + deviceType + "\" \"" + iOSVersion + "\"");
        return output;
    }
}
