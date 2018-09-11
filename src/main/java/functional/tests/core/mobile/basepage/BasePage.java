package functional.tests.core.mobile.basepage;

import functional.tests.core.enums.ClickType;
import functional.tests.core.log.Log;
import functional.tests.core.mobile.app.App;
import functional.tests.core.mobile.appium.Client;
import functional.tests.core.mobile.basetest.MobileContext;
import functional.tests.core.mobile.basetest.MobileSetupManager;
import functional.tests.core.mobile.element.UIElement;
import functional.tests.core.mobile.find.Find;
import functional.tests.core.mobile.find.Locators;
import functional.tests.core.mobile.find.Wait;
import functional.tests.core.mobile.gestures.Gestures;
import functional.tests.core.mobile.helpers.NavigationHelper;
import functional.tests.core.mobile.settings.MobileSettings;
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
    public MobileSettings settings;
    public Locators locators;
    public Log log;
    public MobileContext context;
    public App app;


    /**
     * TODO(): Please explain when we should use BasePage().
     */
    public BasePage() {
        this(MobileSetupManager.getTestSetupManager().getContext());
    }

    /**
     * TODO(): Please explain when we should use BasePage(MobileContext context).
     *
     * @param mobileContext
     */
    public BasePage(MobileContext mobileContext) {
        this.context = mobileContext;
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
        return NavigationHelper.navigateTo(example, ClickType.Tap, null, "");
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
