package functional.tests.core.Device;

import functional.tests.core.Device.Android.AndroidDevice;
import functional.tests.core.Device.iOS.iOSDevice;
import functional.tests.core.Enums.PlatformType;
import functional.tests.core.Log.Log;
import functional.tests.core.Settings.Settings;

public class DeviceManager {

    public static IDevice getDevice() {

        if (Settings.platform == PlatformType.iOS) {
            return new iOSDevice();
        } else if (Settings.platform == PlatformType.Andorid) {
            return new AndroidDevice();
        } else {
            Log.error(String.format("The device for %s platform is not implemented.", Settings.platform));
            throw new UnsupportedOperationException();
        }
    }
}
