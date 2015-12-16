package functional.tests.core.Screenshot;

import functional.tests.core.Appium.Client;
import functional.tests.core.Log.Log;
import functional.tests.core.Settings.Settings;
import org.openqa.selenium.OutputType;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageUtils {

    /**
     * Get current screen.
     */
    protected static BufferedImage getScreen() {
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
}
