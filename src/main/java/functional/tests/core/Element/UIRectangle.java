package functional.tests.core.Element;

import functional.tests.core.Appium.Client;
import functional.tests.core.Log.Log;
import io.appium.java_client.AppiumDriver;

import java.awt.*;

public class UIRectangle {
    private Rectangle rectangle;
    private Client client;

    public UIRectangle(Rectangle rectangle, Client client) {
        this.rectangle = rectangle;
        this.client = client;
    }

    public Rectangle extendRectangle(int xOffset, int yOffset, int widthOffset, int heightOffset) {
        Rectangle rect = new Rectangle(this.rectangle.x + xOffset, this.rectangle.y + yOffset, this.rectangle.width + widthOffset, this.rectangle.height + heightOffset);
        this.rectangle = rect;

        return this.rectangle;
    }

    public Rectangle getRectangle() {
        return this.rectangle;
    }

    public void longPress() {
        Log.info("UIRectangle longPress on x: " + this.rectangle.x + " y: " + this.rectangle.y);
        io.appium.java_client.TouchAction action = new io.appium.java_client.TouchAction(this.client.getDriver());
        action.longPress(this.rectangle.x, this.rectangle.y, 2000).perform();
    }

    public void tap() {
        Log.info("UIRectangle tap on x: " + this.rectangle.x + " y: " + this.rectangle.y);
        io.appium.java_client.TouchAction action = new io.appium.java_client.TouchAction(this.client.getDriver());
        action.tap(this.rectangle.x, this.rectangle.y).perform();
    }
}
