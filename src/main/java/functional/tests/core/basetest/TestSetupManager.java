package functional.tests.core.basetest;

import functional.tests.core.enums.DeviceType;
import functional.tests.core.exceptions.DeviceException;
import functional.tests.core.exceptions.MobileAppException;
import functional.tests.core.log.LoggerBase;
import functional.tests.core.utils.FileSystem;
import functional.tests.core.utils.OSUtils;
import org.testng.ITestResult;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * TODO(svetli): Add docs.
 */
public class TestSetupManager {

    private static final LoggerBase LOGGER_BASE = LoggerBase.getLogger("TestSetupManager");
    private final Context context;

    /**
     * TODO(svetli): Add docs.
     *
     * @param context
     */
    public TestSetupManager(Context context) {
        this.context = context;
    }

    /**
     * Init Appium server wrapper.
     *
     * @return True if server is started successfully.
     */
    public boolean initServer() {
        boolean hasInit = true;
        try {
            this.context.server.initServer();
        } catch (Exception ex) {
            this.takeScreenOfHost("Host_Fail_to_Init_Server");
            hasInit = false;
        }

        return hasInit;
    }

    /**
     * Restart Appium server.
     */
    public void restartServer() {
        // TODO(dtopuzov): It looks this need improvements.
        this.checkLogsForCrash();
        this.takeScreenOfHost("HostOS_Failed_To_Init_Appium_Session");
        LOGGER_BASE.info("Retry initializing appium server and client");
        this.context.settings.appiumLogLevel = "debug";
        this.context.settings.deviceBootTimeout = this.context.settings.deviceBootTimeout * 2;
        try {
            try {
                String log = this.context.server.service.getStdOut();
                if (log != null) {
                    this.context.log.separator();
                    this.context.log.info(log);
                    this.context.log.separator();
                } else {
                    LOGGER_BASE.error("Server log not available!");
                }
            } catch (Exception ex) {
                LOGGER_BASE.error("Failed to get appium logs.");
            }
            this.initServer();
        } catch (Exception re) {
            try {
                this.takeScreenOfHost("HostOS_Failed_To_Init_Appium_Session_After_Retry");
                String log = this.context.server.service.getStdOut();
                if (log != null) {
                    LOGGER_BASE.separator();
                    LOGGER_BASE.info(log);
                    LOGGER_BASE.separator();
                } else {
                    LOGGER_BASE.error("Server log not available!");
                }
            } catch (Exception ex) {
                LOGGER_BASE.error("Failed to get appium logs.");
            }
            this.checkLogsForCrash();
            String error = "Failed to init appium session. Please see appium logs.";
            LOGGER_BASE.fatal(error);
            LOGGER_BASE.info(re.toString());
            LOGGER_BASE.info(re.getStackTrace().toString());
            try {
                throw re;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Stop app under test, appium client and server.
     * If reuseDevice is false, stop emulator/simulator.
     */
    public void fullStop() {
        this.context.app.close();
        this.context.client.stopDriver();
        this.context.server.stopServer();
        if (!this.context.settings.reuseDevice) {
            this.stopDevice();
        }
    }

    /**
     * Restart Appium server and client (only if Appium session is not available).
     */
    public void restartSession() {
        // TODO(dtopuzov): This need refactoring. Do not stop server if it is running.
        this.fullStop(); // For sure we don't need full stop here!
        this.restartServer();
        LOGGER_BASE.info("Failed to restart test app. Resets apppium client/server.");

        try {
            this.startDevice();
        } catch (Exception e2) {
            this.context.log.logScreen("Emulator", this.context.settings.packageId + " failed at startup.");
            this.takeScreenOfHost("HostOS");
        }
    }

    /**
     * TODO(): Add docs.
     *
     * @throws MobileAppException
     * @throws DeviceException
     * @throws TimeoutException
     */
    public void startDevice() throws MobileAppException, DeviceException, TimeoutException {
        try {
            this.context.device.start();
        } catch (Exception ex) {
            this.takeScreenOfHost("onStartDevice");
        }
    }

    /**
     * TODO(): Add docs.
     */
    public void stopDevice() {
        this.context.device.stop();
    }

    /**
     * TODO(): Add docs.
     *
     * @param previousTestStatus
     * @param testCase
     */
    public void logTestResult(int previousTestStatus, String testCase) {
        if (this.context.device == null) {
            LOGGER_BASE.error("The device is null");
        } else {
            try {
                this.context.device.writeConsoleLogToFile(testCase);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (previousTestStatus == ITestResult.SUCCESS) {
            if (this.context.settings.takeScreenShotAfterTest) {
                this.context.log.logScreen(testCase + "_pass", "Screenshot after " + testCase);
            }
        } else if (previousTestStatus == ITestResult.FAILURE) {
            this.context.log.logScreen(testCase + "_fail", "Screenshot after " + testCase);
            this.context.log.saveXmlTree(testCase + "_VisualTree.xml");
        }
    }

    /**
     * TODO(): Add docs.
     *
     * @param fileName
     */
    protected void takeScreenOfHost(String fileName) {
        if ((this.context.settings.deviceType == DeviceType.Simulator) || (this.context.settings.deviceType == DeviceType.Emulator)) {
            OSUtils.getScreenshot("HostOS_" + fileName, this.context.settings);
        }
    }

    /**
     * TODO(): Add docs.
     */
    private void checkLogsForCrash() {
        try {
            String appiumLog = FileSystem.readFile(this.context.settings.appiumLogFile);
            String[] lines = appiumLog.split("\\r?\\n");
            for (String line : lines) {
                if (line.contains("IOS_SYSLOG_ROW") && line.contains("crashed.")) {
                    LOGGER_BASE.fatal("app crashes at startup. Please see appium logs.");
                }
            }
        } catch (IOException e) {
            LOGGER_BASE.info("Failed to check appium log files.");
        }
    }
}
