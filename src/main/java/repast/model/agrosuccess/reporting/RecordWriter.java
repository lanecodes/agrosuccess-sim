/**
 *
 */
package repast.model.agrosuccess.reporting;

import java.io.IOException;
import java.util.Map;

/**
 * Accumulates {@code Map} objects for reporting purposes. Must call {@code flush()} to write the
 * data in the maps to disk.
 *
 * @author Andrew Lane
 *
 * @param <K> Type of the keys in the maps to be accumulated.
 * @param <V> Type of the values in the maps to be accumulated.
 *
 */
public interface RecordWriter<K, V> {
  void add(Map<K, V> record);

  void flush() throws IOException;
}
