package functional.tests.core.OSUtils;

import functional.tests.core.Log.Log;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileSystem {

    public static void deletePath(String Path) throws IOException {
        try {
            File file = new File(Path);
            if (file.isDirectory()) {
                FileUtils.deleteDirectory(file);
            } else {
                file.delete();
            }
            Log.info("Delete " + Path);
        } catch (Exception e) {
            String errorMessage = "Faield to delete " + Path;
            Log.fatal(errorMessage);
            throw new IOException(errorMessage);
        }
    }

    public static void makeDir(String Path) {
        File file = new File(Path);

        if (file.exists()) {
            Log.debug("Path " + Path + " already exists.");
        } else {
            file.mkdirs();
            Log.debug("Path  " + Path + " created.");
        }
    }

    public static String readFile(String filePath) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(filePath));
        return new String(encoded, Charset.defaultCharset());
    }

    public static boolean exist(String path) throws IOException {
        File file = new File(path);
        return file.exists();
    }
}
