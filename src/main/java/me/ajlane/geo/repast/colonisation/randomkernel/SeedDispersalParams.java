/**
 *
 */
package me.ajlane.geo.repast.colonisation.randomkernel;

/**
 * <p>
 * This class contains the parameters needed to control how seeds are distributed in the landscape.
 * Used by the {@code AcornPresenceProbGenerator} and {@code WindSeedPresenceProbGenerator} classes,
 * and passed to classes which create such objects, including the
 * {@code SpatiallyRandomSeedDisperser}.
 * </p>
 *
 * <h3>Acorn dispersal parameters</h3>
 * <p>
 * Following Millington et al. (2009) we assume the probability of an Oak acorn being present in a
 * cell at a given time is specified by the lognormal distribution where x is the distance from the
 * cell to the nearest oak-containing pixel:
 * </p>
 *
 * <p>
 * p(x) = \frac{1}{x \sigma \sqrt{2 \pi}} \exp\left[- \frac{(\ln(x) - \mu)^2}{2\sigma^2}\right]
 * </p>
 *
 * <p>
 * Pons and Pausas (2007) found that the acorn dispersal distance distribution which best fit
 * empirical data was the lognormal distribution with parameters sigma = 0.851 and mu = 3.844.
 * Following Millington et al. (2009), it is assumed that at any distance greater than Oak MD &gt
 * 550m the probability of finding an acorn is 0.001.
 * </p>
 *
 * <p>Summary of acorn dispersal parameters:</p>
 * <ul>
 *  <li>mu: Acorn dispersal distribution location parameter.</li>
 *  <li>sigma: Acorn dispersal distribution scale parameter.</li>
 *  <li>Oak MD: Maximum distance for which probability of acorn presence is lognormally
 *      distributed.</li>
 * </ul>
 *
 * <h3>Wind dispersed seed dispersal parameters</h3>
 * <p>
 * Following Millington et al. (2009) we assume the probability of a wind dispersed seed being found
 * in a cell at a given time is determined by the following probability-assigning procedure:
 * </p>
 *
 *  p(x) = 0.95 if x &#8804 ED <br>
 *  p(x) = exp^{-b/MD * x} if  ED &lt x &#8804 MD<br>
 *  p(x) = 0.001 if x &gt MD <br>
 *
 * <p>Summary of wind dispersal parameters:</p>
 * <ul>
 *  <li>b: Distance-decrease parameter controlling the rate at which the probability of wind
 *      dispersed seed presence decreases as one moves further from a seed source.
 *  <li>ED: Minimum distance for which probability of wind dispersed seed presence is exponentially
 *      distributed.</li>
 *  <li>MD: Maximum distance for which probability of wind dispersed seed presence is exponentially
 *      distributed.</li>
 * </ul>
 *
 * <p>
 * Millington et al. (2009) used the following wind dispersed seed parameters: b=5, ED=75m, and
 * MD=100m.
 * <p>
 *
 * <h3>References</h3>
 * <p>
 * Pons, J., & Pausas, J. G. (2007). Acorn dispersal estimated by radio-tracking.
 * Oecologia, 153(4), 903–911. https://doi.org/10.1007/s00442-007-0788-x
 * </p>
 *
 * <p>
 * Millington, J. D. A., Wainwright, J., Perry, G.
 * L. W., Romero-Calcerrada, R., & Malamud, B. D. (2009). Modelling
 * Mediterranean landscape succession-disturbance dynamics: A landscape
 * fire-succession model. Environmental Modelling and Software, 24(10),
 * 1196–1208. https://doi.org/10.1016/j.envsoft.2009.03.013
 * <p>
 *
 * @author Andrew Lane
 *
 */
public class SeedDispersalParams {
  double acornLocationParam, acornScaleParam, acornMaxLognormalDist;
  double windDistDecreaseParam, windMinExpDist, windMaxExpDist;

  /**
   * @param acornLocationParam Acorn dispersal distribution location parameter, mu.
   * @param acornScaleParam Acorn dispersal distribution scale parameter, sigma.
   * @param acornMaxLognormalDist Maximum distance for which probability of acorn presence is
   *        lognormally distributed, Oak MD.
   * @param windDistDecreaseParam Distance-decrease parameter controlling the rate at which the
   *        probability of wind dispersed seed presence decreases as one moves further from a seed
   *        source.
   * @param windMinExpDist Minimum distance for which probability of wind dispersed seed presence is
   *        exponentially distributed.
   * @param windMaxExpDist Maximum distance for which probability of wind dispersed seed presence is
   *        exponentially distributed.
   */
  public SeedDispersalParams(double acornLocationParam, double acornScaleParam,
      double acornMaxLognormalDist, double windDistDecreaseParam, double windMinExpDist,
      double windMaxExpDist) {

    this.acornLocationParam = acornLocationParam;
    this.acornScaleParam = acornScaleParam;
    this.acornMaxLognormalDist = acornMaxLognormalDist;

    this.windDistDecreaseParam = windDistDecreaseParam;
    this.windMinExpDist = windMinExpDist;
    this.windMaxExpDist = windMaxExpDist;

  }

  public double getAcornLocationParam() {
    return acornLocationParam;
  }

  public double getAcornScaleParam() {
    return acornScaleParam;
  }

  public double getAcornMaxLognormalDist() {
    return acornMaxLognormalDist;
  }

  public double getWindDistDecreaseParam() {
    return windDistDecreaseParam;
  }

  public double getWindMinExpDist() {
    return windMinExpDist;
  }

  public double getWindMaxExpDist() {
    return windMaxExpDist;
  }

  @Override
  public String toString() {
    return "SeedDispersalParams["
        + "acornLocationParam=" + String.format("%.3f", this.acornLocationParam) + ", "
        + "acornScaleParam=" + String.format("%.3f", this.acornScaleParam) + ", "
        + "acornMaxLognormalDist=" + String.format("%.3f", this.acornMaxLognormalDist) + ", "
        + "windDistDecreaseParam=" + String.format("%.3f", this.windDistDecreaseParam) + ", "
        + "windMinExpDist=" + String.format("%.3f", this.windMinExpDist) + ", "
        + "windMaxExpDist=" + String.format("%.3f", this.windMaxExpDist) + "]";
  }

  @Override
  public boolean equals(Object other){
    if (other == null) return false;
    if (other == this) return true;
    if (!(other instanceof SeedDispersalParams)) return false;
    SeedDispersalParams otherSdp = (SeedDispersalParams)other;
    if (otherSdp.getAcornLocationParam() == this.getAcornLocationParam()
        && otherSdp.getAcornScaleParam() == this.getAcornScaleParam()
        && otherSdp.getAcornMaxLognormalDist() == this.getAcornMaxLognormalDist()
        && otherSdp.getWindDistDecreaseParam() == this.getWindDistDecreaseParam()
        && otherSdp.getWindMinExpDist() == this.getWindMinExpDist()
        && otherSdp.getWindMaxExpDist() == this.getWindMaxExpDist()) {
      return true;
    } else return false;
    }

}
