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

    private List<UIElement> convertListOfMobileElementToUIElement(List<MobileElement> list) {
        ArrayList<UIElement> elements = new ArrayList<>();
        for (MobileElement element : list) {
            elements.add(new UIElement(element));
        }
        return elements;
    }

    private UIElement findElementByLocator(By locator) {
        return new UIElement((MobileElement) this.client.getDriver().findElement(locator));
    }

    private List<UIElement> findElementsByLocator(By locator) {
        return convertListOfMobileElementToUIElement((List<MobileElement>) this.client.driver.findElements(locator));
    }

    private UIElement findTextByXPath(String value) {
        return findElementByLocator(Locators.findByTextLocator("*", value, true));
    }

    public UIElement byText(String value) {
        if (Settings.platform == PlatformType.Andorid) {
            return this.findTextByXPath(value);
        } else if (Settings.platform == PlatformType.iOS) {
            return findElementByLocator(By.id(value));
        } else {
            return null;
        }
    }

    public UIElement byText(String value, int timeOut) {
        Client.setWait(timeOut);
        UIElement result;
        try {
            result = this.byText(value);
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
}
