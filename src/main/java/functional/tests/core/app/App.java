package functional.tests.core.app;

import functional.tests.core.basetest.Context;
import functional.tests.core.device.Device;
import functional.tests.core.element.UIElement;
import functional.tests.core.enums.PlatformType;
import functional.tests.core.enums.Position;
import functional.tests.core.helpers.NavigationHelper;
import functional.tests.core.log.LoggerBase;
import io.appium.java_client.SwipeElementDirection;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.remote.HideKeyboardStrategy;
import org.openqa.selenium.By;

import java.awt.*;

/**
 * Application.
 */
public class App {

    private static final LoggerBase LOGGER_BASE = LoggerBase.getLogger("App");
    private Device device;
    private Context context;

    /**
     * Initialize Application object.
     *
     * @param context
     */
    public App(Context context) {
        this.context = context;
        this.device = context.device;
    }

    /**
     * Get id of current application under test.
     *
     * @return PackageId of app under test.
     */
    public String getId() {
        return this.context.settings.packageId;
    }

    /**
     * Get name of application under test.
     *
     * @return Name of application under test.
     */
    public String getName() {
        return this.context.settings.testAppName;
    }

    /**
     * TODO(): This does not work! Tested with Cuteness on Android.
     * Steps: Go to About page and call slideBack() and nothing happens.
     * The problem: We should not use FromCorner.
     * We should swipe back with Y = this.context.getDevice().getWindowSize().height/2
     */
    public void slideBack() {
        if (this.context.settings.platform == PlatformType.iOS) {
            Rectangle window = new Rectangle(0, 20, this.context.getDevice().getWindowSize().width, this.context.getDevice().getWindowSize().height);
            this.context.gestures.scrollInRectangle(SwipeElementDirection.LEFT, window, Position.FromCorner, 0, 0, 100);
        } else {
            App.LOGGER_BASE.error("Slide back is implemented only for iOS!!!");
        }
    }

    /**
     * Navigate back (press back button).
     */
    public void navigateBack() {
        NavigationHelper.navigateBack(this.context);
    }

    /**
     * Hides keyboard.
     */
    public void hideKeyboard() {
        if (this.context.settings.platform == PlatformType.Andorid) {
            try {
                this.context.client.getDriver().hideKeyboard();
                LOGGER_BASE.info("Hide heyboard.");
            } catch (Exception e) {
                LOGGER_BASE.info("Soft keyboard not present.");
            }
        } else {
            By doneButtonLocator = By.id("Done");
            By returnButtonLocator = By.id("Return");
            UIElement doneButton = this.context.find.byLocator(doneButtonLocator, 1);
            if (doneButton != null) {
                ((IOSDriver) this.context.client.getDriver()).hideKeyboard(HideKeyboardStrategy.PRESS_KEY, "Done");
                LOGGER_BASE.info("Hide keyboard with Done key.");
            } else if (this.context.find.byLocator(returnButtonLocator, 1) != null) {
                ((IOSDriver) this.context.client.getDriver()).hideKeyboard(HideKeyboardStrategy.PRESS_KEY, "Return");
                LOGGER_BASE.info("Hide keyboard with Return key.");
            } else {
                ((IOSDriver) this.context.client.getDriver()).hideKeyboard(HideKeyboardStrategy.TAP_OUTSIDE);
                LOGGER_BASE.info("Hide keyboard with tap outside.");
            }
        }
    }

    /**
     * Restart current application.
     */
    public void restart() {
        this.device.restartApp();
    }

    /**
     * Run current application in background for X seconds.
     */
    public void runInBackground(int seconds) {
        this.device.runAppInBackground(seconds);
    }

    /**
     * Close current application.
     */
    public void close() {
        this.LOGGER_BASE.info("Close the app.");
        this.device.closeApp();
    }
}
