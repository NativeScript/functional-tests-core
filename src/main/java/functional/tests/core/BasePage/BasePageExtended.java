package functional.tests.core.BasePage;

import functional.tests.core.Appium.Client;
import functional.tests.core.BaseTest.TestsStateManager;
import functional.tests.core.Element.UIElement;
import functional.tests.core.Enums.PlatformType;
import functional.tests.core.Find.ActionHelper;
import functional.tests.core.Find.Find;
import functional.tests.core.Find.Locators;
import functional.tests.core.Log.Log;
import functional.tests.core.Settings.Settings;
import org.openqa.selenium.By;

import java.util.List;

public abstract class BasePageExtended extends BasePage {

    protected TestsStateManager testsStateManager;
    protected String mainPage;
    protected By elementToWaitForMainPage;

    public BasePageExtended(Client client, String mainPage, TestsStateManager testsStateManager) {
        this(client, testsStateManager);
        this.mainPage = mainPage;
        if (this.mainPage != null && this.mainPage != "") {
            this.testsStateManager.setMainPage(this.mainPage);
        }
    }

    public BasePageExtended(Client client, TestsStateManager testsStateManager) {
        super(client);
        this.testsStateManager = testsStateManager;
    }

    public BasePageExtended() {
    }

    /**
     * @deprecated use non static find instead.
     */
    @Deprecated
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

    /**
     * @deprecated use non static find instead.
     */
    @Deprecated
    public static UIElement findByText(String value) {
        return Find.findByText(value, Settings.shortTimeout);
    }

    /**
     * @deprecated use non static find instead.
     */
    @Deprecated
    public static UIElement findByType(String value) {
        return Find.findByType(value, Settings.shortTimeout);
    }

    /**
     * @deprecated use non static find instead.
     */
    @Deprecated
    public static UIElement findElementByLocator(By locator) {
        return Find.findElementByLocator(locator, Settings.shortTimeout);
    }

    /**
     * @deprecated use non static find instead.
     */
    @Deprecated
    public static List<UIElement> findElementsByLocator(By locator) {
        return Find.findElementsByLocator(locator, Settings.shortTimeout);
    }

    public void navBack() {
        this.testsStateManager.navigateBack();
    }

    public void navBack(UIElement element) {
        this.testsStateManager.navBack(element);
    }

    public boolean navigateToMainPage() {
        return this.testsStateManager.navigateToMainPage();
    }

    public void navigateToHomePage() {
        this.testsStateManager.navigateToHomePage();
    }

    public void resetNavigationToLastPage() {
        this.testsStateManager.resetNavigationToLastOpenedPage();
    }

    public boolean navigateToPage(String demoPath) {
        this.navigateToMainPage();
        return navigateTo(demoPath);
    }

    public boolean navigateToPage(String demoPath, boolean shouldRestartToMainPage) {
        if (shouldRestartToMainPage) {
            this.navigateToMainPage();
        }

        return navigateTo(demoPath);
    }

    public boolean navigateTo(UIElement element) {
        return this.testsStateManager.navigateTo(element);
    }

    private boolean navigateTo(String demoPath) {
        if (this.testsStateManager.getCurrentPage() != null && this.testsStateManager.getCurrentPage() != "") {
            if (this.testsStateManager.getCurrentPage() != demoPath) {
                return ActionHelper.navigateTo(demoPath, this.testsStateManager, this.client);
            }
        }

        return ActionHelper.navigateTo(demoPath, this.testsStateManager, this.client);
    }
}
