package functional.tests.core.BaseTest;

import functional.tests.core.Appium.Client;
import functional.tests.core.Element.UIElement;
import functional.tests.core.Find.ActionHelper;
import functional.tests.core.Log.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TestsStateManager {

    public int status;
    private int navigationLevel;
    private String mainPage;
    private String currentPage;
    private Map<String, Integer> pages;
    private ArrayList<String> usedPages;
    private Client client;

    public TestsStateManager(Client client) {
        this.navigationLevel = 0;
        this.pages = new HashMap<String, Integer>();
        this.usedPages = new ArrayList<String>();
        this.client = client;
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
        if (mainPage.contains("/") || mainPage.contains(("."))) {
            String splitSeparator = mainPage.contains("/") ? "/" : ".";
            this.mainPage = mainPage.split(splitSeparator)[0];
        } else {
            this.mainPage = mainPage;
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
        this.setNewPage();
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
            this.mainPage = this.usedPages.get(0);

            this.usedPages = new ArrayList<String>();
            if (pagesAsString.endsWith("/")) {
                pagesToNavigateToAsString = pagesAsString.substring(0, pagesAsString.length() - 1);
            } else {
                pagesToNavigateToAsString = pagesAsString;
            }

            ActionHelper.navigateTo(pagesToNavigateToAsString, this, this.client);
        } else {
            if (this.mainPage != null && this.mainPage != "") {
                ActionHelper.navigateTo(this.mainPage, this, this.client);
            }
        }
    }

    /**
     * Navigate to the home page.
     */
    public void navigateToHomePage() {
        Log.info("Navigate to home page!");
        while (this.getLevel() > 0) {
            try {
                this.navBack();
                Log.info("navBack");
            } catch (Exception ex) {
                Log.error("Could not navigate back: " + ex.getMessage());
            }
        }
        Log.info("Navigated to home page!");
    }

    /**
     * Navigate to the main page.
     */
    public void navigateToMainPage() {
        if (this.getPageIndex(this.getMainPage()) >= this.getLevel()) {
            Log.info("The navigation to the main page will be skipped because this should be the main page!");
        }

        while (this.getPageIndex(this.getMainPage()) < this.getLevel()) {
            Log.info("Navigating to main page " + this.getMainPage() + " ...");
            this.navBack();
            Log.info("Navigate back to go to: " + this.getMainPage() + " !");
        }
    }

    /**
     * Navigate to element.
     *
     * @param element
     * @return
     */
    public boolean navigateTo(UIElement element) {
        ActionHelper.navigateTo(element);
        this.increaseNavigationLevel();
        Log.info("Navigate to " + element);
        return true;
    }

    /**
     * Navigate back by element.
     *
     * @param element
     */
    public void navBack(UIElement element) {
        if (this.getLevel() > 0) {
            element.tap();
            this.updatePagesOnNavigateBack();
            Log.info("Navigated back by element.");
        } else {
            Log.info("This is the main page!");
        }
    }

    /**
     * Navigate back.
     */
    public void navBack() {
        if (this.getLevel() > 0) {
            this.navigateBack();
            Log.info("Navigated back.");
        } else {
            Log.info("This is the main page!");
        }
    }

    /**
     * Navigate to the main page.
     */
    public void resetNavigationMainPage() {
        this.navigationLevel = 0;
        ActionHelper.navigateTo(this.mainPage, this, this.client);
    }

    /**
     * Remove current page.
     */
    public void removeCurrentPage() {
        this.pages.remove(this.currentPage);
        if (this.navigationLevel < 0) {
            Log.info("See the navigation");
        }

        if (this.usedPages.size() > 0) {
            this.usedPages.remove(this.currentPage);
        }

        if (this.usedPages.size() > 0) {
            this.currentPage = this.usedPages.get(this.usedPages.size() - 1);
        }
    }

    /**
     * Navigate back.
     */
    public void navigateBack() {
        this.updatePagesOnNavigateBack();
        ActionHelper.navigateBack(this.client);
    }

    /**
     * Navigate forward.
     */
    public void navigateForward() {
        this.increaseNavigationLevel();
        ActionHelper.navigateForward(this.client);
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

    /**
     * Update pages on navigating back.
     */
    private void updatePagesOnNavigateBack() {
        this.removeCurrentPage();
        this.decreaseNavigationLevel();
    }
}
