package functional.tests.core.Appium;

import functional.tests.core.Enums.OSType;
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

public class Server {

    public static AppiumDriverLocalService service;

    public static void initAppiumServer() throws IOException, AppiumException {
        Log.info("Init Appium server...");

        // On Windows sometimes (when you force stop test run in the middle of execution)
        // test log file is locked by node.exe, kill it!
        if (Settings.OS == OSType.Windows) {
            OSUtils.stopProcess("node.exe");
        }

        File logFile = new File(Settings.appiumLogFile);
        Files.deleteIfExists(logFile.toPath());
        boolean mkdirResult = logFile.getParentFile().mkdirs();
        boolean createLogFileResult = logFile.createNewFile();

        if (mkdirResult && createLogFileResult) {
            Log.debug("Appium log file created.");
        } else {
            Log.fatal("Failed to create appium log file.");
        }


        // Appium Version Manager is not available on Windows, so tests will use the global installation
        AppiumServiceBuilder serviceBuilder = new AppiumServiceBuilder()
                .withLogFile(logFile)
                .usingAnyFreePort()
                .withArgument(GeneralServerFlag.AUTOMATION_NAME, Settings.automationName)
                .withArgument(GeneralServerFlag.COMMAND_TIMEOUT, String.valueOf(Settings.deviceBootTimeout));

        // On Linux and OSX use appium version manager
        if ((Settings.OS == OSType.Linux) || (Settings.OS == OSType.MacOS)) {
            // TODO: Use "avm bin <version>" to get path of appium executable
            String appiumPath = "/usr/local/avm/versions/" +
                    Settings.appiumVersion +
                    "/node_modules/appium/bin/appium.js";
            File appiumExecutable = new File(appiumPath);
            if (logFile.exists()) {
                serviceBuilder.withAppiumJS(appiumExecutable);
            } else {
                String error = "Failed to find appium " + Settings.appiumVersion + " at " + appiumPath;
                Log.fatal(error);
                throw new AppiumException(error);
            }
        }

        Log.info(serviceBuilder.toString());
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
                Log.fatal("Failed to stop Appium server.");
            }
        } else {
            Log.info("Appium server already stopped.");
        }
    }
}
