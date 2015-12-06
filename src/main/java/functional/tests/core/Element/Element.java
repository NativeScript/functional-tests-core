package functional.tests.core.Element;

import functional.tests.core.Log.Log;
import io.appium.java_client.MobileElement;
import org.apache.commons.lang.reflect.FieldUtils;

public class Element {

    /**
     * Get String description of MobileElement
     */
    public static String getDescription(MobileElement element) {
        String elementText = "";
        String elementTag = "";

        try {
            elementText = element.getText();
        } catch (Exception e) {
        }
        try {
            elementTag = element.getTagName();
        } catch (Exception e) {
        }

        String elementCoordinates =
                String.valueOf(element.getCenter().x)
                        + ":" + String.valueOf(element.getCenter().y);

        if (elementText != "") {
            return elementText + " at " + elementCoordinates;
        } else if (elementTag != "") {
            return elementTag + " at " + elementCoordinates;
        } else {
            return "at " + elementCoordinates;
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
