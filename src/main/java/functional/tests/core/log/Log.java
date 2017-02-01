package functional.tests.core.log;

import functional.tests.core.appium.Client;
import functional.tests.core.basetest.Context;
import functional.tests.core.exceptions.AppiumException;
import functional.tests.core.image.ImageUtils;
import functional.tests.core.image.ImageVerificationResult;
import functional.tests.core.settings.Settings;
import functional.tests.core.utils.FileSystem;
import org.testng.Reporter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import static functional.tests.core.utils.FileSystem.readFile;

/**
 * TODO(): Add docs.
 */
public class Log {

    private static final LoggerBase LOGGER_BASE = LoggerBase.getLogger("Log");
    private static int thumbHeight = 80;
    private static int thumbWidth = 60;
    private static String templatePath = System.getProperty("user.dir") + File.separator + "resources" + File.separator + "templates";
    private Client client;
    private ImageUtils imageUtils;
    private Settings settings;

    /**
     * TODO(): Add docs.
     */
    public Log() {
    }

    /**
     * TODO(): Add docs.
     *
     * @param context
     */
    public Log(Context context) {
        this.settings = context.settings;
        this.client = context.client;
        this.imageUtils = context.imageUtils;
    }

    /**
     * Log image template.
     *
     * @param msg String content of the template
     */
    public static void template(String msg) {
        Reporter.log(LoggerBase.formatLoggerMessage(msg, "IMAGE"));
    }

    /**
     * Log a trace message.
     *
     * @param msg Message to log.
     */
    public void trace(String msg) {
        LOGGER_BASE.trace(msg);
    }

    /**
     * Log a debug message.
     *
     * @param msg Message to log.
     */
    public void debug(String msg) {
        LOGGER_BASE.debug(msg);
    }

    /**
     * Log an info message.
     *
     * @param msg Message to log.
     */
    public void info(String msg) {
        LOGGER_BASE.info(msg);
    }

    /**
     * Log a warn message.
     *
     * @param msg Message to log.
     */
    public void warn(String msg) {
        LOGGER_BASE.warn(msg);
    }

    /**
     * Log an error message.
     *
     * @param msg Message to log.
     */
    public void error(String msg) {
        LOGGER_BASE.error(msg);
    }

    /**
     * Log a fatal message.
     *
     * @param msg Message to log.
     */
    public void fatal(String msg) {
        LOGGER_BASE.fatal(msg);
    }

    /**
     * Separator for log readability.
     */
    public void separator() {
        LOGGER_BASE.separator();
    }

    /**
     * Save current visual tree in file.
     *
     * @param fileName File where visual tree will be saved (file will be written in baseLogDir).
     */
    public void saveXmlTree(String fileName) {
        try {
            String tree = this.client.driver.getPageSource();
            PrintWriter out = new PrintWriter(this.settings.baseLogDir + File.separator + fileName);
            out.println(tree);
            out.close();
        } catch (Exception e) {
            this.error("Failed to get and save current visual tree.");
        }
    }

    /**
     * TODO(): Add docs.
     *
     * @param fileName
     * @param text
     */
    public void logString(String fileName, String text) {
        try {
            String filePath = this.settings.baseLogDir + File.separator + fileName;
            FileSystem.writeFile(filePath, text);
        } catch (Exception e) {
            LOGGER_BASE.error("Failed to log data in " + fileName);
        }
    }

    /**
     * Log template of the current screen.
     *
     * @param imageName   Name of the template. For example: test_01_smoke.
     * @param title       Title of the template in the report template.
     * @param thumbHeight Height of the template in the report template.
     * @param thumbWidth  Width of the template in the report template.
     */
    public void logScreen(String imageName, String title, int thumbHeight, int thumbWidth) {
        try {
            String fullFileName = this.settings.screenshotOutDir + File.separator + imageName;

            if (this.imageUtils != null) {
                this.imageUtils.saveScreen(fullFileName);
            } else {
                this.error("ImageUtils is null.");
            }
        } catch (AppiumException e) {
            this.error("Failed to get screenshot.");
        } catch (IOException e) {
            this.error("Failed to save current screenshot to file with name " + imageName);
        }

        String logTemplatePath = templatePath + File.separator + "screenshot.template";
        File logTemplateFile = new File(logTemplatePath);
        String logMessage;

        try {
            if (!logTemplateFile.exists()) {
                logMessage = this.readResource("/templates/screenshot.template");
            } else {
                logMessage = readFile(logTemplatePath);
            }

            logMessage = logMessage
                    .replace("IMAGE_TITLE", title)
                    .replace("IMAGE_URL", "../screenshots/" + imageName + ".png")
                    .replace("THUMB_WIDTH", String.valueOf(thumbWidth))
                    .replace("THUMB_HEIGHT", String.valueOf(thumbHeight));

            template(logMessage);

        } catch (Exception e) {
            this.error("Failed to generate log template string.");
        }
    }

    /**
     * Log template of the current screen.
     *
     * @param imageName Name of the template and the title in the report template.
     *                  For example: test_01_smoke.
     */
    public void logScreen(String imageName) {
        this.logScreen(imageName, imageName, thumbHeight, thumbWidth);
    }

    /**
     * Log template of the current screen.
     *
     * @param imageName Name of the template. For example: test_01_smoke.
     * @param title     Title of the template in the report template.
     */
    public void logScreen(String imageName, String title) {
        this.logScreen(imageName, title, thumbHeight, thumbWidth);
    }

    /**
     * Log the result of template verification.
     *
     * @param result      Result of template verification.
     * @param imageName   Name of the template. For example: test_01_smoke.
     * @param thumbHeight Height of the template in the report template.
     * @param thumbWidth  Weight of the template in the report template.
     */
    public void logImageVerificationResult(ImageVerificationResult result, String imageName, int thumbHeight, int thumbWidth) {
        try {
            this.imageUtils.saveImageVerificationResult(result, imageName);
            String message = String.format("%s does NOT look OK. Diff percents: %.2f%%. Waiting ...", imageName, result.diffPercent);
            LOGGER_BASE.info(message);

            String logTemplatePath = templatePath + File.separator + "imageVerification.template";
            File logTemplateFile = new File(logTemplatePath);
            String logMessage;

            if (!logTemplateFile.exists()) {
                logMessage = this.readResource("/templates/imageVerification.template");
            } else {
                logMessage = readFile(logTemplatePath);
            }

            logMessage = logMessage
                    .replace("IMAGE_NAME", imageName)
                    .replace("ACTUAL_IMAGE_URL", "../screenshots/" + imageName + result.actualSuffix + ".png")
                    .replace("DIFF_IMAGE_URL", "../screenshots/" + imageName + result.diffSuffix + ".png")
                    .replace("EXPECTED_IMAGE_URL", "../screenshots/" + imageName + result.expectedSuffix + ".png")
                    .replace("THUMB_WIDTH", String.valueOf(thumbWidth))
                    .replace("THUMB_HEIGHT", String.valueOf(thumbHeight));

            this.template(logMessage);
        } catch (Exception e) {
            this.error("Failed to log current screen.");
        }
    }

    /**
     * Log the result of template verification.
     *
     * @param result    Result of template verification.
     * @param imageName Name of the template. For example: test_01_smoke.
     */
    public void logImageVerificationResult(ImageVerificationResult result, String imageName) {
        this.logImageVerificationResult(result, imageName, thumbHeight, thumbWidth);
    }

    /**
     * Read a text file resource.
     *
     * @param resourcePath Path to the resource.
     * @return String content of the resource.
     * @throws IOException
     */
    public String readResource(String resourcePath) throws IOException {
        InputStream resourceStream = this.getClass().getResourceAsStream(resourcePath);
        StringBuilder builder = new StringBuilder();
        int ch;
        while ((ch = resourceStream.read()) != -1) {
            builder.append((char) ch);
        }
        return builder.toString();
    }
}
