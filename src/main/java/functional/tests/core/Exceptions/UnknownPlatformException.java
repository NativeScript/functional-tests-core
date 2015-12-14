package functional.tests.core.Exceptions;

import functional.tests.core.Log.Log;

/**
 * Created by Dimitar on 10/26/2015.
 */
public class UnknownPlatformException extends Exception {
    public UnknownPlatformException(String message) {
        super(message);
    }
}