package repast.model.agrosuccess.anthro;

/**
 * Parameters controlling the relative importance of the terms in the calculation of the value of
 * different farming plots.
 *
 * @see FarmingPatchEvaluator#getValue
 * @author Andrew Lane
 *
 */
public class FarmPlotValueParams {

  private double distanceParam;
  private double fertilityParam;
  private double landCoverCoversionParam;

  /**
   * @param distanceParam
   * @param fertilityParam
   * @param landCoverCoversionParam
   */
  public FarmPlotValueParams(double distanceParam, double fertilityParam,
      double landCoverCoversionParam) {
    this.distanceParam = distanceParam;
    this.fertilityParam = fertilityParam;
    this.landCoverCoversionParam = landCoverCoversionParam;
  }

  /**
   * @return the distanceParam
   */
  public double getDistanceParam() {
    return distanceParam;
  }

  /**
   * @return the fertilityParam
   */
  public double getFertilityParam() {
    return fertilityParam;
  }

  /**
   * @return the landCoverCoversionParam
   */
  public double getLandCoverCoversionParam() {
    return landCoverCoversionParam;
  }

  @Override
  public String toString() {
    return "FarmPlotValueParams [distanceParam=" + distanceParam + ", fertilityParam="
        + fertilityParam + ", landCoverCoversionParam=" + landCoverCoversionParam + "]";
  }

}
