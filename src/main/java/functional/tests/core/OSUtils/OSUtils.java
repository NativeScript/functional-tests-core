package functional.tests.core.OSUtils;

import functional.tests.core.Enums.OSType;
import functional.tests.core.Log.Log;
import functional.tests.core.Settings.Settings;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class OSUtils {

    private static final String[] WIN_RUNTIME = {"cmd.exe", "/C"};
    private static final String[] OS_LINUX_RUNTIME = {"/bin/bash", "-l", "-c"};

    private OSUtils() {
    }

    private static <T> T[] concat(T[] first, T[] second) {
        T[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    public static String runProcess(boolean waitFor, int timeOut, String... command) {
        String[] allCommand = null;

        String finalCommand = "";
        for (String s : command) {
            finalCommand = finalCommand + s;
        }

        try {
            if (Settings.OS == OSType.Windows) {
                allCommand = concat(WIN_RUNTIME, command);
            } else {
                allCommand = concat(OS_LINUX_RUNTIME, command);
            }
            ProcessBuilder pb = new ProcessBuilder(allCommand);
            Process p = pb.start();

            if (waitFor) {
                StringBuffer output = new StringBuffer();

                // Note: No idea why reader should be before p.waitFor(),
                //       but when it is after p.waitFor() execution of
                //       some adb command freeze on Windows
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(p.getInputStream()));

                String line = "";
                while ((line = reader.readLine()) != null) {
                    output.append(line + "\n");
                }

                p.waitFor(timeOut, TimeUnit.SECONDS);

                Log.debug("Execute command: " + finalCommand);
                Log.trace("Result: " + output.toString());

                return output.toString();
            } else {
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String runProcess(String... command) {
        return runProcess(true, 10 * 60, command);
    }

    public static String runProcess(int timeOut, String... command) {
        return runProcess(true, timeOut, command);
    }

    public static void stopProcess(String name) {
        try {
            if (Settings.OS == OSType.Windows) {
                String command = "taskkill /F /IM " + name;
                OSUtils.runProcess(command);
            } else {
                String stopCommand = "ps -A | grep '" + name + "'";

                String processes = OSUtils.runProcess(stopCommand);
                String lines[] = processes.split("(\r\n|\n)");

                for (int i = 0; i < lines.length; i++) {
                    String line = lines[i].trim();
                    String procId = line.split("\\s+")[0];
                    OSUtils.runProcess("kill -9 " + procId);
                }
            }
        } catch (Exception e) {
            Log.debug("Failed to stop process: " + name);
        }
    }
}
