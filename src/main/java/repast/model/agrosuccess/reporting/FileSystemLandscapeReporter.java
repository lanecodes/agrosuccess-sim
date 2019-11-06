package repast.model.agrosuccess.reporting;

import java.io.File;
import org.apache.commons.lang3.StringUtils;
import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ISchedule;

public class FileSystemLandscapeReporter extends LandscapeReporter {  
  
  private File outputDir;  
  private File outputCsvFile;

  public FileSystemLandscapeReporter(Context<Object> context, ISchedule schedule, String simID, 
      File outputDir, boolean writeLctMap, boolean writeLctProps, int bufferSize) {
    super(context, schedule, simID, writeLctMap, writeLctProps, bufferSize);
    this.outputDir = outputDir;
    this.outputDir.mkdirs(); // ensure outputDir exists
    this.outputCsvFile = new File(outputDir, simID + ".csv");    
  }
  
  private void writeLctPropsToCsv() {    
  }
  
  @Override
  void reportLctProportions() {
    this.lctPropsQueue.add(this.lctPropAgg.getLctProportions());
    if (this.schedule.getTickCount() % this.bufferSize == 0) {
      writeLctPropsToCsv();
    }
  }
  
  
  /**
   * @param Number of digits to pad the timestep number with. E.g. for a simulation with ID "run10" 
   *    to make timestep 345 rave ID "run10_00345" set padNum to 5.
   * @return A unique identifier for the timestep constructed out of both the simulation ID and 
   *    the timestep.
   */
  private String getTimeStepID(int padNum) {
    Integer ts = (int) this.schedule.getTickCount();
    return this.simID + "_" + StringUtils.leftPad(ts.toString(), 5, "0");
  }
  
  private String getTimeStepID() {
    return getTimeStepID(5);
  }

  @Override
  void reportLctMap() {
    // TODO Auto-generated method stub
  }
}