package functional.tests.core.settings;

import functional.tests.core.enums.ImageVerificationType;
import functional.tests.core.enums.OSType;
import functional.tests.core.enums.PlatformType;
import functional.tests.core.exceptions.HostException;
import functional.tests.core.log.LoggerBase;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Properties;

/**
 * MobileSettings.
 * Read settings from config file to MobileSettings object.
 * Config file is specified via appConfig VM option in tests based on this framework.
 * For example: -DappConfig=resources/config/cuteness/cuteness.emu.default.api23.properties
 */
public class Settings {

    protected static final LoggerBase LOGGER_BASE = LoggerBase.getLogger("Settings");
    protected static final String APP_CONFIG_PATH = System.getProperty("appConfig");
    protected static final String STORAGE_ENVIRONMENT_VARIABLE = "STORAGE";
    protected static final String DEBUG_ENVIRONMENT_VARIABLE = "DEBUG";
    protected static final String USER_DIR = System.getProperty("user.dir");

    public static final int DEFAULT_TAP_DURATION = 250;
    public static final String BASE_RESOURCE_DIR = USER_DIR + File.separator + "resources";
    public static final String BASE_TEST_DATA_DIR = BASE_RESOURCE_DIR + File.separator + "testdata";
    public static final String BASE_TEST_APP_DIR = USER_DIR + File.separator + "testapp";
    public static OSType os;

    protected Properties properties;

    public final String buildRunStartupTime = new SimpleDateFormat("dd MM yyyy HH:mm:ss").format(System.currentTimeMillis());

    public String baseLogDir;
    public String consoleLogDir;
    public String perfDir;
    public String screenshotOutDir;
    public String screenshotResDir;
    public String baseOutputDir;
    public String testAppFileName;
    public String testAppName;
    public String deviceName;
    public boolean debug;
    public boolean restartApp;
    public boolean takeScreenShotAfterTest;
    public boolean logImageVerificationStatus;
    public int shortTimeout;
    public int defaultTimeout;
    public int deviceBootTimeout;
    public PlatformType platform;
    public ImageVerificationType imageVerificationType;
    public LoggerBase log;

    /**
     * Init settings.
     */
    public Settings() {
        this.baseOutputDir = USER_DIR + File.separator + "target" + File.separator + "surefire-reports";

        // Read properties file
        try {
            this.properties = this.readProperties();
        } catch (Exception e) {
            LOGGER_BASE.error(e.getMessage());
        }
    }

    /**
     * Get current app configuration.
     *
     * @return
     */
    public static String getAppConfig() {
        return System.getProperty("appConfig");
    }

    /**
     * Init common settings.
     */
    public void initSettings() {
        LOGGER_BASE.separator();

        this.os = this.getOSType();

        // Set locations and cleanup output folders
        this.setupLocations();

        this.platform = this.getPlatformType();
        this.testAppFileName = this.properties.getProperty("testAppName");
        this.logImageVerificationStatus = this.properties.getProperty("logImageVerificationStatus") != null ?
                new Boolean(this.properties.getProperty("logImageVerificationStatus")) : false;

        // Set debug
        this.debug = Boolean.valueOf(this.getEnvironmentVariable(DEBUG_ENVIRONMENT_VARIABLE, "False")) ||
                java.lang.management.ManagementFactory
                        .getRuntimeMXBean().getInputArguments().toString().indexOf("jdwp") >= 0;

        // Set takeScreenShotAfterTest
        this.takeScreenShotAfterTest = this.propertyToBoolean("takeScreenShotAfterTest", false);

        // Set image verification type
        this.imageVerificationType = this.getImageVerificationType();


        // If defaultTimeout is not specified set it to 60 sec.
        this.defaultTimeout = this.convertPropertyToInt("defaultTimeout", 60);
        this.shortTimeout = this.defaultTimeout / 5;

        // If deviceBootTimeout is not specified set it equal to defaultTimeout
        this.deviceBootTimeout = this.convertPropertyToInt("deviceBootTimeout", 300);

        this.deviceName = this.properties.getProperty("deviceName");

        // Set restartApp
        this.restartApp = this.propertyToBoolean("restartApp", false);

        LOGGER_BASE.info("OS Type: " + this.os);
        LOGGER_BASE.info("Mobile Platform: " + this.platform);
        LOGGER_BASE.info("Device Name: " + this.deviceName);
        LOGGER_BASE.info("Take Screenshot After Test: " + this.takeScreenShotAfterTest);
        LOGGER_BASE.info("Image Verification Type: " + this.imageVerificationType);

        // This was moved to MobileSettings.
        // LOGGER_BASE.info("Default Timeout: " + this.defaultTimeout);
        LOGGER_BASE.info("Device Boot Time: " + this.deviceBootTimeout);
        LOGGER_BASE.info("TestApp File Name: " + this.testAppFileName);
        LOGGER_BASE.info("Log Output Folder: " + this.baseLogDir);
        LOGGER_BASE.info("Perfapp Folder: " + this.perfDir);
        LOGGER_BASE.info("Screenshot Output Folder: " + this.screenshotOutDir);
        LOGGER_BASE.info("Screenshot Resources Folder: " + this.screenshotResDir);
        LOGGER_BASE.info("Debug: " + this.debug);
        LOGGER_BASE.info("Log image verification status: " + this.logImageVerificationStatus);
    }

    /**
     * Helper method that converts property to int.
     *
     * @param property     as String.
     * @param defaultValue of property as int.
     * @return Value of property as int.
     */
    public int convertPropertyToInt(String property, int defaultValue) {
        String propertyString = this.properties.getProperty(property);
        if (propertyString != null) {
            return Integer.valueOf(propertyString);
        } else {
            return defaultValue;
        }
    }

    /**
     * Helper method that converts property to Boolean.
     *
     * @param property as String.
     * @return Value of property as Boolean.
     */
    public Boolean propertyToBoolean(String property, boolean defaultValue) {
        String value = this.properties.getProperty(property);
        if (value == null) {
            return defaultValue;
        }
        if (value.equalsIgnoreCase("true")) {
            return true;
        } else if (value.equalsIgnoreCase("false")) {
            return false;
        } else {
            return null;
        }
    }

    /**
     * Set location settings such as baseLogDir, consoleLogDir, screenshotOutDir, screenshotResDir and appiumLogFile.
     */
    public void setupLocations() {
        this.baseLogDir = this.baseOutputDir + File.separator + "logs";
        this.consoleLogDir = this.baseLogDir + File.separator + "console";
        this.perfDir = this.getStorage() + File.separator + "perf";
        this.screenshotOutDir = this.baseOutputDir + File.separator + "screenshots";
        this.screenshotResDir = this.getStorage() + File.separator + "images";

        try {
            File baseScreenshotDirLocation = new File(this.screenshotOutDir);
            baseScreenshotDirLocation.mkdirs();
            FileUtils.cleanDirectory(baseScreenshotDirLocation);
        } catch (IOException e) {
            LOGGER_BASE.fatal("Failed to cleanup and create screenshot output folder.");
        }

        try {
            File baseLogDirLocation = new File(this.baseLogDir);
            baseLogDirLocation.mkdirs();
            File consoleLogDirLocation = new File(this.consoleLogDir);
            consoleLogDirLocation.mkdirs();
            FileUtils.cleanDirectory(consoleLogDirLocation);
        } catch (IOException e) {
            LOGGER_BASE.fatal("Failed to cleanup and create logs folder.");
            try {
                throw new IOException(e);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }


    /**
     * Get image verification type setting.
     * Default value: ImageVerificationType.Default
     *
     * @return ImageVerificationType value.
     */
    private ImageVerificationType getImageVerificationType() {
        String imageVerificationTypeString = this.properties.getProperty("imageVerificationType");
        ImageVerificationType type = ImageVerificationType.Default;

        if (imageVerificationTypeString != null) {
            if (imageVerificationTypeString.equalsIgnoreCase("firsttimecapture")) {
                type = ImageVerificationType.FirstTimeCapture;
            } else if (imageVerificationTypeString.equalsIgnoreCase("skip")) {
                type = ImageVerificationType.Skip;
            }
        }

        return type;
    }

    /**
     * Get type of host operating system.
     *
     * @throws HostException when operating system is unknown.
     * @retur OSType value.
     */
    private OSType getOSType() {
        String osTypeString = System.getProperty("os.name", "generic").toLowerCase();
        if ((osTypeString.contains("mac")) || (osTypeString.contains("darwin"))) {
            return OSType.MacOS;
        } else if (osTypeString.contains("win")) {
            return OSType.Windows;
        } else if (osTypeString.contains("nux")) {
            return OSType.Linux;
        } else {
            LOGGER_BASE.fatal("Unknown host OS.");
            try {
                throw new HostException("Unknown host OS.");
            } catch (HostException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    /**
     * Get storage settings.
     * Read from STORAGE environment variable.
     *
     * @return value of STORAGE environment variable
     */
    private String getStorage() {
        return this.getEnvironmentVariable(STORAGE_ENVIRONMENT_VARIABLE, BASE_RESOURCE_DIR);
    }

    /**
     * Read properties from file to Properties object.
     *
     * @return Properties object.
     * @throws Exception When properties file is not found.
     */
    private Properties readProperties() throws Exception {
        String appConfigFile = USER_DIR + File.separator + APP_CONFIG_PATH;
        try {
            InputStream input = new FileInputStream(appConfigFile);
            Properties prop = new Properties();
            prop.load(input);
            return prop;
        } catch (Exception e) {
            LOGGER_BASE.fatal("Failed to read and init settings. Please check if " + appConfigFile + " exists.");
            throw new Exception(e);
        }
    }

    /**
     * Get mobile platform type.
     *
     * @return PlatformType value.
     */
    private PlatformType getPlatformType() {
        String platformTypeString = this.properties.getProperty("platformName");
        PlatformType platformType;
        if (platformTypeString.equalsIgnoreCase("Android")) {
            platformType = PlatformType.Android;
        } else if (platformTypeString.equalsIgnoreCase("iOS")) {
            platformType = PlatformType.iOS;
        } else if (platformTypeString.equalsIgnoreCase("chrome")) {
            return PlatformType.Chrome;
        } else if (platformTypeString.equalsIgnoreCase("vscode")) {
            return PlatformType.VSCode;
        } else {
            platformType = PlatformType.Other;
        }

        return platformType;
    }

    private String getEnvironmentVariable(String variable, String defaultValue) {
        String finalValue = defaultValue;
        String env = System.getenv(variable);
        if (env != null) {
            finalValue = env;
        }
        LOGGER_BASE.info(String.format("%s=%s", variable, finalValue));
        return finalValue;
    }
}
