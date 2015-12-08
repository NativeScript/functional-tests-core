package functional.tests.core.Element;

import functional.tests.core.Log.Log;
import io.appium.java_client.MobileElement;
import org.apache.commons.lang.reflect.FieldUtils;

public class Element {

    /**
     * Get text of MobileElement
     */
    public static String getText(MobileElement element) {
        String elementText = null;
        try {
            elementText = element.getText();
        } catch (Exception e) {
        }
        return elementText;
    }

    /**
     * Get text of MobileElement
     */
    public static String getTagName(MobileElement element) {
        String tagName = null;
        try {
            tagName = element.getTagName();
        } catch (Exception e) {
        }
        return tagName;
    }

    /**
     * Get text of MobileElement
     */
    public static String getCoordinates(MobileElement element) {
        return String.valueOf(element.getCenter().x) + ":" + String.valueOf(element.getCenter().y);
    }

    /**
     * Get String description of MobileElement
     */
    public static String getDescription(MobileElement element) {
        String elementText = getText(element);
        if (elementText != null) {
            return elementText;
        } else {
            String elementTag = getTagName(element);
            if (elementTag != null) {
                return elementTag + " at " + getCoordinates(element);
            } else {
                return "Element at " + getCoordinates(element);
            }
        }
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
}
