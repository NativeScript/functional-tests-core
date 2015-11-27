package functional.tests.core.BaseTest;

import functional.tests.core.Appium.Client;
import functional.tests.core.Appium.Server;
import functional.tests.core.Device.BaseDevice;
import functional.tests.core.Log.Log;
import functional.tests.core.Settings.Settings;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.lang.reflect.Method;

public class BaseTest {

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

        try {
            Client.driver.resetApp();
        } catch (Exception e) {
            Log.error("Failed to restart the app.");
        }
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) throws Exception {

        // Get test case name
        String testCase = result.getMethod().getMethodName();

        // Report results
        if (result.getStatus() == ITestResult.SUCCESS) {
            Log.logScreen(testCase + "_pass", "Screenshot after " + testCase);
            Log.info("=> Test " + testCase + " passed!");
        } else if (result.getStatus() == ITestResult.SKIP) {
            Log.error("=> Test " + testCase + " skipped!");
        } else if (result.getStatus() == ITestResult.FAILURE) {
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
