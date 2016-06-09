package functional.tests.core.Find;

import functional.tests.core.Appium.Client;
import functional.tests.core.Exceptions.AppiumException;
import functional.tests.core.Log.Log;
import functional.tests.core.Settings.Settings;
import io.appium.java_client.MobileElement;
import org.openqa.selenium.By;
import org.testng.Assert;

import java.util.Date;
import java.util.List;

public class Wait {

    public static boolean waitForVisible(By locator, int timeOut, boolean failOnNotVisible) {
        Client.setWait(timeOut);
        MobileElement result;
        try {
            result = Find.findElementByLocator(locator);
        } catch (Exception e) {
            result = null;
        }
        Client.setWait(Settings.defaultTimeout);
        if (result != null) {
            return true;
        } else {
            if (failOnNotVisible) {
                Assert.fail("Failed to find element: " + locator.toString());
            }
            return false;
        }
    }

    public static boolean waitForVisible(By locator, boolean failOnNotVisible) {
        return waitForVisible(locator, Settings.defaultTimeout, failOnNotVisible);
    }

    public static boolean waitForVisible(By locator) {
        return waitForVisible(locator, Settings.defaultTimeout, false);
    }

    public static boolean waitForNotVisible(By locator, int timeOut, boolean failOnVisble) {
        Client.setWait(1);
        long startTime = new Date().getTime();
        boolean found = true;
        for (int i = 0; i < 1000; i++) {
            long currentTime = new Date().getTime();
            if ((currentTime - startTime) < timeOut * 1000) {
                List<MobileElement> elements = null;
                try {
                    elements = Find.findElementsByLocator(locator);
                } catch (Exception e) {
                }

                if ((elements != null) && (elements.size() != 0)) {
                    Log.debug("Element exists: " + locator.toString());
                } else {
                    found = false;
                    break;
                }
            }
        }
        Client.setWait(Settings.defaultTimeout);
        if (found) {
            String error = "Element still visible: " + locator.toString();
            Log.error(error);
            if (failOnVisble) {
                Assert.fail(error);
            }
        } else {
            Log.debug("Element not found: " + locator.toString());
        }
        return found;
    }

    public static boolean waitForNotVisible(By locator, boolean failOnVisible) {
        return waitForNotVisible(locator, Settings.shortTimeout, failOnVisible);
    }

    public static boolean waitForNotVisible(By locator) throws AppiumException {
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
