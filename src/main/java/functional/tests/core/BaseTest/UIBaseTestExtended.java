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
        String testName = createImageName();
        return ImageVerification.compareScreens(testName, timeOut, 0, 0, 00.1);
    }

    public boolean waitForScreen(String imageName, int timeOut, double percentTolerance) throws Exception {
        return ImageVerification.compareScreens(imageName, timeOut, 0, 0, percentTolerance);
    }

    public boolean waitForScreen(int timeOut, double percentTolerance) throws Exception {
        String testName = createImageName();
        return ImageVerification.compareScreens(testName, timeOut, 0, 0, percentTolerance);
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

    public void compareScreens(int timeOut) throws Exception {
        this.compareScreens(timeOut, defaultWaitTime, 0, 0);
    }

    public boolean compareScreens(int timeOut, double percentTolerance) throws Exception {
        return compareScreens(timeOut, defaultWaitTime, Integer.MAX_VALUE, percentTolerance);
    }

    public boolean compareScreens(int timeOut, int wait, double percentTolerance) throws Exception {
        return compareScreens(timeOut, wait, Integer.MAX_VALUE, percentTolerance);
    }

    public void clearImagesResults() {
        this.imagesResults.clear();
        this.imageCounter = 1;
    }

    public <T> void assertEquals(T actual, T expected) {
        Assert.assertEquals(actual, expected, "assertEquals failed! Actual: " + actual + "; Expected: " + expected + ";");
        Log.info("assertEquals passed! Actual: " + actual + "; Expected: " + expected + ";");
    }

    public void assertImagesResults() {
        for (String imageName : this.imagesResults.keySet()) {
            Assert.assertTrue(this.imagesResults.get(imageName), String.format("%s test failed because image %s is not as actual", getTestName(), imageName));
        }
    }

    private boolean compareScreens(int timeOut, int waitTime, int pixelTolerance, double percentTolerance) throws Exception {
        String testName = createImageName();
        boolean result = ImageVerification.compareScreens(testName, timeOut, waitTime, pixelTolerance, percentTolerance);
        this.imagesResults.put(testName, result);
        this.imageCounter++;

        return result;
    }

    private void compareElements(UIElement element, int timeOut, int waitTime, int pixelTolerance, double percentTolerance) throws Exception {
        String testName = createImageName();
        this.compareElements(testName, element, timeOut, waitTime, pixelTolerance, percentTolerance);
    }

    private void compareElements(String imageName, UIElement element, int timeOut, int waitTime, int pixelTolerance, double percentTolerance) throws Exception {
        String testName = createImageName();
        boolean result = ImageVerification.compareElements(element, testName, timeOut, waitTime, pixelTolerance, percentTolerance);
        this.imagesResults.put(testName, result);
        this.imageCounter++;
    }

    private String createImageName() {
        return this.imageCounter <= 1 ? getTestName() : getTestName() + "_" + this.imageCounter;
    }
}
