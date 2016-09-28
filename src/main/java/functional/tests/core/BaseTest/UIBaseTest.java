package functional.tests.core.BaseTest;

import functional.tests.core.App.App;
import functional.tests.core.Appium.Client;
import functional.tests.core.Appium.Server;
import functional.tests.core.Device.Device;
import functional.tests.core.Device.iOS.Simctl;
import functional.tests.core.Enums.DeviceType;
import functional.tests.core.Find.FindHelper;
import functional.tests.core.Gestures.GesturesHelper;
import functional.tests.core.ImageProcessing.Sikuli.Sikuli;
import functional.tests.core.Log.Log;
import functional.tests.core.OSUtils.FileSystem;
import functional.tests.core.Settings.Settings;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.io.IOException;
import java.lang.reflect.Method;

public abstract class UIBaseTest extends BaseTest {

    private static boolean failAtStartUp = false;
    private static boolean isFistTest = true;
    private static Device staticDevice;
    public Client client;
    public Device device;
    public FindHelper find;
    public GesturesHelper gestures;
    public TestsStateManager testsStateManager;
    private Sikuli skulkImageProcessing;

    public static Device baseDevice() {
        return staticDevice;
    }

    private static void checkAppiumLogsForCrash() {
        try {
            String appiumLog = FileSystem.readFile(Settings.appiumLogFile);
            String[] lines = appiumLog.split("\\r?\\n");
            for (String line : lines) {
                if (line.contains("IOS_SYSLOG_ROW") && line.contains("crashed.")) {
                    Log.fatal("App crashes at startup. Please see appium logs.");
                }
            }
        } catch (IOException e) {
            Log.info("Failed to check appium log files.");
        }
    }

    protected Sikuli sikuliImagePorcessing() {
        return this.skulkImageProcessing;
    }

    @BeforeSuite(alwaysRun = true)
    public void beforeSuiteUIBaseTest() throws Exception {
        this.device = new Device();
        staticDevice = this.device;

        if (!Settings.debug) {
            this.device.stopDevice();
            this.device.initDevice();
            this.device.initTestApp();
        }

        try {
            Server.initAppiumServer();
            Client.initAppiumDriver();
            this.initHelpers();
        } catch (Exception e) {
            checkAppiumLogsForCrash();
            takeScreenOfHost("HostOS_Failed_To_Init_Appium_Session");
            Log.info("Retry initializing appium server and client");
            Settings.appiumLogLevel = "debug";
            Settings.deviceBootTimeout = Settings.deviceBootTimeout * 2;
            try {
                try {
                    String log = Server.service.getStdOut();
                    if (log != null) {
                        Log.separator();
                        Log.info(log);
                        Log.separator();
                    } else {
                        Log.error("Server log not available!");
                    }
                } catch (Exception ex) {
                    Log.error("Failed to get appium logs.");
                }
                Client.stopAppiumDriver();
                Server.stopAppiumServer();
                Server.initAppiumServer();
                Client.initAppiumDriver();
            } catch (Exception re) {
                try {
                    takeScreenOfHost("HostOS_Failed_To_Init_Appium_Session_After_Retry");
                    String log = Server.service.getStdOut();
                    if (log != null) {
                        Log.separator();
                        Log.info(log);
                        Log.separator();
                    } else {
                        Log.error("Server log not available!");
                    }
                } catch (Exception ex) {
                    Log.error("Failed to get appium logs.");
                }
                checkAppiumLogsForCrash();
                String error = "Failed to init Appium session. Please see Appium logs.";
                Log.fatal(error);
                Log.info(re.toString());
                Log.info(re.getStackTrace().toString());
                throw re;
            }
        }

        // Verify app not crashed
        try {
            this.device.verifyAppRunning(Settings.deviceId, Settings.packageId);
        } catch (Exception e) {
            failAtStartUp = true;
            Log.logScreen("Emulator", Settings.packageId + " failed at startup.");
            takeScreenOfHost("HostOS");
            throw e;
        }

        // Get logs for initial app startup
        this.device.writeConsoleLogToFile("init");
    }

    @BeforeClass(alwaysRun = true)
    public void beforeClassUIBaseTest() {
        this.initHelpers();
    }

    @BeforeMethod(alwaysRun = true)
    public void beforeMethodUIBaseTest(Method method) throws Exception {
        if (this.device == null) {
            this.device = new Device();
        }

        if (previousTestStatus == ITestResult.FAILURE) {
            try {
                App.fullRestart();
            } catch (Exception e1) {
                Log.info("Failed to restart test app. Rests Apppium client/server.");
                Server.stopAppiumServer();
                this.device.stopTestApp();
                this.device.stopDevice();
                Server.initAppiumServer();
                Client.initAppiumDriver();
                isFistTest = true;
                // Verify app not crashed
                try {
                    this.device.verifyAppRunning(Settings.deviceId, Settings.packageId);
                } catch (Exception e2) {
                    Log.logScreen("Emulator", Settings.packageId + " failed at startup.");
                    takeScreenOfHost("HostOS");
                    throw e2;
                }
            }

            if (this.testsStateManager != null && this.testsStateManager.status != ITestResult.SUCCESS) {
                this.testsStateManager.resetNavigationToLastOpenedPage();
            } else {
                Log.error("TestStateManager is: " + this.testsStateManager + " in beforeMethodUIBaseTest!");
            }
        }

        if (isFistTest) {
            isFistTest = false;
        } else {
            if (Settings.restartApp) {
                if (Settings.deviceType == DeviceType.Simulator) {
                    Simctl.reinstallApp();
                    Client.stopAppiumDriver();
                    Client.initAppiumDriver();
                } else {
                    App.fullRestart();
                }
            }
        }
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethodUIBaseTest(ITestResult result) throws IOException {

        // Get test case name
        String testCase = result.getMethod().getMethodName();

        if (this.device == null) {
            Log.error("The device is null");
        } else {
            this.device.writeConsoleLogToFile(testCase);
        }

        if (this.testsStateManager != null) {
            this.testsStateManager.status = result.getStatus();
        }
    }

    @AfterClass(alwaysRun = true)
    public void afterClassUIBaseTest() {
        if (this.testsStateManager != null) {
            Log.info("Navigate to home page in afterClassUIBaseTest!");
            this.testsStateManager.navigateToHomePage();
        }
    }

    @AfterSuite(alwaysRun = true)
    public void afterSuiteUIBaseTest() throws Exception {
        if (this.testsStateManager != null) {
            this.testsStateManager.navigateToHomePage();
        }

        Client.stopAppiumDriver();

        if (!Settings.debug) {
            Server.stopAppiumServer();
            this.device.stopTestApp();
            this.device.stopDevice();
        }
    }

    private void initHelpers() {
        if (this.client == null)
            this.client = new Client();
        if (this.find == null)
            this.find = new FindHelper(this.client);
        if (this.gestures == null)
            this.gestures = new GesturesHelper(this.client);
        if (this.testsStateManager == null)
            this.testsStateManager = new TestsStateManager(this.client);
        if (this.skulkImageProcessing == null)
            this.skulkImageProcessing = new Sikuli(BaseTest.getAppName(), this.client);
    }
}