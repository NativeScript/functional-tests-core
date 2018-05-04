package functional.tests.core.mobile.app;

import functional.tests.core.enums.PlatformType;
import functional.tests.core.enums.Position;
import functional.tests.core.enums.SwipeElementDirection;
import functional.tests.core.log.LoggerBase;
import functional.tests.core.mobile.basetest.MobileSetupManager;
import functional.tests.core.mobile.device.Device;
import functional.tests.core.mobile.helpers.NavigationHelper;
import functional.tests.core.mobile.settings.MobileSettings;

import java.awt.*;

/**
 * Application.
 */
public class App {

    private static final LoggerBase LOGGER_BASE = LoggerBase.getLogger("App");
    private Device device;
    private MobileSettings settings;

    /**
     * Initialize Application object.
     *
     * @param device   Device object.
     * @param settings MobileSettings object.
     */
    public App(Device device, MobileSettings settings) {
        this.device = device;
        this.settings = settings;
    }

    /**
     * Get id of current application under test.
     *
     * @return PackageId of app under test.
     */
    public String getId() {
        return this.settings.packageId;
    }

    /**
     * Get name of application under test.
     *
     * @return Name of application under test.
     */
    public String getName() {
        return this.settings.testAppFileName;
    }

    /**
     * TODO(): This does not work! Tested with Cuteness on Android.
     * Steps: Go to About page and call slideBack() and nothing happens.
     * The problem: We should not use FromCorner.
     * We should swipe back with Y = this.context.getDevice().getWindowSize().height/2
     */
    public void slideBack() {
        if (this.settings.platform == PlatformType.iOS) {
            Rectangle window = new Rectangle(0, 120, this.device.getWindowSize().width, this.device.getWindowSize().height);
            MobileSetupManager.getTestSetupManager().getContext().gestures.scrollInRectangle(SwipeElementDirection.LEFT, window, Position.FromCorner, 0, 0, 100);
        } else {
            App.LOGGER_BASE.error("Slide back is implemented only for iOS!!!");
        }
    }

    /**
     * Navigate back (press back button).
     */
    public void navigateBack() {
        NavigationHelper.navigateBack(MobileSetupManager.getTestSetupManager().getContext());
    }

    /**
     * Hides keyboard.
     */
    public void hideKeyboard() {
        this.device.hideKeyboard();
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
        LOGGER_BASE.info("Close the app.");
        this.device.closeApp();
    }
}
