package functional.tests.core.Find;

import functional.tests.core.Appium.Client;
import functional.tests.core.Element.Element;
import functional.tests.core.Log.Log;
import functional.tests.core.Settings.Settings;
import io.appium.java_client.MobileElement;
import org.openqa.selenium.By;

import java.util.List;

public class Find {

    /**
     * Find an element that has some attribute with specified value.
     */
    public static MobileElement findByText(String controlType, String value, boolean exactMatch) {
        return findElementByLocator(Locators.findByTextLocator(controlType, value, exactMatch));
    }

    /**
     * Find an element that has some attribute with specified value.
     */
    public static MobileElement findByText(String value, boolean exactMatch) {
        return findElementByLocator(Locators.findByTextLocator("*", value, exactMatch));
    }

    /**
     * Find an element that has some attribute with specified value.
     */
    public static MobileElement findByText(String value) {
        return (MobileElement) findElementByLocator(Locators.findByTextLocator("*", value, true));
    }

    /**
     * Find an element that has some attribute with specified value.
     */
    public static MobileElement findByText(String value, int timeOut) {
        Client.setWait(timeOut);
        MobileElement result;
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
     * Search is based on specified MobileElement
     */
    public static MobileElement findByText(MobileElement element, String value) {
        String parentXpath = Element.getXpath(element);
        parentXpath = parentXpath.substring(2);

        By locator = Locators.findByTextLocator(value, true);
        String childXpath = locator.toString().replace("By.xpath: //", "").replace("//*", "");

        String finalXpath = "//" + parentXpath + "//" + childXpath;
        Log.debug("Looking for element with following xpath:" + finalXpath);
        MobileElement e = (MobileElement) Client.driver.findElement(By.xpath(finalXpath));
        Log.debug("Found " + Element.getDescription(e));
        return e;
    }

    /**
     * Find an element by class (UI control class)
     */
    public static MobileElement findByType(String value) {
        return (MobileElement) Client.driver.findElement(By.className(value));
    }

    /**
     * Find an element by locator
     */
    public static MobileElement findElementByLocator(By locator) {
        return (MobileElement) Client.driver.findElement(locator);
    }

    /**
     * Find an element by locator
     */
    public static MobileElement findElementByLocator(By locator, int timeout) {
        Client.setWait(timeout);
        MobileElement result;
        try {
            result = findElementByLocator(locator);
        } catch (Exception e) {
            Log.error("Failed to find element by locator: " + locator + " in " + String.valueOf(timeout) + " seconds.");
            result = null;
        }
        Client.setWait(Settings.defaultTimeout);
        return result;
    }

    /**
     * Find an elements by locator
     */
    public static List<MobileElement> findElementsByLocator(By locator) {
        return (List<MobileElement>) Client.driver.findElements(locator);
    }

    /**
     * Find an elements by locator
     */
    public static List<MobileElement> findElementsByLocator(By locator, int timeout) {
        Client.setWait(timeout);
        List<MobileElement> result;
        try {
            result = findElementsByLocator(locator);
        } catch (Exception e) {
            Log.error("Failed to find elements by locator: " + locator + " in " + String.valueOf(timeout) + " seconds.");
            result = null;
        }
        Client.setWait(Settings.defaultTimeout);
        return result;
    }

    /**
     * Find parent of an element
     */
    public static MobileElement getParent(MobileElement element) {
        String xpathString = Element.getXpath(element) + "/..";
        Log.debug("Looking for parent with following xpath:" + xpathString);
        MobileElement e = (MobileElement) Client.driver.findElement(By.xpath(xpathString));
        Log.debug("Found " + Element.getDescription(e));
        return e;
    }
}
