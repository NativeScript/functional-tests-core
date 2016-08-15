package functional.tests.core.Perf;

import functional.tests.core.Enums.PlatformType;
import functional.tests.core.Log.Log;
import functional.tests.core.OSUtils.OSUtils;
import functional.tests.core.Settings.Settings;

public class PerfInfo {

    public static long getMem() {
        if (Settings.platform == PlatformType.Andorid) {
            String command = "adb -s " + Settings.deviceId + " shell dumpsys meminfo  | grep " + Settings.packageId;
            String output = OSUtils.runProcess(command);
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
