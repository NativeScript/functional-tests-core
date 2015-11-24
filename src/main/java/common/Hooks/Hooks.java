package common.Hooks;

import common.Appium.Client;
import common.Appium.Server;
import common.Device.BaseDevice;
import common.Log.Log;
import common.Settings.Settings;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.lang.reflect.Method;

public class Hooks {

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
        Log.info("Start test: " + method.getName());
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) throws Exception {

        // Get test case name
        String testCase = result.getMethod().getMethodName();

        // Report results
        if (result.getStatus() == ITestResult.SUCCESS) {
            Log.logScreen(testCase, "Screenshot after " + testCase);
            Log.info("=> Test " + testCase + " passed!");
        } else if (result.getStatus() == ITestResult.SKIP) {
            Log.error("=> Test " + testCase + " skipped!");
        } else if (result.getStatus() == ITestResult.FAILURE) {
            Log.logScreen(testCase, "Screenshot after " + testCase);
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
