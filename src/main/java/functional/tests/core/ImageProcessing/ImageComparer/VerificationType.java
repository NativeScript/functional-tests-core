package functional.tests.core.ImageProcessing.ImageComparer;

/**
 * Types of image verification
 */
public enum VerificationType {
    Default, // Default image verification
    JustCapture,  // Just capture current images in screenshot/actual/ folder
    FirstTimeCapture, // Just capture current images at location where expected image should be
    Skip // This will skip image verification
}