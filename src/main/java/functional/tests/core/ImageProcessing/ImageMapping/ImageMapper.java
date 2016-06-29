package functional.tests.core.ImageProcessing.ImageMapping;

import functional.tests.core.Appium.Client;
import functional.tests.core.ImageProcessing.ImageComparer.ImageVerification;
import functional.tests.core.ImageProcessing.ImageUtils;
import functional.tests.core.Log.Log;
import functional.tests.core.Settings.Settings;
import io.appium.java_client.TouchAction;
import org.opencv.core.Point;
import org.openqa.selenium.interactions.touch.TouchActions;
import org.testng.Assert;

import java.io.File;

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


//    public boolean tryTapImageOnScreen(String image) throws Exception {
//        Point[] imgRect = findImageOnScreenNoAssert(image);
//
//        if (imgRect == null) {
//            return false;
//        } else {
//            if (Settings.automationName.equals("selendroid")) {
//                selendroidTapAtCoordinate((int) imgRect[4].x, (int) imgRect[4].y, 1);
//            } else {
//                this.client.getDriver().tap(1, (int) imgRect[4].x, (int) imgRect[4].y, 1);
//            }
//            sleep(SHORT_SLEEP);
//            return true;
//        }
//    }

//    public boolean tryTapImageOnScreen(String image, int retries) throws Exception {
//        Point[] imgRect = findImageOnScreenNoAssert(image, retries);
//
//        if (imgRect == null) {
//            return false;
//        } else {
//            if (Settings.automationName.equals("selendroid")) {
//                selendroidTapAtCoordinate((int) imgRect[4].x, (int) imgRect[4].y, 1);
//            } else {
//                this.client.getDriver().tap(1, (int) imgRect[4].x, (int) imgRect[4].y, 1);
//            }
//            sleep(SHORT_SLEEP);
//            return true;
//        }
//    }

//    public boolean tryTapImageOnScreen(String image, double x_offset, double y_offset, int retries) throws Exception {
//        Point[] imgRect = findImageOnScreenNoAssert(image, retries);
//
//        if (imgRect == null) {
//            return false;
//        } else {
//            Point top_left = imgRect[0];
//            Point top_right = imgRect[1];
//            Point bottom_left = imgRect[2];
//
//            //adding the offset to each coordinate; if offset = 0.5, middle will be returned
//            double newX = top_left.x + (top_right.x - top_left.x) * x_offset;
//            double newY = top_left.y + (bottom_left.y - top_left.y) * y_offset;
//
//            if (Settings.automationName.equals("selendroid")) {
//                selendroidTapAtCoordinate((int) newX, (int) newY, 1);
//            } else {
//                this.client.getDriver().tap(1, (int) newX, (int) newY, 1);
//            }
//            return true;
//        }
//    }

//    public Point[] findImage(String image, String scene) {
//        return findImage(image, scene, rotation);
//    }
//
//    public Point[] findImage(String image, String scene, String setRotation) {
//        return findImage(image, scene, setRotation, 0.6);
//    }
//
//    public Point[] findImageOnScreen(String image, String setRotation) throws Exception {
//        return findImage(image, this.takeScreenshot(image), setRotation, 0.6);
//    }
//
//    public Point[] findImageOnScreen(String image, int retries) throws Exception {
//        return findImageOnScreen(image, retries, 0.6);
//    }
//    public Point[] findImageOnScreenNoAssert(String image) throws Exception {
//        int retries = 5;
//        Point[] imgRect = null;
//
//        while ((retries > 0) && (imgRect == null)) {
//            if (retries < 5) {
//                Log.info("Find image failed, retries left: " + retries);
//            }
//            takeScreenshot(image + "_screenshot");
//            imgRect = findImage(image, image + "_screenshot");
//            retries = retries - 1;
//        }
//        return imgRect;
//    }
//
//    public Point[] findImageOnScreenNoAssert(String image, int retries) throws Exception {
//        Point[] imgRect = null;
//
//        while ((retries > 0) && (imgRect == null)) {
//            if (retries < 5) {
//                Log.info("Find image failed, retries left: " + retries);
//            }
//            takeScreenshot(image + "_screenshot");
//            imgRect = findImage(image, image + "_screenshot");
//            retries = retries - 1;
//        }
//        return imgRect;
//    }

//    public Point[] findImageOnScreenAndSetRotation(String image) throws Exception {
//        //used to initially determine if the screenshots need to be rotated and by what degree
//        int retries = 5;
//        Point[] imgRect = null;
//        imgRect = findImage(image, image + "_screenshot", "notSet", retries); //this will identify the rotation initially
//        Assert.assertNotNull(imgRect, "Image " + image + " not found on screen.");
//        return imgRect;
//    }    public Point[] findImageOnScreenAndSetRotation(String image) throws Exception {
//        //used to initially determine if the screenshots need to be rotated and by what degree
//        int retries = 5;
//        Point[] imgRect = null;
//        imgRect = findImage(image, image + "_screenshot", "notSet", retries); //this will identify the rotation initially
//        Assert.assertNotNull(imgRect, "Image " + image + " not found on screen.");
//        return imgRect;
//    }

    public ActionImage findImageOnScreen(String image) throws Exception {
        return new ActionImage(findImageOnScreen(image, 5, 0), this.client);
    }


    public Point[] findImageOnScreen(String image, int retries, double tolerance) throws Exception {
        Point[] imgRect = null;

        while ((retries > 0) && (imgRect == null)) {
            Log.info("Find image started, retries left: " + retries);
            String screenshotName = image + screenShotsName;
            String sceneFullImagePath = this.takeScreenshot(screenshotName);
            imgRect = this.findImage(image, sceneFullImagePath, rotation, tolerance);
            retries--;
        }
        Assert.assertNotNull(imgRect, "Image " + image + " not found on screen.");

        return imgRect;
    }

    public String takeScreenshot(String screenshotName) throws Exception {
        counter++;
        String fullFileName = ImageUtils.getImageBaseFolder(this.appName, mappingsFolderName) + File.separator + screenshotName + "_" + counter;
        File file = new File(fullFileName + ".png");
        ImageUtils.saveBufferedImage(ImageUtils.getScreen(), file);

        return fullFileName;
    }

    private Point[] findImage(String searchedImageName, String sceneImageFullName, String setRotation, double tolerance) {
        String imageBaseFolder = ImageUtils.getImageBaseFolder(this.appName, this.mappingsFolderName);
        String searchedImageFullName = ImageUtils.getImageFullName(imageBaseFolder, searchedImageName);
//        String screenImageFullName = ImageUtils.getImageFullName(imageBaseFolder, image + "_screen");
        Log.info("In screen to search:" + sceneImageFullName);
        Log.info("Image to find:" + searchedImageFullName);
        ImageFinder imageFinder = new ImageFinder(rotation);
        Point[] imgRect = new Point[0];

        try {
            imgRect = imageFinder.findImage(searchedImageFullName, sceneImageFullName, tolerance);
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
