package functional.tests.core.Perf;

import functional.tests.core.Device.Android.Adb;
import functional.tests.core.Enums.PlatformType;
import functional.tests.core.Log.Log;
import functional.tests.core.Settings.Settings;

public class PerfInfo {

    public static long getMem() {
        if (Settings.platform == PlatformType.Andorid) {
            String command = "shell dumpsys meminfo  | grep " + Settings.packageId;
            String output = Adb.runAdbCommand(Settings.deviceId, command);

            if (output.contains(Settings.packageId)) {
                String memString = output.split(":")[0];
                memString = memString.replace("kB", "").trim();
                return Long.parseLong(memString);
            } else {
                return 0;
            }
        } else {
            Log.error("Get memory is implemented only for Android.");
            return 0;
        }
    }
}
