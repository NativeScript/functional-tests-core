package functional.tests.core.mobile.appium;

import functional.tests.core.enums.OSType;
import functional.tests.core.enums.PlatformType;
import functional.tests.core.exceptions.AppiumException;
import functional.tests.core.log.Log;
import functional.tests.core.log.LoggerBase;
import functional.tests.core.mobile.settings.MobileSettings;
import functional.tests.core.utils.FileSystem;
import functional.tests.core.utils.OSUtils;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.appium.java_client.service.local.flags.GeneralServerFlag;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;

/**
 * Appium Server.
 */
public class Server {
    private static final LoggerBase LOGGER_BASE = LoggerBase.getLogger("Server");
    public AppiumDriverLocalService service;
    private MobileSettings settings;

    /**
     * Init Appium Server object.
     *
     * @param settings MobileSettings.
     */
    public Server(MobileSettings settings) {
        this.settings = settings;
    }

    /**
     * Start Appium server.
     *
     * @throws IOException     When some IO operation fails.
     * @throws AppiumException When fail to start Appium server.
     */
    public void initServer() throws IOException, AppiumException {

        LOGGER_BASE.info("Init appium server ...");

        // On Windows, when you force stop test run in the middle of execution,
        // test log file is locked by node.exe, so ... kill it!
        // Notes: This does not allow parallel test execution on Windows !!!
        if (this.settings.os == OSType.Windows) {
            OSUtils.stopProcess("node.exe");
        }

        // Create Appium server log file
        File logFile = this.createLogFile();

        // Get Appium executable path
        File appiumExecutable = this.getAppiumExecutable();

        // Init AppiumServiceBuilder
        AppiumServiceBuilder serviceBuilder = new AppiumServiceBuilder()
                .withLogFile(logFile)
                .usingAnyFreePort()
                .withIPAddress("127.0.0.1")
                .withAppiumJS(appiumExecutable);

        // Set iOS specific Appium server arguments
        if (this.settings.platform == PlatformType.iOS) {
            serviceBuilder.withStartUpTimeOut(this.settings.deviceBootTimeout, TimeUnit.SECONDS);
        }

        // Set Appium log level
        if (this.settings.appiumLogLevel != null) {
            serviceBuilder.withArgument(GeneralServerFlag.LOG_LEVEL, this.settings.appiumLogLevel);
        }

        // Start Appium server
        this.service = AppiumDriverLocalService.buildService(serviceBuilder);
        LOGGER_BASE.info("Starting Appium server...");
        this.service.start();

        // Verify Appium server started
        if (this.service == null || !this.service.isRunning()) {
            Log.logScreenOfHost(this.settings, "Host_Fail_to_Init_Server");
            String error = "Appium server failed to start! Please check appium log file.";
            LOGGER_BASE.fatal(error);
            throw new RuntimeException(error);
        } else {
            LOGGER_BASE.info("Appium Server is up and running!");
        }
    }

    /**
     * Stop Appium server.
     */
    public void stopServer() {
        if (this.service != null) {
            try {
                this.service.stop();
                LOGGER_BASE.info("Appium server stopped.");
            } catch (Exception e) {
                LOGGER_BASE.fatal("Failed to stopSession appium server!");
            }
        } else {
            LOGGER_BASE.info("Appium server already stopped.");
        }
    }

    /**
     * Create log file for Appium server.
     *
     * @return File for Appium server logs.
     * @throws IOException When fail to create log file.
     */
    private File createLogFile() throws IOException {
        File logFile = new File(this.settings.appiumLogFile);
        Files.deleteIfExists(logFile.toPath());
        logFile.getParentFile().mkdirs();
        boolean createLogFileResult = logFile.createNewFile();

        if (createLogFileResult) {
            LOGGER_BASE.debug("Appium log file created.");
        } else {
            LOGGER_BASE.fatal("Failed to create Appium log file.");
        }
        return logFile;
    }

    /**
     * Get Appium executable file.
     *
     * @return Appium executable file.
     * @throws AppiumException When Appium executable not found.
     */
    private File getAppiumExecutable() throws AppiumException {

        // Find Appium path.
        String appiumPath;
        if (this.settings.os == OSType.Windows) {
            // TODO(dtopuzov): "where appium" is not good enough, we should try to be flexible.
            // I've heard someone to complain for NodeJS on Windows not installed in default path.
            appiumPath = System.getenv("APPDATA") + "\\npm\\node_modules\\appium\\build\\lib\\main.js";
        } else {
            // On different OSs, different nodejs managers might be in use,
            // therefore appium installation location may vary.
            appiumPath = OSUtils.runProcess("which appium").trim();
        }

        // Check if exists
        File appiumExecutable = null;
        try {
            LOGGER_BASE.info("CHECK APPIUM EXECUTABLE!!!");
            appiumExecutable = new File(appiumPath).toPath().toRealPath().toFile();
            LOGGER_BASE.info("APPIUM EXECUTABLE: " + appiumExecutable.toPath().toString());
        } catch (Exception e) {
            LOGGER_BASE.error(e.getMessage());
            e.printStackTrace();
        }

        if (!appiumExecutable.exists()) {
            String error = "Appium does not exist at: " + appiumPath;
            LOGGER_BASE.fatal(error);
            throw new AppiumException(error);
        } else {
            LOGGER_BASE.info("Appium Executable: " + appiumPath);
        }

        // Return Appium executable file.
        return appiumExecutable;
    }

    /**
     * Get Appium server logs.
     *
     * @return appium server log as string.
     */
    public String getServerLogs() {
        try {
            return FileSystem.readFile(this.settings.appiumLogFile);
        } catch (IOException e) {
            LOGGER_BASE.error("Failed to get Appium Server logs form " + this.settings.appiumLogFile);
            return "";
        }
    }
}
