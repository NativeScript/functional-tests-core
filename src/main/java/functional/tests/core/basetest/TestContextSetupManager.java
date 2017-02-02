package functional.tests.core.basetest;

import functional.tests.core.app.App;
import functional.tests.core.appium.Client;
import functional.tests.core.appium.Server;
import functional.tests.core.device.Device;
import functional.tests.core.find.Find;
import functional.tests.core.find.Locators;
import functional.tests.core.find.Wait;
import functional.tests.core.gestures.Gestures;
import functional.tests.core.image.ImageUtils;
import functional.tests.core.image.Sikuli;
import functional.tests.core.log.Log;
import functional.tests.core.log.LoggerBase;
import functional.tests.core.settings.Settings;
import org.testng.ITestResult;

import java.util.HashMap;
import java.util.Map;

/**
 * Setup all dependencies of the tests context.
 */
public class TestContextSetupManager {

    private static final LoggerBase LOGGER_BASE = LoggerBase.getLogger("TestContextSetupManager");

    protected static Map<String, TestContextSetupManager> initSettings;
    public Log log;
    public Settings settings;
    public Locators locators;
    public Client client;
    public Server server;
    public App app;
    public Find find;
    public Gestures gestures;
    public Device device;
    public Sikuli sikuliImageProcessing;
    public Wait wait;
    public ImageUtils imageUtils;

    public Context context;

    private TestContextSetupManager() {
        this.context = new Context();
    }


    /**
     * Init only once in per configuration.
     *
     * @return Instance of TestContextSetupManager.
     */
    public static TestContextSetupManager initTestSetupBasic() {
        TestContextSetupManager testContextSetupManager;
        if (TestContextSetupManager.initSettings == null) {
            TestContextSetupManager.initSettings = new HashMap<>();
            LoggerBase.initLog4j();
        }

        if (!TestContextSetupManager.initSettings.containsKey(getAppConfig())) {
            testContextSetupManager = new TestContextSetupManager();
            testContextSetupManager.initSettings();
            testContextSetupManager.initLog();
            testContextSetupManager.context.lastTestResult = ITestResult.SUCCESS;
            TestContextSetupManager.initSettings.put(getAppConfig(), testContextSetupManager);
        } else {
            testContextSetupManager = TestContextSetupManager.initSettings.get(getAppConfig());
        }

        return testContextSetupManager;
    }

    /**
     * Get current app configuration.
     *
     * @return
     */
    public static String getAppConfig() {
        return System.getProperty("appConfig");
    }

    /**
     * Init tests context.
     *
     * @return
     */
    public Context initUITestSetup() {
        if (this.client == null) {
            this.initLocators();
            this.initServer();
            this.initClient();
            this.initImageUtils();
            this.initDevice();
            this.initLog(this.context);
            this.initApp();
            this.initFind();
            this.initGestures();
            this.initSikuliImageProcessing();
            this.initWait();
        }

        return this.context;
    }

    /**
     * TODO(svetli): Add docs.
     *
     * @return
     */
    private ImageUtils initImageUtils() {
        this.imageUtils = new ImageUtils(this.context);

        return this.imageUtils;
    }

    /**
     * Get the TestContextSetupManager for the current instance according to app config.
     *
     * @return
     */
    public static TestContextSetupManager getTestSetupManager() {
        return TestContextSetupManager.initSettings.get(TestContextSetupManager.getAppConfig());
    }

    /**
     * Init settings.
     *
     * @return
     */
    public Settings initSettings() {
        this.settings = new Settings();
        this.updateTestContext();

        return this.settings;
    }

    /**
     * Init Log class.
     *
     * @param context
     * @return Log
     */
    public Log initLog(Context context) {
        this.log = new Log(context);
        this.updateTestContext();
        return this.log;
    }

    /**
     * Init Locators.
     *
     * @return
     */
    public Locators initLocators() {
        this.locators = new Locators(this.settings);
        this.updateTestContext();
        return this.locators;
    }

    /**
     * Init Server.
     *
     * @return
     */
    public Server initServer() {
        this.server = new Server(this.settings);
        this.updateTestContext();
        return this.server;
    }

    /**
     * Init client.
     *
     * @return
     */
    public Client initClient() {
        this.client = new Client(this.server, this.settings);
        this.updateTestContext();
        return this.client;
    }

    /**
     * Init device.
     *
     * @return
     */
    public Device initDevice() {
        this.device = new Device(this.context);
        this.updateTestContext();
        return this.device;
    }

    /**
     * Init app.
     *
     * @return
     */
    public App initApp() {
        this.app = new App(this.context);
        this.updateTestContext();
        return this.app;
    }

    /**
     * Init Find.
     *
     * @return
     */
    public Find initFind() {
        this.find = new Find(this.context);
        this.updateTestContext();
        return this.find;
    }

    /**
     * Init Gestures.
     *
     * @return
     */
    public Gestures initGestures() {
        this.gestures = new Gestures(this.context);
        this.updateTestContext();
        return this.gestures;
    }

    /**
     * Init sikuli image processing.
     *
     * @return
     */
    public Sikuli initSikuliImageProcessing() {
        this.sikuliImageProcessing = new Sikuli(this.settings.testAppImageFolder + "map");
        this.updateTestContext();
        return this.sikuliImageProcessing;
    }

    /**
     * Init wait.
     *
     * @return
     */
    public Wait initWait() {
        this.wait = new Wait(this.context);
        this.updateTestContext();
        return this.wait;
    }

    /**
     * Inti Log.
     *
     * @return
     */
    private Log initLog() {
        this.log = new Log();
        this.updateTestContext();
        return this.log;
    }

    /**
     * Updates the test context properties.
     */
    private void updateTestContext() {
        this.context.settings = this.settings;
        this.context.log = this.log;
        this.context.locators = this.locators;
        this.context.server = this.server;
        this.context.client = this.client;
        this.context.device = this.device;
        this.context.app = this.app;
        this.context.find = this.find;
        this.context.wait = this.wait;
        this.context.gestures = this.gestures;
        this.context.imageUtils = this.imageUtils;
        this.context.sikuliImageProcessing = this.sikuliImageProcessing;
    }
}
