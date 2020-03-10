package repast.model.agrosuccess.anthro;

import repast.simphony.space.grid.GridPoint;

/**
 * Evaluate the value of land cover patches for firewood gathering
 *
 * @author Andrew Lane
 *
 */
public class WoodPatchEvaluator implements PatchEvaluator {

  WoodPlotValueParams params;
  DistanceCalculator distCalc;

  /**
   * @param params Parameters controlling the relative importance of the different terms in the
   *        wood value calculation
   * @param distCalc Object which is aware of the simulation raster grid used for calculating the
   *        distances between land cover patches
   */
  public WoodPatchEvaluator(WoodPlotValueParams params, DistanceCalculator distCalc) {
    this.params = params;
    this.distCalc = distCalc;
  }

  /**
   * <p>
   * Implements the equation {@code eq:wood-value}:
   * </p>
   * <p>
   * (1 / 1 + distanceParam) * (woodValue + distanceParam * (1 - distance))
   * </p>
   * {@inheritDoc}
   */
  @Override
  public double getValue(PatchOption targetPatch, GridPoint villageLocation) {
    double woodValue = targetPatch.getWoodValue();
    double distParam = this.params.getDistanceParam();
    double dist = this.distCalc.propMaxDist(villageLocation, targetPatch.getLocation());
    return (1 / (1 + distParam)) * (woodValue + distParam * (1 - dist));
  }

}
