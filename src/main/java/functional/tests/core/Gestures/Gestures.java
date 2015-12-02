package functional.tests.core.Gestures;

import com.sun.javafx.scene.traversal.Direction;
import functional.tests.core.Appium.Client;
import functional.tests.core.Element.Element;
import functional.tests.core.Find.Find;
import functional.tests.core.Find.Locators;
import functional.tests.core.Find.Wait;
import functional.tests.core.Log.Log;
import functional.tests.core.Settings.Settings;
import io.appium.java_client.MobileElement;
import org.openqa.selenium.Dimension;
import org.testng.Assert;

public class Gestures {

    public static void tap(MobileElement element, int fingers, int duration) {
        element.tap(fingers, duration);
        Wait.sleep(250);
        Log.info("Tap " + Element.getDescription(element));
    }

    public static void tap(MobileElement element, int fingers) {
        tap(element, fingers, Settings.defaultTapDuration);
    }

    public static void tap(MobileElement element) {
        tap(element, 1, Settings.defaultTapDuration);
    }

    private static void swipe(Direction direction, int duration) {

        Dimension dimensions = Client.driver.manage().window().getSize();
        int width = dimensions.width;
        int height = dimensions.height;
        int centerY = height / 2;
        int centerX = width / 2;

        int initialX = centerX;
        int initialY = centerY;
        int finalX = centerX;
        int finalY = centerY;

        if (direction == Direction.DOWN) {
            initialX = centerX;
            initialY = centerY + centerY / 2 + centerY / 4 - centerY / 8;
            finalX = centerX;
            finalY = centerY - centerY / 2 - centerY / 4 + centerY / 8;
        }
        if (direction == Direction.UP) {
            initialX = centerX;
            initialY = centerY - centerY / 2 - centerY / 4;
            finalX = centerX;
            finalY = centerY + centerY / 2 + centerY / 4;
        }
        if (direction == Direction.LEFT) {
            initialX = centerX + centerX / 2 + centerX / 4;
            initialY = centerY;
            finalX = centerX - centerX / 2 - centerX / 4;
            finalY = centerY;
        }
        if (direction == Direction.RIGHT) {
            initialX = centerX - centerX / 2 - centerX / 4;
            initialY = centerY;
            finalX = centerX + centerX / 2 + centerX / 4;
            finalY = centerY;
        }

        try {
            Client.driver.swipe(initialX, initialY, finalX, finalY, duration);
            Log.info("Swipe " + direction + " with " + duration + " duration.");
            Thread.sleep(100);
        } catch (Exception e) {
            String error = "Swipe " + direction + " with " + duration + " duration failed.";
            Log.error(error);
            Assert.fail(error);
        }
    }

    /**
     * Scroll down until element is visible via swipe gesture *
     */
    public static MobileElement swipeDownToElement(String elementText, int duration) {
        Log.error("Swipe down to " + elementText);
        for (int i = 0; i < 10; i++) {
            MobileElement element = Find.findElementByLocator(Locators.findByTextLocator(elementText, true), 0);
            if (element != null) {
                Log.info(elementText + "found.");
                return element;
            } else {
                swipe(Direction.DOWN, duration);
                i++;
            }
            if (i == 10) {
                Log.error(elementText + " not found after 10 swipes.");
            }
        }
        return null;
    }

    /**
     * Scroll down until element is visible via swipe gesture *
     */
    public static MobileElement swipeUpToElement(String elementText, int duration) {
        Log.error("Swipe up to " + elementText);
        for (int i = 0; i < 10; i++) {
            MobileElement element = Find.findElementByLocator(Locators.findByTextLocator(elementText, true), 0);
            if (element != null) {
                Log.info(elementText + "found.");
                return element;
            } else {
                swipe(Direction.UP, duration);
                i++;
            }
            if (i == 10) {
                Log.error(elementText + " not found after 10 swipes.");
            }
        }
        return null;
    }
}
