package functional.tests.core.mobile.appium;

import functional.tests.core.enums.PlatformType;
import functional.tests.core.mobile.settings.MobileSettings;
import functional.tests.core.utils.FileSystem;
import functional.tests.core.utils.OSUtils;
import io.appium.java_client.remote.AndroidMobileCapabilityType;
import io.appium.java_client.remote.AutomationName;
import io.appium.java_client.remote.IOSMobileCapabilityType;
import io.appium.java_client.remote.MobileCapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;

/**
 * Appium Desired Capabilities.
 */
public class Capabilities {

    /**
     * Load common capabilities.
     *
     * @param settings Current settings.
     * @return Common capabilities.
     */
    public DesiredCapabilities loadDesiredCapabilities(MobileSettings settings) {
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability(MobileCapabilityType.AUTOMATION_NAME, settings.automationName);
        capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, settings.platform);
        capabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION, this.getPlatformVersion(settings.platformVersion));

        //
        String deviceName = OSUtils.getEnvironmentVariable("DEVICE_NAME", settings.deviceName);
        capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, deviceName);

        // Increase the NEW_COMMAND_TIMEOUT capability
        // to avoid ending the session during debug.
        int newCommandTimeout = settings.deviceBootTimeout * 10;
        if (settings.debug) {
            newCommandTimeout *= 3;
        }
        capabilities.setCapability(MobileCapabilityType.NEW_COMMAND_TIMEOUT, newCommandTimeout);

        // --no-reset
        // Do not reset app state between sessions
        // IOS: do not delete app plist files;
        // Android: do not uninstall app before new session)
        capabilities.setCapability(MobileCapabilityType.NO_RESET, true);

        // --full-reset
        // (iOS) Delete the entire simulator folder.
        // (Android) Reset app state by uninstalling app instead of clearing app data.
        // On Android, this will also remove the app after the session is complete.
        capabilities.setCapability(MobileCapabilityType.FULL_RESET, false);

        if (settings.orientation != null) {
            capabilities.setCapability(MobileCapabilityType.ORIENTATION, settings.orientation);
        }

        capabilities.setCapability(MobileCapabilityType.UDID, settings.deviceId.trim());

        capabilities.setCapability(MobileCapabilityType.APP,
                settings.BASE_TEST_APP_DIR + File.separator + settings.testAppFileName);

        return capabilities;
    }

    /**
     * Load Android specific capabilities.
     *
     * @param settings Current settings.
     * @return Android specific capabilities.
     */
    public DesiredCapabilities loadAndroidCapabilities(MobileSettings settings) {
        DesiredCapabilities capabilities = this.loadDesiredCapabilities(settings);

        // Android
        if (settings.platform == PlatformType.Android) {
            capabilities.setCapability(AndroidMobileCapabilityType.APP_PACKAGE, settings.packageId);
            capabilities.setCapability(AndroidMobileCapabilityType.APP_ACTIVITY, settings.android.defaultActivity);
            capabilities.setCapability(AndroidMobileCapabilityType.APP_WAIT_ACTIVITY, settings.android.appWaitActivity);
            capabilities.setCapability(AndroidMobileCapabilityType.APP_WAIT_PACKAGE, settings.android.appWaitPackage);
            capabilities.setCapability(AndroidMobileCapabilityType.NO_SIGN, true);
            if (settings.automationName.equalsIgnoreCase(AutomationName.ANDROID_UIAUTOMATOR2)) {
                String systemPortString = OSUtils.getEnvironmentVariable("SYSTEM_PORT",
                        String.valueOf(OSUtils.getFreePort(8201, 8501)));
                capabilities.setCapability(AndroidMobileCapabilityType.SYSTEM_PORT, Integer.valueOf(systemPortString));
            }
            if (settings.isRealDevice) {
                capabilities.setCapability(MobileCapabilityType.NO_RESET, false);
            } else {
                if (settings.platformVersion < 5.0) {
                    capabilities.setCapability("skipUnlock", false);
                } else {
                    capabilities.setCapability("skipUnlock", true);
                }
            }
        }

        return capabilities;
    }

    /**
     * Load iOS specific capabilities.
     *
     * @param settings Current settings.
     * @return iOS specific capabilities.
     */
    public DesiredCapabilities loadIOSCapabilities(MobileSettings settings) {
        DesiredCapabilities capabilities = this.loadDesiredCapabilities(settings);

        // Please refer to https://www.npmjs.com/package/appium-xcuitest-driver
        capabilities.setCapability(IOSMobileCapabilityType.AUTO_ACCEPT_ALERTS, settings.ios.acceptAlerts);
        capabilities.setCapability(IOSMobileCapabilityType.LAUNCH_TIMEOUT, settings.deviceBootTimeout * 1000); // In ms.
        capabilities.setCapability(IOSMobileCapabilityType.SCREENSHOT_WAIT_TIMEOUT, settings.defaultTimeout);
        capabilities.setCapability(IOSMobileCapabilityType.SHOW_IOS_LOG, true);
        capabilities.setCapability(IOSMobileCapabilityType.USE_NEW_WDA, false);
        capabilities.setCapability(IOSMobileCapabilityType.WDA_STARTUP_RETRIES, 5);
        capabilities.setCapability(IOSMobileCapabilityType.SHOULD_USE_SINGLETON_TESTMANAGER, false);

        // It looks we need it for XCTest (iOS 10+ automation)
        if (settings.platformVersion >= 10) {
            int port = settings.ios.wdaLocalPort;

            if (port == 0) {
                port = OSUtils.getFreePort(8100, 8200);
            }
            capabilities.setCapability(IOSMobileCapabilityType.WDA_LOCAL_PORT, port);
        }

        if (settings.isRealDevice) {
            if (FileSystem.exist(settings.ios.xCode8ConfigFile)) {
                capabilities.setCapability(IOSMobileCapabilityType.XCODE_CONFIG_FILE, settings.ios.xCode8ConfigFile);
            }
            capabilities.setCapability(MobileCapabilityType.NO_RESET, false);
            capabilities.setCapability(MobileCapabilityType.FULL_RESET, true);
        }

        return capabilities;
    }

    private String getPlatformVersion(double version) {
        String versionString = String.valueOf(version);
        if (versionString.equals("8.1")) {
            versionString = "api27";
        }
        return versionString;
    }
}
