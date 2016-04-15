package functional.tests.core.Device.Android;

import functional.tests.core.Enums.DeviceType;
import functional.tests.core.Exceptions.DeviceException;
import functional.tests.core.Find.Wait;
import functional.tests.core.Log.Log;
import functional.tests.core.Settings.Settings;

import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class AndroidDevice {

    public static void initDevice() throws TimeoutException, DeviceException {
        if (!Settings.isRealDevice) {
            // Create emulator if create emulator options are available
            if (Settings.emulatorCreateOptions != null)
                Adb.createEmulator(Settings.deviceName, Settings.emulatorCreateOptions);

            String port = Settings.deviceId.split("-")[1];
            Adb.startEmulator(Settings.deviceName, Integer.valueOf(port));
        }
        Adb.waitForDevice(Settings.deviceId, Settings.deviceBootTimeout);
        if (!Settings.isRealDevice) {
            // Wait until emulator boot
            Adb.waitUntilEmulatorBoot(Settings.deviceId, Settings.deviceBootTimeout);
            // Unlock if locked
            if (Adb.isLocked(Settings.deviceId)) {
                Log.info("Device is locked.");
                Adb.unlock(Settings.deviceId);
                Wait.sleep(3000);
                Log.info("Device locked: " + String.valueOf(Adb.isLocked(Settings.deviceId)));
            }
        }
    }

    private static boolean available(int port) {
        Log.debug("--------------Testing port " + port);
        Socket s = null;
        try {
            s = new Socket("localhost", port);

            // If the code makes it this far without an exception it means
            // something is using the port and has responded.
            Log.debug("--------------Port " + port + " is not available");
            return false;
        } catch (IOException e) {
            Log.debug("--------------Port " + port + " is available");
            return true;
        } finally {
            if (s != null) {
                try {
                    s.close();
                } catch (IOException e) {
                    throw new RuntimeException("You should handle this error.", e);
                }
            }
        }
    }

    public static void stopDevice() {
        if (Settings.deviceType == DeviceType.Emulator) {
            Adb.stopEmulator();
            // This is to fix the case when you have Androdi Studio 2 and previous CI stop build is force stopped
            int port = Integer.parseInt(Settings.deviceId.substring(Settings.deviceId.lastIndexOf("-") + 1));
            if (!available(port)) {
                Log.info("Port " + port + " in use.");
                Adb.startAdb();
                Adb.startAdb();
            }
            if (available(port)) {
                Log.fatal("Port " + port + " still in use. Most likely emulator will not start.");
            }
        }
    }

    public static void stopApps(List<String> uninstallAppsList) {
        List<String> installedApps = Adb.getInstalledApps();

        for (String appToUninstall : uninstallAppsList) {
            for (String appId : installedApps) {
                if (appId.contains(appToUninstall)) {
                    Log.info("Stop " + appId);
                    Adb.stopApp(appId);
                }
            }
        }
    }

    public static void uninstallApps(List<String> uninstallAppsList) {
        List<String> installedApps = Adb.getInstalledApps();

        for (String appToUninstall : uninstallAppsList) {
            for (String appId : installedApps) {
                if (appId.contains(appToUninstall)) {
                    Adb.uninstallApp(appId);
                }
            }
        }
    }
}
