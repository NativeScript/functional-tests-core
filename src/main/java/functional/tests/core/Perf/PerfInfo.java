package functional.tests.core.Perf;

import functional.tests.core.Device.Android.Adb;
import functional.tests.core.Enums.PlatformType;
import functional.tests.core.Log.Log;
import functional.tests.core.Settings.Settings;

public class PerfInfo {

    public static int getMem(String deviceId) {
        if (Settings.platform == PlatformType.Andorid) {
            String command = "shell dumpsys meminfo | grep " + Settings.packageId;
            String output = Adb.runAdbCommand(deviceId, command);
            if (output.contains(Settings.packageId)) {
                String memString = output.split(":")[0];
                if (Settings.platformVersion.equalsIgnoreCase("7.0")) {
                    memString = memString.replace(",", "").trim();
                    memString = memString.replace("K", "").trim();
                } else {
                    memString = memString.replace("kB", "").trim();
                }
                return Integer.parseInt(memString);
            } else {
                Log.error("\"dumpsys meminfo\" command failed!");
                return 0;
            }
        } else {
            Log.error("Get memory is implemented only for Android.");
            return 0;
        }
    }

    public static int getMem() {
        return getMem(Settings.deviceId);
    }
}
