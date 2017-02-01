package functional.tests.core.find;

import functional.tests.core.appium.Client;
import functional.tests.core.basetest.Context;
import functional.tests.core.basetest.TestContextSetupManager;
import functional.tests.core.element.UIElement;
import functional.tests.core.log.LoggerBase;
import functional.tests.core.settings.Settings;
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
    private Settings settings;

    public Wait() {
        this(TestContextSetupManager.getTestSetupManager().context);
    }

    public Wait(Context context) {
        this.client = context.client;
        this.find = context.find;
        this.settings = context.settings;
    }

    public static void sleep(int milliseconds) {
        try {
            Thread.currentThread().sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
        List<UIElement> results;
        this.client.setWait(timeOut);

        try {
            results = this.find.elementsByLocator(locator);
        } catch (Exception e) {
            results = null;
        }

        this.client.setWait(this.settings.defaultTimeout);

        if (results != null) {
            for (UIElement element : results) {
                if (element.isVisible()) {
                    return element;
                }
            }
        } else {
            new NotImplementedException("This platform: " + this.settings.platform + " is not implemented");
        }


        if (failOnNotVisible) {
            Assert.fail("Failed to find element: " + locator.toString());
        }

        return null;
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
