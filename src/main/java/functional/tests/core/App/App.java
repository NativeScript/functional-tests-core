package functional.tests.core.App;

import functional.tests.core.Appium.Client;
import functional.tests.core.Enums.PlatformType;
import functional.tests.core.Find.Wait;
import functional.tests.core.Log.Log;
import functional.tests.core.OSUtils.OSUtils;
import functional.tests.core.Settings.Settings;
import io.appium.java_client.android.AndroidDriver;
import org.apache.commons.lang3.NotImplementedException;

public class App {

    /**
     * Restart application
     */
    public static void restart(String appId, boolean hardReset) throws NotImplementedException {
        Log.info("Restarting current app...");
        if (Settings.platform == PlatformType.Andorid) {
            String activity = ((AndroidDriver) Client.driver).currentActivity();
            stopApplication(appId);
            Wait.sleep(2000);
            startApplication(appId, activity);
            Wait.sleep(2000);
        } else {
            throw new NotImplementedException("Restart app not implemented for iOS.");
        }
        Log.info("Restarted.");
    }

    public static void restart(String appId) throws NotImplementedException {
        restart(appId, false);
    }

    public static void fullRestart() throws NotImplementedException {
        Client.driver.resetApp();
    }

    /**
     * Run app in background for X seconds *
     */
    public static void runInBackground(int seconds) {
        Log.info("Run current app in background for " + seconds + " seconds.");
        Client.driver.runAppInBackground(seconds);
        Log.info("Bring the app to front.");
    }

    /**
     * Stop application *
     */
    public static void stopApplication(String appId) throws NotImplementedException {
        Log.info("Stop " + appId);
        if (Settings.platform == PlatformType.Andorid) {
            String homeCommand = "adb -s " + Settings.deviceId + " shell am force-stop " + appId;
            OSUtils.runProcess(homeCommand);
            Wait.sleep(1000);
        } else {
            throw new NotImplementedException("Stop application not implemented for current platform.");
        }
    }

    /**
     * Start application *
     */
    public static void startApplication(String appId, String activity) throws NotImplementedException {
        Log.info("Start " + appId);
        if (Settings.platform == PlatformType.Andorid) {
            String homeCommand = "adb -s " + Settings.deviceId + " shell am start -a android.intent.action.MAIN -n " + appId + "/" + activity;
            OSUtils.runProcess(homeCommand);
            Wait.sleep(1000);
        } else {
            throw new NotImplementedException("Start application not implemented for current platform.");
        }
    }

    /**
     * Close application *
     */
    public static void closeApp() {
        Log.info("Close the app.");
        Client.driver.closeApp();
    }
}
