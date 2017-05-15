package functional.tests.core.utils;

import functional.tests.core.enums.OSType;
import functional.tests.core.log.LoggerBase;
import functional.tests.core.settings.Settings;

import java.io.File;

/**
 * Android Asset Packaging Tool.
 */
public class Aapt {

    private static final LoggerBase LOGGER_BASE = LoggerBase.getLogger("Aapt");
    private Settings settings;
    public String aaptPath;

    /**
     * Initialize aapt object.
     *
     * @param settings Current settings.
     */
    public Aapt(Settings settings) {
        this.aaptPath = this.getAaptPath();
        this.settings = settings;
    }

    /**
     * Locate aapt on current host machine.
     *
     * @return aapt path.
     */
    private String getAaptPath() {
        String aaptPath;

        String aaptExecutableName = "aapt";
        if (this.settings.os == OSType.Windows) {
            aaptExecutableName += ".exe";
        }

        File androidHome = new File(System.getenv("ANDROID_HOME") + File.separator + "build-tools");
        File aaptExecutablePath = OSUtils.find(androidHome, aaptExecutableName);
        if (aaptExecutablePath == null) {
            aaptPath = null;
        } else {
            aaptPath = aaptExecutablePath.getAbsolutePath();
        }

        return aaptPath;
    }

    /**
     * Execute aapt dump badging against apk file and filter by expression.
     *
     * @param grep String that is used to filter aapt dump badging output.
     * @return aapt dump badging filtered output.
     */
    private String runAaptCommand(String grep) {
        String value;
        String command = this.aaptPath +
                " dump badging " + this.settings.BASE_TEST_APP_DIR +
                File.separator + this.settings.testAppFileName + " | grep " + grep;
        String result = OSUtils.runProcess(command);

        // Parse result
        if (result.contains(grep)) {
            value = result.substring(result.indexOf("'") + 1);
            value = value.substring(0, value.indexOf("'"));
        } else {
            value = null;
        }

        return value;
    }

    /**
     * Get packageId from apk file of app under test.
     *
     * @return PacakgeId.
     */
    public String getPackage() {
        return this.runAaptCommand("package:");
    }

    /**
     * Get default activity from apk file of app under test.
     *
     * @return Default activity.
     */
    public String getLaunchableActivity(Settings settings) {
        return this.runAaptCommand("activity:");
    }

    /**
     * Get name of app under test (what you see bellow the launch icon).
     *
     * @return Application name.
     */
    public String getApplicationLabel(Settings settings) {
        String label = this.runAaptCommand("label:");

        // TODO(): Replace this hack with better fix (will require re-work for adb class).
        // Hack for build tools 24+
        // If build tools 24+ are available then label: will fail.
        // Note: If both build-tools 23 and 24+ are available we will use aapt from 23.
        if (label == null) {
            label = this.runAaptCommand("application:");
        }

        return label;
    }
}
