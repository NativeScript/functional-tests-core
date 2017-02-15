package functional.tests.core.find;

import functional.tests.core.enums.PlatformType;
import functional.tests.core.settings.Settings;
import org.apache.commons.lang.NotImplementedException;

/**
 * TODO(): Add docs.
 */
public class UIElementClass {

    private Settings settings;

    public UIElementClass(Settings settings) {
        this.settings = settings;
    }

    public String activityIndicatorLocator() {
        if (this.settings.platform == PlatformType.Andorid) {
            return "android.widget.ProgressBar";
        } else {
            return this.createIosElement("ActivityIndicator");
        }
    }

    public String buttonLocator() {
        if (this.settings.platform == PlatformType.Andorid) {
            return "android.widget.Button";
        } else {
            return this.createIosElement("Button");
        }
    }

    public String editTextLocator() {
        if (this.settings.platform == PlatformType.Andorid) {
            return "android.widget.EditText";
        } else {
            return this.createIosElement("TextField");
        }
    }

    public String textViewLocator() {
        if (this.settings.platform == PlatformType.Andorid) {
            return "android.widget.TextView";
        } else if (this.settings.platform == PlatformType.iOS) {
            return this.createIosElement("StaticText");
        } else {
            try {
                throw new Exception("Not found.");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public String textFieldLocator() {
        if (this.settings.platform == PlatformType.Andorid) {
            return "android.widget.TextView";
        } else if (this.settings.platform == PlatformType.iOS) {
            return this.createIosElement("TextView");
        } else {
            try {
                throw new Exception("Not found.");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public String imageLocator() {
        if (this.settings.platform == PlatformType.Andorid) {
            return "android.widget.ImageView";
        } else {
            return this.createIosElement("Image");
        }
    }

    public String imageButtonLocator() {
        if (this.settings.platform == PlatformType.Andorid) {
            return "android.widget.ImageButton";
        } else {
            return null;
        }
    }

    public String labelLocator() {
        if (this.settings.platform == PlatformType.Andorid) {
            return "android.widget.TextView";
        } else {
            return this.createIosElement("StaticText");
        }
    }

    public String listViewLocator() {
        if (this.settings.platform == PlatformType.Andorid) {
            return "android.widget.ListView";
        } else {
            String element = "TableView";
            if (this.settings.platformVersion >= 10) {
                element = "Table";
            }

            return this.createIosElement(element);
        }
    }

    public String recyclerViewLocator() {
        if (this.settings.platform == PlatformType.Andorid) {
            return "android.support.v7.widget.RecyclerView";
        } else {
            return this.createIosElement("CollectionView");
        }
    }

    public String cellLocator() {
        if (this.settings.platform == PlatformType.iOS) {
            String element = "TableCell";
            if (this.settings.platformVersion >= 10) {
                element = "Cell";
            }
            return this.createIosElement(element);
        } else {
            return "";
        }
    }

    public String progressLocator() {
        if (this.settings.platform == PlatformType.Andorid) {
            return "android.widget.ProgressBar";
        } else {
            return this.createIosElement("ProgressIndicator");
        }
    }

    public String scrollViewLocator() {
        if (this.settings.platform == PlatformType.Andorid) {
            return "android.widget.ScrollView";
        } else {
            return this.createIosElement("ScrollView");
        }
    }

    public String searchBoxLocator() {
        if (this.settings.platform == PlatformType.Andorid) {
            return "android.widget.EditText";
        } else {
            String text = "SearchBar";
            if (this.settings.platform == PlatformType.iOS && this.settings.platformVersion >= 10) {
                text = "SearchField";
            }
            return this.createIosElement(text);
        }
    }

    public String sliderLocator() {
        if (this.settings.platform == PlatformType.Andorid) {
            return "android.widget.SeekBar";
        } else {
            return this.createIosElement("Slider");
        }
    }

    public String switchLocator() {
        if (this.settings.platform == PlatformType.Andorid) {
            return "android.widget.Switch";
        } else {
            return this.createIosElement("Switch");
        }
    }

    public String webViewLocator() {
        if (this.settings.platform == PlatformType.Andorid) {
            return "android.webkit.WebView";
        } else {
            String element = "WebView";
            if (this.settings.platformVersion >= 10) {
                return null;
            }

            return this.createIosElement(element);
        }
    }

    public String viewGroupLocator() {
        if (this.settings.platform == PlatformType.Andorid) {
            if (this.settings.platformVersion > 5.1) {
                return "android.view.ViewGroup";
            } else {
                return "android.view.View";
            }
        } else {
            throw new NotImplementedException();
        }
    }

    public String frameLayoutLocator() {
        if (this.settings.platform == PlatformType.Andorid) {
            return "android.widget.FrameLayout";
        } else {
            throw new NotImplementedException();
        }
    }

    // Not sure that for iOS the element is Picker
    public String timePickerLocator() {
        if (this.settings.platform == PlatformType.Andorid) {
            return "android.widget.TimePicker";
        } else {
            String text = "Picker";
            if (this.settings.platform == PlatformType.iOS && this.settings.platformVersion >= 10) {
                text = "DatePicker";
            }
            return this.createIosElement(text);
        }
    }

    // Not sure that for iOS the element is Picker
    public String datePickerLocator() {
        if (this.settings.platform == PlatformType.Andorid) {
            return "android.widget.DatePicker";
        } else {
            String text = "Picker";
            if (this.settings.platform == PlatformType.iOS && this.settings.platformVersion >= 10) {
                text = "DatePicker";
            }
            return this.createIosElement(text);
        }
    }

    // Not sure that for iOS the element is Picker
    public String listPicker() {
        if (this.settings.platform == PlatformType.Andorid) {
            return "android.widget.NumberPicker";
        } else {
            return this.createIosElement("Picker");
        }
    }

    public String navigationBarLocator() {
        if (this.settings.platform == PlatformType.Andorid) {
            return null;
        } else {
            return this.createIosElement("NavigationBar");
        }
    }

    public String segmentedControlLocator() {
        if (this.settings.platform == PlatformType.Andorid) {
            return null;
        } else {
            return this.createIosElement("SegmentedControl");
        }
    }

    public String tabHostLocator() {
        if (this.settings.platform == PlatformType.Andorid) {
            return "android.widget.TabHost";
        } else {
            return null;
        }
    }

    public String tabWidgetLocator() {
        if (this.settings.platform == PlatformType.Andorid) {
            return "android.widget.TabWidget";
        } else {
            return null;
        }
    }

    private String createIosElement(String element) {
        String xCUIElementType = "XCUIElementType";
        String uIA = "UIA";
        String elementType;

        if (this.settings.platformVersion.toString().startsWith("10")) {
            elementType = xCUIElementType;
        } else {
            elementType = uIA;
        }

        return elementType + element;
    }
}
