package functional.tests.core.device.ios;

import functional.tests.core.utils.OSUtils;

/**
 * IOS device log.
 */
public class IOSDeviceLog {
    private String deviceId;
    private int lastByteIndex;

    public IOSDeviceLog(String deviceId) {
        this.deviceId = deviceId;
    }

    /**
     * Gets the last part of log, excluding everything from previous log.
     */
    public String getDeviceLogTail() {
        String entireLog = IOSDeviceLog.getDeviceLog(this.deviceId);
        if (this.lastByteIndex < 0 || this.lastByteIndex >= entireLog.length()) {
            this.lastByteIndex = 0;
        }
        String logTail = entireLog.substring(this.lastByteIndex, entireLog.length() - 1);
        this.lastByteIndex = entireLog.length() - 1;

        return logTail;
    }

    /**
     * Gets the whole device log.
     */
    public static String getDeviceLog(String deviceId) {
        String entireLog = OSUtils.runProcess(String.format("cat ~/Library/Logs/CoreSimulator/%s/system.log", deviceId));

        return entireLog;
    }

}
