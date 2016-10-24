package functional.tests.core.Gestures;

import functional.tests.core.Appium.Client;
import functional.tests.core.Element.UIElement;
import functional.tests.core.Enums.PlatformType;
import functional.tests.core.Find.FindHelper;
import functional.tests.core.Find.Locators;
import functional.tests.core.Find.Wait;
import functional.tests.core.Find.WaitHelper;
import functional.tests.core.Log.Log;
import functional.tests.core.Settings.Settings;
import io.appium.java_client.MobileElement;
import io.appium.java_client.SwipeElementDirection;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.testng.Assert;

public class GesturesHelper {

    private Client client;
    private FindHelper find;

    public GesturesHelper(Client client) {
        this.client = client;
        this.find = new FindHelper(this.client);
    }

    public void swipe(SwipeElementDirection direction, int duration, int waitAfterSwipe) {

        Dimension dimensions = Client.driver.manage().window().getSize();
        int width = dimensions.width;
        int height = dimensions.height;
        int centerY = height / 2;
        int centerX = width / 2;

        int initialX = centerX;
        int initialY = centerY;
        int finalX = centerX;
        int finalY = centerY;
        int offset = (int) (height * 0.25D);

        if (direction == SwipeElementDirection.DOWN) {
            initialX = centerX;
            initialY = centerY + offset;
            finalX = centerX;
            finalY = centerY - offset;
        }
        if (direction == SwipeElementDirection.UP) {
            initialX = centerX;
            initialY = centerY - offset;
            finalX = centerX;
            finalY = centerY + offset;
        }
        if (direction == SwipeElementDirection.LEFT) {
            initialX = centerX + offset;
            initialY = centerY;
            finalX = centerX - offset;
            finalY = centerY;
        }
        if (direction == SwipeElementDirection.RIGHT) {
            initialX = centerX - offset;
            initialY = centerY;
            finalX = centerX + offset;
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
                String error = "Failed to swipe " + direction + " with " + duration + " duration.";
                Assert.fail(error);
            }
        }
    }

    // Direction as String
    private void swipeInElement(MobileElement element, String direction, int duration) {

        int centerX = element.getLocation().x + (element.getSize().width / 2);
        int centerY = element.getLocation().y + (element.getSize().height / 2);

        int initialX = centerX;
        int initialY = centerY;
        int finalX = centerX;
        int finalY = centerY;

        int offsetXMin = element.getLocation().x + (element.getSize().width / 2) - (element.getSize().width / 4);
        int offsetXMax = element.getLocation().x + (element.getSize().width / 2) + (element.getSize().width / 4);
        int offsetYMin = element.getLocation().y + (element.getSize().height / 2) - (element.getSize().height / 4);
        int offsetYMax = element.getLocation().y + (element.getSize().height / 2) + (element.getSize().height / 4);

        if (direction.equals("down")) {
            initialX = centerX;
            initialY = offsetYMax;
            finalX = centerX;
            finalY = offsetYMin;
        }
        if (direction.equals("up")) {
            initialX = centerX;
            initialY = offsetYMin;
            finalX = centerX;
            finalY = offsetYMax;
        }
        if (direction.equals("left")) {
            initialX = offsetXMax;
            initialY = centerY;
            finalX = offsetXMin;
            finalY = centerY;
        }
        if (direction.equals("right")) {
            initialX = offsetXMin;
            initialY = centerY;
            finalX = offsetXMax;
            finalY = centerY;
        }

        try {
            Client.driver.swipe(initialX, initialY, finalX, finalY, duration);
            Log.info("Swipe " + direction + " with " + duration + " duration in element with center point " + String.valueOf(centerX) + ":" + String.valueOf(centerY));
        } catch (Exception e) {
            Log.error("Failed to swipe " + direction + " with " + duration + " duration in element with center point " + String.valueOf(centerX) + ":" + String.valueOf(centerY));
        }
    }

    // SwipeElementDirection
    private void swipeInElement(MobileElement element, SwipeElementDirection direction, int duration) {

        int centerX = element.getLocation().x + (element.getSize().width / 2);
        int centerY = element.getLocation().y + (element.getSize().height / 2);

        int initialX = centerX;
        int initialY = centerY;
        int finalX = centerX;
        int finalY = centerY;

        int offsetXMin = element.getLocation().x + (element.getSize().width / 2) - (element.getSize().width / 4);
        int offsetXMax = element.getLocation().x + (element.getSize().width / 2) + (element.getSize().width / 4);
        int offsetYMin = element.getLocation().y + (element.getSize().height / 2) - (element.getSize().height / 4);
        int offsetYMax = element.getLocation().y + (element.getSize().height / 2) + (element.getSize().height / 4);

        if (direction == SwipeElementDirection.DOWN) {
            initialX = centerX;
            initialY = offsetYMax;
            finalX = centerX;
            finalY = offsetYMin;
        }
        if (direction == SwipeElementDirection.UP) {
            initialX = centerX;
            initialY = offsetYMin;
            finalX = centerX;
            finalY = offsetYMax;
        }
        if (direction == SwipeElementDirection.LEFT) {
            initialX = offsetXMax;
            initialY = centerY;
            finalX = offsetXMin;
            finalY = centerY;
        }
        if (direction == SwipeElementDirection.RIGHT) {
            initialX = offsetXMin;
            initialY = centerY;
            finalX = offsetXMax;
            finalY = centerY;
        }

        try {
            Client.driver.swipe(initialX, initialY, finalX, finalY, duration);
            Log.info("Swipe " + direction + " with " + duration + " duration in element with center point " + String.valueOf(centerX) + ":" + String.valueOf(centerY));
        } catch (Exception e) {
            Log.error("Failed to swipe " + direction + " with " + duration + " duration in element with center point " + String.valueOf(centerX) + ":" + String.valueOf(centerY));
        }
    }

    private void swipeFromCorner(SwipeElementDirection direction, int duration, int waitAfterSwipe) {

        int initialX = 0, initialY = 0, finalX = 0, finalY = 0;

        Dimension dimensions = Client.driver.manage().window().getSize();
        int width = dimensions.width;
        int height = dimensions.height;
        int centerY = height / 2;
        int centerX = width / 2;

        int left = 3;
        int top = 7;
        int right = width - 3;
        int bottom = height - 7;
        int offset = (int) (height * 0.5D);

        if (direction == SwipeElementDirection.DOWN) {
            initialX = centerX;
            initialY = top;
            finalX = centerX;
            finalY = top + offset;
        }
        if (direction == SwipeElementDirection.UP) {
            initialX = centerX;
            initialY = bottom;
            finalX = centerX;
            finalY = bottom - offset;
        }
        if (direction == SwipeElementDirection.LEFT) {
            initialX = right;
            initialY = centerY;
            finalX = right - offset;
            finalY = centerY;
        }
        if (direction == SwipeElementDirection.RIGHT) {
            initialX = left;
            initialY = centerY;
            finalX = left + offset;
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
                String error = "Failed to swipe " + direction + " with " + duration + " duration.";
                Assert.fail(error);
            }
        }
    }

    public void swipe(SwipeElementDirection direction, int duration) {
        this.swipe(direction, duration, 0);
    }

    public void swipe(MobileElement element, String direction, int duration) {
        this.swipeInElement(element, direction, duration);
    }

    public UIElement swipeToElement(SwipeElementDirection direction, String elementText, int duration, int retryCount) {
        return this.swipeToElement(direction, Locators.byText(elementText), duration, retryCount);
    }

    public UIElement swipeToElement(SwipeElementDirection direction, By locator, int duration, int retryCount) {
        Log.info("Swipe " + direction.toString() + " to " + locator.toString());
        WaitHelper helper = new WaitHelper(this.client);

        for (int i = 0; i < retryCount; i++) {
            UIElement element = helper.waitForVisible(locator, 2, false);
            if ((element != null)) {
                Log.info("Element found by locator \"" + locator.toString() + "\".");
                return element;
            } else {
                Log.info("Swipe " + direction.toString() + " to " + locator.toString());
                this.swipe(direction, duration, Settings.defaultTapDuration * 2);
            }
            if (i == retryCount - 1) {
                Log.info("Element not found after " + String.valueOf(i + 1) + " swipes by locator \"" + locator.toString() + "\".");
            }
        }
        return null;
    }

    public void swipeFromCorner(SwipeElementDirection direction, int duration) {
        this.swipeFromCorner(direction, duration, 0);
    }
}