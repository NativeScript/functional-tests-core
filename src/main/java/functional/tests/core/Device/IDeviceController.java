package functional.tests.core.Device;

import java.util.List;
import java.util.concurrent.TimeoutException;

public interface IDeviceController {

    // private static String runAdbCommand(String command) {
    String runAdbCommand(String command);

    //private static String runAdbCommand(String deviceId, String command, boolean waitFor) {
    String runCommand(String deviceId, String command, boolean waitFor);

    // String runAdbCommand(String deviceId, String command) {
    String runAdbCommand(String deviceId, String command);

    List<String> getDevices();

    //startAdb
    void start();

    //stopAdb
    void stop();

    //killAdbProcess
    void killAdbProcess();

    //getAdbLog
    String geLog(String deviceId);

    List<String> getInstalledApps();

    void stopApp(String appId);

    boolean isAppInstalled(String appId);

    void installApp(String appFileName, String appId, boolean skipIfAvailable);

    void uninstallApp(String appId);

    // If emulator with same name exists, do nothing, else create emulator
    // createEmulator
    void createEmulator(String avdName, String options, Boolean force);

    // If emulator with same name exists, do nothing, else create emulator
    void createEmulator(String avdName, String options);

    void startEmulator(String avdName, int port);

    void stopEmulator();

    void waitForDevice(String deviceId, int timeOut) throws TimeoutException;

    void setScreenOffTimeOut(String deviceId, int timeOut);

    void waitUntilEmulatorBoot(String deviceId, int timeOut) throws TimeoutException;

    boolean isLocked(String deviceId);

    void unlock(String deviceId);

    void pushFile(String deviceId, String localPath, String remotePath) throws Exception;

    void pullFile(String deviceId, String remotePath, String destinationFolder) throws Exception;

    void pullFile(String deviceId, String remotePath) throws Exception;

    boolean isAppRunning(String deviceId, String appId);

    void getScreenshot(String fileName);

    void goHome(String deviceId);

    /**
     * Stop application *
     */
    void stopApplication(String appId);

    /**
     * Start application *
     */
    void startApplication(String appId, String activity);

    /**
     * Start application *
     */
    void startApplication(String appId);

    void startDeveloperOptions(String deviceId);

    List<String> getSimulatorsIdsByName(String deviceName);

    void deleteSimulator(String deviceName);

    String createSimulator(String simulatorName, String deviceType, String iOSVersion);

    void reinstallApp();

    void resetSimulatorSettings();
}
