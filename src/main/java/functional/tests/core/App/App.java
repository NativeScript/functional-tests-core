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
import org.openqa.selenium.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class App {

    private static final String adbPath = System.getenv("ANDROID_HOME") + File.separator + "platform-tools" + File.separator + "adb";


    /**
     * Restart application
     */
    public static void restart(String appId) throws UnsupportedOperationException {
        Log.info("Restarting current app...");
        if (Settings.platform == PlatformType.Andorid) {
            Adb.stopApplication(appId);
            Wait.sleep(2000);
            Adb.startApplication(appId);
            Wait.sleep(2000);
        } else {
            throw new UnsupportedOperationException("Restart app not implemented for iOS.");
        }
        Log.info("Restarted.");
    }

    public static void fullRestart() throws UnsupportedOperationException {
        Client.driver.resetApp();
    }

    /**
     * Run app in background for X seconds *
     */
    public static void runInBackground(int seconds) {
        Log.info("Run current app in background for " + seconds + " seconds.");
        Adb.runAdbCommand(Settings.deviceId, "shell input keyevent 3");
        Wait.sleep(seconds * 1000);
        Adb.runAdbCommand(Settings.deviceId, "shell monkey -p " + Settings.packageId + " -c android.intent.category.LAUNCHER 1");
        Log.info("Bring " + Settings.packageId + " to front.");
        Wait.sleep(3000);
    }

    /**
     * Close application *
     */
    public static void closeApp() {
        Log.info("Close the app.");
        Client.driver.closeApp();
    }
}
