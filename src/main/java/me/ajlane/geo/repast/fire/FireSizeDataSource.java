package me.ajlane.geo.repast.fire;

import repast.simphony.data2.NonAggregateDataSource;
import repast.simphony.space.grid.GridPoint;

/**
 * Reports simulation tick and number of burnt cells in fires occurring during simulations
 *
 * <p>
 * For notes on how to configure Repast Simphony to use this class as a data source see
 * <code>reporting.html</code> in site documentation.
 * </p>
 *
 * @author Andrew Lane
 */
public class FireSizeDataSource implements NonAggregateDataSource {

  @Override
  public String getId() {
    return "fire_size";
  }

  @Override
  public Class<?> getDataType() {
    return String.class;
  }

  @Override
  public Class<?> getSourceType() {
    return FireReporter.class;
  }

  @SuppressWarnings("unchecked")
  @Override
  public Object get(Object obj) {
    FireReporter<GridPoint> reporter = (FireReporter<GridPoint>) obj;
    return convertReporterToString(reporter);
  }

  /**
   * @param reporter Fire reporter object
   * @return Formatted string to be written to a single line in the output file
   */
  private String convertReporterToString(FireReporter<GridPoint> reporter) {
    StringBuilder sb = new StringBuilder();
    reporter.forEach(fireEvent -> {
      sb.append("(tick=" + fireEvent.getTick()
          + ", nCells=" + fireEvent.getBurntCells().size() + "), ");
    });
    return sb.toString();
  }

}
