package functional.tests.core.ImageProcessing.Sikuli;

import functional.tests.core.Appium.Client;
import functional.tests.core.Element.UIRectangle;
import functional.tests.core.ImageProcessing.ImageUtils;
import org.sikuli.basics.Settings;
import org.sikuli.script.*;
import org.sikuli.script.Image;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Sikuli {
    private String appName;
    private Client client;

    public Sikuli(String appName, Client client) {
        this.appName = appName;
        this.client = client;
    }

    public UIRectangle findImageOnScreen(String imageName, double similarity) {
        BufferedImage screenBufferImage = ImageUtils.getScreen();

        Finder finder = getFinder(screenBufferImage, imageName, (float) similarity);

        Match searchedImageMatch = finder.next();
        Point point = searchedImageMatch.getCenter().getPoint();

        Rectangle rectangle = getRectangle(point, screenBufferImage.getWidth());

        return new UIRectangle(rectangle, this.client);
    }

    public UIRectangle[] findImagesOnScreen(String imageName, double similarity) {
        BufferedImage screenBufferImage = ImageUtils.getScreen();

        Finder finder = getFinder(screenBufferImage, imageName, (float) similarity);

        ArrayList<UIRectangle> rectangles = new ArrayList<>();

        while (finder.hasNext()) {
            Match searchedImageMatch = finder.next();
            Point point = searchedImageMatch.getCenter().getPoint();

            Rectangle rectangle = getRectangle(point, screenBufferImage.getWidth());

            rectangles.add(new UIRectangle(rectangle, this.client));
        }

        UIRectangle[] rectanglesArray = new UIRectangle[rectangles.size()];

        return rectangles.toArray(rectanglesArray);
    }


    public boolean waitForImage(String imageName, double similarity, int timeoutInSeconds) {
        BufferedImage screenBufferImage = ImageUtils.getScreen();
        timeoutInSeconds *= 1000;

        Finder finder = getFinder(screenBufferImage, imageName, (float) similarity);

        Match searchedImageMatch = finder.next();

        if (searchedImageMatch != null && searchedImageMatch.isValid()) {
            return true;
        }

        while (searchedImageMatch == null && timeoutInSeconds > 0) {
            Client.setWait(1000);
            timeoutInSeconds -= 1000;
            screenBufferImage = ImageUtils.getScreen();
            finder = getFinder(screenBufferImage, imageName, (float) similarity);

            searchedImageMatch = finder.next();
        }

        return timeoutInSeconds > 0 && searchedImageMatch != null && searchedImageMatch.isValid();
    }

    public UIRectangle findTextOnScreen(String text) {
        Settings.InfoLogs = true;
        Settings.OcrTextSearch = true;
        Settings.OcrTextRead = true;

        Image mainImage = new Image(ImageUtils.getScreen());
        Finder finder = new Finder(mainImage);
        finder.findAllText(text);
        Match searchedImageMatch = finder.next();

        Point point = searchedImageMatch.getCenter().getPoint();
        Rectangle rectangle = getRectangle(point, mainImage.getSize().width);

        return new UIRectangle(rectangle, this.client);
    }

    private Finder getFinder(BufferedImage screenBufferImage, String imageName, float similarity) {
        BufferedImage searchedBufferImage = ImageUtils.getImageFromFile(ImageUtils.getImageFullName(ImageUtils.getImageBaseFolder(this.appName), imageName));
        Image searchedImage = new Image(searchedBufferImage);
        Pattern searchedImagePattern = new Pattern(searchedImage);

        Image mainImage = new Image(screenBufferImage);

        searchedImagePattern.similar(similarity);

        Finder finder = new Finder(mainImage);
        finder.findAll(searchedImagePattern);

        return finder;
    }

    private Rectangle getRectangle(Point point, int screenShotWidth) {
        int densityRatio = this.client.getDensityRatio(screenShotWidth);

        Rectangle rectangle = new Rectangle(point.x / densityRatio, point.y / densityRatio, 50, 50);

        return rectangle;
    }
}
