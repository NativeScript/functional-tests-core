package functional.tests.core.mobile.find;

import functional.tests.core.log.LoggerBase;
import functional.tests.core.mobile.appium.Client;
import functional.tests.core.mobile.element.UIElement;
import functional.tests.core.mobile.settings.MobileSettings;
import org.apache.commons.lang.NotImplementedException;
import org.openqa.selenium.By;
import org.testng.Assert;

import java.util.Date;
import java.util.List;

/**
 * TODO(): Add docs.
 */
public class Wait {
    private static final LoggerBase LOGGER_BASE = LoggerBase.getLogger("Wait");

    private Client client;
    private Find find;
    private MobileSettings settings;

    public Wait(Client client, Find find, MobileSettings settings) {
        this.client = client;
        this.find = find;
        this.settings = settings;
    }

    public static void sleep(int milliseconds) {
        try {
            Thread.currentThread().sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public UIElement waitForVisibleDefault(By locator, int timeOut, boolean failOnNotVisible) {
        List<UIElement> results;
        this.client.setWait(timeOut);
        try {
            results = this.find.elementsByLocator(locator);

        } catch (Exception e) {
            results = null;
        }

        this.client.setWait(this.settings.defaultTimeout);
        return null;
    }

    /**
     * Wait for element until it gets visible.
     * (Android) Visible == Center of the element is inside view port.
     * (iOS) Visible == Top left corner of the element is inside view port.
     *
     * @param locator          Locator for element.
     * @param timeOut          Timeout.
     * @param failOnNotVisible If true this method will fail. If false and element not found it will return null.
     * @return UIElement (if found).
     */
    public UIElement waitForVisible(By locator, int timeOut, boolean failOnNotVisible) {
        List<UIElement> elements = this.forVisibleElements(locator, timeOut, failOnNotVisible);
        if (elements != null && elements.size() > 0) {
            return elements.get(0);
        }

        return null;
    }

    /**
     * Wait for element until it gets visible.
     * (Android) Visible == Center of the element is inside view port.
     * (iOS) Visible == Top left corner of the element is inside view port.
     *
     * @param locator Locator for element.
     * @param timeOut Timeout.
     * @return UIElement (if found).
     */
    public List<UIElement> forVisibleElements(By locator, int timeOut, boolean failOnNotVisible) {
        List<UIElement> results = this.forElements(locator, timeOut);
        if (results != null && results.size() > 0) {
            results.removeIf(e -> !e.isVisible());
        } else {
            new NotImplementedException("This platform: " + this.settings.platform + " is not implemented");
        }

        if (failOnNotVisible && (results == null || results.size() == 0)) {
            Assert.fail("Failed to find element: " + locator.toString());
        }

        return results;
    }

    /**
     * Wait for element until it gets visible.
     * (Android) Visible == Center of the element is inside view port.
     * (iOS) Visible == Top left corner of the element is inside view port.
     *
     * @param locator Locator for element.
     * @param timeOut Timeout.
     * @return UIElement (if found).
     */
    public List<UIElement> forElements(By locator, int timeOut) {
        List<UIElement> results = null;
        LOGGER_BASE.info("Start to search: " + System.currentTimeMillis());
        this.client.setWait(0);
        long startTime = System.currentTimeMillis();
        while ((System.currentTimeMillis() - startTime) < timeOut * 1000 && (results == null || results.size() == 0)) {
            try {
                results = this.find.elementsByLocator(locator);
            } catch (Exception e) {
                results = null;
            }
        }

        LOGGER_BASE.info("End time total: " + (System.currentTimeMillis() - startTime));

        return results;
    }

    public UIElement waitForVisible(By locator, boolean failOnNotVisible) {
        return this.waitForVisible(locator, this.settings.defaultTimeout, failOnNotVisible);
    }

    public UIElement waitForVisible(By locator) {
        return this.waitForVisible(locator, this.settings.defaultTimeout, false);
    }

    public boolean waitForNotVisible(By locator, int timeOut, boolean failOnVisible) {
        this.client.setWait(1);
        long startTime = new Date().getTime();
        boolean found = true;
        for (int i = 0; i < 1000; i++) {
            long currentTime = new Date().getTime();
            if ((currentTime - startTime) < timeOut * 1000) {
                List<UIElement> elements = null;
                try {
                    elements = this.find.elementsByLocator(locator);
                } catch (Exception e) {
                }

                if ((elements != null) && (elements.size() != 0)) {
                    LOGGER_BASE.debug("OldElement exists: " + locator.toString());
                } else {
                    found = false;
                    break;
                }
            }
        }
        this.client.setWait(this.settings.defaultTimeout);
        if (found) {
            String error = "OldElement still visible: " + locator.toString();
            LOGGER_BASE.error(error);
            if (failOnVisible) {
                Assert.fail(error);
            }
        } else {
            LOGGER_BASE.debug("OldElement not found: " + locator.toString());
        }
        return found;
    }

    public boolean waitForNotVisible(By locator, boolean failOnVisible) {
        return this.waitForNotVisible(locator, this.settings.shortTimeout, failOnVisible);
    }

    public boolean waitForNotVisible(By locator) {
        return this.waitForNotVisible(locator, this.settings.shortTimeout, true);
    }
}