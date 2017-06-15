package functional.tests.core.mobile.basetest;

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
     * Init Appium server wrapper.
     *
     * @return True if server is started successfully.
     */
    public boolean initServer() {
        boolean hasInit = true;
        try {
            this.context.server.initServer();
        } catch (Exception ex) {
            Log.logScreenOfHost(this.settings, "Host_Fail_to_Init_Server");
            hasInit = false;
        }

        return hasInit;
    }

    /**
     * Restart Appium server.
     */
    public void restartServer() {
        // TODO(dtopuzov): It looks this need improvements.
        this.checkLogsForCrash();
        Log.logScreenOfHost(this.settings, "HostOS_Failed_To_Init_Appium_Session");
        LOGGER_BASE.info("Retry initializing appium server and client");
        this.context.settings.appiumLogLevel = "debug";
        this.context.settings.deviceBootTimeout = this.context.settings.deviceBootTimeout * 2;
        try {
            try {
                String log = this.context.server.service.getStdOut();
                if (log != null) {
                    this.context.log.separator();
                    this.context.log.info(log);
                    this.context.log.separator();
                } else {
                    LOGGER_BASE.error("Server log not available!");
                }
            } catch (Exception ex) {
                LOGGER_BASE.error("Failed to get appium logs.");
            }
            this.initServer();
        } catch (Exception re) {
            try {
                Log.logScreenOfHost(this.settings, "HostOS_Failed_To_Init_Appium_Session_After_Retry");
                String log = this.context.server.service.getStdOut();
                if (log != null) {
                    LOGGER_BASE.separator();
                    LOGGER_BASE.info(log);
                    LOGGER_BASE.separator();
                } else {
                    LOGGER_BASE.error("Server log not available!");
                }
            } catch (Exception ex) {
                LOGGER_BASE.error("Failed to get appium logs.");
            }
            this.checkLogsForCrash();
            String error = "Failed to init appium session. Please see appium logs.";
            LOGGER_BASE.fatal(error);
            LOGGER_BASE.info(re.toString());
            LOGGER_BASE.info(re.getStackTrace().toString());
            try {
                throw re;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Stop app under test, appium client and server.
     * If reuseDevice is false, stop emulator/simulator.
     */
    public void fullStop() {
        this.context.app.close();
        this.context.client.stopDriver();
        this.context.server.stopServer();
        if (!this.context.settings.reuseDevice) {
            this.stopDevice();
        }
    }

    /**
     * Restart Appium server and client (only if Appium session is not available).
     */
    public void restartSession() {
        // TODO(dtopuzov): This need refactoring. Do not stop server if it is running.
        this.fullStop(); // For sure we don't need full stop here!
        this.restartServer();
        LOGGER_BASE.info("Failed to restart test app. Resets apppium client/server.");

        try {
            this.startDevice();
        } catch (Exception e2) {
            this.context.log.logScreen("Emulator", this.context.settings.packageId + " failed at startup.");
            Log.logScreenOfHost(this.settings, "HostOS");
        }
    }

    /**
     * TODO(): Add docs.
     *
     * @throws MobileAppException
     * @throws DeviceException
     * @throws TimeoutException
     */
    public void startDevice() throws MobileAppException, DeviceException, TimeoutException {
        try {
            this.context.device.start();
        } catch (Exception ex) {
            Log.logScreenOfHost(this.settings, "onStartDevice");
        }
    }

    /**
     * TODO(): Add docs.
     */
    public void stopDevice() {
        this.context.device.stop();
    }

    /**
     * TODO(): Add docs.
     *
     * @param previousTestStatus
     * @param testCase
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
     * TODO(): Add docs.
     */
    private void checkLogsForCrash() {
        try {
            String appiumLog = FileSystem.readFile(this.context.settings.appiumLogFile);
            String[] lines = appiumLog.split("\\r?\\n");
            for (String line : lines) {
                if (line.contains("IOS_SYSLOG_ROW") && line.contains("crashed.")) {
                    LOGGER_BASE.fatal("app crashes at startup. Please see appium logs.");
                }
            }
        } catch (IOException e) {
            LOGGER_BASE.info("Failed to check appium log files.");
        }
    }
}
