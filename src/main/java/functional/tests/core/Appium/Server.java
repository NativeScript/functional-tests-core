package functional.tests.core.Appium;

import functional.tests.core.Enums.OSType;
import functional.tests.core.Enums.PlatformType;
import functional.tests.core.Exceptions.AppiumException;
import functional.tests.core.Log.Log;
import functional.tests.core.OSUtils.OSUtils;
import functional.tests.core.Settings.Settings;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.appium.java_client.service.local.flags.GeneralServerFlag;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class Server {

    public static AppiumDriverLocalService service;

    public static void initAppiumServer() throws IOException, AppiumException {

        Log.info("Init Appium server ...");

        // On Windows, when you force stop test run in the middle of execution,
        // test log file is locked by node.exe, so ... kill it!
        if (Settings.OS == OSType.Windows) {
            OSUtils.stopProcess("node.exe");
        }

        // Create log file
        File logFile = createAppiumLogFile();

        // Appium Version Manager is not available on Windows, so tests will use the global installation
        AppiumServiceBuilder serviceBuilder = new AppiumServiceBuilder()
                .withLogFile(logFile)
                .usingAnyFreePort();

        // On OSX use appium version manager
        if (Settings.OS == OSType.MacOS) {
            // Get appium path via appium-version-manager
            String appiumPath = OSUtils.runProcess("avm bin " + Settings.appiumVersion);
            // If appium is not installed try to install it
            if (appiumPath.contains("not installed")) {
                Log.info("Appium " + Settings.appiumVersion + " not found. Installing it ...");
                String installAppium = OSUtils.runProcess("avm " + Settings.appiumVersion);
                if (installAppium.contains("appium " + Settings.appiumVersion + " install failed")) {
                    String error = "Failed to install appium. Error: " + installAppium;
                    Log.fatal(error);
                    throw new AppiumException(error);
                } else if (installAppium.contains("installed" + Settings.appiumVersion)) {
                    Log.info("Appium " + Settings.appiumVersion + " installed.");
                }
                appiumPath = OSUtils.runProcess("avm bin " + Settings.appiumVersion);
            }

            String[] appiumPathLines = appiumPath.split("\\r?\\n");
            Arrays.asList(appiumPathLines);
            for (String line : appiumPathLines) {
                if (line.contains("avm")) {
                    appiumPath = line;
                }
            }

            File appiumExecutable = new File(appiumPath);
            if (!appiumExecutable.exists()) {
                String error = "Appium does not exist at: " + appiumPath;
                Log.fatal(error);
                throw new AppiumException(error);
            } else {
                Log.info("Appium Executable: " + appiumPath);
                serviceBuilder.withAppiumJS(appiumExecutable);
            }
        }


        // Set iOS specific Appium Server arguments
        if (Settings.platform == PlatformType.iOS) {
            serviceBuilder.withStartUpTimeOut(Settings.deviceBootTimeout, TimeUnit.SECONDS);
        }

        // Set log level
        if (Settings.appiumLogLevel != null) {
            // TODO: Extract in a class if additional flags are needed.
            serviceBuilder.withArgument(GeneralServerFlag.LOG_LEVEL, Settings.appiumLogLevel);
        }

        service = AppiumDriverLocalService.buildService(serviceBuilder);
        service.start();
        Log.info("Appium server started.");
    }

    public static void stopAppiumServer() {
        if (service != null) {
            try {
                service.stop();
                Log.info("Appium server stopped.");
            } catch (Exception e) {
                Log.fatal("Failed to stop Appium server!");
            }
        } else {
            Log.info("Appium server already stopped.");
        }
    }

    private static File createAppiumLogFile() throws IOException {
        File logFile = new File(Settings.appiumLogFile);
        Files.deleteIfExists(logFile.toPath());
        logFile.getParentFile().mkdirs();
        boolean createLogFileResult = logFile.createNewFile();

        if (createLogFileResult) {
            Log.debug("Appium log file created.");
        } else {
            Log.fatal("Failed to create appium log file.");
        }
        return logFile;
    }
}
