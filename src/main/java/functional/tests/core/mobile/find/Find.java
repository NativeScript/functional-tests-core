package functional.tests.core.mobile.find;

import functional.tests.core.enums.PlatformType;
import functional.tests.core.log.LoggerBase;
import functional.tests.core.mobile.appium.Client;
import functional.tests.core.mobile.element.UIElement;
import functional.tests.core.settings.Settings;
import functional.tests.core.utils.OSUtils;
import io.appium.java_client.MobileElement;
import org.openqa.selenium.By;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO(): Add docs.
 */
public class Find {
    private static final LoggerBase LOGGER_BASE = LoggerBase.getLogger("Find");

    private Client client;
    private functional.tests.core.mobile.find.Locators locators;
    private Settings settings;

    public Find(Client client, Locators locators, Settings settings) {
        this.client = client;
        this.locators = locators;
        this.settings = settings;
    }

    public UIElement byLocator(By locator) {
        return new UIElement((MobileElement) this.client.getDriver().findElement(locator));
    }

    public UIElement byLocator(By locator, int timeOut) {
        this.client.setWait(timeOut);
        UIElement result;
        try {
            result = this.byLocator(locator);
        } catch (Exception e) {
            LOGGER_BASE.debug("Failed to find element by locator: " + locator + " in " + String.valueOf(timeOut) + " seconds.");
            result = null;
        }

        this.client.setWait(this.settings.defaultTimeout);
        return result;
    }

    public UIElement byType(String value) {
        return new UIElement((MobileElement) this.client.getDriver().findElement(By.className(value)));
    }

    public UIElement byType(String value, int timeOut) {
        this.client.setWait(timeOut);
        UIElement result;
        try {
            result = this.byType(value);
        } catch (Exception e) {
            LOGGER_BASE.error("Failed to find element by value: " + value + " in " + String.valueOf(timeOut) + " seconds.");
            result = null;
        }
        this.client.setWait(this.settings.defaultTimeout);
        return result;
    }

    public UIElement byText(String value) {
        return this.byText(value, this.settings.shortTimeout);
    }

    public UIElement byText(String value, int timeOut) {
        this.client.setWait(timeOut);
        UIElement result;
        try {
            if (this.settings.platform == PlatformType.iOS) {
                String xcodeVersionString = OSUtils.runProcess("xcodebuild -version").split("/n")[0].replace("Xcode", "").trim();
                if (xcodeVersionString.startsWith("9")) {
                    result = this.byLocator(By.id(value));
                } else {
                    result = this.byLocator(this.locators.byText(value));
                }
            } else {
                result = this.byLocator(this.locators.byText(value));
            }
        } catch (Exception e) {
            LOGGER_BASE.error("Failed to find element by text: " + value + " in " + String.valueOf(timeOut) + " seconds.");
            result = null;
        }
        this.client.setWait(this.settings.defaultTimeout);
        return result;
    }

    public UIElement byText(String value, Boolean exactMatch, int timeOut) {
        this.client.setWait(timeOut);
        UIElement result;
        try {
            result = this.byLocator(this.locators.byText(value, exactMatch, false));
        } catch (Exception e) {
            LOGGER_BASE.error("Failed to find element by text: " + value + " in " + String.valueOf(timeOut) + " seconds.");
            result = null;
        }
        this.client.setWait(this.settings.defaultTimeout);
        return result;
    }

    public UIElement byTextContains(String value) {
        return this.byLocator(this.locators.byText(value, false, false), this.settings.shortTimeout);
    }

    public List<UIElement> elementsByLocator(By locator) {
//        if (this.settings.platform == PlatformType.Andorid && this.settings.platformVersion >= 7.0) {
//            this.client.setWait(this.settings.shortTimeout / 4);
//        }

        return this.convertListOfMobileElementToUIElement((List<MobileElement>) this.client.driver.findElements(locator));
    }

    public List<UIElement> elementsByLocator(By locator, int timeOut) {
        this.client.setWait(timeOut);
        List<UIElement> result;
        try {
            result = this.elementsByLocator(locator);
        } catch (Exception e) {
            LOGGER_BASE.error("Failed to find elements by locator: " + locator + " in " + String.valueOf(timeOut) + " seconds.");
            result = null;
        }
        this.client.setWait(this.settings.defaultTimeout);
        return result;
    }

    public List<UIElement> elementsbyType(String value) {
        return this.convertListOfMobileElementToUIElement((List<MobileElement>) this.client.driver.findElements(By.className(value)));
    }

    public List<UIElement> elementsbyType(String value, int timeOut) {
        this.client.setWait(timeOut);
        List<UIElement> result;
        try {
            result = this.elementsbyType(value);
        } catch (Exception e) {
            LOGGER_BASE.error("Failed to find elements by type: " + value + " in " + String.valueOf(timeOut) + " seconds.");
            result = null;
        }
        this.client.setWait(this.settings.defaultTimeout);
        return result;
    }

    public UIElement getParent(UIElement element) {
        String xpathString = element.getXpath() + "/..";
        LOGGER_BASE.debug("Looking for parent with the following Xpath: " + xpathString);
        UIElement e = new UIElement((MobileElement) this.client.driver.findElement(By.xpath(xpathString)));
        LOGGER_BASE.debug("Found " + e.getDescription());
        return e;
    }

    private List<UIElement> convertListOfMobileElementToUIElement(List<MobileElement> list) {
        ArrayList<UIElement> elements = new ArrayList<>();
        for (MobileElement element : list) {
            elements.add(new UIElement(element));
        }
        return elements;
    }
}
