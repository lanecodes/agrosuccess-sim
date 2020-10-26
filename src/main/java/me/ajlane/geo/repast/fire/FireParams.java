package me.ajlane.geo.repast.fire;

public class FireParams {

  private double meanNumFiresPerYear;
  private LcfReplicate lcfReplicate;

  public FireParams(double meanNumFiresPerYear, LcfReplicate lcfReplicate) {
    this.meanNumFiresPerYear = meanNumFiresPerYear;
    this.lcfReplicate = lcfReplicate;
  }

  /**
   * @return Mean number of fires occurring in the simulation grid in each simulated year.
   *         Corresponds to λ in Millington et al. 2009 Eq. (7). Units are fires/year.
   */
  public double getMeanNumFiresPerYear() {
    return this.meanNumFiresPerYear;
  }

  /**
   * Allowed values are:
   *
   * <ul>
   * <li>TF1</li>
   * <li>TF2</li>
   * <li>TF3</li>
   * <li>TF4</li>
   * <li>Default</li>
   * <li>TF5</li>
   * <li>TF6</li>
   * <li>TF7</li>
   * <li>TF8</li>
   * </ul>
   *
   * See Table 6 in Millington et al. 2009.
   *
   * @return The {@code LcfReplicate} to use in the simulation run
   */
  public LcfReplicate getLcfReplicate() {
    return this.lcfReplicate;
  }

}
