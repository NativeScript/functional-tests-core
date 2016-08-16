package functional.tests.core.BaseTest;

import functional.tests.core.Appium.Client;
import functional.tests.core.Element.UIElement;
import functional.tests.core.Find.ActionHelper;
import functional.tests.core.Log.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TestsStateManager {
    private int navigationLevel;
    private String mainPage;
    private String currentPage;
    private Map<String, Integer> pages;
    private ArrayList<String> usedPages;
    private Client client;

    public int status;

    public TestsStateManager(Client client) {
        this.navigationLevel = 0;
        this.pages = new HashMap<String, Integer>();
        this.usedPages = new ArrayList<String>();
        this.client = client;
    }

    public int getLevel() {
        return this.navigationLevel;
    }

    public void setMainPage(String mainPage) {
        if (mainPage.contains("/") || mainPage.contains(("."))) {
            String splitSeparator = mainPage.contains("/") ? "/" : ".";
            this.mainPage = mainPage.split(splitSeparator)[0];
        } else {
            this.mainPage = mainPage;
        }
    }

    public String getMainPage() {
        return this.mainPage;
    }

    public void setCurrentPage(String page) {
        this.currentPage = page;
        this.setNewPage();
    }

    public String getCurrentPage() {
        return this.currentPage;
    }

    public int getPageIndex(String page) {
        try {
            return this.pages.get(page);
        } catch (Exception e) {
            return 0;
        }
    }

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

    public void navigateToHomePage() {
        Log.info("Navigating to home page!");
        while (this.getLevel() > 0) {
            try {
                this.navBack();
            } catch (Exception ex) {
                Log.error("Could not navigate back: " + ex.getMessage());
            }
        }

        Log.info("Navigated to home page!");
    }

    public void navigateToMainPage() {

        if (this.getPageIndex(this.getMainPage()) >= this.getLevel()) {
            Log.info("The navigation to the main page will be skipped because this should be the main page!!!");
        }

        while (this.getPageIndex(this.getMainPage()) < this.getLevel()) {
            Log.info("Navigating to main page " + this.getMainPage() + " .....");
            this.navBack();
            Log.info("Nav back to go to: " + this.getMainPage() + " !");
        }
    }

    public boolean navigateTo(UIElement element) {
        ActionHelper.navigateTo(element);
        this.increaseNavigationLevel();
        Log.info("Navigate to " + element);

        return true;
    }

    public void navBack(UIElement element) {
        if (this.getLevel() > 0) {
            element.click();
            this.updatePagesOnNavigateBack();
            Log.info("Navigate back.");
        } else {
            Log.info("This is main page!");
        }
    }

    public void navBack() {
        if (this.getLevel() > 0) {
            this.navigateBack();
            Log.info("Navigate back.");
        } else {
            Log.info("This is main page!");
        }
    }

    public void resetNavigationMainPage() {
        this.navigationLevel = 0;
        ActionHelper.navigateTo(this.mainPage, this, this.client);
    }

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

    public void navigateBack() {
        this.updatePagesOnNavigateBack();
        ActionHelper.navigateBack(this.client);
    }

    public void navigateForward() {
        this.increaseNavigationLevel();
        ActionHelper.navigateForward(this.client);
    }

    private int decreaseNavigationLevel() {
        this.navigationLevel--;

        return this.navigationLevel;
    }

    private void setNewPage() {
        this.increaseNavigationLevel();

        this.pages.put(this.currentPage, this.navigationLevel);
        this.usedPages.add(this.currentPage);
    }

    private int increaseNavigationLevel() {
        this.navigationLevel++;
        return this.navigationLevel;
    }

    private void updatePagesOnNavigateBack() {
        this.removeCurrentPage();
        this.decreaseNavigationLevel();
    }
}
