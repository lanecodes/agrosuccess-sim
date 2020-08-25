package me.ajlane.geo.repast.fire;

import java.util.List;
import repast.simphony.engine.schedule.ISchedule;
import repast.simphony.space.grid.GridPoint;

/**
 * Wraps a {@code FireSpreader} to log fire events with a {@code FireReporter}
 *
 * <p>
 * We make a {@code FireSpreader} responsible for logging data including the location of fire events
 * rather than the {@link FireManager} because {@code FireSpreader} has immediate access to the data
 * about where fires are located in the landscape. Assignment of responsibility in this way complies
 * with the <a href=
 * "https://en.wikipedia.org/wiki/GRASP_(object-oriented_design)#Information_expert"><emph>Information
 * Expert</emph></a> design principle.
 * </p>
 *
 * @author Andrew Lane
 */
public class ReportingRepastFireSpreader implements FireSpreader<GridPoint> {

  private final FireSpreader<GridPoint> fireSpreader;
  private final FireReporter<GridPoint> reporter;
  private final ISchedule simSchedule;

  /**
   * @param fireSpreader {@code FireSpreader} to decorate
   * @param fireReporter Reporter used to log fire events
   * @param simSchedule Simulation schedule, used to ascertain tick number
   */
  public ReportingRepastFireSpreader(FireSpreader<GridPoint> fireSpreader,
      FireReporter<GridPoint> reporter, ISchedule simSchedule) {
    this.fireSpreader = fireSpreader;
    this.reporter = reporter;
    this.simSchedule = simSchedule;
  }

  @Override
  public List<GridPoint> spreadFire(GridPoint ignitionPoint) {
    List<GridPoint> cellsBurntInFire = this.fireSpreader.spreadFire(ignitionPoint);
    logFireEventWithReporter(ignitionPoint, cellsBurntInFire);
    return cellsBurntInFire;
  }

  private void logFireEventWithReporter(GridPoint ignitionPoint, List<GridPoint> allBurntCells) {
    FireEvent<GridPoint> fireEvent = new FireEvent<>((int) this.simSchedule.getTickCount(),
        ignitionPoint, allBurntCells);
    this.reporter.addFire(fireEvent);
  }

}
