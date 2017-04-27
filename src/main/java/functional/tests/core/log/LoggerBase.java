package functional.tests.core.log;

import functional.tests.core.mobile.basetest.MobileSetupManager;
import functional.tests.core.utils.OSUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.testng.Reporter;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**
 * TODO(): Add docs.
 */
public class LoggerBase {
    private static final String LOG_LEVEL = "INFO";

    private Logger logger;

    /**
     * TODO(): Add docs.
     *
     * @param className
     */
    private LoggerBase(String className) {
        this.logger = Logger.getLogger(className);
    }

    /**
     * TODO(): Add docs.
     *
     * @param className
     * @return
     */
    public static LoggerBase getLogger(String className) {
        return new LoggerBase(className);
    }

    /**
     * TODO(): Add docs.
     */
    public static void initLog4j() {
        String userDir = System.getProperty("user.dir");
        String log4jConfig = userDir + File.separator + "resources" + File.separator + "log" + File.separator + "log4j.properties";

        File logFile = new File(log4jConfig);

        if (!logFile.exists()) {
            Properties props = new Properties();
            try {
                props.load(MobileSetupManager.class.getClass().getResourceAsStream("/log/log4j.properties"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            PropertyConfigurator.configure(props);
        } else {
            PropertyConfigurator.configure(log4jConfig);
        }
        String logLevel;
        LoggerBase log = getLogger("LOG");
        try {
            logLevel = log.getLevel().toString();
        } catch (Exception ex) {
            log.info(ex.getMessage());
        }

        Reporter.setEscapeHtml(false);
        log.info("Log4j initialized.");
    }

    /**
     * TODO(): Add docs.
     *
     * @param msg
     * @param level
     * @return
     */
    public static String formatLoggerMessage(String msg, String level) {
        return String.format("%s %s - %s", OSUtils.getTimestamp(), level, msg);
    }

    /**
     * TODO(): Add docs.
     *
     * @return
     */
    public Level getLevel() {
        return this.logger.getLevel();
    }

    /**
     * TODO(): Add docs.
     *
     * @param msg
     */
    public void debug(String msg) {
        this.logger.debug(msg);
        if ((LoggerBase.LOG_LEVEL.equalsIgnoreCase("TRACE")) || (LoggerBase.LOG_LEVEL.equalsIgnoreCase("DEBUG"))) {
            Reporter.log(formatLoggerMessage(msg, "DEBUG"));
        }
    }

    /**
     * TODO(): Add docs.
     *
     * @param msg
     */
    public void info(String msg) {
        this.logger.info(msg);
        Reporter.log(formatLoggerMessage(msg, "INFO"));
    }

    /**
     * TODO(): Add docs.
     *
     * @param msg
     */
    public void warn(String msg) {
        this.logger.warn(msg);
        Reporter.log(formatLoggerMessage(msg, "WARN"));
    }

    /**
     * TODO(): Add docs.
     *
     * @param msg
     */
    public void error(String msg) {
        this.logger.error(msg);
        Reporter.log(formatLoggerMessage(msg, "ERROR"));
    }

    /**
     * TODO(): Add docs.
     *
     * @param msg
     */
    public void fatal(String msg) {
        this.logger.fatal(msg);
        Reporter.log(formatLoggerMessage(msg, "FATAL"));
    }

    /**
     * TODO(): Add docs.
     */
    public void separator() {
        String msg = "=============================================";
        this.info(msg);
    }

    /**
     * TODO(): Add docs.
     */
    public void separatorAndroid() {
        String msg = "================== Android ==================";
        this.info(msg);
    }

    /**
     * TODO(): Add docs.
     */
    public void separatorIOS() {
        String msg = "==================== iOS ====================";
        this.info(msg);
    }

    /**
     * TODO(): Add docs.
     *
     * @param msg
     */
    public void trace(String msg) {
        this.debug(msg);
        if (LoggerBase.LOG_LEVEL.equalsIgnoreCase("TRACE")) {
            Reporter.log(LoggerBase.formatLoggerMessage(msg, "TRACE"));
        }
    }
}
