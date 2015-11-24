package common.Image;

import common.Appium.Client;
import common.Log.Log;
import common.Settings.Settings;
import io.appium.java_client.TouchAction;
import org.opencv.core.Point;
import org.openqa.selenium.interactions.touch.TouchActions;
import org.testng.Assert;

public class Image {

    public String rotation = "notSet";
    public static final int SHORT_SLEEP = 1;
    public static final int LONG_SLEEP = 10;
    public static int counter = 0;

    public Point[] findImage(String image, String scene, String setRotation) {
        return findImage(image, scene, setRotation, 0.6);
    }

    public Point[] findImage(String image, String scene, String setRotation, double tolerance) {
        //Log.info("Image to find: queryimages/" + screenshotsFolder + "/" + image + " in " + getScreenshotsFolder() + getScreenshotsCounter() + "_" + scene);
        ImageFinder imageFinder = new ImageFinder(rotation);
        //ImageFind imageFinder = new ImageFind(rotation);
        Point[] imgRect = new Point[0];

        try {
            //imgRect = imageFinder.findImage("queryimages/" + screenshotsFolder + "/" + image, getScreenshotsFolder() + getScreenshotsCounter() + "_" + scene, tolerance);
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
                org.openqa.selenium.Dimension size = Client.driver.manage().window().getSize();

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

    public Point[] findImage(String image, String scene) {
        return findImage(image, scene, rotation);
    }

    public void tapImage(String image, String scene) throws Exception {
        Point[] imgRect = findImage(image, scene);
        //imgRect[4] will have the center of the rectangle containing the image

        if (Settings.automationName.equals("selendroid")) {
            selendroidTapAtCoordinate((int) imgRect[4].x, (int) imgRect[4].y, 1);
        } else {
            Client.driver.tap(1, (int) imgRect[4].x, (int) imgRect[4].y, 1);
        }
        sleep(SHORT_SLEEP);
    }

    public void tapMiddle() throws Exception {
        org.openqa.selenium.Dimension size = Client.driver.manage().window().getSize();
        Point middle = new Point(size.getWidth() / 2, size.getHeight() / 2);

        if (Settings.automationName.equals("selendroid")) {
            selendroidTapAtCoordinate((int) middle.x, (int) middle.y, 1);
        } else {
            Client.driver.tap(1, (int) middle.x, (int) middle.y, 1);
        }
        sleep(SHORT_SLEEP);
    }

    public void tapAtCoordinates(int x, int y) throws Exception {
        if (Settings.automationName.equals("selendroid")) {
            selendroidTapAtCoordinate(x, y, 1);
        } else {
            Client.driver.tap(1, x, y, 1);
        }
        sleep(SHORT_SLEEP);
    }

    public void selendroidTapAtCoordinate(int x, int y, int secs) throws Exception {
        TouchActions actions = new TouchActions(Client.driver);
        actions.down(x, y).perform();
        sleep(secs);
        actions.up(x, y).perform();
    }

    public void selendroidTapAtImageOnScreen(String image, int secs) throws Exception {
        Point[] imgRect = findImageOnScreen(image);
        //imgRect[4] will have the center of the rectangle containing the image
        TouchActions actions = new TouchActions(Client.driver);
        actions.down((int) imgRect[4].x, (int) imgRect[4].y).perform();
        sleep(secs);
        actions.up((int) imgRect[4].x, (int) imgRect[4].y).perform();
    }

    public void tapImageOnScreen(String image) throws Exception {
        Point[] imgRect = findImageOnScreen(image);
        //imgRect[4] will have the center of the rectangle containing the image

        if (Settings.automationName.equals("selendroid")) {
            selendroidTapAtCoordinate((int) imgRect[4].x, (int) imgRect[4].y, 1);
        } else {
            Client.driver.tap(1, (int) imgRect[4].x, (int) imgRect[4].y, 1);
        }
        sleep(SHORT_SLEEP);
    }

    public void tapImageOnScreen(String image, int retries) throws Exception {
        Point[] imgRect = findImageOnScreen(image, retries);
        //imgRect[4] will have the center of the rectangle containing the image

        if (Settings.automationName.equals("selendroid")) {
            selendroidTapAtCoordinate((int) imgRect[4].x, (int) imgRect[4].y, 1);

        } else {
            Client.driver.tap(1, (int) imgRect[4].x, (int) imgRect[4].y, 1);
        }
    }

    public void tapImageOnScreen(String image, double x_offset, double y_offset) throws Exception {
        Point[] imgRect = findImageOnScreen(image);
        Point top_left = imgRect[0];
        Point top_right = imgRect[1];
        Point bottom_left = imgRect[2];

        //adding the offset to each coordinate; if offset = 0.5, middle will be returned
        double newX = top_left.x + (top_right.x - top_left.x) * x_offset;
        double newY = top_left.y + (bottom_left.y - top_left.y) * y_offset;

        if (Settings.automationName.equals("selendroid")) {
            selendroidTapAtCoordinate((int) newX, (int) newY, 1);
        } else {
            Client.driver.tap(1, (int) newX, (int) newY, 1);
        }
    }

    public void tapImageOnScreen(String image, double x_offset, double y_offset, int retries) throws Exception {
        tapImageOnScreen(image, x_offset, y_offset, retries, 0.6);
    }

    public void tapImageOnScreen(String image, double x_offset, double y_offset, int retries, double tolerance) throws Exception {
        Point[] imgRect = findImageOnScreen(image, retries, tolerance);
        Point top_left = imgRect[0];
        Point top_right = imgRect[1];
        Point bottom_left = imgRect[2];

        //adding the offset to each coordinate; if offset = 0.5, middle will be returned
        double newX = top_left.x + (top_right.x - top_left.x) * x_offset;
        double newY = top_left.y + (bottom_left.y - top_left.y) * y_offset;

        if (Settings.automationName.equals("selendroid")) {
            selendroidTapAtCoordinate((int) newX, (int) newY, 1);
        } else {
            Client.driver.tap(1, (int) newX, (int) newY, 1);
        }
    }

    public void tapImageOnScreenTwice(String image, double x_offset, double y_offset) throws Exception {
        Point[] imgRect = findImageOnScreen(image);
        Point top_left = imgRect[0];
        Point top_right = imgRect[1];
        Point bottom_left = imgRect[2];

        //adding the offset to each coordinate; if offset = 0.5, middle will be returned
        double newX = top_left.x + (top_right.x - top_left.x) * x_offset;
        double newY = top_left.y + (bottom_left.y - top_left.y) * y_offset;

        if (Settings.automationName.equals("selendroid")) {
            selendroidTapAtCoordinate((int) newX, (int) newY, 1);
            sleep(SHORT_SLEEP);
            selendroidTapAtCoordinate((int) newX, (int) newY, 1);
        } else {
            Client.driver.tap(1, (int) newX, (int) newY, 1);
            sleep(SHORT_SLEEP);
            Client.driver.tap(1, (int) newX, (int) newY, 1);
        }
    }

    public boolean tryTapImageOnScreen(String image) throws Exception {
        Point[] imgRect = findImageOnScreenNoAssert(image);

        if (imgRect == null) {
            return false;
        } else {
            if (Settings.automationName.equals("selendroid")) {
                selendroidTapAtCoordinate((int) imgRect[4].x, (int) imgRect[4].y, 1);
            } else {
                Client.driver.tap(1, (int) imgRect[4].x, (int) imgRect[4].y, 1);
            }
            sleep(SHORT_SLEEP);
            return true;
        }
    }

    public boolean tryTapImageOnScreen(String image, int retries) throws Exception {
        Point[] imgRect = findImageOnScreenNoAssert(image, retries);

        if (imgRect == null) {
            return false;
        } else {
            if (Settings.automationName.equals("selendroid")) {
                selendroidTapAtCoordinate((int) imgRect[4].x, (int) imgRect[4].y, 1);
            } else {
                Client.driver.tap(1, (int) imgRect[4].x, (int) imgRect[4].y, 1);
            }
            sleep(SHORT_SLEEP);
            return true;
        }
    }

    public boolean tryTapImageOnScreen(String image, double x_offset, double y_offset, int retries) throws Exception {
        Point[] imgRect = findImageOnScreenNoAssert(image, retries);

        if (imgRect == null) {
            return false;
        } else {
            Point top_left = imgRect[0];
            Point top_right = imgRect[1];
            Point bottom_left = imgRect[2];

            //adding the offset to each coordinate; if offset = 0.5, middle will be returned
            double newX = top_left.x + (top_right.x - top_left.x) * x_offset;
            double newY = top_left.y + (bottom_left.y - top_left.y) * y_offset;

            if (Settings.automationName.equals("selendroid")) {
                selendroidTapAtCoordinate((int) newX, (int) newY, 1);
            } else {
                Client.driver.tap(1, (int) newX, (int) newY, 1);
            }
            return true;
        }
    }

    public Point[] findImageOnScreen(String image) throws Exception {
        int retries = 5;
        Point[] imgRect = null;

        while ((retries > 0) && (imgRect == null)) {
            if (retries < 5) {
                Log.info("Find image failed, retries left: " + retries);
            }
            takeScreenshot(image + "_screenshot");
            imgRect = findImage(image, image + "_screenshot");
            retries = retries - 1;
        }
        Assert.assertNotNull(imgRect, "Image " + image + " not found on screen.");
        return imgRect;
    }

    public Point[] findImageOnScreen(String image, int retries) throws Exception {
        return findImageOnScreen(image, retries, 0.6);
    }

    public Point[] findImageOnScreen(String image, int retries, double tolerance) throws Exception {
        Point[] imgRect = null;

        while ((retries > 0) && (imgRect == null)) {
            Log.info("Find image started, retries left: " + retries);
            takeScreenshot(image + "_screenshot");
            imgRect = findImage(image, image + "_screenshot", rotation, tolerance);
            retries = retries - 1;
        }
        Assert.assertNotNull(imgRect, "Image " + image + " not found on screen.");
        return imgRect;
    }

    public Point[] findImageOnScreenNoAssert(String image) throws Exception {
        int retries = 5;
        Point[] imgRect = null;

        while ((retries > 0) && (imgRect == null)) {
            if (retries < 5) {
                Log.info("Find image failed, retries left: " + retries);
            }
            takeScreenshot(image + "_screenshot");
            imgRect = findImage(image, image + "_screenshot");
            retries = retries - 1;
        }
        return imgRect;
    }

    public Point[] findImageOnScreenNoAssert(String image, int retries) throws Exception {
        Point[] imgRect = null;

        while ((retries > 0) && (imgRect == null)) {
            if (retries < 5) {
                Log.info("Find image failed, retries left: " + retries);
            }
            takeScreenshot(image + "_screenshot");
            imgRect = findImage(image, image + "_screenshot");
            retries = retries - 1;
        }
        return imgRect;
    }

    public Point[] findImageOnScreenAndSetRotation(String image) throws Exception {
        //used to initially determine if the screenshots need to be rotated and by what degree
        int retries = 5;
        Point[] imgRect = null;

        while ((retries > 0) && (imgRect == null)) {
            if (retries < 5) {
                Log.info("Find image failed, retries left: " + retries);
            }
            takeScreenshot(image + "_screenshot");
            imgRect = findImage(image, image + "_screenshot", "notSet"); //this will identify the rotation initially
            retries = retries - 1;
        }
        Assert.assertNotNull(imgRect, "Image " + image + " not found on screen.");
        return imgRect;
    }

    public void swipeScreenWithImage(String image) throws Exception {
        swipeScreenWithImage(image, 1);
    }

    public void swipeScreenWithImage(String image, int repeats) throws Exception {
        //Point[] imgRect = findImageOnScreen(image, 10);
        sleep(SHORT_SLEEP);
        Point[] imgRect = findImageOnScreen(image, 10, 0.3);
        org.openqa.selenium.Dimension size = Client.driver.manage().window().getSize();

        if (Settings.automationName.equals("selendroid")) {
            TouchActions action = new TouchActions(Client.driver);
            action.down((int) imgRect[4].x, (int) imgRect[4].y).perform();

            double left_x = size.getWidth() * 0.20;
            double right_x = size.getWidth() * 0.80;
            double top_y = size.getHeight() * 0.20;

            //we will repeat the swiping based on "repeats" argument
            while (repeats > 0) {
                Log.info("Swiping screen with image in progress..");
                action.move((int) left_x, (int) top_y).perform();
                sleep(SHORT_SLEEP);
                //swiping horizontally
                int i = 1;
                while (top_y + i * 10 < size.getHeight() * 0.9) {
                    action.move((int) right_x, (int) (top_y + i * 10)).perform();
                    Thread.sleep(50);
                    action.move((int) left_x, (int) (top_y + i * 10)).perform();
                    Thread.sleep(50);
                    i = i + 1;
                }
                //swiping vertically
                i = 1;
                action.move((int) left_x, (int) top_y);
                while (left_x + i * 10 < right_x) {
                    action.move((int) (left_x + i * 10), (size.getHeight() - 1));
                    Thread.sleep(50);
                    action.move((int) (left_x + i * 10), (int) top_y);
                    Thread.sleep(50);
                    i = i + 1;
                }
                repeats = repeats - 1;
            }
            action.up(0, 0).perform();
        } else {
            TouchAction action = new TouchAction(Client.driver);
            action.press((int) imgRect[4].x, (int) imgRect[4].y).perform();

            double left_x = size.getWidth() * 0.20;
            double right_x = size.getWidth() * 0.80;
            double top_y = size.getHeight() * 0.20;

            //we will repeat the swiping based on "repeats" argument
            while (repeats > 0) {
                Log.info("Swiping screen with image in progress..");
                action.moveTo((int) left_x, (int) top_y).perform();
                sleep(SHORT_SLEEP);
                //swiping horizontally
                int i = 1;
                while (top_y + i * 20 < size.getHeight() * 0.9) {
                    action.moveTo((int) right_x, (int) (top_y + i * 20)).perform();
                    Thread.sleep(50);
                    action.moveTo((int) left_x, (int) (top_y + i * 20)).perform();
                    Thread.sleep(50);
                    i = i + 1;
                }
                //swiping vertically
                i = 1;
                action.moveTo((int) left_x, (int) top_y);
                while (left_x + i * 20 < right_x) {
                    action.moveTo((int) (left_x + i * 20), size.getHeight() - 1);
                    Thread.sleep(50);
                    action.moveTo((int) (left_x + i * 20), (int) top_y);
                    Thread.sleep(50);
                    i = i + 1;
                }
                repeats = repeats - 1;
            }
            action.release().perform();
        }
    }

    public void dragImage(String image, double x_offset, double y_offset) throws Exception {
        //drags image on screen using x and y offset from middle of the screen
        //0.5 offset => middle point
        Point[] imgRect = findImageOnScreen(image, 10);
        org.openqa.selenium.Dimension size = Client.driver.manage().window().getSize();
        Point point = new Point(size.getWidth() * x_offset, size.getHeight() * y_offset);
        Log.info("Dragging image to coordinates: " + point.toString());

        if (Settings.automationName.equals("selendroid")) {
            TouchActions action = new TouchActions(Client.driver);
            action.down((int) imgRect[4].x, (int) imgRect[4].y).perform();
            sleep(SHORT_SLEEP);
            action.move((int) point.x, (int) point.y).perform();
            action.up((int) point.x, (int) point.y).perform();
        } else {
            TouchAction action = new TouchAction(Client.driver);
            action.press((int) imgRect[4].x, (int) imgRect[4].y).perform();
            sleep(SHORT_SLEEP);
            action.moveTo((int) point.x, (int) point.y).perform();
            action.release().perform();
        }
    }

    public void sleep(int seconds) throws Exception {
        Thread.sleep(seconds * 1000);
    }

    public void takeScreenshot(String screenshotName) throws Exception {
        counter = counter + 1;
        //String fullFileName = System.getProperty("user.dir") + "/" + getScreenshotsFolder() + getScreenshotsCounter() + "_" + screenshotName + ".png";
        //Client.driver.takeScreenshot(fullFileName);
    }
}
