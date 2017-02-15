package functional.tests.core.device.android;

import functional.tests.core.enums.OSType;
import functional.tests.core.exceptions.DeviceException;
import functional.tests.core.find.Wait;
import functional.tests.core.log.LoggerBase;
import functional.tests.core.settings.Settings;
import functional.tests.core.utils.FileSystem;
import functional.tests.core.utils.OSUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * TODO(dtopuzov): Add docs.
 */
public class Adb {

    private static final String ADB_PATH = System.getenv("ANDROID_HOME") + File.separator + "platform-tools" + File.separator + "adb";
    private static final String ANDROID_PATH = System.getenv("ANDROID_HOME") + File.separator + "tools" + File.separator + "android";
    private static final String EMULATOR_PATH = System.getenv("ANDROID_HOME") + File.separator + "tools" + File.separator + "emulator";
    private static final LoggerBase LOGGER_BASE = LoggerBase.getLogger("Adb");
    private String emulatorStartLogPath;
    private Settings settings;

    /**
     * TODO(dtopuzov): Add docs.
     *
     * @param settings
     */
    public Adb(Settings settings) {
        this.settings = settings;
        this.emulatorStartLogPath = this.settings.baseLogDir + File.separator + "emulator.log";
    }

    /**
     * TODO(dtopuzov): Add docs.
     *
     * @param command
     * @return
     */
    private String runAdbCommand(String command) {
        return this.runAdbCommand(null, command);
    }

    /**
     * TODO(dtopuzov): Add docs.
     *
     * @param deviceId
     * @param command
     * @return
     */
    public String runAdbCommand(String deviceId, String command) {
        return this.runAdbCommand(deviceId, command, this.settings.deviceBootTimeout, true);
    }

    /**
     * TODO(dtopuzov): Add docs.
     *
     * @return
     */
    protected List<String> getDevices() {
        String rowData = this.runAdbCommand("devices");
        String[] list = rowData.split("\\r?\\n");
        return Arrays.asList(list);
    }

    /**
     * TODO(dtopuzov): Add docs.
     */
    protected void startAdb() {
        LOGGER_BASE.info("Start adb");
        OSUtils.runProcess(ADB_PATH + " start-server");
    }

    /**
     * TODO(dtopuzov): Add docs.
     */
    protected void stopAdb() {
        LOGGER_BASE.info("Stop adb");
        OSUtils.runProcess(ADB_PATH + " kill-server");
    }

    /**
     * TODO(dtopuzov): Add docs.
     */
    protected void killAdbProcess() {
        if (this.settings.os == OSType.Windows) {
            OSUtils.stopProcess("adb.exe");
        } else {
            OSUtils.stopProcess("adb");
        }
    }

    /**
     * TODO(dtopuzov): Add docs.
     *
     * @param deviceId
     * @return
     */
    public String getAdbLog(String deviceId) {
        return this.runAdbCommand(this.settings.deviceId, "logcat -d");
    }

    /**
     * TODO(dtopuzov): Add docs.
     *
     * @param appId
     * @param activity
     */
    public void startApplication(String appId, String activity) {
        LOGGER_BASE.debug("Start " + appId + " with command:");
        String command = "shell am start -a android.intent.action.MAIN -n " + appId + "/" + activity;
        LOGGER_BASE.debug(command);
        this.runAdbCommand(this.settings.deviceId, command);
    }

    /**
     * TODO(dtopuzov): Add docs.
     *
     * @param appId
     */
    public void startApplication(String appId) {
        LOGGER_BASE.debug("Start " + appId + " with command:");
        String command = "shell monkey -p " + appId + " 1";
        LOGGER_BASE.debug(command);
        this.runAdbCommand(this.settings.deviceId, command);
    }

    /**
     * TODO(dtopuzov): Add docs.
     *
     * @param appId
     */
    public void stopApplication(String appId) {
        LOGGER_BASE.debug("Stop " + appId);
        String command = "shell am force-stop " + appId;
        this.runAdbCommand(this.settings.deviceId, command);
    }

    /**
     * TODO(dtopuzov): Add docs.
     *
     * @return
     */
    public List<String> getInstalledApps() {
        String rowData = this.runAdbCommand(this.settings.deviceId, "shell pm list packages -3");
        String trimData = rowData.replace("package:", "");
        String[] list = trimData.split("\\r?\\n");
        return Arrays.asList(list);
    }

    /**
     * TODO(dtopuzov): Add docs.
     *
     * @param appId
     */
    protected void stopApp(String appId) {
        String stopCommand = this.runAdbCommand(this.settings.deviceId, "shell am force-stop " + appId);
        OSUtils.runProcess(stopCommand);
    }

    /**
     * TODO(dtopuzov): Add docs.
     *
     * @param appId
     * @return
     */
    public boolean isAppInstalled(String appId) {
        List<String> installedApps = this.getInstalledApps();
        boolean appFound = false;
        for (String app : installedApps) {
            if (app.contains(appId)) {
                LOGGER_BASE.info("app " + appId + " found.");
                appFound = true;
            }
        }
        return appFound;
    }

    /**
     * TODO(dtopuzov): Add docs.
     *
     * @param testAppName
     * @param packageId
     * @throws IOException
     */
    public void installApp(String testAppName, String packageId) throws IOException {
        boolean appInstalled;
        appInstalled = this.isAppInstalled(packageId);
        if (appInstalled) {
            LOGGER_BASE.info("Uninstall a previous version " + packageId + " app.");
            this.uninstallApp(packageId);
        }

        String apkPath = this.settings.BASE_TEST_APP_DIR + File.separator + testAppName;
        LOGGER_BASE.info("Installing " + apkPath + " ...");
        String output = this.runAdbCommand(this.settings.deviceId, "install -r " + apkPath);
        LOGGER_BASE.info(output);

        appInstalled = this.isAppInstalled(packageId);
        if (!appInstalled) {
            LOGGER_BASE.error("Failed to install" + apkPath + "!");
            throw new IOException("Failed to install" + apkPath + "!");
        }
    }

    /**
     * TODO(dtopuzov): Add docs.
     *
     * @param appId
     */
    public void uninstallApp(String appId) {
        this.stopApp(appId);
        String uninstallResult = this.runAdbCommand(this.settings.deviceId, "shell pm uninstall -k " + appId);

        if (uninstallResult.contains("Success")) {
            LOGGER_BASE.info(appId + " successfully uninstalled.");
        } else {
            LOGGER_BASE.error("Failed to uninstall " + appId + ". Error: " + uninstallResult);
        }
    }

    /**
     * TODO(dtopuzov): Add docs.
     *
     * @param deviceId
     * @param appId
     * @return
     */
    public boolean isAppRunning(String deviceId, String appId) {
        String processes = this.runAdbCommand(deviceId, "shell ps");
        if (processes.contains(appId)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * TODO(dtopuzov): Add docs.
     * If emulator with same name exists, do nothing, else create emulator.
     *
     * @param avdName
     * @param options
     * @param force
     * @throws DeviceException
     */
    public void createEmulator(String avdName, String options, Boolean force) throws DeviceException {

        String avds = OSUtils.runProcess(ANDROID_PATH + " list avds");
        Boolean emulatorExists = false;
        if (avds.contains(avdName + ".avd")) {
            LOGGER_BASE.info(avdName + " already exists.");
            emulatorExists = true;
        }
        if (force || (!emulatorExists)) {
            // Create emulator
            String command;
            if (this.settings.os == OSType.Windows) {
                LOGGER_BASE.fatal("Create emulator not implemented for Windows systems.");
                throw new UnsupportedOperationException("Create emulator not implemented for Windows systems.");
            } else {
                command = "echo no | " + ANDROID_PATH + " -s create avd -n " + avdName + " " + options + " -f";
            }

            LOGGER_BASE.info("Create emulator with command: ");
            LOGGER_BASE.info(command);
            OSUtils.runProcess(command);

            // Verify it exists
            avds = OSUtils.runProcess(ANDROID_PATH + " list avds");
            if (avds.contains(avdName + ".avd")) {
                LOGGER_BASE.info(avdName + " created successfully.");
            } else {
                String error = "Emulator " + avdName + " is not available!";
                LOGGER_BASE.fatal(error);
                throw new DeviceException(error);
            }
        }
    }

    /**
     * TODO(dtopuzov): Add docs.
     * If emulator with same name exists, do nothing, else create emulator.
     *
     * @param avdName
     * @param options
     * @throws DeviceException
     */
    public void createEmulator(String avdName, String options) throws DeviceException {
        this.createEmulator(avdName, options, false);
    }

    /**
     * TODO(dtopuzov): Add docs.
     *
     * @param avdName
     * @param port
     */
    protected void startEmulator(String avdName, int port) {
        String command = EMULATOR_PATH + " -port " + port + " -avd " + avdName;
        if (this.settings.android.emulatorOptions != null) {
            command = command + " " + this.settings.android.emulatorOptions;
        }
        command = command + " > " + this.emulatorStartLogPath + " 2>&1";
        LOGGER_BASE.info("Starting emulator with command: " + command);
        OSUtils.runProcess(false, Integer.MAX_VALUE, command);
    }

    /**
     * TODO(dtopuzov): Add docs.
     */
    protected void stopAllEmulators() {
        if (this.settings.os == OSType.Windows) {
            OSUtils.stopProcess("emulator64-x86.exe");
            OSUtils.stopProcess("emulator-x86.exe");
            OSUtils.stopProcess("emulator-arm.exe");
            OSUtils.stopProcess("qemu-system-i386.exe");
            OSUtils.stopProcess("qemu-system-arm.exe");
            OSUtils.stopProcess("emulator64-crash-service.exe");
            OSUtils.stopProcess("emulator-crash-service.exe");
            OSUtils.stopProcess("emulator-check.exe");
            OSUtils.stopProcess("emulator64-arm.exe");
        } else {
            OSUtils.stopProcess("emulator64-x86");
            OSUtils.stopProcess("emulator-x86");
            OSUtils.stopProcess("emulator-arm");
            OSUtils.stopProcess("qemu-system-i386");
            OSUtils.stopProcess("qemu-system-arm");
            OSUtils.stopProcess("emulator64-crash-service");
            OSUtils.stopProcess("emulator-crash-service");
            OSUtils.stopProcess("emulator-check");

            // Linux (Ubuntu)
            // For some reason the 'qemu-system-i386' process appears as 'qemu-system-i38'.
            if (this.settings.os == OSType.Linux) {
                OSUtils.stopProcess("qemu-system-i38"); // qemu-system-i386
            }
        }
        LOGGER_BASE.info("Emulators killed.");
    }

    /**
     * TODO(dtopuzov): Add docs.
     *
     * @param deviceId
     * @return
     */
    protected boolean checkIfEmulatorIsRunning(String deviceId) {
        boolean hasBooted = false;

        String rowData = this.runAdbCommand(deviceId, "shell dumpsys activity");
        String[] list = rowData.split("\\r?\\n");

        for (String line : list) {

            if (line.contains("Recent #0")
                    && (line.contains("com.android.launcher")
                    || line.contains("com.google.android.googlequicksearchbox")
                    || line.contains("com.google.android.apps.nexuslauncher")
                    || line.contains(this.settings.packageId))) {
                hasBooted = true;
                break;
            }
        }

        if (hasBooted) {
            LOGGER_BASE.info("Emulator is up and running.");
            this.setScreenOffTimeOut(deviceId, 180000);
        } else {
            LOGGER_BASE.debug("Emulator is not running.");
        }

        return hasBooted;
    }

    /**
     * TODO(dtopuzov): Add docs.
     *
     * @param deviceId
     * @param timeOut
     * @throws TimeoutException
     */
    protected void waitUntilEmulatorBoot(String deviceId, int timeOut) throws TimeoutException {
        long startTime = new Date().getTime();
        long currentTime = new Date().getTime();
        boolean found = false;

        while ((currentTime - startTime) < timeOut * 1000) {
            currentTime = new Date().getTime();
            found = this.checkIfEmulatorIsRunning(deviceId);

            if (found) {
                break;
            } else {
                LOGGER_BASE.info("Booting emulator ...");
                Wait.sleep(3000);
            }
        }

        if (!found) {
            String error = deviceId + " failed to boot in " + String.valueOf(timeOut) + " seconds.";
            LOGGER_BASE.fatal(error);
            OSUtils.getScreenshot("HostOS_Failed_To_Boot_Emulator", this.settings);
            throw new TimeoutException(error);
        }
    }

    /**
     * TODO(dtopuzov): Add docs.
     *
     * @param deviceId
     * @param timeOut
     * @throws TimeoutException
     */
    protected void waitForDevice(String deviceId, int timeOut) throws TimeoutException {
        long startTime = new Date().getTime();
        long currentTime = new Date().getTime();
        boolean found = false;

        while ((currentTime - startTime) < timeOut * 1000) {
            currentTime = new Date().getTime();
            if (this.isAvailable(deviceId)) {
                LOGGER_BASE.info("device " + deviceId + " found.");
                found = true;
                break;
            } else {
                Wait.sleep(1000);
            }

            LOGGER_BASE.info("device " + deviceId + " not found. Wait...");
        }

        if (!found) {
            String error = "Failed to find device "
                    + deviceId + " in " + String.valueOf(timeOut)
                    + " seconds.";
            LOGGER_BASE.fatal(error);

            throw new TimeoutException(error);
        }
    }

    /**
     * TODO(dtopuzov): Add docs.
     *
     * @param deviceId
     * @return
     */
    protected boolean isAvailable(String deviceId) {
        boolean found = false;
        List<String> devices = this.getDevices();
        for (String device : devices) {
            if (device.contains(deviceId) && device.contains("device")) {
                found = true;
                break;
            }
        }
        return found;
    }

    /**
     * TODO(dtopuzov): Add docs.
     *
     * @param deviceId
     * @param timeOut
     */
    protected void setScreenOffTimeOut(String deviceId, int timeOut) {
        LOGGER_BASE.info("Set SCREEN_OFF_TIMEOUT to " + String.valueOf(timeOut));
        this.runAdbCommand(deviceId, "shell settings put system screen_off_timeout " + timeOut);
    }

    /**
     * TODO(dtopuzov): Add docs.
     *
     * @param deviceId
     * @return
     */
    public boolean isLocked(String deviceId) {
        String output = this.runAdbCommand(deviceId, "shell dumpsys window windows");
        if (output.contains("mDrawState=HAS_DRAWN mLastHidden=true")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * TODO(dtopuzov): Add docs.
     *
     * @param deviceId
     */
    public void unlock(String deviceId) {
        this.runAdbCommand(deviceId, "shell input keyevent 82");
    }

    /**
     * TODO(dtopuzov): Add docs.
     *
     * @param deviceId
     * @param localPath
     * @param remotePath
     * @throws Exception
     */
    public void pushFile(String deviceId, String localPath, String remotePath) throws Exception {

        this.runAdbCommand(this.settings.deviceId, "shell mount -o rw,remount -t rootfs /");

        // Verify remotePath
        String remoteBasePath = remotePath.substring(0, remotePath.lastIndexOf("/"));
        String sdcardFiles = this.runAdbCommand(this.settings.deviceId, "shell ls -la " + remoteBasePath);
        if (sdcardFiles.contains("No such file or directory")) {
            String error = remoteBasePath + " does not exist.";
            LOGGER_BASE.error(error);
            throw new Exception(error);
        }

        // Verify localPath
        localPath = localPath.replace("/", File.separator);
        localPath = localPath.replace("\\", File.separator);
        String localFilePath = this.settings.BASE_TEST_DATA_DIR + File.separator + localPath;
        if (!FileSystem.exist(localFilePath)) {
            String error = localPath + " does not exist.";
            LOGGER_BASE.error(error);
            throw new Exception(error);
        }

        // Push files
        String output = this.runAdbCommand(deviceId, "push " + localFilePath + " " + remotePath);
        LOGGER_BASE.info(output);
        if ((output.toLowerCase().contains("error")) || (output.toLowerCase().contains("failed"))) {
            String error = "Failed to transfer " + localPath + " to " + remotePath;
            LOGGER_BASE.error(error);
            LOGGER_BASE.error("Error: " + output);
            throw new Exception(error);
        } else {
            LOGGER_BASE.info(localPath + " transferred to " + remotePath);
        }
    }

    /**
     * TODO(dtopuzov): Add docs.
     *
     * @param deviceId
     * @param remotePath
     * @param destinationFolder
     * @throws Exception
     */
    public void pullFile(String deviceId, String remotePath, String destinationFolder) throws Exception {

        // Verify remotePath
        String remoteBasePath = remotePath.substring(0, remotePath.lastIndexOf("/"));
        String sdcardFiles = this.runAdbCommand(this.settings.deviceId, "shell ls -la " + remoteBasePath);
        if (sdcardFiles.contains("No such file or directory")) {
            String error = remoteBasePath + " does not exist.";
            LOGGER_BASE.error(error);
            throw new Exception(error);
        }

        // Verify localPath
        String localPath = this.settings.baseLogDir;
        if (destinationFolder != null) {
            destinationFolder = destinationFolder.replace("/", File.separator);
            destinationFolder = destinationFolder.replace("\\", File.separator);
            localPath = this.settings.baseLogDir + File.separator + destinationFolder;
        }

        // Pull files
        String output = this.runAdbCommand(deviceId, "pull " + remotePath + " " + localPath);
        LOGGER_BASE.info(output);
        String o = output.toLowerCase();
        if ((o.contains("error")) || (o.contains("failed")) || (o.contains("does not exist"))) {
            String error = "Failed to transfer " + remotePath + " to " + localPath;
            LOGGER_BASE.error(error);
            LOGGER_BASE.error("Error: " + output);
            throw new Exception(error);
        } else {
            LOGGER_BASE.info(remotePath + " transferred to " + localPath);
        }
    }

    /**
     * TODO(dtopuzov): Add docs.
     *
     * @param deviceId
     * @param remotePath
     * @throws Exception
     */
    public void pullFile(String deviceId, String remotePath) throws Exception {
        this.pullFile(deviceId, remotePath, null);
    }

    /**
     * TODO(dtopuzov): Add docs.
     *
     * @param fileName
     */
    public void getScreenshot(String fileName) {
        try {
            String takeScreenCommand = this.runAdbCommand(this.settings.deviceId, "shell screencap -p /sdcard/" + fileName);
            String copyFileCommand = this.runAdbCommand(this.settings.deviceId, "pull /sdcard/" + fileName);
            OSUtils.runProcess(takeScreenCommand);
            OSUtils.runProcess(copyFileCommand);
        } catch (Exception e) {
            LOGGER_BASE.error("Failed to take screenshot with adb.");
        }
    }

    /**
     * TODO(dtopuzov): Add docs.
     *
     * @param deviceId
     */
    public void goHome(String deviceId) {
        String command = "shell am start -a android.intent.action.MAIN -c android.intent.category.HOME";
        this.runAdbCommand(deviceId, command);
        LOGGER_BASE.info("Navigate go home following command:");
        LOGGER_BASE.info(command);
    }

    /**
     * TODO(dtopuzov): Add docs.
     *
     * @param deviceId
     * @param deviceBootTime
     */
    public static void startDeveloperOptions(String deviceId, int deviceBootTime) {
        String command = "shell am start -n com.android.settings/.DevelopmentSettings";

        Adb.runAdbCommandStatic(deviceId, command, deviceBootTime, true);
        LOGGER_BASE.info("Start Development settings by the following command:");
        LOGGER_BASE.info(command);
    }

    /**
     * TODO(dtopuzov): Add docs.
     *
     * @param deviceId
     * @param deviceBootTimeout
     */
    public static void changeLocationMode(String deviceId, int deviceBootTimeout) {
        String command = "shell settings put secure location_providers_allowed gps,network";
        Adb.runAdbCommandStatic(deviceId, command, deviceBootTimeout, true);
        LOGGER_BASE.info("Change Location mode to High accuracy by the following command:");
        LOGGER_BASE.info(command);
    }

    /**
     * TODO(dtopuzov): Add docs.
     *
     * @param deviceId
     */
    protected void closeErrorActivty(String deviceId) {
        String command = "shell am force-stop com.android.launcher3";
        this.runAdbCommand(deviceId, command);
    }

    /**
     * TODO(dtopuzov): Add docs.
     *
     * @param deviceId
     * @param command
     * @param deviceBootTime
     * @param waitFor
     * @return
     */
    private static String runAdbCommandStatic(String deviceId, String command, int deviceBootTime, boolean waitFor) {
        String adbCommand = ADB_PATH;
        if (deviceId != null && deviceId != "") {
            adbCommand += " -s " + deviceId;
        }
        adbCommand += " " + command;
        String output = OSUtils.runProcess(waitFor, deviceBootTime, adbCommand);

        return output;
    }

    /**
     * TODO(dtopuzov): Add docs.
     *
     * @param deviceId
     * @param command
     * @param deviceBootTime
     * @param waitFor
     * @return
     */
    private String runAdbCommand(String deviceId, String command, int deviceBootTime, boolean waitFor) {
        String adbCommand = ADB_PATH;
        if (deviceId != null && deviceId != "") {
            adbCommand += " -s " + deviceId;
        }
        adbCommand += " " + command;
        String output = OSUtils.runProcess(waitFor, deviceBootTime, adbCommand);
        if (output.toLowerCase().contains("address already in use")) {
            this.killAdbProcess();
            output = OSUtils.runProcess(adbCommand);
        }
        return output;
    }


    public String findConnectedDeviceViaUsb() {
        String listAllDeviceCommand = "devices -l";

        String allDevices = this.runAdbCommand(listAllDeviceCommand);
        String[] list = allDevices.split("\\r?\\n");

        for (String device : list) {
            if (device.contains("usb") && device.contains("device")) {
                return device.substring(0, device.indexOf(" ")).trim();
            }
        }

        return "";
    }
}
