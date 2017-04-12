package functional.tests.core.mobile.appium;

import functional.tests.core.enums.PlatformType;
import functional.tests.core.log.Log;
import functional.tests.core.log.LoggerBase;
import functional.tests.core.mobile.device.IDevice;
import functional.tests.core.mobile.find.Wait;
import functional.tests.core.mobile.settings.MobileSettings;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.concurrent.TimeUnit;

/**
 * Appium Client.
 */
public class Client {

    private static final LoggerBase LOGGER_BASE = LoggerBase.getLogger("Client");

    public Server server;
    public MobileSettings settings;
    public AppiumDriver<?> driver;

    /**
     * Initialize Appium client object.
     *
     * @param server   Appium server instance.
     * @param settings Current settings.
     */
    public Client(Server server, MobileSettings settings) {
        this.server = server;
        this.settings = settings;
    }

    /**
     * Get current Appium driver.
     *
     * @return Current Appium driver.
     */
    public AppiumDriver<?> getDriver() {
        return this.driver;
    }

    /**
     * Start Appium driver.
     */
    public void initDriver() {

        LOGGER_BASE.info("Start appium client ...");

        // Verify service
        if (this.server.service == null || !this.server.service.isRunning()) {
            String error = "Appium server not running!";
            LOGGER_BASE.fatal(error);
            throw new RuntimeException(error);
        }

        // Load capabilities
        Capabilities capabilitiesLoader = new Capabilities();

        IDevice device = null;
        // Start AndroidDriver
        if (this.settings.platform == PlatformType.Android) {
            DesiredCapabilities capabilities = capabilitiesLoader.loadAndroidCapabilities(this.settings);
            LOGGER_BASE.info(capabilities.toString());
            try {
                this.driver = new AndroidDriver<>(this.server.service.getUrl(), capabilities);
            } catch (Exception e) {
                LOGGER_BASE.fatal(e.getMessage());
            }
        }

        // Start IOSDriver
        if (this.settings.platform == PlatformType.iOS) {
            DesiredCapabilities capabilities = capabilitiesLoader.loadIOSCapabilities(this.settings);
            LOGGER_BASE.info(capabilities.toString());
            try {
                this.driver = new IOSDriver<>(this.server.service.getUrl(), capabilities);
            } catch (Exception e) {
                LOGGER_BASE.fatal(e.getMessage());
            }
        }

        // Set default timeout
        if (this.driver != null) {
            this.driver.manage().timeouts().implicitlyWait(this.settings.defaultTimeout, TimeUnit.SECONDS);
            LOGGER_BASE.info("appium client started.");
        } else {
            String error = "Appium client failed to start!";
            LOGGER_BASE.fatal(error);
            Log.logScreenOfHost(this.settings, "failed to start appium driver");

            throw new RuntimeException(error);
        }
    }

    /**
     * Stop Appium driver.
     */
    public void stopDriver() {

        LOGGER_BASE.info("Stop appium client ...");

        if (this.driver != null) {
            try {
                this.driver.quit();
                LOGGER_BASE.info("appium client stopped.");
            } catch (Exception e) {
                LOGGER_BASE.fatal("Failed to stop appium client!");
            }
        } else {
            LOGGER_BASE.info("appium client already stopped.");
        }
    }

    /**
     * Set implicit wait in seconds.
     *
     * @param seconds
     */
    public void setWait(int seconds) {
        this.driver.manage().timeouts().implicitlyWait(seconds, TimeUnit.SECONDS);
        // When call very fast setWait and find sometimes server first receive find command and then setWait command.
        // Hope this hack will make the framework more stable.
        Wait.sleep(250);
    }
}
