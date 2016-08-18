package functional.tests.core.Element;

import functional.tests.core.Appium.Client;
import functional.tests.core.Log.Log;

import java.awt.*;

public class UIRectangle {
    protected Rectangle rectangle;
    protected Client client;

    public UIRectangle(Rectangle rectangle, Client client) {
        this.rectangle = rectangle;
        this.client = client;
    }

    public Rectangle extendRectangle(int xOffset, int yOffset, int widthOffset, int heightOffset) {
        this.rectangle = extendRectangle(this.rectangle, xOffset, yOffset, widthOffset, heightOffset);

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

    public static Rectangle extendRectangle(Rectangle rect, int xOffset, int yOffset, int widthOffset, int heightOffset) {
        Rectangle rectangle = new Rectangle(rect.x + xOffset, rect.y + yOffset, rect.width + widthOffset, rect.height + heightOffset);

        return rectangle;
    }
}
