package functional.tests.core.BasePage;

import functional.tests.core.Appium.Client;
import functional.tests.core.Find.Find;
import functional.tests.core.Find.Locators;
import functional.tests.core.Find.Wait;
import functional.tests.core.Log.Log;
import functional.tests.core.Settings.Settings;
import io.appium.java_client.MobileElement;
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
        try {
            Client.driver.hideKeyboard();
            Log.info("Hide heyboard.");
        } catch (Exception e) {
            Log.info("Soft keyboard not present.");
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
        MobileElement element = Find.findElementByLocator(locator, timeout);
        Assert.assertNotNull(element, "Can not find: " + locator.toString());
    }

    /**
     * Verify text visible *
     */
    public static void verifyElementVisible(By locator) {
        MobileElement element = Find.findElementByLocator(locator, Settings.defaultTimeout);
        Assert.assertNotNull(element, "Can not find: " + locator.toString());
    }
}
