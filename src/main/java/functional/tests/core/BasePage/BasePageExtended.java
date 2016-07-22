package functional.tests.core.BasePage;

import functional.tests.core.Appium.Client;
import functional.tests.core.BasePage.BasePage;
import functional.tests.core.BaseTest.TestsStateManager;
import functional.tests.core.Element.UIElement;
import functional.tests.core.Enums.PlatformType;
import functional.tests.core.Find.ActionHelper;
import functional.tests.core.Find.Find;
import functional.tests.core.Find.FindHelper;
import functional.tests.core.Gestures.Gestures;
import functional.tests.core.Settings.Settings;
import org.openqa.selenium.By;

import java.util.List;

public abstract class BasePageExtended extends BasePage {
    protected TestsStateManager testsStateManager;
    protected Client client;
    protected String mainPage;
    protected Gestures gestures;
    protected FindHelper find;

    public BasePageExtended() {
    }

    public BasePageExtended(Client client, String mainPage, TestsStateManager testsStateManager) {
        this.gestures = new Gestures();
        this.find = new FindHelper(client);
        this.mainPage = mainPage;
        this.testsStateManager = testsStateManager;
        this.testsStateManager.setMainPage(this.mainPage);
        this.client = client;
    }

    public void navBack() {
        this.testsStateManager.navigateBack(this.client);
    }

    public void navigateToMainPage() {
        this.testsStateManager.navigateToMainPage();
    }

    public void navigateToHomePage() {
        this.testsStateManager.navigateToHomePage();
    }

    public void resetNavigationToLastPage() {
        this.testsStateManager.resetNavigationToLastOpenedPage();
    }

    public boolean navigateToPage(String demoPath) {
        this.navigateToMainPage();
        if (this.testsStateManager.getCurrentPage() != null && this.testsStateManager.getCurrentPage() != "") {
            if (this.testsStateManager.getCurrentPage() != demoPath) {
                return ActionHelper.navigateTo(demoPath, this.testsStateManager, this.client);
            }
        }

        return ActionHelper.navigateTo(demoPath, this.testsStateManager, this.client);
    }

    public static UIElement find(String value) {
        if (Settings.platform == PlatformType.Andorid) {
            return findByText(value);
        }
        if (Settings.platform == PlatformType.iOS) {
            return findElementByLocator(By.id(value));
        } else {
            return null;
        }
    }

    public static UIElement findByText(String value) {
        return Find.findByText(value, Settings.shortTimeout);
    }

    public static UIElement findByType(String value) {
        return Find.findByType(value, Settings.shortTimeout);
    }

    public static UIElement findElementByLocator(By locator) {
        return Find.findElementByLocator(locator, Settings.shortTimeout);
    }

    public static List<UIElement> findElementsByLocator(By locator) {
        return Find.findElementsByLocator(locator, Settings.shortTimeout);
    }
}
