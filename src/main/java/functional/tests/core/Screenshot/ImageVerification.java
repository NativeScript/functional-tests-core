package functional.tests.core.Screenshot;

import functional.tests.core.Enums.PlatformType;
import functional.tests.core.Exceptions.AppiumException;
import functional.tests.core.Exceptions.ImageVerificationException;
import functional.tests.core.Find.Wait;
import functional.tests.core.Log.Log;
import functional.tests.core.OSUtils.FileSystem;
import functional.tests.core.Settings.Settings;
import io.appium.java_client.MobileElement;
import org.testng.Assert;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageVerification {

    // TODO: Read this from global config
    private static final boolean IGNORE_HEADER = true;
    private static final int SIMILAR_PIXEL_TOLERANCE = 50;
    private static final int DEFAULT_PIXEL_TOLERANCE = 250;
    private static final double DEFAULT_PERCENT_TOLERANCE = 1.0;
    private static final VerificationType VERIFICATION_TYPE = Settings.imageVerificationType;

    private static BufferedImage copyImage(BufferedImage source) {
        BufferedImage b = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
        Graphics g = b.getGraphics();
        g.drawImage(source, 0, 0, null);
        g.dispose();
        return b;
    }

    /**
     * Compares two BufferedImage and return ImageVerificationResult
     */
    private static ImageVerificationResult compareImages(BufferedImage actualImage, BufferedImage expectedImage)
            throws ImageVerificationException {

        int diffPixels = 0;
        double diffPercent;

        // Generate diff image
        BufferedImage diffImage = copyImage(actualImage);

        // Get image sizes
        int width1 = diffImage.getWidth(null);
        int width2 = expectedImage.getWidth(null);
        int height1 = diffImage.getHeight(null);
        int height2 = expectedImage.getHeight(null);

        // If image size is different then skip comparison
        if ((width1 != width2) || (height1 != height2)) {
            Log.error("Screenshot and expected image are with different size.");
            Log.error("Actual image: " + width1 + "x" + height1);
            Log.error("Expected image: " + width2 + "x" + height2);

            diffPixels = width1 * height1;
            //throw new ImageVerificationException("Screenshot and expected image are with different size.");
        } else {

            // Define red color
            Color red = new Color(255, 0, 0);
            int redRgb = red.getRGB();

            // If IGNORE_HEADER is True pixels at top are ignored in comparison
            int startY = 0;
            if (IGNORE_HEADER) {
                // TODO: Reasearch if we can better define what is header
                if (Settings.platform == PlatformType.Andorid) {
                    startY = (int) (height1 * 0.07);
                } else if (Settings.platform == PlatformType.iOS) {
                    startY = (int) (height1 * 0.03);
                }
            }

            // Compare pixel by pixel
            for (int i = startY; i < height1; i++) {
                for (int j = 0; j < width1; j++) {
                    int rgb1 = diffImage.getRGB(j, i);
                    int blue1 = rgb1 & 0xFF;
                    int green1 = (rgb1 >> 8) & 0xFF;
                    int red1 = (rgb1 >> 16) & 0xFF;
                    int rgb2 = expectedImage.getRGB(j, i);
                    int blue2 = rgb2 & 0xFF;
                    int green2 = (rgb2 >> 8) & 0xFF;
                    int red2 = (rgb2 >> 16) & 0xFF;
                    int rDiff = Math.abs(red1 - red2);
                    int gDiff = Math.abs(green1 - green2);
                    int bDiff = Math.abs(blue1 - blue2);
                    int rootMeanSquare = (int) Math.sqrt((((rDiff * rDiff)
                            + (gDiff * gDiff) + (bDiff * bDiff)) / 3));

                    if (rootMeanSquare > SIMILAR_PIXEL_TOLERANCE) {
                        // Increase count in diffPixels
                        diffPixels++;
                        // Write different pixels in diffImage with red color
                        diffImage.setRGB(j, i, redRgb);
                    }
                }
            }
        }

        diffPercent = (100 * diffPixels) / (double) (width2 * height2);
        return new ImageVerificationResult(diffPixels, diffPercent, actualImage, diffImage, expectedImage);
    }

    /**
     * Verify mobile element
     **/
    public static void verifyElement(MobileElement element, String appName, String expectedElementImage, int pixelTolerance, double percentTolerance) throws AppiumException, IOException, ImageVerificationException {
        BufferedImage actualImage = ImageUtils.getElementImage(element);
        BufferedImage expectedImage = null;

        String expectedImageBasePath = Settings.screenshotResDir + File.separator +
                appName + File.separator +
                Settings.deviceName;

        String expectedImagePath = expectedImageBasePath + File.separator + expectedElementImage + ".png";

        if (VERIFICATION_TYPE == VerificationType.Skip) {
            Log.warn("Image comparison skipped!");
        } else if (VERIFICATION_TYPE == VerificationType.FirstTimeCapture) {
            Wait.sleep(1000); // Wait some time until animations finish
            Log.warn("Image comparison skipped. Actual images will be also saved at expected image location.");
            FileSystem.makeDir(expectedImageBasePath);
            ImageUtils.saveBufferedImage(actualImage, new File(expectedImagePath));
        } else if (VERIFICATION_TYPE == VerificationType.JustCapture) {
            Wait.sleep(1000); // Wait some time until animations finish
            Log.warn("Image comparison skipped. Actual images will be saved at $SCREENSHOT_LOCATION/actual");
            FileSystem.makeDir(Settings.screenshotOutDir + File.separator + "actual");
            ImageUtils.saveBufferedImage(actualImage, "actual" + File.separator + expectedElementImage + ".png");
        } else if (VERIFICATION_TYPE == VerificationType.Default) {
            expectedImage = ImageUtils.getImageFromFile(expectedImagePath);
            if (expectedImage == null) {
                Wait.sleep(1000); // Wait some time until animations finish
                Log.error("Failed to read expected image, image comparison skipped.");
                Log.info("Actual images will be also saved at expected image location.");
                FileSystem.makeDir(expectedImageBasePath);
                ImageUtils.saveBufferedImage(actualImage, new File(expectedImagePath));
            } else {
                ImageVerificationResult result = compareImages(actualImage, expectedImage);

                Log.logImageVerificationResult(result, expectedElementImage);
                if ((result.diffPixels > pixelTolerance) || (result.diffPercent > percentTolerance)) {
                    String errorString = expectedElementImage + " does not look OK. Diff: " + String.format("%.2f", result.diffPercent);
                    Log.error(errorString);
                    Assert.fail(errorString);
                } else {
                    Log.info(expectedElementImage + " looks OK.");
                }
            }
        }
    }

    /**
     * Verify mobile element
     **/
    public static void verifyElement(MobileElement element, String appName, String expectedElementImage, double percentTolerance) throws AppiumException, IOException, ImageVerificationException {
        verifyElement(element, appName, expectedElementImage, Integer.MAX_VALUE, percentTolerance);
    }

    /**
     * Verify mobile element
     **/
    public static void verifyElement(MobileElement element, String appName, String expectedElementImage, int pixelTolerance) throws AppiumException, IOException, ImageVerificationException {
        verifyElement(element, appName, expectedElementImage, pixelTolerance, Double.MAX_VALUE);
    }

    /**
     * Verify current screen
     **/
    public static void verifyScreen(String appName, String pageName, int pixelTolerance, double percentTolerance) throws AppiumException, IOException, ImageVerificationException {

        BufferedImage actualImage = ImageUtils.getScreen();
        BufferedImage expectedImage = null;

        String expectedImageBasePath = Settings.screenshotResDir + File.separator +
                appName + File.separator +
                Settings.deviceName;

        String expectedImagePath = expectedImageBasePath + File.separator + pageName + ".png";

        if (VERIFICATION_TYPE == VerificationType.Skip) {
            Log.warn("Image comparison skipped!");
        } else if (VERIFICATION_TYPE == VerificationType.FirstTimeCapture) {
            Wait.sleep(1000); // Wait some time until animations finish
            Log.warn("Image comparison skipped. Actual images will be also saved at expected image location.");
            FileSystem.makeDir(expectedImageBasePath);
            ImageUtils.saveBufferedImage(actualImage, new File(expectedImagePath));
        } else if (VERIFICATION_TYPE == VerificationType.JustCapture) {
            Wait.sleep(1000); // Wait some time until animations finish
            Log.warn("Image comparison skipped. Actual images will be saved at $SCREENSHOT_LOCATION/actual");
            FileSystem.makeDir(Settings.screenshotOutDir + File.separator + "actual");
            ImageUtils.saveBufferedImage(actualImage, "actual" + File.separator + pageName + ".png");
        } else if (VERIFICATION_TYPE == VerificationType.Default) {
            expectedImage = ImageUtils.getImageFromFile(expectedImagePath);
            if (expectedImage == null) {
                Wait.sleep(1000); // Wait some time until animations finish
                Log.error("Failed to read expected image, image comparison skipped.");
                Log.info("Actual images will be also saved at expected image location.");
                FileSystem.makeDir(expectedImageBasePath);
                ImageUtils.saveBufferedImage(actualImage, new File(expectedImagePath));
            } else {
                ImageVerificationResult result = compareImages(actualImage, expectedImage);

                Log.logImageVerificationResult(result, pageName);
                if ((result.diffPixels > pixelTolerance) || (result.diffPercent > percentTolerance)) {
                    String errorString = pageName + " does not look OK. Diff: " + String.format("%.2f", result.diffPercent);
                    Log.error(errorString);
                    Assert.fail(errorString);
                } else {
                    Log.info(pageName + " looks OK.");
                }
            }
        }
    }

    /**
     * Verify current screen
     **/
    public static void waitForScreen(String appName, String pageName, int pixelTolerance, double percentTolerance, int timeOut) throws AppiumException, IOException, ImageVerificationException {

        BufferedImage expectedImage = null;

        String expectedImageBasePath = Settings.screenshotResDir + File.separator +
                appName + File.separator +
                Settings.deviceName;

        String expectedImagePath = expectedImageBasePath + File.separator + pageName + ".png";

        if (VERIFICATION_TYPE == VerificationType.Skip) {
            Log.warn("Image comparison skipped!");
        } else if (VERIFICATION_TYPE == VerificationType.FirstTimeCapture) {
            Wait.sleep(1000); // Wait some time until animations finish
            Log.warn("Image comparison skipped. Actual images will be also saved at expected image location.");
            FileSystem.makeDir(expectedImageBasePath);
            BufferedImage actualImage = ImageUtils.getScreen();
            ImageUtils.saveBufferedImage(actualImage, new File(expectedImagePath));
        } else if (VERIFICATION_TYPE == VerificationType.JustCapture) {
            Wait.sleep(1000); // Wait some time until animations finish
            Log.warn("Image comparison skipped. Actual images will be saved at $SCREENSHOT_LOCATION/actual");
            FileSystem.makeDir(Settings.screenshotOutDir + File.separator + "actual");
            BufferedImage actualImage = ImageUtils.getScreen();
            ImageUtils.saveBufferedImage(actualImage, "actual" + File.separator + pageName + ".png");
        } else if (VERIFICATION_TYPE == VerificationType.Default) {
            expectedImage = ImageUtils.getImageFromFile(expectedImagePath);
            if (expectedImage == null) {
                Wait.sleep(1000); // Wait some time until animations finish
                Log.error("Failed to read expected image, image comparison skipped.");
                Log.info("Actual images will be also saved at expected image location.");
                FileSystem.makeDir(expectedImageBasePath);
                BufferedImage actualImage = ImageUtils.getScreen();
                ImageUtils.saveBufferedImage(actualImage, new File(expectedImagePath));
            } else {
                ImageVerificationResult result = null;
                long startTime = System.currentTimeMillis();
                while ((System.currentTimeMillis() - startTime) < timeOut * 1000) {
                    BufferedImage actualImage = ImageUtils.getScreen();
                    result = compareImages(actualImage, expectedImage);
                    if ((result.diffPixels > pixelTolerance) || (result.diffPercent > percentTolerance)) {
                        String errorString = pageName + " does not look OK. Diff: " + String.format("%.2f", result.diffPercent) + ". Waiting...";
                        Log.info(errorString);
                        Wait.sleep(1000);
                    } else {
                        Log.info(pageName + " looks OK.");
                        break;
                    }
                }

                Log.logImageVerificationResult(result, pageName);
                if ((result.diffPixels > pixelTolerance) || (result.diffPercent > percentTolerance)) {
                    String errorString = pageName + " does not look OK. Diff: " + String.format("%.2f", result.diffPercent);
                    Log.error(errorString);
                    Assert.fail(errorString);
                }
            }
        }
    }

    /**
     * Verify current screen
     **/
    public static void verifyScreen(String appName, String pageName, double percentTolerance) throws AppiumException, IOException, ImageVerificationException {
        verifyScreen(appName, pageName, Integer.MAX_VALUE, percentTolerance);
    }

    /**
     * Verify current screen
     **/
    public static void verifyScreen(String appName, String pageName, int pixelTolerance) throws AppiumException, IOException, ImageVerificationException {
        verifyScreen(appName, pageName, pixelTolerance, Double.MAX_VALUE);
    }

    /**
     * Verify current screen
     **/
    public static void verifyScreen(String appName, String pageName) throws AppiumException, IOException, ImageVerificationException {
        verifyScreen(appName, pageName, DEFAULT_PIXEL_TOLERANCE, DEFAULT_PERCENT_TOLERANCE);
    }

    /**
     * Wait until screen looks OK
     **/
    public static void waitForScreen(String appName, String pageName) throws AppiumException, IOException, ImageVerificationException {
        waitForScreen(appName, pageName, DEFAULT_PIXEL_TOLERANCE, DEFAULT_PERCENT_TOLERANCE, Settings.defaultTimeout);
    }

    /**
     * Wait until screen looks OK
     **/
    public static void waitForScreen(String appName, String pageName, double percentTolerance, int timeOut) throws AppiumException, IOException, ImageVerificationException {
        waitForScreen(appName, pageName, Integer.MAX_VALUE, percentTolerance, timeOut);
    }

    /**
     * Wait until screen looks OK
     **/
    public static void waitForScreen(String appName, String pageName, double percentTolerance) throws AppiumException, IOException, ImageVerificationException {
        waitForScreen(appName, pageName, Integer.MAX_VALUE, percentTolerance, Settings.defaultTimeout);
    }


}
