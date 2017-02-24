package functional.tests.core.web.settings;

import functional.tests.core.settings.Settings;

/**
 * Web settings class.
 */
public class WebSettings extends Settings {

    public final String uri;

    public WebSettings() {
        this.uri = this.properties.getProperty("uri");
        super.initSettings();
    }
}
