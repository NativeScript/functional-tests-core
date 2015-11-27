package functional.tests.core.Find;

import functional.tests.core.Appium.Client;
import functional.tests.core.Element.Element;
import functional.tests.core.Log.Log;
import functional.tests.core.Settings.Settings;
import io.appium.java_client.MobileElement;
import org.apache.commons.lang.reflect.FieldUtils;
import org.openqa.selenium.By;

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
     * Get xpath of element
     */
    public static String getXpath(MobileElement element) {

        String foundBy = "";

        try {
            foundBy = FieldUtils.readField(element, "foundBy", true).toString();
        } catch (IllegalAccessException e) {
            Log.error("Failed to get find filed 'foundBy' of element: " + Element.getDescription(element));
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
        String parentXpath = getXpath(element);
        parentXpath = parentXpath.substring(2);

        By locator = Locators.findByTextLocator(value, true);
        String childXpath = locator.toString().replace("By.xpath: //", "").replace("//*", "");

        String finalXpath = "//" + parentXpath + "/" + childXpath;
        Log.debug("Looking for element with following xpath:" + finalXpath);
        MobileElement e = (MobileElement) Client.driver.findElement(By.xpath(finalXpath));
        Log.debug("Found " + Element.getDescription(e));
        return e;
    }

    /**
     * Find parent of an element
     */
    public static MobileElement getParent(MobileElement element) {
        String xpathString = getXpath(element) + "/..";
        Log.debug("Looking for parent with following xpath:" + xpathString);
        MobileElement e = (MobileElement) Client.driver.findElement(By.xpath(xpathString));
        Log.debug("Found " + Element.getDescription(e));
        return e;
    }

    /**
     * Return true if element is visible and false if it does not exists or not visible
     */
    public static boolean isVisible(MobileElement element) {
        Client.driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
        boolean visibility = false;
        try {
            if (element.isDisplayed()) {
                visibility = true;
            } else {
                visibility = false;
            }
        } catch (Exception e) {
            visibility = false;
        }
        Client.driver.manage().timeouts().implicitlyWait(Settings.defaultTimeout, TimeUnit.SECONDS);
        return visibility;
    }
}
