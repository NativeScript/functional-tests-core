package functional.tests.core.ImageProcessing.SikuliImageProcessing;

import functional.tests.core.Appium.Client;
import org.opencv.core.Rect;
import org.sikuli.script.*;

/**
 * Created by tsenov on 7/5/16.
 */
public class SikuliImageProcessing {
    private Region region;

    public SikuliImageProcessing(Rect rectangle) {
        this.region = new Region(rectangle.x, rectangle.y, rectangle.height, rectangle.width);
    }

    public SikuliImageProcessing(String windowHandle) {
        App app = new App(windowHandle);
        this.region = app.window();

    }

    public void click(String imageFullName) {
        try {
            this.region.click();
            // s.wait("imgs/spotlight-input.png");
            this.region.type(null, "hello world\n", 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
