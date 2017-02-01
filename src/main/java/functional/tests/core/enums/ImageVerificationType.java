package functional.tests.core.enums;

/**
 * Types of image verification.
 */
public enum ImageVerificationType {

    Default,            // Fail test if expected image is not available;
                        // Save actual image with an '_actual' postfix at the expected location;
                        // Perform image verification.

    FirstTimeCapture,   // Do NOT fail test if expected image is not available;
                        // Save actual image with the expected name at the expected location;
                        // Perform image verification.

    Skip,               // Do NOT perform image verification.
}
