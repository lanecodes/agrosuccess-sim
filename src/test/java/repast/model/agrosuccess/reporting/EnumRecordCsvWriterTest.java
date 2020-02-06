/**
 *
 */
package repast.model.agrosuccess.reporting;

import static org.junit.Assert.*;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import com.opencsv.CSVReader;

/**
 * @author Andrew Lane
 *
 */
public class EnumRecordCsvWriterTest {

  public enum SimpleEnum {
    A, B, C;
  }

  @Rule
  public TemporaryFolder testDir = new TemporaryFolder();

  @Test
  public void testWriteDoubles() throws IOException {
    File testFile = testDir.newFile();
    RecordWriter<SimpleEnum, Double> writer;
    writer = new EnumRecordCsvWriter<SimpleEnum, Double>(SimpleEnum.class, testFile);

    Map<SimpleEnum, Double> record1 = new HashMap<>();
    record1.put(SimpleEnum.A, 0.5);
    record1.put(SimpleEnum.C, 1.2);
    record1.put(SimpleEnum.B, -2.5);

    Map<SimpleEnum, Double> record2 = new HashMap<>();
    record2.put(SimpleEnum.C, 3.2);
    record2.put(SimpleEnum.B, -5.8);
    record2.put(SimpleEnum.A, 4.1);

    writer.add(record1);
    writer.add(record2);
    writer.flush();

    try (Reader reader = Files.newBufferedReader(testFile.toPath());
        CSVReader csvReader = new CSVReader(reader);) {
      String[] nextRecord;
      nextRecord = csvReader.readNext();
      assertEquals(nextRecord[0], "A");
      assertEquals(nextRecord[1], "B");
      assertEquals(nextRecord[2], "C");

      nextRecord = csvReader.readNext();
      assertEquals(nextRecord[0], "0.5");
      assertEquals( nextRecord[1], "-2.5");
      assertEquals(nextRecord[2], "1.2");

      nextRecord = csvReader.readNext();
      assertEquals(nextRecord[0], "4.1");
      assertEquals(nextRecord[1], "-5.8");
      assertEquals(nextRecord[2], "3.2");

      assertNull(csvReader.readNext());

    }

  }
}
