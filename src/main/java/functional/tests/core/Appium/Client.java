package functional.tests.core.Appium;

import functional.tests.core.Device.Android.Adb;
import functional.tests.core.Enums.PlatformType;
import functional.tests.core.Find.Wait;
import functional.tests.core.Log.Log;
import functional.tests.core.Settings.Settings;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.concurrent.TimeUnit;

public class Client {

    public static AppiumDriver<?> driver;

    public AppiumDriver<?> getDriver(){
        return driver;
    }

    public static void initAppiumDriver() {

        Log.info("Start Appium client ...");

        // Verify service
        if (Server.service == null || !Server.service.isRunning()) {
            Log.fatal("Appium service is null or not running!");
            throw new RuntimeException("Appium service is null or not running!");
        }

        // Load capabilities
        Capabilities capabilitiesLoader = new Capabilities();
        DesiredCapabilities capabilities = capabilitiesLoader.loadDesiredCapabilities();

        // Start AndroidDriver
        if (Settings.platform == PlatformType.Andorid) {
            try {
                driver = new AndroidDriver<>(Server.service.getUrl(), capabilities);
                if (Adb.isLocked(Settings.deviceId)) {
                    Adb.unlock(Settings.deviceId);
                }
            } catch (Exception e) {
                // Sometimes Appium fails to unlock device
                if (e.toString().contains("Screen did not unlock")) {
                    Adb.unlock(Settings.deviceId);
                    driver = new AndroidDriver<>(Server.service.getUrl(), capabilities);
                }
            }
        }

        // Start IOSDriver
        if (Settings.platform == PlatformType.iOS) {
            driver = new IOSDriver<>(Server.service.getUrl(), capabilities);
        }

        // Set default timeout
        if (driver != null) {
            driver.manage().timeouts().implicitlyWait(Settings.defaultTimeout, TimeUnit.SECONDS);
            Log.info("Appium client started.");
        } else {
            Log.fatal("Driver is null! Appium client failed to start!");
            throw new RuntimeException("Appium client failed to start!");
        }
    }

    public static void stopAppiumDriver() {

        Log.info("Stop Appium client ...");

        if (driver != null) {
            try {
                driver.quit();
                Log.info("Appium client stopped.");
            } catch (Exception e) {
                Log.fatal("Failed to stop Appium client!");
            }
        } else {
            Log.info("Appium client already stopped.");
        }
    }

    /**
     * Set implicit wait in seconds *
     */
    public static void setWait(int seconds) {
        driver.manage().timeouts().implicitlyWait(seconds, TimeUnit.SECONDS);
        // When call very fast setWait and find sometimes server first receive find command and then setWait command.
        // Hope this hack will make the framework more stable
        Wait.sleep(250);
    }

    public static void startActivity(String appPackage, String appActivity) {
        ((AndroidDriver) Client.driver).startActivity(appPackage, appActivity);
    }

    public int getDensityRation(int screenshotWidth) {
        if (Settings.platform == PlatformType.iOS) {
            return screenshotWidth / this.getDriver().manage().window().getSize().width;
        } else {
            return 1;
        }
    }
}
