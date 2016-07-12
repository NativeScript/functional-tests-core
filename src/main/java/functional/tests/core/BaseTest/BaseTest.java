package functional.tests.core.BaseTest;

import functional.tests.core.Enums.DeviceType;
import functional.tests.core.Log.Log;
import functional.tests.core.OSUtils.OSUtils;
import functional.tests.core.Settings.Settings;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.io.IOException;
import java.lang.reflect.Method;

public class BaseTest {

    protected static int previousTestStatus = ITestResult.SUCCESS;

    public BaseTest() {
        try {
            Log.initLogging();
            Settings.initSettings();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getAppName() {
        String appName = Settings.testAppImageFolder;
        return appName;
    }

    protected static void takeScreenOfHost(String fileName) {
        if ((Settings.deviceType == DeviceType.Simulator) || (Settings.deviceType == DeviceType.Emulator)) {
            OSUtils.getScreenshot("HostOS_" + fileName);
        }
    }

    @BeforeMethod(alwaysRun = true)
    public void beforeMethodBaseTest(Method method) throws Exception {
        Log.separator();
        Log.info("Start test: " + method.getName());
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethodBaseTest(ITestResult result) throws IOException {

        // Get test case name
        String testCase = result.getMethod().getMethodName();

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

    public String getTestName() {
        String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
        return methodName;
    }
}
