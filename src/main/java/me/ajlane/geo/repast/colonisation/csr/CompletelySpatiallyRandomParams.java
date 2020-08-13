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

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    long temp;
    temp = Double.doubleToLongBits(baseRate);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(spreadRate);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    CompletelySpatiallyRandomParams other = (CompletelySpatiallyRandomParams) obj;
    if (Double.doubleToLongBits(baseRate) != Double.doubleToLongBits(other.baseRate))
      return false;
    if (Double.doubleToLongBits(spreadRate) != Double.doubleToLongBits(other.spreadRate))
      return false;
    return true;
  }

}
