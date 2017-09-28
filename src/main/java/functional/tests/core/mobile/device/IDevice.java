package functional.tests.core.mobile.device;

import functional.tests.core.enums.DeviceType;
import functional.tests.core.exceptions.DeviceException;
import functional.tests.core.exceptions.MobileAppException;
import org.openqa.selenium.html5.Location;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * Device abstraction.
 */
public interface IDevice {

    /**
     * TODO(): Add docs.
     *
     * @return
     */
    String getName();

    /**
     * TODO(): Add docs.
     *
     * @return
     */
    DeviceType getType();

    /**
     * Start device (ensure it is up and running).
     *
     * @return Device object.
     * @throws DeviceException  When fail to create emulator or simulator.
     * @throws TimeoutException When device fails to boot or not attached to host.
     */
    IDevice start() throws DeviceException, TimeoutException;

    /**
     * Stop emulators/simulators.
     */
    void stop() throws DeviceException;

    /**
     * Get id of current device.
     *
     * @return Id of current device.
     */
    String getId();

    /**
     * Install application (force reinstall if already available).
     *
     * @param appName   Name of application package.
     * @param packageId PackageId of application.
     * @throws IOException When test application not found.
     */
    void installApp(String appName, String packageId) throws IOException;

    /**
     * Start application.
     *
     * @param packageId Bundle identifier.
     */
    void startApplication(String packageId);

    /**
     * Restart application under test.
     */
    void restartApp();

    /**
     * Close application under test.
     */
    void closeApp();

    /**
     * TODO(): Add docs.
     *
     * @param packageId
     * @throws MobileAppException
     */
    void verifyAppRunning(String packageId) throws MobileAppException;

    /**
     * TODO(): Add docs.
     *
     * @param packageId
     * @return
     */
    boolean isAppRunning(String packageId);

    /**
     * TODO(): Add docs.
     *
     * @param seconds
     */
    void runAppInBackGround(int seconds);

    /**
     * Android: Uninstall all third party apps.
     * iOS: Uninstall all apps matching org.nativescript. and com.telerik.
     *
     * @throws DeviceException When uninstall fails
     */
    void uninstallApps() throws DeviceException;

    /**
     * TODO(): Add docs.     // Get file system of device
     *
     * @param fileName
     * @return
     * @throws IOException
     */
    String getContent(String fileName) throws IOException;

    /**
     * TODO(): Add docs.
     *
     * @param fileName
     * @throws IOException
     */
    void writeConsoleLogToFile(String fileName) throws IOException;

    /**
     * TODO(): Add docs.
     */
    void cleanConsoleLog();

    /**
     * TODO(): Add docs.
     *
     * @param localPath
     * @param remotePath
     * @throws Exception
     */
    void pushFile(String localPath, String remotePath) throws Exception;

    /**
     * TODO(): Add docs.
     *
     * @param remotePath
     * @param destinationFolder
     * @throws Exception
     */
    void pullFile(String remotePath, String destinationFolder) throws Exception;

    /**
     * TODO(): Add docs.
     *
     * @param packageId
     * @return
     */
    String getStartupTime(String packageId);

    /**
     * TODO(): Add docs.
     *
     * @param packageId
     * @return
     */
    int getMemUsage(String packageId);


    void logAppStartupTime(String packageId);

    /**
     * TODO(): Add docs.
     *
     * @throws IOException
     */
    void logPerfInfo() throws IOException;

    /**
     * TODO(): Add docs.
     *
     * @param location
     */
    void setLocation(Location location);
}
