package functional.tests.core.element;

import functional.tests.core.appium.Client;
import functional.tests.core.basetest.Context;
import functional.tests.core.basetest.TestContextSetupManager;
import functional.tests.core.enums.PlatformType;
import functional.tests.core.enums.Position;
import functional.tests.core.find.Wait;
import functional.tests.core.log.LoggerBase;
import functional.tests.core.settings.Settings;
import io.appium.java_client.MobileElement;
import io.appium.java_client.MultiTouchAction;
import io.appium.java_client.SwipeElementDirection;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import org.apache.commons.lang.reflect.FieldUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Point;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.RemoteWebElement;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This object wrappes the Appium MobileElement.
 */
public class UIElement {

    private static final LoggerBase LOGGER_BASE = LoggerBase.getLogger("UIElement");

    private MobileElement element;
    private Client client;
    private Context context;
    private Settings settings;
    private Rectangle elementRectangle;
    private int offsetX;
    private int offsetY;

    public UIElement(MobileElement element) {
        this.element = element;
        this.context = TestContextSetupManager.getTestSetupManager().context;
        this.settings = this.context.settings;
        this.client = this.context.client;
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
        if (this.settings.platform == PlatformType.Andorid) {
            // Tap at the and of the text box (this is very important)
            int currentLegth = this.element.getText().length();
            int x = this.element.getLocation().getX() + this.element.getSize().width - 5;
            int y = this.element.getLocation().getY() + (this.element.getSize().height / 3);
            this.client.driver.tap(1, x, y, Settings.DEFAULT_TAP_DURATION);
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
            Dimension windowRect = this.context.device.getWindowSize();
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

    public void sendKeys(String value) {
        this.element.sendKeys(value);
    }

    public void click() {
        String text = this.getDescription();
        this.click(text);
    }

    public void click(String description) {
        this.element.click();
        LOGGER_BASE.info("Click on " + description);
    }

    public void tap(int fingers, int duration, int waitAfterTap) {
        String text = this.getDescription();

        this.element.tap(fingers, duration);
        if (waitAfterTap > 0) {
            Wait.sleep(waitAfterTap);
        }

        LOGGER_BASE.info("Tap on \"" + text + "\"");
    }

    public void tap() {
        this.tap(1, Settings.DEFAULT_TAP_DURATION, Settings.DEFAULT_TAP_DURATION);
    }

    public void tap(int fingers, int duration) {
        this.tap(fingers, duration, Settings.DEFAULT_TAP_DURATION);
    }

    public void tap(int fingers) {
        this.tap(fingers, Settings.DEFAULT_TAP_DURATION, Settings.DEFAULT_TAP_DURATION);
    }

    public void doubleTap() {
        LOGGER_BASE.info("Double Tap: "); // + Elements.getElementDetails(element));
        if (this.settings.platform == PlatformType.Andorid) {

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

    public void longPress(int duration) {
        LOGGER_BASE.info("LongPress: "); // + Elements.getElementDetails(element));
        TouchAction action = new TouchAction(this.client.driver);
        if (this.client.settings.platform == PlatformType.iOS && this.client.settings.platformVersion >= 10) {
            action.longPress(this.element).perform();
        } else {
            action.press(this.element).waitAction(duration).release().perform();
        }
    }

    public void pressAndHold() {
        TouchAction action = new TouchAction(this.client.getDriver());
        Rectangle rect = this.getUIRectangle();
        this.client.getDriver().performTouchAction(action.press(rect.x, rect.y));
    }

    public void pinch() {

        LOGGER_BASE.info("Pinch: "); // + Elements.getElementDetails(element));

        if (this.settings.platform == PlatformType.Andorid) {
            TouchAction action1 = new TouchAction(this.client.driver);
            TouchAction action2 = new TouchAction(this.client.driver);

            int elementWidth = this.element.getSize().width;
            int elementHeight = this.element.getSize().height;

            action1.press(this.element, 10, 10).moveTo(this.element, 50, 50);

            action2.press(this.element, elementWidth - 10, elementHeight - 10)
                    .moveTo(this.element, elementWidth - 50, elementHeight - 50);

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

            action1.press(this.element, 10, 10).moveTo(this.element, 50, 50).release();

            action2.press(this.element, elementWidth - 10, elementHeight - 10)
                    .moveTo(this.element, elementWidth - 50, elementHeight - 50)
                    .release();

            MultiTouchAction multiAction = new MultiTouchAction(this.client.driver);
            multiAction.add(action1);
            multiAction.add(action2);
            multiAction.perform();
        }
    }

    public void rotate() {
        LOGGER_BASE.info("Rotate: "); // + Elements.getElementDetails(element));

        if (this.settings.platform == PlatformType.Andorid) {
            TouchAction action1 = new TouchAction(this.client.driver);
            TouchAction action2 = new TouchAction(this.client.driver);

            int elementWidth = this.element.getSize().width;
            int elementHeight = this.element.getSize().height;

            action1.press(this.element, 10, 10).moveTo(this.element, 10, 50);

            action2.press(this.element, elementWidth - 10, elementHeight - 10)
                    .moveTo(this.element, elementWidth - 10, elementHeight - 50);

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

            action1.press(this.element, 10, 10).moveTo(this.element, 10, 50).release();

            action2.press(this.element, elementWidth - 10, elementHeight - 10)
                    .moveTo(this.element, elementWidth - 10, elementHeight - 50)
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
        this.client.driver.swipe(point.getX(), point.getY(), point.getX() + xOffset, point.getY() + yOffset, duration);
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
                if (this.elementRectangle.getX() == 0) {
                    this.offsetX = 10;
                }
                if (this.elementRectangle.getY() == 0) {
                    this.offsetY = 10;
                }

                if (this.elementRectangle.width + this.elementRectangle.x == this.context.device.getWindowSize().width) {
                    this.offsetX = 10;
                }

                if (this.elementRectangle.height + this.elementRectangle.y == this.context.device.getWindowSize().height) {
                    this.offsetY = 15;
                }
            }
        }

        this.context.gestures.scrollInRectangle(direction, this.elementRectangle, startPosition, this.offsetX, this.offsetY, waitAfter);
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
            element = this.context.wait.waitForVisible(locator, 2, false);
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

        if (this.context.settings.platform == PlatformType.Andorid) {
            if (this.elementRectangle.width + this.elementRectangle.x == this.context.device.getWindowSize().width) {
                this.elementRectangle.width -= 5;
            }

            if (this.elementRectangle.height + this.elementRectangle.y == this.context.device.getWindowSize().height) {
                this.elementRectangle.height -= 5;
            }
        }

        this.context.gestures.swipeInRectangle(direction, this.elementRectangle, duration, waitAfter);
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
