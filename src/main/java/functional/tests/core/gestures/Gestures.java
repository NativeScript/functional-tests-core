package functional.tests.core.gestures;

import functional.tests.core.appium.Client;
import functional.tests.core.basetest.Context;
import functional.tests.core.basetest.TestContextSetupManager;
import functional.tests.core.element.UIElement;
import functional.tests.core.enums.PlatformType;
import functional.tests.core.enums.Position;
import functional.tests.core.find.Find;
import functional.tests.core.find.Locators;
import functional.tests.core.find.Wait;
import functional.tests.core.log.LoggerBase;
import functional.tests.core.settings.Settings;
import io.appium.java_client.SwipeElementDirection;
import io.appium.java_client.TouchAction;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.testng.Assert;

import java.awt.*;

/**
 * TODO(): Add docs.
 */
public class Gestures {

    private static final int LEFT_RIGHT_MARGIN = 15;
    private static final LoggerBase LOGGER_BASE = LoggerBase.getLogger("Gestures");

    private Client client;
    private Find find;
    private Locators locators;
    private Settings settings;
    private Context context;

    /**
     * TODO(): Add docs.
     * Explain when to use Gestures().
     */
    public Gestures() {
        this(TestContextSetupManager.getTestSetupManager().context);
    }

    /**
     * TODO(): Add docs.
     * Explain when to use Gestures(Context context).
     *
     * @param context
     */
    public Gestures(Context context) {
        this.context = context;
        this.client = context.client;
        this.find = context.find;
        this.settings = context.settings;
        this.locators = context.locators;
    }

    /**
     * Swipes in the bound of window by given swipe direction, start position, duration of swipe.
     * Wait time after swipe.
     *
     * @param direction
     * @param startPosition
     * @param waitAfterSwipe
     */
    public void scrollInWindow(SwipeElementDirection direction, Position startPosition, int waitAfterSwipe) {
        int offsetX = 0;
        int offsetY = 0;
        Rectangle windowRectangle = this.getWindowRectangle();

        if (startPosition == Position.FromCorner) {
            //windowRectangle.height = (int) (windowRectangle.height - windowRectangle.height * 0.10D);
            offsetX = (int) (windowRectangle.height * 0.15D);
            offsetY = (int) (windowRectangle.height * 0.15D);
        }

        this.scrollInRectangle(direction, windowRectangle, startPosition, offsetX, offsetY, waitAfterSwipe);
    }

    /**
     * Swipes in the bound of rectangle by given swipe direction, rectangle,
     * start position and offset from start point, duration of swipe and wait time after swipe.
     * Notes: Start point prevent us to make swipe from very begging of window.
     *
     * @param direction
     * @param windowRectangle
     * @param startPosition
     * @param offsetX
     * @param offsetY
     * @param waitAfterSwipe
     */
    public void scrollInRectangle(SwipeElementDirection direction, Rectangle windowRectangle,
                                  Position startPosition, int offsetX, int offsetY, int waitAfterSwipe) {
        SwipePoint swipePoint = null;

        switch (startPosition) {
            case FromCorner:
                swipePoint = this.calculatePointForScroll(direction, windowRectangle, offsetX, offsetY);
                break;
            case FromCenter:
                swipePoint = this.calculatePointForScroll(direction, windowRectangle,
                        windowRectangle.x + (windowRectangle.width / 2),
                        windowRectangle.y + (windowRectangle.height / 2));
                break;
            case FromQuarter:
                swipePoint = this.calculatePointForScroll(direction, windowRectangle,
                        windowRectangle.x + (windowRectangle.width / 4),
                        windowRectangle.y + (windowRectangle.height / 4));
                break;
            default:
                LOGGER_BASE.error("swipeInRectangle not implemented for " + startPosition);
        }

        Gestures.scroll(waitAfterSwipe,
                swipePoint.initialX, swipePoint.initialY,
                swipePoint.finalX, swipePoint.finalY,
                this.context);
    }

    /**
     * Swipes to element as long as the element by given text is found or retry counts is equal zero.
     *
     * @param direction
     * @param elementText
     * @param retryCount
     * @return
     */
    public UIElement scrollToElement(SwipeElementDirection direction, String elementText, int retryCount) {
        By locator = this.locators.byText(elementText);
        if (this.settings.platform == PlatformType.iOS && this.settings.platformVersion < 10) {
            locator = By.id(elementText);
        }

        return this.scrollToElement(direction, locator, retryCount);
    }

    /**
     * Swipes to element as long as the element by given locator is found or retry counts is equal zero.
     *
     * @param direction
     * @param locator
     * @param retryCount
     * @return
     */
    public UIElement scrollToElement(SwipeElementDirection direction, By locator, int retryCount) {
        LOGGER_BASE.debug("Swipe " + direction.toString());

        for (int i = 0; i < retryCount; i++) {
            UIElement element = this.context.wait.waitForVisible(locator, 2, false);
            if (element != null) {
                LOGGER_BASE.info("element found by locator \"" + locator.toString() + "\".");
                return element;
            } else {
                LOGGER_BASE.info("Swipe " + direction.toString());
                this.scrollInWindow(direction, Position.FromCorner, Settings.DEFAULT_TAP_DURATION);
            }
            if (i == retryCount - 1) {
                LOGGER_BASE.info("element not found after " + String.valueOf(i + 1) + " swipes by locator \"" + locator.toString() + "\".");
            }
        }
        return null;
    }

    /**
     * Swipes to element as long as the element by given text is found or retry counts is equal zero.
     *
     * @param direction
     * @param duration
     */
    public void swipeInWindow(SwipeElementDirection direction, int duration) {
        this.swipeInWindow(direction, duration, 0);
    }

    /**
     * Swipes to element as long as the element by given locator is found or retry counts is equal zero.
     *
     * @param direction
     * @param duration
     */
    public void swipeInWindow(SwipeElementDirection direction, int duration, int waitAfter) {
        LOGGER_BASE.debug("Swipe " + direction.toString());
        Rectangle windowRectangle = this.getWindowRectangle();

        windowRectangle.height = (int) (windowRectangle.height - windowRectangle.height * 0.15D);
        //TODO(dtopuzov): According direction calculate x and y

        this.swipeInRectangle(direction, windowRectangle, duration, waitAfter);
    }

    /**
     * Swipes to element as long as the element by given locator is found or retry counts is equal zero.
     *
     * @param direction
     * @param duration
     */
    public void swipeInRectangle(SwipeElementDirection direction, Rectangle rectangle, int duration, int waitAfter) {
        LOGGER_BASE.debug("Swipe " + direction.toString());

        int initialX = 0;
        int initialY = 0;
        int finalX = 0;
        int finalY = 0;

        if (direction == SwipeElementDirection.DOWN) {
            initialY = (int) (rectangle.height - rectangle.height * 0.15D) + rectangle.y;
            finalY = rectangle.y;
            initialX = (rectangle.x + rectangle.width) / 2;
            finalX = initialX;
        }
        if (direction == SwipeElementDirection.UP) {
            initialY = rectangle.y + (int) (rectangle.height - rectangle.height * 0.25D);
            finalY = rectangle.height;
            initialX = (rectangle.x + rectangle.width) / 2;
            finalX = initialX;
        }
        if (direction == SwipeElementDirection.RIGHT) {
            initialY = rectangle.y + rectangle.height / 2;
            finalY = initialY;
            initialX = (rectangle.x + rectangle.width) - (int) (rectangle.x + rectangle.width * 0.15D);
            finalX = rectangle.x;
        }
        if (direction == SwipeElementDirection.LEFT) {
            initialY = rectangle.y + rectangle.height / 2;
            finalY = initialY;
            initialX = rectangle.x + (int) (rectangle.x + rectangle.width * 0.15D);
            finalX = rectangle.width;
        }

        Gestures.swipe(duration, Settings.DEFAULT_TAP_DURATION * 2, initialX, initialY, finalX, finalY, this.context);
        if (waitAfter > 0) {
            Wait.sleep(waitAfter);
        }
    }

    /**
     * Swipes to element as long as the element by given locator is found or retry counts is equal zero.
     *
     * @param direction
     * @param duration
     */
    public UIElement swipeInWindowToElement(SwipeElementDirection direction, By element, int retriesCount, int duration, int waitAfter) {
        UIElement el = this.context.wait.waitForVisible(element, this.settings.shortTimeout, false);
        while (el == null && retriesCount >= 0) {
            this.swipeInWindow(direction, duration, waitAfter);
            el = this.context.wait.waitForVisible(element, this.settings.shortTimeout, false);
            retriesCount--;
        }

        return el;
    }

    /**
     * Swipes by given swipe direction.
     * Rectangle which limits the final swipe to the bound of rectangle and initial x, y and final x, y.
     *
     * @param waitAfterSwipe
     * @param initialX
     * @param initialY
     * @param finalX
     * @param finalY
     * @param context
     */
    private static void scroll(int waitAfterSwipe, int initialX, int initialY, int finalX, int finalY, Context context) {
        try {
            if (context.settings.platform == PlatformType.Andorid) {
                new TouchAction(context.client.driver).press(initialX, initialY).moveTo(finalX, finalY).perform();
            }

            if (context.settings.platform == PlatformType.iOS) {
                new TouchAction(context.client.driver).press(initialX, initialY).moveTo(finalX, finalY).release().perform();
            }

            if (waitAfterSwipe > 0) {
                Wait.sleep(waitAfterSwipe);
            }
        } catch (Exception e) {
            if ((context.settings.platform == PlatformType.Andorid) && (context.settings.platformVersion == 4.2)) {
                LOGGER_BASE.debug("Known issue: Swipe works on Api17, but error is thrown.");
            } else {
                Assert.fail("Failed to scroll");
                context.log.error(e.getMessage());
            }
        }
    }

    private Rectangle getWindowRectangle() {
        Dimension windowDimensions = this.context.device.getWindowSize();
        Rectangle windowRect = new Rectangle(0, 0, windowDimensions.width, windowDimensions.height);

        return windowRect;
    }

    /**
     * Helps to calculate SwipePoint by given rectangle and offset from the final point.
     * The directions are according to iOS.
     *
     * @param direction
     * @param window
     * @param offsetX
     * @param offsetY
     * @return
     */
    private static SwipePoint calculatePointForScroll(SwipeElementDirection direction, Rectangle window, int offsetX, int offsetY) {
        int initialX = 0;
        int initialY = 0;
        int finalX = 0;
        int finalY = 0;

        if (direction == SwipeElementDirection.DOWN) {
            initialY = window.height + window.y - offsetY;
            finalY = -initialY + 1;
            initialX = window.x + offsetX;
            finalX = initialX;
        }
        if (direction == SwipeElementDirection.UP) {
            initialY = window.y + offsetY;
            finalY = window.height - 1;
            initialX = window.x + window.width + offsetX;
            finalX = initialX;
        }
        if (direction == SwipeElementDirection.RIGHT) {
            initialY = window.y + offsetY;
            finalY = initialY;
            initialX = window.width;
            finalX = window.x;
        }
        if (direction == SwipeElementDirection.LEFT) {
            initialY = window.y + offsetY;
            finalY = initialY;
            initialX = window.x + offsetX;
            finalX = window.width - initialX;
        }

        LOGGER_BASE.debug("Initial Point: " + initialX + ":" + initialY);
        LOGGER_BASE.debug("Final Point: " + finalX + ":" + finalY);
        return new SwipePoint(initialX, initialY, finalX, finalY);
    }

    /**
     * Swipes by given swipe direction.
     * Rectangle which limits the final swipe to the bound of rectangle and initial x, y and final x, y.
     *
     * @param duration
     * @param waitAfterSwipe
     * @param initialX
     * @param initialY
     * @param finalX
     * @param finalY
     * @param context
     */
    private static void swipe(int duration, int waitAfterSwipe, int initialX, int initialY, int finalX, int finalY, Context context) {

        // Swipe with duration < 500 is not possible for iOS8 and iOS9.
        if (context.settings.platform == PlatformType.iOS && context.settings.platformVersion < 10.0 && duration < 500) {
            LOGGER_BASE.warn("Swipe with duration < 500 not possible. Will use duration = 500.");
            duration = 500;
        }

        try {
            context.client.driver.swipe(initialX, initialY, finalX, finalY, duration);

            if (waitAfterSwipe > 0) {
                Wait.sleep(waitAfterSwipe);
            }
        } catch (Exception e) {
            if ((context.settings.platform == PlatformType.Andorid) && (context.settings.platformVersion == 4.2)) {
                LOGGER_BASE.debug("Known issue: Swipe works on Api17, but error is thrown.");
            } else {
                String error = "Failed to swipe " + " with " + duration + " duration.";
                Assert.fail(error);
            }
        }
    }
}

