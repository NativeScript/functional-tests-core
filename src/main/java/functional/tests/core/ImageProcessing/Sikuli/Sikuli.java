package functional.tests.core.ImageProcessing.Sikuli;

import functional.tests.core.Appium.Client;
import functional.tests.core.Element.UIRectangle;
import functional.tests.core.ImageProcessing.ImageUtils;
import org.sikuli.script.*;
import org.sikuli.script.Image;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Sikuli {
    private String appName;
    private Client client;

    public Sikuli(String appName, Client client) {
        this.appName = appName;
        this.client = client;
    }

    public UIRectangle findImageOnScreen(String imageFullName, double similarity) {
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
        finder.findAll(searchedImagePattern);

        Match searchedImageMatch = finder.next();
        Point point = searchedImageMatch.getCenter().getPoint();
        Rectangle rectangle = new Rectangle(point.x, point.y, 50, 50);

        return new UIRectangle(rectangle, this.client);
    }

//    public Sikuli findTextOnScreen(String text) {
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
}
