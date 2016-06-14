package functional.tests.core.BaseTest;

import functional.tests.core.BasePage.BasePage;
import functional.tests.core.Element.UIElement;
import functional.tests.core.Enums.PlatformType;
import functional.tests.core.Find.Find;
import functional.tests.core.Settings.Settings;
import org.openqa.selenium.By;

import java.util.List;

public abstract class BasePageExtended extends BasePage {

    public static UIElement find(String value) {
        if (Settings.platform == PlatformType.Andorid) {
            return findByText(value);
        }
        if (Settings.platform == PlatformType.iOS) {
            return findElementByLocator(By.id(value));
        } else {
            return null;
        }
    }

    public static UIElement findByText(String value) {
        return Find.findByText(value, Settings.shortTimeout);
    }

    public static UIElement findByType(String value) {
        return Find.findByType(value, Settings.shortTimeout);
    }

    public static UIElement findElementByLocator(By locator) {
        return Find.findElementByLocator(locator, Settings.shortTimeout);
    }

    public static List<UIElement> findElementsByLocator(By locator) {
        return Find.findElementsByLocator(locator, Settings.shortTimeout);
    }
}
