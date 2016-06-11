package functional.tests.core.Screenshot;

import functional.tests.core.Element.UIElement;
import functional.tests.core.Enums.PlatformType;
import functional.tests.core.Exceptions.ImageVerificationException;
import functional.tests.core.Find.Wait;
import functional.tests.core.Log.Log;
import functional.tests.core.OSUtils.FileSystem;
import functional.tests.core.Settings.Settings;
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
    private static final int DEFAULT_WAIT_TIME = 1000;
    private static final int MIN_TIMEOUT = 1;
    private static final VerificationType VERIFICATION_TYPE = Settings.imageVerificationType;

    public static boolean compareElements(final UIElement element, String appName, String expectedElementImage, int timeOut, int waitTime, int pixelTolerance, double percentTolerance) throws Exception {
        return verifyImages(appName, expectedElementImage, pixelTolerance, percentTolerance, new Callable<BufferedImage>() {
            @Override
            public BufferedImage call() throws Exception {
                return ImageUtils.getElementImage(element);
            }
        }, timeOut, waitTime, !IGNORE_HEADER);
    }

    /**
     * Verify current screen
     **/
    public static boolean compareScreens(String appName, String pageName, int timeOut, int waitTime, int pixelTolerance, double percentTolerance) throws Exception {
        return verifyImages(appName, pageName, pixelTolerance, percentTolerance, new Callable<BufferedImage>() {
            @Override
            public BufferedImage call() throws Exception {
                return ImageUtils.getScreen();
            }
        }, timeOut, waitTime, IGNORE_HEADER);
    }

    /**
     * Verify mobile element
     **/
    public static void verifyElement(final UIElement element, String appName, String expectedElementImage, double percentTolerance) throws Exception {
        verifyElement(element, appName, expectedElementImage, Integer.MAX_VALUE, percentTolerance, MIN_TIMEOUT);
    }

    /**
     * Verify mobile element
     **/
    public static void verifyElement(final UIElement element, String appName, String expectedElementImage, int pixelTolerance) throws Exception {
        verifyElement(element, appName, expectedElementImage, pixelTolerance, Double.MAX_VALUE, MIN_TIMEOUT);
    }

    /**
     * Verify mobile element
     **/
    public static void verifyElement(final UIElement element, String appName, String expectedElementImage, int pixelTolerance, double percentTolerance, int timeOut) throws Exception {
        assertImages(new Callable<BufferedImage>() {
            @Override
            public BufferedImage call() throws Exception {
                return ImageUtils.getElementImage(element);
            }
        }, appName, expectedElementImage, pixelTolerance, percentTolerance, timeOut, DEFAULT_WAIT_TIME, false);
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
        verifyScreen(appName, pageName, pixelTolerance, percentTolerance, Settings.defaultTimeout, 1000);
    }

    /**
     * Verify current screen
     **/
    public static void verifyScreen(String appName, String pageName, int pixelTolerance, double percentTolerance, int timeOut, int sleepTime) throws Exception {
        assertImages(new Callable<BufferedImage>() {
            @Override
            public BufferedImage call() throws Exception {
                return ImageUtils.getScreen();
            }
        }, appName, pageName, pixelTolerance, percentTolerance, timeOut, sleepTime, IGNORE_HEADER);
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
    public static void waitForScreen(String appName, String pageName, double percentTolerance) throws Exception {
        waitForScreen(appName, pageName, Integer.MAX_VALUE, percentTolerance, Settings.defaultTimeout);
    }

    /**
     * Wait until screen looks OK
     **/
    public static void waitForScreen(String appName, String pageName, double percentTolerance, int timeOut) throws Exception {
        waitForScreen(appName, pageName, Integer.MAX_VALUE, percentTolerance, timeOut);
    }

    public static void waitForScreen(String appName, String pageName, int pixelTolerance, double percentTolerance, int timeOut) throws Exception {
        assertImages(new Callable<BufferedImage>() {
            @Override
            public BufferedImage call() throws Exception {
                return ImageUtils.getScreen();
            }
        }, appName, pageName, pixelTolerance, percentTolerance, timeOut, DEFAULT_WAIT_TIME, IGNORE_HEADER);
    }

    private static void assertImages(Callable<BufferedImage> element, String appName, String imageName, int pixelTolerance, double percentTolerance, int timeOut, int sleepTime, boolean ignoreHeader) throws Exception {
        boolean result = verifyImages(appName, imageName, pixelTolerance, percentTolerance, element, timeOut, sleepTime, ignoreHeader);
        Assert.assertTrue(result, String.format("Image comparison failed. %s is not as expected!", imageName));
    }

    private static boolean verifyImages(String appName, String imageName, int pixelTolerance, double percentTolerance, Callable<BufferedImage> actualImage, int timeOut, int sleepTime, boolean ignoreHeader) throws Exception {
        boolean areImagesEqual = false;
        BufferedImage expectedImage;

        if (VERIFICATION_TYPE == VerificationType.Skip) {
            Log.warn("Image comparison skipped!");
            return true;
        }

        if (VERIFICATION_TYPE == VerificationType.JustCapture) {
            String folderName = Settings.screenshotResDir + File.separator + "actual";
            String actualImageName = folderName + File.separator + imageName + ".png";
            saveImage(actualImageName, actualImage, folderName, "Image comparison skipped. Actual images will be saved at $SCREENSHOT_LOCATION/actual");
            return true;
        }

        String expectedImageBasePath = Settings.screenshotResDir + File.separator + appName + File.separator + Settings.deviceName;
        String expectedImagePath = expectedImageBasePath + File.separator + imageName + ".png";

        if (VERIFICATION_TYPE == VerificationType.FirstTimeCapture) {
            saveImage(expectedImagePath, actualImage, expectedImageBasePath, "Image comparison skipped. Actual images will be also saved at expected image location.");
        }

        if (VERIFICATION_TYPE == VerificationType.Default) {
            expectedImage = ImageUtils.getImageFromFile(expectedImagePath);
            if (expectedImage == null) {
                Wait.sleep(sleepTime);
                Log.error("Failed to read expected image, image comparison skipped.");
                saveImage(expectedImagePath, actualImage, expectedImageBasePath, "Actual images will be also saved at expected image location.");
                String tempImageStorage = Settings.screenshotResDir + "-temp" + File.separator + appName + File.separator;
                String tempImageName = tempImageStorage + File.separator + Settings.deviceName + "_" + imageName + ".png";
                saveImage(tempImageName, actualImage, tempImageStorage, String.format("Actual images will be also saved in %s folder.", Settings.screenshotResDir + "-temp"));
            } else {
                long startTime = System.currentTimeMillis();
                ImageVerificationResult result = null;
                while ((System.currentTimeMillis() - startTime) < timeOut * 1000) {
                    result = compareImages(actualImage.call(), expectedImage, ignoreHeader);
                    //Log.logImageVerificationResult(result, "result_" + String.valueOf(System.currentTimeMillis() - startTime));
                    if ((result.diffPixels > pixelTolerance) || (result.diffPercent >  percentTolerance)) {
                        String toleranceInfo = String.format("Percent tolerance: %.2f %% and pixel tolerance: %s", result.diffPercent, "" + result.diffPixels);
                        String errorString = imageName + " does not look OK. Diff: " + String.format("%.2f %% and pixelDiff: %s", result.diffPercent,"" + result.diffPixels) + ". Waiting...";
                        Log.info(toleranceInfo);
                        Log.info(errorString);
                    } else {
                        Log.info(imageName + " looks OK.");
                        areImagesEqual = true;
                        break;
                    }
                }
                if (!areImagesEqual) {
                    Log.logImageVerificationResult(result, imageName);
                }
            }
        }

        return areImagesEqual;
    }

    private static void saveImage(String imageName, Callable<BufferedImage> actualImage, String expectedImageFolderName, String message) throws Exception {
        File image = new File(imageName);
        Wait.sleep(1000); // Wait some time until animations finish
        Log.warn(message);
        FileSystem.ensureFolderExists(expectedImageFolderName);
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
}
