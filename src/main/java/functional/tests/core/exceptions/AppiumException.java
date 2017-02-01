package functional.tests.core.exceptions;

/**
 * Appium Server or Client exception.
 */
public class AppiumException extends Exception {

    /**
     * Appium Server or Client exception.
     *
     * @param message Exception message.
     */
    public AppiumException(String message) {
        super(message);
    }
}
