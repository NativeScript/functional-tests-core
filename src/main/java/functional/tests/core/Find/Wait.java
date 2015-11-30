package functional.tests.core.Find;

import functional.tests.core.Appium.Client;
import functional.tests.core.Exceptions.AppiumException;
import functional.tests.core.Log.Log;
import functional.tests.core.Settings.Settings;
import io.appium.java_client.MobileElement;
import org.openqa.selenium.By;

import java.util.Date;
import java.util.List;

public class Wait {

    public static boolean waitForVisible(By locator, int timeOut, int retryPeriod, boolean failOnNotVisible) throws AppiumException {
        Client.setWait(0);
        long startTime = new Date().getTime();
        boolean found = false;
        for (int i = 0; i < 1000; i++) {
            if (retryPeriod > 0) {
                try {
                    Thread.sleep(retryPeriod);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            long currentTime = new Date().getTime();
            if ((currentTime - startTime) < timeOut * 1000) {
                List<MobileElement> elements = Find.findElementsByLocator(locator);

                if (elements.size() == 0) {
                    Log.debug("Element does not exist: " + locator.toString());
                } else {
                    found = true;
                    break;
                }
            }
        }
        Client.setWait(Settings.defaultTimeout);
        if (found) {
            Log.debug("Element found: " + locator.toString());
        } else {
            String error = "Element not found: " + locator.toString();
            Log.error(error);
            if (failOnNotVisible){
                throw new AppiumException(error);
            }
        }
        return found;
    }

    public static boolean waitForVisible(By locator, int timeOut) throws AppiumException {
        return waitForVisible(locator, timeOut, 0, false);
    }

    public static boolean waitForVisible(By locator) throws AppiumException {
        return waitForVisible(locator, Settings.defaultTimeout, 0, false);
    }

    public static boolean waitForNotVisible(By locator, int timeOut, boolean failOnVisble) throws AppiumException {
        Client.setWait(0);
        long startTime = new Date().getTime();
        boolean found = true;
        for (int i = 0; i < 1000; i++) {
            long currentTime = new Date().getTime();
            if ((currentTime - startTime) < timeOut * 1000) {
                List<MobileElement> elements = Find.findElementsByLocator(locator);

                if (elements.size() == 0) {
                    found = false;
                    break;
                } else {
                    Log.debug("Element exists: " + locator.toString());
                }
            }
        }
        Client.setWait(Settings.defaultTimeout);
        if (found) {
            String error = "Element still visible: " + locator.toString();
            Log.error(error);
            if (failOnVisble) {
                throw new AppiumException(error);
            }
        } else {
            Log.debug("Element not found: " + locator.toString());
        }
        return found;
    }

    public static boolean waitForNotVisible(By locator, boolean failOnVisible) throws AppiumException {
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
