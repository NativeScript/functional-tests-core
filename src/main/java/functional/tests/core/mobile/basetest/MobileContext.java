package functional.tests.core.mobile.basetest;

import functional.tests.core.image.ImageUtils;
import functional.tests.core.log.Log;
import functional.tests.core.mobile.app.App;
import functional.tests.core.mobile.appium.Client;
import functional.tests.core.mobile.appium.Server;
import functional.tests.core.mobile.device.Device;
import functional.tests.core.mobile.find.Find;
import functional.tests.core.mobile.find.Locators;
import functional.tests.core.mobile.find.UIElementClass;
import functional.tests.core.mobile.find.Wait;
import functional.tests.core.mobile.gestures.Gestures;
import functional.tests.core.mobile.helpers.NavigationManager;
import functional.tests.core.mobile.settings.MobileSettings;

/**
 * Provides access to Server, Client,  Device, App and other help classes.
 */
public class MobileContext {

    public UIElementClass uiElementClass;
    public Server server;
    public MobileSettings settings;
    public Wait wait;
    public Find find;
    public Gestures gestures;
    public Log log;
    public Locators locators;
    public ImageUtils imageUtils;
    public Client client;
    public App app;
    public Device device;
    public boolean shouldRestartAppOnFailure;
    public NavigationManager navigationManager;
    public int lastTestResult;
    private String testName;

    /**
     * @param settings
     * @param log
     * @param client
     * @param server
     * @param device
     * @param app
     * @param find
     * @param gestures
     * @param imageUtils
     * @param locators
     * @param wait
     * @param uiElementClass
     */
    public MobileContext(MobileSettings settings, Log log, Client client,
                         Server server, Device device, App app, Find find, Gestures gestures, ImageUtils imageUtils,
                         Locators locators, Wait wait, UIElementClass uiElementClass) {
        this.settings = settings;
        this.log = log;
        this.client = client;
        this.device = device;
        this.app = app;
        this.find = find;
        this.gestures = gestures;
        this.imageUtils = imageUtils;
        this.locators = locators;
        this.shouldRestartAppOnFailure = true;
        this.server = server;
        this.wait = wait;
        this.uiElementClass = uiElementClass;
    }

    /**
     * Get name of current test method.
     *
     * @return name of current test method.
     */
    public String getTestName() {
        return this.testName;
    }

    /**
     * Set name of current test method.
     *
     * @param testName name of current test method.
     */
    public void setTestName(String testName) {
        this.testName = testName;
    }

    /**
     * Get current device.
     *
     * @return Current device.
     */
    public Device getDevice() {
        return this.device;
    }
}
