package functional.tests.core.ImageProcessing.OpneCV;

import functional.tests.core.Appium.Client;
import functional.tests.core.ImageProcessing.ImageUtils;
import functional.tests.core.Log.Log;
import functional.tests.core.Settings.Settings;
import org.opencv.core.Point;
import org.testng.Assert;

public class ImageMapper {

    private final Client client;
    private String appName;
    private static String mappingsFolderName = "mappings";
    private static String screenShotsName = "_screenshoot";
    public String rotation = "notSet";
    public static final int SHORT_SLEEP = 1;
    public static final int LONG_SLEEP = 10;
    public static int counter = 0;


    public ImageMapper(Client client, String appName) {
        this.client = client;
        this.appName = appName;
    }

    public ActionImage findImageOnScreen(String image, double tolerance) throws Exception {
        return new ActionImage(findImageOnScreen(image, 5, tolerance), this.client);
    }


    public Point[] findImageOnScreen(String searchedImageimage, int retries, double tolerance) throws Exception {
        Point[] imgRect = null;

        while ((retries > 0) && (imgRect == null)) {
            Log.info("Find image started, retries left: " + retries);
            String screenshotName = searchedImageimage + screenShotsName;
            String image = this.takeScreenshot(screenshotName);
            imgRect = this.findImage(searchedImageimage, image, rotation, tolerance);
            retries--;
        }
        Assert.assertNotNull(imgRect, "Image " + searchedImageimage + " not found on screen.");

        return imgRect;
    }

    public String takeScreenshot(String screenshotName) throws Exception {
//        counter++;
//        String fullFileName = ImageUtils.getImageBaseFolder(this.appName, mappingsFolderName) + File.separator + screenshotName + "_" + counter;
//        File file = new File(fullFileName + ".png");
//        ImageUtils.saveBufferedImage(ImageUtils.getScreen(), file);
//        OSUtils.getScreenshot(file);

        String fullFileName = "/Users/tsenov/git/functional-tests-new/resources/images/testsappng/Emulator-Api23-Default/mappings/geometric_shapes.png";

        return fullFileName;
    }

    private Point[] findImage(String searchedImageName, String image, String setRotation, double tolerance) {
        String imageBaseFolder = ImageUtils.getImageBaseFolder(this.appName, this.mappingsFolderName);
        String searchedImageFullName = ImageUtils.getImageFullName(imageBaseFolder, searchedImageName);
        Log.info("In screen to search:" + image);
        Log.info("Image to find:" + searchedImageFullName);
        ImageFinder imageFinder = new ImageFinder(rotation);
        Point[] imgRect = new Point[0];

        try {
            imgRect = imageFinder.findImage(searchedImageFullName, image, tolerance);
        } catch (Exception e) {
            e.printStackTrace();
        }
        rotation = imageFinder.getRotation();

        if (imgRect != null) {
            //for retina devices we need to recalculate coordinates
            if ((Settings.deviceName.contains("iPhone"))
                    || (Settings.deviceName.contains("iPad 3"))
                    || (Settings.deviceName.contains("iPad 4"))
                    || (Settings.deviceName.contains("iPad Air"))) {
                Point[] imgRectScaled = new Point[]{new Point(imgRect[0].x / 2, imgRect[0].y / 2), new Point(imgRect[1].x / 2, imgRect[1].y / 2), new Point(imgRect[2].x / 2, imgRect[2].y / 2), new Point(imgRect[3].x / 2, imgRect[3].y / 2), new Point(imgRect[4].x / 2, imgRect[4].y / 2)};
                Log.info("Device with Retina display => co-ordinates have been recalculated");
                return imgRectScaled;
            } else {

                Point center = imgRect[4];
                org.openqa.selenium.Dimension size = this.client.getDriver().manage().window().getSize();

                if ((center.x >= size.width) || (center.x < 0) || (center.y >= size.height) || (center.y < 0)) {
                    Log.info("Screen size is (width, height): " + size.getWidth() + ", " + size.getHeight());
                    Log.info("WARNING: Coordinates found do not match the screen --> image not found.");
                    //     return imgRect;
                }
                return imgRect;
            }
        } else {
            return null;
        }
    }
}