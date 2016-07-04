package functional.tests.core.Appium;

import functional.tests.core.Enums.DeviceType;
import functional.tests.core.Enums.PlatformType;
import functional.tests.core.Log.Log;
import functional.tests.core.Settings.Settings;
import io.appium.java_client.remote.AndroidMobileCapabilityType;
import io.appium.java_client.remote.IOSMobileCapabilityType;
import io.appium.java_client.remote.MobileCapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;

public class Capabilities {

    public DesiredCapabilities loadDesiredCapabilities() {

        DesiredCapabilities capabilities = new DesiredCapabilities();

        // Common
        capabilities.setCapability(MobileCapabilityType.APPIUM_VERSION, Settings.appiumVersion);
        capabilities.setCapability(MobileCapabilityType.AUTOMATION_NAME, Settings.automationName);
        capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, Settings.platform);
        capabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION, Settings.platformVersion);
        capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, Settings.deviceName);
        capabilities.setCapability(MobileCapabilityType.NEW_COMMAND_TIMEOUT, Settings.deviceBootTimeout);
        capabilities.setCapability(MobileCapabilityType.APP, Settings.baseTestAppDir + File.separator + Settings.testAppName);

        capabilities.setCapability(MobileCapabilityType.FULL_RESET, false);
        //capabilities.setCapability(MobileCapabilityType.ORIENTATION, Settings.orientation);

        if (Settings.isRealDevice == true) {
            capabilities.setCapability(MobileCapabilityType.UDID, Settings.deviceId);
        }

        // Android
        if (Settings.platform == PlatformType.Andorid) {
            // capabilities.setCapability(AndroidMobileCapabilityType.APP_ACTIVITY, Settings.defaultActivity);
            // capabilities.setCapability(AndroidMobileCapabilityType.APP_PACKAGE, Settings.packageId);
            capabilities.setCapability(AndroidMobileCapabilityType.APP_WAIT_ACTIVITY, Settings.defaultActivity);
            capabilities.setCapability(AndroidMobileCapabilityType.APP_WAIT_PACKAGE, Settings.packageId);
            // capabilities.setCapability(AndroidMobileCapabilityType.INTENT_ACTION, "android.intent.action.MAIN");
            // capabilities.setCapability(AndroidMobileCapabilityType.INTENT_CATEGORY, "org.nativescript.TestApp.MAIN");
            // capabilities.setCapability(AndroidMobileCapabilityType.INTENT_FLAGS, "0x10200000");
            // capabilities.setCapability(AndroidMobileCapabilityType.DONT_STOP_APP_ON_RESET, true);
            // capabilities.setCapability(AndroidMobileCapabilityType.USE_KEYSTORE, false);
            // capabilities.setCapability(AndroidMobileCapabilityType.IGNORE_UNIMPORTANT_VIEWS, true);
            capabilities.setCapability(AndroidMobileCapabilityType.NO_SIGN, true);
            if (Settings.isRealDevice == true) {
                // capabilities.setCapability(AndroidMobileCapabilityType.DEVICE_READY_TIMEOUT, Settings.deviceBootTimeout);
                // capabilities.setCapability(AndroidMobileCapabilityType.ANDROID_DEVICE_READY_TIMEOUT, Settings.defaultTimeout);
            } else {
                // capabilities.setCapability(AndroidMobileCapabilityType.ADB_PORT, 5037);
                // capabilities.setCapability(AndroidMobileCapabilityType.AVD_LAUNCH_TIMEOUT, Settings.deviceBootTimeout * 1000); // In ms, default 120000.
                // capabilities.setCapability(AndroidMobileCapabilityType.AVD_READY_TIMEOUT, Settings.defaultTimeout * 1000); // In ms, default 120000.
                if (Settings.debug) {
                    capabilities.setCapability(AndroidMobileCapabilityType.AVD, Settings.deviceName);
                    capabilities.setCapability(AndroidMobileCapabilityType.AVD_ARGS, Settings.emulatorOptions);
                }
            }
        }

        // iOS
        if (Settings.platform == PlatformType.iOS) {
            capabilities.setCapability(IOSMobileCapabilityType.AUTO_ACCEPT_ALERTS, Settings.acceptAlerts);
            capabilities.setCapability(IOSMobileCapabilityType.LAUNCH_TIMEOUT, Settings.deviceBootTimeout * 1000); // In ms.
            capabilities.setCapability(IOSMobileCapabilityType.SCREENSHOT_WAIT_TIMEOUT, Settings.defaultTimeout);
            capabilities.setCapability(IOSMobileCapabilityType.SHOW_IOS_LOG, true);
            // capabilities.setCapability(IOSMobileCapabilityType.NATIVE_INSTRUMENTS_LIB, true);
            // capabilities.setCapability(IOSMobileCapabilityType.LOCATION_SERVICES_ENABLED, true);
            // capabilities.setCapability(IOSMobileCapabilityType.SEND_KEY_STRATEGY, "setValue");
            // capabilities.setCapability(IOSMobileCapabilityType.WAIT_FOR_APP_SCRIPT, true);
            // capabilities.setCapability(IOSMobileCapabilityType.APP_NAME, Settings.testAppName);

            if (Settings.deviceType == DeviceType.Simulator) {
                // This is required by the safe simulator restart. TODO: Test on Android
                capabilities.setCapability(MobileCapabilityType.NO_RESET, true);
            }
        }

        Log.info(capabilities.toString());
        return capabilities;
    }
}
