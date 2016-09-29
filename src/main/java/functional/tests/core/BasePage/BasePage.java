package functional.tests.core.BasePage;

import functional.tests.core.Appium.Client;
import functional.tests.core.BaseTest.UIBaseTestExtended;
import functional.tests.core.Element.UIElement;
import functional.tests.core.Enums.PlatformType;
import functional.tests.core.Find.*;
import functional.tests.core.Gestures.GesturesHelper;
import functional.tests.core.Log.Log;
import functional.tests.core.Settings.Settings;
import io.appium.java_client.SwipeElementDirection;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.remote.HideKeyboardStrategy;
import org.openqa.selenium.By;
import org.testng.Assert;

public class BasePage {
    public Client client;
    public GesturesHelper gestures;
    public FindHelper find;
    public WaitHelper wait;

    public BasePage() {
    }

    public BasePage(Client client) {
        this.client = client;
        this.gestures = new GesturesHelper(client);
        this.find = new FindHelper(client);
        this.wait = new WaitHelper(client);
    }

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
            By doneButtonLocator = By.id("Done");
            By returnButtonLocator = By.id("Return");
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
        String className = UIBaseTestExtended.getTestNameToWriteFile();
        if (element != null) {
            Log.info(String.format("%s page loaded.", className));
        } else {
            Assert.fail(String.format("%s NOT loaded.", className));
        }
    }

    private static String getClassName() {
        String className = UIBaseTestExtended.getTestNameToWriteFile();
        return className;
    }

    public UIElement scrollTo(String example) {
        Log.info("Swiping to \"" + example + "\" ...");
        UIElement demoBtn = this.find.byText(example, 3);

        int count = 0;
        while (demoBtn == null) {
            if (count <= 3) {
                demoBtn = this.gestures.swipeToElement(SwipeElementDirection.DOWN, example, 750, 5);
            } else {
                Assert.fail("Failed to swipe to \"" + example + "\".");
            }
            count++;
        }
        Log.info("Swiped to '" + example + "' successfully.");

        return demoBtn;
    }
}
