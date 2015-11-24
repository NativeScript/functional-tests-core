package common.Screenshot;

import common.Appium.Client;
import common.Log.Log;
import common.Settings.Settings;
import org.openqa.selenium.OutputType;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by vchimev on 11/2/2015.
 */
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
     */
    protected static BufferedImage getImageFromFile(String filePath)
            throws IOException {
        File file = new File(filePath);
        Log.debug("Read expected image from: " + file.getAbsolutePath());
        return ImageIO.read(file);
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
