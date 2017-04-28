package functional.tests.core.utils;

import functional.tests.core.log.LoggerBase;
import org.apache.commons.io.FileUtils;
import org.testng.Assert;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Utils for file system operations.
 */
public class FileSystem {

    private static final LoggerBase LOGGER_BASE = LoggerBase.getLogger("FileSystem");

    /**
     * Delete file path.
     *
     * @param path Path to file for folder.
     * @throws IOException When fail to delete it.
     */
    public static void deletePath(String path) throws IOException {
        try {
            File file = new File(path);
            if (file.isDirectory()) {
                FileUtils.deleteDirectory(file);
            } else {
                file.delete();
            }
            LOGGER_BASE.info("Delete " + path);
        } catch (Exception e) {
            String errorMessage = "Failed to delete " + path;
            LOGGER_BASE.fatal(errorMessage);
            throw new IOException(errorMessage);
        }
    }

    /**
     * Read content of file.
     *
     * @param filePath File path as String.
     * @return Content of file as String.
     * @throws IOException When fail to read file.
     */
    public static String readFile(String filePath) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(filePath));
        return new String(encoded, Charset.defaultCharset());
    }

    /**
     * Append content of String to file.
     *
     * @param filePath File path as String.
     * @param text     Content to be written in file.
     * @throws IOException When fail to write in file.
     */
    public static void appendFile(String filePath, String text) throws IOException {
        FileUtils.writeStringToFile(new File(filePath), text, true);
    }

    /**
     * Write content of String to file.
     *
     * @param filePath File path as String.
     * @param text     Content to be written in file.
     * @throws IOException When fail to write in file.
     */
    public static void writeFile(String filePath, String text) throws IOException {
        FileUtils.writeStringToFile(new File(filePath), text);
    }

    /**
     * Check if path exists.
     *
     * @param path Path as String.
     * @return True if path exists. False if path does not exist.
     */
    public static boolean exist(String path) {
        File file = new File(path);
        return file.exists();
    }

    /**
     * Ensure path exists (create if does not exists).
     *
     * @param directory Path to directory.
     */
    public static void ensureFolderExists(String directory) {
        File file = new File(directory);
        if (!file.exists()) {
            boolean result = file.mkdirs();
            if (!result) {
                LOGGER_BASE.error("Failed to create folder: " + directory);
            }
        }
    }

    /**
     * Get size of file.
     *
     * @param path Path to file.
     * @return Size of file in kB.
     */
    public static long getFileSize(String path) {
        File file;
        long size = 0;
        file = new File(path);
        if (file.exists()) {
            size = file.length() / 1024; // In KBs
        } else {
            Assert.fail("File '" + file + "' does not exist!");
        }
        return size;
    }
}
