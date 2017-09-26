package functional.tests.core.mobile.device.ios;

import functional.tests.core.enums.DeviceType;
import functional.tests.core.enums.EmulatorState;
import functional.tests.core.exceptions.DeviceException;
import functional.tests.core.exceptions.MobileAppException;
import functional.tests.core.extensions.SystemExtension;
import functional.tests.core.log.LoggerBase;
import functional.tests.core.mobile.appium.Client;
import functional.tests.core.mobile.device.EmulatorInfo;
import functional.tests.core.mobile.device.IDevice;
import functional.tests.core.mobile.find.Wait;
import functional.tests.core.mobile.settings.MobileSettings;
import functional.tests.core.utils.FileSystem;
import functional.tests.core.utils.OSUtils;
import io.appium.java_client.MobileElement;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.html5.Location;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * iOS Device implementation.
 */
public class IOSDevice implements IDevice {

    private static final LoggerBase LOGGER_BASE = LoggerBase.getLogger("IOSDevice");
    public IOSDeviceLog iosDeviceLog;
    public String simulatorLogPath;
    private Client client;
    private Simctl simctl;
    private MobileSettings settings;
    private String name;
    private DeviceType type;

    /**
     * Init IOS device.
     *
     * @param client   Appium client.
     * @param settings MobileSettings.
     */
    public IOSDevice(Client client, MobileSettings settings) {
        this.client = client;
        this.settings = settings;
        this.simulatorLogPath = this.settings.baseLogDir + File.separator + "simulator.log";
        this.name = this.settings.deviceName;
        this.type = this.settings.deviceType;

        if (this.settings.deviceType == DeviceType.Simulator) {
            this.simctl = new Simctl(this.settings);
        }

        this.iosDeviceLog = new IOSDeviceLog(this.getId(), this.settings);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public DeviceType getType() {
        return this.type;
    }

    @Override
    public String getId() {
        return this.settings.deviceId;
    }

    @Override
    public IDevice start() throws DeviceException {
        // Start simulator
        if (this.getType() == DeviceType.Simulator) {
            this.startSimulator();
        }

        // Ensure device is available
        if (this.getType() == DeviceType.iOS) {
            this.startRealDevice();
        }

        // Uninstall test apps
        if (!this.settings.debug) {
            this.uninstallApp(this.settings.packageId);
        }

        // Start Appium Client
        try {
            this.client.initDriver();
        } catch (Exception e) {
            SystemExtension.interruptProcess("Check logs for more information");
        }

        return this;
    }

    @Override
    public void installApp(String appName, String packageId) {
        if (this.settings.isRealDevice) {
            String appPath = this.settings.BASE_TEST_APP_DIR + File.separator + appName;
            String result = OSUtils.runProcess("ideviceinstaller -u " + this.getId() + " -i " + appPath);
            if (result.contains("Complete")) {
                IOSDevice.LOGGER_BASE.info(appName + " successfully installed.");
            } else {
                IOSDevice.LOGGER_BASE.error("Failed to install " + appName + ". Error: " + result);
            }
        } else {
            this.simctl.installApp();
        }
    }

    @Override
    public void startApplication(String packageId) {
        IOSDevice.LOGGER_BASE.warn("iOSDevice.startApplication method is not implemented");
    }

    @Override
    public void stop() throws DeviceException {
        if (this.getType() == DeviceType.Simulator) {
            this.simctl.markUnused(this.settings.deviceId);
        }
    }

    /**
     * Uninstall apps in uninstallAppsList.
     *
     * @param uninstallAppsList List of prefixes (example: org.nativescript.)
     */
    @Override
    public void uninstallApps(List<String> uninstallAppsList) throws DeviceException {
        IOSDevice.LOGGER_BASE.info("Uninstalling apps...");
        List<String> installedApps = this.getInstalledApps();

        for (String appToUninstall : uninstallAppsList) {
            for (String appId : installedApps) {
                if (appId.contains(appToUninstall)) {
                    this.uninstallApp(appId);
                }
            }
        }
    }

    @Override
    public String getContent(String testName) throws IOException {
        return FileSystem.readFile(this.settings.consoleLogDir + File.separator + "syslog_" + testName + ".log");
    }

    @Override
    public void writeConsoleLogToFile(String fileName) {
        try {
            String log = this.iosDeviceLog.getDeviceLogTail();
            String logLocation = this.settings.consoleLogDir + File.separator + "syslog_" + fileName + ".log";

            FileWriter writer = new FileWriter(logLocation, true);
            writer.write(log);
            writer.write(System.lineSeparator());

            writer.close();
        } catch (Exception e) {
            IOSDevice.LOGGER_BASE.warn("Failed to get log.");
            e.printStackTrace();
        }
    }

    @Override
    public void verifyAppRunning(String packageId) throws MobileAppException {
        IOSDevice.LOGGER_BASE.debug("iOSDevice.verifyAppRunning method is not implemented");
    }

    @Override
    public void pushFile(String localPath, String remotePath) throws Exception {
        IOSDevice.LOGGER_BASE.warn("iOSDevice.pushFile method is not implemented");
    }

    @Override
    public void pullFile(String remotePath, String destinationFolder) throws Exception {
        IOSDevice.LOGGER_BASE.warn("iOSDevice.pullFile method is not implemented");
    }

    @Override
    public void cleanConsoleLog() {
        try {

            this.client.driver.manage().logs().get("syslog");
            this.client.driver.manage().logs().get("crashlog");
        } catch (Exception e) {
            IOSDevice.LOGGER_BASE.warn("Failed to cleanup logs.");
        }
    }

    @Override
    public boolean isAppRunning(String packageId) {
        IOSDevice.LOGGER_BASE.debug("iOSDevice.isAppRunning method is not implemented");
        return true;
    }

    @Override
    public String getStartupTime(String packageId) {
        IOSDevice.LOGGER_BASE.debug("iOSDevice.getStartupTime method is not implemented");
        return null;
    }

    @Override
    public int getMemUsage(String packageId) {
        IOSDevice.LOGGER_BASE.debug("iOSDevice.getMemUsage method is not implemented");
        return -1;
    }

    @Override
    public void logAppStartupTime(String packageId) {
        IOSDevice.LOGGER_BASE.debug("iOSDevice.logAppStartupTime method is not implemented");
    }

    @Override
    public void logPerfInfo() throws IOException {
        String appPath = this.settings.BASE_TEST_APP_DIR + File.separator + this.settings.testAppFileName;
        String appSize = String.valueOf(FileSystem.getFileSize(appPath));

        LOGGER_BASE.info("Application size: " + appSize);
        StringBuilder sb = new StringBuilder();
        sb.append("AppSize");
        sb.append('\n');
        sb.append(appSize);
        sb.append('\n');
        String perfInfoLog = sb.toString();
        String filePath = this.settings.baseLogDir + File.separator + "perfInfo.csv";
        FileSystem.writeFile(filePath, perfInfoLog);
    }

    @Override
    public void setLocation(Location location) {
        ((IOSDriver) this.client.driver).setLocation(location);
    }

    @Override
    public void runAppInBackGround(int seconds) {
        if (this.settings.platformVersion >= 10) {
            this.client.getDriver().runAppInBackground(seconds);
        } else {
            try {
                JavascriptExecutor jse = (JavascriptExecutor) this.client.getDriver();
                jse.executeScript("var x = target.deactivateAppForDuration(" + String.valueOf(seconds) + "); " +
                        "var MAX_RETRY=5, retry_count = 0; while (!x && retry_count < MAX_RETRY) " +
                        "{ x = target.deactivateAppForDuration(2); retry_count += 1}; x");
            } catch (WebDriverException e) {
                if (e.getMessage().contains("An error occurred while executing user supplied JavaScript")) {
                    this.client.getDriver().findElement(By.id(this.settings.testAppFriendlyName)).click();
                } else {
                    // This hack workarounds run in background issue on iOS9
                    By appLocator = By.xpath("//UIAScrollView[@name='AppSwitcherScrollView']/UIAElement");

                    MobileElement element = (MobileElement) this.client.getDriver().findElement(appLocator);
                    int offset = 5; // 5px offset within the top-left corner of element
                    Point elementTopLeft = element.getLocation();
                    this.client.getDriver().tap(1, elementTopLeft.x + offset, elementTopLeft.y + offset, 500);
                }
            }
        }
    }

    @Override
    public void restartApp() {
        this.client.driver.resetApp();
    }

    @Override
    public void closeApp() {
        if (this.client.driver != null) {
            this.client.driver.closeApp();
        } else {
            LOGGER_BASE.error("Appium driver is dead. Can not close the app!");
        }
    }

    /**
     * Ensure iOS physical device is available.
     *
     * @throws DeviceException When device is not available.
     */
    private void startRealDevice() throws DeviceException {
        String commandGetAvailableDevices = "idevice_id --list";
        String devices = OSUtils.runProcess(commandGetAvailableDevices);
        if (!devices.contains(this.getId())) {
            if (this.settings.debug) {
                IOSDevice.LOGGER_BASE.error("device with " + this.getId() + " not found.");
                IOSDevice.LOGGER_BASE.info("Trying to find device!!! ");
                this.settings.deviceId = devices.substring(devices.lastIndexOf("(") + 1, devices.lastIndexOf(")")).trim();
            } else {
                String error = "device " + this.getId() + " not found.";
                IOSDevice.LOGGER_BASE.error(error);
                throw new DeviceException(error);
            }
        }

        IOSDevice.LOGGER_BASE.info("Found device with uidid: " + this.getId() + " .");

        if (this.settings.restartRealDevice) {
            OSUtils.runProcess(this.settings.defaultTimeout, "idevicediagnostics restart -u " + this.getId());
            IOSDevice.LOGGER_BASE.info("The device is restarting!");
            devices = "";
            Wait.sleep(20000);
            while (!devices.contains(this.getId())) {
                IOSDevice.LOGGER_BASE.info("Booting...");
                devices = OSUtils.runProcess(this.settings.defaultTimeout, commandGetAvailableDevices);
            }
            Wait.sleep(20000);

            IOSDevice.LOGGER_BASE.info("The device should be restarted!");
        }

        // Try to list all apps to verify if device works properly
        String fileContent = OSUtils.runProcess(this.settings.defaultTimeout, "ideviceinstaller -u " + this.getId() + " -l");
        IOSDevice.LOGGER_BASE.info("apps: " + fileContent);

        if (fileContent.contains("Total:") || fileContent.contains("CFBundleIdentifier")) {
            IOSDevice.LOGGER_BASE.info("device " + this.getId() + " responds.");
        } else {
            String error = "device " + this.getId() + " does not respond.";
            IOSDevice.LOGGER_BASE.error(error);
            throw new DeviceException(error);
        }
    }

    /**
     * Start iOS Simulator (or reuse available).
     *
     * @throws DeviceException When iOS Simulator can't start.
     */
    private void startSimulator() throws DeviceException {

        // Kill simulators used more than 2 hours
        this.simctl.stopUsedSimulators(120);

        String simId = this.simctl.getFreeSimulator(this.settings.deviceName);
        if (simId != null) {
            // If appropriate device is already running and free -> use it!
            LOGGER_BASE.info(this.settings.deviceName + " simulator with id " + simId + " is free!");
            this.settings.deviceId = simId;
            this.simctl.markUsed(simId);
        } else {

            LOGGER_BASE.info("Can not find free and booted simulator with name " + this.settings.deviceName);

            int maxSimCount = this.simctl.getMaxCountOfRunningSimulators();
            int free = this.simctl.getSimulatorsInfo(EmulatorState.Free).size();
            int used = this.simctl.getSimulatorsInfo(EmulatorState.Used).size();
            int currentSimCount = free + used;

            // If limit of maximum parallel simulators is reached kill free simulators.
            if (currentSimCount >= maxSimCount) {
                LOGGER_BASE.warn("Maximum number of running iOS Simulator limit exceeded.");
                List<EmulatorInfo> freeSimulators = this.simctl.getSimulatorsInfo(EmulatorState.Free);
                freeSimulators.forEach(sim -> this.simctl.stop(sim.id));
            }

            // Start desired simulator
            if (currentSimCount >= maxSimCount) {
                // If still running iOS Simulators are more than limit we can't help.
                throw new DeviceException("Maximum number of running iOS Simulator limit exceeded.");
            } else {
                // If desired iOS Simulator do not exists -> create it!
                String offlineSim = this.simctl.getOffineSimulator(this.settings.deviceName);
                if (offlineSim == null) {
                    offlineSim = this.simctl.create(this.settings.deviceName, this.settings.ios.simulatorType, String.valueOf(this.settings.platformVersion));
                }

                // Start iOS Simulator
                LOGGER_BASE.info("Another " + this.settings.deviceName + " found!");
                this.settings.deviceId = offlineSim;
                this.simctl.start(offlineSim, this.settings.deviceBootTimeout);
                this.simctl.markUsed(offlineSim);
            }
        }
    }

    /**
     * Uninstall application from device.
     *
     * @param appId Bundle identifier.
     * @throws DeviceException If uninstall operation fails.
     */
    private void uninstallApp(String appId) throws DeviceException {
        if (this.settings.isRealDevice) {
            String uninstallResult = OSUtils.runProcess("ideviceinstaller --udid " + this.settings.deviceId + " --uninstall " + appId);
            if (!uninstallResult.contains("Complete")) {
                IOSDevice.LOGGER_BASE.error(String.format("Failed to uninstall %s with ideviceinstaller tool. Error: %s", appId, uninstallResult));
                throw new DeviceException("Failed to uninstall " + appId + " from " + this.settings.deviceId);
            }
        } else {
            this.simctl.uninstallApp(appId);
        }
        IOSDevice.LOGGER_BASE.info(appId + " successfully uninstalled.");
    }

    /**
     * Get list of installed applications.
     *
     * @return List of bundle identifiers.
     */
    private List<String> getInstalledApps() {
        if (this.settings.isRealDevice) {
            List<String> list = new ArrayList<>();
            String rowData = OSUtils.runProcess("ideviceinstaller -u " + this.settings.deviceId + " -l");
            String trimData = rowData.replace("package:", "");
            String[] rowList = trimData.split("\\r?\\n");
            for (String item : rowList) {
                if (item.contains(".") && item.contains("-")) {
                    String rowAppId = item.replace(" ", "");
                    String appId = rowAppId.split("-")[0];
                    list.add(appId);
                }
            }
            return list;
        } else {
            return this.simctl.getInstalledApps(this.settings.deviceId);
        }
    }

    /**
     * Start watcher on iOS physical device logs.
     */
    public void startIOSRealDeviceLogWatcher() {
        try {
            String command = "/usr/local/bin/idevicesyslog -u "
                    + this.settings.deviceId
                    + " > "
                    + this.settings.consoleLogDir + File.separator + IOSDeviceLog.IOS_REAL_DEVICE_LOG_FILE;
            String[] commands = new String[]{command};
            String[] allCommand = OSUtils.concat(OSUtils.OS_LINUX_RUNTIME, commands);
            ProcessBuilder pb = new ProcessBuilder(allCommand);
            pb.start();

            IOSDevice.LOGGER_BASE.info("IOS device log is running!!!");
        } catch (Exception e) {
            e.printStackTrace();
            IOSDevice.LOGGER_BASE.error("IOS device log has stopped!!! Exception:" + e.getMessage());
        }
    }
}

