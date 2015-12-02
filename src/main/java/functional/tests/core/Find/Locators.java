package functional.tests.core.Find;

import functional.tests.core.Enums.PlatformType;
import functional.tests.core.Log.Log;
import functional.tests.core.Settings.Settings;
import org.apache.commons.lang.NotImplementedException;
import org.openqa.selenium.By;

import java.util.List;

public class Locators {

    public static By findByTextLocator(String controlType, String value,
                                          boolean exactMatch) {
        if (Settings.platform == PlatformType.Andorid) {
            if (exactMatch) {
                return By.xpath("//" + controlType + "[@content-desc=\""
                        + value + "\" or @resource-id=\"" + value
                        + "\" or @text=\"" + value + "\"]");
            } else {
                return By.xpath("//" + controlType + "[@content-desc=\""
                        + value + "\" or @resource-id=\"" + value
                        + "\" or @text=\"" + value
                        + "\"] | //*[contains(translate(@content-desc,\""
                        + value + "\",\"" + value + "\"), \"" + value
                        + "\") or contains(translate(@text,\"" + value
                        + "\",\"" + value + "\"), \"" + value
                        + "\") or @resource-id=\"" + value + "\"]");
            }
        } else if (Settings.platform == PlatformType.iOS) {
            if (exactMatch) {
                // TODO : Fix the logic in this if statement
                String up = value.toUpperCase();
                String down = value.toLowerCase();
                return By.xpath("//" + controlType
                        + "[@visible=\"true\" and (contains(translate(@name,\""
                        + up + "\",\"" + down + "\"), \"" + down
                        + "\") or contains(translate(@hint,\"" + up + "\",\""
                        + down + "\"), \"" + down
                        + "\") or contains(translate(@label,\"" + up + "\",\""
                        + down + "\"), \"" + down
                        + "\") or contains(translate(@value,\"" + up + "\",\""
                        + down + "\"), \"" + down + "\"))]");
            } else {
                String up = value.toUpperCase();
                String down = value.toLowerCase();
                return By.xpath("//" + controlType
                        + "[@visible=\"true\" and (contains(translate(@name,\""
                        + up + "\",\"" + down + "\"), \"" + down
                        + "\") or contains(translate(@hint,\"" + up + "\",\""
                        + down + "\"), \"" + down
                        + "\") or contains(translate(@label,\"" + up + "\",\""
                        + down + "\"), \"" + down
                        + "\") or contains(translate(@value,\"" + up + "\",\""
                        + down + "\"), \"" + down + "\"))]");
            }
        } else {
            String error = "findByText not implemented for platform: " + Settings.platform;
            Log.fatal(error);
            throw new NotImplementedException(error);
        }
    }

    public static By findByTextLocator(String value, boolean exactMatch){
        return findByTextLocator("*", value, exactMatch);
    }

    public static By switchLocator() {
        if (Settings.platform == PlatformType.Andorid) {
            return By.className("android.widget.Switch");
        } else {
            return By.className("UIASwitch");
        }
    }

    public static By scrollViewLocator() {
        if (Settings.platform == PlatformType.Andorid) {
            return By.className("android.widget.ScrollView");
        } else {
            return By.className("UIAScrollView");
        }
    }

    public static By activityIndicatorLocator() {
        if (Settings.platform == PlatformType.Andorid) {
            return By.className("android.widget.ProgressBar");
        } else {
            return By.className("UIAActivityIndicator");
        }
    }
}