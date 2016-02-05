package functional.tests.core.Element;

import functional.tests.core.Appium.Client;
import functional.tests.core.Enums.PlatformType;
import functional.tests.core.Find.Wait;
import functional.tests.core.Log.Log;
import functional.tests.core.Settings.Settings;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
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
     * Set text of MobileElement
     */
    public static void setText(MobileElement editTextElement, String value) {
        if (Settings.platform == PlatformType.Andorid) {
            // Tap at the and of the text box (this is very important)
            int currentLegth = editTextElement.getText().length();
            int x = editTextElement.getLocation().getX() + editTextElement.getSize().width - 5;
            int y = editTextElement.getLocation().getY() + (editTextElement.getSize().height / 3);
            Client.driver.tap(1, x, y, Settings.defaultTapDuration);
            Wait.sleep(Settings.defaultTapDuration);

            // Clean old value
            for (int l = 0; l < currentLegth; l++) {
                ((AndroidDriver) Client.driver).pressKeyCode(67);
                Wait.sleep(100);
            }
            Log.info("Clean old value of edit field.");
            Wait.sleep(Settings.defaultTapDuration);

            // Set new value
            editTextElement.sendKeys(value);
            Wait.sleep(Settings.defaultTapDuration);
        } else {
            editTextElement.click();
            editTextElement.clear();
            Log.info("Clean old value of edit field.");
            Wait.sleep(Settings.defaultTapDuration);
            editTextElement.sendKeys(value);
            Wait.sleep(Settings.defaultTapDuration);
        }
        Log.info("Set value of text field: " + value);
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
     * Get coordinates of MobileElement
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
