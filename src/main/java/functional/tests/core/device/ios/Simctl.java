package functional.tests.core.device.ios;

import functional.tests.core.find.Wait;
import functional.tests.core.log.LoggerBase;
import functional.tests.core.settings.Settings;
import functional.tests.core.utils.FileSystem;
import functional.tests.core.utils.OSUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TODO(dtopuzov): Add docs for everything in this class.
 */
public class Simctl {

    private static final LoggerBase LOGGER_BASE = LoggerBase.getLogger("Simctl");
    private Settings settings;
    private List<String> udids;

    /**
     * TODO(): Add docs.
     *
     * @param settings
     */
    public Simctl(Settings settings) {
        this.settings = settings;
        this.udids = this.getSimulatorUdidsByName(this.settings.deviceName);
    }

    public static String getSimulatorId(String simulatorName) {
        String simulatorData = OSUtils.runProcess(String.format("instruments -s | grep \"%s\"", simulatorName));

        Pattern pattern = Pattern.compile("\\[(.*?)\\]");
        Matcher matcher = pattern.matcher(simulatorData);
        String simulatorId = "";
        if (matcher.find()) {
            simulatorId = matcher.group(1);
        }

        return simulatorId;
    }

    // TODO(vchimev): Rethink!
    // - the name of this method,
    // - get udid of the simulator in constructor

    /**
     * TODO(): Add docs.
     *
     * @return
     */
    protected String initSimulator() {
        if (this.udids.size() == 0) {
            LOGGER_BASE.error("Simulator " + this.settings.deviceName + " does NOT exist!");
            // TODO(vchimev): Fail test execution.
        } else if (this.udids.size() == 1) {
            LOGGER_BASE.info("Simulator " + this.settings.deviceName + " exists.");
            this.settings.deviceId = this.udids.get(0);
            return this.udids.get(0);
        } else {
            LOGGER_BASE.error("Multiple simulators with name " + this.settings.deviceName + " found. Deleting them ...");
            this.deleteSimulator();
        }

        return "";
    }

    /**
     * TODO(): Add docs.
     *
     * @param name
     * @return
     */
    protected List<String> getSimulatorUdidsByName(String name) {
        String command = "xcrun simctl list devices | grep " + name.replaceAll("\\s", "\\\\ ");
        String output = OSUtils.runProcess(command);
        String[] lines = output.split("\\r?\\n");
        List<String> list = new ArrayList<>();

        for (String line : lines) {
            // TODO(vchimev): Rethink! If the simulator is unavailable, it means that:
            // - runtime profile not found => test execution should exit earlier,
            // - the simulator is broken and should be recreated.
            if (!line.contains("unavailable") && !line.isEmpty()) {
                String udid = line.substring(line.indexOf('(') + 1, line.indexOf(')'));
                list.add(udid);
            }
        }

        return list;
    }

    /**
     * TODO(): Add docs.
     */
    public void deleteSimulator() {
        for (String udid : this.udids) {
            LOGGER_BASE.info("Deleting " + this.settings.deviceName + " simulator with udid: " + udid);
            OSUtils.runProcess("xcrun simctl delete " + udid);
        }
    }

    /**
     * TODO(): Add docs.
     */
    protected void resetSimulatorSettings() {
        if (this.settings.debug) {
            LOGGER_BASE.info("[Debug mode] Do not reset sim settings.");
        } else {
            String path = this.settings.screenshotResDir + File.separator + this.settings.testAppImageFolder;
            if (FileSystem.exist(path)) {
                LOGGER_BASE.info("This test run will compare images. Reset simulator zoom.");
                try {
                    FileSystem.deletePath(System.getProperty("user.home") + "/Library/Preferences/com.apple.iphonesimulator.plist");
                    Wait.sleep(1000);
                    OSUtils.runProcess("defaults write ~/Library/Preferences/com.apple.iphonesimulator SimulatorWindowLastScale \"1\"");
                    Wait.sleep(1000);
                    LOGGER_BASE.info("Global simulator settings restarted");
                } catch (IOException e) {
                    LOGGER_BASE.error("Failed to restart global simulator settings.");
                }
            } else {
                LOGGER_BASE.info("No need to restart simulator settings.");
            }
        }
    }

    /**
     * TODO(): Add docs.
     *
     * @param deviceName
     * @return
     */
    public boolean checkIfSimulatorExists(String deviceName) {
        String rowDevices = OSUtils.runProcess("instruments -s");
        String[] deviceList = rowDevices.split("\\r?\\n");

        boolean found = false;
        for (String device : deviceList) {
            if (device.contains("iP")) {
                LOGGER_BASE.debug(device);
            }
            if (device.contains(this.settings.deviceName)) {
                found = true;
            }
        }
        return found;
    }

    /**
     * TODO(): Add docs.
     *
     * @param simulatorName
     * @param deviceType
     * @param iOSVersion
     * @return
     */
    public String createSimulator(String simulatorName, String deviceType, String iOSVersion) {
        if (this.settings.debug) {
            LOGGER_BASE.info("[Debug mode] Do not reset sim settings.");
        } else {
            // Due to Xcode 7.1 issues screenshots of iOS9 devices are broken if device is not zoomed at 100%
            if (this.settings.platformVersion.toString().contains("9")) {
                this.resetSimulatorSettings();
            }
        }

        LOGGER_BASE.info("Create simulator with following command:");
        String command = "xcrun simctl create \"" + simulatorName +
                "\" \"com.apple.CoreSimulator.SimDeviceType." + deviceType.trim().replace(" ", "-") +
                "\" \"com.apple.CoreSimulator.SimRuntime.iOS-" + iOSVersion.trim().replace(".", "-") + "\"";

        LOGGER_BASE.info(command);
        String output = OSUtils.runProcess(command);

        return output;
    }

    /**
     * TODO(): Add docs.
     */
    public void reinstallApp() {
        String uninstallCommand = "xcrun simctl uninstall booted " + this.settings.packageId;
        String installCommand = "xcrun simctl install booted " + Settings.BASE_TEST_APP_DIR + File.separator + this.settings.testAppName;
        OSUtils.runProcess(uninstallCommand);
        Wait.sleep(250);
        OSUtils.runProcess(installCommand);
        Wait.sleep(250);
        LOGGER_BASE.info(this.settings.packageId + " re installed.");
    }

    /**
     * TODO(): Add docs.
     *
     * @param deviceId
     * @return
     */
    public boolean checkIfSimulatorIsAlive(String deviceId) {
        // TODO(): Implement it.
        return false;
    }
}
