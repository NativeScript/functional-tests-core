package functional.tests.core.Perf;

import functional.tests.core.Enums.PlatformType;
import functional.tests.core.Log.Log;
import functional.tests.core.OSUtils.OSUtils;
import functional.tests.core.Settings.Settings;

import java.io.File;

public class Mem {

    public static void saveRowMemInfo(String fileName) {
        if (Settings.platform == PlatformType.Andorid) {
            String command = "adb -s " + Settings.deviceId +
                    " shell dumpsys meminfo > " +
                    Settings.baseLogDir + File.separator + fileName;
            OSUtils.runProcess(true, command);
            Log.info("Meminfo saved in " + fileName);
        } else if (Settings.platform == PlatformType.iOS) {
            // TODO: Implement it
        }
    }
}
