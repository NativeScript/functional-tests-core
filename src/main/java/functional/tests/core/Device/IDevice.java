package functional.tests.core.Device;

import functional.tests.core.Device.Android.Adb;
import functional.tests.core.Enums.PlatformType;
import functional.tests.core.Exceptions.AppiumException;
import functional.tests.core.Exceptions.DeviceException;
import functional.tests.core.Settings.Settings;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

public interface IDevice {
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
