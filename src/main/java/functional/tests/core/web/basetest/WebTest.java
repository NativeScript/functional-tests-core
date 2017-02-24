package functional.tests.core.web.basetest;

import functional.tests.core.log.Log;
import functional.tests.core.log.LoggerBase;
import functional.tests.core.web.find.Find;
import functional.tests.core.web.find.Locators;
import functional.tests.core.web.settings.WebSettings;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * Created by tseno on 22.2.2017 г..
 */
public class WebTest {

    protected Log log;
    protected WebContext webContext;
    protected WebSettings settings;
    protected Find find;
    protected Locators locators;
    private WebSetupManager webSetupManager;
    private ChromeDriver driver;

    public WebTest() {
        LoggerBase.initLog4j();
        this.initUITestHelpers();
    }

    public ChromeDriver getDriver() {
        return this.driver;
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

    private void initUITestHelpers() {
        if (this.webSetupManager == null) {
            this.webSetupManager = WebSetupManager.init();
        }
        if (this.getDriver() == null) {
            this.webContext = this.webSetupManager.getContext();
            this.settings = this.webContext.settings;
            this.driver = this.webContext.driver;
            this.find = this.webContext.find;
            this.log = this.webContext.log;
            this.locators = this.webContext.locators;
        }
    }
}
