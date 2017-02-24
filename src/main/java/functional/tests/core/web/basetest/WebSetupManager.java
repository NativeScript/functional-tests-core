package functional.tests.core.web.basetest;

import functional.tests.core.chromedriver.ChromeDriverOptions;
import functional.tests.core.log.Log;
import functional.tests.core.log.LoggerBase;
import functional.tests.core.mobile.basetest.MobileSetupManager;
import functional.tests.core.settings.Settings;
import functional.tests.core.web.find.Find;
import functional.tests.core.web.find.Locators;
import functional.tests.core.web.settings.WebSettings;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.ITestResult;

import java.util.HashMap;
import java.util.Map;

import static functional.tests.core.settings.Settings.getAppConfig;

/**
 * Created by tsenov on 2/23/17.
 */
public class WebSetupManager {

    private static final LoggerBase LOGGER_BASE = LoggerBase.getLogger("WebSetupManager");
    protected static Map<String, WebSetupManager> initSettings;
    private WebContext context;
    private ChromeDriver driver;

    private WebSetupManager() {
        this.context = new WebContext();
    }

    /**
     * Init only once per configuration.
     *
     * @return Instance of MobileSetupManager.
     */
    public static WebSetupManager init() {
        WebSetupManager webSetupManager;
        if (WebSetupManager.initSettings == null) {
            WebSetupManager.initSettings = new HashMap<>();
        }

        if (!WebSetupManager.initSettings.containsKey(Settings.getAppConfig())) {
            webSetupManager = new WebSetupManager();
            webSetupManager.context.settings = new WebSettings();
            webSetupManager.driver = new ChromeDriver(new ChromeDriverOptions().loadChromeDriverOptions(webSetupManager.context.settings));
            webSetupManager.context.driver = webSetupManager.driver;
            webSetupManager.context.log = new Log();
            webSetupManager.context.locators = new Locators();
            webSetupManager.context.lastTestResult = ITestResult.SUCCESS;
            webSetupManager.context.find = new Find(webSetupManager.driver);
            WebSetupManager.initSettings.put(getAppConfig(), webSetupManager);
        } else {
            webSetupManager = WebSetupManager.initSettings.get(getAppConfig());
        }

        return webSetupManager;
    }

    /**
     * Get the MobileSetupManager for the current instance according to app config.
     *
     * @return
     */
    public static WebSetupManager getTestSetupManager() {
        return WebSetupManager.initSettings.get(MobileSetupManager.getAppConfig());
    }

    public WebContext getContext() {
        return this.context;
    }

    public ChromeDriver getDriver() {
        return this.driver;
    }

    public void killChromeDriver() {
    }
}
