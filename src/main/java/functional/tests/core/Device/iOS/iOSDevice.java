package functional.tests.core.Device.iOS;

import functional.tests.core.Appium.Client;
import functional.tests.core.Device.IDevice;
import functional.tests.core.Device.IDeviceController;
import functional.tests.core.Enums.DeviceType;
import functional.tests.core.Exceptions.AppiumException;
import functional.tests.core.Exceptions.DeviceException;
import functional.tests.core.Log.Log;
import functional.tests.core.OSUtils.FileSystem;
import functional.tests.core.OSUtils.OSUtils;
import functional.tests.core.Settings.Settings;
import org.openqa.selenium.logging.LogEntry;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class iOSDevice implements IDevice {
    private IDeviceController _deviceController;
    public static final String simulatorLogPath = Settings.baseLogDir + File.separator + "simulator.log";
    private static String simulatorGuid = null;

    public iOSDevice() {
        //this._deviceController = new Simctl();
    }

    @Override
    public IDeviceController getDeviceController() {
        return this._deviceController;
    }

    @Override
    public void installApp(String appName) {
        String appPath = Settings.baseTestAppDir + File.separator + appName;
        String result = OSUtils.runProcess("ideviceinstaller -u " + Settings.deviceId + " -i " + appPath);
        if (result.contains("Complete")) {
            Log.info(appName + " successfully installed.");
        } else {
            Log.error("Failed to install " + appName + ". Error: " + result);
        }
    }

    @Override
    public void initDevice() throws DeviceException {
        if (Settings.deviceType == DeviceType.Simulator) {

            List<String> simulatorGuilds = Simctl.getSimulatorsIdsByName(Settings.deviceName);

            if (simulatorGuilds.size() == 1) {
                Log.info("Simulator " + Settings.deviceName + " exists.");
                Settings.deviceId = simulatorGuilds.get(0);
            } else {
                if (simulatorGuilds.size() > 1) {
                    Log.error("Multiple simulators with name " + Settings.deviceName + " found. Delete...");
                    Simctl.deleteSimulator(Settings.deviceName);
                } else {
                    // Create simulator specified by settings
                    String result = Simctl.createSimulator(Settings.deviceName, Settings.simulatorType, Settings.platformVersion);
                    if (result.toLowerCase().contains("error") || result.toLowerCase().contains("invalid")) {
                        Log.fatal("Failed to create simulator. Error: " + result);
                        throw new DeviceException("Failed to create simulator. Error: " + result);
                    } else {
                        String guid = result;
                        String[] rowList = result.split("\\r?\\n");
                        for (String rowLine : rowList) {
                            if (rowLine.contains("-")) {
                                guid = rowLine.trim();
                            }
                        }
                        simulatorGuid = guid;
                        Settings.deviceId = simulatorGuid;
                        Log.info("Simulator created: " + simulatorGuid);
                    }

                    // Verify created
                    if (Simctl.getSimulatorsIdsByName(Settings.deviceName).size() < 1) {
                        String error = "Simulator " + Settings.deviceName + " does not exist. Hint: verify SDKs available.";
                        Log.error(error);
                        throw new DeviceException(error);
                    }
                }
            }
        } else if (Settings.deviceType == DeviceType.iOS) {
            String devices = OSUtils.runProcess("instruments -s");
            if (devices.contains(Settings.deviceId)) {
                Log.info("Device " + Settings.deviceId + " found.");

                // Try to list all apps to verify if device works properly
                String fileContent = OSUtils.runProcess(Settings.defaultTimeout, "ideviceinstaller -u " + Settings.deviceId + " -l");
                Log.info("apps: " + fileContent);

                if (fileContent.contains("Total:") || fileContent.contains("CFBundleIdentifier")) {
                    Log.info("Device " + Settings.deviceId + " responds.");
                } else {
                    String error = "Device " + Settings.deviceId + " does not respond.";
                    Log.error(error);
                    throw new DeviceException(error);
                }
            } else {
                String error = "Device " + Settings.deviceId + " not found.";
                Log.error(error);
                throw new DeviceException(error);
            }
        }
    }

    @Override
    public void stopDevice() {
        if (Settings.deviceType == DeviceType.Simulator) {
            OSUtils.runProcess("killall \"iOS Simulator\"");
            OSUtils.runProcess("killall Simulator");
            Log.info("iOS Simulator killed.");
        }
    }

    @Override
    public void stopApps(List<String> uninstallAppsList) {
        Log.warn("iOSDEvice.stopApps method is not implemented");
    }

    @Override
    public void uninstallApps(List<String> uninstallAppsList) {
        if (Settings.isRealDevice) {
            List<String> installedApps = getInstalledApps();

            for (String appToUninstall : uninstallAppsList) {
                for (String appId : installedApps) {
                    if (appId.contains(appToUninstall)) {
                        uninstallApp(appId);
                    }
                }
            }

            // iOS7 devices use old appium version
            // In older appium version installation of test app is not stable
            // installApp method will deploy with ideviceinstaller
            if (Settings.platformVersion.contains("7")) {
                this.installApp(Settings.testAppName);
            }
        } else {
            String command = "xcrun simctl erase " + Settings.deviceId;
            Log.info(command);
            OSUtils.runProcess(command);
        }

        Log.info("Old apps uninstalled.");
    }

    @Override
    public String getContent(String testName) throws IOException {
        String logContent = FileSystem.readFile(Settings.consoleLogDir + File.separator + "syslog_" + testName + ".log");

        return logContent;
    }

    @Override
    public void writeConsoleLogToFile(String fileName) throws IOException {
        try {
            List<LogEntry> logEntries = Client.driver.manage().logs().get("syslog").getAll();
            if (logEntries.size() >= 1) {
                String logLocation = Settings.consoleLogDir + File.separator + "syslog_" + fileName + ".log";
                FileWriter writer = new FileWriter(logLocation, true);
                for (LogEntry log : logEntries) {
                    writer.write(log.toString());
                    writer.write(System.lineSeparator());
                }
                writer.close();
            }
        } catch (Exception e) {
            Log.warn("Failed to get syslog.");
            e.printStackTrace();
        }

        try {
            List<LogEntry> logEntries = Client.driver.manage().logs().get("crashlog").getAll();
            if (logEntries.size() >= 1) {
                String logLocation = Settings.consoleLogDir + File.separator + "crashlog_" + fileName + ".log";
                FileWriter writer = new FileWriter(logLocation, true);
                for (LogEntry log : logEntries) {
                    writer.write(log.toString());
                    writer.write(System.lineSeparator());
                }
                writer.close();
            }
        } catch (Exception e) {
            Log.warn("Failed to get crashlog.");
            e.printStackTrace();
        }
    }

    @Override
    public void verifyAppRunning(String deviceId, String appId) throws AppiumException, IOException {
        Log.warn("iOSDEvice.verifyAppRunning method is not implemented");
    }

    @Override
    public void pushFile(String deviceId, String localPath, String remotePath) throws Exception {
        Log.warn("iOSDEvice.pushFile method is not implemented");
    }

    @Override
    public void pullFile(String deviceId, String remotePath, String destinationFolder) throws Exception {
        Log.warn("iOSDEvice.pullFile method is not implemented");
    }

    @Override
    public void pullFile(String deviceId, String remotePath) throws Exception {
        Log.warn("iOSDEvice.pullFile method is not implemented");
    }

    @Override
    public void cleanConsoleLog() {
        try {

            Client.driver.manage().logs().get("syslog");
            Client.driver.manage().logs().get("crashlog");
        } catch (Exception e) {
            Log.warn("Failed to cleanup logs.");
        }
    }

    @Override
    public boolean isAppRunning(String deviceId, String appId) {
        Log.warn("iOSDEvice.isAppRunning method is not implemented");
        return true;
    }

    @Override
    public String getStartupTime(String appId) throws IOException {
        return "";
    }

    private static void copySimulatorSystemLog() {
        if (Settings.deviceType == DeviceType.Simulator) {
            if (Settings.deviceId != null) {
                String command = "cp -f ~/Library/Logs/CoreSimulator/" + Settings.deviceId + "/system.log " + iOSDevice.simulatorLogPath;
                Log.info(command);
                OSUtils.runProcess(command);
            } else {
                Log.error("simulatorGuid is null. Debug mode might be enabled.");
            }
        }
    }

    private static void uninstallApp(String appId) {
        String uninstallResult = OSUtils.runProcess("ideviceinstaller -u " + Settings.deviceId + " -U " + appId);
        if (uninstallResult.contains("Complete")) {
            Log.info(appId + " successfully uninstalled.");
        } else {
            Log.error("Failed to uninstall " + appId + ". Error: " + uninstallResult);
        }
    }

    private static List<String> getInstalledApps() {
        String rowData = OSUtils.runProcess("ideviceinstaller -u " + Settings.deviceId + " -l");
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
}
