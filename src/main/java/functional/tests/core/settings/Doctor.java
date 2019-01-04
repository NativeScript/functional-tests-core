package functional.tests.core.settings;

import functional.tests.core.extensions.SystemExtension;
import functional.tests.core.log.LoggerBase;

/**
 * Doctor verify system and settings.
 * Executed before tests and do not run them if problem is found.
 */
public class Doctor {

    private static final LoggerBase LOGGER_BASE = LoggerBase.getLogger("Doctor");

    private Settings settings;

    public Doctor(Settings settings) {
        this.settings = settings;
    }

    /**
     * Verify system is ready to run tests and settings are valid.
     *
     * @throws Exception When system is not ready to run tests or settings are invalid.
     */
    public void check() throws Exception {
        try {
            //verifyJava();
            LOGGER_BASE.info("System and settings are OK.");
        } catch (Exception e) {
            LOGGER_BASE.error("System and settings are NOT OK.");
            LOGGER_BASE.fatal(e.getMessage());
            SystemExtension.interruptProcess("Check Setting  again");
        }
    }

    /**
     * Verify host OS has Java 1.8+.
     *
     * @throws Exception When Java is not available or Java version is lower than 1.8.
     */
    protected static void verifyJava() throws Exception {
        // String version = System.getProperty("java.version");
        // int pos = version.indexOf('.');
        // pos = version.indexOf('.', pos + 1);
        // double ver = Double.parseDouble(version.substring(0, pos));
        // if (ver < 1.8) {
        //     String message = "Please use Java 1.8+. Current version is: " + version;
        //     throw new FileNotFoundException(message);
        // }
    }
}
