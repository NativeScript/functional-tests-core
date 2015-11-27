package functional.tests.core.Gestures;

import functional.tests.core.Element.Element;
import functional.tests.core.Log.Log;
import functional.tests.core.Settings.Settings;
import io.appium.java_client.MobileElement;

public class Gestures {

    public static void tap(MobileElement element, int fingers, int duration) {
        element.tap(fingers, duration);
        Log.info("Tap " + Element.getDescription(element));
    }

    public static void tap(MobileElement element, int fingers) {
        element.tap(fingers, Settings.defaultTapDuration);
    }

    public static void tap(MobileElement element) {
        element.tap(1, Settings.defaultTapDuration);
    }
}
