package functional.tests.core.basepage;

import functional.tests.core.basetest.Context;
import functional.tests.core.element.UIElement;
import functional.tests.core.enums.ClickType;
import functional.tests.core.helpers.NavigationManager;

/**
 * This page basically contains extended logic to help you to navigate through the application using NavigationManager.
 */
public abstract class BasePageExtended extends BasePage {

    protected NavigationManager navigationManager;
    protected String mainPage;

    /**
     * In order not to set the main page in initialization of the page.
     *
     * @param context
     */
    public BasePageExtended(Context context) {
        this(context, new NavigationManager(context));
    }

    /**
     * In order to set the main page in initialization of the page.
     *
     * @param mainPage
     * @param context
     */
    public BasePageExtended(String mainPage, Context context) {
        this(context);
        this.mainPage = mainPage;
        if (!this.mainPage.isEmpty()) {
            this.navigationManager.setMainPage(this.mainPage);
        }
    }

    /**
     * In order not to set the main page in initialization of the page. Reusing NavigationManager when we initialized more than one page in test class
     *
     * @param context
     * @param navigationManager
     */
    public BasePageExtended(Context context, NavigationManager navigationManager) {
        super(context);
        this.context.navigationManager = navigationManager;
        this.context.log.info("NavigationManager initialized");

        this.navigationManager = this.context.navigationManager;
    }

    /**
     * In order not to set the main page in initialization of the page. Reusing NavigationManager when we initialized more than one page in test class
     *
     * @param context
     * @param navigationManager
     */
    public BasePageExtended(String mainPage, Context context, NavigationManager navigationManager) {
        this(context, navigationManager);
        this.mainPage = mainPage;
        if (!this.mainPage.isEmpty()) {
            this.navigationManager.setMainPage(this.mainPage);
        }
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
