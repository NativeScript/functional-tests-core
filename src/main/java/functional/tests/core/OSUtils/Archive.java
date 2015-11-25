package functional.tests.core.OSUtils;

import functional.tests.core.Log.Log;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;

import java.io.*;

/**
 * Created by Dimitar on 10/10/2015.
 */
public class Archive {

    public static void extractArchive(File archive, File dest) throws IOException {

        String archivePath = archive.getAbsolutePath();
        Log.info("Extracting " + archivePath + " ...");

        if (!dest.exists()) {
            dest.mkdir();
        }

        TarArchiveInputStream tarIn = null;

        tarIn = new TarArchiveInputStream(new GzipCompressorInputStream(
                new BufferedInputStream(new FileInputStream(archive))));

        TarArchiveEntry tarEntry = tarIn.getNextTarEntry();

        while (tarEntry != null) {
            File destPath = new File(dest, tarEntry.getName());
            if (tarEntry.isDirectory()) {
                destPath.mkdirs();
            } else {
                destPath.createNewFile();
                byte[] btoRead = new byte[1024];
                BufferedOutputStream bout = new BufferedOutputStream(
                        new FileOutputStream(destPath));
                int len = 0;

                while ((len = tarIn.read(btoRead)) != -1) {
                    bout.write(btoRead, 0, len);
                }

                bout.close();
            }
            tarEntry = tarIn.getNextTarEntry();
        }

        tarIn.close();
        Log.info(archivePath + " extracted.");
    }
}
