package functional.tests.core.ImageProcessing.SikuliImageProcessing;

import com.sun.imageio.plugins.common.ImageUtil;
import functional.tests.core.Appium.Client;
import functional.tests.core.ImageProcessing.ImageUtils;
import org.opencv.core.Rect;
import org.sikuli.basics.Settings;
import org.sikuli.libsmac.*;
import org.sikuli.script.*;
import org.sikuli.script.Image;
import org.sikuli.script.ImageFinder;
import org.sikuli.script.Sikulix;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class SikuliImageProcessing {
    private Region region;
    private Rectangle rectangle;
    private String appName;
    private Client client;

    public SikuliImageProcessing(String appName, Client client) {
        this.appName = appName;
        this.client = client;
    }

    public UIRectangle findImageOnScreen(String imageFullName, double similarity, int index, int xOffset, int yOffset) {
        BufferedImage searchedBufferImage = ImageUtils.getImageFromFile(ImageUtils.getImageFullName(ImageUtils.getImageBaseFolder(this.appName), imageFullName));
        Image searchedImage = new Image(searchedBufferImage);
        Pattern searchedImagePattern = new Pattern(searchedImage);

        BufferedImage screenBufferImage = ImageUtils.getScreen();
        Image mainImage = new Image(screenBufferImage);

        try {
            ImageUtils.saveBufferedImage(screenBufferImage, "test");
        } catch (IOException e) {
            e.printStackTrace();
        }

        searchedImagePattern.similar((float) similarity);
        Finder finder = new Finder(mainImage);
        finder.findAll(searchedImage);
        Match searchedImageMatch = null;
        for (int i = 0; i <= index; i++) {

            if (i == index && finder.hasNext()) {
                searchedImageMatch = finder.next();
                i = index;
            }
        }
        Rectangle rectangle = searchedImageMatch.getCenter().getScreen().getRect();
        return new UIRectangle(rectangle, this.client);
    }
//
//    public SikuliImageProcessing findTextOnScreen(String text) {
//        Settings.InfoLogs = true;
//        Settings.OcrTextSearch = true;
//        Settings.OcrTextRead = true;
//
//        try {
//            ImageUtils.saveBufferedImage(ImageUtils.getScreen(), ImageUtils.getImageBaseFolder(this.appName, "test") + "test.png");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        Image mainImage = new Image(ImageUtils.getScreen());
//        Finder finder = new Finder(mainImage);
//        finder.findAllText(text);
//        Match searchedImageMatch = finder.next();
//
//        return new UIRectangle(searchedImageMatch.getCenter().getScreen().getRect(), this.client);
//    }


    public void longPress() {
        io.appium.java_client.TouchAction action = new io.appium.java_client.TouchAction(this.client.getDriver());
        action.longPress((int) this.rectangle.getX(), (int) this.rectangle.getY()).perform();
    }
}
