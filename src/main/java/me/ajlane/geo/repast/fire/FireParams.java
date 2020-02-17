package me.ajlane.geo.repast.fire;

public class FireParams {

  private double climateIgnitionScalingParam;
  private LcfReplicate lcfReplicate;

  public FireParams(double climateIgnitionScalingParam, LcfReplicate lcfReplicate) {
    this.climateIgnitionScalingParam = climateIgnitionScalingParam;
    this.lcfReplicate = lcfReplicate;
  }

  /**
   * @return Climate ignition scaling parameter corresponding to m in Millington et al. 2009 Eq.
   *         (7). Units are mm / °C.
   */
  public double getClimateIgnitionScalingParam() {
    return this.climateIgnitionScalingParam;
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
