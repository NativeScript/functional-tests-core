package functional.tests.core.Screenshot;

import functional.tests.core.Appium.Client;
import functional.tests.core.Exceptions.AppiumException;
import functional.tests.core.Log.Log;
import functional.tests.core.Settings.Settings;
import io.appium.java_client.MobileElement;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageUtils {
    private Client client;

    public ImageUtils(Client client) {
        this.client = client;
    }

    /**
     * Get current screen.
     */
    protected BufferedImage getScreen() {
        try {
            File screen = this.client.driver.getScreenshotAs(OutputType.FILE);
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
    protected static BufferedImage getImageFromFile(String filePath) {
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
    protected static void saveBufferedImage(BufferedImage img, File file)
            throws IOException {
        Log.debug("Save Picture: " + file.getAbsolutePath());
        ImageIO.write(img, "png", file);
    }

    /**
     * Save buffered image.
     */
    protected static void saveBufferedImage(BufferedImage img, String fileName)
            throws IOException {
        File f = new File(Settings.screenshotOutDir + File.separator + fileName);
        Log.debug("Save Picture: " + f.getAbsolutePath());
        ImageIO.write(img, "png", f);
    }

    /**
     * Save current screen.
     **/
    public void saveScreen(String imageName)
            throws AppiumException, IOException {
        BufferedImage img = this.getScreen();
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
    protected BufferedImage getElementImage(MobileElement element) {
        BufferedImage img = getScreen();

        int screenWidth = this.client.driver.manage().window().getSize().width;
        int screenHeight = this.client.driver.manage().window().getSize().height;

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
    protected void saveElementImage(MobileElement element, String fileName)
            throws IOException {
        BufferedImage img = getElementImage(element);
        saveBufferedImage(img, fileName);
    }
}
