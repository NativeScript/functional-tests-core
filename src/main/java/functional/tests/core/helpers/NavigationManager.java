package functional.tests.core.helpers;

import functional.tests.core.appium.Client;
import functional.tests.core.basetest.Context;
import functional.tests.core.basetest.TestContextSetupManager;
import functional.tests.core.element.UIElement;
import functional.tests.core.enums.ClickType;
import functional.tests.core.enums.PlatformType;
import functional.tests.core.find.Wait;
import functional.tests.core.log.LoggerBase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is for handling the navigation in application which is like a tree.
 * The home page is the start page of the application
 * The main page points to the level of tree which will be set as home and will not navigate back on lower level
 */
public class NavigationManager {

    private static final LoggerBase LOGGER_BASE = LoggerBase.getLogger("TestStateManager");

    private int navigationLevel;
    private String mainPage;
    private String currentPage;
    private Map<String, Integer> pages;
    private ArrayList<String> usedPages;
    private Client client;
    private Wait wait;
    private Context context;
    private int scrollToElementRetriesCount;

    /**
     * TODO(): Add docs.
     * Explain when to use NavigationManager()
     */
    public NavigationManager() {
        this(TestContextSetupManager.getTestSetupManager().context);
    }

    /**
     * TODO(): Add docs.
     * Explain when to use NavigationManager(Context context)
     *
     * @param context
     */
    public NavigationManager(Context context) {
        this.context = context;
        this.navigationLevel = 0;
        this.pages = new HashMap<String, Integer>();
        this.usedPages = new ArrayList<String>();
        this.client = this.context.client;
        this.wait = this.context.wait;
        this.scrollToElementRetriesCount = 5;
    }

    /**
     * Get the navigation level.
     *
     * @return
     */
    public int getLevel() {
        return this.navigationLevel;
    }

    /**
     * Get the main page.
     *
     * @return
     */
    public String getMainPage() {
        return this.mainPage;
    }

    /**
     * Set the main page.
     *
     * @param mainPage
     */
    public void setMainPage(String mainPage) {
        this.mainPage = mainPage;
        if (mainPage != null && !this.mainPage.isEmpty() && mainPage.contains("/") || mainPage.contains(".")) {
            String splitSeparator = mainPage.contains("/") ? "/" : ".";
            String[] listOfPages = mainPage.split(splitSeparator);
            this.mainPage = listOfPages[0].toLowerCase();
        }

        if (!this.mainPage.isEmpty() && this.context.settings.platform == PlatformType.Andorid) {
            this.mainPage = this.mainPage.toLowerCase();
        }
    }

    /**
     * Get the current page.
     *
     * @return
     */
    public String getCurrentPage() {
        return this.currentPage;
    }

    /**
     * Set the current page.
     *
     * @param page
     */
    public void setCurrentPage(String page) {
        this.currentPage = page;
        if (this.context.settings.platform == PlatformType.Andorid) {
            this.currentPage = page.toLowerCase();
        }

        this.setNewPage();
    }

    public void setScrollToElementRetriesCount(int scrollToElementRetriesCount) {
        this.scrollToElementRetriesCount = scrollToElementRetriesCount;
    }

    /**
     * Get the index of a page.
     *
     * @param page
     * @return
     */
    public int getPageIndex(String page) {
        try {
            return this.pages.get(page);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Navigate to the previous opened page.
     */
    public void resetNavigationToLastOpenedPage() {
        this.navigationLevel = 0;
        if (this.usedPages.size() > 0) {
            String pagesAsString = "";
            String pagesToNavigateToAsString = "";

            for (int i = 0; i < this.usedPages.size(); i++) {
                pagesAsString += this.usedPages.get(i) + "/";
            }

            this.usedPages = new ArrayList<String>();
            if (pagesAsString.endsWith("/")) {
                pagesToNavigateToAsString = pagesAsString.substring(0, pagesAsString.length() - 1);
            } else {
                pagesToNavigateToAsString = pagesAsString;
            }

            NavigationHelper.navigateTo(pagesToNavigateToAsString, this, this.context, this.scrollToElementRetriesCount);
        } else {
            if (this.mainPage != null && this.mainPage != "") {
                NavigationHelper.navigateTo(this.mainPage, this, this.context, this.scrollToElementRetriesCount);
            }
        }
    }

    /**
     * Navigate to the main page.
     */
    public void resetNavigationMainPage() {
        this.navigateToMainPage();
        for (String page :
                this.usedPages) {
            this.usedPages.remove(page);
            if (page == this.mainPage) {
                break;
            }
        }

        if (this.context.wait.waitForVisible(this.context.locators.byText(this.mainPage)) != null) {
            NavigationHelper.navigateTo(this.mainPage, this, this.context, this.scrollToElementRetriesCount);
        }
    }

    /**
     * Navigate to the home page.
     */
    public void navigateToHomePage() {
        while (this.getLevel() > 0) {
            try {
                this.navigateBack();
            } catch (Exception ex) {
                LOGGER_BASE.error("Could not navigate back: " + ex.getMessage());
            }
        }

        if (this.mainPage != null && !this.mainPage.isEmpty()) {
            UIElement checked = this.wait.waitForVisible(this.context.locators.findByTextLocator(this.mainPage, true), false);
            LOGGER_BASE.info(this.mainPage + " in navigateToHomePage is displayed: " + (checked != null ? checked.isDisplayed() : "null"));
        }
    }

    /**
     * Navigate to the main page.
     */
    public void navigateToMainPage() {
        if (this.getPageIndex(this.getMainPage()) >= this.getLevel()) {
            LOGGER_BASE.info("The navigation to the main page will be skipped because this should be the main page!");
        }

        while (this.getPageIndex(this.getMainPage()) < this.getLevel()) {
            this.navigateBack();
        }
    }

    /**
     * Navigate to element.
     *
     * @param element
     * @return
     */
    public boolean navigateTo(UIElement element, String pageName) {
        return NavigationHelper.navigateTo(element, this, pageName);
    }

    /**
     * Navigates to page using element and click type.
     *
     * @param element   Uses to click
     * @param clickType
     * @param page      This page is used to be logged in NavigationManager so it can track the levels of navigation.
     *                  If empty it will try to get the text content of the button
     * @return
     */
    public boolean navigateTo(UIElement element, ClickType clickType, String page) {
        return NavigationHelper.navigateTo(element, clickType, this, page);
    }

    /**
     * TODO(): Add docs.
     *
     * @param demoPath
     * @return
     */
    public boolean navigateTo(String demoPath) {
        if (this.getCurrentPage() != null && this.getCurrentPage() == demoPath) {
            return false;
        }

        return NavigationHelper.navigateTo(demoPath, this, this.context, this.scrollToElementRetriesCount);
    }

    /**
     * Navigate back by element.
     *
     * @param element
     */
    public void navigateBack(UIElement element) {
        if (this.getLevel() > 0) {
            element.tap();
            this.updatePagesOnNavigateBack();
            LOGGER_BASE.info("Navigated back by element.");
        } else {
            LOGGER_BASE.info("This is the main page!");
        }
    }

    /**
     * Navigate back.
     */
    public void navigateBack() {
        if (this.getLevel() > 0) {
            this.updatePagesOnNavigateBack();
            NavigationHelper.navigateBack(this.context);
            LOGGER_BASE.info("Navigated back.");
        } else {
            LOGGER_BASE.info("This is the main page!");
        }
    }

    /**
     * Remove current page.
     */
    public void removeCurrentPage() {
        this.pages.remove(this.currentPage);
        if (this.navigationLevel < 0) {
            LOGGER_BASE.info("See the navigation");
        }

        if (this.usedPages.size() > 0) {
            this.usedPages.remove(this.currentPage);
        }

        if (this.usedPages.size() > 0) {
            this.currentPage = this.usedPages.get(this.usedPages.size() - 1);
        }
    }

    /**
     * Navigate forward.
     */
    public void navigateForward() {
        this.increaseNavigationLevel();
        NavigationHelper.navigateForward(this.context);
    }

    /**
     * Update pages on navigating back.
     */
    public void updatePagesOnNavigateBack() {
        this.removeCurrentPage();
        this.decreaseNavigationLevel();
    }

    /**
     * Decrease the navigation level.
     *
     * @return
     */
    private int decreaseNavigationLevel() {
        this.navigationLevel--;

        return this.navigationLevel;
    }

    /**
     * Increase the navigation level.
     *
     * @return
     */
    private int increaseNavigationLevel() {
        this.navigationLevel++;
        return this.navigationLevel;
    }

    /**
     * Set a new pages.
     */
    private void setNewPage() {
        this.increaseNavigationLevel();
        this.pages.put(this.currentPage, this.navigationLevel);
        this.usedPages.add(this.currentPage);
    }
}
