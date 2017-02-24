package functional.tests.core.web.find;

import org.openqa.selenium.By;

/**
 * Locators.
 */
public class Locators {
    public Locators() {
    }

    public By byTextContains(String tag, String text) {
        String result = "//" + tag
                + "[contains(text(),'" + text + "')]";
        return By.xpath(result);
    }

    public By byTextContains(String text) {
        String result = "//*[contains(text(),'" + text + "')]";
        return By.xpath(result);
    }

    public By byTextContainsCaseInsesitive(String tag, String text) {
        String result = "//" + tag + "[contains(translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'),'" + text.toLowerCase() + "')]";
        return By.xpath(result);
    }
}
