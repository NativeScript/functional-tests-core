package functional.tests.core.mobile.device.android;

import functional.tests.core.enums.EmulatorState;
import functional.tests.core.enums.OSType;
import functional.tests.core.exceptions.DeviceException;
import functional.tests.core.log.LoggerBase;
import functional.tests.core.mobile.device.EmulatorInfo;
import functional.tests.core.mobile.find.Wait;
import functional.tests.core.mobile.settings.MobileSettings;
import functional.tests.core.utils.FileSystem;
import functional.tests.core.utils.OSUtils;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

/**
 * Adb wrapper.
 */
public class Adb {

    private static final String ADB_PATH = System.getenv("ANDROID_HOME") + File.separator + "platform-tools" + File.separator + "adb";
    private static final String ANDROID_PATH = System.getenv("ANDROID_HOME") + File.separator + "tools" + File.separator + "android";
    private static final String AVDMANAGER_PATH = System.getenv("ANDROID_HOME") + File.separator + "tools" + File.separator + "bin" + File.separator + "avdmanager";
    private static final String EMULATOR_PATH = System.getenv("ANDROID_HOME") + File.separator + "tools" + File.separator + "emulator";
    private static final LoggerBase LOGGER_BASE = LoggerBase.getLogger("Adb");
    private String avdPath;
    private String emulatorStartLogPath;
    private MobileSettings settings;

    /**
     * Init adb wrapper.
     *
     * @param settings MobileSettings object.
     */
    public Adb(MobileSettings settings) {
        this.avdPath = this.getAvdPath();
        this.settings = settings;
        this.emulatorStartLogPath = this.settings.baseLogDir + File.separator + "emulator.log";
    }

    private String getAvdPath() {
        String output = OSUtils.runProcess(true, 5, ANDROID_PATH + " -h");
        if (output.toLowerCase().contains("usage:")) {
            return ANDROID_PATH;
        } else {
            return AVDMANAGER_PATH;
        }
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
     * Get list of installed android apps.
     *
     * @return List of apps.
     */
    public List<String> getInstalledApps() {
        List<String> list = new ArrayList<String>();
        String rowData = this.runAdbCommand(this.settings.deviceId, "shell pm list packages -3");
        for (String line : rowData.split("\\r?\\n")) {
            if (line.contains(".") && !line.contains("WARNING")) {
                String appId = line.replace("package:", "").trim();
                list.add(appId);
            }
        }
        return list;
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
     * Uninstall application.
     *
     * @param appId Bundle identifier.
     */
    public void uninstallApp(String appId) {
        if (this.isAppInstalled(appId)) {
            this.stopApp(appId);
            if (!appId.contains("appium")) {
                String uninstallResult = this.runAdbCommand(this.settings.deviceId, "uninstall " + appId);
                if (uninstallResult.contains("Success")) {
                    LOGGER_BASE.info(appId + " successfully uninstalled.");
                } else {
                    LOGGER_BASE.error("Failed to uninstall " + appId + ". Error: " + uninstallResult);
                }
            } else {
                LOGGER_BASE.info("Skip uninstall: " + appId);
            }
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
     * Start emulator image.
     *
     * @param avdName Name of AVD image.
     * @param port    Port.
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


    protected void stopEmulator(String deviceId) {
        this.runAdbCommand(deviceId, "emu kill");
        LOGGER_BASE.info("Emulator " + deviceId + " killed.");
    }

    /**
     * Stop all emulator processes.
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
     * Check if emulator is running.
     *
     * @param deviceId Device identifier.
     * @return True if it is running.
     */
    protected boolean isRunning(String deviceId) {
        boolean booted = false;

        String rowData = this.runAdbCommand(deviceId, "shell dumpsys activity");
        String[] list = rowData.split("\\r?\\n");

        for (String line : list) {

            if (line.contains("Recent #0")
                    && (line.contains("com.android.launcher")
                    || line.contains("com.google.android.googlequicksearchbox")
                    || line.contains("com.google.android.apps.nexuslauncher")
                    || line.contains(this.settings.packageId))) {
                booted = true;
                break;
            }
        }

        if (booted) {
            LOGGER_BASE.info(deviceId + " is up and running.");
            this.setScreenOffTimeOut(deviceId, 180000);
        } else {
            LOGGER_BASE.debug(deviceId + " is not running.");
        }

        return booted;
    }

    /**
     * Wait until emulator boot.
     *
     * @param deviceId Device identifier.
     * @param timeOut  Timeout in seconds.
     * @throws TimeoutException When if fails to boot.
     */
    protected void waitUntilEmulatorBoot(String deviceId, int timeOut) throws TimeoutException {
        long startTime = new Date().getTime();
        long currentTime = new Date().getTime();
        boolean found = false;

        while ((currentTime - startTime) < timeOut * 1000) {
            currentTime = new Date().getTime();
            found = this.isRunning(deviceId);

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

    public String getAvdName(String deviceId) throws DeviceException {
        String command = "(sleep 1; echo avd name) | telnet localhost " + deviceId.split("-")[1];
        String name = "";
        for (long stop = System.nanoTime() + TimeUnit.SECONDS.toNanos(30); stop > System.nanoTime(); ) {
            String output = OSUtils.runProcess(command);
            try {
                name = StringUtils.substringBetween(output, "OK", "OK").trim();
                break;
            } catch (Exception e) {
                LOGGER_BASE.debug("Failed to get name of " + deviceId);
            }
        }
        if (name.equalsIgnoreCase("")) {
            throw new DeviceException("Failed to get name of " + deviceId);
        }
        return name;
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
     * @param timeout
     * @param waitFor
     * @return
     */
    private static String runAdbCommandStatic(String deviceId, String command, int timeout, boolean waitFor) {
        String adbCommand = ADB_PATH;
        if (deviceId != null && deviceId != "") {
            adbCommand += " -s " + deviceId;
        }
        adbCommand += " " + command;
        String output = OSUtils.runProcess(waitFor, timeout, adbCommand);

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

    /**
     * Return time since emulator is in use.
     *
     * @param deviceId Device identifier.
     * @return Time since emulator is in use in milliseconds (0 if emulator is not in used).
     */
    long usedSince(String deviceId) {
        File temp = new File("used.tmp");
        if (temp.exists()) {
            temp.delete();
        }
        this.runAdbCommand(deviceId, "pull -a /data/local/tmp/used.tmp used.tmp");
        temp = new File("used.tmp");
        if (temp.exists()) {
            long now = new Date().getTime();
            long lastModified = temp.lastModified();
            long usedFrom = now - lastModified;
            temp.delete();
            LOGGER_BASE.info(deviceId + " is in use from " + String.valueOf(TimeUnit.MILLISECONDS.toSeconds(usedFrom)) + " seconds.");
            return usedFrom;
        } else {
            LOGGER_BASE.info(deviceId + " is not used!");
            return 0;
        }
    }

    /**
     * Mark emulator as used.
     *
     * @param deviceId emulator identifier.
     */
    void markUsed(String deviceId) {
        LOGGER_BASE.info("Mark emulator as used: " + deviceId);
        this.runAdbCommand(deviceId, "shell touch /data/local/tmp/used.tmp");
    }

    /**
     * Mark emulator as unused.
     *
     * @param deviceId emulator identifier.
     */
    void markUnused(String deviceId) {
        LOGGER_BASE.info("Mark emulator as unused: " + deviceId);
        this.runAdbCommand(deviceId, "shell rm -rf /data/local/tmp/used.tmp");
    }

    /**
     * Get emulator info - id, name, state.
     *
     * @param state EmulatorState filter.
     * @return List of EmulatorInfo objects.
     * @throws DeviceException When fails to get AVD name.
     */
    List<EmulatorInfo> getEmulatorInfo(EmulatorState state) throws DeviceException {
        List<EmulatorInfo> simulators = this.getEmulatorInfo();
        return simulators.stream().filter(p -> p.state == state).collect(Collectors.toList());
    }

    /**
     * Get emulator info - id, name, state.
     *
     * @return List of EmulatorInfo objects.
     * @throws DeviceException When fails to get AVD name.
     */
    List<EmulatorInfo> getEmulatorInfo() throws DeviceException {
        List<EmulatorInfo> list = new ArrayList<>();
        for (String item : this.getDevices()) {
            if (item.contains("emulator-")) {
                String id = item.split("\t")[0].trim();
                String name = this.getAvdName(id);
                EmulatorState state = EmulatorState.Shutdown;
                long usedFrom = -1;
                if (item.contains("device")) {
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
     * Stop emulators used for more than X minutes.
     *
     * @param minutes Minutes.
     * @throws DeviceException When fails to get AVD name.
     */
    public void stopUsedEmulators(int minutes) throws DeviceException {
        List<EmulatorInfo> usedEmulators = this.getEmulatorInfo(EmulatorState.Used);
        usedEmulators.forEach((emu) -> {
            if (emu.usedFrom > minutes * 60 * 1000) {
                LOGGER_BASE.warn(String.format("Emulators used more than %s minutes detected!", String.valueOf(minutes)));

                // Stop all emulators
                this.stopEmulator(emu.id);
                Wait.sleep(1000);

                // Kill all the processes related with emulator (on linux and mac)
                if (this.settings.os != OSType.Windows) {
                    String killCommand = "ps aux | grep -ie " + emu.id + " | awk '{print $2}' | xargs kill -9";
                    OSUtils.runProcess(killCommand);
                }
            }
        });
    }
}
