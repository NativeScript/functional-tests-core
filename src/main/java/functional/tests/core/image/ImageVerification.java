package functional.tests.core.image;

import functional.tests.core.basetest.Context;
import functional.tests.core.basetest.TestContextSetupManager;
import functional.tests.core.element.UIElement;
import functional.tests.core.enums.ImageVerificationType;
import functional.tests.core.enums.PlatformType;
import functional.tests.core.exceptions.ImageVerificationException;
import functional.tests.core.find.Wait;
import functional.tests.core.log.Log;
import functional.tests.core.log.LoggerBase;
import functional.tests.core.settings.Settings;
import functional.tests.core.utils.FileSystem;
import org.testng.Assert;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * TODO(): Add docs.
 */
public class ImageVerification {

    private static final LoggerBase LOGGER_BASE = LoggerBase.getLogger("ImageVerification");

    // TODO(): Read this from global config
    private static final boolean IGNORE_HEADER = true;
    private static final int SIMILAR_PIXEL_TOLERANCE = 30;
    private static final int DEFAULT_PIXEL_TOLERANCE = 250;
    private static final double DEFAULT_PERCENT_TOLERANCE = 1.0;
    private static final int DEFAULT_WAIT_TIME = 1000;
    private static final int MIN_TIMEOUT = 1;
    private ImageVerificationType verificationType;
    private ImageUtils imageUtils;
    private Context context;
    private Settings settings;

    /**
     * TODO(): Add docs.
     */
    public ImageVerification() {
        this.context = TestContextSetupManager.getTestSetupManager().context;
        this.settings = this.context.settings;
        this.imageUtils = this.context.imageUtils;
        this.verificationType = this.context.settings.imageVerificationType;
    }

    /**
     * TODO(): Add docs.
     *
     * @param source
     * @return
     */
    private static BufferedImage copyImage(BufferedImage source) {
        BufferedImage b = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
        Graphics g = b.getGraphics();
        g.drawImage(source, 0, 0, null);
        g.dispose();
        return b;
    }

    /**
     * TODO(): Add docs.
     *
     * @param element
     * @param expectedElementImage
     * @param timeOut
     * @param waitTime
     * @param pixelTolerance
     * @param percentTolerance
     * @return
     * @throws Exception
     */
    public boolean compareElements(final UIElement element, String expectedElementImage, int timeOut, int waitTime, int pixelTolerance, double percentTolerance) throws Exception {
        return this.verifyImages(this.settings.testAppImageFolder, expectedElementImage, pixelTolerance, percentTolerance, new IElementToImageConverter<BufferedImage>() {
            @Override
            public BufferedImage call(ImageUtils imageUtils) throws Exception {
                return imageUtils.getElementImage(element);
            }
        }, timeOut, waitTime, !IGNORE_HEADER);
    }

    /**
     * TODO(): Add docs.
     *
     * @param pageName
     * @param timeOut
     * @param waitTime
     * @param pixelTolerance
     * @param percentTolerance
     * @return
     * @throws Exception
     */
    public boolean compareScreens(String pageName, int timeOut, int waitTime, int pixelTolerance, double percentTolerance) throws Exception {
        return this.verifyImages(this.settings.testAppImageFolder, pageName, pixelTolerance, percentTolerance, new IElementToImageConverter<BufferedImage>() {
            @Override
            public BufferedImage call(ImageUtils imageUtils) throws Exception {
                return imageUtils.getScreen();
            }
        }, timeOut, waitTime, IGNORE_HEADER);
    }

    /**
     * TODO(): Add docs.
     *
     * @param element
     * @param expectedElementImage
     * @param percentTolerance
     * @throws Exception
     */
    public void verifyElement(final UIElement element, String expectedElementImage, double percentTolerance) throws Exception {
        this.verifyElement(element, expectedElementImage, Integer.MAX_VALUE, percentTolerance, MIN_TIMEOUT);
    }

    /**
     * TODO(): Add docs.
     *
     * @param element
     * @param expectedElementImage
     * @param pixelTolerance
     * @throws Exception
     */
    public void verifyElement(final UIElement element, String expectedElementImage, int pixelTolerance) throws Exception {
        this.verifyElement(element, expectedElementImage, pixelTolerance, Double.MAX_VALUE, MIN_TIMEOUT);
    }

    /**
     * TODO(): Add docs.
     *
     * @param element
     * @param expectedElementImage
     * @param pixelTolerance
     * @param percentTolerance
     * @param timeOut
     * @throws Exception
     */
    public void verifyElement(final UIElement element, String expectedElementImage, int pixelTolerance, double percentTolerance, int timeOut) throws Exception {
        this.assertImages(new IElementToImageConverter<BufferedImage>() {
            @Override
            public BufferedImage call(ImageUtils imageUtils) throws Exception {
                return imageUtils.getElementImage(element);
            }
        }, this.settings.testAppImageFolder, expectedElementImage, pixelTolerance, percentTolerance, timeOut, DEFAULT_WAIT_TIME, false);
    }

    /**
     * TODO(): Add docs.
     *
     * @param pageName
     * @throws Exception
     */
    public void verifyScreen(String pageName) throws Exception {
        this.verifyScreen(pageName, DEFAULT_PIXEL_TOLERANCE, DEFAULT_PERCENT_TOLERANCE);
    }

    /**
     * TODO(): Add docs.
     *
     * @param pageName
     * @param percentTolerance
     * @throws Exception
     */
    public void verifyScreen(String pageName, double percentTolerance) throws Exception {
        this.verifyScreen(pageName, Integer.MAX_VALUE, percentTolerance);
    }

    /**
     * TODO(): Add docs.
     *
     * @param pageName
     * @param pixelTolerance
     * @throws Exception
     */
    public void verifyScreen(String pageName, int pixelTolerance) throws Exception {
        this.verifyScreen(pageName, pixelTolerance, Double.MAX_VALUE);
    }

    /**
     * TODO(): Add docs.
     *
     * @param pageName
     * @param pixelTolerance
     * @param percentTolerance
     * @throws Exception
     */
    public void verifyScreen(String pageName, int pixelTolerance, double percentTolerance) throws Exception {
        this.verifyScreen(pageName, pixelTolerance, percentTolerance, this.settings.defaultTimeout, 1000);
    }

    /**
     * TODO(): Add docs.
     *
     * @param pageName
     * @param pixelTolerance
     * @param percentTolerance
     * @param timeOut
     * @param sleepTime
     * @throws Exception
     */
    public void verifyScreen(String pageName, int pixelTolerance, double percentTolerance, int timeOut, int sleepTime) throws Exception {
        this.assertImages(new IElementToImageConverter<BufferedImage>() {
            @Override
            public BufferedImage call(ImageUtils imageUtils) throws Exception {
                return imageUtils.getScreen();
            }
        }, this.settings.testAppImageFolder, pageName, pixelTolerance, percentTolerance, timeOut, sleepTime, IGNORE_HEADER);
    }

    /**
     * TODO(): Add docs.
     *
     * @param element
     * @param appName
     * @param imageName
     * @param pixelTolerance
     * @param percentTolerance
     * @param timeOut
     * @param sleepTime
     * @param ignoreHeader
     * @throws Exception
     */
    private void assertImages(IElementToImageConverter<BufferedImage> element, String appName, String imageName,
                              int pixelTolerance, double percentTolerance, int timeOut, int sleepTime,
                              boolean ignoreHeader) throws Exception {
        boolean result = this.verifyImages(appName, imageName,
                pixelTolerance, percentTolerance, element, timeOut, sleepTime, ignoreHeader);
        Assert.assertTrue(result, String.format("Image comparison failed. %s is not as expected!", imageName));
    }

    /**
     * TODO(): Add docs.
     *
     * @param appName
     * @param imageName
     * @param pixelTolerance
     * @param percentTolerance
     * @param actualImage
     * @param timeOut
     * @param sleepTime
     * @param ignoreHeader
     * @return
     * @throws Exception
     */
    private boolean verifyImages(String appName, String imageName, int pixelTolerance, double percentTolerance,
                                 IElementToImageConverter<BufferedImage> actualImage, int timeOut, int sleepTime,
                                 boolean ignoreHeader) throws Exception {
        BufferedImage expectedImage;
        Log log = this.context.log;

        // ImageVerificationType.Skip:
        // Do NOT perform image verification.
        if (this.verificationType == ImageVerificationType.Skip) {
            this.LOGGER_BASE.warn("Skip image verification!");

            return true;
        }

        // For example: /Users/vchimev/Work/git/functional-tests/resources/images/uitests/Emulator-Api23-Default
        String expectedImageFolderPath = this.imageUtils.getImageFolderPath(appName);

        // For example: /Users/vchimev/Work/git/functional-tests/resources/images/uitests/Emulator-Api23-Default/flexbox_00_default.png
        String expectedImageFullName = this.imageUtils.getImageFullName(expectedImageFolderPath, imageName);

        expectedImage = this.imageUtils.getImageFromFile(expectedImageFullName);

        boolean areImagesEqual = false;

        if (expectedImage == null) {
            ImageVerification.LOGGER_BASE.error("Expected image is NOT available!");
            Wait.sleep(sleepTime);

            // ImageVerificationType.Default:
            // Fail test if expected image is not available;
            // Save actual image with an '_actual' postfix at the expected location;
            // Perform image verification.
            if (this.verificationType == ImageVerificationType.Default) {
                String actualImageName = imageName + "_actual";
                String actualImageFullName = this.imageUtils.getImageFullName(expectedImageFolderPath, actualImageName);
                expectedImageFullName = actualImageFullName;
                // Do NOT restart the app to preserve its state
                this.context.shouldRestartAppOnFailure = false;
            }

            // ImageVerificationType.FirstTimeCapture:
            // Do NOT fail test if expected image is not available;
            // Save actual image with the expected name at the expected location;
            // Perform image verification.
            if (this.verificationType == ImageVerificationType.FirstTimeCapture) {

                areImagesEqual = true;
            }

            this.saveImage(expectedImageFullName, actualImage, expectedImageFolderPath, "Save actual image at expected location: " + expectedImageFullName);

            // Log image to be visible in the report of tests
            BufferedImage image = actualImage.call(this.imageUtils);
            ImageVerificationResult imageVerificationResult = new ImageVerificationResult(0, 0, image, image, image);
            log.logImageVerificationResult(imageVerificationResult, imageName);

            return areImagesEqual;

        } else {
            ImageVerificationResult result = null;
            long startTime = System.currentTimeMillis();
            while ((System.currentTimeMillis() - startTime) < timeOut * 1000) {
                result = this.compareImages(actualImage.call(this.imageUtils), expectedImage, ignoreHeader);
                if ((result.diffPixels > pixelTolerance) || (result.diffPercent > percentTolerance)) {
                    if (this.settings.logImageVerificationStatus) {
                        log.logImageVerificationResult(result, "result_" + String.valueOf(System.currentTimeMillis() - startTime + "_" + imageName));
                    } else {
                        String message = String.format("%s does NOT look OK. Diff percents: %.2f%% . Waiting ...", imageName, result.diffPercent);
                        this.LOGGER_BASE.error(message);
                    }
                } else {
                    this.LOGGER_BASE.info(imageName + " looks OK.");
                    areImagesEqual = true;
                    break;
                }
            }

            if (!areImagesEqual) {
                log.logImageVerificationResult(result, imageName);
            }
        }

        return areImagesEqual;
    }

    /**
     * TODO(): Add docs.
     *
     * @param imageName
     * @param actualImage
     * @param expectedImageFolderName
     * @param message
     * @throws Exception
     */
    private void saveImage(String imageName, IElementToImageConverter<BufferedImage> actualImage, String expectedImageFolderName, String message) throws Exception {
        Wait.sleep(ImageVerification.DEFAULT_WAIT_TIME); // Wait some time until animations finish
        this.LOGGER_BASE.warn(message);
        FileSystem.ensureFolderExists(expectedImageFolderName);
        this.imageUtils.saveBufferedImage(actualImage.call(this.imageUtils), imageName);
    }

    /**
     * TODO(): Add docs.
     *
     * @param actualImage
     * @param expectedImage
     * @param ignoreHeader
     * @return
     * @throws ImageVerificationException
     */
    private ImageVerificationResult compareImages(BufferedImage actualImage, BufferedImage expectedImage, Boolean ignoreHeader)
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
            this.LOGGER_BASE.error("Screenshot and expected image are with different size.");
            this.LOGGER_BASE.error("Actual image: " + width1 + "x" + height1);
            this.LOGGER_BASE.error("Expected image: " + width2 + "x" + height2);

            diffPixels = width1 * height1;
            //throw new ImageVerificationException("Screenshot and expected image are with different size.");
        } else {

            // Define red color
            Color red = new Color(255, 0, 0);
            int redRgb = red.getRGB();

            // If ignoreHeader is True pixels at top are ignored in comparison
            int startY = 0;
            if (ignoreHeader) {
                // TODO(): Reasearch if we can better define what is header
                if (this.settings.platform == PlatformType.Andorid) {
                    startY = (int) (height1 * 0.07);
                } else if (this.settings.platform == PlatformType.iOS) {
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
                    int rootMeanSquare = (int) Math.sqrt(((rDiff * rDiff)
                            + (gDiff * gDiff) + (bDiff * bDiff)) / 3);

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
     * TODO(): Add docs.
     *
     * @param <V>
     */
    public interface IElementToImageConverter<V> {

        /**
         * Computes a result, or throws an exception if unable to do so.
         *
         * @return computed result
         * @throws Exception if unable to compute a result
         */
        V call(ImageUtils imageUtils) throws Exception;
    }
}
