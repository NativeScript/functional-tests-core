package functional.tests.core.chromedriver;

import functional.tests.core.enums.OSType;
import functional.tests.core.enums.PlatformType;
import functional.tests.core.extensions.SystemExtension;
import functional.tests.core.utils.OSUtils;
import functional.tests.core.web.settings.WebSettings;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;

/**
 * ChromeDriverOptions.
 */
public class ChromeDriverOptions {

    public ChromeOptions loadChromeDriverOptions(WebSettings webSettings) {
        ChromeOptions options = new ChromeOptions();

        if (webSettings.platform == PlatformType.VSCode) {
            options.setBinary(this.getFileLocation("code"));
        } else {
        }

        //String chromeDriverFullPath = OSUtils.runProcess("which chromedriver");
        //System.setProperty("webdriver.chrome.driver", "/usr/local/Cellar/chromedriver/2.22/bin/chromedriver");

        return options;
    }

    private String getFileLocation(String appName) {
        String pathToVSCode = "";

        if (WebSettings.os == OSType.Windows) {

        } else {
            String vsCodeSymLink = OSUtils.runProcess("which " + appName);
            String pathToVSCodeWithSymLink = OSUtils.runProcess("ls -la " + vsCodeSymLink);
            pathToVSCode = pathToVSCodeWithSymLink.substring(pathToVSCodeWithSymLink.lastIndexOf(">") + 1).trim();
        }

        File vSCodeFile = new File(pathToVSCode);

        if (!vSCodeFile.exists()) {
            SystemExtension.interruptProcess("Path to the vscode is not correct");
        }

        return pathToVSCode;
    }
}
