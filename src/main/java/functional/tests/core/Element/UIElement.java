package functional.tests.core.Element;

import functional.tests.core.Appium.Client;
import functional.tests.core.Enums.PlatformType;
import functional.tests.core.Find.Find;
import functional.tests.core.Find.Locators;
import functional.tests.core.Find.Wait;
import functional.tests.core.Log.Log;
import functional.tests.core.Settings.Settings;
import io.appium.java_client.MobileElement;
import io.appium.java_client.MultiTouchAction;
import io.appium.java_client.SwipeElementDirection;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import org.apache.commons.lang.reflect.FieldUtils;
import org.opencv.core.Rect;
import org.openqa.selenium.*;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.RemoteWebElement;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UIElement {

    private MobileElement element;

    public UIElement(MobileElement element) {
        this.element = element;
    }

    public String getId(){
        return this.element.getId();
    }
    public void swipe(SwipeElementDirection direction, int duration) {
        swipeInElement(direction.toString(), duration);
    }

    public static void swipeFromCorner(SwipeElementDirection direction, int duration) {
        swipeFromCorner(direction, duration, 0);
    }

    public static void swipe(SwipeElementDirection direction, int duration, int waitAfterSwipe) {

        // In iOS, the swipe gesture requires a short duration with Appium 1.5
        if (Settings.platform == PlatformType.iOS) {
            if (duration >= 100) {
                duration = duration / 10;
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
                String error = "Swipe " + direction + " with " + duration + " duration failed.";
                Log.error(error);
                Assert.fail(error);
            }
        }
    }

    private static void swipeFromCorner(SwipeElementDirection direction, int duration, int waitAfterSwipe) {

        int initialX = 0, initialY = 0, finalX = 0, finalY = 0;

        // In iOS, the swipe gesture requires a short duration with Appium 1.5
        if (Settings.platform == PlatformType.iOS) {
            if (duration >= 100) {
                duration = duration / 10;
            }
        }

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
                String error = "Swipe " + direction + " with " + duration + " duration failed.";
                Log.error(error);
                Assert.fail(error);
            }
        }
    }

    public String getAttribute(String value) {
        return this.element.getAttribute(value);
    }

    public String getText() {
        String elementText = null;
        try {
            elementText = this.element.getText();
        } catch (Exception e) {
        }
        return elementText;
    }

    public void setText(String value) {
        if (Settings.platform == PlatformType.Andorid) {
            // Tap at the and of the text box (this is very important)
            int currentLegth = this.element.getText().length();
            int x = this.element.getLocation().getX() + this.element.getSize().width - 5;
            int y = this.element.getLocation().getY() + (this.element.getSize().height / 3);
            Client.driver.tap(1, x, y, Settings.defaultTapDuration);
            Wait.sleep(Settings.defaultTapDuration);

            // Clean old value
            for (int l = 0; l < currentLegth; l++) {
                ((AndroidDriver) Client.driver).pressKeyCode(67);
                Wait.sleep(100);
            }
            Log.info("Clean old value of edit field.");
            Wait.sleep(Settings.defaultTapDuration);

            // Set new value
            this.sendKeys(value);
            Wait.sleep(Settings.defaultTapDuration);
        } else {
            this.element.click();
            try {
                // If keyboard is above text field clear throws exception.
                this.element.clear();
                Log.info("Clean old value of edit field.");
            } catch (Exception e) {
                Log.error("Failed to clean old value.");
            }
            Wait.sleep(Settings.defaultTapDuration);
            this.sendKeys(value);
            Wait.sleep(Settings.defaultTapDuration);
        }
        Log.info("Set value of text field: " + value);
    }

    public UIElement findElementById(String id) {
        return new UIElement(this.element.findElementById(id));
    }

    public Dimension getSize() {
        return this.element.getSize();
    }

    public boolean isSelected() {
        return this.element.isSelected();
    }

    public boolean isEnabled() {
        return this.element.isEnabled();
    }

    public UIElement findElement(By by) {
        return new UIElement(this.element.findElement(by));
    }

    public void sendKeys(String value) {
        this.element.sendKeys(value);
    }

    public String getTagName() {
        String tagName = null;
        try {
            tagName = this.element.getTagName();
        } catch (Exception e) {
        }
        return tagName;
    }

    public String getCoordinates() {
        return String.valueOf(this.element.getCenter().x) + ":" + String.valueOf(this.element.getCenter().y);
    }

    public String getDescription() {
        String elementText = this.getText();
        if (elementText != null) {
            return elementText;
        } else {
            String elementTag = getTagName();
            if (elementTag != null) {
                return elementTag + " at " + getCoordinates();
            } else {
                return "OldElement at " + getCoordinates();
            }
        }
    }

    public String getXpath() {

        String foundBy = "";

        try {
            foundBy = FieldUtils.readField(this.element, "foundBy", true).toString();
        } catch (IllegalAccessException e) {
            Log.error("Failed to get find filed 'foundBy' of element: " + this.getDescription());
        }

        String[] split = foundBy.split("xpath: ");
        String xpathString = split[1];

        return xpathString;
    }

    public void tap(int fingers, int duration, int waitAfterTap) {
        this.element.tap(fingers, duration);
        if (waitAfterTap > 0) {
            Wait.sleep(waitAfterTap);
        }
        try {
            Log.info("Tap on \"" + this.getDescription() + "\"");
        } catch (Exception e) {
            Log.error(e.getMessage());
        }
    }

    public void tap() {
        this.tap(1, Settings.defaultTapDuration, Settings.defaultTapDuration);
    }

    public void tap(int fingers, int duration) {
        this.tap(fingers, duration, Settings.defaultTapDuration);
    }

    public void tap(int fingers) {
        this.tap(fingers, Settings.defaultTapDuration, Settings.defaultTapDuration);
    }

    public void tap(MobileElement element) {
        this.tap(1, Settings.defaultTapDuration, Settings.defaultTapDuration);
    }

    public void swipe(String direction, int duration) {
        swipeInElement(direction, duration);
    }

    public void swipeFromCorner(String direction, int duration) {
        swipeInElementFromCorner(direction, duration);
    }

    public void doubleTap() {
        Log.info("Double Tap: "); // + Elements.getElementDetails(element));
        if (Settings.platform == PlatformType.Andorid) {

            Double x = (double) this.element.getLocation().x
                    + (double) (this.element.getSize().width / 2);
            Double y = (double) this.element.getLocation().y
                    + (double) (this.element.getSize().height / 2);
            JavascriptExecutor js = (JavascriptExecutor) Client.driver;
            HashMap<String, Double> tapObject = new HashMap<String, Double>();
            tapObject.put("x", x);
            tapObject.put("y", y);
            tapObject.put("touchCount", (double) 1);
            tapObject.put("tapCount", (double) 1);
            tapObject.put("duration", 0.05);
            js.executeScript("mobile: tap", tapObject);
            js.executeScript("mobile: tap", tapObject);
        }
        if (Settings.platform == PlatformType.iOS) {
            RemoteWebElement e = (RemoteWebElement) this.element;
            ((RemoteWebDriver) Client.driver).executeScript("au.getElement('" + e.getId() + "').tapWithOptions({tapCount:2});");
        }
    }

    public void longPress(int duration) {
        Log.info("LongPress: "); // + Elements.getElementDetails(element));
        TouchAction action = new TouchAction(Client.driver);
        action.press(this.element).waitAction(duration).release().perform();
    }

    public void pinch() {

        Log.info("Pinch: "); // + Elements.getElementDetails(element));

        if (Settings.platform == PlatformType.Andorid) {
            TouchAction action1 = new TouchAction(Client.driver);
            TouchAction action2 = new TouchAction(Client.driver);

            int elementWidth = this.element.getSize().width;
            int elementHeight = this.element.getSize().height;

            action1.press(this.element, 10, 10).moveTo(this.element, 50, 50);

            action2.press(this.element, elementWidth - 10, elementHeight - 10)
                    .moveTo(this.element, elementWidth - 50, elementHeight - 50);

            MultiTouchAction multiAction = new MultiTouchAction(Client.driver);
            multiAction.add(action1);
            multiAction.add(action2);
            multiAction.perform();
        }
        if (Settings.platform == PlatformType.iOS) {
            TouchAction action1 = new TouchAction(Client.driver);
            TouchAction action2 = new TouchAction(Client.driver);

            int elementWidth = this.element.getSize().width;
            int elementHeight = this.element.getSize().height;

            action1.press(this.element, 10, 10).moveTo(this.element, 50, 50).release();

            action2.press(this.element, elementWidth - 10, elementHeight - 10)
                    .moveTo(this.element, elementWidth - 50, elementHeight - 50)
                    .release();

            MultiTouchAction multiAction = new MultiTouchAction(Client.driver);
            multiAction.add(action1);
            multiAction.add(action2);
            multiAction.perform();
        }
    }

    public void rotate() {
        Log.info("Rotate: "); // + Elements.getElementDetails(element));

        if (Settings.platform == PlatformType.Andorid) {
            TouchAction action1 = new TouchAction(Client.driver);
            TouchAction action2 = new TouchAction(Client.driver);

            int elementWidth = this.element.getSize().width;
            int elementHeight = this.element.getSize().height;

            action1.press(this.element, 10, 10).moveTo(this.element, 10, 50);

            action2.press(this.element, elementWidth - 10, elementHeight - 10)
                    .moveTo(this.element, elementWidth - 10, elementHeight - 50);

            MultiTouchAction multiAction = new MultiTouchAction(Client.driver);
            multiAction.add(action1);
            multiAction.add(action2);
            multiAction.perform();
        }
        if (Settings.platform == PlatformType.iOS) {
            TouchAction action1 = new TouchAction(Client.driver);
            TouchAction action2 = new TouchAction(Client.driver);

            int elementWidth = this.element.getSize().width;
            int elementHeight = this.element.getSize().height;

            action1.press(this.element, 10, 10).moveTo(this.element, 10, 50).release();

            action2.press(this.element, elementWidth - 10, elementHeight - 10)
                    .moveTo(this.element, elementWidth - 10, elementHeight - 50)
                    .release();

            MultiTouchAction multiAction = new MultiTouchAction(Client.driver);
            multiAction.add(action1);
            multiAction.add(action2);
            multiAction.perform();
        }
    }

    public void pan(String direction, int duration) {
        Log.info("Pan: "); // + Elements.getElementDetails(element));
        this.swipeInElement(direction, duration);
    }

    public void dragAndDrop(int xOffset, int yOffset, int duration) {
        Point point = this.element.getLocation();
        Client.driver.swipe(point.getX(), point.getY(), point.getX() - xOffset, point.getY() - yOffset, duration);
    }

    private void swipeInElement(String direction, int duration) {

        // In iOS, the swipe gesture requires a short duration with Appium 1.5
        if (Settings.platform == PlatformType.iOS) {
            if (duration >= 100) {
                duration = duration / 10;
            }
        }

        int centerX = this.element.getLocation().x + (this.element.getSize().width / 2);
        int centerY = this.element.getLocation().y + (this.element.getSize().height / 2);

        int initialX = centerX;
        int initialY = centerY;
        int finalX = centerX;
        int finalY = centerY;

        int offsetXMin = this.element.getLocation().x
                + (this.element.getSize().width / 2) - (this.element.getSize().width / 4);
        int offsetXMax = this.element.getLocation().x
                + (this.element.getSize().width / 2) + (this.element.getSize().width / 4);
        int offsetYMin = this.element.getLocation().y
                + (this.element.getSize().height / 2)
                - (this.element.getSize().height / 4);
        int offsetYMax = this.element.getLocation().y
                + (this.element.getSize().height / 2)
                + (this.element.getSize().height / 4);

        if (direction.equalsIgnoreCase("down")) {
            initialX = centerX;
            initialY = offsetYMax;
            finalX = centerX;
            finalY = offsetYMin;
        }
        if (direction.equalsIgnoreCase("up")) {
            initialX = centerX;
            initialY = offsetYMin;
            finalX = centerX;
            finalY = offsetYMax;
        }
        if (direction.equalsIgnoreCase("left")) {
            initialX = offsetXMax;
            initialY = centerY;
            finalX = offsetXMin;
            finalY = centerY;
        }
        if (direction.equalsIgnoreCase("right")) {
            initialX = offsetXMin;
            initialY = centerY;
            finalX = offsetXMax;
            finalY = centerY;
        }

        try {
            Client.driver.swipe(initialX, initialY, finalX, finalY, duration);
            Log.info("Swipe " + direction + " with " + duration
                    + " duration in element with center point "
                    + String.valueOf(centerX) + ":" + String.valueOf(centerY));
        } catch (Exception e) {
            Log.error("Swipe " + direction + " with " + duration
                    + " duration in element with center point "
                    + String.valueOf(centerX) + ":" + String.valueOf(centerY)
                    + "failed.");
        }
    }

    private void swipeInElementFromCorner(String direction, int duration) {

        // In iOS, the swipe gesture requires a short duration with Appium 1.5
        if (Settings.platform == PlatformType.iOS) {
            if (duration >= 100) {
                duration = duration / 10;
            }
        }

        int initialX = 0, initialY = 0, finalX = 0, finalY = 0;

        int left = this.element.getLocation().x;
        int top = this.element.getLocation().y;
        int right = this.element.getLocation().x + (this.element.getSize().width);
        int bottom = this.element.getLocation().y + (this.element.getSize().height);

        int offsetLeft = left + (this.element.getSize().width);
        int offsetTop = top + (this.element.getSize().height);
        int offsetRight = right - (this.element.getSize().width);
        int offsetBottom = bottom - (this.element.getSize().height);

        if (direction.equals("down")) {
            initialX = left;
            initialY = top;
            finalX = left;
            finalY = offsetTop;
        }
        if (direction.equals("up")) {
            initialX = right;
            initialY = bottom;
            finalX = right;
            finalY = offsetBottom;
        }
        if (direction.equals("left")) {
            initialX = right;
            initialY = bottom;
            finalX = offsetRight;
            finalY = bottom;
        }
        if (direction.equals("right")) {
            initialX = left;
            initialY = top;
            finalX = offsetLeft;
            finalY = top;
        }

        try {
            Client.driver.swipe(initialX, initialY, finalX, finalY, duration);
            Log.info("Swipe " + direction + " with " + duration);
        } catch (Exception e) {
            Log.error("Swipe " + direction + " with " + duration + " failed.");
        }
    }

    public UIElement swipeToElement(SwipeElementDirection direction, String elementText, int duration, int retryCount) {
        return this.swipeToElement(direction, Locators.findByTextLocator(elementText, true), duration, retryCount);

//        for (int i = 0; i < retryCount; i++) {
//            UIElement element = Find.findElementByLocator(Locators.findByTextLocator(elementText, true), 2);
//            if ((element != null) && (element.isDisplayed())) {
//                Log.info(elementText + " found.");
//                return element;
//            } else {
//                Log.info("Swipe " + direction.toString() + " to " + elementText);
//                swipe(direction, duration, Settings.defaultTapDuration * 2);
//            }
//            if (i == retryCount - 1) {
//                Log.error(elementText + " not found after " + String.valueOf(i + 1) + " swipes.");
//            }
//        }
//        return null;
    }

    public UIElement swipeToElement(SwipeElementDirection direction, By locator, int duration, int retryCount) {
        Log.info("Swipe " + direction.toString() + " to " + locator.toString());
        for (int i = 0; i < retryCount; i++) {
            UIElement element = Find.findElementByLocator(locator, 2);
            if ((element != null) && (element.isDisplayed())) {
                Log.info("OldElement found: " + locator.toString());
                return element;
            } else {
                Log.info("Swipe " + direction.toString() + " to " + locator.toString());
                swipe(direction, duration, Settings.defaultTapDuration * 2);
            }
            if (i == retryCount - 1) {
                Log.info("OldElement not found after " + String.valueOf(i + 1) + " swipes." + locator.toString());
            }
        }
        return null;
    }

    public boolean isDisplayed() {
        return this.element.isDisplayed();
    }

    public Point getCenter() {
        return this.element.getCenter();
    }

    public Point getLocation() {
        return this.element.getLocation();
    }

    public void click() {
        this.element.click();
    }

    public ArrayList<UIElement> findElements(By by) {
        List<MobileElement> elements = this.element.findElements(by);
        ArrayList<UIElement> UIElements = new ArrayList<>();

        for (MobileElement elment : elements) {
            UIElements.add(new UIElement(element));
        }

        return UIElements;
    }

    public Rect getElementRect() {
        Rect rect = new Rect();

        rect.height = this.element.getSize().getHeight();
        rect.width = this.element.getSize().getWidth();
        rect.x = this.element.getLocation().getX();
        rect.y = this.element.getLocation().getY();

        return rect;
    }

    public java.awt.Rectangle getUIRectangle() {
        java.awt.Rectangle rect = new java.awt.Rectangle(
                this.element.getLocation().getX(),
                this.element.getLocation().getY(),
                this.element.getSize().getWidth(),
                this.element.getSize().getHeight());

        return rect;
    }
}
