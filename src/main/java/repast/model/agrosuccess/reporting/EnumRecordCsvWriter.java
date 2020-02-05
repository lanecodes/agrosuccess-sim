package repast.model.agrosuccess.reporting;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.opencsv.CSVWriter;

/**
 * Accumulates {@code Map} object which associate enumeration constants with data. Call
 * {@code flush()} to write this data to a csv file.
 *
 * <dl>
 * <dt>Notes:</dt>
 * <dd>Implementation follows advice given <a href="https://stackoverflow.com/questions/2205891">on
 * SO</a> for extracting the list of enumeration constants from a generic Enum.</dd>
 * <dt>TODO:</dt>
 * <dd>
 * <ul>
 * <li>Think about implementing buffering such that constructor accepts a number of records to
 * accept before it writes to file.</li>
 * </ul>
 * </dd>
 * </dl>
 *
 * @author Andrew Lane
 *
 * @param <E> Type of enumeration whose values will be used as column names in the produced csv file.
 * @param <V> Type of the values entered into the rows of the csv file.
 */
public class EnumRecordCsvWriter<E extends Enum<E>, V> implements RecordWriter<E, V> {

  Class<E> enumType;
  CSVWriter csvWriter;
  List<Map<E, V>> records;

  /**
   * @param enumType The {@code Enum} whose values will be used as column headers in the produced csv file.
   * @param outputFile Name of the generated csv file.
   * @throws IOException
   */
  public EnumRecordCsvWriter(Class<E> enumType, File outputFile) throws IOException {
    this.enumType = enumType;
    this.csvWriter = new CSVWriter(new FileWriter(outputFile));
    this.records = new ArrayList<>();
  }

  public void add(Map<E, V> record) {
    this.records.add(record);
  }

  public void flush() throws IOException {
    List<String> fields = new ArrayList<>();
    for (E entry : this.enumType.getEnumConstants()) {
      fields.add(entry.toString());
    }

    this.csvWriter.writeNext(fields.toArray(new String[0]));

    for (Map<E, V> record : this.records) {
      List<String> recordValues = new ArrayList<>();
      for (E fieldId : this.enumType.getEnumConstants()) {
        String fieldValue = record.get(fieldId).toString();
        recordValues.add(fieldValue);
      }
      this.csvWriter.writeNext(recordValues.toArray(new String[0]));
    }
    this.csvWriter.flush();
  }

}
