package functional.tests.core.BaseTest;

import functional.tests.core.App.App;
import functional.tests.core.Appium.Client;
import functional.tests.core.Appium.Server;
import functional.tests.core.Device.BaseDevice;
import functional.tests.core.Log.Log;
import functional.tests.core.Settings.Settings;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import java.io.IOException;
import java.lang.reflect.Method;

public abstract class BaseTest {

    private static boolean isFistTest = true;
    private static int previousTestStatus = ITestResult.SUCCESS;

    @BeforeSuite(alwaysRun = true)
    public static void beforeClass() throws Exception {
        Log.initLogging();
        Settings.initSettings();
        BaseDevice.stopDevice();
        BaseDevice.initDevice();
        BaseDevice.initTestApp();

        // Clean old logs
        BaseDevice.cleanConsoleLog();

        try {
            Server.initAppiumServer();
            Client.initAppiumDriver();
        } catch (Exception e) {
            Log.info("Retry initializing appium server and client");
            Client.stopAppiumDriver();
            Server.stopAppiumServer();
            Server.initAppiumServer();
            Client.initAppiumDriver();
        }

        // Verify app not crashed
        BaseDevice.verifyAppRunning(Settings.deviceId, Settings.testAppPackageId);

        // Get logs for initial app startup
        BaseDevice.getConsoleLog("init");
    }

    @BeforeMethod(alwaysRun = true)
    public void setUp(Method method) throws Exception {
        Log.separator();
        Log.info("Start test: " + method.getName());

        if (isFistTest) {
            isFistTest = false;
        } else {
            if (Settings.restartApp) {
                App.fullRestart();
            }
        }

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
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) throws IOException {

        // Get test case name
        String testCase = result.getMethod().getMethodName();

        // Write console log
        BaseDevice.getConsoleLog(testCase);

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
        }
    }

    @AfterSuite(alwaysRun = true)
    public static void afterClass() throws Exception {
        Client.stopAppiumDriver();
        Server.stopAppiumServer();
        BaseDevice.stopTestApp();
        BaseDevice.stopDevice();
    }
}
