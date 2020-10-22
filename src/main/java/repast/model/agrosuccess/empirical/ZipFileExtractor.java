package repast.model.agrosuccess.empirical;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.apache.commons.io.IOUtils;

public class ZipFileExtractor {

  private final ZipFile zipFile;

  /**
   * @param zipFile Path to zip file whose contents will be extracted
   * @throws IOException if zip file cannot be read
   */
  public ZipFileExtractor(File zipFile) throws IOException {
    this.zipFile = new ZipFile(zipFile.getAbsolutePath());
  }

  /**
   * Extract a file from inside a zip archive to a temporary directory.
   *
   * @param qualifiedFileName Fully qualified file name of the file to extract from within the zip
   *        archive
   * @return Path to extracted file in temporary directory
   * @throws IOException if specified entry cannot be found in the zip file, or a temporary
   *         directory could not be created
   */
  public File extract(String qualifiedFileName) throws IOException {
    ZipEntry entry = this.zipFile.getEntry(qualifiedFileName);
    File outputFile = new File(getTempDir(), qualifiedFileName);
    try (InputStream inputStream = this.zipFile.getInputStream(entry)) {
      writeOutputFile(outputFile, inputStream);
    } catch (NullPointerException e) {
      throw new IOException("Could not locate file " + qualifiedFileName
          + " in " + this.zipFile.getName());
    }
    return outputFile;
  }

  private File getTempDir() throws IOException {
    return Files.createTempDirectory(null).toFile();
  }

  private void writeOutputFile(File outputFile, InputStream inputStream) throws IOException {
    Files.copy(inputStream, outputFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
    IOUtils.closeQuietly(inputStream);
  }

}
