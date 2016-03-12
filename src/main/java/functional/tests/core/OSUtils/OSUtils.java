package functional.tests.core.OSUtils;

import functional.tests.core.Enums.OSType;
import functional.tests.core.Log.Log;
import functional.tests.core.Settings.Settings;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;

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

    public static String getTime() {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static void getScreenshot(String fileName) {
        try {
            BufferedImage image = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
            String path = Settings.screenshotOutDir + File.separator + fileName;
            File f = new File(path);
            Log.debug("Save Picture: " + f.getAbsolutePath());
            ImageIO.write(image, "png", f);
            Log.error("Save screenshot of host OS: " + path);
        } catch (Exception e) {
            Log.error("Faield to take host OS screenshot.");
        }
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

    /**
     * This is a convenience method that calls find(File, String, boolean) with
     * the last parameter set to "false" (does not match directories).
     *
     * @see #find(File, String, boolean)
     */
    public static File find(File contextRoot, String fileName) {
        return find(contextRoot, fileName, false);
    }

    /**
     * Searches through the directory tree under the given context directory and
     * finds the first file that matches the file name. If the third parameter is
     * true, the method will also try to match directories, not just "regular"
     * files.
     *
     * @param contextRoot      The directory to start the search from.
     * @param fileName         The name of the file (or directory) to search for.
     * @param matchDirectories True if the method should try and match the name against directory
     *                         names, not just file names.
     * @return The java.io.File representing the <em>first</em> file or
     * directory with the given name, or null if it was not found.
     */
    public static File find(File contextRoot, String fileName, boolean matchDirectories) {
        if (contextRoot == null)
            throw new NullPointerException("NullContextRoot");

        if (fileName == null)
            throw new NullPointerException("NullFileName");

        if (!contextRoot.isDirectory()) {
            Object[] filler = {contextRoot.getAbsolutePath()};
            String message = "NotDirectory";
            throw new IllegalArgumentException(message);
        }

        File[] files = contextRoot.listFiles();

        //
        // for all children of the current directory...
        //
        for (int n = 0; n < files.length; ++n) {
            String nextName = files[n].getName();

            //
            // if we find a directory, there are two possibilities:
            //
            // 1. the names match, AND we are told to match directories.
            // in this case we're done
            //
            // 2. not told to match directories, so recurse
            //
            if (files[n].isDirectory()) {
                if (nextName.equals(fileName) && matchDirectories)
                    return files[n];

                File match = find(files[n], fileName);

                if (match != null)
                    return match;
            }

            //
            // in the case of regular files, just check the names
            //
            else if (nextName.equals(fileName))
                return files[n];
        }

        return null;
    }
}
