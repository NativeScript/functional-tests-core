package functional.tests.core.mobile.basetest;

import functional.tests.core.exceptions.AppiumException;
import functional.tests.core.exceptions.DeviceException;
import functional.tests.core.exceptions.MobileAppException;
import functional.tests.core.image.ImageUtils;
import functional.tests.core.image.Sikuli;
import functional.tests.core.log.Log;
import functional.tests.core.log.LoggerBase;
import functional.tests.core.mobile.app.App;
import functional.tests.core.mobile.appium.Client;
import functional.tests.core.mobile.appium.Server;
import functional.tests.core.mobile.device.Device;
import functional.tests.core.mobile.find.Find;
import functional.tests.core.mobile.find.Locators;
import functional.tests.core.mobile.find.UIElementClass;
import functional.tests.core.mobile.find.Wait;
import functional.tests.core.mobile.gestures.Gestures;
import functional.tests.core.mobile.settings.MobileSettings;
import functional.tests.core.utils.FileSystem;
import org.testng.ITestResult;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * TODO(svetli): Add docs.
 */
public class MobileSetupManager {

    private static final LoggerBase LOGGER_BASE = LoggerBase.getLogger("MobileSetupManager");
    protected static Map<String, MobileSetupManager> initSettings;
    private MobileContext context;
    private MobileSettings settings;
    private Log log;
    private Locators locators;
    private Server server;
    private Client client;
    private Device device;
    private ImageUtils imageUtils;
    private App app;
    private Find find;
    private Wait wait;
    private Gestures gestures;
    private Sikuli sikuliImageProcessing;
    private UIElementClass uiElements;

    private MobileSetupManager() {
    }

    /**
     * Init only once per configuration.
     *
     * @return Instance of MobileSetupManager.
     */
    public static MobileSetupManager initTestSetupBasic(boolean shouldStartLog4j) {
        MobileSetupManager mobileSetupManager;
        if (MobileSetupManager.initSettings == null) {
            if (shouldStartLog4j) {
                LoggerBase.initLog4j();
            }
            MobileSetupManager.initSettings = new HashMap<>();
        }

        if (!MobileSetupManager.initSettings.containsKey(getAppConfig())) {
            mobileSetupManager = new MobileSetupManager();
            mobileSetupManager.settings = new MobileSettings();
            mobileSetupManager.log = new Log();
            mobileSetupManager.uiElements = new UIElementClass(mobileSetupManager.settings);
            mobileSetupManager.locators = new Locators(mobileSetupManager.settings);
            mobileSetupManager.server = new Server(mobileSetupManager.settings);
            mobileSetupManager.client = new Client(mobileSetupManager.server, mobileSetupManager.settings);
            mobileSetupManager.device = new Device(mobileSetupManager.client, mobileSetupManager.settings);
            mobileSetupManager.imageUtils = new ImageUtils(mobileSetupManager.settings, mobileSetupManager.client, mobileSetupManager.device);
            mobileSetupManager.log = new Log(mobileSetupManager.client, mobileSetupManager.settings);
            mobileSetupManager.app = new App(mobileSetupManager.device, mobileSetupManager.settings);
            mobileSetupManager.find = new Find(mobileSetupManager.client, mobileSetupManager.locators, mobileSetupManager.settings);
            mobileSetupManager.wait = new Wait(mobileSetupManager.client, mobileSetupManager.find, mobileSetupManager.settings);

            mobileSetupManager.gestures = new Gestures(
                    mobileSetupManager.client,
                    mobileSetupManager.wait,
                    mobileSetupManager.device,
                    mobileSetupManager.locators,
                    mobileSetupManager.settings);

            mobileSetupManager.sikuliImageProcessing =
                    new Sikuli(mobileSetupManager.settings.testAppName + "-map",
                            mobileSetupManager.client,
                            mobileSetupManager.imageUtils);

            mobileSetupManager.context = new MobileContext(
                    mobileSetupManager.settings,
                    mobileSetupManager.log,
                    mobileSetupManager.client,
                    mobileSetupManager.server,
                    mobileSetupManager.device,
                    mobileSetupManager.sikuliImageProcessing,
                    mobileSetupManager.app,
                    mobileSetupManager.find,
                    mobileSetupManager.gestures,
                    mobileSetupManager.imageUtils,
                    mobileSetupManager.locators,
                    mobileSetupManager.wait,
                    mobileSetupManager.uiElements);

            mobileSetupManager.context.lastTestResult = ITestResult.SUCCESS;

            MobileSetupManager.initSettings.put(getAppConfig(), mobileSetupManager);
        } else {
            mobileSetupManager = MobileSetupManager.initSettings.get(getAppConfig());
        }

        return mobileSetupManager;
    }

    /**
     * Get the MobileSetupManager for the current instance according to app config.
     *
     * @return
     */
    public static MobileSetupManager getTestSetupManager() {
        return MobileSetupManager.initSettings.get(MobileSetupManager.getAppConfig());
    }

    /**
     * Get current app configuration.
     *
     * @return
     */
    public static String getAppConfig() {
        return System.getProperty("appConfig");
    }

    public MobileContext getContext() {
        return this.context;
    }

    /**
     * Init Appium Server.
     */
    public void initServer() throws IOException, AppiumException {
        this.context.server.initServer();
    }

    /**
     * Stop app under test, appium client and server.
     */
    public void stopSession() throws DeviceException {
        this.context.app.close();
        this.context.client.stopDriver();
        this.context.server.stopServer();
    }


    public void restartSession() throws MobileAppException, DeviceException, TimeoutException, IOException, AppiumException {
        this.stopSession();
        this.initServer();
        this.initDevice();
    }

    /**
     * Init device - ensure it is up and running and test app is deployed.
     * This will also start Appium client session.
     *
     * @throws MobileAppException When app can not be deployed or started.
     * @throws DeviceException    When device is not attached or emulator can not start.
     * @throws TimeoutException   When device can not be started in appropriate time.
     */
    public void initDevice() throws MobileAppException, DeviceException, TimeoutException {
        try {
            this.context.device.start();
        } catch (Exception ex) {
            Log.logScreenOfHost(this.settings, "onStartDevice");
            throw ex;
        }
    }

    /**
     * Log test results.
     *
     * @param previousTestStatus Outcome of the test.
     * @param testCase           Test name.
     */
    public void logTestResult(int previousTestStatus, String testCase) {
        if (this.context.device == null) {
            LOGGER_BASE.error("The device is null");
        } else {
            try {
                this.context.device.writeConsoleLogToFile(testCase);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (previousTestStatus == ITestResult.SUCCESS) {
            if (this.context.settings.takeScreenShotAfterTest) {
                this.context.log.logScreen(testCase + "_pass", "Screenshot after " + testCase);
            }
            this.log.info("=> Test " + testCase + " passed!");
        } else if (previousTestStatus == ITestResult.FAILURE) {
            this.context.log.logScreen(testCase + "_fail", "Screenshot after " + testCase);
            this.context.log.saveXmlTree(testCase + "_VisualTree.xml");
            this.log.error("=> Test " + testCase + " failed!");
        } else if (this.context.lastTestResult == ITestResult.SKIP) {
            this.context.log.logScreen(testCase + "_skip", "Screenshot after " + testCase);
            this.log.error("=> Test " + testCase + " skipped!");
        }
    }

    /**
     * Check Appium server log iOS crashes.
     * Note: Implemented only for iOS.
     */
    private void checkAppiumServerLogs() {
        try {
            String appiumLog = FileSystem.readFile(this.context.settings.appiumLogFile);
            String[] lines = appiumLog.split("\\r?\\n");
            for (String line : lines) {
                if (line.contains("IOS_SYSLOG_ROW") && line.contains("crashed.")) {
                    LOGGER_BASE.fatal("App crashes at startup. Please see appium logs.");
                }
            }
        } catch (IOException e) {
            LOGGER_BASE.info("Failed to check appium log files.");
        }
    }
}
