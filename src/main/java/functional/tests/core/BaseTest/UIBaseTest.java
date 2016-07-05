package functional.tests.core.BaseTest;

import functional.tests.core.App.App;
import functional.tests.core.Appium.Client;
import functional.tests.core.Appium.Server;
import functional.tests.core.Device.Device;
import functional.tests.core.Device.iOS.Simctl;
import functional.tests.core.Enums.DeviceType;
import functional.tests.core.Log.Log;
import functional.tests.core.OSUtils.FileSystem;
import functional.tests.core.OSUtils.OSUtils;
import functional.tests.core.Settings.Settings;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import java.io.IOException;
import java.lang.reflect.Method;

public abstract class UIBaseTest extends BaseTest{

    private static boolean failAtStartUp = false;
    private static boolean isFistTest = true;
    private static Device staticDevice;
    private Device device;

    public UIBaseTest() {
    }

    public static Device baseDevice(){
        return staticDevice;
    }

    private static void checkAppiumLogsForCrash() {
        try {
            String appiumLog = FileSystem.readFile(Settings.appiumLogFile);
            String[] lines = appiumLog.split("\\r?\\n");
            for (String line : lines) {
                if (line.contains("IOS_SYSLOG_ROW") && line.contains("crashed.")) {
                    Log.fatal("App crashes at startup. Please see appium logs.");
                }
            }
        } catch (IOException e) {
            Log.info("Failed to check appium log files.");
        }
    }

    @BeforeSuite(alwaysRun = true)
    public void beforeSuite() throws Exception {
        this.device = new Device();
        staticDevice = this.device;

        if (!Settings.debug) {
            this.device.stopDevice();
            this.device.initDevice();
        }

        this.device.initTestApp();

        try {
            Server.initAppiumServer();
            Client.initAppiumDriver();
        } catch (Exception e) {
            checkAppiumLogsForCrash();
            takeScreenOfHost("HostOS_Failed_To_Init_Appium_Session");
            Log.info("Retry initializing appium server and client");
            Settings.appiumLogLevel = "debug";
            Settings.deviceBootTimeout = Settings.deviceBootTimeout * 2;
            try {
                try {
                    String log = Server.service.getStdOut();
                    if (log != null) {
                        Log.separator();
                        Log.info(log);
                        Log.separator();
                    } else {
                        Log.error("Server log not available!");
                    }
                } catch (Exception ex) {
                    Log.error("Failed to get appium logs.");
                }
                Client.stopAppiumDriver();
                Server.stopAppiumServer();
                Server.initAppiumServer();
                Client.initAppiumDriver();
            } catch (Exception re) {
                try {
                    takeScreenOfHost("HostOS_Failed_To_Init_Appium_Session_After_Retry");
                    String log = Server.service.getStdOut();
                    if (log != null) {
                        Log.separator();
                        Log.info(log);
                        Log.separator();
                    } else {
                        Log.error("Server log not available!");
                    }
                } catch (Exception ex) {
                    Log.error("Failed to get appium logs.");
                }
                checkAppiumLogsForCrash();
                String error = "Failed to init Appium session. Please see Appium logs.";
                Log.fatal(error);
                Log.info(re.toString());
                Log.info(re.getStackTrace().toString());
                throw re;
            }
        }

        // Verify app not crashed
        try {
            this.device.verifyAppRunning(Settings.deviceId, Settings.packageId);
        } catch (Exception e) {
            failAtStartUp = true;
            Log.logScreen("Emulator", Settings.packageId + " failed at startup.");
            takeScreenOfHost("HostOS");
            throw e;
        }

        // Get logs for initial app startup
        this.device.writeConsoleLogToFile("init");
    }

    @BeforeMethod(alwaysRun = true)
    public void beforeMethod(Method method) throws Exception {
        if (this.device == null){
            this.device = new Device();
        }

        if (previousTestStatus == ITestResult.FAILURE) {
            try {
                App.fullRestart();
            } catch (Exception e1) {
                Log.info("Failed to restart test app. Rests Apppium client/server.");
                Server.stopAppiumServer();
                this.device.stopTestApp();
                this.device.stopDevice();
                Server.initAppiumServer();
                Client.initAppiumDriver();
                isFistTest = true;
                // Verify app not crashed
                try {
                    this.device.verifyAppRunning(Settings.deviceId, Settings.packageId);
                } catch (Exception e2) {
                    Log.logScreen("Emulator", Settings.packageId + " failed at startup.");
                    takeScreenOfHost("HostOS");
                    throw e2;
                }
            }
        }

        if (isFistTest) {
            isFistTest = false;
        } else {
            if (Settings.restartApp) {
                if (Settings.deviceType == DeviceType.Simulator) {
                    Simctl.reinstallApp();
                    Client.stopAppiumDriver();
                    Client.initAppiumDriver();
                } else {
                    App.fullRestart();
                }
            }
        }
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod(ITestResult result) throws IOException {

        // Get test case name
        String testCase = result.getMethod().getMethodName();

        if(this.device == null){
            Log.error("The device is null");
        } else{
            this.device.writeConsoleLogToFile(testCase);
        }
    }

    @AfterSuite(alwaysRun = true)
    public void afterSuite() throws Exception {
        Client.stopAppiumDriver();

        if (!Settings.debug) {
            Server.stopAppiumServer();
            this.device.stopTestApp();
            this.device.stopDevice();
        }
    }
}