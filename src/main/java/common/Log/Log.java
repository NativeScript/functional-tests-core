package common.Log;

import common.Appium.Client;
import common.Exceptions.AppiumException;
import common.Screenshot.ImageVerification;
import common.Screenshot.ImageVerificationResult;
import common.Settings.Settings;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.testng.Reporter;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

public class Log {

    // Initialize log4j logs
    private static Logger Log = Logger.getLogger(Log.class.getName());

    private static String formatLogMessage(String msg, String level) {
        java.util.Date date = new java.util.Date();
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss.SSS");
        String timestamp = sdf.format(date);
        return String.format("%s %s - %s", timestamp, level, msg);
    }

    private static String readFromTemplate(String templateName) throws IOException {
        String path = templateName;
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, Charset.defaultCharset());
    }

    public static void info(String msg) {
        Log.info(msg);
        Reporter.log(formatLogMessage(msg, "INFO"));
    }

    public static void warn(String msg) {
        Log.warn(msg);
        Reporter.log(formatLogMessage(msg, "WARN"));
    }

    public static void error(String msg) {
        Log.error(msg);
        Reporter.log(formatLogMessage(msg, "ERROR"));
    }

    public static void fatal(String msg) {
        Log.fatal(msg);
        Reporter.log(formatLogMessage(msg, "FATAL"));
    }

    public static void debug(String msg) {
        Log.debug(msg);
        Reporter.log(formatLogMessage(msg, "DEBUG"));
    }

    public static void image(String msg) {
        Reporter.log(formatLogMessage(msg, "IMAGE"));
    }

    public static void initLogging() throws IOException {
        String userDir = System.getProperty("user.dir");
        String log4jConfig = userDir + File.separator + "resources" + File.separator + "log" + File.separator + "log4j.properties";
        PropertyConfigurator.configure(log4jConfig);
        Reporter.setEscapeHtml(false);
        info("Logger initialized.");
    }

    /**
     * Save current visual tree in file *
     */
    public static void saveXmlTree(String fileName) {
        try {
            String tree = Client.driver.getPageSource();
            PrintWriter out = new PrintWriter(Settings.baseLogDir + File.separator + fileName);
            out.println(tree);
            out.close();
        } catch (Exception e) {
            error("Failed to get and save current visual tree.");
        }
    }

    /**
     * Log current screen *
     */
    public static void logScreen(String fileName, String title) throws AppiumException {
        try {
            ImageVerification.saveScreen(fileName);

            String logTemplate = readFromTemplate(Settings.templatePath + File.separator + "screenshot.template");
            String logMessage = logTemplate
                    .replace("IMAGE_TITLE", title)
                    .replace("IMAGE_URL", "../screenshots/" + fileName + ".png");
            image(logMessage);
        } catch (Exception e) {
            error("Failed to log current screen.");
            throw new AppiumException("Failed to log current screen. Error: " + e);
        }
    }

    /**
     * Log image verification result *
     */
    public static void logImageVerificationResult(ImageVerificationResult result, String filePrefix) throws AppiumException {
        try {
            ImageVerification.saveImageVerificationResult(result, filePrefix);

            String imageTitle = String.format("%s looks OK", filePrefix);
            if (result.diffPixels > 100){ // TODO: Read it from global config
                String diffPercentString = new DecimalFormat("##.##").format(result.diffPercent);
                imageTitle = String.format("%s does not look OK. Diff: %s %", filePrefix, diffPercentString);
            }

            String logTemplate = readFromTemplate(Settings.templatePath + File.separator + "imageVerification.template");
            String logMessage = logTemplate
                    .replace("IMAGE_TITLE", imageTitle)
                    .replace("EXPECTED_IMAGE_URL", "../screenshots/" + filePrefix + String.format("_%s.png", result.actualSuffix))
                    .replace("DIFF_IMAGE_URL", "../screenshots/" + filePrefix + String.format("_%s.png", result.diffSuffix))
                    .replace("ACTUAL_IMAGE_URL", "../screenshots/" + filePrefix + String.format("_%s.png", result.expectedSuffix));
            image(logMessage);
        } catch (Exception e) {
            error("Failed to log current screen.");
            throw new AppiumException("Failed to log image verification result. Error: " + e);
        }
    }
}