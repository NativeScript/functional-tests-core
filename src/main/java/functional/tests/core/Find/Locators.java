package functional.tests.core.Find;

import functional.tests.core.Enums.PlatformType;
import functional.tests.core.Log.Log;
import functional.tests.core.Settings.Settings;
import org.openqa.selenium.By;

public class Locators {

    public static By byText(String text) {
        return byText(text, true, false);
    }

    public static By byText(String text, boolean exactMatch, boolean caseSensitive) {
        if (Settings.platform == PlatformType.Andorid) {
            return findByTextLocator("*", text, exactMatch, caseSensitive);
        } else if (Settings.platform == PlatformType.iOS) {
            return By.id(text);
        } else {
            return null;
        }
    }

    public static By findByTextLocator(String controlType, String value, boolean exactMatch, boolean caseSensitive) {

        // Android
        if (Settings.platform == PlatformType.Andorid) {

            // exactMatch = true
            if (exactMatch) {

                // caseSensitive = true
                if (caseSensitive) {
                    return By.xpath("//" + controlType
                            + "[@content-desc=\"" + value
                            + "\" or @resource-id=\"" + value
                            + "\" or @text=\"" + value
                            + "\"]");
                } else

                // caseSensitive = false
                {
                    return By.xpath("//" + controlType
                            + "[@content-desc=\"" + value
                            + "\" or @content-desc=\"" + value.toLowerCase()
                            + "\" or @content-desc=\"" + value.toUpperCase()
                            + "\" or @resource-id=\"" + value
                            + "\" or @resource-id=\"" + value.toLowerCase()
                            + "\" or @resource-id=\"" + value.toUpperCase()
                            + "\" or @text=\"" + value
                            + "\" or @text=\"" + value.toLowerCase()
                            + "\" or @text=\"" + value.toUpperCase()
                            + "\"]");
                }

            } else
            // exactMatch = false
            {

                // caseSensitive = true
                if (caseSensitive) {
                    return By.xpath("//*"
                            + "[contains(@content-desc,\"" + value
                            + "\") or contains(@resource-id,\"" + value
                            + "\") or contains(@text,\"" + value
                            + "\")]");
                } else

                // caseSensitive = false
                {
                    return By.xpath("//*"
                            + "[contains(@content-desc,\"" + value
                            + "\") or contains(@content-desc,\"" + value.toLowerCase()
                            + "\") or contains(@content-desc,\"" + value.toUpperCase()
                            + "\") or contains(@resource-id,\"" + value
                            + "\") or contains(@resource-id,\"" + value.toLowerCase()
                            + "\") or contains(@resource-id,\"" + value.toUpperCase()
                            + "\") or contains(@text,\"" + value
                            + "\") or contains(@text,\"" + value.toLowerCase()
                            + "\") or contains(@text,\"" + value.toUpperCase()
                            + "\")]");
                }

            }
        } else
            // iOS
            // TODO: Refactor
            if (Settings.platform == PlatformType.iOS) {
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
        return findByTextLocator("*", value, exactMatch, false);
    }

    public static By activityIndicatorLocator() {
        if (Settings.platform == PlatformType.Andorid) {
            return By.className("android.widget.ProgressBar");
        } else {
            return createIosElement("ActivityIndicator");
        }
    }

    public static By buttonLocator() {
        if (Settings.platform == PlatformType.Andorid) {
            return By.className("android.widget.Button");
        } else {
            return createIosElement("Button");
        }
    }

    public static By editTextLocator() {
        if (Settings.platform == PlatformType.Andorid) {
            return By.className("android.widget.EditText");
        } else {
            return createIosElement("TextField");
        }
    }

    public static By textViewLocator() {
        if (Settings.platform == PlatformType.Andorid) {
            return By.className("android.widget.TextView");
        } else if (Settings.platform == PlatformType.iOS) {
            return createIosElement("TextView");
        } else {
            try {
                throw new Exception("Not found.");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public static By imageLocator() {
        if (Settings.platform == PlatformType.Andorid) {
            return By.className("android.widget.ImageView");
        } else {
            return createIosElement("Image");
        }
    }

    public static By imageButtonLocator() {
        if (Settings.platform == PlatformType.Andorid) {
            return By.className("android.widget.ImageButton");
        } else {
            return null;
        }
    }

    public static By labelLocator() {
        if (Settings.platform == PlatformType.Andorid) {
            return By.className("android.widget.TextView");
        } else {
            return createIosElement("StaticText");
        }
    }

    public static By listViewLocator() {
        if (Settings.platform == PlatformType.Andorid) {
            return By.className("android.widget.ListView");
        } else {
            return createIosElement("TableView");
        }
    }

    public static By listViewItemsLocator() {
        if (Settings.platform == PlatformType.Andorid) {
            return By.xpath("//android.widget.ListView/*");
        } else {
            return createIosElement("TableCell");
        }
    }

    public static By progressLocator() {
        if (Settings.platform == PlatformType.Andorid) {
            return By.className("android.widget.ProgressBar");
        } else {
            return createIosElement("ProgressIndicator");
        }
    }

    public static By scrollViewLocator() {
        if (Settings.platform == PlatformType.Andorid) {
            return By.className("android.widget.ScrollView");
        } else {
            return createIosElement("ScrollView");
        }
    }

    public static By searchBoxLocator() {
        if (Settings.platform == PlatformType.Andorid) {
            return By.className("android.widget.EditText");
        } else {
            return createIosElement("SearchBar");
        }
    }

    public static By sliderLocator() {
        if (Settings.platform == PlatformType.Andorid) {
            return By.className("android.widget.SeekBar");
        } else {
            return createIosElement("Slider");
        }
    }

    public static By switchLocator() {
        if (Settings.platform == PlatformType.Andorid) {
            return By.className("android.widget.Switch");
        } else {
            return createIosElement("Switch");
        }
    }

    public static By webViewLocator() {
        if (Settings.platform == PlatformType.Andorid) {
            return By.className("android.webkit.WebView");
        } else {
            return createIosElement("ScrollView");
        }
    }

    private static By createIosElement(String element) {
        String xCUIElementType = "XCUIElementType";
        String uIA = "UIA";
        String elementType;

        if (Settings.platformVersion.toString().startsWith("10")) {
            elementType = xCUIElementType;
        } else {
            elementType = uIA;
        }

        return By.className(elementType + element);
    }
}