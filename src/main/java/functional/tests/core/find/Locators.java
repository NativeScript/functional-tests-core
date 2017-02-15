package functional.tests.core.find;

import functional.tests.core.enums.PlatformType;
import functional.tests.core.log.LoggerBase;
import functional.tests.core.settings.Settings;
import org.openqa.selenium.By;

/**
 * TODO(): Add docs.
 */
public class Locators {

    private static final LoggerBase LOGGER_BASE = LoggerBase.getLogger("Locators");
    private Settings settings;
    private UIElementClass uiElementClass;

    public Locators(Settings settings) {
        this.settings = settings;
        this.uiElementClass = new UIElementClass(this.settings);
    }

    public By byText(String text) {
        return this.byText(text, true, false);
    }

    public By byText(String text, boolean exactMatch, boolean caseSensitive) {
        return this.findByTextLocator("*", text, exactMatch, caseSensitive);
    }

    // TODO(): caseSensitive is ignored for iOS. Fix it!
    public By findByTextLocator(String controlType, String value, boolean exactMatch, boolean caseSensitive) {
        String toLowerCaseValue = value.toLowerCase();
        // Android
        if (this.settings.platform == PlatformType.Andorid) {
            // exactMatch = true
            if (exactMatch) {
                // caseSensitive = true
                if (caseSensitive) {
                    return By.xpath("//" + controlType
                            + "["
                            + this.getXpathComparingAttributesForEqualityForAndroid("content-desc", value) + " or "
                            + this.getXpathComparingAttributesForEqualityForAndroid("resource-id", value) + " or "
                            + this.getXpathComparingAttributesForEqualityForAndroid("text", value)
                            + "]");
                } else {
                    // caseSensitive = false
                    String result = "//" + controlType
                            + "["
                            + this.getXpathComparingAttributesForEqualityForAndroid("content-desc", toLowerCaseValue) + " or "
                            + this.getXpathComparingAttributesForEqualityForAndroid("resource-id", toLowerCaseValue) + " or "
                            + this.getXpathComparingAttributesForEqualityForAndroid("text", toLowerCaseValue)
                            + "]";
                    // LOGGER.info("Used Xpath: " + result);
                    return By.xpath(result);
                }
            } else {
                // exactMatch = falsе
                String result = "//*["
                        + this.getXpathComparingAttributesByTextContainsForAndroid("content-desc", toLowerCaseValue) + " or "
                        + this.getXpathComparingAttributesByTextContainsForAndroid("resource-id", toLowerCaseValue) + " or "
                        + this.getXpathComparingAttributesByTextContainsForAndroid("text", toLowerCaseValue)
                        + "]";
                // LOGGER.info("Used Xpath: " + result);
                return By.xpath(result);
            }

        } else if (this.settings.platform == PlatformType.iOS) {
            if (exactMatch) {
                return By.xpath("//" + controlType
                        + "[@label='" + value + "' or @value='" + value + "' or @hint='" + value + "']");
            } else {
                return By.xpath("//" + controlType
                        + "[contains(@label,'" + value + "') or contains(@value,'" + value + "') or contains(@hint,'" + value + "')]");
            }
        } else {
            String error = "findByText not implemented for platform: " + this.settings.platform;
            LOGGER_BASE.fatal(error);
            throw new UnsupportedOperationException(error);
        }
    }

    // Doesn't work for iOS 10
    private String getXpathComparingAttributesForEqualityForAndroid(String attribute, String value) {
        String result = String.format("%s=\"%s\"", this.convertAttributeValueToLowerCase(attribute), value);
        return result;
    }

    // Doesn't work for iOS 10
    private String getXpathComparingAttributesByTextContainsForAndroid(String attribute, String value) {
        String result = String.format("contains(%s,\"%s\")", this.convertAttributeValueToLowerCase(attribute), value);
        return result;
    }

    // Doesn't work for iOS 10
    private String convertAttributeValueToLowerCase(String attribute) {
        return "translate(@" + attribute + ",'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')";
    }

    public By findByTextLocator(String value, boolean exactMatch) {
        return this.findByTextLocator("*", value, exactMatch, false);
    }

    public By activityIndicatorLocator() {
        return By.className(this.uiElementClass.activityIndicatorLocator());
    }

    public By buttonLocator() {
        return By.className(this.uiElementClass.buttonLocator());
    }

    public By editTextLocator() {
        return By.className(this.uiElementClass.editTextLocator());
    }

    public By textViewLocator() {
        return By.className(this.uiElementClass.textViewLocator());
    }

    public By textFieldLocator() {
        return By.className(this.uiElementClass.textFieldLocator());
    }

    public By imageLocator() {
        return By.className(this.uiElementClass.imageLocator());
    }

    public By imageButtonLocator() {
        return By.className(this.uiElementClass.imageButtonLocator());
    }

    public By labelLocator() {
        return By.className(this.uiElementClass.labelLocator());
    }

    public By listViewLocator() {
        return By.className(this.uiElementClass.listViewLocator());
    }

    public By listViewItemsLocator() {
        if (this.settings.platform == PlatformType.Andorid) {
            return By.xpath("//" + this.uiElementClass.listViewLocator() + "/*");
        } else {
            return By.className(this.uiElementClass.cellLocator());
        }
    }

    public By recyclerViewLocator() {
        return By.className(this.uiElementClass.recyclerViewLocator());
    }

    public By progressLocator() {
        return By.className(this.uiElementClass.progressLocator());
    }

    public By scrollViewLocator() {
        return By.className(this.uiElementClass.scrollViewLocator());
    }

    public By searchBoxLocator() {
        return By.className(this.uiElementClass.searchBoxLocator());
    }

    public By sliderLocator() {
        return By.className(this.uiElementClass.sliderLocator());
    }

    public By switchLocator() {
        return By.className(this.uiElementClass.switchLocator());
    }

    public By webViewLocator() {
        return By.className(this.uiElementClass.webViewLocator());
    }

    public By viewGroupLocator() {
        return By.className(this.uiElementClass.viewGroupLocator());
    }

    public By frameLayoutLocator() {
        return By.className(this.uiElementClass.frameLayoutLocator());
    }

    // Not sure that for iOS the element is Picker
    public By timePickerLocator() {
        return By.className(this.uiElementClass.timePickerLocator());
    }

    // Not sure that for iOS the element is Picker
    public By datePickerLocator() {
        return By.className(this.uiElementClass.datePickerLocator());
    }

    // Not sure that for iOS the element is Picker
    public By listPicker() {
        return By.className(this.uiElementClass.listPicker());
    }

    public By navigationBarLocator() {
        return By.className(this.uiElementClass.navigationBarLocator());
    }

    public By segmentedControlLocator() {
        return By.className(this.uiElementClass.segmentedControlLocator());
    }

    public By tabHostLocator() {
        return By.className(this.uiElementClass.tabHostLocator());
    }

    public By tabWidgetLocator() {
        return By.className(this.uiElementClass.tabWidgetLocator());
    }
}
