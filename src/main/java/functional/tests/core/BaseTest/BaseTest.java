package functional.tests.core.BaseTest;

import functional.tests.core.App.App;
import functional.tests.core.Appium.Client;
import functional.tests.core.Appium.Server;
import functional.tests.core.Device.BaseDevice;
import functional.tests.core.Device.iOS.Simctl;
import functional.tests.core.Enums.DeviceType;
import functional.tests.core.Log.Log;
import functional.tests.core.OSUtils.FileSystem;
import functional.tests.core.OSUtils.OSUtils;
import functional.tests.core.Settings.Settings;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import java.io.IOException;
import java.lang.reflect.Method;

public abstract class BaseTest {

    private static boolean failAtStartUp = false;
    private static boolean isFistTest = true;
    private static int previousTestStatus = ITestResult.SUCCESS;

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

    @BeforeSuite(alwaysRun = true)
    public static void beforeClass() throws Exception {
        Log.initLogging();
        Settings.initSettings();

        if (!Settings.debug) {
            BaseDevice.stopDevice();
            BaseDevice.initDevice();
        }

        BaseDevice.initTestApp();

        try {
            Server.initAppiumServer();
            Client.initAppiumDriver();
        } catch (Exception e) {
            checkAppiumLogsForCrash();
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
            BaseDevice.verifyAppRunning(Settings.deviceId, Settings.packageId);
        } catch (Exception e) {
            failAtStartUp = true;
            Log.logScreen("Emulator", Settings.packageId + " failed at startup.");
            takeScreenOfHost("HostOS");
            throw e;
        }

        // Get logs for initial app startup
        BaseDevice.writeConsoleLogToFile("init");
    }

    @BeforeMethod(alwaysRun = true)
    public void setUp(Method method) throws Exception {
        Log.separator();
        Log.info("Start test: " + method.getName());

        if (previousTestStatus == ITestResult.FAILURE) {
            // Start server if it is dead
            if (Server.service == null || !Server.service.isRunning()) {
                Server.initAppiumServer();
            }
            // Start client if it is dead
            if (Client.driver == null) {
                Client.initAppiumDriver();
            }
            // Restart app
            App.fullRestart();
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

    private static void takeScreenOfHost(String fileName) {
        if ((Settings.deviceType == DeviceType.Simulator) || (Settings.deviceType == DeviceType.Emulator)) {
            OSUtils.getScreenshot("HostOS_" + fileName);
        }
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) throws IOException {

        // Get test case name
        String testCase = result.getMethod().getMethodName();

        // Write console log
        BaseDevice.writeConsoleLogToFile(testCase);

        // Report results
        previousTestStatus = result.getStatus();
        if (previousTestStatus == ITestResult.SUCCESS) {
            if (Settings.takeScreenShotAfterTest) {
                Log.logScreen(testCase + "_pass", "Screenshot after " + testCase);
            }
            Log.info("=> Test " + testCase + " passed!");
        } else if (previousTestStatus == ITestResult.SKIP) {
            Log.error("=> Test " + testCase + " skipped!");
        } else if (previousTestStatus == ITestResult.FAILURE) {
            Log.logScreen(testCase + "_fail", "Screenshot after " + testCase);
            Log.saveXmlTree(testCase + "_VisualTree.xml");
            Log.error("=> Test " + testCase + " failed!");
            takeScreenOfHost(testCase);
        }
    }

    @AfterSuite(alwaysRun = true)
    public static void afterClass() throws Exception {
        Client.stopAppiumDriver();

        if (!Settings.debug) {
            Server.stopAppiumServer();
            BaseDevice.stopTestApp();
            BaseDevice.stopDevice();
        }
    }

    public static String getTestName() {
        String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
        return methodName;
    }
}