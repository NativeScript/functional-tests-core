package functional.tests.core.mobile.helpers;

import functional.tests.core.enums.ClickType;
import functional.tests.core.enums.PlatformType;
import functional.tests.core.enums.SwipeElementDirection;
import functional.tests.core.log.LoggerBase;
import functional.tests.core.mobile.basetest.MobileContext;
import functional.tests.core.mobile.element.UIElement;
import functional.tests.core.mobile.element.UIRectangle;
import functional.tests.core.mobile.find.Wait;
import io.appium.java_client.MobileBy;
import org.openqa.selenium.By;
import org.testng.Assert;

import java.awt.*;

/**
 * Navigation provides methods for navigation.
 */
public class NavigationHelper {

    private static final LoggerBase LOGGER_BASE = LoggerBase.getLogger("NavigationHelper");

    /**
     * TODO(): Add docs.
     *
     * @param demoPath
     * @param mobileContext
     * @return
     */
    public static boolean navigateTo(String demoPath, MobileContext mobileContext, int scrollToElementRetriesCount) {
        return navigateTo(demoPath, null, mobileContext, scrollToElementRetriesCount);
    }

    /**
     * TODO(): Add docs.
     *
     * @param demoPath
     * @param navigationManager
     * @param mobileContext
     * @return
     */
    public static boolean navigateTo(String demoPath, NavigationManager navigationManager, MobileContext mobileContext, int scrollToElementRetriesCount) {
        LOGGER_BASE.info("Navigating to \"" + demoPath + "\".");
        String splitSeparator = demoPath.contains("/") ? "/" : ".";
        String[] demos = demoPath.split(splitSeparator);

        if (demos.length == 0) {
            demos = new String[1];
            demos[0] = demoPath;
        }

        for (int i = 0; i < demos.length; i++) {
            String btnText = demos[i];
            UIElement demoBtn = null;
            UIRectangle rectBtn = null;
            if (navigationManager != null) {
                if (navigationManager.getScrollMethod() != null) {
                    demoBtn = navigationManager.getScrollMethod().apply(btnText);
                } else if (navigationManager.getScrollToRectangleMethod() != null) {
                    Rectangle rect = navigationManager.getScrollToRectangleMethod().apply(btnText);
                    if (rect != null) {
                        rectBtn = new UIRectangle(rect, mobileContext);
                    }
                } else if (navigationManager.getNavigationMethod() != null) {
                    navigationManager.getNavigationMethod().accept(btnText);
                } else {
                    demoBtn = scrollTo(btnText, mobileContext, scrollToElementRetriesCount);
                }
            } else {
                demoBtn = scrollTo(btnText, mobileContext, scrollToElementRetriesCount);
            }

            if (demoBtn == null && rectBtn == null) {
                return false;
            }
            if (demoBtn != null) {
                if (mobileContext.settings.platformVersion < 10 && mobileContext.settings.platform == PlatformType.iOS) {
                    demoBtn.click();
                } else {
                    demoBtn.tap();
                }
            }
            if (rectBtn != null) {
                LOGGER_BASE.info(demoPath);
                rectBtn.tap();
            }

            if (navigationManager != null && !btnText.isEmpty()) {
                navigationManager.setCurrentPage(btnText);
            }

            if (i < demos.length - 1) {
                String nextBtnText = demos[i + 1];
                UIElement nextDemoBtn = mobileContext.wait.waitForVisible(mobileContext.locators.byText(nextBtnText), 5, false);
            }
        }

        return true;
    }

    /**
     * TODO(): Add docs.
     *
     * @param element
     * @param navigationManager
     * @param btnContent
     * @return
     */
    public static boolean navigateTo(UIElement element, NavigationManager navigationManager, String btnContent) {
        return navigateTo(element, ClickType.Tap, navigationManager, btnContent);
    }

    /**
     * TODO(): Add docs.
     *
     * @param element
     * @param clickType
     * @param navigationManager
     * @param page
     * @return
     */
    public static boolean navigateTo(UIElement element, ClickType clickType, NavigationManager navigationManager, String page) {
        String btnContent = (page.equals("") || page == null) ? (element.getText().equals("") ? element.getId() : element.getText()) : page;
        switch (clickType) {
            case Click:
                element.click();
                LOGGER_BASE.info("Click on\"" + btnContent + "\".");
                break;
            case Tap:
                element.tap();
                break;
        }

        LOGGER_BASE.info("Navigating to \"" + btnContent + "\".");

        if (navigationManager != null) {
            navigationManager.setCurrentPage(btnContent);
        }

        return true;
    }

    /**
     * Navigate back.
     *
     * @param mobileContext MobileContext object.
     */
    public static void navigateBack(MobileContext mobileContext) {
        // Api 24 and 25 emulators have no browsers.
        // When you open a link it is opened in WebView Tester.
        // In this case client.driver.navigate().back) successfully navigate back, but throws exception.
        try {
            if (mobileContext.settings.platform == PlatformType.iOS) {
                Wait.sleep(250);
                UIElement backButton = mobileContext.wait.waitForVisible(By.id("Back"), 3, false);
                if (backButton != null) {
                    backButton.tap();
                } else {
                    mobileContext.client.getDriver().navigate().back();
                }
                Wait.sleep(500);
            } else {
                mobileContext.client.getDriver().navigate().back();
            }
            Wait.sleep(500);
        } catch (Exception e) {
            LOGGER_BASE.warn("Navigate back throws exception.");
        }
    }

    /**
     * TODO(): Add docs.
     *
     * @param mobileContext
     */

    public static void navigateForward(MobileContext mobileContext) {
        mobileContext.client.getDriver().navigate().forward();
    }

    /**
     * Scroll to example.
     *
     * @param example       Example name.
     * @param mobileContext mobile context.
     * @return UIElement.
     */
    public static UIElement scrollTo(String example, MobileContext mobileContext, int retryCount) {
        UIElement demoBtn;
        if (mobileContext.settings.platform == PlatformType.Android) {
            demoBtn = mobileContext.wait.waitForVisible(mobileContext.locators.byText(example, true, false), 3, false);
        } else {
            demoBtn = mobileContext.wait.waitForVisible(MobileBy.AccessibilityId(example), 3, false);
        }

        if (demoBtn == null && retryCount > 0) {
            LOGGER_BASE.info("Scroll to \"" + example + "\" ...");
            demoBtn = mobileContext.gestures.scrollToElement(SwipeElementDirection.DOWN, example, retryCount);
        }

        if (demoBtn == null) {
            Assert.fail("Failed to find \"" + example + "\".");
        }

        LOGGER_BASE.info("Element '" + example + "' successfully found");

        return demoBtn;
    }
}
