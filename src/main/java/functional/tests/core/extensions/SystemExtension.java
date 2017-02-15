package functional.tests.core.extensions;

import functional.tests.core.log.LoggerBase;

/**
 *
 */
public class SystemExtension {
    private static final LoggerBase LOGGER_BASE = LoggerBase.getLogger("System");

    /**
     * @param msg
     */
    public static void interruptProcess(String msg) {
        SystemExtension.LOGGER_BASE.fatal("The process is interrupted: " + msg);
        System.exit(-1);
    }
}
