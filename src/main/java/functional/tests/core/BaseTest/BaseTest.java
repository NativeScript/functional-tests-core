package functional.tests.core.BaseTest;

import functional.tests.core.Enums.DeviceType;
import functional.tests.core.Enums.PlatformType;
import functional.tests.core.Log.Log;
import functional.tests.core.OSUtils.OSUtils;
import functional.tests.core.Perf.PerfInfo;
import functional.tests.core.Settings.Doctor;
import functional.tests.core.Settings.Settings;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import java.io.IOException;
import java.lang.reflect.Method;

public class BaseTest {

    protected static int previousTestStatus = ITestResult.SUCCESS;

    public BaseTest() {
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

    @BeforeSuite(alwaysRun = true)
    public void beforeSuiteBaseTest() throws Exception {
        try {
            Log.initLogging();
            Settings.initSettings();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Doctor.check();
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

        this.checkMemoryPerformance(result);
    }

    public static String getTestNameToWriteFile() {
        StackTraceElement[] stackTraces = Thread.currentThread().getStackTrace();

        for (int i = 0; i < stackTraces.length; i++) {
            StackTraceElement stackTrace = stackTraces[i];
            try {
                Class<?> cls = Class.forName(stackTrace.getClassName());
                java.lang.reflect.Method method = cls.getDeclaredMethod(stackTrace.getMethodName());
                Test annotation = method.getAnnotation(Test.class);
                if (annotation != null) {
                    String testName = stackTrace.getMethodName();

                    return testName;
                }
            } catch (ClassNotFoundException e) {
            } catch (NoSuchMethodException e) {
                //e.printStackTrace();
            }
        }

        return "Couldn't parse test method name";
    }

    public String getTestName() {
        return getTestNameToWriteFile();
    }

    private void checkMemoryPerformance(ITestResult result) {
        Long usedMemory = PerfInfo.getMem(Settings.deviceId);
        Log.info("Performance info of used memory: " + usedMemory);
        Log.info("Expected max memory usage: " + Settings.memoryMaxUsageLimit);

        if (Settings.memoryMaxUsageLimit > 0) {
            boolean hasMemoryLeak = Settings.memoryMaxUsageLimit < usedMemory;
            if (hasMemoryLeak) {
                Log.error("=== Memory leak appears after test " + result.getName() + " ====");
                Assert.assertTrue(!hasMemoryLeak, "Used memory of " + usedMemory + " is more than expected " + Settings.memoryMaxUsageLimit + " !!!");
                result.setStatus(ITestResult.FAILURE);
            }
        }
    }
}
