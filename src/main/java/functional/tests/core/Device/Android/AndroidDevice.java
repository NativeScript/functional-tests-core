package functional.tests.core.Device.Android;

import functional.tests.core.Enums.DeviceType;
import functional.tests.core.Exceptions.DeviceException;
import functional.tests.core.Log.Log;
import functional.tests.core.Settings.Settings;

import java.util.List;
import java.util.concurrent.TimeoutException;

public class AndroidDevice {

    public static void initDevice() throws TimeoutException, InterruptedException, DeviceException {
        if (!Settings.isRealDevice) {
            // Create emulator if create emulator options are available
            if (Settings.emulatorCreateOptions != null)
                Adb.createEmulator(Settings.deviceName, Settings.emulatorCreateOptions);

            String port = Settings.deviceId.split("-")[1];
            Adb.startEmulator(Settings.deviceName, Integer.valueOf(port));
        }
        Adb.waitForDevice(Settings.deviceId, Settings.deviceBootTimeout);
    }

    public static void stopDevice() {
        if (DeviceType.Emulator == Settings.deviceType) Adb.stopEmulator();
    }

    public static void stopApps(List<String> uninstallAppsList) {
        List<String> installedApps = Adb.getInstalledApps();

        for (String appToUninstall : uninstallAppsList) {
            for (String appId : installedApps) {
                if (appId.contains(appToUninstall)){
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
                if (appId.contains(appToUninstall)){
                    Adb.uninstallApp(appId);
                }
            }
        }
    }
}
