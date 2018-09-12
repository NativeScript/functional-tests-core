package functional.tests.core.mobile.element;

import functional.tests.core.enums.PlatformType;
import functional.tests.core.enums.Position;
import functional.tests.core.enums.SwipeElementDirection;
import functional.tests.core.log.LoggerBase;
import functional.tests.core.mobile.appium.Client;
import functional.tests.core.mobile.basetest.MobileContext;
import functional.tests.core.mobile.basetest.MobileSetupManager;
import functional.tests.core.mobile.find.Wait;
import functional.tests.core.mobile.settings.MobileSettings;
import functional.tests.core.settings.Settings;
import io.appium.java_client.MobileElement;
import io.appium.java_client.MultiTouchAction;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.ElementOption;
import io.appium.java_client.touch.offset.PointOption;
import org.apache.commons.lang.reflect.FieldUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Point;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.RemoteWebElement;

import java.awt.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This object wrappes the Appium MobileElement.
 */
public class UIElement {

    private static final LoggerBase LOGGER_BASE = LoggerBase.getLogger("UIElement");

    public MobileElement element;
    private Client client;
    private MobileContext mobileContext;
    private MobileSettings settings;
    private Rectangle elementRectangle;
    private int offsetX;
    private int offsetY;

    public UIElement(MobileElement element) {
        this.element = element;
        this.mobileContext = MobileSetupManager.getTestSetupManager().getContext();
        this.settings = this.mobileContext.settings;
        this.client = this.mobileContext.client;
        this.offsetX = 0;
        this.offsetY = 0;
    }

    public String getId() {
        return this.element.getId();
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
        if (this.settings.platform == PlatformType.Android) {
            // Tap at the and of the text box (this is very important)
            int currentLegth = this.element.getText().length();
            int x = this.element.getLocation().getX() + this.element.getSize().width - 5;
            int y = this.element.getLocation().getY() + (this.element.getSize().height / 3);
            TouchAction action = new TouchAction(this.client.driver);
            action.tap(PointOption.point(x, y));
            Wait.sleep(Settings.DEFAULT_TAP_DURATION);

            // Clean old value
            for (int l = 0; l < currentLegth; l++) {
                ((AndroidDriver) this.client.driver).pressKeyCode(67);
                Wait.sleep(100);
            }
            LOGGER_BASE.info("Clean old value of edit field.");
            Wait.sleep(Settings.DEFAULT_TAP_DURATION);

            // Set new value
            this.sendKeys(value);
            Wait.sleep(Settings.DEFAULT_TAP_DURATION);
        } else {
            this.element.click();
            try {
                // If keyboard is above text field clear throws exception.
                this.element.clear();
                LOGGER_BASE.info("Clean old value of edit field.");
            } catch (Exception e) {
                LOGGER_BASE.error("Failed to clean old value.");
            }
            Wait.sleep(Settings.DEFAULT_TAP_DURATION);
            this.sendKeys(value);
            Wait.sleep(Settings.DEFAULT_TAP_DURATION);
        }
        LOGGER_BASE.info("Set value of text field: " + value);
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
        String elementText = "";
        try {
            elementText = this.getText();
            if (!elementText.isEmpty()) {
                return elementText;
            } else {
                String elementTag = this.getTagName();
                if (elementTag != null) {
                    return elementTag + " at " + this.getCoordinates();
                } else {
                    return "OldElement at " + this.getCoordinates();
                }
            }
        } catch (Exception e) {
            LOGGER_BASE.warn("Could not get the element description");
            LOGGER_BASE.warn(e.getMessage());
        }

        return elementText;
    }

    public String getXpath() {

        String foundBy = "";

        try {
            foundBy = FieldUtils.readField(this.element, "foundBy", true).toString();
        } catch (IllegalAccessException e) {
            LOGGER_BASE.error("Failed to get find filed 'foundBy' of element: " + this.getDescription());
        }

        String[] split = foundBy.split("xpath: ");
        String xpathString = split[1];

        return xpathString;
    }

    public Dimension getSize() {
        return this.element.getSize();
    }

    public Point getCenter() {
        return this.element.getCenter();
    }

    public Point getLocation() {
        return this.element.getLocation();
    }

    public boolean isSelected() {
        return this.element.isSelected();
    }

    public boolean isEnabled() {
        return this.element.isEnabled();
    }

    public boolean isDisplayed() {
        return this.element.isDisplayed();
    }

    public boolean isVisible() {
        if (this.isDisplayed()) {
            Dimension windowRect = this.mobileContext.device.getWindowSize();
            Rectangle elementRect = this.getUIRectangle();
            if (elementRect.getHeight() / 2 + elementRect.getY() > windowRect.getHeight()) {
                return false;
            } else if (elementRect.x < 0 || elementRect.y < 0 || elementRect.width <= 0 || elementRect.height <= 0) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    public UIElement findElement(By by) {
        return new UIElement(this.element.findElement(by));
    }

    public UIElement findElementById(String id) {
        return this.findElement(By.id(id));
    }

    public ArrayList<UIElement> findElements(By by) {
        List<MobileElement> elements = this.element.findElements(by);
        ArrayList<UIElement> uiElements = new ArrayList<>();

        for (MobileElement elment : elements) {
            uiElements.add(new UIElement(this.element));
        }

        return uiElements;
    }

    public void clear() {
        this.element.clear();
    }

    public void sendKeys(String value, Boolean clean) {
        if (clean) {
            this.clear();
        }
        this.element.sendKeys(value);
    }

    public void sendKeys(String value) {
        this.sendKeys(value, false);
    }

    public void click() {
        String text = this.getDescription();
        this.click(text);
    }

    public void click(String description) {
        if (this.settings.platform == PlatformType.Android) {
            this.element.click();
            LOGGER_BASE.info("Click on " + description);
        } else {
            // Due to issue with Appium@1.9.0 fallback to tap();
            this.tap();
        }
    }

    public void tap(int waitAfterTap) {
        String text = this.getDescription();
        new TouchAction(this.client.driver)
                .tap(PointOption.point(this.element.getCenter().getX(), this.element.getCenter().getY()))
                .perform();
        if (waitAfterTap > 0) {
            Wait.sleep(waitAfterTap);
        }

        LOGGER_BASE.info("Tap on \"" + text + "\"");
    }

    public void tap() {
        this.tap(Settings.DEFAULT_TAP_DURATION);
    }

    public void doubleTap() {
        LOGGER_BASE.info("Double Tap: "); // + Elements.getElementDetails(element));
        if (this.settings.platform == PlatformType.Android) {

            Double x = (double) this.element.getLocation().x
                    + (double) (this.element.getSize().width / 2);
            Double y = (double) this.element.getLocation().y
                    + (double) (this.element.getSize().height / 2);
            JavascriptExecutor js = (JavascriptExecutor) this.client.driver;
            HashMap<String, Double> tapObject = new HashMap<String, Double>();
            tapObject.put("x", x);
            tapObject.put("y", y);
            tapObject.put("touchCount", (double) 1);
            tapObject.put("tapCount", (double) 1);
            tapObject.put("duration", 0.05);
            js.executeScript("mobile: tap", tapObject);
            js.executeScript("mobile: tap", tapObject);
        }
        if (this.settings.platform == PlatformType.iOS) {
            RemoteWebElement e = (RemoteWebElement) this.element;
            ((RemoteWebDriver) this.client.driver).executeScript("au.getElement('" + e.getId() + "').tapWithOptions({tapCount:2});");
        }
    }

    /**
     * Long press.
     *
     * @param duration Duration in milliseconds.
     */
    public void longPress(int duration) {
        LOGGER_BASE.info("LongPress at " + this.element.getCenter().toString());
        TouchAction action = new TouchAction(this.client.driver);
        action
                .press(PointOption.point(this.element.getCenter().getX(), this.element.getCenter().getY()))
                .waitAction(WaitOptions.waitOptions(Duration.ofMillis(duration)))
                .release()
                .perform();

    }

    public void pressAndHold() {
        TouchAction action = new TouchAction(this.client.getDriver());
        Rectangle rect = this.getUIRectangle();
        this.client.getDriver().performTouchAction(action.press(PointOption.point(rect.x, rect.y)));
    }

    public void pinch() {

        LOGGER_BASE.info("Pinch: "); // + Elements.getElementDetails(element));

        if (this.settings.platform == PlatformType.Android) {
            TouchAction action1 = new TouchAction(this.client.driver);
            TouchAction action2 = new TouchAction(this.client.driver);

            int elementWidth = this.element.getSize().width;
            int elementHeight = this.element.getSize().height;

            action1.press(ElementOption.element(this.element, 10, 10)).moveTo(ElementOption.element(this.element, 50, 50));

            action2.press(ElementOption.element(this.element, elementWidth - 10, elementHeight - 10))
                    .moveTo(ElementOption.element(this.element, elementWidth - 50, elementHeight - 50));

            MultiTouchAction multiAction = new MultiTouchAction(this.client.driver);
            multiAction.add(action1);
            multiAction.add(action2);
            multiAction.perform();
        }
        if (this.settings.platform == PlatformType.iOS) {
            TouchAction action1 = new TouchAction(this.client.driver);
            TouchAction action2 = new TouchAction(this.client.driver);

            int elementWidth = this.element.getSize().width;
            int elementHeight = this.element.getSize().height;

            action1.press(ElementOption.element(this.element, 10, 10)).moveTo(ElementOption.element(this.element, 50, 50)).release();

            action2.press(ElementOption.element(this.element, elementWidth - 10, elementHeight - 10))
                    .moveTo(ElementOption.element(this.element, elementWidth - 50, elementHeight - 50))
                    .release();

            MultiTouchAction multiAction = new MultiTouchAction(this.client.driver);
            multiAction.add(action1);
            multiAction.add(action2);
            multiAction.perform();
        }
    }

    public void rotate() {
        LOGGER_BASE.info("Rotate: "); // + Elements.getElementDetails(element));

        if (this.settings.platform == PlatformType.Android) {
            TouchAction action1 = new TouchAction(this.client.driver);
            TouchAction action2 = new TouchAction(this.client.driver);

            int elementWidth = this.element.getSize().width;
            int elementHeight = this.element.getSize().height;

            action1.press(ElementOption.element(this.element, 10, 10)).moveTo(ElementOption.element(this.element, 10, 50));

            action2.press(ElementOption.element(this.element, elementWidth - 10, elementHeight - 10))
                    .moveTo(ElementOption.element(this.element, elementWidth - 10, elementHeight - 50));

            MultiTouchAction multiAction = new MultiTouchAction(this.client.driver);
            multiAction.add(action1);
            multiAction.add(action2);
            multiAction.perform();
        }
        if (this.settings.platform == PlatformType.iOS) {
            TouchAction action1 = new TouchAction(this.client.driver);
            TouchAction action2 = new TouchAction(this.client.driver);

            int elementWidth = this.element.getSize().width;
            int elementHeight = this.element.getSize().height;

            action1.press(ElementOption.element(this.element, 10, 10)).moveTo(ElementOption.element(this.element, 10, 50)).release();

            action2.press(ElementOption.element(this.element, elementWidth - 10, elementHeight - 10))
                    .moveTo(ElementOption.element(this.element, elementWidth - 10, elementHeight - 50))
                    .release();

            MultiTouchAction multiAction = new MultiTouchAction(this.client.driver);
            multiAction.add(action1);
            multiAction.add(action2);
            multiAction.perform();
        }
    }

    public void pan(SwipeElementDirection direction, int duration) {
        LOGGER_BASE.info("Pan: "); // + Elements.getElementDetails(element));
        this.swipeInElement(direction, duration, 5);
    }


    /**
     * Move the element to given offset.
     *
     * @param xOffset
     * @param yOffset
     * @param duration
     */
    public void dragAndDrop(int xOffset, int yOffset, int duration) {
        Point point = this.element.getLocation();
        try {
            TouchAction action = new TouchAction(this.client.driver);
            action.press(PointOption.point(point.getX(), point.getY()))
                    .moveTo(PointOption.point(point.getX() + xOffset, point.getY() + yOffset))
                    .release()
                    .perform();
        } catch (Exception ex) {
            // This method throws exception for api17 for Android even though it is working.
        }
    }

    /**
     * Swipes in the bounds of element.
     * Helpful to be used in lists.
     * The initial start position is the top center point of the element
     *
     * @param direction
     * @param waitAfter
     */
    public void scrollInElement(SwipeElementDirection direction, int waitAfter) {
        this.scrollInElement(direction, Position.FromCorner, waitAfter);
    }

    /**
     * Swipes in the bounds of element. Helpful to be used in lists.
     *
     * @param direction
     * @param startPosition
     * @param waitAfter
     */
    public void scrollInElement(SwipeElementDirection direction, Position startPosition, int waitAfter) {
        if (this.elementRectangle == null) {
            this.elementRectangle = this.getUIRectangle();
            if (startPosition == Position.FromCorner) {
                if (this.elementRectangle.getX() == 0 && this.offsetX == 0) {
                    this.offsetX = 10;
                }
                if (this.elementRectangle.getY() == 0 && this.offsetY == 0) {
                    this.offsetY = 10;
                }

                if (this.elementRectangle.width + this.elementRectangle.x == this.mobileContext.device.getWindowSize().width
                        && this.offsetX == 0) {
                    this.offsetX = 10;
                }

                if (this.elementRectangle.height + this.elementRectangle.y == this.mobileContext.device.getWindowSize().height
                        && this.offsetY == 0) {
                    this.offsetY = 15;
                }

                if (direction == SwipeElementDirection.RIGHT) {
                    this.offsetX *= -1;
                }
            }
        }

        this.mobileContext.gestures.scrollInRectangle(direction, this.elementRectangle, startPosition, this.offsetX, this.offsetY, waitAfter);
    }

    public UIElement scrollInElementToElement(SwipeElementDirection direction, Position startPosition, By locator, int waitAfter, int retryCount, int offsetX, int offsetY) {
        LOGGER_BASE.debug("Swipe " + direction.toString());
        UIElement element = null;
        for (int i = 0; i < retryCount; i++) {
            element = this.mobileContext.wait.waitForVisible(locator, 2, false);
            if (element != null) {
                LOGGER_BASE.info("element found by locator \"" + locator.toString() + "\".");
                return element;
            } else {
                LOGGER_BASE.info("Swipe " + direction.toString());
                this.mobileContext.gestures.scrollInRectangle(direction, this.getUIRectangle(), startPosition, offsetX, offsetY, waitAfter);
            }
        }

        if (element == null) {
            LOGGER_BASE.info("element not found after " + retryCount + " swipes by locator \"" + locator.toString() + "\".");
        }

        return element;

    }

    /**
     * Swipes to element as long as the element by given locator is found or retry counts is equal zero.
     *
     * @param direction
     * @param locator
     * @param waitAfter
     * @param retryCount
     * @return
     */
    public UIElement scrollInElementToElement(SwipeElementDirection direction, By locator, int waitAfter, int retryCount) {
        return this.scrollInElementToElement(direction, Position.FromQuarter, locator, waitAfter, retryCount);
    }

    /**
     * Swipes to element as long as the element by given locator is found or retry counts is equal zero.
     *
     * @param direction
     * @param position
     * @param locator
     * @param waitAfter
     * @param retryCount
     * @return
     */
    public UIElement scrollInElementToElement(SwipeElementDirection direction, Position position, By locator, int waitAfter, int retryCount) {
        LOGGER_BASE.debug("Swipe " + direction.toString());
        UIElement element = null;
        for (int i = 0; i < retryCount; i++) {
            element = this.mobileContext.wait.waitForVisible(locator, 2, false);
            if (element != null) {
                LOGGER_BASE.info("element found by locator \"" + locator.toString() + "\".");
                return element;
            } else {
                LOGGER_BASE.info("Swipe " + direction.toString());
                this.scrollInElement(direction, position, waitAfter);
            }
        }

        if (element == null) {
            LOGGER_BASE.info("element not found after " + retryCount + " swipes by locator \"" + locator.toString() + "\".");
        }

        return element;
    }

    /**
     * Swipes in the bounds of element. Helpful to be used in lists.
     *
     * @param direction
     * @param duration
     * @param waitAfter
     */
    public void swipeInElement(SwipeElementDirection direction, int duration, int waitAfter) {
        if (this.elementRectangle == null) {
            this.elementRectangle = this.getUIRectangle();
        }

        if (this.mobileContext.settings.platform == PlatformType.Android) {
            if (this.elementRectangle.width + this.elementRectangle.x == this.mobileContext.device.getWindowSize().width) {
                this.elementRectangle.width -= 5;
            }

            if (this.elementRectangle.height + this.elementRectangle.y == this.mobileContext.device.getWindowSize().height) {
                this.elementRectangle.height -= 5;
            }
        }

        this.mobileContext.gestures.swipeInRectangle(direction, this.getUIRectangle(), duration, waitAfter);
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
