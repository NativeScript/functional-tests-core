package functional.tests.core.web.find;

import functional.tests.core.web.element.UIElement;
import functional.tests.core.log.LoggerBase;
import io.appium.java_client.MobileElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.ArrayList;
import java.util.List;

/**
 * Find.
 */
public class Find {
    private static final LoggerBase LOGGER_BASE = LoggerBase.getLogger("Find");

    private ChromeDriver driver;

    public Find(ChromeDriver driver) {
        this.driver = driver;
    }

    public static void wait(int timeOut) {
        try {
            Thread.sleep(timeOut);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public UIElement byLocator(By locator) {
        return new UIElement((WebElement) this.driver.findElement(locator));
    }

    public UIElement byLocator(By locator, int timeOut) {
        wait(timeOut);
        UIElement result;
        try {
            result = this.byLocator(locator);
        } catch (Exception e) {
            LOGGER_BASE.debug("Failed to find element by locator: " + locator + " in " + String.valueOf(timeOut) + " seconds.");
            result = null;
        }

        return result;
    }

    public UIElement byType(String value) {
        return new UIElement((WebElement) this.driver.findElement(By.className(value)));
    }

    public List<UIElement> elementsByLocator(By locator) {
        return convertListOfMobileElementToUIElement((List<WebElement>) this.driver.findElements(locator));
    }

    public List<UIElement> elementsByLocator(By locator, int timeOut) {
        wait(timeOut);
        List<UIElement> result;
        try {
            result = this.elementsByLocator(locator);
        } catch (Exception e) {
            LOGGER_BASE.error("Failed to find elements by locator: " + locator + " in " + String.valueOf(timeOut) + " seconds.");
            result = null;
        }

        return result;
    }

    public List<UIElement> elementsbyType(String value) {
        return this.convertListOfMobileElementToUIElement((List<WebElement>) this.driver.findElements(By.className(value)));
    }

    public List<UIElement> elementsbyType(String value, int timeOut) {
        wait(timeOut);
        List<UIElement> result;
        try {
            result = this.elementsbyType(value);
        } catch (Exception e) {
            LOGGER_BASE.error("Failed to find elements by type: " + value + " in " + String.valueOf(timeOut) + " seconds.");
            result = null;
        }

        return result;
    }

    public UIElement getParent(UIElement element) {
        String xpathString = element.getXpath() + "/..";
        LOGGER_BASE.debug("Looking for parent with the following Xpath: " + xpathString);
        UIElement e = new UIElement((MobileElement) this.driver.findElement(By.xpath(xpathString)));
        LOGGER_BASE.debug("Found " + e.getDescription());
        return e;
    }

    private static List<UIElement> convertListOfMobileElementToUIElement(List<WebElement> list) {
        ArrayList<UIElement> elements = new ArrayList<>();
        for (WebElement element : list) {
            elements.add(new UIElement(element));
        }
        return elements;
    }
}
