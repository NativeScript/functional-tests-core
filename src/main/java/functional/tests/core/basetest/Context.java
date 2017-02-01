package functional.tests.core.basetest;

import functional.tests.core.app.App;
import functional.tests.core.appium.Client;
import functional.tests.core.appium.Server;
import functional.tests.core.device.Device;
import functional.tests.core.find.Find;
import functional.tests.core.find.Locators;
import functional.tests.core.find.Wait;
import functional.tests.core.gestures.Gestures;
import functional.tests.core.helpers.NavigationManager;
import functional.tests.core.image.ImageUtils;
import functional.tests.core.image.Sikuli;
import functional.tests.core.log.Log;
import functional.tests.core.settings.Settings;

/**
 * Provides access to Server, Client, Device, App and other help classes.
 */
public class Context {

    private String testName;

    public Server server;
    public Settings settings;
    public Wait wait;
    public Find find;
    public Gestures gestures;
    public Log log;
    public Locators locators;
    public ImageUtils imageUtils;
    public Client client;
    public Sikuli sikuliImageProcessing;
    public App app;
    public Device device;
    public boolean shouldRestartAppOnFailure;
    public NavigationManager navigationManager;
    public int lastTestResult;

    /**
     * TODO(svetli): Add docs.
     */
    public Context() {

    }

    /**
     * Init context.
     *
     * @param settings
     * @param log
     * @param client
     * @param device
     * @param sikuliImageProcessing
     * @param app
     * @param find
     * @param gestures
     * @param imageUtils
     * @param locators
     */
    public Context(Settings settings, Log log, Client client, Device device, Sikuli sikuliImageProcessing, App app, Find find, Gestures gestures, ImageUtils imageUtils, Locators locators) {
        this();
        this.settings = settings;
        this.log = log;
        this.client = client;
        this.device = device;
        this.sikuliImageProcessing = sikuliImageProcessing;
        this.app = app;
        this.find = find;
        this.gestures = gestures;
        this.imageUtils = imageUtils;
        this.locators = locators;
        this.shouldRestartAppOnFailure = true;
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
