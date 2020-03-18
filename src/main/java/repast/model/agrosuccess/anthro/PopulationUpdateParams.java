package repast.model.agrosuccess.anthro;

import repast.model.agrosuccess.params.ParameterOutOfBoundsException;

/**
 * Parameters needed to specify how household populations are updated as a consequence of failing to
 * meet, meeting, or exceeding resource objectives.
 *
 * @author Andrew Lane
 *
 */
public class PopulationUpdateParams {

  private final double initBirthRate;
  private final double initDeathRate;
  private final double targetYieldBufferFactor;
  private final double maxBirthRateDelta;
  private final double maxBirthRate;
  private final double minBirthRate;
  private final double maxDeathRateDelta;
  private final double maxDeathRate;
  private final double minDeathRate;

  /**
   * @param initBirthRate Birth rate at the beginning of the simulation, $\initBirthRate{} \in
   *        [0,1]$, such that $\initBirthRate{} = 1$ implies a birth for every member of the
   *        household. Note the domain of $\initBirthRate{}$ limits births to once per year per
   *        agent
   * @param initDeathRate Death rate at the beginning of the simulation, $\initDeathRate{} \in
   *        [0,1]$, such that $\initDeathRate{} = 1$ implies every member of the household dies
   * @param targetYieldBufferFactor The target yield buffer factor, $\targetYieldBufferFac{} \in
   *        [0,1]$, which controls the range of acceptable total calories produced compared to the
   *        amount required such that birth and death rates are unaffected
   * @param maxBirthRateDelta Maximum amount by which birth rate can increase/ decrease in a single
   *        year, $\maxBirthRateDelta{} \in [0,\maxBirthRate{}]$
   * @param maxBirthRate Maximum possible birth rate, $\maxBirthRate{} \in [\minBirthRate{},1]$
   * @param minBirthRate Minimum possible birth rate, $\minBirthRate{} \in [0,\maxBirthRate{}]$
   * @param maxDeathRateDelta Maximum amount by which death rate can increase/ decrease in a single
   *        year, $\maxDeathRateDelta{} \in [0,\maxDeathRate{}]$
   * @param maxDeathRate Maximum possible death rate, $\maxDeathRate{} \in [\minDeathRate{},1]$
   * @param minDeathRate Minimum possible death rate, $\minDeathRate{} \in [0,\maxDeathRate{}]$
   */
  public PopulationUpdateParams(double initBirthRate, double initDeathRate,
      double targetYieldBufferFactor, double maxBirthRateDelta, double maxBirthRate,
      double minBirthRate, double maxDeathRateDelta, double maxDeathRate, double minDeathRate) {
    this.initBirthRate = checkBounds("initBirthRate", initBirthRate, 0, 1);
    this.initDeathRate = checkBounds("initDeathRate", initDeathRate, 0, 1);
    this.targetYieldBufferFactor =
        checkBounds("targetYieldBufferFactor", targetYieldBufferFactor, 0, 1);
    this.maxBirthRateDelta = checkBounds("maxBirthRateDelta", maxBirthRateDelta, 0, maxBirthRate);
    this.maxBirthRate = checkBounds("maxBirthRate", maxBirthRate, minBirthRate, 1);
    this.minBirthRate = checkBounds("minBirthRate", minBirthRate, 0, maxBirthRate);
    this.maxDeathRateDelta = checkBounds("maxDeathRateDelta", maxDeathRateDelta, 0, maxDeathRate);
    this.maxDeathRate = checkBounds("maxDeathRate", maxDeathRate, minDeathRate, 1);
    this.minDeathRate = checkBounds("minDeathRate", minDeathRate, 0, maxDeathRate);
  }

  private double checkBounds(String paramName, double param, double lowerBound, double upperBound) {
    if (param < lowerBound || param > upperBound) {
      throw new ParameterOutOfBoundsException(paramName, param, lowerBound, upperBound);
    }
    return param;
  }

  /**
   * @return the initBirthRate
   */
  public double getInitBirthRate() {
    return initBirthRate;
  }

  /**
   * @return the initDeathRate
   */
  public double getInitDeathRate() {
    return initDeathRate;
  }

  /**
   * @return the targetYieldBufferFactor
   */
  public double getTargetYieldBufferFactor() {
    return targetYieldBufferFactor;
  }

  /**
   * @return the maxBirthRateDelta
   */
  public double getMaxBirthRateDelta() {
    return maxBirthRateDelta;
  }

  /**
   * @return the maxBirthRate
   */
  public double getMaxBirthRate() {
    return maxBirthRate;
  }

  /**
   * @return the minBirthRate
   */
  public double getMinBirthRate() {
    return minBirthRate;
  }

  /**
   * @return the maxDeathRateDelta
   */
  public double getMaxDeathRateDelta() {
    return maxDeathRateDelta;
  }

  /**
   * @return the maxDeathRate
   */
  public double getMaxDeathRate() {
    return maxDeathRate;
  }

  /**
   * @return the minDeathRate
   */
  public double getMinDeathRate() {
    return minDeathRate;
  }

  @Override
  public String toString() {
    return "PopulationUpdateParams [initBirthRate=" + initBirthRate + ", initDeathRate="
        + initDeathRate + ", targetYieldBufferFactor=" + targetYieldBufferFactor
        + ", maxBirthRateDelta=" + maxBirthRateDelta + ", maxBirthRate=" + maxBirthRate
        + ", minBirthRate=" + minBirthRate + ", maxDeathRateDelta=" + maxDeathRateDelta
        + ", maxDeathRate=" + maxDeathRate + ", minDeathRate=" + minDeathRate + "]";
  }

}
