package functional.tests.core.mobile.device.ios;

import functional.tests.core.exceptions.DeviceException;
import functional.tests.core.log.LoggerBase;
import functional.tests.core.mobile.find.Wait;
import functional.tests.core.mobile.settings.MobileSettings;
import functional.tests.core.settings.Settings;
import functional.tests.core.utils.FileSystem;
import functional.tests.core.utils.OSUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * TODO(dtopuzov): Add docs for everything in this class.
 */
public class Simctl {

    private static final LoggerBase LOGGER_BASE = LoggerBase.getLogger("Simctl");
    private MobileSettings settings;
    private static String xcrun = "xcrun simctl ";
    private static String xcrunFormat = xcrun + " %s ";
    private static String xcrunListDevices = String.format(xcrunFormat, "list devices ");
    private static String xcrunListDevicesWithParamsFormat = xcrunListDevices + " %s ";

    /**
     * Using to controls simulators.
     *
     * @param settings
     */
    public Simctl(MobileSettings settings) {
        this.settings = settings;
    }

    /**
     * This will reset content and setting of emulator only if the emulator is not booted.
     */
    public static void eraseData(String deviceId) {
        Simctl.LOGGER_BASE.warn("Erase data from simulator");
        String command = String.format(xcrunFormat, "erase " + deviceId);
        Simctl.LOGGER_BASE.warn(command);
        OSUtils.runProcess(command);
    }

    /**
     * Deletes simulator.
     */
    public static void deleteSimulator(String name) {
        Simctl.getAvailableSimulatorUdidsByName(name).forEach(s -> {
            LOGGER_BASE.info("Deleting " + name + " simulator with udid: " + s);
            OSUtils.runProcess(String.format(xcrunFormat, "delete " + s));
        });
    }

    /**
     * Check if simulator is alive.
     *
     * @param deviceId
     * @return
     */
    public static boolean checkIfSimulatorIsBooted(String deviceId) {
        return Simctl.getSimulatorsBy(deviceId, "Booted").size() > 0;
    }

    /**
     * Check if simulator already exists.
     *
     * @param deviceName
     * @return
     */
    public static boolean checkIfSimulatorExists(String deviceName) {
        List<String> devices = Simctl.getSimulatorsBy(deviceName);
        return devices.size() > 0;
    }

    /**
     * Get all devices.
     *
     * @param params
     * @return
     */
    public static List<String> getSimulatorsBy(String... params) {
        String paramsList = "";
        for (String param : params) {
            paramsList += String.format(" | grep  '%s'", param);
        }

        String command = String.format(xcrunListDevicesWithParamsFormat, paramsList);
        String output = OSUtils.runProcess(command);

        if (output.isEmpty()) {
            return new ArrayList<>();
        }

        List<String> lines = Arrays.asList(output.split(System.lineSeparator()));
        lines.removeIf(l -> l.isEmpty());
        List<String> results = new ArrayList<>();
        lines.forEach(l -> results.add(l.trim()));

        return results;
    }

    /**
     * Get all simulators by name.
     *
     * @param name
     * @return
     */
    public static List<String> getAvailableSimulatorUdidsByName(String name) {
        List<String> lines = Simctl.getSimulatorsBy(name);
        ArrayList<String> listOfDevices = new ArrayList<>();
        lines.forEach(l -> {
            if (!l.contains("unavailable") && !l.isEmpty()) {
                String udid = l.substring(l.indexOf('(') + 1, l.indexOf(')')).trim();
                listOfDevices.add(udid);
            }
        });

        return listOfDevices;
    }

    public String ensureOnlyOneSimulatorExist() {
        List<String> simulators = Simctl.getSimulatorsBy(this.settings.deviceName);
        if (simulators.size() > 1) {
            LOGGER_BASE.error("Multiple simulators with name " + this.settings.deviceName + " found. Deleting them ...");
            Simctl.deleteSimulator(this.settings.deviceName);
            return this.createSimulator(this.settings.deviceName, this.settings.ios.simulatorType, this.settings.platformVersion.toString());
        } else if (simulators.size() == 0) {
            LOGGER_BASE.error("No simulators found with name " + this.settings.deviceName);
            return this.createSimulator(this.settings.deviceName, this.settings.ios.simulatorType, this.settings.platformVersion.toString());
        } else {
            LOGGER_BASE.error(String.format("Ensured only single simulator %s with uidid: %s found!", this.settings.deviceName, this.settings.deviceId));
            return this.settings.deviceId;
        }
    }

    /**
     * Creates simulator by name, type, iOS version.
     *
     * @param simulatorName
     * @param deviceType
     * @param iOSVersion
     * @return
     */
    public String createSimulator(String simulatorName, String deviceType, String iOSVersion) {
        LOGGER_BASE.info("Create simulator with following command:");
//        String command = "xcrun simctl create \"" + simulatorName +
//                "\" \"com.apple.CoreSimulator.SimDeviceType." + deviceType.trim().replace(" ", "-") +
//                "\" \"com.apple.CoreSimulator.SimRuntime.iOS-" + iOSVersion.trim().replace(".", "-") + "\"";


        String deviceTypeNameSpace = deviceType.trim().replace(" ", "-");
        String iOSVersionParsed = iOSVersion.trim().replace(".", "-");
        String createSimulatorCommand = String.format(
                "xcrun simctl create '%s' 'com.apple.CoreSimulator.SimDeviceType.%s' 'com.apple.CoreSimulator.SimRuntime.iOS-%s'",
                simulatorName,
                deviceTypeNameSpace,
                iOSVersionParsed);

        LOGGER_BASE.info(createSimulatorCommand);
        String output = OSUtils.runProcess(createSimulatorCommand);

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
                this.settings.deviceId = udid.trim();
                Simctl.LOGGER_BASE.info("Simulator created with UDID: " + this.settings.deviceId);
            }
        }

        return output.trim();
    }

    /**
     * Reset simulator default settings.
     */
    protected void resetSimulatorSettings() {
        if (this.settings.debug) {
            LOGGER_BASE.info("[Debug mode] Do not reset sim settings.");
        } else {
            String path = this.settings.screenshotResDir + File.separator + this.settings.testAppName;
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
        String uninstallCommand = String.format(xcrunFormat, "uninstall booted " + this.settings.packageId);
        String installCommand = String.format(xcrunFormat, "install booted " + Settings.BASE_TEST_APP_DIR + File.separator + this.settings.testAppFileName);
        OSUtils.runProcess(uninstallCommand);
        Wait.sleep(250);
        OSUtils.runProcess(installCommand);
        Wait.sleep(250);
        LOGGER_BASE.info(this.settings.packageId + " re installed.");
    }

}
