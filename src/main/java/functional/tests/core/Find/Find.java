package functional.tests.core.Find;

import functional.tests.core.Appium.Client;
import functional.tests.core.Enums.PlatformType;
import functional.tests.core.Log.Log;
import functional.tests.core.Settings.Settings;
import io.appium.java_client.MobileElement;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.reflect.FieldUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NotFoundException;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static functional.tests.core.Find.Locators.findByTextLocator;

public class Find {

    private static MobileElement element(By locator) {
        return (MobileElement) Client.driver.findElement(locator);
    }

    private static List<MobileElement> elements(By locator) {
        return (List<MobileElement>) Client.driver.findElements(locator);
    }

    /**
     * Get String description of MobileElement
     */
    private static String getDescription(MobileElement element) {
        String elementText = element.getText();
        String elementTag = element.getTagName();
        String elementCoordinates = String.valueOf(element.getLocation().x)
                + ":" + String.valueOf(element.getLocation().y);

        String descString = elementText;
        if (elementText == null) {
            descString = elementTag + " at " + elementCoordinates;
        }

        return descString;
    }

    /**
     * Get xpath of element
     */
    public static String getXpath(MobileElement element) {

        String foundBy = "";

        try {
            foundBy = FieldUtils.readField(element, "foundBy", true).toString();
        } catch (IllegalAccessException e) {
            Log.error("Failed to get find filed 'foundBy' of element: " + getDescription(element));
        }

        String[] split = foundBy.split("xpath: ");
        String xpathString = split[1];

        return xpathString;
    }

    /**
     * Find an element that has some attribute with specified value.
     */
    public static MobileElement findByText(String controlType, String value, boolean exactMatch) {
        return element(findByTextLocator(controlType, value, exactMatch));
    }

    /**
     * Find an element that has some attribute with specified value.
     */
    public static MobileElement findByText(String value, boolean exactMatch) {
        return element(findByTextLocator("*", value, exactMatch));
    }

    /**
     * Find an element that has some attribute with specified value.
     */
    public static MobileElement findByText(String value) {
        return (MobileElement) element(findByTextLocator("*", value, true));
    }

    /**
     * Find an element that has some attribute with specified value
     * Search is based on specified MobileElement
     */
    public static MobileElement findByText(MobileElement element, String value) {
        return (MobileElement) element(findByTextLocator(getXpath(element), value, true));
    }

    /**
     * Find parent of an element
     */
    public static MobileElement getParent(MobileElement element) {
        String xpathString = getXpath(element) + "/..";
        Log.debug("Looking for parent with following xpath:" + xpathString);
        return (MobileElement) Client.driver.findElement(By.xpath(xpathString));
    }

    /**
     * Return true if element is visible and false if it does not exists or not visible
     */
    public static boolean isVisible(MobileElement element) {
        try {
            if (element.isDisplayed()) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }
}
