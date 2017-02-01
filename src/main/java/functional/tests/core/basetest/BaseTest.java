package functional.tests.core.basetest;

import functional.tests.core.find.Locators;
import functional.tests.core.log.Log;
import functional.tests.core.settings.Doctor;
import functional.tests.core.settings.Settings;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * Base Test.
 * Base tests is designed for all kind of tests.
 * No UI related dependencies should be used in BaseTest.
 */
public class BaseTest {

    protected TestSetupManager testSetupManager;
    protected TestContextSetupManager testContextSetupManager;
    protected Context context;
    protected Settings settings;
    protected Locators locators;
    protected Log log;

    /**
     * Init test setup.
     */
    public BaseTest() {
        this.initTestSetup();
        this.testSetupManager = new TestSetupManager(this.context);
    }

    /**
     * Execute before suite.
     * Actions:
     * 1. Run doctor and check systems and settings (skipped in debug mode)
     *
     * @throws Exception
     */
    @BeforeSuite(alwaysRun = true)
    public void beforeSuiteBaseTest() throws Exception {
        // Run doctor to check if system and settings are ok.
        if (this.settings.debug) {
            this.log.info("[DEBUG MODE] Skip doctor.");
        } else {
            Doctor.check(this.settings);
        }
    }

    /**
     * Execute before each test.
     * Actions:
     * 1. Just log that test method execution starts.
     *
     * @param method
     * @throws Exception
     */
    @BeforeMethod(alwaysRun = true)
    public void beforeMethodBaseTest(Method method) throws Exception {
        this.context.setTestName(method.getName());
        this.log.separator();
        this.log.info("Start test: " + method.getName());
    }

    /**
     * Executed after each test.
     * Actions:
     * 1. Get test result and log it.
     *
     * @param result
     * @throws IOException
     */
    @AfterMethod(alwaysRun = true)
    public void afterMethodBaseTest(ITestResult result) throws IOException {

        // Get test case name
        String testCase = result.getMethod().getMethodName();

        this.context.lastTestResult = result.getStatus();
        if (this.context.lastTestResult == ITestResult.SUCCESS) {
            this.log.info("=> Test " + testCase + " passed!");
        } else if (this.context.lastTestResult == ITestResult.SKIP) {
            this.log.error("=> Test " + testCase + " skipped!");
        } else if (this.context.lastTestResult == ITestResult.FAILURE) {
            this.log.error("=> Test " + testCase + " failed!");
        }
    }

    /**
     * Executed once after all tests.
     * Actions: None.
     *
     * @throws Exception
     */
    @AfterSuite(alwaysRun = true)
    public void afterSuiteBaseTest() throws Exception {
    }

    /**
     * Get value of config parsed by VM options.
     *
     * @return Value of config parsed by VM options.
     */
    protected String getAppConfig() {
        return System.getProperty("appConfig");
    }

    /**
     * Init test setup.
     */
    private void initTestSetup() {
        this.testContextSetupManager = TestContextSetupManager.initTestSetupBasic();
        this.context = this.testContextSetupManager.context;
        this.settings = this.context.settings;
        this.locators = this.context.locators;
        this.log = this.context.log;
    }
}
