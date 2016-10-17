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
            } else if (platformVersion >= 10) {
                return findByTextLocator("*", text, exactMatch, caseSensitive);
            }
        }

        return null;
    }

    public static By findByTextLocator(String controlType, String value, boolean exactMatch, boolean caseSensitive) {
        String toLowerCaseValue = value.toLowerCase();
        // Android
        if (Settings.platform == PlatformType.Andorid) {
            // exactMatch = true
            if (exactMatch) {
                // caseSensitive = true
                if (caseSensitive) {
                    return By.xpath("//" + controlType
                            + "["
                            + getXpathComparingAttributesForEqualityForAndroid("content-desc", value) + " or "
                            + getXpathComparingAttributesForEqualityForAndroid("resource-id", value) + " or "
                            + getXpathComparingAttributesForEqualityForAndroid("text", value)
                            + "]");
                } else
                // caseSensitive = false
                {
                    String result = "//" + controlType
                            + "["
                            + getXpathComparingAttributesForEqualityForAndroid("content-desc", toLowerCaseValue) + " or "
                            + getXpathComparingAttributesForEqualityForAndroid("resource-id", toLowerCaseValue) + " or "
                            + getXpathComparingAttributesForEqualityForAndroid("text", toLowerCaseValue)
                            + "]";
                    // Log.info("Used Xpath: " + result);
                    return By.xpath(result);
                }
            } else {
                // exactMatch = falsе
                String result = "//*["
                        + getXpathComparingAttributesByTextContainsForAndroid("content-desc", toLowerCaseValue) + " or "
                        + getXpathComparingAttributesByTextContainsForAndroid("resource-id", toLowerCaseValue) + " or "
                        + getXpathComparingAttributesByTextContainsForAndroid("text", toLowerCaseValue)
                        + "]";
                // Log.info("Used Xpath: " + result);
                return By.xpath(result);
            }

        } else if (Settings.platform == PlatformType.iOS) {
            if (exactMatch) {
                return By.xpath("//" + controlType
                        + "[@label='" + value + "' or @value='" + value + "' or @hint='" + value + "']");
            } else {
                return By.xpath("//" + controlType
                        + "[contains(@label,'" + value + "') or contains(@value,'" + value + "') or contains(@hint,'" + value + "')]");
            }
        } else {
            String error = "findByText not implemented for platform: " + Settings.platform;
            Log.fatal(error);
            throw new UnsupportedOperationException(error);
        }
    }

    // Doesn't work for iOS 10
    private static String getXpathComparingAttributesForEqualityForAndroid(String attribute, String value) {
        String result = String.format("%s=\"%s\"", convertAttributeValueToLowerCase(attribute), value);
        return result;
    }

    // Doesn't work for iOS 10
    private static String getXpathComparingAttributesByTextContainsForAndroid(String attribute, String value) {
        String result = String.format("contains(%s,\"%s\")", convertAttributeValueToLowerCase(attribute), value);
        return result;
    }

    // Doesn't work for iOS 10
    private static String convertAttributeValueToLowerCase(String attribute) {
        return "translate(@" + attribute + ",'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')";
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
            if (Settings.platform == PlatformType.iOS && Settings.platformVersion.startsWith("10")) {
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

    public static By viewGroupLocator() {
        if (Settings.platform == PlatformType.Andorid) {
            double platform = Double.parseDouble(Settings.platformVersion);
            if (platform > 5.1) {
                return By.className("android.view.ViewGroup");
            } else {
                return By.className("android.view.View");
            }
        } else {
            throw new NotImplementedException();
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
            if (Settings.platform == PlatformType.iOS && Settings.platformVersion.startsWith("10")) {
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
            if (Settings.platform == PlatformType.iOS && Settings.platformVersion.startsWith("10")) {
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

    public static By segmentedControlLocator() {
        if (Settings.platform == PlatformType.Andorid) {
            return null;
        } else {
            return createIosElement("SegmentedControl");
        }
    }

    public static By tabHostLocator() {
        if (Settings.platform == PlatformType.Andorid) {
            return By.className("android.widget.TabHost");
        } else {
            return null;
        }
    }

    public static By tabWidgetLocator() {
        if (Settings.platform == PlatformType.Andorid) {
            return By.className("android.widget.TabWidget");
        } else {
            return null;
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