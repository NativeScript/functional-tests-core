package functional.tests.core.ImageProcessing.SikuliImageProcessing;

import functional.tests.core.Appium.Client;

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
        io.appium.java_client.TouchAction action = new io.appium.java_client.TouchAction(this.client.getDriver());
        action.longPress((int) this.rectangle.getX(), (int) this.rectangle.getY()).perform();
    }
}
