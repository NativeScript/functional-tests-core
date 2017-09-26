package functional.tests.core.mobile.basetest;

import functional.tests.core.enums.PlatformType;
import functional.tests.core.image.ImageUtils;
import functional.tests.core.image.ImageVerification;
import functional.tests.core.image.Sikuli;
import functional.tests.core.log.Log;
import functional.tests.core.log.LoggerBase;
import functional.tests.core.mobile.app.App;
import functional.tests.core.mobile.appium.Client;
import functional.tests.core.mobile.appium.Server;
import functional.tests.core.mobile.device.Device;
import functional.tests.core.mobile.element.UIElement;
import functional.tests.core.mobile.find.Find;
import functional.tests.core.mobile.find.Locators;
import functional.tests.core.mobile.find.UIElementClass;
import functional.tests.core.mobile.find.Wait;
import functional.tests.core.mobile.gestures.Gestures;
import functional.tests.core.mobile.settings.MobileDoctor;
import functional.tests.core.mobile.settings.MobileSettings;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Inherit this class in case to run tests on mobile devices.
 */
public abstract class MobileTest {

    private static final LoggerBase LOGGER = LoggerBase.getLogger("MobileTest");
    public MobileContext context;
    public Client client;
    public Device device;
    public Find find;
    public Gestures gestures;
    public App app;
    public Wait wait;
    public ImageUtils imageUtils;
    protected MobileSetupManager mobileSetupManager;
    protected MobileSettings settings;
    protected Locators locators;
    protected Log log;
    protected ImageVerification imageVerification;
    protected UIElementClass uiElements;
    private int imageCounter = 1;
    private int defaultWaitTime = 1000;
    private int maxPixelTolerance = Integer.MAX_VALUE;
    private double minPercentTolerant = 0.003D;
    private boolean firstTest;
    private Map<String, Boolean> imagesResults;
    private Sikuli sikuliImageProcessing;
    private Server server;

    /**
     * Init UI Tests setup.
     */
    public MobileTest() {
        this.initUITestHelpers();
        this.imageVerification = new ImageVerification();
    }

    /**
     * TODO(): Add docs.
     *
     * @return
     */
    protected Sikuli getSikuliImagePorcessing() {
        return this.sikuliImageProcessing;
    }

    /**
     * Executed before suite with UI Tests.
     *
     * @throws Exception
     */
    @BeforeSuite(alwaysRun = true)
    public void beforeSuiteUIBaseTest() throws Exception {

        // Run doctor to detect issues with setup and settings.
        if (this.settings.debug) {
            this.log.info("[DEBUG MODE] Skip doctor.");
        } else {
            MobileDoctor mobileDoctor = new MobileDoctor(this.settings);
            mobileDoctor.check();
        }

        // Start Appium server and init device (this include Appium client start)
        this.mobileSetupManager.initServer();
        this.mobileSetupManager.initDevice();

        // Mark this test as first in suite
        this.firstTest = true;
    }

    /**
     * Executed before each UI Test method.
     *
     * @throws Exception
     */
    @BeforeClass(alwaysRun = true)
    public void beforeMethodUIBaseClass() throws Exception {
        // Perform set of actions on test fail.
        if (this.context.lastTestResult == ITestResult.FAILURE && this.context.shouldRestartAppOnFailure) {
            try {
                // Restart app under test
                this.context.app.restart();
                this.context.shouldRestartAppOnFailure = false;
            } catch (Exception e) {
                // Restart might fail if server or client are dead
                this.mobileSetupManager.restartSession();
            }
        }
    }

    /**
     * Executed before each UI Test method.
     *
     * @param method
     * @throws Exception
     */
    @BeforeMethod(alwaysRun = true)
    public void beforeMethodUIBaseTest(Method method) throws Exception {

        this.context.setTestName(method.getName());
        this.log.separator();
        this.log.info("Start test: " + method.getName());

        // Perform actions when previous test passed.
        if (this.context.lastTestResult == ITestResult.SUCCESS && this.settings.restartApp && !this.firstTest) {
            this.context.app.restart();
        }

        // Perform set of actions on test fail.
        if (this.context.lastTestResult == ITestResult.FAILURE && this.context.shouldRestartAppOnFailure) {
            try {
                // Restart app under test
                this.context.app.restart();
            } catch (Exception e) {
                // Restart might fail if server or client are dead
                this.mobileSetupManager.restartSession();
            }

            // Reset navigation state manager
            if (this.context.navigationManager != null && this.context.shouldRestartAppOnFailure) {
                this.context.navigationManager.resetNavigationToLastOpenedPage();
            } else {
                LOGGER.error("TestStateManager is: " + this.context.navigationManager + " in beforeMethodUIBaseTest!");
            }
        }

        // First test is already started, so set this.firstTest = false;
        this.firstTest = false;

        this.imagesResults = new HashMap<String, Boolean>();
        this.imageCounter = 1;
        if (this.settings.isRealDevice && this.settings.platform == PlatformType.iOS) {
            this.device.getIOSDevice().startIOSRealDeviceLogWatcher();
        }
    }

    /**
     * Executed after each UI Test.
     *
     * @param result
     * @throws IOException
     */
    @AfterMethod(alwaysRun = true)
    public void afterMethodUIBaseTest(ITestResult result) throws IOException {

        // Log memory usage and assert it is less than memoryMaxUsageLimit (if memoryMaxUsageLimit is set)
        this.checkMemoryPerformance(result);

        this.context.lastTestResult = result.getStatus();
        this.mobileSetupManager.logTestResult(this.context.lastTestResult, this.context.getTestName());
    }

    /**
     * Navigate to appropriate page.
     */
    @AfterClass(alwaysRun = true)
    public void afterClassUIBaseTest() {
        if (this.context.navigationManager != null) {
            try {
                this.context.navigationManager.navigateToHomePage();
            } catch (Exception ex) {
                LOGGER.error(ex.getMessage());
            }

            this.context.navigationManager = null;
        }
    }

    /**
     * Executed after all tests complete.
     *
     * @throws Exception
     */
    @AfterSuite(alwaysRun = true)
    public void afterSuiteUIBaseTest() throws Exception {
        try {
            this.context.device.logPerfInfo();
            this.mobileSetupManager.stopSession();
            this.mobileSetupManager.cleanDevice();
        } catch (Exception e) {
            this.context.device.stop();
            throw e;
        }
    }


    /**
     * Get memory usage and log it.
     * Assert memory usage is less than memoryMaxUsageLimit (if memoryMaxUsageLimit is specified).
     *
     * @param result
     */
    protected void checkMemoryPerformance(ITestResult result) {
        if (this.settings.platform == PlatformType.Android) {
            int usedMemory = this.device.getMemUsage(this.settings.packageId);
            if (usedMemory > -1) {
                LOGGER.info("Used memory: " + usedMemory);

                int currentMaxMem = this.context.device.getAndroidDevice().getMaxUsedMemory();
                if (currentMaxMem < usedMemory) {
                    currentMaxMem = usedMemory;
                    this.context.device.getAndroidDevice().setMaxUsedMemory(currentMaxMem);
                    LOGGER.debug("Maximum used memory: " + currentMaxMem);
                }

                if (this.settings.android.memoryMaxUsageLimit > 0) {
                    LOGGER.info("Expected max memory usage: " + this.settings.android.memoryMaxUsageLimit);
                    if (this.settings.android.memoryMaxUsageLimit < usedMemory) {
                        LOGGER.error("=== Memory leak appears after test " + result.getName() + " ====");
                        Assert.assertTrue(false, "Used memory of " + usedMemory + " is more than expected " + this.settings.android.memoryMaxUsageLimit + " !!!");
                        result.setStatus(ITestResult.FAILURE);
                    }
                }
            } else {
                LOGGER.error("Failed to get memory usage stats.");
            }
        } else {
            this.log.debug("Check performance not implemented for iOS.");
        }
    }

    /**
     * TODO(): Add docs.
     *
     * @param element
     * @param timeOut
     * @throws Exception
     */
    public void compareElements(UIElement element, int timeOut) throws Exception {
        this.compareElements(element, timeOut, this.defaultWaitTime, Integer.MAX_VALUE, this.minPercentTolerant);
    }

    /**
     * TODO(): Add docs.
     *
     * @param element
     * @param timeOut
     * @param percentTolerance
     * @throws Exception
     */
    public void compareElements(UIElement element, int timeOut, double percentTolerance) throws Exception {
        this.compareElements(element, timeOut, this.defaultWaitTime, Integer.MAX_VALUE, percentTolerance);
    }

    /**
     * TODO(): Add docs.
     *
     * @param imageName
     * @param element
     * @param timeOut
     * @param percentTolerance
     * @throws Exception
     */
    public void compareElements(String imageName, UIElement element, int timeOut, double percentTolerance) throws Exception {
        this.compareElements(imageName, element, timeOut, this.defaultWaitTime, Integer.MAX_VALUE, percentTolerance);
    }

    /**
     * TODO(): Add docs.
     *
     * @param element
     * @param timeOut
     * @param waitTime
     * @throws Exception
     */
    public void compareElements(UIElement element, int timeOut, int waitTime) throws Exception {
        this.compareElements(element, timeOut, waitTime, Integer.MAX_VALUE, this.minPercentTolerant);
    }

    /**
     * Compare the current screen.
     *
     * @throws Exception
     */
    public void compareScreens() throws Exception {
        this.compareScreens(1, this.defaultWaitTime, this.maxPixelTolerance, this.minPercentTolerant);
    }

    /**
     * Assert the current screen.
     *
     * @throws Exception
     */
    public void assertScreen() throws Exception {
        this.compareScreens(1, this.defaultWaitTime, this.maxPixelTolerance, this.minPercentTolerant);
        this.assertImagesResults();
    }

    /**
     * Compare the current screen.
     *
     * @param name of the image
     * @throws Exception
     */
    public void compareScreens(String name) throws Exception {
        this.compareScreens(name, 1, this.defaultWaitTime, this.maxPixelTolerance, this.minPercentTolerant);
    }

    /**
     * Assert the current screen.
     *
     * @param name of the image
     * @throws Exception
     */
    public void assertScreen(String name) throws Exception {
        this.compareScreens(name, 1, this.defaultWaitTime, this.maxPixelTolerance, this.minPercentTolerant);
        this.assertImagesResults();
    }

    /**
     * Compare the current screen.
     *
     * @param timeOut to wait for the image
     * @throws Exception
     */
    public void compareScreens(int timeOut) throws Exception {
        this.compareScreens(timeOut, this.defaultWaitTime, this.maxPixelTolerance, this.minPercentTolerant);
    }

    /**
     * Assert the current screen.
     *
     * @param timeOut to wait for the image
     * @throws Exception
     */
    public void assertScreen(int timeOut) throws Exception {
        this.compareScreens(timeOut, this.defaultWaitTime, this.maxPixelTolerance, this.minPercentTolerant);
        this.assertImagesResults();
    }

    /**
     * Compare the current screen.
     *
     * @param name    of the image
     * @param timeOut to wait for the image
     * @throws Exception
     */
    public void compareScreens(String name, int timeOut) throws Exception {
        this.compareScreens(name, timeOut, this.defaultWaitTime, this.maxPixelTolerance, this.minPercentTolerant);
    }

    /**
     * Assert the current screen.
     *
     * @param name    of the image
     * @param timeOut to wait for the image
     * @throws Exception
     */
    public void assertScreen(String name, int timeOut) throws Exception {
        this.compareScreens(name, timeOut, this.defaultWaitTime, this.maxPixelTolerance, this.minPercentTolerant);
        this.assertImagesResults();
    }

    /**
     * Compare the current screen.
     *
     * @param timeOut          to wait for the image
     * @param percentTolerance of the image
     * @return boolean
     * @throws Exception
     */
    public boolean compareScreens(int timeOut, double percentTolerance) throws Exception {
        return this.compareScreens(timeOut, this.defaultWaitTime, this.maxPixelTolerance, percentTolerance);
    }

    /**
     * Assert the current screen.
     *
     * @param timeOut          to wait for the image
     * @param percentTolerance of the image
     * @throws Exception
     */
    public void assertScreen(int timeOut, double percentTolerance) throws Exception {
        this.compareScreens(timeOut, this.defaultWaitTime, this.maxPixelTolerance, percentTolerance);
        this.assertImagesResults();
    }

    /**
     * Compare the current screen.
     *
     * @param timeOut          to wait for the image
     * @param wait             to sleep before asserting the image
     * @param percentTolerance of the image
     * @return boolean
     * @throws Exception
     */
    public boolean compareScreens(int timeOut, int wait, double percentTolerance) throws Exception {
        return this.compareScreens(timeOut, wait, this.maxPixelTolerance, percentTolerance);
    }

    /**
     * Assert the current screen.
     *
     * @param timeOut          to wait for the image
     * @param wait             to sleep before asserting the image
     * @param percentTolerance of the image
     * @throws Exception
     */
    public void assertScreen(int timeOut, int wait, double percentTolerance) throws Exception {
        this.compareScreens(timeOut, wait, this.maxPixelTolerance, percentTolerance);
        this.assertImagesResults();
    }

    /**
     * Compare the current screen.
     *
     * @param name             of the image
     * @param timeOut          to wait for the image
     * @param percentTolerance of the image
     * @return boolean
     * @throws Exception
     */
    public boolean compareScreens(String name, int timeOut, double percentTolerance) throws Exception {
        boolean result = this.compareScreens(name, timeOut, 0, this.maxPixelTolerance, percentTolerance);
        return result;
    }

    /**
     * Assert the current screen.
     *
     * @param name             of the image
     * @param timeOut          to wait for the image
     * @param percentTolerance of the image
     * @throws Exception
     */
    public void assertScreen(String name, int timeOut, double percentTolerance) throws Exception {
        this.compareScreens(name, timeOut, 0, this.maxPixelTolerance, percentTolerance);
        this.assertImagesResults();
    }

    /**
     * TODO(): Add docs.
     */
    public void clearImagesResults() {
        this.imagesResults.clear();
        this.imageCounter = 1;
    }

    /**
     * TODO(): Add docs.
     */
    public void assertImagesResults() {
        for (String imageName : this.imagesResults.keySet()) {
            Assert.assertTrue(this.imagesResults.get(imageName), String.format("The test failed - %s does not match the actual image.", imageName));
        }
    }

    /**
     * TODO(): Add docs.
     *
     * @param timeOut
     * @param waitTime
     * @param pixelTolerance
     * @param percentTolerance
     * @return
     * @throws Exception
     */
    private boolean compareScreens(int timeOut, int waitTime, int pixelTolerance, double percentTolerance) throws Exception {
        String testName = this.createImageName();
        boolean result = this.compareScreens(testName, timeOut, waitTime, pixelTolerance, percentTolerance);

        return result;
    }

    /**
     * TODO(): Add docs.
     *
     * @param name
     * @param timeOut
     * @param waitTime
     * @param pixelTolerance
     * @param percentTolerance
     * @return
     * @throws Exception
     */
    private boolean compareScreens(String name, int timeOut, int waitTime, int pixelTolerance, double percentTolerance) throws Exception {
        boolean result = this.imageVerification.compareScreens(name, timeOut, waitTime, pixelTolerance, percentTolerance);
        this.imagesResults.put(name, result);
        this.imageCounter++;

        return result;
    }

    /**
     * TODO(): Add docs.
     *
     * @param element
     * @param timeOut
     * @param waitTime
     * @param pixelTolerance
     * @param percentTolerance
     * @throws Exception
     */
    private void compareElements(UIElement element, int timeOut, int waitTime, int pixelTolerance, double percentTolerance) throws Exception {
        String testName = this.createImageName();
        this.compareElements(testName, element, timeOut, waitTime, pixelTolerance, percentTolerance);
    }

    /**
     * TODO(): Add docs.
     *
     * @param imageName
     * @param element
     * @param timeOut
     * @param waitTime
     * @param pixelTolerance
     * @param percentTolerance
     * @throws Exception
     */
    private void compareElements(String imageName, UIElement element, int timeOut, int waitTime, int pixelTolerance, double percentTolerance) throws Exception {
        boolean result = this.imageVerification.compareElements(element, imageName, timeOut, waitTime, pixelTolerance, percentTolerance);
        this.imagesResults.put(imageName, result);
        this.imageCounter++;
    }

    /**
     * TODO(): Add docs.
     *
     * @return
     */
    private String createImageName() {
        return this.imageCounter <= 1 ? this.context.getTestName() : this.context.getTestName() + "_" + this.imageCounter;
    }

    /**
     * Init all tests helpers like find, wait, device.
     */
    private void initUITestHelpers() {
        if (this.mobileSetupManager == null) {
            this.mobileSetupManager = MobileSetupManager.initTestSetupBasic(true);
            this.context = this.mobileSetupManager.getContext();
            this.settings = this.mobileSetupManager.getContext().settings;
            this.uiElements = this.mobileSetupManager.getContext().uiElementClass;
            this.locators = this.context.locators;
            this.log = this.context.log;
            this.locators = this.context.locators;
            this.server = this.context.server;
            this.client = this.context.client;
            this.device = this.context.device;
            this.imageUtils = this.context.imageUtils;
            this.log = this.context.log;
            this.app = this.context.app;
            this.find = this.context.find;
            this.wait = this.context.wait;
            this.gestures = this.context.gestures;
            this.sikuliImageProcessing = this.context.sikuliImageProcessing;
        }
    }
}
