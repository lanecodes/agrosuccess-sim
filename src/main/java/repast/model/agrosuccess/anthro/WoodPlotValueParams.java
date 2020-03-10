package repast.model.agrosuccess.anthro;

/**
 * Parameters controlling the relative importance of the terms in the calculation of the value of
 * different wood gathering plots.
 *
 * @see WoodPatchEvaluator#getValue
 * @author Andrew Lane
 *
 */
public class WoodPlotValueParams {

  private double distanceParam;

  /**
   * @param distanceParam
   */
  public WoodPlotValueParams(double distanceParam) {
    this.distanceParam = distanceParam;
  }

  /**
   * @return the distanceParam
   */
  public double getDistanceParam() {
    return distanceParam;
  }

  @Override
  public String toString() {
    return "WoodPlotValueParams [distanceParam=" + distanceParam + "]";
  }

}
