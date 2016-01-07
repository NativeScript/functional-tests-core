package functional.tests.core.Gestures;

import functional.tests.core.Appium.Client;
import functional.tests.core.Element.Element;
import functional.tests.core.Enums.PlatformType;
import functional.tests.core.Find.Find;
import functional.tests.core.Find.Locators;
import functional.tests.core.Find.Wait;
import functional.tests.core.Log.Log;
import functional.tests.core.Settings.Settings;
import io.appium.java_client.MobileElement;
import io.appium.java_client.SwipeElementDirection;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.testng.Assert;

public class Gestures {

    public static void tap(MobileElement element, int fingers, int duration, int waitAfterTap) {
        element.tap(fingers, duration);
        if (waitAfterTap > 0) {
            Wait.sleep(waitAfterTap);
        }
        Log.info("Tap " + Element.getDescription(element));
    }

    public static void tap(MobileElement element, int fingers) {
        tap(element, fingers, Settings.defaultTapDuration, Settings.defaultTapDuration);
    }

    public static void tap(MobileElement element) {
        tap(element, 1, Settings.defaultTapDuration, Settings.defaultTapDuration);
    }

    public static void swipe(SwipeElementDirection direction, int duration, int waitAfterSwipe) {

        // In iOS swipe with duration < 0.5 seconds is not possible
        if (Settings.platform == PlatformType.iOS) {
            if (duration < 500) {
                duration = 500;
            }
        }

        Dimension dimensions = Client.driver.manage().window().getSize();
        int width = dimensions.width;
        int height = dimensions.height;
        int centerY = height / 2;
        int centerX = width / 2;

        int initialX = centerX;
        int initialY = centerY;
        int finalX = centerX;
        int finalY = centerY;

        if (direction == SwipeElementDirection.DOWN) {
            initialX = centerX;
            initialY = centerY + centerY / 2 + centerY / 4 - centerY / 8;
            finalX = centerX;
            finalY = centerY - centerY / 2 - centerY / 4 + centerY / 8;
        }
        if (direction == SwipeElementDirection.UP) {
            initialX = centerX;
            initialY = centerY - centerY / 2 - centerY / 4;
            finalX = centerX;
            finalY = centerY + centerY / 2 + centerY / 4;
        }
        if (direction == SwipeElementDirection.LEFT) {
            initialX = centerX + centerX / 2 + centerX / 4;
            initialY = centerY;
            finalX = centerX - centerX / 2 - centerX / 4;
            finalY = centerY;
        }
        if (direction == SwipeElementDirection.RIGHT) {
            initialX = centerX - centerX / 2 - centerX / 4;
            initialY = centerY;
            finalX = centerX + centerX / 2 + centerX / 4;
            finalY = centerY;
        }

        try {
            Client.driver.swipe(initialX, initialY, finalX, finalY, duration);
            Log.info("Swipe " + direction + " with " + duration + " duration.");
            if (waitAfterSwipe > 0) {
                Wait.sleep(waitAfterSwipe);
            }
        } catch (Exception e) {
            if ((Settings.platform == PlatformType.Andorid) && (Settings.platformVersion.contains("4.2"))) {
                Log.info("Known issue: Swipe works on Api17, but error is thrown.");
            } else {
                String error = "Swipe " + direction + " with " + duration + " duration failed.";
                Log.error(error);
                Assert.fail(error);
            }
        }
    }

    public static void swipe(SwipeElementDirection direction, int duration) {
        swipe(direction, duration, 0);
    }

    /**
     * Scroll down until element is visible via swipe gesture *
     */
    public static MobileElement swipeToElement(SwipeElementDirection direction, String elementText, int duration, int retryCount) {
        Log.info("Swipe " + direction.toString() + " to " + elementText);
        for (int i = 0; i < retryCount; i++) {
            MobileElement element = Find.findElementByLocator(Locators.findByTextLocator(elementText, true), 2);
            if ((element != null) && (element.isDisplayed())) {
                Log.info(elementText + " found.");
                return element;
            } else {
                swipe(direction, duration, Settings.defaultTapDuration * 2);
            }
            if (i == retryCount - 1) {
                Log.error(elementText + " not found after " + String.valueOf(retryCount) + " swipes.");
            }
        }
        return null;
    }

    /**
     * Scroll until element is visible via swipe gesture *
     */
    public static MobileElement swipeToElement(SwipeElementDirection direction, By locator, int duration, int retryCount) {
        Log.info("Swipe " + direction.toString() + " to " + locator.toString());
        for (int i = 0; i < retryCount; i++) {
            MobileElement element = Find.findElementByLocator(locator, 2);
            if ((element != null) && (element.isDisplayed())) {
                Log.info("Element found: " + locator.toString());
                return element;
            } else {
                swipe(direction, duration, Settings.defaultTapDuration * 2);
            }
            if (i == retryCount - 1) {
                Log.info("Element not found after " + String.valueOf(retryCount) + " swipes." + locator.toString());
            }
        }
        return null;
    }
}
