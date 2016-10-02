package functional.tests.core.Find;

import functional.tests.core.Enums.PlatformType;
import functional.tests.core.Log.Log;
import functional.tests.core.Settings.Settings;
import org.apache.commons.lang.NotImplementedException;
import org.openqa.selenium.By;

public class Locators {

    public static By byText(String text) {
        return byText(text, true, false);
    }

    public static By byText(String text, boolean exactMatch, boolean caseSensitive) {

        if (Settings.platform == PlatformType.Andorid) {
            return findByTextLocator("*", text, exactMatch, caseSensitive);
        }

        if (Settings.platform == PlatformType.iOS) {
            Double platformVersion = Double.parseDouble(Settings.platformVersion.trim());
            if (platformVersion < 10) {
                return By.id(text);
            } else if (platformVersion >= 10 && (!exactMatch || !caseSensitive)) {
                return findByTextLocator("*", text, exactMatch, caseSensitive);
            }
        }

        return null;
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
        } else if (Settings.platform == PlatformType.iOS) {
            if (exactMatch) {
                return By.xpath("//" + controlType
                        + "[@label='" + value + "']");
            } else {
                return By.xpath("//" + controlType
                        + "[contains(@label,'" + value + "')]");
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

    public static By textFieldLocator() {
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
            String element = "TableView";
            if (Settings.platformVersion.startsWith("10")) {
                element = "Table";
            }

            return createIosElement(element);
        }
    }

    public static By listViewItemsLocator() {
        if (Settings.platform == PlatformType.Andorid) {
            return By.xpath("//android.widget.ListView/*");
        } else {
            String element = "TableCell";
            if (Settings.platformVersion.startsWith("10")) {
                element = "Cell";
            }

            return createIosElement(element);
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
            String text = "SearchBar";
            if(Settings.platform == PlatformType.iOS && Settings.platformVersion.startsWith("10")){
                text = "SearchField";
            }
            return createIosElement(text);
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
            String element = "WebView";
            if (Settings.platformVersion.startsWith("10")) {
                return null;
            }

            return createIosElement(element);
        }
    }

    public static By frameLayoutLocator() {
        if (Settings.platform == PlatformType.Andorid) {
            return By.className("android.widget.FrameLayout");
        } else {
            throw new NotImplementedException();
        }
    }

    // Not sure that for iOS the element is Picker
    public static By timePickerLocator() {
        if (Settings.platform == PlatformType.Andorid) {
            return By.className("android.widget.TimePicker");
        } else {
            String text = "Picker";
            if(Settings.platform == PlatformType.iOS && Settings.platformVersion.startsWith("10")){
                text = "DatePicker";
            }
            return createIosElement(text);
        }
    }

    // Not sure that for iOS the element is Picker
    public static By datePickerLocator() {
        if (Settings.platform == PlatformType.Andorid) {
            return By.className("android.widget.DatePicker");
        } else {
            String text = "Picker";
            if(Settings.platform == PlatformType.iOS && Settings.platformVersion.startsWith("10")){
                text = "DatePicker";
            }
            return createIosElement(text);
        }
    }

    // Not sure that for iOS the element is Picker
    public static By listPicker() {
        if (Settings.platform == PlatformType.Andorid) {
            return By.className("android.widget.NumberPicker");
        } else {
            return createIosElement("Picker");
        }
    }

    public static By navigationBarLocator() {
        if (Settings.platform == PlatformType.Andorid) {
            return null;
        } else {
            return createIosElement("NavigationBar");
        }
    }

    private static By createIosElement(String element) {
        return By.className(createElementClassForIos(element));
    }

    private static String createElementClassForIos(String element) {
        String xCUIElementType = "XCUIElementType";
        String uIA = "UIA";
        String elementType;

        if (Settings.platformVersion.toString().startsWith("10")) {
            elementType = xCUIElementType;
        } else {
            elementType = uIA;
        }

        return elementType + element;
    }
}