package functional.tests.core.BaseTest;

import functional.tests.core.Element.UIElement;
import functional.tests.core.ImageProcessing.ImageVerification.ImageVerification;
import functional.tests.core.Log.Log;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;

import java.util.HashMap;
import java.util.Map;

public abstract class UIBaseTestExtended extends UIBaseTest {

    private int imageCounter = 1;
    private int defaultWaitTime = 1000;
    private double minPercentTolerant = 0.001;
    private Map<String, Boolean> imagesResults;

    @BeforeMethod(alwaysRun = true)
    public void initBeforeUIBaseTestExtended() {
        this.imagesResults = new HashMap<String, Boolean>();
        this.imageCounter = 1;
    }

    public boolean waitForElement(UIElement element, int timeOut, double percentTolerance) throws Exception {
        String testName = createImageName();
        return this.waitForElement(testName, element, timeOut, percentTolerance);
    }

    public boolean waitForElement(String imageName, UIElement element, int timeOut, double percentTolerance) throws Exception {
        return ImageVerification.compareElements(element, imageName, timeOut, 0, 0, percentTolerance);
    }

    public boolean waitForScreen(int timeOut) throws Exception {
        return this.waitForScreen(timeOut, 0.1);
    }

    public boolean waitForScreen(int timeOut, double percentTolerance) throws Exception {
        String testName = createImageName();
        return this.waitForScreen(testName, timeOut, percentTolerance);
    }

    public boolean waitForScreen(String imageName, int timeOut, double percentTolerance) throws Exception {
        return ImageVerification.compareScreens(imageName, timeOut, 0, 0, percentTolerance);
    }

    public void compareElements(UIElement element, int timeOut) throws Exception {
        this.compareElements(element, timeOut, defaultWaitTime, Integer.MAX_VALUE, minPercentTolerant);
    }

    public void compareElements(UIElement element, int timeOut, double percentTolerance) throws Exception {
        this.compareElements(element, timeOut, defaultWaitTime, Integer.MAX_VALUE, percentTolerance);
    }

    public void compareElements(String imageName, UIElement element, int timeOut, double percentTolerance) throws Exception {
        this.compareElements(imageName, element, timeOut, defaultWaitTime, Integer.MAX_VALUE, percentTolerance);
    }

    public void compareElements(UIElement element, int timeOut, int waitTime) throws Exception {
        this.compareElements(element, timeOut, waitTime, Integer.MAX_VALUE, minPercentTolerant);
    }

    public void compareScreens() throws Exception {
        this.compareScreens(1, defaultWaitTime, 0, 0); // As '0' does not do the trick.
    }

    public void compareScreens(int timeOut) throws Exception {
        this.compareScreens(timeOut, defaultWaitTime, 0, 0);
    }

    public boolean compareScreens(int timeOut, double percentTolerance) throws Exception {
        return compareScreens(timeOut, defaultWaitTime, Integer.MAX_VALUE, percentTolerance);
    }

    public boolean compareScreens(int timeOut, int wait, double percentTolerance) throws Exception {
        return compareScreens(timeOut, wait, Integer.MAX_VALUE, percentTolerance);
    }

    public boolean compareScreens(String name, int timeOut, double percentTolerance) throws Exception {
        boolean result = this.compareScreens(name, timeOut, 0, Integer.MAX_VALUE, percentTolerance);

        return result;
    }

    public void clearImagesResults() {
        this.imagesResults.clear();
        this.imageCounter = 1;
    }

    public void assertImagesResults() {
        for (String imageName : this.imagesResults.keySet()) {
            Assert.assertTrue(this.imagesResults.get(imageName), String.format("%s test failed because image %s is not as actual", getTestName(), imageName));
        }
    }

    private boolean compareScreens(int timeOut, int waitTime, int pixelTolerance, double percentTolerance) throws Exception {
        String testName = createImageName();
        boolean result = this.compareScreens(testName, timeOut, waitTime, pixelTolerance, percentTolerance);

        return result;
    }

    private boolean compareScreens(String name, int timeOut, int waitTime, int pixelTolerance, double percentTolerance) throws Exception {
        boolean result = ImageVerification.compareScreens(name, timeOut, waitTime, pixelTolerance, percentTolerance);
        this.imagesResults.put(name, result);
        this.imageCounter++;

        return result;
    }

    private void compareElements(UIElement element, int timeOut, int waitTime, int pixelTolerance, double percentTolerance) throws Exception {
        String testName = createImageName();
        this.compareElements(testName, element, timeOut, waitTime, pixelTolerance, percentTolerance);
    }

    private void compareElements(String imageName, UIElement element, int timeOut, int waitTime, int pixelTolerance, double percentTolerance) throws Exception {
        boolean result = ImageVerification.compareElements(element, imageName, timeOut, waitTime, pixelTolerance, percentTolerance);
        this.imagesResults.put(imageName, result);
        this.imageCounter++;
    }

    private String createImageName() {
        return this.imageCounter <= 1 ? getTestName() : getTestName() + "_" + this.imageCounter;
    }
}
