package me.ajlane.geo.repast.colonisation.csr;

/**
 * Container for model parameters used to configure the {@link CompletelySpatiallyRandomColoniser}.
 *
 * @author Andrew Lane
 *
 */
public class CompletelySpatiallyRandomParams {

  private final double baseRate;
  private final double spreadRate;

  /**
   * @param baseRate Proportion of the number of simulation grid cells that have juveniles added for
   *        each colonising species in each time step. Models the spread of seeds from outside the
   *        simulation grid.
   * @param spreadRate Proportion of grid cells containing mature land-cover that cause additional
   *        juvenile vegetation to be added to other cells. Models the spread of seeds from within
   *        the simulation grid.
   */
  public CompletelySpatiallyRandomParams(double baseRate, double spreadRate) {
    this.baseRate = baseRate;
    this.spreadRate = spreadRate;
  }

  /**
   * @return the baseRate
   */
  public double getBaseRate() {
    return baseRate;
  }

  /**
   * @return the spreadRate
   */
  public double getSpreadRate() {
    return spreadRate;
  }

  @Override
  public String toString() {
    return "CompletelySpatiallyRandomParams [baseRate=" + baseRate + ", spreadRate=" + spreadRate
        + "]";
  }

}
