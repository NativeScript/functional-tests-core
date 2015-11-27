package functional.tests.core.Gestures;

import functional.tests.core.Appium.Client;
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
        tap(element, fingers, Settings.defaultTapDuration);
    }

    public static void tap(MobileElement element) {
        tap(element, 1, Settings.defaultTapDuration);
    }
}
