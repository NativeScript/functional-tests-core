package functional.tests.core.utils;

import functional.tests.core.log.LoggerBase;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;

import java.io.*;

/**
 * Archive utils.
 */
public class Archive {

    private static final LoggerBase LOGGER_BASE = LoggerBase.getLogger("Archive");

    /**
     * Extract tgz file to specified location.
     *
     * @param archive tgz archive.
     * @param dest    destination folder.
     * @throws IOException when some file operation fails.
     */
    public static void extractArchive(File archive, File dest) throws IOException {
        String archivePath = archive.getAbsolutePath();
        LOGGER_BASE.info("Extracting " + archivePath + " ...");

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
        LOGGER_BASE.info(archivePath + " extracted.");
    }
}
