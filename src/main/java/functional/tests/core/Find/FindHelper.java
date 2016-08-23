package functional.tests.core.Find;

import functional.tests.core.Appium.Client;
import functional.tests.core.Element.UIElement;
import functional.tests.core.Enums.PlatformType;
import functional.tests.core.Log.Log;
import functional.tests.core.Settings.Settings;
import io.appium.java_client.MobileElement;
import org.openqa.selenium.By;

import java.util.ArrayList;
import java.util.List;

public class FindHelper {

    private Client client;

    public FindHelper(Client client) {
        this.client = client;
    }

    public UIElement byLocator(By locator) {
        return new UIElement((MobileElement) this.client.getDriver().findElement(locator));
    }

    public UIElement byType(String value) {
        return new UIElement((MobileElement) this.client.driver.findElement(By.className(value)));
    }

    public List<UIElement> elementsByLocator(By locator) {
        return convertListOfMobileElementToUIElement((List<MobileElement>) this.client.driver.findElements(locator));
    }

    public UIElement byLocator(By locator, int timeOut) {
        this.client.setWait(timeOut);
        UIElement result;
        try {
            result = this.byLocator(locator);
        } catch (Exception e) {
            Log.error("Failed to find element by locator: " + locator + " in " + String.valueOf(timeOut) + " seconds.");
            result = null;
        }
        this.client.setWait(Settings.defaultTimeout);
        return result;
    }

    public UIElement byText(String value) {
        return this.byText(value, Settings.shortTimeout);
    }

    public UIElement byText(String value, int timeOut) {
        this.client.setWait(timeOut);
        UIElement result;
        try {
            result = this.byLocator(Locators.byText(value));
        } catch (Exception e) {
            Log.error("Failed to find element by text: " + value + " in " + String.valueOf(timeOut) + " seconds.");
            result = null;
        }
        this.client.setWait(Settings.defaultTimeout);
        return result;
    }

    public UIElement byTextContains(String value) {
        return this.byLocator(Locators.byText(value, false));
    }

    public UIElement byType(String value, int timeOut) {
        this.client.setWait(timeOut);
        UIElement result;
        try {
            result = this.byType(value);
        } catch (Exception e) {
            Log.error("Failed to find element by type: " + value + " in " + String.valueOf(timeOut) + " seconds.");
            result = null;
        }
        this.client.setWait(Settings.defaultTimeout);

        return result;
    }

    public List<UIElement> elementsByLocator(By locator, int timeOut) {
        this.client.setWait(timeOut);
        List<UIElement> result;
        try {
            result = this.elementsByLocator(locator);
        } catch (Exception e) {
            Log.error("Failed to find element by locator: " + locator + " in " + String.valueOf(timeOut) + " seconds.");
            result = null;
        }
        this.client.setWait(Settings.defaultTimeout);
        return result;
    }

    public UIElement getParent(UIElement element) {
        String xpathString = element.getXpath() + "/..";
        Log.debug("Looking for parent with the following Xpath: " + xpathString);
        UIElement e = new UIElement((MobileElement) Client.driver.findElement(By.xpath(xpathString)));
        Log.debug("Found " + e.getDescription());
        return e;
    }

    private List<UIElement> convertListOfMobileElementToUIElement(List<MobileElement> list) {
        ArrayList<UIElement> elements = new ArrayList<>();
        for (MobileElement element : list) {
            elements.add(new UIElement(element));
        }
        return elements;
    }
}
