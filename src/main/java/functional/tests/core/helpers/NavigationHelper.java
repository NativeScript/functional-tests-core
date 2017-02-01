package functional.tests.core.helpers;

import functional.tests.core.basetest.Context;
import functional.tests.core.element.UIElement;
import functional.tests.core.enums.ClickType;
import functional.tests.core.enums.PlatformType;
import functional.tests.core.find.Find;
import functional.tests.core.log.LoggerBase;
import io.appium.java_client.SwipeElementDirection;
import org.testng.Assert;

/**
 * TODO(): Add docs.
 */
public class NavigationHelper {

    private static final LoggerBase LOGGER_BASE = LoggerBase.getLogger("NavigationHelper");

    /**
     * TODO(): Add docs.
     *
     * @param demoPath
     * @param context
     * @return
     */
    public static boolean navigateTo(String demoPath, Context context, int scrollToElementRetriesCount) {
        return navigateTo(demoPath, null, context, scrollToElementRetriesCount);
    }

    /**
     * TODO(): Add docs.
     *
     * @param demoPath
     * @param navigationManager
     * @param context
     * @return
     */
    public static boolean navigateTo(String demoPath, NavigationManager navigationManager, Context context, int scrollToElementRetriesCount) {
        LOGGER_BASE.info("Navigating to \"" + demoPath + "\".");
        Find find = context.find;
        String splitSeparator = demoPath.contains("/") ? "/" : ".";
        String[] demos = demoPath.split(splitSeparator);

        if (demos.length == 0) {
            demos = new String[1];
            demos[0] = demoPath;
        }

        for (int i = 0; i < demos.length; i++) {
            String btnText = demos[i];
            UIElement demoBtn = scrollTo(btnText, context, scrollToElementRetriesCount);
            if (demoBtn == null) {
                return false;
            }
            demoBtn.tap();

            if (navigationManager != null) {
                navigationManager.setCurrentPage(btnText);
            }

            if (i < demos.length - 1) {
                String nextBtnText = demos[i + 1];
                UIElement nextDemoBtn = find.byText(nextBtnText, 3);
                if (nextDemoBtn == null) {
                    nextDemoBtn = find.byText(nextBtnText, 3);
                }
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
        String btnContent = (page == "" || page == null) ? (element.getText() == "" ? element.getId() : element.getText()) : page;
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
     * TODO(): Add docs.
     *
     * @param context
     */
    public static void navigateBack(Context context) {
        // TODO(svetli): Can we use App.navigate back and move iOS 10 logic there?
        if (context.settings.platform == PlatformType.iOS) {
            if (context.settings.platformVersion >= 10) {
                Find find = context.find;
                LOGGER_BASE.debug("In iOS 10 navigate back is using client.driver.findElement(Locators.byText(\"Back\")");
                UIElement btnBack = find.byText("Back");
                if (btnBack != null && btnBack.isDisplayed()) {
                    btnBack.tap();
                }
            } else {
                context.client.getDriver().navigate().back();
            }
        } else if (context.settings.platform == PlatformType.Andorid) {
            // Api 24 and 25 emulators have no browsers.
            // When you open a link it is opened in WebView Tester.
            // In this case client.driver.navigate().back() successfully navigate back, but throws exception.
            if (context.settings.platformVersion >= 7.0) {
                try {
                    context.client.getDriver().navigate().back();
                } catch (Exception e) {
                    LOGGER_BASE.warn("Navigate back throws exception.");
                }
            } else {
                context.client.getDriver().navigate().back();
            }
        }
    }

    /**
     * TODO(): Add docs.
     *
     * @param context
     */
    public static void navigateForward(Context context) {
        context.client.getDriver().navigate().forward();
    }

    /**
     * TODO(): Add docs.
     *
     * @param example
     * @param context
     * @return
     */
    public static UIElement scrollTo(String example, Context context, int retryCount) {
        UIElement demoBtn = context.wait.waitForVisible(context.locators.byText(example, true, false), 3, false);

        if (demoBtn == null && retryCount > 0) {
            LOGGER_BASE.info("Sroll to \"" + example + "\" ...");
            demoBtn = context.gestures.scrollToElement(SwipeElementDirection.DOWN, example, 5);
        }

        if (demoBtn == null) {
            Assert.fail("Failed to find \"" + example + "\".");
        }

        LOGGER_BASE.info("Element '" + example + "' successfully found");

        return demoBtn;
    }
}
