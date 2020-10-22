package repast.model.agrosuccess.empirical;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import org.junit.Test;

public class ZipFileExtractorTest {

  File testFile = new File("src/test/resources/init_lct_maps.zip");

  @Test
  public void testConstructor() throws IOException {
    new ZipFileExtractor(testFile);
  }

  @Test
  public void testCanExtractExistingFile() throws IOException {
    ZipFileExtractor extractor = new ZipFileExtractor(testFile);
    File extractedFile = extractor.extract("init-landcover1.csv");
    System.out.println(extractedFile.getAbsolutePath());
    assertTrue(extractedFile.exists());

    String[] expectedLines = {
        "lct_name,lct_code,target_prop,landscape_prop,score",
        "shrubland,5,0.2880549553389876,0.28787744496638973,0.9998224896274022",
        "pine,6,0.6949331922211932,0.6951238285386413,0.9998093636825519",
        "deciduous,8,0.0,0.0,1.0",
        "oak,9,0.017011852439819106,0.016998726494968957,0.9999868740551499"
    };

    Scanner reader = new Scanner(extractedFile);
    for (int i = 0; i < expectedLines.length; i++) {
      assertTrue(reader.hasNext());
      assertEquals(expectedLines[i], reader.nextLine());
    }
    assertFalse(reader.hasNext());
    reader.close();
  }

  @Test(expected = IOException.class)
  public void testExceptionThrownIfNonExistantFileSpecified() throws IOException {
    ZipFileExtractor extractor = new ZipFileExtractor(testFile);
    extractor.extract("file-i-forgot-to-add.csv");
  }

}
