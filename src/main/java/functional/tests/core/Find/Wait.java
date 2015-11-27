package functional.tests.core.Find;

import functional.tests.core.Appium.Client;
import functional.tests.core.Log.Log;
import functional.tests.core.Settings.Settings;
import io.appium.java_client.MobileElement;
import org.openqa.selenium.By;

import java.util.Date;
import java.util.List;

public class Wait {

    public static boolean waitForVisible(By locator, int timeOut, int retryPeriod) {
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
            Log.info("Element found: " + locator.toString());
        } else {
            Log.error("Element not found: " + locator.toString());
        }
        return found;
    }

    public static boolean waitForVisible(By locator, int timeOut) {
        return waitForVisible(locator, timeOut, 0);
    }

    public static boolean waitForNotVisible(By locator, int timeOut) {
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
            Log.error("Element found: " + locator.toString());
        } else {
            Log.info("Element not found: " + locator.toString());
        }
        return found;
    }
}
