package functional.tests.core.image;

import functional.tests.core.enums.PlatformType;
import functional.tests.core.mobile.appium.Client;
import functional.tests.core.mobile.element.UIRectangle;
import org.sikuli.basics.Settings;
import org.sikuli.script.Finder;
import org.sikuli.script.Image;
import org.sikuli.script.Match;
import org.sikuli.script.Pattern;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * TODO(): Add docs.
 */
public class Sikuli {
    private String appName;
    private Client client;
    private ImageUtils imageUtils;

    /**
     * TODO(): Add docs.
     *
     * @param appName
     */
    public Sikuli(String appName, Client client, ImageUtils imageUtils) {
        this.appName = appName;
        this.client = client;
        this.imageUtils = imageUtils;
    }

    /**
     * TODO(): Add docs.
     *
     * @param imageName
     * @param similarity
     * @return
     */
    public UIRectangle findImageOnScreen(String imageName, double similarity) {
        BufferedImage screenBufferImage = this.imageUtils.getScreen();

        Finder finder = this.getFinder(screenBufferImage, imageName, (float) similarity);

        Match searchedImageMatch = finder.next();
        Point point = searchedImageMatch.getCenter().getPoint();

        Rectangle rectangle = this.getRectangle(point, screenBufferImage.getWidth());

        return new UIRectangle(rectangle);
    }

    /**
     * TODO(): Add docs.
     *
     * @param imageName
     * @param similarity
     * @return
     */
    public UIRectangle[] findImagesOnScreen(String imageName, double similarity) {
        BufferedImage screenBufferImage = this.imageUtils.getScreen();

        Finder finder = this.getFinder(screenBufferImage, imageName, (float) similarity);

        ArrayList<UIRectangle> rectangles = new ArrayList<>();

        while (finder.hasNext()) {
            Match searchedImageMatch = finder.next();
            Point point = searchedImageMatch.getCenter().getPoint();

            Rectangle rectangle = this.getRectangle(point, screenBufferImage.getWidth());

            rectangles.add(new UIRectangle(rectangle));
        }

        UIRectangle[] rectanglesArray = new UIRectangle[rectangles.size()];

        return rectangles.toArray(rectanglesArray);
    }

    /**
     * TODO(): Add docs.
     *
     * @param imageName
     * @param similarity
     * @param timeoutInSeconds
     * @return
     */
    public boolean waitForImage(String imageName, double similarity, int timeoutInSeconds) {
        BufferedImage screenBufferImage = this.imageUtils.getScreen();
        timeoutInSeconds *= 1000;

        Finder finder = this.getFinder(screenBufferImage, imageName, (float) similarity);

        Match searchedImageMatch = finder.next();

        if (searchedImageMatch != null && searchedImageMatch.isValid()) {
            return true;
        }

        while (searchedImageMatch == null && timeoutInSeconds > 0) {
            this.client.setWait(1000);
            timeoutInSeconds -= 1000;
            screenBufferImage = this.imageUtils.getScreen();
            finder = this.getFinder(screenBufferImage, imageName, (float) similarity);

            searchedImageMatch = finder.next();
        }

        return timeoutInSeconds > 0 && searchedImageMatch != null && searchedImageMatch.isValid();
    }

    /**
     * TODO(): Add docs.
     *
     * @param text
     * @return
     */
    public UIRectangle findTextOnScreen(String text) {
        Settings.InfoLogs = true;
        Settings.OcrTextSearch = true;
        Settings.OcrTextRead = true;

        Image mainImage = new Image(this.imageUtils.getScreen());
        Finder finder = new Finder(mainImage);
        finder.findAllText(text);
        Match searchedImageMatch = finder.next();

        Point point = searchedImageMatch.getCenter().getPoint();
        Rectangle rectangle = this.getRectangle(point, mainImage.getSize().width);

        return new UIRectangle(rectangle);
    }

    /**
     * TODO(): Add docs.
     *
     * @param screenBufferImage
     * @param imageName
     * @param similarity
     * @return
     */
    private Finder getFinder(BufferedImage screenBufferImage, String imageName, float similarity) {
        BufferedImage searchedBufferImage = this.imageUtils.getImageFromFile(this.imageUtils.getImageFullName(this.imageUtils.getImageFolderPath(this.appName), imageName));
        Image searchedImage = new Image(searchedBufferImage);
        Pattern searchedImagePattern = new Pattern(searchedImage);

        Image mainImage = new Image(screenBufferImage);

        searchedImagePattern.similar(similarity);

        Finder finder = new Finder(mainImage);
        finder.findAll(searchedImagePattern);

        return finder;
    }

    /**
     * TODO(): Add docs.
     *
     * @param point
     * @param screenShotWidth
     * @return
     */
    private Rectangle getRectangle(Point point, int screenShotWidth) {
        int densityRatio = this.getDensityRatio(screenShotWidth);

        Rectangle rectangle = new Rectangle(point.x / densityRatio, point.y / densityRatio, 50, 50);

        return rectangle;
    }

    /**
     * TODO(): Add docs.
     *
     * @param screenshotWidth
     * @return
     */
    private int getDensityRatio(int screenshotWidth) {
        if (this.client.settings.platform == PlatformType.iOS) {
            return screenshotWidth / this.client.driver.manage().window().getSize().width;
        } else {
            return 1;
        }
    }
}
