package functional.tests.core.mobile.device.ios;

import functional.tests.core.mobile.settings.MobileSettings;
import functional.tests.core.utils.OSUtils;

import java.io.File;

/**
 * IOS device log.
 */
public class IOSDeviceLog {
    private int lastByteIndex;
    private MobileSettings settings;

    public static final String IOS_REAL_DEVICE_LOG_FILE = "iOS_log.txt";

    public IOSDeviceLog(MobileSettings settings) {
        this.settings = settings;
    }

    /**
     * Gets the last part of log, excluding everything from previous log.
     */
    public String getDeviceLogTail() {
        String entireLog = "";
        if (this.settings.isRealDevice || this.settings.platformVersion >= 10) {
        this.lastByteIndex = -1;
        entireLog = IOSDeviceLog.getDeviceLog(this.settings.consoleLogDir + File.separator + IOSDeviceLog.IOS_REAL_DEVICE_LOG_FILE);
        } else {
            entireLog = IOSDeviceLog.getSimulatorLog(this.settings.deviceId);
        }

        if (this.lastByteIndex < 0 || this.lastByteIndex >= entireLog.length()) {
            this.lastByteIndex = 0;
        }

        String logTail = "";
        if (!entireLog.isEmpty()) {
            logTail = entireLog.substring(this.lastByteIndex, entireLog.length() - 1);
        }
        this.lastByteIndex = entireLog.length() - 1;

        return logTail;
    }

    /**
     * Gets the whole device log.
     */
    public static String getSimulatorLog(String deviceId) {
        String entireLog = OSUtils.runProcess(String.format("cat ~/Library/Logs/CoreSimulator/%s/system.log", deviceId));

        return entireLog;
    }

    /**
     * Gets the whole device log.
     */
    public static String getDeviceLog(String fileFullName) {
        String entireLog = OSUtils.runProcess(String.format("cat %s", fileFullName));

        return entireLog;
    }
}
