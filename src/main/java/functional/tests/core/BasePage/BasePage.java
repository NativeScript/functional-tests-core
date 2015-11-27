package functional.tests.core.BasePage;

import functional.tests.core.Appium.Client;
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

}
