package functional.tests.core.device.ios;

import functional.tests.core.exceptions.DeviceException;
import functional.tests.core.find.Wait;
import functional.tests.core.log.LoggerBase;
import functional.tests.core.settings.Settings;
import functional.tests.core.utils.FileSystem;
import functional.tests.core.utils.OSUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO(dtopuzov): Add docs for everything in this class.
 */
public class Simctl {

    private static final LoggerBase LOGGER_BASE = LoggerBase.getLogger("Simctl");
    private Settings settings;

    /**
     * TODO(): Add docs.
     *
     * @param settings
     */
    public Simctl(Settings settings) {
        this.settings = settings;
    }

    /**
     * TODO(): Add docs.
     *
     * @param name
     * @return
     */
    public static List<String> getSimulatorUdidsByName(String name) {
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

    public String ensureOnlyOneSimulatorExist() {
        LOGGER_BASE.error("Multiple simulators with name " + this.settings.deviceName + " found. Deleting them ...");
        if (Simctl.getSimulatorUdidsByName(this.settings.deviceName).size() != 1) {
            Simctl.deleteSimulator(this.settings.deviceName);
            return this.createSimulator(this.settings.deviceName, this.settings.ios.simulatorType, this.settings.platformVersion.toString());
        } else {
            return this.settings.deviceId;
        }

    }

    /**
     * TODO(): Add docs.
     */
    public static void deleteSimulator(String name) {
        for (String udid : Simctl.getSimulatorUdidsByName(name)) {
            LOGGER_BASE.info("Deleting " + name + " simulator with udid: " + udid);
            OSUtils.runProcess("xcrun simctl delete " + udid);
        }
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
        LOGGER_BASE.info("Create simulator with following command:");
        String command = "xcrun simctl create \"" + simulatorName +
                "\" \"com.apple.CoreSimulator.SimDeviceType." + deviceType.trim().replace(" ", "-") +
                "\" \"com.apple.CoreSimulator.SimRuntime.iOS-" + iOSVersion.trim().replace(".", "-") + "\"";

        LOGGER_BASE.info(command);
        String output = OSUtils.runProcess(command);

        if (output.toLowerCase().contains("error") || output.toLowerCase().contains("invalid")) {
            Simctl.LOGGER_BASE.fatal("Failed to create simulator. Error: " + output);
            try {
                throw new DeviceException("Failed to create simulator. Error: " + output);
            } catch (DeviceException e) {
                e.printStackTrace();
            }
        } else {
            String udid = output;
            String[] list = output.split("\\r?\\n");
            for (String line : list) {
                if (line.contains("-")) {
                    udid = line.trim();
                }
                this.settings.deviceId = udid;
                Simctl.LOGGER_BASE.info("Simulator created with UDID: " + this.settings.deviceId);
            }
        }

        return output;
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
     * This will reset content and setting of emulator only if the emulator is not booted.
     */
    public void eraseData() {
        Simctl.LOGGER_BASE.warn("Erase data from simulator");
        String command = "xcrun simctl erase " + this.settings.deviceId;
        Simctl.LOGGER_BASE.warn(command);
        OSUtils.runProcess(command);
    }

    /**
     * TODO(): Add docs.
     *
     * @param deviceId
     * @return
     */
    public boolean checkIfSimulatorIsAlive(String deviceId) {
        String rowDevices = OSUtils.runProcess("instruments -s | grep Booted");
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
}
