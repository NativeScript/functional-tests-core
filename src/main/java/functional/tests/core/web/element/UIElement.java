package functional.tests.core.web.element;

import functional.tests.core.log.LoggerBase;
import org.apache.commons.lang.reflect.FieldUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This object wrappes the Appium MobileElement.
 */
public class UIElement {

    private static final LoggerBase LOGGER_BASE = LoggerBase.getLogger("UIElement");

    private WebElement element;
    private Rectangle elementRectangle;
    private int offsetX;
    private int offsetY;

    public UIElement(WebElement element) {
        this.element = element;
        this.offsetX = 0;
        this.offsetY = 0;
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

    public String getTagName() {
        String tagName = null;
        try {
            tagName = this.element.getTagName();
        } catch (Exception e) {
        }
        return tagName;
    }

    public String getCoordinates() {
        return String.valueOf(this.element.getLocation().x) + ":" + String.valueOf(this.element.getLocation().y);
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

//    public boolean isVisible() {
//        if (this.isDisplayed()) {
//            Dimension windowRect = this.getUIRectangle();
//            Rectangle elementRect = this.getUIRectangle();
//            if (elementRect.getHeight() / 2 + elementRect.getY() > windowRect.getHeight()) {
//                return false;
//            } else if (elementRect.x < 0 || elementRect.y < 0 || elementRect.width <= 0 || elementRect.height <= 0) {
//                return false;
//            } else {
//                return true;
//            }
//        } else {
//            return false;
//        }
//    }

    public UIElement findElement(By by) {
        return new UIElement(this.element.findElement(by));
    }

    public UIElement findElementById(String id) {
        return this.findElement(By.id(id));
    }

    public ArrayList<UIElement> findElements(By by) {
        List<WebElement> elements = this.element.findElements(by);
        ArrayList<UIElement> uiElements = new ArrayList<>();

        for (WebElement elment : elements) {
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

    public java.awt.Rectangle getUIRectangle() {
        java.awt.Rectangle rect = new java.awt.Rectangle(
                this.element.getLocation().getX(),
                this.element.getLocation().getY(),
                this.element.getSize().getWidth(),
                this.element.getSize().getHeight());

        return rect;
    }
}
