package functional.tests.core.web.basetest;

import functional.tests.core.log.Log;
import functional.tests.core.web.find.Find;
import functional.tests.core.web.find.Locators;
import functional.tests.core.web.settings.WebSettings;
import org.openqa.selenium.chrome.ChromeDriver;

/**
 * Created by tsenov on 2/23/17.
 */
public class WebContext {
    public ChromeDriver driver;
    public int lastTestResult;
    public Log log;
    public WebSettings settings;
    public Find find;
    public ChromeDriver chromeDriver;
    public Locators locators;

    public WebContext() {
    }

    public WebContext(ChromeDriver chromeDriver, Find find, Log log, WebSettings settings) {
        this.chromeDriver = chromeDriver;
        this.find = find;
        this.log = log;
        this.settings = settings;
        this.locators = new Locators();
    }
}
