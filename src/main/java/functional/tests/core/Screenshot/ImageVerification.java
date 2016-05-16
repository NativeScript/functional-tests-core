package functional.tests.core.Screenshot;

import functional.tests.core.Enums.PlatformType;
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
import java.util.concurrent.Callable;

public class ImageVerification {

    // TODO: Read this from global config
    private static final boolean IGNORE_HEADER = true;
    private static final int SIMILAR_PIXEL_TOLERANCE = 50;
    private static final int DEFAULT_PIXEL_TOLERANCE = 250;
    private static final double DEFAULT_PERCENT_TOLERANCE = 1.0;
    private static final VerificationType VERIFICATION_TYPE = Settings.imageVerificationType;


    public static boolean compareElements(final MobileElement element, String appName, String expectedElementImage, int repeatTimes, int pixelTolerance, double percentTolerance) throws Exception {
        long timeOut = convertSecondsToMilliseconds(repeatTimes);
        return verifyImages(appName, expectedElementImage, pixelTolerance, percentTolerance, new Callable<BufferedImage>() {
            @Override
            public BufferedImage call() throws Exception {
                return ImageUtils.getElementImage(element);
            }
        }, timeOut, 1000, false, new ICustomFunction() {
            @Override
            public long calculateTime(long timeOut, long startTime) {
                return calculateRepeatimes(timeOut, startTime);
            }
        });
    }

    /**
     * Verify mobile element
     **/
    public static void verifyElement(final MobileElement element, String appName, String expectedElementImage, double percentTolerance) throws Exception {
        verifyElement(element, appName, expectedElementImage, Integer.MAX_VALUE, percentTolerance, 0);
    }

    /**
     * Verify mobile element
     **/
    public static void verifyElement(final MobileElement element, String appName, String expectedElementImage, int pixelTolerance) throws Exception {
        verifyElement(element, appName, expectedElementImage, pixelTolerance, Double.MAX_VALUE, 0);
    }

    /**
     * Verify mobile element
     **/
    public static void verifyElement(final MobileElement element, String appName, String expectedElementImage, int pixelTolerance, double percentTolerance, int timeOut) throws Exception {
        long timeLimit = convertSecondsToMilliseconds(timeOut);
        assertImages(new Callable<BufferedImage>() {
            @Override
            public BufferedImage call() throws Exception {
                return ImageUtils.getElementImage(element);
            }
        }, appName, expectedElementImage, pixelTolerance, percentTolerance, timeLimit, 1000, false, new ICustomFunction() {
            @Override
            public long calculateTime(long timeLimit, long startTime) {
                return calculateRepeatimes(timeLimit, startTime);
            }
        });
    }

    /**
     * Verify current screen
     **/
    public static void verifyScreen(String appName, String pageName) throws Exception {
        verifyScreen(appName, pageName, DEFAULT_PIXEL_TOLERANCE, DEFAULT_PERCENT_TOLERANCE);
    }

    /**
     * Verify current screen
     **/
    public static void verifyScreen(String appName, String pageName, double percentTolerance) throws Exception {
        verifyScreen(appName, pageName, Integer.MAX_VALUE, percentTolerance);
    }

    /**
     * Verify current screen
     **/
    public static void verifyScreen(String appName, String pageName, int pixelTolerance) throws Exception {
        verifyScreen(appName, pageName, pixelTolerance, Double.MAX_VALUE);
    }

    /**
     * Verify current screen
     **/
    public static void verifyScreen(String appName, String pageName, int pixelTolerance, double percentTolerance) throws Exception {
        assertImages(new Callable<BufferedImage>() {
            @Override
            public BufferedImage call() throws Exception {
                return ImageUtils.getScreen();
            }
        }, appName, pageName, pixelTolerance, percentTolerance, 0, 0, false, null);
    }

    /**
     * Wait until screen looks OK
     **/
    public static void waitForScreen(String appName, String pageName) throws Exception {
        waitForScreen(appName, pageName, DEFAULT_PIXEL_TOLERANCE, DEFAULT_PERCENT_TOLERANCE, Settings.defaultTimeout);
    }

    /**
     * Wait until screen looks OK
     **/
    public static void waitForScreen(String appName, String pageName, double percentTolerance, int timeOut) throws Exception {
        waitForScreen(appName, pageName, Integer.MAX_VALUE, percentTolerance, timeOut);
    }

    /**
     * Wait until screen looks OK
     **/
    public static void waitForScreen(String appName, String pageName, double percentTolerance) throws Exception {
        waitForScreen(appName, pageName, Integer.MAX_VALUE, percentTolerance, Settings.defaultTimeout);
    }

    public static void waitForScreen(String appName, String pageName, int pixelTolerance, double percentTolerance, int timeOut) throws Exception {
        long timeLimit = convertSecondsToMilliseconds(timeOut);
        assertImages(new Callable<BufferedImage>() {
            @Override
            public BufferedImage call() throws Exception {
                return ImageUtils.getScreen();
            }
        }, appName, pageName, pixelTolerance, percentTolerance, timeLimit, System.currentTimeMillis(), IGNORE_HEADER, new ICustomFunction() {
            @Override
            public long calculateTime(long timeLimit, long startTime) {
                return calculateWhileTimeOut(timeLimit, startTime);
            }
        });
    }

    private static long convertSecondsToMilliseconds(long seconds) {
        return seconds * 1000;
    }

    private static void assertImages(Callable<BufferedImage> element, String appName, String expectedElementImage, int pixelTolerance, double percentTolerance, long timeOut, long time, boolean readHeader, ICustomFunction calculateWaitTime) throws Exception {
        boolean result = verifyImages(appName, expectedElementImage, pixelTolerance, percentTolerance, element, timeOut, time, readHeader, calculateWaitTime);
        Assert.assertEquals(result, true);
    }

    private static boolean verifyImages(String appName, String expectedElementImage, int pixelTolerance, double percentTolerance, Callable<BufferedImage> actualImage, long timeOut, long time, boolean readHeader, ICustomFunction calculateWaitTime) throws Exception {
        boolean areImagesEqual = true;
        BufferedImage expectedImage;

        if (VERIFICATION_TYPE == VerificationType.Skip) {
            Log.warn("Image comparison skipped!");
            return true;
        }

        if (VERIFICATION_TYPE == VerificationType.JustCapture) {
            String folderName = Settings.screenshotResDir + File.separator + "actual";
            String actualImageName = folderName + File.separator + expectedElementImage + ".png";
            saveImage(actualImageName, actualImage, folderName, "Image comparison skipped. Actual images will be saved at $SCREENSHOT_LOCATION/actual");
            return true;
        }

        String expectedImageBasePath = Settings.screenshotResDir + File.separator + appName + File.separator + Settings.deviceName;
        String expectedImagePath = expectedImageBasePath + File.separator + expectedElementImage + ".png";

        if (VERIFICATION_TYPE == VerificationType.FirstTimeCapture) {
            saveImage(expectedImagePath, actualImage, expectedImageBasePath, "Image comparison skipped. Actual images will be also saved at expected image location.");
        }

        if (VERIFICATION_TYPE == VerificationType.Default) {
            expectedImage = ImageUtils.getImageFromFile(expectedImagePath);

            if (expectedImage == null) {
                Log.error("Failed to read expected image, image comparison skipped.");
                saveImage(expectedImagePath, actualImage, expectedImageBasePath, "Actual images will be also saved at expected image location.");
            } else {
                ImageVerificationResult result = compareImages(actualImage.call(), expectedImage, readHeader);
                if ((result.diffPixels > pixelTolerance) || (result.diffPercent > percentTolerance)) {
                    areImagesEqual = false;
                    while (timeOut > 0) {
                        String errorString = expectedElementImage + " does not look OK. Diff: " + String.format("%.2f", result.diffPercent) + ". Waiting...";
                        Log.info(errorString);
                        timeOut = calculateWaitTime.calculateTime(timeOut, time);
                        verifyImages(appName, expectedElementImage, pixelTolerance, percentTolerance, actualImage, timeOut, time, readHeader, calculateWaitTime);
                    }

                    String errorString = expectedElementImage + " does not look OK. Diff: " + String.format("%.2f", result.diffPercent);
                    Log.fatal(errorString);
                } else {
                    Log.info(expectedElementImage + " looks OK.");
                    areImagesEqual = true;
                }
                Log.logImageVerificationResult(result, expectedElementImage);
            }
        }
        return areImagesEqual;
    }

    private static long calculateWhileTimeOut(long timeOut, long startTime) {
        Wait.sleep(1000);

        timeOut = (System.currentTimeMillis() - startTime) - timeOut;

        return timeOut;
    }

    private static long calculateRepeatimes(long timeOut, long elapsedTime) {
        Wait.sleep((int) elapsedTime);

        timeOut = timeOut - elapsedTime;

        return timeOut;
    }

    private static long retryImageComparisson(long timeOut, long waitTime) {
        return timeOut - waitTime;
    }

    private static void saveImage(String imageName, Callable<BufferedImage> actualImage, String excpectedImageFolderName, String message) throws Exception {
        File image = new File(imageName);
        Wait.sleep(1000); // Wait some time until animations finish
        Log.warn(message);
        FileSystem.ensureFolderExists(excpectedImageFolderName);
        ImageUtils.saveBufferedImage(actualImage.call(), image);
    }

    /**
     * Compares two BufferedImage and return ImageVerificationResult
     */
    private static ImageVerificationResult compareImages(BufferedImage actualImage, BufferedImage expectedImage, Boolean ignoreHeader)
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

            // If ignoreHeader is True pixels at top are ignored in comparison
            int startY = 0;
            if (ignoreHeader) {
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

    private static BufferedImage copyImage(BufferedImage source) {
        BufferedImage b = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
        Graphics g = b.getGraphics();
        g.drawImage(source, 0, 0, null);
        g.dispose();
        return b;
    }

    public interface ICustomFunction {
        public long calculateTime(long timeOut, long startTime);
    }

}
