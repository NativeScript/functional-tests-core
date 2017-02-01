package functional.tests.core.image;

import functional.tests.core.appium.Client;
import functional.tests.core.basetest.Context;
import functional.tests.core.basetest.TestContextSetupManager;
import functional.tests.core.element.UIElement;
import functional.tests.core.exceptions.AppiumException;
import functional.tests.core.log.LoggerBase;
import functional.tests.core.settings.Settings;
import functional.tests.core.utils.FileSystem;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Image utils.
 */
public class ImageUtils {

    private static final LoggerBase LOGGER_BASE = LoggerBase.getLogger("ImageUtils");
    private Client client;
    private Settings settings;
    private Context context;

    /**
     * TODO(svetli): Explain when we need ImageUtils().
     */
    public ImageUtils() {
        this(TestContextSetupManager.getTestSetupManager().context);
    }

    /**
     * TODO(svetli): Explain when we need ImageUtils(Context context).
     *
     * @param context
     */
    public ImageUtils(Context context) {
        this.context = TestContextSetupManager.getTestSetupManager().context;
        this.client = this.context.client;
        this.settings = this.context.settings;
    }

    /**
     * Get image from file.
     *
     * @param filePath Path to image.
     * @return BufferedImage from path. Null if path does not exist.
     */
    public BufferedImage getImageFromFile(String filePath) {
        File file = new File(filePath);
        LOGGER_BASE.debug("Read expected image from: " + file.getAbsolutePath());
        try {
            return ImageIO.read(file);
        } catch (IOException e) {
            LOGGER_BASE.debug("Failed to read image: " + filePath);
            return null;
        }
    }

    /**
     * Save buffered image.
     *
     * @param img          Image to be saved.
     * @param fileFullName Name of the image on disk (it will be saved in default screenshotOutDir).
     *                     For example: /Users/vchimev/Work/git/functional-tests/target/surefire-reports/screenshots/test_01_smoke.
     * @throws IOException When IO operation fails.
     */
    public void saveBufferedImage(BufferedImage img, String fileFullName)
            throws IOException {
        String imageFormat = "png";
        String fullImageFileName = fileFullName.endsWith("." + imageFormat) ? fileFullName : String.format("%s.%s", fileFullName, imageFormat);
        File file = new File(fullImageFileName);
        LOGGER_BASE.debug("Save Picture: " + file.getAbsolutePath());
        ImageIO.write(img, imageFormat, file);
    }

    /**
     * Save current screen.
     *
     * @param imageFullName Full name of the image on disk (it will be saved in default screenshotOutDir).
     *                      For example: /Users/vchimev/Work/git/functional-tests/target/surefire-reports/screenshots/test_01_smoke.
     * @throws AppiumException When fail to get screenshot.
     * @throws IOException     When fail to write image on disk.
     */
    public void saveScreen(String imageFullName)
            throws AppiumException, IOException {
        BufferedImage img = this.context.device.getScreenshot();
        this.saveBufferedImage(img, imageFullName);
    }

    /**
     * Save image verification result (actual, diff and expected images).
     *
     * @param result    ImageVerificationResult object.
     * @param imageName Name of the image. For example: test_01_smoke.
     * @throws IOException When fail to write images on disk.
     */
    public void saveImageVerificationResult(ImageVerificationResult result, String imageName)
            throws IOException {
        String actualImageName = imageName + result.actualSuffix;
        String diffImageName = imageName + result.diffSuffix;
        String expectedImageName = imageName + result.expectedSuffix;

        String actualImageFullName = this.getImageFullName(this.settings.screenshotOutDir, actualImageName);
        String diffImageFullName = this.getImageFullName(this.settings.screenshotOutDir, diffImageName);
        String expectedImageFullName = this.getImageFullName(this.settings.screenshotOutDir, expectedImageName);

        this.saveBufferedImage(result.actualImage, actualImageFullName);
        this.saveBufferedImage(result.diffImage, diffImageFullName);
        this.saveBufferedImage(result.expectedImage, expectedImageFullName);
    }

    /**
     * Get screenshot of UIElement.
     *
     * @param element UIElement object.
     * @return BufferedImage of specified UIElement.
     */
    public BufferedImage getElementImage(UIElement element) {
        BufferedImage img = this.getScreen();
        int screenWidth = 1;
        int screenHeight = 1;

        try {
            Dimension dimension = this.context.getDevice().getWindowSize();
            screenWidth = dimension.width;
            screenHeight = dimension.height;
        } catch (Exception e) {
            LOGGER_BASE.error(" Client.driver.manage().window() failed: " + e.getMessage());
        }

        int screenshotWidth = img.getWidth();
        int zoomFactor = screenshotWidth / screenWidth;

        Point point = element.getLocation();

        int width = element.getSize().getWidth() * zoomFactor;
        int height = element.getSize().getHeight() * zoomFactor;

        if (width > screenWidth * zoomFactor) {
            width = (screenWidth - point.getX()) * zoomFactor;
        }
        if (height > screenHeight * zoomFactor) {
            height = (screenHeight - point.getY()) * zoomFactor;
        }

        BufferedImage outputImage = img.getSubimage(point.getX() * zoomFactor,
                point.getY() * zoomFactor, width, height);

        return outputImage;
    }

    /**
     * Get current screen of mobile device.
     *
     * @return BufferedImage of current screen.
     */
    protected BufferedImage getScreen() {
        return this.context.device.getScreenshot();
    }

    /**
     * Get image folder path.
     *
     * @param appName Application name.
     * @return path to image folder for current application. For example: $STORAGE/images/uitests/Emulator-Api23-Default
     */
    protected String getImageFolderPath(String appName) {
        String imageFolderPath = this.settings.screenshotResDir + File.separator + appName + File.separator + this.settings.deviceName;
        LOGGER_BASE.debug("Image folder path: " + imageFolderPath);
        FileSystem.ensureFolderExists(imageFolderPath);
        return imageFolderPath;
    }

    /**
     * Get image full name.
     *
     * @param imageFolderPath Image folder of current application.
     * @param imageName       Image name.
     * @return Full path of image. For example: $STORAGE/images/uitests/Emulator-Api23-Default/flexbox_00_default.png
     */
    protected String getImageFullName(String imageFolderPath, String imageName) {
        String imageFullName = imageFolderPath + File.separator + imageName + ".png";
        LOGGER_BASE.debug("Image full name: " + imageFullName);
        return imageFullName;
    }
}
