package functional.tests.core.mobile.element;

import functional.tests.core.log.LoggerBase;
import functional.tests.core.mobile.appium.Client;
import functional.tests.core.mobile.basetest.MobileContext;
import functional.tests.core.mobile.basetest.MobileSetupManager;
import io.appium.java_client.TouchAction;
import io.appium.java_client.touch.LongPressOptions;
import io.appium.java_client.touch.offset.PointOption;

import java.awt.*;
import java.time.Duration;

/**
 * TODO(): Add docs.
 */
public class UIRectangle {

    private static final LoggerBase LOGGER_BASE = LoggerBase.getLogger("UIRectangle");
    protected Rectangle rectangle;
    protected Client client;

    public UIRectangle(Rectangle rectangle) {
        this(rectangle, MobileSetupManager.getTestSetupManager().getContext());
    }

    public UIRectangle(Rectangle rectangle, MobileContext mobileContext) {
        this.rectangle = rectangle;
        this.client = mobileContext.client;
    }

    public Rectangle getRectangle() {
        return this.rectangle;
    }

    public void longPress() {
        LOGGER_BASE.info("UIRectangle longPress on x: " + this.rectangle.x + " y: " + this.rectangle.y);
        TouchAction action = new io.appium.java_client.TouchAction(this.client.getDriver());
        LongPressOptions options = LongPressOptions.longPressOptions()
                .withDuration(Duration.ofMillis(2000))
                .withPosition(PointOption.point(this.rectangle.x, this.rectangle.y));
        action.longPress(options).release().perform();
    }

    public void tap() {
        LOGGER_BASE.info("UIRectangle tap on x: " + this.rectangle.x + " y: " + this.rectangle.y);
        io.appium.java_client.TouchAction action = new io.appium.java_client.TouchAction(this.client.getDriver());
        action.tap(PointOption.point(this.rectangle.x, this.rectangle.y)).perform();
    }

    public Rectangle extendRectangle(int xOffset, int yOffset, int widthOffset, int heightOffset) {
        this.rectangle = this.extendRectangle(this.rectangle, xOffset, yOffset, widthOffset, heightOffset);

        return this.rectangle;
    }

    public static Rectangle extendRectangle(Rectangle rect, int xOffset, int yOffset, int widthOffset, int heightOffset) {
        Rectangle rectangle = new Rectangle(rect.x + xOffset, rect.y + yOffset, rect.width + widthOffset, rect.height + heightOffset);

        return rectangle;
    }
}
