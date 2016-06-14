package functional.tests.core.App;

import functional.tests.core.Appium.Client;
import functional.tests.core.Device.Android.Adb;
import functional.tests.core.Element.UIElement;
import functional.tests.core.Enums.DeviceType;
import functional.tests.core.Enums.PlatformType;
import functional.tests.core.Find.Find;
import functional.tests.core.Find.Wait;
import functional.tests.core.Gestures.Gestures;
import functional.tests.core.Log.Log;
import functional.tests.core.Settings.Settings;
import io.appium.java_client.MobileElement;
import io.appium.java_client.SwipeElementDirection;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidKeyCode;
import org.apache.commons.lang3.NotImplementedException;
import org.openqa.selenium.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class App {

    private static final String adbPath = System.getenv("ANDROID_HOME") + File.separator + "platform-tools" + File.separator + "adb";


    /**
     * Restart application
     */
    public static void restart(String appId) throws NotImplementedException {
        Log.info("Restarting current app...");
        if (Settings.platform == PlatformType.Andorid) {
            Adb.stopApplication(appId);
            Wait.sleep(2000);
            Adb.startApplication(appId);
            Wait.sleep(2000);
        } else {
            throw new NotImplementedException("Restart app not implemented for iOS.");
        }
        Log.info("Restarted.");
    }

    public static void fullRestart() throws NotImplementedException {
        Client.driver.resetApp();
    }

    /**
     * Run app in background for X seconds *
     */
    public static void runInBackground(int seconds, String appName) {
        Log.info("Run current app in background for " + seconds + " seconds.");
        if (Settings.platform == PlatformType.Andorid) {
            Log.info("Navigate to HOME.");
            ((AndroidDriver) Client.driver).pressKeyCode(AndroidKeyCode.KEYCODE_HOME);
            if ((Settings.deviceType == DeviceType.Emulator) && (Settings.platformVersion.contains("6."))) {
                // Default Android 6 emulator report crash in home activity (not releated with NativeScript)
                // first time when hit Home button. Hack it with second hit of Home button.
                Wait.sleep(5000);
                ((AndroidDriver) Client.driver).pressKeyCode(AndroidKeyCode.KEYCODE_HOME);
            }

            // Wait specified timeout
            Wait.sleep(seconds * 1000);

            // Dismiss welcome dialog on Android 6 emulators
            if ((Settings.deviceType == DeviceType.Emulator) && (Settings.platformVersion.contains("6."))) {
                UIElement dismissButton = Find.findElementByLocator(By.id("com.android.launcher3:id/cling_dismiss_longpress_info"), Settings.shortTimeout);
                if (dismissButton != null) {
                    dismissButton.click();
                    Log.info("Tap Got IT to dismiss.");
                } else {
                    Log.info("No dialog to dismiss. Do nothing...");
                }
            }

            // Locate bottom icons bar
            By bottomToolBarLocator = By.id("com.android.launcher:id/layout");
            if (Settings.platformVersion.contains("6.")) {
                bottomToolBarLocator = By.id("com.android.launcher3:id/layout");
            } else if (Settings.platformVersion.contains("4.2")) {
                bottomToolBarLocator = By.xpath("//android.widget.TextView[@text='People']/..");
            }

            // Handle Samsung devices
            if (Settings.deviceName.toLowerCase().contains("galaxy")) {
                bottomToolBarLocator = By.xpath("//android.widget.TextView[@text='Apps']/..");
            }

            // Handle Nexus devices
            if (Settings.deviceName.toLowerCase().contains("nexus")) {
                bottomToolBarLocator = By.id("com.google.android.googlequicksearchbox:id/layout");
            }

            // Tap {Apps} button.
            List<WebElement> allAppsButtons = ((AndroidDriver) Client.driver)
                    .findElement(bottomToolBarLocator)
                    .findElements(By.className("android.widget.TextView"));

            MobileElement allAppsButton = null;

            for (WebElement element : allAppsButtons) {
                try {
                    // getText fails for {Apps} because of special symbols
                    String buttonName = element.getText();
                    Log.info(buttonName);
                    if ((buttonName.equalsIgnoreCase("")) || (buttonName.equalsIgnoreCase("apps"))) {
                        allAppsButton = (MobileElement) element;
                        break;
                    }
                } catch (Exception e) {
                    allAppsButton = (MobileElement) element;
                    break;
                }
            }

            allAppsButton.click();
            Wait.sleep(1000);
            Log.info("Tap {Apps} button.");

            // Sometimes there is error dialog and allAppsButton should be clicked again
            // Note: This breaks API17, so for Api17 - do nothing
            if (!Settings.platformVersion.contains("4.2")) {
                UIElement bottomToolBar = Find.findElementByLocator(bottomToolBarLocator, Settings.shortTimeout);
                if (bottomToolBar != null) {
                    ArrayList<UIElement> allButtons = bottomToolBar.findElements(By.className("android.widget.TextView"));
                    if (allButtons.size() > 3) {
                        allAppsButton.click();
                        Wait.sleep(1000);
                        Log.info("Tap {Apps} button again.");
                    }
                }
            }

            // Dismiss help dialog shown on some emulators
            if ((Settings.deviceType == DeviceType.Emulator)) {
                By okButtonLocator = By.id("com.android.launcher:id/cling_dismiss");
                if (Settings.platformVersion.contains("6.")) {
                    // it looks we do not need to do something
                } else if (Settings.platformVersion.contains("5")) {
                    // it looks we do not need to do something
                } else if (Settings.platformVersion.contains("4")) {
                    okButtonLocator = By.xpath("//android.widget.Button[@text='OK']");
                }
                UIElement dismissButton = Find.findElementByLocator(okButtonLocator, Settings.shortTimeout);
                if (dismissButton != null) {
                    dismissButton.click();
                    Wait.sleep(1000);
                    Log.info("Tap OK to dismiss.");
                } else {
                    Log.info("No dialog to dismiss. Do nothing...");
                }
            }

            // Swipe to find it
            SwipeElementDirection firstDirection = SwipeElementDirection.LEFT;
            SwipeElementDirection secondDirection = SwipeElementDirection.RIGHT;

            if ((Settings.deviceName.toLowerCase().contains("nexus")) || (Settings.platformVersion.contains("6."))) {
                firstDirection = SwipeElementDirection.DOWN;
                secondDirection = SwipeElementDirection.UP;
            }

            UIElement testAppButon = Gestures.swipeToElement(firstDirection, appName, Settings.defaultTapDuration * 2, 5);

            // Tap application icon
            if (testAppButon != null) {
                Log.info("Tap " + appName + " icon.");
                Wait.sleep(1000);
                testAppButon.click();
                Wait.sleep(1000);
            } else {
                Log.error("App with name " + appName + " not found.");
                testAppButon = Gestures.swipeToElement(secondDirection, appName, Settings.defaultTapDuration * 2, 5);
                if (testAppButon != null) {
                    Log.info("Tap " + appName + " icon.");
                    Wait.sleep(1000);
                    testAppButon.click();
                    Wait.sleep(1000);
                } else {
                    Log.error("App with name " + appName + " not found.");
                }
            }
        } else {
            try {
                JavascriptExecutor jse = (JavascriptExecutor) Client.driver;
                jse.executeScript("var x = target.deactivateAppForDuration(" + String.valueOf(seconds) + "); var MAX_RETRY=5, retry_count = 0; while (!x && retry_count < MAX_RETRY) { x = target.deactivateAppForDuration(2); retry_count += 1}; x");
            } catch (WebDriverException e) {
                if (e.getMessage().contains("An error occurred while executing user supplied JavaScript")) {
                    // This hack workarounds run in background issue on iOS9
                    By appLocator = By.xpath("//UIAScrollView[@name='AppSwitcherScrollView']/UIAElement");
                    MobileElement element = (MobileElement) Client.driver.findElement(appLocator);
                    int offset = 5; // 5px offset within the top-left corner of element
                    Point elementTopLeft = element.getLocation();
                    Client.driver.tap(1, elementTopLeft.x + offset, elementTopLeft.y + offset, 500);
                } else {
                    throw new RuntimeException(e);
                }
            }
        }
        Wait.sleep(1000);
        Log.info("Bring the app to front.");
    }

    /**
     * Run app in background for X seconds *
     */
    public static void runInBackground(int seconds) {
        String appName = Settings.packageId.substring(Settings.packageId.lastIndexOf(".") + 1);
        runInBackground(seconds, appName);
    }

    /**
     * Close application *
     */
    public static void closeApp() {
        Log.info("Close the app.");
        Client.driver.closeApp();
    }
}
