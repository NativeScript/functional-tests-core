package functional.tests.core.mobile.gestures;

import functional.tests.core.enums.PlatformType;
import functional.tests.core.enums.Position;
import functional.tests.core.enums.SwipeElementDirection;
import functional.tests.core.log.LoggerBase;
import functional.tests.core.mobile.appium.Client;
import functional.tests.core.mobile.device.Device;
import functional.tests.core.mobile.element.UIElement;
import functional.tests.core.mobile.find.Locators;
import functional.tests.core.mobile.find.Wait;
import functional.tests.core.mobile.settings.MobileSettings;
import functional.tests.core.settings.Settings;
import io.appium.java_client.TouchAction;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.testng.Assert;

import java.awt.*;
import java.time.Duration;

/**
 * Gestures.
 */
public class Gestures {

    private static final int LEFT_RIGHT_MARGIN = 15;
    private static final LoggerBase LOGGER_BASE = LoggerBase.getLogger("Gestures");

    private Client client;
    private Wait wait;
    private Device device;
    private Locators locators;
    private MobileSettings settings;

    /**
     * TODO(): Add docs.
     * Explain when to use Gestures(MobileContext context).
     *
     * @param client
     * @param wait
     * @param device
     * @param locators
     * @param settings
     */
    public Gestures(Client client, Wait wait, Device device, Locators locators, MobileSettings settings) {
        this.client = client;
        this.wait = wait;
        this.device = device;
        this.settings = settings;
        this.locators = locators;
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
            offsetX = (int) (windowRectangle.height * 0.25D);

            if (direction == SwipeElementDirection.RIGHT) {
                offsetX *= -1;
            }
            offsetY = (int) (windowRectangle.height * 0.25D);
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
                this.settings, this.client);
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
            UIElement element = this.wait.waitForVisible(locator, 2, false);
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
            finalX = rectangle.width - 1;
        }

        Gestures.swipe(duration, waitAfter, initialX, initialY, finalX, finalY, this.client, this.settings);
    }

    /**
     * Swipes to element as long as the element by given locator is found or retry counts is equal zero.
     *
     * @param direction
     * @param duration
     */
    public UIElement swipeInWindowToElement(SwipeElementDirection direction, By element, int retriesCount, int duration, int waitAfter) {
        UIElement el = this.wait.waitForVisible(element, this.settings.shortTimeout, false);
        while (el == null && retriesCount >= 0) {
            this.swipeInWindow(direction, duration, waitAfter);
            el = this.wait.waitForVisible(element, this.settings.shortTimeout, false);
            retriesCount--;
        }

        return el;
    }

    /**
     * Scrolls to absolute coordinates for Android and calculate relative coordinates for iOS.
     *
     * @param startX
     * @param startY
     * @param endX
     * @param endY
     */
    public void scrollTo(int startX, int startY, int endX, int endY) {
        // Calculating the speed to avoid inertia. It is like pixel per milliseconds except for iOS9
        Duration duration = Duration.ofMillis((startY - endY) * 10);
        if (this.settings.platform == PlatformType.iOS) {
            endY = endY - startY;
        }

        try {
            new TouchAction(this.client.driver)
                    .press(PointOption.point(startX, startY))
                    .waitAction(duration)
                    .moveTo(PointOption.point(endX, endY))
                    .release()
                    .perform();
        } catch (Exception ex) {
            // This method throws exception for api17 for Android even though it is working.
        }
    }

    /**
     * Swipes by given swipe direction and relative point from initial x and initial y.
     * Rectangle which limits the final swipe to the bound of rectangle and initial x, y and final x, y.
     *
     * @param waitAfterSwipe
     * @param initialX
     * @param initialY
     * @param finalX
     * @param finalY
     * @param settings
     * @param client
     */
    public static void scroll(int waitAfterSwipe, int initialX, int initialY, int finalX, int finalY, MobileSettings settings, Client client) {
        try {
            new TouchAction(client.driver)
                    .press(PointOption.point(initialX, initialY))
                    .waitAction(WaitOptions.waitOptions(Duration.ofMillis(1000)))
                    .moveTo(PointOption.point(finalX, finalY))
                    .perform();

            if (waitAfterSwipe > 0) {
                Wait.sleep(waitAfterSwipe);
            }
        } catch (Exception e) {
            if ((settings.platform == PlatformType.Android) && (settings.platformVersion == 4.2)) {
                LOGGER_BASE.debug("Known issue: Swipe works on Api17, but error is thrown.");
            } else {
                Assert.fail("Failed to scroll");
                Gestures.LOGGER_BASE.error(e.getMessage());
            }
        }
    }

    private Rectangle getWindowRectangle() {
        Dimension windowDimensions = this.device.getWindowSize();
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
            initialX = window.x + offsetX;
            initialY = window.height + window.y - offsetY;
            finalX = window.x + offsetX;
            finalY = window.y + offsetY;
        }
        if (direction == SwipeElementDirection.UP) {
            initialX = window.x + offsetX;
            initialY = window.y + offsetY;
            finalX = window.x + offsetX;
            finalY = window.height + window.y - offsetY;
        }
        if (direction == SwipeElementDirection.RIGHT) {
            initialY = window.y + offsetY;
            finalY = initialY;
            initialX = window.width - 1 + offsetX;
            finalX = -initialX;
        }
        if (direction == SwipeElementDirection.LEFT) {
            initialY = window.y + offsetY;
            finalY = initialY;
            initialX = window.x + 1 + offsetX;

            finalX = window.width - initialX - 1;
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
     * @param client
     * @param settings
     */
    private static void swipe(int duration, int waitAfterSwipe, int initialX, int initialY, int finalX, int finalY, Client client, MobileSettings settings) {

        // Swipe with duration < 500 is not possible for iOS8 and iOS9.
        if (settings.platform == PlatformType.iOS && settings.platformVersion < 10.0 && duration < 500) {
            LOGGER_BASE.warn("Swipe with duration < 500 not possible. Will use duration = 500.");
            duration = 500;
        }

        try {
            TouchAction swipe = new TouchAction(client.driver)
                    .press(PointOption.point(initialX, initialY))
                    .waitAction(WaitOptions.waitOptions(Duration.ofMillis(duration)))
                    .moveTo(PointOption.point(finalX, finalY))
                    .release();
            swipe.perform();

            if (waitAfterSwipe > 0) {
                Wait.sleep(waitAfterSwipe);
            }
        } catch (Exception e) {
            if (settings.platform == PlatformType.Android && settings.platformVersion == 4.2) {
                LOGGER_BASE.debug("Known issue: Swipe works on Api17, but error is thrown.");
            } else {
                String error = "Failed to swipe " + " with " + duration + " duration.";
                Assert.fail(error);
            }
        }
    }
}

