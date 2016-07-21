package functional.tests.core.Find;

import functional.tests.core.Appium.Client;
import functional.tests.core.Element.UIElement;
import functional.tests.core.Enums.PlatformType;
import functional.tests.core.Log.Log;
import functional.tests.core.Settings.Settings;
import io.appium.java_client.MobileElement;
import org.openqa.selenium.By;
import org.openqa.selenium.interactions.Action;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class Find {
    private Client client;

    public Find(Client client){
      this.client = client;
    }

    /**
     * Find an element that has some attribute with specified value.
     */
    public static UIElement findByText(String controlType, String value, boolean exactMatch) {
        return findElementByLocator(Locators.findByTextLocator(controlType, value, exactMatch));
    }

    /**
     * Find an element that has some attribute with specified value.
     */
    public static UIElement findByText(String value, boolean exactMatch) {
        return findElementByLocator(Locators.findByTextLocator("*", value, exactMatch));
    }

    /**
     * Find an element that has some attribute with specified value.
     */
    public static UIElement findByText(String value) {
        return findElementByLocator(Locators.findByTextLocator("*", value, true));
    }

    /**
     * Find an element that has some attribute with specified value.
     */
    public static UIElement findByText(String value, int timeOut) {
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

    /**
     * Find an element that has some attribute with specified value
     * Search is based on specified UIElement
     */
    public static UIElement findByText(UIElement element, String value) {
        String parentXpath = element.getXpath();
        parentXpath = parentXpath.substring(2);

        By locator = Locators.findByTextLocator(value, true);
        String childXpath = locator.toString().replace("By.xpath: //", "").replace("//*", "");

        String finalXpath = "//" + parentXpath + "//" + childXpath;
        Log.debug("Looking for element with following xpath:" + finalXpath);
        UIElement e = new UIElement((MobileElement) Client.driver.findElement(By.xpath(finalXpath)));
        Log.debug("Found " + element.getDescription());
        return e;
    }

    /**
     * Find an element by class (UI control class)
     */
    public static UIElement findByType(String value) {
        return new UIElement((MobileElement) Client.driver.findElement(By.className(value)));
    }

    /**
     * Find an element by class (UI control class)
     */
    public static UIElement findByType(String value, int timeOut) {
        Client.setWait(timeOut);
        UIElement result;
        try {
            result = findByType(value);
        } catch (Exception e) {
            Log.error("Failed to find element by type: " + value + " in " + String.valueOf(timeOut) + " seconds.");
            result = null;
        }
        Client.setWait(Settings.defaultTimeout);
        return result;
    }

    /**
     * Find an element by locator
     */
    public static UIElement findElementByLocator(By locator) {
        return new UIElement((MobileElement) Client.driver.findElement(locator));
    }

    /**
     * Find an element by locator
     */
    public static UIElement findElementByLocator(By locator, int timeout, boolean logErrorOnNotFound) {
        Client.setWait(timeout);
        UIElement result;
        try {
            result = findElementByLocator(locator);
        } catch (Exception e) {
            if (logErrorOnNotFound) {
                Log.error("Failed to find element by locator: " + locator + " in " + String.valueOf(timeout) + " seconds.");
            }
            result = null;
        }
        Client.setWait(Settings.defaultTimeout);
        return result;
    }

    /**
     * Find an element by locator
     */
    public static UIElement findElementByLocator(By locator, int timeout) {
        return findElementByLocator(locator, timeout, true);
    }

    /**
     * Find an elements by locator
     */
    public static List<UIElement> findElementsByLocator(By locator) {
        return convertListOfMobileElementToUIElement((List<MobileElement>) Client.driver.findElements(locator));
    }

    /**
     * Find an elements by locator
     */
    public static List<UIElement> findElementsByLocator(By locator, int timeout) {
        return findElementsByLocator(locator, timeout, true);
    }

    /**
     * Find an elements by locator
     */
    public static List<UIElement> findElementsByLocator(By locator, int timeout, boolean logErrorOnNotFound) {
        Client.setWait(timeout);
        List<UIElement> result;
        try {
            result = findElementsByLocator(locator);
        } catch (Exception e) {
            if (logErrorOnNotFound) {
                Log.error("Failed to find elements by locator: " + locator + " in " + String.valueOf(timeout) + " seconds.");
            }
            result = null;
        }
        Client.setWait(Settings.defaultTimeout);
        return result;
    }

    /**
     * Find parent of an element
     */
    public static UIElement getParent(UIElement element) {
        String xpathString = element.getXpath() + "/..";
        Log.debug("Looking for parent with following xpath:" + xpathString);
        UIElement e = new UIElement((MobileElement) Client.driver.findElement(By.xpath(xpathString)));
        Log.debug("Found " + element.getDescription());
        return e;
    }

    public static UIElement by(String value, int waitForElementExists) {
        if (Settings.platform == PlatformType.Andorid) {
            return findByText(value, waitForElementExists);///Locators.findByTextLocator("*", value, true)
        }
        if (Settings.platform == PlatformType.iOS) {
            return findElementByLocator(By.id(value), waitForElementExists);
        } else {
            return null;
        }
    }

    private static List<UIElement> convertListOfMobileElementToUIElement(List<MobileElement> list) {

        ArrayList<UIElement> elements = new ArrayList<>();

        for (MobileElement element : list) {
            elements.add(new UIElement(element));
        }

        return elements;
    }
}
