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

import java.lang.reflect.Method;

public class BaseTest {

    private static boolean isFistTest = true;
    private static int previousTestStatus = ITestResult.SUCCESS;

    @BeforeSuite(alwaysRun = true)
    public static void beforeClass() throws Exception {
        Log.initLogging();
        Settings.initSettings();
        BaseDevice.stopDevice();
        BaseDevice.initDevice();
        BaseDevice.initTestApp();
        Server.initAppiumServer();
        Client.initAppiumDriver();
    }

    @BeforeMethod(alwaysRun = true)
    public void setUp(Method method) throws Exception {
        Log.info("=============================================");
        Log.info("Start test: " + method.getName());

        if (isFistTest) {
            isFistTest = false;
        } else {
            if (Settings.restartApp) {
                App.restart(true);
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
        }
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) throws Exception {

        // Get test case name
        String testCase = result.getMethod().getMethodName();

        // Report results
        previousTestStatus = result.getStatus();
        if (previousTestStatus == ITestResult.SUCCESS) {
            Log.logScreen(testCase + "_pass", "Screenshot after " + testCase);
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
