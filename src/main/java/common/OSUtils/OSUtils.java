package common.OSUtils;

import com.sun.jna.platform.FileUtils;
import common.Enums.OSType;
import common.Log.Log;
import common.Settings.Settings;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Arrays;

/**
 * Created by Dimitar on 10/10/2015.
 */
public class OSUtils {

    public static String logPrefix;
    private static final String[] WIN_RUNTIME = {"cmd.exe", "/C"};
    private static final String[] OS_LINUX_RUNTIME = {"/bin/bash", "-l", "-c"};

    private OSUtils() {
    }

    private static <T> T[] concat(T[] first, T[] second) {
        T[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    public static String runProcess(boolean waitFor, String... command) {
        String[] allCommand = null;

        String finalCommand = "";
        for (String s : command) {
            finalCommand = finalCommand + s;
        }

        try {
            if (Settings.OS == OSType.Windows) {
                allCommand = concat(WIN_RUNTIME, command);
                //allCommand = command;
            } else {
                allCommand = concat(OS_LINUX_RUNTIME, command);
            }
            ProcessBuilder pb = new ProcessBuilder(allCommand);
            pb.redirectErrorStream(true);
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

                p.waitFor();

                //Logger.logDebug("Execute command: " + finalCommand);
                //Logger.logDebug("Result: " + output.toString());

                return output.toString();
            } else {
                //Logger.logDebug("Execute command: " + finalCommand);
                return null;
            }

        } catch (Exception e) {
            //Logger.logError("Failed to execute command:" + finalCommand);
            e.printStackTrace();
            return null;
        }
    }

    public static void stopProcess(String name) {
        try {
            if (Settings.OS == OSType.Windows) {
                String command = "taskkill /F /IM " + name;
                OSUtils.runProcess(true, command);
            } else {
                String stopCommand = "ps -A | grep '" + name + "'";

                String processes = OSUtils.runProcess(true, stopCommand);
                String lines[] = processes.split("(\r\n|\n)");

                for (int i = 0; i < lines.length; i++) {
                    String line = lines[i].trim();
                    String procId = line.split("\\s+")[0];
                    OSUtils.runProcess(true, "kill -9 " + procId);
                }
            }
        } catch (Exception e) {
            //Logger.logError("Failed to stop process with name: " + name);
        }
    }
}
