/**
 *
 */
package repast.model.agrosuccess.reporting;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Use to generate a unique time-based ID for a simulation run.
 *
 * @author Andrew Lane
 *
 */
public class SimulationID {

  public static final DateTimeFormatter DATE_FORMATTER =
      DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss-SSS");

  private final String id;

  /**
   * @param siteName Name of the study site for which the simulation is being run.
   * @param time Time at which the simulation is being run.
   */
  public SimulationID(String siteName, LocalDateTime time) {
    id = siteName + "_" + time.format(DATE_FORMATTER);
  }

  /**
   * Simulation run time will be assumed to be the time at which this object is initialised.
   *
   * @param siteName Name of the study site for which the simulation is being run.
   */
  public SimulationID(String siteName) {
    LocalDateTime now = LocalDateTime.now();
    id = siteName + "_" + now.format(DATE_FORMATTER);
  }

  public String toString() {
    return id;
  }

}
