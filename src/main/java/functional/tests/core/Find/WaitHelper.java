package functional.tests.core.Find;

import functional.tests.core.Appium.Client;
import functional.tests.core.Element.UIElement;
import functional.tests.core.Enums.PlatformType;
import functional.tests.core.Exceptions.AppiumException;
import functional.tests.core.Log.Log;
import functional.tests.core.Settings.Settings;
import org.apache.commons.lang.NotImplementedException;
import org.openqa.selenium.By;
import org.testng.Assert;

import java.awt.*;
import java.util.Date;
import java.util.List;

public class WaitHelper {

    private Client client;
    private FindHelper find;

    public WaitHelper(Client client) {
        this.client = client;
        this.find = new FindHelper(this.client);
    }

    public UIElement waitForVisible(By locator, int timeOut, boolean failOnNotVisible) {
        List<UIElement> results;
        this.client.setWait(timeOut);

        try {
            results = this.find.elementsByLocator(locator);
        } catch (Exception e) {
            results = null;
        }

        this.client.setWait(Settings.defaultTimeout);

        if (results != null) {
            for (UIElement element : results) {
                if (element.isDisplayed()) {
                    Rectangle rect = element.getUIRectangle();
                    if (rect.x >= 0 && rect.y >= 0 && rect.width > 0 && rect.height > 0) {
                        return element;
                    }
                }
            }
        } else {
            new NotImplementedException("This platform: " + Settings.platform + " is not implemented");
        }


        if (failOnNotVisible) {
            Assert.fail("Failed to find element: " + locator.toString());
        }

        return null;
    }

    public UIElement waitForVisible(By locator, boolean failOnNotVisible) {
        return waitForVisible(locator, Settings.defaultTimeout, failOnNotVisible);
    }

    public UIElement waitForVisible(By locator) {
        return waitForVisible(locator, Settings.defaultTimeout, false);
    }

    public boolean waitForNotVisible(By locator, int timeOut, boolean failOnVisble) {
        this.client.setWait(1);
        long startTime = new Date().getTime();
        boolean found = true;
        for (int i = 0; i < 1000; i++) {
            long currentTime = new Date().getTime();
            if ((currentTime - startTime) < timeOut * 1000) {
                List<UIElement> elements = null;
                try {
                    elements = Find.findElementsByLocator(locator);
                } catch (Exception e) {
                }

                if ((elements != null) && (elements.size() != 0)) {
                    Log.debug("OldElement exists: " + locator.toString());
                } else {
                    found = false;
                    break;
                }
            }
        }
        this.client.setWait(Settings.defaultTimeout);
        if (found) {
            String error = "OldElement still visible: " + locator.toString();
            Log.error(error);
            if (failOnVisble) {
                Assert.fail(error);
            }
        } else {
            Log.debug("OldElement not found: " + locator.toString());
        }
        return found;
    }

    public boolean waitForNotVisible(By locator, boolean failOnVisible) {
        return waitForNotVisible(locator, Settings.shortTimeout, failOnVisible);
    }

    public boolean waitForNotVisible(By locator) throws AppiumException {
        return waitForNotVisible(locator, Settings.shortTimeout, true);
    }

    public static void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
