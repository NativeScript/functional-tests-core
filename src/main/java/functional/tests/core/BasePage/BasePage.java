package functional.tests.core.BasePage;

import functional.tests.core.Appium.Client;
import functional.tests.core.Exceptions.AppiumException;
import functional.tests.core.Find.Locators;
import functional.tests.core.Find.Wait;
import functional.tests.core.Log.Log;

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
     * Verify text visible *
     */
    public static void verifyTextVisible(String text) throws AppiumException {
        Wait.waitForVisible(Locators.findByTextLocator(text, true), true);
        Log.info("Text " + text + " found.");
    }
}
