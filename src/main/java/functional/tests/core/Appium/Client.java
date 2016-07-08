package functional.tests.core.Appium;

import functional.tests.core.Device.Android.Adb;
import functional.tests.core.Enums.DeviceType;
import functional.tests.core.Enums.PlatformType;
import functional.tests.core.Find.Wait;
import functional.tests.core.Log.Log;
import functional.tests.core.Settings.Settings;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.remote.MobileCapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class Client {

    public static AppiumDriver<?> driver;

    public Client() {

    }

    public AppiumDriver<?> getDriver() {
        return driver;
    }

    public static void initAppiumDriver() {

        Log.info("Start Appium client...");

        if (Server.service == null || !Server.service.isRunning()) {
            Log.fatal("An appium server node is not started!");
            throw new RuntimeException("An appium server node is not started!");
        }

        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability(MobileCapabilityType.APPIUM_VERSION, Settings.appiumVersion);
        capabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION, Settings.platformVersion);
        capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, Settings.platform);
        capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, Settings.deviceName);
        capabilities.setCapability(MobileCapabilityType.APP, Settings.baseTestAppDir + File.separator + Settings.testAppName);
        capabilities.setCapability(MobileCapabilityType.NEW_COMMAND_TIMEOUT, Settings.deviceBootTimeout);
        capabilities.setCapability(MobileCapabilityType.LAUNCH_TIMEOUT, true);

        // Set UDID for real devices
        if ((Settings.deviceType == DeviceType.iOS) || (Settings.deviceType == DeviceType.Android)) {
            capabilities.setCapability(MobileCapabilityType.UDID, Settings.deviceId);
        }

        // Device Specific Settings
        if (Settings.platform == PlatformType.Andorid) {
            try {
                capabilities.setCapability("noSign", "true");
                capabilities.setCapability(MobileCapabilityType.APP_WAIT_PACKAGE, Settings.packageId);
                capabilities.setCapability(MobileCapabilityType.APP_WAIT_ACTIVITY, Settings.defaultActivity);
                driver = new AndroidDriver<>(Server.service.getUrl(), capabilities);
                if (Adb.isLocked(Settings.deviceId)) {
                    Adb.unlock(Settings.deviceId);
                }
            } catch (Exception e) {
                // Some times Appium fails to unlock device
                if (e.toString().contains("Screen did not unlock")) {
                    Adb.unlock(Settings.deviceId);
                    driver = new AndroidDriver<>(Server.service.getUrl(), capabilities);
                }
            }
        }

        // iOS Specific Settings
        if (Settings.platform == PlatformType.iOS) {
            capabilities.setCapability("screenshotWaitTimeout", Settings.defaultTimeout);
            capabilities.setCapability("autoAcceptAlerts", Settings.acceptAlerts);
            capabilities.setCapability("launchTimeout", Settings.deviceBootTimeout * 1000);
            driver = new IOSDriver<>(Server.service.getUrl(), capabilities);
        }

        // Set default timeout
        driver.manage().timeouts().implicitlyWait(Settings.defaultTimeout, TimeUnit.SECONDS);

        Log.info("Appium client started.");
    }

    public static void stopAppiumDriver() {

        Log.info("Stop Appium client...");

        if (driver != null) {
            try {
                driver.quit();
                Log.info("Appium client stopped.");
            } catch (Exception e) {
                Log.fatal("Failed to stop Appium client.");
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
