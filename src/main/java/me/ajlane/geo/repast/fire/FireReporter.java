package me.ajlane.geo.repast.fire;

import me.ajlane.reporting.AbstractReporter;

/**
 * Collects data about fire events in the simulation and reports on them when required.
 *
 * <p>
 * Once the {@code FireReporter} has <emph>reported</emph> a fire, either by calling
 * {@link #getNextFire()} or by iterating over it in a for-each loop or by calling
 * {@code FireReporter#forEach(java.util.function.Consumer)}, the reporter 'forgets' about the fire
 * and responsibility for handling the returned data falls to the client code.
 * </p>
 *
 * @author Andrew Lane
 *
 * @param <T> Type of objects used to specify the locations of grid cells where fires took place
 */
public class FireReporter<T>
    extends AbstractReporter<FireEvent<T>>
    implements Iterable<FireEvent<T>> {

  public FireReporter() {
    super();
  }

  /**
   * Makes the reporter aware of a fire to be reported later.
   *
   * @param fire to be reported
   */
  public void addFire(FireEvent<T> fire) {
    this.internalQueue.add(fire);
  }

  /**
   * Retrieves and removes the next fire to be reported, or returns {@code null} if there are no
   * more fires to report.
   *
   * @return The next fire to report
   */
  public FireEvent<T> getNextFire() {
    return this.internalQueue.poll();
  }

}
