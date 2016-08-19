package functional.tests.core.Find;

import functional.tests.core.Enums.PlatformType;
import functional.tests.core.Log.Log;
import functional.tests.core.Settings.Settings;
import org.openqa.selenium.By;

public class Locators {

    public static By byText(String text) {
        return byText(text, true);
    }

    public static By byText(String text, boolean exactMatch) {
        if (Settings.platform == PlatformType.Andorid) {
            return findByTextLocator("*", text, false);
        } else if (Settings.platform == PlatformType.iOS) {
            return By.id(text);
        } else {
            return null;
        }
    }

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
            throw new UnsupportedOperationException(error);
        }
    }

    public static By findByTextLocator(String value, boolean exactMatch) {
        return findByTextLocator("*", value, exactMatch);
    }

    public static By activityIndicatorLocator() {
        if (Settings.platform == PlatformType.Andorid) {
            return By.className("android.widget.ProgressBar");
        } else {
            return By.className("UIAActivityIndicator");
        }
    }

    public static By buttonLocator() {
        if (Settings.platform == PlatformType.Andorid) {
            return By.className("android.widget.Button");
        } else {
            return By.className("UIAButton");
        }
    }

    public static By editTextLocator() {
        if (Settings.platform == PlatformType.Andorid) {
            return By.className("android.widget.EditText");
        } else {
            return By.className("UIATextField");
        }
    }

    public static By imageLocator() {
        if (Settings.platform == PlatformType.Andorid) {
            return By.className("android.widget.ImageView");
        } else {
            return By.className("UIAImage");
        }
    }

    public static By labelLocator() {
        if (Settings.platform == PlatformType.Andorid) {
            return By.className("android.widget.TextView");
        } else {
            return By.className("UIAStaticText");
        }
    }

    public static By listViewLocator() {
        if (Settings.platform == PlatformType.Andorid) {
            return By.className("android.widget.ListView");
        } else {
            return By.className("UIATableView");
        }
    }

    public static By listViewItemsLocator() {
        if (Settings.platform == PlatformType.Andorid) {
            return By.xpath("//android.widget.ListView/*");
        } else {
            return By.xpath("//UIATableView/UIATableCell");
        }
    }

    public static By progressLocator() {
        if (Settings.platform == PlatformType.Andorid) {
            return By.className("android.widget.ProgressBar");
        } else {
            return By.className("UIAProgressIndicator");
        }
    }

    public static By scrollViewLocator() {
        if (Settings.platform == PlatformType.Andorid) {
            return By.className("android.widget.ScrollView");
        } else {
            return By.className("UIAScrollView");
        }
    }

    public static By searchBoxLocator() {
        if (Settings.platform == PlatformType.Andorid) {
            return By.className("android.widget.EditText");
        } else {
            return By.className("UIASearchBar");
        }
    }

    public static By sliderLocator() {
        if (Settings.platform == PlatformType.Andorid) {
            return By.className("android.widget.SeekBar");
        } else {
            return By.className("UIASlider");
        }
    }

    public static By switchLocator() {
        if (Settings.platform == PlatformType.Andorid) {
            return By.className("android.widget.Switch");
        } else {
            return By.className("UIASwitch");
        }
    }

    public static By webViewLocator() {
        if (Settings.platform == PlatformType.Andorid) {
            return By.className("android.webkit.WebView");
        } else {
            return By.className("UIAScrollView");
        }
    }
}