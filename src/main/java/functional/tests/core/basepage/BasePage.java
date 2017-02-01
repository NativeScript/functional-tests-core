package functional.tests.core.basepage;

import functional.tests.core.app.App;
import functional.tests.core.appium.Client;
import functional.tests.core.basetest.Context;
import functional.tests.core.basetest.TestContextSetupManager;
import functional.tests.core.element.UIElement;
import functional.tests.core.enums.ClickType;
import functional.tests.core.find.Find;
import functional.tests.core.find.Locators;
import functional.tests.core.find.Wait;
import functional.tests.core.gestures.Gestures;
import functional.tests.core.helpers.NavigationHelper;
import functional.tests.core.log.Log;
import functional.tests.core.settings.Settings;
import org.bridj.ann.Virtual;
import org.testng.Assert;

/**
 * Base page.
 */
public class BasePage {
    public Client client;
    public Gestures gestures;
    public Find find;
    public Wait wait;
    public Settings settings;
    public Locators locators;
    public Log log;
    public Context context;
    public App app;


    /**
     * TODO(): Please explain when we should use BasePage().
     */
    public BasePage() {
        this(TestContextSetupManager.getTestSetupManager().context);
    }

    /**
     * TODO(): Please explain when we should use BasePage(Context context).
     *
     * @param context
     */
    public BasePage(Context context) {
        this.context = context;
        this.client = this.context.client;
        this.gestures = this.context.gestures;
        this.find = this.context.find;
        this.wait = this.context.wait;
        this.settings = this.context.settings;
        this.locators = this.context.locators;
        this.log = this.context.log;
        this.app = this.context.app;
    }

    /**
     * TODO(): Please explain this.
     *
     * @param example
     * @return
     */
    @Virtual
    public UIElement scrollTo(String example, int retriesCount) {
        return NavigationHelper.scrollTo(example, this.context, retriesCount);
    }


    /**
     * TODO(): Please explain this.
     *
     * @param example
     * @return
     */
    public boolean navigateTo(String example) {
        return NavigationHelper.navigateTo(example, this.context, 0);
    }

    public boolean navigateTo(UIElement example) {
        return NavigationHelper.navigateTo(example, ClickType.Click, null, "");
    }

    /**
     * Navigate back.
     * Call this.app.navigateBack();
     */
    public void navigateBack() {
        this.app.navigateBack();
    }

    /**
     * Hide keyboard.
     * Call this.app.hideKeyboard();
     */
    public void hideKeyboard() {
        this.app.hideKeyboard();
    }

    /**
     * Verify page is loaded.
     *
     * @param element UIElement that should be available in base page.
     */
    public void loaded(UIElement element) {
        String className = this.context.getTestName();
        Assert.assertNotNull(element, String.format("%s page loaded.", className));
        this.log.info(String.format("%s page loaded.", className));
    }
}
