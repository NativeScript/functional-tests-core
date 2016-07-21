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

    public UIElement findElementByLocator(By locator) {
        return new UIElement((MobileElement) this.client.getDriver().findElement(locator));
    }

    public List<UIElement> findElementsByLocator(By locator) {
        return convertListOfMobileElementToUIElement((List<MobileElement>) this.client.driver.findElements(locator));
    }

    public UIElement findByText(String value) {
        return findElementByLocator(Locators.findByTextLocator("*", value, true));
    }

    public UIElement findByText(String value, int timeOut) {
        Client.setWait(timeOut);
        UIElement result;
        try {
            result = findByText(value);
        } catch (Exception e) {
            Log.error("Failed to find element by: " + value + " in " + String.valueOf(timeOut) + " seconds.");
            result = null;
        }
        Client.setWait(Settings.defaultTimeout);
        return result;
    }

    public UIElement byType(String value) {
        return new UIElement((MobileElement) this.client.driver.findElement(By.className(value)));
    }

    public UIElement byText(String str) {
        if (Settings.platform == PlatformType.Andorid) {
            return findByText(str);
        } else if (Settings.platform == PlatformType.iOS) {
            return findElementByLocator(By.id(str));
        } else {
            return null;
        }
    }

    public UIElement byTextNaviagtion(String value, int timeOut) {
        Client.setWait(timeOut);
        UIElement result;
        try {
            result = findByText(value);
        } catch (Exception e) {
            Log.error("Failed to find element by: " + value + " in " + String.valueOf(timeOut) + " seconds.");
            result = null;
        }
        Client.setWait(Settings.defaultTimeout);
        return result;
    }

    private static List<UIElement> convertListOfMobileElementToUIElement(List<MobileElement> list) {

        ArrayList<UIElement> elements = new ArrayList<>();

        for (MobileElement element : list) {
            elements.add(new UIElement(element));
        }

        return elements;
    }
}
