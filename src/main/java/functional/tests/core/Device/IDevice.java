package functional.tests.core.Device;

import functional.tests.core.Exceptions.AppiumException;
import functional.tests.core.Exceptions.DeviceException;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

public interface IDevice {

    IDeviceController getDeviceController();

    void installApp(String appName);

    void initDevice() throws DeviceException, TimeoutException;

    void stopDevice();

    void stopApps(List<String> uninstallAppsList);

    void uninstallApps(List<String> uninstallAppsList);

    String getContent(String testName) throws IOException;

    void writeConsoleLogToFile(String fileName) throws IOException;

    void verifyAppRunning(String deviceId, String appId) throws AppiumException, IOException;

    void pushFile(String deviceId, String localPath, String remotePath) throws Exception;

    void pullFile(String deviceId, String remotePath, String destinationFolder) throws Exception;

    void pullFile(String deviceId, String remotePath) throws Exception;

    void cleanConsoleLog();

    boolean isAppRunning(String deviceId, String appId);

    String getStartupTime(String appId) throws IOException;
}
