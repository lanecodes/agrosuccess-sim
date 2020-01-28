package repast.model.agrosuccess.reporting;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.opencsv.CSVWriter;
import repast.model.agrosuccess.AgroSuccessCodeAliases.Lct;

public class LctProportionWriter {

  CSVWriter csvWriter;
  List<Map<Lct, Double>> proportionRecords;

  public LctProportionWriter(File outputFile) throws IOException {
    this.csvWriter = new CSVWriter(new FileWriter(outputFile));
    this.proportionRecords = new ArrayList<>();
  }

  public void add(Map<Lct, Double> record) {
    this.proportionRecords.add(record);
  }

  public void writeFile() throws IOException {
    List<String> fields = new ArrayList<>();
    Map<Lct, Double> firstRow = this.proportionRecords.get(0);
    for (Lct entry : firstRow.keySet()) {
      fields.add(entry.toString());
    }

    this.csvWriter.writeNext(fields.toArray(new String[0]));

    for (Map<Lct, Double> record : this.proportionRecords) {
      List<String> entries = new ArrayList<>();
      for (int i=0; i<fields.size(); i++) {
        entries.add(record.get(Lct.valueOf(fields.get(i))).toString());
      }
      this.csvWriter.writeNext(entries.toArray(new String[0]));
    }
    this.csvWriter.flush();
  }

}
