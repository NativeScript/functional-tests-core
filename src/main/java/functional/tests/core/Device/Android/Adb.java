package functional.tests.core.Device.Android;

import functional.tests.core.Enums.DeviceType;
import functional.tests.core.Enums.OSType;
import functional.tests.core.Exceptions.DeviceException;
import functional.tests.core.Find.Wait;
import functional.tests.core.Log.Log;
import functional.tests.core.OSUtils.FileSystem;
import functional.tests.core.OSUtils.OSUtils;
import functional.tests.core.Settings.Settings;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class Adb {

    private static final String adbPath = System.getenv("ANDROID_HOME") + File.separator + "platform-tools" + File.separator + "adb";
    private static final String androidPath = System.getenv("ANDROID_HOME") + File.separator + "tools" + File.separator + "android";
    private static final String emulatorPath = System.getenv("ANDROID_HOME") + File.separator + "tools" + File.separator + "emulator";
    private static final String emulatorStartLogPath = Settings.baseLogDir + File.separator + "emulator.log";

    private static String runAdbCommand(String command) {
        String adbCommand = adbPath + " " + command;
        String output = OSUtils.runProcess(adbCommand);
        if (output.toLowerCase().contains("address already in use")) {
            killAdbProcess();
            output = OSUtils.runProcess(adbCommand);
        }
        return output;
    }

    private static String runAdbCommand(String deviceId, String command, boolean waitFor) {
        String adbCommand = adbPath + " -s " + deviceId + " " + command;
        String output = OSUtils.runProcess(waitFor, Settings.defaultTimeout * 5, adbCommand);
        if (output.toLowerCase().contains("address already in use")) {
            killAdbProcess();
            output = OSUtils.runProcess(adbCommand);
        }
        return output;
    }

    public static String runAdbCommand(String deviceId, String command) {
        return runAdbCommand(deviceId, command, true);
    }

    protected static List<String> getDevices() {
        String rowData = runAdbCommand("devices");
        String[] list = rowData.split("\\r?\\n");
        return Arrays.asList(list);
    }

    protected static void startAdb() {
        Log.info("Start adb");
        OSUtils.runProcess(adbPath + " start-server");
    }

    protected static void stopAdb() {
        Log.info("Stop adb");
        OSUtils.runProcess(adbPath + " kill-server");
    }

    protected static void killAdbProcess() {
        if (Settings.OS == OSType.Windows) {
            OSUtils.stopProcess("adb.exe");
        } else {
            OSUtils.stopProcess("adb");
        }
    }

    public static String getAdbLog(String deviceId) {
        return runAdbCommand(Settings.deviceId, "logcat -d");
    }

    public static List<String> getInstalledApps() {
        String rowData = runAdbCommand(Settings.deviceId, "shell pm list packages");
        String trimData = rowData.replace("package:", "");
        String[] list = trimData.split("\\r?\\n");
        return Arrays.asList(list);
    }

    protected static void stopApp(String appId) {
        String stopCommand = runAdbCommand(Settings.deviceId, "shell am force-stop " + appId);
        OSUtils.runProcess(stopCommand);
    }

    public static boolean isAppInstalled(String appId) {
        List<String> installedApps = Adb.getInstalledApps();
        boolean appFound = false;
        for (String app : installedApps) {
            if (app.contains(appId)) {
                Log.info("App " + appId + " found.");
                appFound = true;
            }
        }
        return appFound;
    }

    public static void installApp(String testAppName, String packageId) throws IOException {
        boolean appInstalled;
        appInstalled = isAppInstalled(packageId);
        if (appInstalled) {
            Log.info("Uninstall a previous version " + packageId + " app.");
            Adb.uninstallApp(packageId);
        }

        String apkPath = Settings.baseTestAppDir + File.separator + testAppName;
        Log.info("Installing " + apkPath + " ...");
        String output = Adb.runAdbCommand(Settings.deviceId, "install " + apkPath, true);
        Log.info(output);

        appInstalled = isAppInstalled(packageId);
        if (!appInstalled) {
            Log.error("Failed to install" + apkPath + "!");
            throw new IOException("Failed to install" + apkPath + "!");
        }
    }

    public static void uninstallApp(String appId) {
        stopApp(appId);
        String uninstallResult = runAdbCommand(Settings.deviceId, "shell pm uninstall -k " + appId);

        if (uninstallResult.contains("Success")) {
            Log.info(appId + " successfully uninstalled.");
        } else {
            Log.error("Failed to uninstall " + appId + ". Error: " + uninstallResult);
        }
    }

    // If emulator with same name exists, do nothing, else create emulator
    public static void createEmulator(String avdName, String options, Boolean force) throws DeviceException {

        String avds = OSUtils.runProcess(androidPath + " list avds");
        Boolean emulatorExists = false;
        if (avds.contains(avdName + ".avd")) {
            Log.info(avdName + " already exists.");
            emulatorExists = true;
        }
        if (force || (!emulatorExists)) {
            // Create emulator
            String command;
            if (Settings.OS == OSType.Windows) {
                Log.fatal("Create emulator not implemented for Windows systems.");
                throw new UnsupportedOperationException("Create emulator not implemented for Windows systems.");
            } else {
                command = "echo no | " + androidPath + " -s create avd -n " + avdName + " " + options + " -f";
            }

            Log.info("Create emulator with command: ");
            Log.info(command);
            OSUtils.runProcess(command);

            // Verify it exists
            avds = OSUtils.runProcess(androidPath + " list avds");
            if (avds.contains(avdName + ".avd")) {
                Log.info(avdName + " created successfully.");
            } else {
                String error = "Emulator " + avdName + " is not available!";
                Log.fatal(error);
                throw new DeviceException(error);
            }
        }
    }

    // If emulator with same name exists, do nothing, else create emulator
    public static void createEmulator(String avdName, String options) throws DeviceException {
        createEmulator(avdName, options, false);
    }

    protected static void startEmulator(String avdName, int port) {
        String command = emulatorPath + " -port " + port + " -avd " + avdName;
        if (Settings.emulatorOptions != null) {
            command = command + " " + Settings.emulatorOptions;
        }
        command = command + " > " + emulatorStartLogPath + " 2>&1";
        Log.info("Starting emulator with command: " + command);
        OSUtils.runProcess(false, Integer.MAX_VALUE, command);
    }

    protected static void stopEmulator() {
        if (Settings.OS == OSType.Windows) {
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
            OSUtils.stopProcess("emulator64-arm");

            // Linux Ubuntu
            // For some reason the 'qemu-system-i386' process appears as 'qemu-system-i38'.
            // An alternative way to stop the emulator would be through the adb. For example:
            // adb -s emulator-5554 emu kill
            if (Settings.OS == OSType.Linux) {
                OSUtils.stopProcess("qemu-system-i38"); // qemu-system-i386
            }
        }
        Log.info("Emulator killed.");
    }

    protected static void waitForDevice(String deviceId, int timeOut) throws TimeoutException {
        long startTime = new Date().getTime();
        for (int i = 0; i < 999; i++) {

            boolean found = false;
            long currentTime = new Date().getTime();

            String emulatorStartupLog = "";
            if ((currentTime - startTime) < timeOut * 1000) {

                List<String> devices = getDevices();

                for (String device : devices) {
                    if (device.contains(deviceId) && device.contains("device")) {
                        found = true;
                        break;
                    }
                }

                if (found) {
                    Log.info("Device " + deviceId + " found.");
                    break;
                } else {
                    Log.info("Device " + deviceId + " not found. Wait...");

                    if (!Settings.isRealDevice) {
                        try {
                            emulatorStartupLog = FileSystem.readFile(emulatorStartLogPath);
                        } catch (IOException e) {
                            Log.error("Failed to read emulator log: " + emulatorStartLogPath);
                        }

                        if (emulatorStartupLog.contains("ERROR")) {
                            break;
                        }
                    }
                    Wait.sleep(3000);
                }
            } else {
                String error = "Failed to find device "
                        + deviceId + " in " + String.valueOf(timeOut)
                        + " seconds.";
                Log.fatal(error);

                if (Settings.deviceType == DeviceType.Emulator) {
                    Log.separator();
                    Log.info(emulatorStartupLog);
                }

                throw new TimeoutException(error);
            }
        }
    }

    protected static void setScreenOffTimeOut(String deviceId, int timeOut) {
        Log.info("Set SCREEN_OFF_TIMEOUT to " + String.valueOf(timeOut));
        runAdbCommand(deviceId, "shell settings put system screen_off_timeout " + timeOut);
    }

    protected static void waitUntilEmulatorBoot(String deviceId, int timeOut) throws TimeoutException {
        long startTime = new Date().getTime();
        for (int i = 0; i < 999; i++) {

            boolean found = false;
            long currentTime = new Date().getTime();

            if ((currentTime - startTime) < timeOut * 1000) {

                String rowData = runAdbCommand(deviceId, "shell dumpsys activity");
                String[] list = rowData.split("\\r?\\n");

                for (String line : list) {

                    if (line.contains("Recent #0") &&
                            ((line.contains("com.android.launcher"))
                                    || (line.contains("com.google.android.googlequicksearchbox")))) {
                        found = true;
                        break;
                    }
                }

                if (found) {
                    Log.info("Emulator is up and running.");
                    setScreenOffTimeOut(deviceId, 180000);
                    break;
                } else {
                    Log.info("Booting...");
                    Wait.sleep(3000);
                }
            } else {
                String error = "Failed to load com.android.launcher activity in "
                        + String.valueOf(timeOut)
                        + " seconds.";
                Log.fatal(error);
                throw new TimeoutException(error);
            }
        }
    }

    public static boolean isLocked(String deviceId) {
        String output = runAdbCommand(deviceId, "shell dumpsys window windows");
        if (output.contains("mDrawState=HAS_DRAWN mLastHidden=true")) {
            return true;
        } else {
            return false;
        }
    }

    public static void unlock(String deviceId) {
        runAdbCommand(deviceId, "shell input keyevent 82");
    }

    public static void pushFile(String deviceId, String localPath, String remotePath) throws Exception {

        runAdbCommand(Settings.deviceId, "shell mount -o rw,remount -t rootfs /");

        // Verify remotePath
        String remoteBasePath = remotePath.substring(0, remotePath.lastIndexOf("/"));
        String sdcardFiles = runAdbCommand(Settings.deviceId, "shell ls -la " + remoteBasePath);
        if (sdcardFiles.contains("No such file or directory")) {
            String error = remoteBasePath + " does not exist.";
            Log.error(error);
            throw new Exception(error);
        }

        // Verify localPath
        localPath = localPath.replace("/", File.separator);
        localPath = localPath.replace("\\", File.separator);
        String localFilePath = Settings.baseTestDataDir + File.separator + localPath;
        if (!FileSystem.exist(localFilePath)) {
            String error = localPath + " does not exist.";
            Log.error(error);
            throw new Exception(error);
        }

        // Push files
        String output = runAdbCommand(deviceId, "push " + localFilePath + " " + remotePath);
        Log.info(output);
        if ((output.toLowerCase().contains("error")) || (output.toLowerCase().contains("failed"))) {
            String error = "Failed to transfer " + localPath + " to " + remotePath;
            Log.error(error);
            Log.error("Error: " + output);
            throw new Exception(error);
        } else {
            Log.info(localPath + " transferred to " + remotePath);
        }
    }

    public static void pullFile(String deviceId, String remotePath, String destinationFolder) throws Exception {

        // Verify remotePath
        String remoteBasePath = remotePath.substring(0, remotePath.lastIndexOf("/"));
        String sdcardFiles = runAdbCommand(Settings.deviceId, "shell ls -la " + remoteBasePath);
        if (sdcardFiles.contains("No such file or directory")) {
            String error = remoteBasePath + " does not exist.";
            Log.error(error);
            throw new Exception(error);
        }

        // Verify localPath
        String localPath = Settings.baseLogDir;
        if (destinationFolder != null) {
            destinationFolder = destinationFolder.replace("/", File.separator);
            destinationFolder = destinationFolder.replace("\\", File.separator);
            localPath = Settings.baseLogDir + File.separator + destinationFolder;
        }

        // Pull files
        String output = runAdbCommand(deviceId, "pull " + remotePath + " " + localPath);
        Log.info(output);
        String o = output.toLowerCase();
        if ((o.contains("error")) || (o.contains("failed")) || (o.contains("does not exist"))) {
            String error = "Failed to transfer " + remotePath + " to " + localPath;
            Log.error(error);
            Log.error("Error: " + output);
            throw new Exception(error);
        } else {
            Log.info(remotePath + " transferred to " + localPath);
        }
    }

    public static void pullFile(String deviceId, String remotePath) throws Exception {
        pullFile(deviceId, remotePath, null);
    }

    public static boolean isAppRunning(String deviceId, String appId) {
        String processes = runAdbCommand(deviceId, "shell ps");
        if (processes.contains(appId)) {
            return true;
        } else {
            return false;
        }
    }

    public static void getScreenshot(String fileName) {
        try {
            String takeScreenCommand = runAdbCommand(Settings.deviceId, "shell screencap -p /sdcard/" + fileName);
            String copyFileCommand = runAdbCommand(Settings.deviceId, "pull /sdcard/" + fileName);
            OSUtils.runProcess(takeScreenCommand);
            OSUtils.runProcess(copyFileCommand);
        } catch (Exception e) {
            Log.error("Failed to take screenshot with adb.");
        }
    }

    public static void goHome(String deviceId) {
        String command = "shell am start -a android.intent.action.MAIN -c android.intent.category.HOME";
        runAdbCommand(deviceId, command);
        Log.info("Navigate go home following command:");
        Log.info(command);
    }

    /**
     * Stop application *
     */
    public static void stopApplication(String appId) {
        Log.info("Stop " + appId);
        String command = "shell am force-stop " + appId;
        runAdbCommand(Settings.deviceId, command);
    }

    /**
     * Start application *
     */
    public static void startApplication(String appId, String activity) {
        Log.info("Start " + appId + " with command:");
        String command = "shell am start -a android.intent.action.MAIN -n " + appId + "/" + activity;
        Log.info(command);
        runAdbCommand(Settings.deviceId, command);
    }

    /**
     * Start application *
     */
    public static void startApplication(String appId) {
        Log.info("Start " + appId + " with command:");
        String command = "shell monkey -p " + appId + " 1";
        Log.info(command);
        runAdbCommand(Settings.deviceId, command);
    }

    /**
     * Start Developer Settings.
     * @param deviceId
     */
    public static void startDeveloperOptions(String deviceId) {
        String command = "shell am start -n com.android.settings/.DevelopmentSettings";
        runAdbCommand(deviceId, command);
        Log.info("Start Development Settings by the following command:");
        Log.info(command);
    }

    /**
     * Change Location mode to High accuracy.
     * @param deviceId
     */
    public static void changeLocationMode(String deviceId) {
        String command = "shell settings put secure location_providers_allowed gps,network";
        runAdbCommand(deviceId, command);
        Log.info("Change Location mode to High accuracy by the following command:");
        Log.info(command);
    }
}
