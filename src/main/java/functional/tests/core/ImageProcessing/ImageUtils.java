package functional.tests.core.ImageProcessing;

import functional.tests.core.Appium.Client;
import functional.tests.core.Element.UIElement;
import functional.tests.core.Exceptions.AppiumException;
import functional.tests.core.ImageProcessing.ImageComparer.ImageVerificationResult;
import functional.tests.core.Log.Log;
import functional.tests.core.OSUtils.FileSystem;
import functional.tests.core.Settings.Settings;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageUtils {

    /**
     * Get current screen.
     */
    public static BufferedImage getScreen() {
        try {
            File screen = Client.driver.getScreenshotAs(OutputType.FILE);
            return ImageIO.read(screen);
        } catch (Exception e) {
            Log.error("Failed to take screenshot! May be appium driver is dead.");
            return null;
        }
    }

    /**
     * Get image from file.
     * Returns null if image does not exist.
     */
    public static BufferedImage getImageFromFile(String filePath) {
        File file = new File(filePath);
        Log.debug("Read expected image from: " + file.getAbsolutePath());
        try {
            return ImageIO.read(file);
        } catch (IOException e) {
            Log.error("Failed to read image: " + filePath);
            return null;
        }
    }

    /**
     * Save buffered image.
     */
    public static void saveBufferedImage(BufferedImage img, File file)
            throws IOException {
        Log.debug("Save Picture: " + file.getAbsolutePath());
        ImageIO.write(img, "png", file);
    }

    /**
     * Save buffered image.
     */
    public static void saveBufferedImage(BufferedImage img, String fileName)
            throws IOException {
        File f = new File(Settings.screenshotOutDir + File.separator + fileName);
        Log.debug("Save Picture: " + f.getAbsolutePath());
        ImageIO.write(img, "png", f);
    }

    /**
     * Save current screen.
     **/
    public static void saveScreen(String imageName)
            throws AppiumException, IOException {
        BufferedImage img = ImageUtils.getScreen();
        ImageUtils.saveBufferedImage(img, imageName + ".png");
    }

    /**
     * Save current screen.
     **/
    public static void saveImageVerificationResult(ImageVerificationResult result, String filePrefix)
            throws IOException {
        ImageUtils.saveBufferedImage(result.actualImage, filePrefix + String.format("_%s.png", result.actualSuffix));
        ImageUtils.saveBufferedImage(result.diffImage, filePrefix + String.format("_%s.png", result.diffSuffix));
        ImageUtils.saveBufferedImage(result.expectedImage, filePrefix + String.format("_%s.png", result.expectedSuffix));
    }

    /**
     * Get MobileElement screenshot.
     */
    public static BufferedImage getElementImage(UIElement element) {
        BufferedImage img = getScreen();

        int screenWidth = Client.driver.manage().window().getSize().width;
        int screenHeight = Client.driver.manage().window().getSize().height;

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
     * Save MobileElement buffered image.
     */
    public static void saveElementImage(UIElement element, String fileName)
            throws IOException {
        BufferedImage img = getElementImage(element);
        saveBufferedImage(img, fileName);
    }

    public static String getImageBaseFolder(String appName, String... folders) {
        String expectedImageBaseFolderPath = Settings.screenshotResDir + File.separator + appName + File.separator + Settings.deviceName;
        if (folders != null && folders.length > 0) {
            for (String folder :
                    folders) {
                expectedImageBaseFolderPath += File.separator + folder;
            }
        }
        Log.info("Expected image base folder path: " + expectedImageBaseFolderPath);
        FileSystem.ensureFolderExists(expectedImageBaseFolderPath);

        return expectedImageBaseFolderPath;
    }

    public static String getImageFullName(String imageBaseFolderPath, String imageName) {
        String expectedImageFullName = imageBaseFolderPath + File.separator + imageName + ".png";
        Log.info("Expected image full name: " + expectedImageFullName);

        return expectedImageFullName;
    }
}
