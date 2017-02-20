package functional.tests.core.mobile.device;

import functional.tests.core.enums.DeviceType;
import functional.tests.core.enums.PlatformType;
import functional.tests.core.exceptions.DeviceException;
import functional.tests.core.exceptions.MobileAppException;
import functional.tests.core.log.LoggerBase;
import functional.tests.core.mobile.appium.Client;
import functional.tests.core.mobile.basetest.MobileContext;
import functional.tests.core.mobile.basetest.MobileSetupManager;
import functional.tests.core.mobile.device.android.AndroidDevice;
import functional.tests.core.mobile.device.ios.IOSDevice;
import functional.tests.core.mobile.element.UIElement;
import functional.tests.core.mobile.settings.MobileSettings;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.remote.HideKeyboardStrategy;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.ScreenOrientation;
import org.openqa.selenium.html5.Location;
import org.testng.Assert;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * Device abstraction.
 */
public class Device {

    private static final LoggerBase LOGGER_BASE = LoggerBase.getLogger("Device");
    private IDevice device;
    private MobileSettings settings;
    private Client client;
    //private MobileContext context;
    private Dimension windowSize;

    /**
     * Init iOS device.
     *
     * @param client
     * @param settings
     */
    public Device(Client client, MobileSettings settings) {
        this.client = client;
        this.settings = settings;
    }

    /**
     * Get android device.
     *
     * @return AndroidDevice instance.
     */
    public AndroidDevice getAndroidDevice() {
        return (AndroidDevice) this.device;
    }


    /**
     * Get device.
     *
     * @return IOSDevice instance.
     */
    public IOSDevice getIOSDevice() {
        return (IOSDevice) this.device;
    }

    /**
     * Get device name.
     *
     * @return Name of current device.
     */
    public String getName() {
        return this.device.getName();
    }

    /**
     * Get device id.
     *
     * @return Device unique identifier.
     */
    public String getId() {
        return this.device.getId();
    }

    /**
     * Get device type.
     *
     * @return DeviceType enum value.
     */
    public DeviceType getType() {
        return this.device.getType();
    }

    /**
     * Start installed application with given package id.
     *
     * @param packageId PackageID of application.
     */
    public void startApplication(String packageId) {
        this.device.startApplication(packageId);
    }

    /**
     * Start device.
     * 1. Start device.
     * 2. Start appium session.
     * 3. Ensure app is running.
     *
     * @throws DeviceException    When device is not available.
     * @throws TimeoutException   When fail to start device in specified time.
     * @throws MobileAppException When app fail to start.
     */
    public void start() throws DeviceException, TimeoutException, MobileAppException {
        if (this.settings.platform == PlatformType.Android) {
            this.device = new AndroidDevice(this.client, this.settings);
        } else if (this.settings.platform == PlatformType.iOS) {
            this.device = new IOSDevice(this.client, this.settings);
        } else {
            String error = "No such device implemented";
            LOGGER_BASE.fatal(error);
            throw new DeviceException(error);
        }

        this.device = this.device.start();

        // Verify app is running.
        this.verifyAppRunning(this.settings.packageId);

        this.logAppStartupTime(this.settings.packageId);

        // Set windowSize
        this.windowSize = this.client.driver.manage().window().getSize();
    }

    /**
     * Stop device (kills emulator/simulator).
     */
    public void stop() {
        this.device.stop();
    }

    /**
     * Push file from host machine to mobile device.
     *
     * @param localPath  Full path to local file.
     * @param remotePath Remote folder.
     * @throws Exception When operation fails.
     */
    public void pushFile(String localPath, String remotePath) throws Exception {
        this.device.pushFile(localPath, remotePath);
    }

    /**
     * Pull file from mobile device to host machine.
     * destinationFolder is relative path based on this.settings.baseLogDir.
     * If destinationFolder is null then file will be saved in this.settings.baseLogDir
     *
     * @param remotePath        Full path to remove file.
     * @param destinationFolder Path to host folder.
     * @throws Exception
     */
    public void pullFile(String remotePath, String destinationFolder) throws Exception {
        this.device.pullFile(remotePath, destinationFolder);
    }

    /**
     * Clean console logs.
     */
    public void cleanConsoleLog() {
        this.device.cleanConsoleLog();
    }

    /**
     * Write console logs in file.
     *
     * @param fileName file name (will be saved in default console log folder).
     * @throws IOException When fails to write in file.
     */
    public void writeConsoleLogToFile(String fileName) throws IOException {
        this.device.writeConsoleLogToFile(fileName);
    }

    /**
     * Assert log contains a string.
     *
     * @param str String that should be available in logs.
     * @throws IOException When fail to write current log (it first write it to disk and then get the content).
     */
    public void assertLogContains(String str) throws IOException {
        String testName = MobileSetupManager.getTestSetupManager().getContext().getTestName();
        String logContent = this.getLogContent(testName);
        Assert.assertTrue(logContent.contains(str), "The log does not contain '" + str + "'.");
        LOGGER_BASE.info("The log contains '" + str + "'.");
    }

    /**
     * Assert log does not contain a string.
     *
     * @param str String that should not be available in logs.
     * @throws IOException When fail to write current log (it first write it to disk and then get the content).
     */
    public void assertLogNotContains(String str) throws IOException {
        String testName = MobileSetupManager.getTestSetupManager().getContext().getTestName();
        String logContent = this.getLogContent(testName);
        Assert.assertFalse(logContent.contains(str), "The log contains '" + str + "'.");
        LOGGER_BASE.info("The log does not contains '" + str + "'.");
    }

    /**
     * Verify app is running.
     *
     * @param appId Package ID of application.
     * @throws MobileAppException
     */
    public void verifyAppRunning(String appId) throws MobileAppException {
        this.device.verifyAppRunning(appId);
    }

    /**
     * Check if app is running.
     *
     * @param appId Package ID of application.
     * @return True if app is running and False when it is not.
     */
    public boolean isAppRunning(String appId) {
        return this.device.isAppRunning(appId);
    }

    /**
     * Startup time of application in ms.
     *
     * @param packageId PackageId of application.
     * @return Application startup time in ms.
     */
    public String getStartupTime(String packageId) {
        return this.device.getStartupTime(packageId);
    }

    /**
     * Memory usage of application in kB.
     *
     * @param packageId PackageId of application.
     * @return Current application memory usage in kB.
     */
    public int getMemUsage(String packageId) {
        return this.device.getMemUsage(packageId);
    }

    /**
     * Log perforamcne info such as memory usage, load time, app size in CSV file.
     *
     * @throws IOException When fail to write in csv file.
     */
    public void logPerfInfo() throws IOException {
        this.device.logPerfInfo();
    }

    /**
     * Log startup time info in CSV file. IOException is catched if fails to write in csv file or to get startup time.
     */
    public void logAppStartupTime(String packageId) {
        this.device.logAppStartupTime(packageId);
    }

    /**
     * Get console logs during execution of current test.
     *
     * @return Console log as string
     * @throws IOException When log fail to be writen in file.
     */
    private String getLogContent(String testName) throws IOException {
        // TODO(): Can we somehow get log without writing in file?
        // Now we
        this.writeConsoleLogToFile(testName);
        String logContent = this.device.getContent(testName);

        return logContent;
    }

    /**
     * Restart app under test.
     */
    public void restartApp() {
        this.device.restartApp();
    }

    /**
     * Uninstall user application.
     */
    public void uninstallApps() {
        this.device.stopApps(uninstallAppsList());
    }

    /**
     * Set geo location.
     *
     * @param location Geo location.
     */
    public void setLocation(Location location) {
        this.device.setLocation(location);
    }

    /**
     * Run current application in background.
     *
     * @param seconds
     */
    public void runAppInBackground(int seconds) {
        LOGGER_BASE.info("Run current app in background for " + seconds + " seconds.");
        this.device.runAppInBackGround(seconds);
        LOGGER_BASE.info("Bring " + this.settings.packageId + " to front.");
    }

    /**
     * Close current app.
     */
    public void closeApp() {
        this.device.closeApp();
    }

    /**
     * List of user apps (apps that are safe to be uninstalled.
     *
     * @return List of package ids.
     */
    public static List<String> uninstallAppsList() {
        // TODO(dtopuzov): Do not use hardcoded app names.
        return Arrays.asList("org.nativescript", "com.telerik");
    }

    /**
     * Get mobile device window size.
     *
     * @return Window size.
     */
    public Dimension getWindowSize() {
        return this.windowSize;
    }


    /**
     * Get current screen as BufferedImage.
     *
     * @return BufferedImage of mobile device. Null if getScreenshot fails.
     */
    public BufferedImage getScreenshot() {
        try {
            File screen = this.client.driver.getScreenshotAs(OutputType.FILE);
            return ImageIO.read(screen);
        } catch (Exception e) {
            LOGGER_BASE.error("Failed to take screenshot! May be appium driver is dead.");
            return null;
        }
    }

    /**
     * Rotate the device.
     *
     * @param screenOrientation
     */
    public void rotate(ScreenOrientation screenOrientation) {
        this.client.driver.rotate(screenOrientation);
    }

    public void hideKeyboard() {
        MobileContext mobileContext = MobileSetupManager.getTestSetupManager().getContext();
        if (this.settings.platform == PlatformType.Android) {
            try {
                this.client.getDriver().hideKeyboard();
                LOGGER_BASE.info("Hide heyboard.");
            } catch (Exception e) {
                LOGGER_BASE.info("Soft keyboard not present.");
            }
        } else {
            By doneButtonLocator = By.id("Done");
            By returnButtonLocator = By.id("Return");
            UIElement doneButton = mobileContext.find.byLocator(doneButtonLocator, 1);
            if (doneButton != null) {
                ((IOSDriver) mobileContext.client.getDriver()).hideKeyboard(HideKeyboardStrategy.PRESS_KEY, "Done");
                LOGGER_BASE.info("Hide keyboard with Done key.");
            } else if (mobileContext.find.byLocator(returnButtonLocator, 1) != null) {
                ((IOSDriver) mobileContext.client.getDriver()).hideKeyboard(HideKeyboardStrategy.PRESS_KEY, "Return");
                LOGGER_BASE.info("Hide keyboard with Return key.");
            } else {
                ((IOSDriver) mobileContext.client.getDriver()).hideKeyboard(HideKeyboardStrategy.TAP_OUTSIDE);
                LOGGER_BASE.info("Hide keyboard with tap outside.");
            }
        }
    }
}
