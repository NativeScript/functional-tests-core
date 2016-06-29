package functional.tests.core.ImageProcessing.ImageMapping;


import functional.tests.core.Appium.Client;
import functional.tests.core.Log.Log;
import functional.tests.core.Settings.Settings;
import io.appium.java_client.TouchAction;
import org.opencv.core.Point;
import org.openqa.selenium.interactions.touch.TouchActions;

/**
 * Created by tsenov on 6/29/16.
 */
public class ActionImage {

    private static String mappingsFolderName = "mappings";
    private static String screenShotsName = "screenshoot";
    public String rotation = "notSet";
    public static final int SHORT_SLEEP = 1;
    public static final int LONG_SLEEP = 10;
    public static int counter = 0;

    private Point[] imgRect;
    private Client client;

    public ActionImage(Point[] imagePoints, Client client) {
        this.imgRect = imagePoints;
        this.client = client;
    }

    public void tapImage() throws Exception {

        if (Settings.automationName.equals("selendroid")) {
            selendroidTapAtCoordinate((int) imgRect[4].x, (int) imgRect[4].y, 1);
        } else {
            this.client.getDriver().tap(1, (int) this.imgRect[4].x, (int) this.imgRect[4].y, 1);
        }
        sleep(SHORT_SLEEP);
    }

    public void tapMiddleOffScreen() throws Exception {
        org.openqa.selenium.Dimension size = this.client.getDriver().manage().window().getSize();
        Point middle = new Point(size.getWidth() / 2, size.getHeight() / 2);

        if (Settings.automationName.equals("selendroid")) {
            this.selendroidTapAtCoordinate((int) middle.x, (int) middle.y, 1);
        } else {
            this.client.getDriver().tap(1, (int) middle.x, (int) middle.y, 1);
        }
        sleep(SHORT_SLEEP);
    }

    public void tapAtCoordinates(int x, int y) throws Exception {
        if (Settings.automationName.equals("selendroid")) {
            selendroidTapAtCoordinate(x, y, 1);
        } else {
            this.client.getDriver().tap(1, x, y, 1);
        }
        sleep(SHORT_SLEEP);
    }


    public void selendroidTapAtCoordinate(int x, int y, int secs) throws Exception {
        TouchActions actions = new TouchActions(this.client.getDriver());
        actions.down(x, y).perform();
        sleep(secs);
        actions.up(x, y).perform();
    }

    public void selendroidTapAtImageOnScreen(int secs) throws Exception {
        //imgRect[4] will have the center of the rectangle containing the image
        TouchActions actions = new TouchActions(this.client.getDriver());
        actions.down((int) imgRect[4].x, (int) imgRect[4].y).perform();
        sleep(secs);
        actions.up((int) imgRect[4].x, (int) imgRect[4].y).perform();
    }

    public void tapImageOnScreen() throws Exception {
        //imgRect[4] will have the center of the rectangle containing the image

        if (Settings.automationName.equals("selendroid")) {
            selendroidTapAtCoordinate((int) imgRect[4].x, (int) imgRect[4].y, 1);
        } else {
            this.client.getDriver().tap(1, (int) imgRect[4].x, (int) imgRect[4].y, 1);
        }
        sleep(SHORT_SLEEP);
    }

    public void tapImageOnScreen(double x_offset, double y_offset) throws Exception {
        Point top_left = imgRect[0];
        Point top_right = imgRect[1];
        Point bottom_left = imgRect[2];

        //adding the offset to each coordinate; if offset = 0.5, middle will be returned
        double newX = top_left.x + (top_right.x - top_left.x) * x_offset;
        double newY = top_left.y + (bottom_left.y - top_left.y) * y_offset;

        if (Settings.automationName.equals("selendroid")) {
            selendroidTapAtCoordinate((int) newX, (int) newY, 1);
        } else {
            this.client.getDriver().tap(1, (int) newX, (int) newY, 1);
        }
    }

    public void tapImageOnScreenTwice(double x_offset, double y_offset) throws Exception {
        this.tapImageOnScreen(x_offset, y_offset);
        sleep(SHORT_SLEEP);
        this.tapImageOnScreen(x_offset, y_offset);
    }

    public void swipeScreenWithImage( int repeats) throws Exception {
        //Point[] imgRect = findImageOnScreen(image, 10);
        sleep(SHORT_SLEEP);
        org.openqa.selenium.Dimension size = this.client.getDriver().manage().window().getSize();

        if (Settings.automationName.equals("selendroid")) {
            TouchActions action = new TouchActions(this.client.getDriver());
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
            TouchAction action = new TouchAction(this.client.getDriver());
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

    public void dragImage(double x_offset, double y_offset) throws Exception {
        //drags image on screen using x and y offset from middle of the screen
        //0.5 offset => middle point
        org.openqa.selenium.Dimension size = this.client.getDriver().manage().window().getSize();
        Point point = new Point(size.getWidth() * x_offset, size.getHeight() * y_offset);
        Log.info("Dragging image to coordinates: " + point.toString());

        if (Settings.automationName.equals("selendroid")) {
            TouchActions action = new TouchActions(this.client.getDriver());
            action.down((int) imgRect[4].x, (int) imgRect[4].y).perform();
            sleep(SHORT_SLEEP);
            action.move((int) point.x, (int) point.y).perform();
            action.up((int) point.x, (int) point.y).perform();
        } else {
            TouchAction action = new TouchAction(this.client.getDriver());
            action.press((int) imgRect[4].x, (int) imgRect[4].y).perform();
            sleep(SHORT_SLEEP);
            action.moveTo((int) point.x, (int) point.y).perform();
            action.release().perform();
        }
    }

    public void sleep(int seconds) throws Exception {
        Thread.sleep(seconds * 1000);
    }

}
