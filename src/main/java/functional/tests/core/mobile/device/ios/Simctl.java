package functional.tests.core.mobile.device.ios;

import functional.tests.core.enums.EmulatorState;
import functional.tests.core.exceptions.DeviceException;
import functional.tests.core.extensions.SystemExtension;
import functional.tests.core.log.LoggerBase;
import functional.tests.core.mobile.device.EmulatorInfo;
import functional.tests.core.mobile.find.Wait;
import functional.tests.core.mobile.settings.MobileSettings;
import functional.tests.core.settings.Settings;
import functional.tests.core.utils.OSUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

/**
 * Simctl wrapper.
 */
public class Simctl {

    private MobileSettings settings;
    private static final LoggerBase LOGGER_BASE = LoggerBase.getLogger("Simctl");
    private static final String SIM_ROOT = System.getProperty("user.home") + "/Library/Developer/CoreSimulator/Devices/";

    /**
     * Initialize Simctl.
     *
     * @param settings Settings object.
     */
    public Simctl(MobileSettings settings) {
        this.settings = settings;
    }

    /**
     * Creates simulator by name, type, iOS version.
     *
     * @param simulatorName Simulator display name.
     * @param deviceType    Device type, for example "iPhone 6" (How you see them in Xcode).
     * @param iOSVersion    iOS version, for example 10.0 (How you see them in Xcode).
     * @return UDID of created iOS Simulator.
     */
    public String create(String simulatorName, String deviceType, String iOSVersion) throws DeviceException {
        String deviceTypeNameSpace = deviceType.trim().replace(" ", "-");
        if (deviceTypeNameSpace.toLowerCase().contains("x")) {
            deviceTypeNameSpace = deviceTypeNameSpace.replace("-" + iOSVersion, "");
        }
        String iOSVersionParsed = iOSVersion.trim().replace(".", "-");
        String createSimulatorCommand = String.format(
                "xcrun simctl create '%s' 'com.apple.CoreSimulator.SimDeviceType.%s' 'com.apple.CoreSimulator.SimRuntime.iOS-%s'",
                simulatorName,
                deviceTypeNameSpace,
                iOSVersionParsed);

        LOGGER_BASE.info("Create simulator with following command:");
        LOGGER_BASE.info(createSimulatorCommand);
        String output = OSUtils.runProcess(createSimulatorCommand);
        String udid = "";
        String[] list = output.split("\\r?\\n");
        for (String line : list) {
            if (line.contains("-")) {
                udid = line.trim();
            }
            this.settings.deviceId = udid.trim();
            LOGGER_BASE.info("Simulator created with UDID: " + this.settings.deviceId);
        }

        // Make sure iOS Simulator is created.
        if (!udid.contains("-")) {
            SystemExtension.interruptProcess("Failed to create desired iOS Simulator!");
        }

        return udid;
    }

    /**
     * Check if iOS Simulator is running.
     *
     * @param simId iOS Simulator identifier.
     * @return True if iOS Simulator is running.
     */
    Boolean isRunning(String simId) {
        String out = this.runSimctlCommand("spawn", simId, "launchctl print system | grep com.apple.springboard.services");
        return out.contains("M   A   com.apple.springboard.services");
    }

    /**
     * Start iOS Simulator.
     *
     * @param simId   iOS Simulator identifier.
     * @param timeout Timeout for iOS Simulator to boot.
     */
    public void start(String simId, int timeout) throws DeviceException {

        // Start it...
        LOGGER_BASE.info("Start iOS Simulator: " + simId);
        try {
            OSUtils.executeCommand("xcrun simctl erase " + this.settings.deviceId, this.settings.deviceBootTimeout);
            OSUtils.executeCommand("xcrun simctl boot " + this.settings.deviceId, this.settings.deviceBootTimeout);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }

        // Wait until it boot...
        for (long stop = System.nanoTime() + TimeUnit.SECONDS.toNanos(timeout); stop > System.nanoTime(); ) {
            if (this.isRunning(simId)) {
                LOGGER_BASE.info(simId + " booted!");
                break;
            } else {
                LOGGER_BASE.info(simId + " still booting...");
                Wait.sleep(this.settings.shortTimeout);
            }
        }

        // Trow exception in case it failed to start
        if (!this.isRunning(simId)) {
            throw new DeviceException("Failed to boot simulator: " + simId);
        }
    }

    /**
     * Stop iOS Simulator.
     *
     * @param simId iOS Simulator identifier.
     */
    public void stop(String simId) {
        Simctl.LOGGER_BASE.info("Stop iOS Simulator: " + simId);
        this.runSimctlCommand("shutdown", simId, "");
    }


    /**
     * Stop simulators used for more than X minutes.
     *
     * @param minutes Minutes.
     */
    public void stopUsedSimulators(int minutes) {
        List<EmulatorInfo> usedSimulators = this.getSimulatorsInfo(EmulatorState.Used);
        usedSimulators.forEach((sim) -> {
            if (sim.usedFrom > minutes * 60 * 1000) {
                LOGGER_BASE.warn(String.format("Simulator used more than %s minutes detected!", String.valueOf(minutes)));

                // Stop all simulators via xcrun simctl
                this.stop(sim.id);
                Wait.sleep(1000);

                // Kill all the processes related with sim.id (for example WDA agents).
                String killCommand = "ps aux | grep -ie " + sim.id + " | awk '{print $2}' | xargs kill -9";
                OSUtils.runProcess(killCommand);
            }
        });
    }

    /**
     * Delete all the settings and data from iOS Simulator.
     *
     * @param simId iOS Simulator identifier.
     * @throws DeviceException When iOS Simulator is booted (this operation can be performed only if it is shutdown).
     */
    public void erase(String simId) throws DeviceException {
        if (this.isRunning(simId)) {
            throw new DeviceException("Can not erase simulator while it is running!");
        } else {
            Simctl.LOGGER_BASE.info("Erase iOS Simulator: " + simId);
            this.runSimctlCommand("erase", this.settings.deviceId, "");
        }
    }

    /**
     * Get location of iOS Simulator.
     *
     * @param simId iOS Simulator identifier.
     * @return Location to root folder of the simulator.
     */
    public String getSimLocation(String simId) {
        return SIM_ROOT + simId;
    }

    public List<String> getInstalledApps(String simId) {
        List<String> list = new ArrayList<>();
        String rowData = OSUtils.runProcess("find " + this.getSimLocation(simId) + "/data/Containers/Bundle/Application -type d -name *.app");
        String[] rowList = rowData.split("\\r?\\n");
        for (String item : rowList) {
            String rowBundle = OSUtils.runProcess("defaults read " + item + "/Info.plist | grep CFBundleIdentifier");
            try {
                String appId = rowBundle.split("\"")[1];
                list.add(appId);
            } catch (Exception e) {
                LOGGER_BASE.error("Failed to get installed apps!");
                LOGGER_BASE.error("Row bundle data:");
                LOGGER_BASE.error(rowBundle);
            }
        }
        return list;
    }

    /**
     * Install application under test.
     */
    public void installApp() {
        this.installApp(this.settings.testAppFileName);
    }

    /**
     * Install application under test.
     */
    public void installApp(String appName) {
        String app = Settings.BASE_TEST_APP_DIR + File.separator + appName;
        this.runSimctlCommand("install", this.settings.deviceId, app);
        Wait.sleep(250);
        LOGGER_BASE.info(this.settings.packageId + " installed.");
    }

    /**
     * Uninstall application under test.
     */
    public void uninstallApp() {
        this.uninstallApp(this.settings.packageId);
    }

    /**
     * Uninstall application.
     */
    public void uninstallApp(String bundleId) {
        this.runSimctlCommand("uninstall", this.settings.deviceId, bundleId);
        Wait.sleep(250);
    }

    /**
     * Return time since iOS Simulator is in use.
     *
     * @param simId iOS Simulator identifier.
     * @return Time since iOS Simulator is in use in milliseconds (0 if iOS Simulator is not in used).
     */
    long usedSince(String simId) {
        File temp = new File(this.getSimLocation(simId) + "/data/tmp/used.tmp");
        if (temp.exists()) {
            long now = new Date().getTime();
            long lastModified = temp.lastModified();
            long usedFrom = now - lastModified;
            LOGGER_BASE.debug(simId + " is in use from " + String.valueOf(TimeUnit.MILLISECONDS.toSeconds(usedFrom)) + " seconds.");
            return usedFrom;
        } else {
            LOGGER_BASE.debug(simId + " is not used!");
            return 0;
        }
    }

    /**
     * Mark iOS Simulator as used.
     *
     * @param simId iOS Simulator identifier.
     */
    void markUsed(String simId) {
        LOGGER_BASE.info("Mark iOS Simulator as used: " + simId);
        String command = "touch " + this.getSimLocation(simId) + "/data/tmp/used.tmp";
        OSUtils.runProcess(command);
    }

    /**
     * Mark iOS Simulator as unused.
     *
     * @param simId iOS Simulator identifier.
     */
    void markUnused(String simId) {
        LOGGER_BASE.info("Mark iOS Simulator as unused: " + simId);
        String command = "rm -rf " + this.getSimLocation(simId) + "/data/tmp/used.tmp";
        OSUtils.runProcess(command);
    }

    /**
     * Get iOS Simulators info - id, name, state.
     *
     * @param state EmulatorState filter.
     * @return List of EmulatorInfo objects.
     */
    List<EmulatorInfo> getSimulatorsInfo(EmulatorState state) {
        List<EmulatorInfo> simulators = this.getSimulatorsInfo();
        return simulators.stream().filter(p -> p.state == state).collect(Collectors.toList());
    }

    /**
     * Get free iOS Simulator.
     *
     * @return Identifier of free iOS Simulator.
     */
    String getFreeSimulator(String name) {
        List<EmulatorInfo> simList = this.getSimulatorsInfo();
        simList = simList.stream().filter(p -> p.name.equals(name)).collect(Collectors.toList());
        if (this.settings.debug) {
            LOGGER_BASE.info("[DEBUG MODE] All iOS Simulators matching desired name will be free.");
        } else {
            simList = simList.stream().filter(p -> p.usedFrom == 0).collect(Collectors.toList());
        }
        if (simList.size() > 0) {
            return simList.get(0).id;
        } else {
            return null;
        }
    }

    /**
     * Get free iOS Simulator.
     *
     * @return Identifier of free iOS Simulator.
     */
    String getOffineSimulator(String name) {
        List<EmulatorInfo> allSim = this.getSimulatorsInfo();
        List<EmulatorInfo> sameNameSims = allSim.stream().filter(p -> p.name.equals(name)).collect(Collectors.toList());
        List<EmulatorInfo> freeSim = sameNameSims.stream().filter(p -> p.state == EmulatorState.Shutdown).collect(Collectors.toList());
        if (freeSim.size() > 0) {
            return freeSim.get(0).id;
        } else {
            return null;
        }
    }

    /**
     * Get iOS Simulators info - id, name, state.
     *
     * @return List of EmulatorInfo objects.
     */
    private List<EmulatorInfo> getSimulatorsInfo() {
        List<EmulatorInfo> list = new ArrayList<>();
        String rowData = this.runSimctlCommand("list", "devices", "");
        String[] rowList = rowData.split("\\r?\\n");
        for (String item : rowList) {
            if (item.contains("(") && item.contains(")")) {
                String name = item.split("\\(")[0].trim();
                String id = item.split("\\(")[1].split("\\)")[0].trim();
                String rowState = item.split("\\(")[2].split("\\)")[0].trim();
                EmulatorState state = EmulatorState.Shutdown;
                long usedFrom = -1;
                if (rowState.equalsIgnoreCase("Booted")) {
                    usedFrom = this.usedSince(id);
                    if (usedFrom == 0) {
                        state = EmulatorState.Free;
                    } else {
                        state = EmulatorState.Used;
                    }
                }
                EmulatorInfo info = new EmulatorInfo(id, name, state, usedFrom);
                list.add(info);
            }
        }
        return list;
    }

    /**
     * Run simctl command.
     *
     * @param command   Command.
     * @param simId     iOS Simulator identifier.
     * @param arguments Arguments.
     * @return Result as String.
     */
    private String runSimctlCommand(String command, String simId, String arguments) {
        String finalCommand = String.format("xcrun simctl %s %s %s", command, simId, arguments);
        return OSUtils.runProcess(60, finalCommand);
    }
}
