package functional.tests.core.BaseTest;

import functional.tests.core.Log.Log;
import org.testng.ITestResult;

import java.util.Dictionary;

public class TestsStateManager {
    private ITestResult status;
    private int navigationLevel;
    private int maxNavigationLevel;
    private String mainPage;
    private String currentPage;
    private Dictionary<String, Integer> pages;

    public TestsStateManager() {
        this.navigationLevel = 0;
        this.maxNavigationLevel = 0;
    }

    public int increaseNavigationLevel() {
        this.maxNavigationLevel++;
        return this.navigationLevel++;
    }

    public int decreasNavigationLevel() {
        if (this.navigationLevel < 0) {
            Log.info("See the navigation");
        }
        return this.navigationLevel--;
    }

    public int getLevel() {
        return this.navigationLevel;
    }

    public int resetLevel() {
        this.navigationLevel = 0;

        return this.navigationLevel;
    }

    public void setMainPage(String mainPage) {
        this.mainPage = mainPage;
        this.pages.put(this.mainPage, this.navigationLevel);
    }

    public String getMainPage() {
        return this.mainPage;
    }

    public void setCurrentPage(String page) {
        this.currentPage = page;
    }

    public String getCurrentPage() {
        return this.currentPage;
    }
}
