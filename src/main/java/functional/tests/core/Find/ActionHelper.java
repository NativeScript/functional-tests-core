package functional.tests.core.Find;

import functional.tests.core.Appium.Client;
import functional.tests.core.BaseTest.TestsStateManager;
import functional.tests.core.Element.UIElement;
import functional.tests.core.Find.FindHelper;
import functional.tests.core.Log.Log;

public class ActionHelper {

    public static void resetNavigation(String btnText, Client client) {
        FindHelper find = new FindHelper(client);
        if (btnText != null) {
            UIElement demoBtn = findButtonByTextContent(btnText, find);
            if (demoBtn == null) {
                Log.error("Failed to navigate to '" + btnText + "' button.");

                return;
            }
            Log.info("Tap on '" + btnText + "' button.");
            demoBtn.click();
        }
    }

    public static boolean navigateTo(String demoPath, TestsStateManager testsStateManager, Client client) {
        Log.info("Navigating to \"" + demoPath + "\".");
        FindHelper find = new FindHelper(client);
        String splitSeparator = demoPath.contains("/") ? "/" : ".";
        String[] demos = demoPath.split(splitSeparator);

        if (demos.length == 0) {
            demos = new String[1];
            demos[0] = demoPath;
        }

        for (int i = 0; i < demos.length; i++) {
            String btnText = demos[i];
            UIElement demoBtn = findButtonByTextContent(btnText, find);
            if (demoBtn == null) {
                return false;
            }
            Log.info("Tap on '" + btnText + "' button.");
            demoBtn.click();

            if (testsStateManager != null) {
                testsStateManager.setCurrentPage(btnText);
            }

            if (i < demos.length - 1) {
                String nextBtnText = demos[i + 1];
                UIElement nextDemoBtn = functional.tests.core.Find.Find.findByText(nextBtnText, 3);
                if (nextDemoBtn == null) {
                    nextDemoBtn = functional.tests.core.Find.Find.findByText(nextBtnText, 3);
                }
            }
        }

        return true;
    }


    public static void navigateBack(Client client) {
        client.getDriver().navigate().back();
    }

    public static void navigateForward(Client client) {
        client.getDriver().navigate().forward();
    }

    private static UIElement findButtonByTextContent(String text, FindHelper find) {
        UIElement btn = find.byTextNaviagtion(text, 3);

        if (btn == null) {
            btn = find.findByText(text, 3);
        }

        if (btn != null) {
            Log.info(String.format("%s element loaded.", text));
        } else {
            Log.error(String.format("%s NOT loaded.", text));
        }
//
        return btn;
    }
}
