package functional.tests.core.Element;

import io.appium.java_client.MobileElement;

/**
 * Created by topuzov on 27.11.15 г..
 */
public class Element {

    /**
     * Get String description of MobileElement
     */
    public static String getDescription(MobileElement element) {
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
}
