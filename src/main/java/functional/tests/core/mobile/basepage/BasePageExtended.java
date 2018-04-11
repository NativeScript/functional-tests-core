package functional.tests.core.mobile.basepage;

import functional.tests.core.enums.ClickType;
import functional.tests.core.enums.PlatformType;
import functional.tests.core.enums.Position;
import functional.tests.core.mobile.basetest.MobileContext;
import functional.tests.core.mobile.element.UIElement;
import functional.tests.core.mobile.helpers.NavigationManager;
import functional.tests.core.enums.SwipeElementDirection;

/**
 * This page basically contains extended logic to help you to navigate through the application using NavigationManager.
 */
public abstract class BasePageExtended extends BasePage {

    protected NavigationManager navigationManager;
    protected String mainPage;

    /**
     * In order not to set the main page in initialization of the page.
     *
     * @param mobileContext
     */
    public BasePageExtended(MobileContext mobileContext) {
        this(mobileContext, new NavigationManager(mobileContext));
    }

    /**
     * In order to set the main page in initialization of the page.
     *
     * @param mainPage
     * @param mobileContext
     */
    public BasePageExtended(String mainPage, MobileContext mobileContext) {
        this(mobileContext);
        this.mainPage = mainPage;
        if (!this.mainPage.isEmpty()) {
            this.navigationManager.setMainPage(this.mainPage);
        }
    }

    /**
     * In order not to set the main page in initialization of the page. Reusing NavigationManager when we initialized more than one page in test class
     *
     * @param mobileContext
     * @param navigationManager
     */
    public BasePageExtended(MobileContext mobileContext, NavigationManager navigationManager) {
        super(mobileContext);
        this.context.navigationManager = navigationManager;
        this.context.log.info("NavigationManager initialized");

        this.navigationManager = this.context.navigationManager;
    }

    /**
     * In order not to set the main page in initialization of the page. Reusing NavigationManager when we initialized more than one page in test class
     *
     * @param mobileContext
     * @param navigationManager
     */
    public BasePageExtended(String mainPage, MobileContext mobileContext, NavigationManager navigationManager) {
        this(mobileContext, navigationManager);
        this.mainPage = mainPage;
        if (!this.mainPage.isEmpty()) {
            this.navigationManager.setMainPage(this.mainPage);
        }
    }

    /**
     * Returns NavigationManager.
     *
     * @return
     */
    public NavigationManager getNavigationManager() {
        return this.navigationManager;
    }

    /**
     * Navigates to elements and set the element text as key in navigation stack.
     *
     * @param element
     * @return
     */
    public boolean navigateTo(UIElement element) {
        return this.navigateTo(element, "");
    }

    /**
     * Navigates to elements and set page name as key in navigation stack.
     *
     * @param element
     * @param page
     * @return
     */
    public boolean navigateTo(UIElement element, String page) {
        return this.navigateTo(element, ClickType.Tap, page);
    }

    /**
     * Navigate to page using the given element and updates NavigationManager.
     *
     * @param element
     * @param clickType
     * @param page      This page name will be saved in TestStateManager to define level of navigation. If false will get the button content
     * @return Returns  true if the navigation succeed
     */
    public boolean navigateTo(UIElement element, ClickType clickType, String page) {
        return this.navigationManager.navigateTo(element, clickType, page);
    }

    /**
     * Scroll to the example and navigates to it. Before scrolling NavigationManager navigates back to the main page.
     *
     * @param example
     * @return element
     */
    public boolean navigateTo(String example) {
        return this.navigateTo(example, true);
    }

    /**
     * Scroll to the example and navigates to it. If should restart to main page is true then the NavigationManager will navigate back to the main page.
     *
     * @param example
     * @param shouldNavigateToMainPage If true will navigate to main page. If false skips the navigation to main page.
     * @return
     */
    public boolean navigateTo(String example, boolean shouldNavigateToMainPage) {
        if (shouldNavigateToMainPage) {
            this.navigateToMainPage();
        }

        return this.navigationManager.navigateTo(example);
    }

    /**
     * @param containerLocator
     * @param text
     * @param position
     * @param retriesCount
     * @param offsetX
     * @param offsetY
     * @return
     */
    public UIElement scrollDownToElement(UIElement containerLocator, String text, Position position, int retriesCount, int offsetX, int offsetY) {
        UIElement mainPage = containerLocator.scrollInElementToElement(SwipeElementDirection.DOWN, position, this.locators.byText(text), 0, retriesCount, offsetX, offsetY);

        return mainPage;
    }

    /**
     * This method unifies swipe and scrollTo methods according to platform and api level and navigates to the page.
     *
     * @param containerLocator
     * @param text
     * @param position
     * @return
     */
    public boolean scrollDownToElementAndNavigate(UIElement containerLocator, String text, Position position) {
        UIElement mainPage = null;
        int retriesCount = 5;
        if (this.settings.platformVersion < 7.0 && this.settings.platform == PlatformType.Android) {
            mainPage = this.gestures.swipeInWindowToElement(SwipeElementDirection.DOWN, this.locators.byText(text), retriesCount, 250, 50);
        } else if (this.settings.platformVersion < 10 && this.settings.platform == PlatformType.iOS) {
            mainPage = this.gestures.swipeInWindowToElement(SwipeElementDirection.DOWN, this.locators.byText(text), retriesCount, 1250, 50);
        } else {
            mainPage = containerLocator.scrollInElementToElement(SwipeElementDirection.DOWN, position, this.locators.byText(text), 0, retriesCount);
        }

        return this.navigateTo(mainPage, text);
    }

    /**
     * Uses default navigation and updates NavigationManager.
     */
    @Override
    public void navigateBack() {
        this.navigationManager.navigateBack();
    }

    /**
     * Navigate back by given element and update state of NavigationManager.
     *
     * @param element
     */
    public void navigateBack(UIElement element) {
        this.navigationManager.navigateBack(element);
    }

    /**
     * Uses default app navigation from App and doesn't update NavigationManager.
     */
    public void navigateBackDefault() {
        super.navigateBack();
    }

    /**
     * Navigates to the main page.
     */
    public void navigateToMainPage() {
        this.navigationManager.navigateToMainPage();
    }

    /**
     * Navigates to home page.
     */
    public void navigateToHomePage() {
        this.navigationManager.navigateToHomePage();
    }

    /**
     * Reset navigation to last opened page using the approach from navigateTo and all pages that are cached in NavigationManager.
     */
    public void resetNavigationToLastPage() {
        this.navigationManager.resetNavigationToLastOpenedPage();
    }
}
