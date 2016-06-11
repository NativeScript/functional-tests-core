package functional.tests.core.BasePage;

import functional.tests.core.Appium.Client;
import functional.tests.core.Element.UIElement;
import functional.tests.core.Enums.PlatformType;
import functional.tests.core.Find.Find;
import functional.tests.core.Find.Locators;
import functional.tests.core.Find.Wait;
import functional.tests.core.Log.Log;
import functional.tests.core.Settings.Settings;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.remote.HideKeyboardStrategy;
import org.openqa.selenium.By;
import org.testng.Assert;

/**
 * Created by topuzov on 11/27/2015.
 */
public class BasePage {

    /**
     * Press the back button *
     */
    public static void navigateBack() {
        Client.driver.navigate().back();
        Log.info("Navigate back.");
    }

    /**
     * Hide keyboard *
     */
    public static void hideKeyboard() {
        if (Settings.platform == PlatformType.Andorid) {
            try {
                Client.driver.hideKeyboard();
                Log.info("Hide heyboard.");
            } catch (Exception e) {
                Log.info("Soft keyboard not present.");
            }
        } else {
            By doneButtonLocator = By.name("Done");
            By returnButtonLocator = By.name("Return");
            UIElement doneButton = Find.findElementByLocator(doneButtonLocator, 1);
            if (doneButton != null) {
                ((IOSDriver) Client.driver).hideKeyboard(HideKeyboardStrategy.PRESS_KEY, "Done");
                Log.info("Hide keyboard with Done key.");
            } else if (Find.findElementByLocator(returnButtonLocator, 1) != null) {
                ((IOSDriver) Client.driver).hideKeyboard(HideKeyboardStrategy.PRESS_KEY, "Return");
                Log.info("Hide keyboard with Return key.");
            } else {
                ((IOSDriver) Client.driver).hideKeyboard(HideKeyboardStrategy.TAP_OUTSIDE);
                Log.info("Hide keyboard with tap outside.");
            }
        }
    }

    /**
     * Verify text visible *
     */
    public static void verifyTextVisible(String text) {
        boolean visible = Wait.waitForVisible(Locators.findByTextLocator(text, true), true);
        Log.info("Text " + text + " found.");
    }

    /**
     * Verify text visible *
     */
    public static void verifyTextVisible(String text, boolean exactMatch) {
        boolean visible = Wait.waitForVisible(Locators.findByTextLocator(text, exactMatch), true);
        Log.info("Text " + text + " found.");
    }

    /**
     * Verify text visible *
     */
    public static void verifyElementVisible(By locator, int timeout) {
        UIElement element = Find.findElementByLocator(locator, timeout);
        Assert.assertNotNull(element, "Can not find: " + locator.toString());
    }

    /**
     * Verify text visible *
     */
    public static void verifyElementVisible(By locator) {
        UIElement element = Find.findElementByLocator(locator, Settings.defaultTimeout);
        Assert.assertNotNull(element, "Can not find: " + locator.toString());
    }

    /**
     * Verify page is loaded
     */
    public static void loaded(UIElement element) {
        String className = getClassName();
        if (element != null) {
            Log.info(String.format("%s page loaded.", className));
        } else {
            Assert.fail(String.format("%s NOT loaded.", className));
        }
    }

    private static String getClassName() {
        String className = Thread.currentThread().getStackTrace()[3].getClassName();
        return className;
    }
}
