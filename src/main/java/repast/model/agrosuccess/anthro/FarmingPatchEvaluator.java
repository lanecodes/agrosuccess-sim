package repast.model.agrosuccess.anthro;

import repast.simphony.space.grid.GridPoint;

/**
 * Evaluate the value of land cover patches for farming
 *
 * @author Andrew Lane
 *
 */
public class FarmingPatchEvaluator implements PatchEvaluator {

  FarmPlotValueParams params;
  DistanceCalculator distCalc;

  /**
   * @param params Parameters controlling the relative importance of the different terms in the
   *        farming value calculation
   * @param distCalc Object which is aware of the simulation raster grid used for calculating the
   *        distances between land cover patches
   */
  public FarmingPatchEvaluator(FarmPlotValueParams params, DistanceCalculator distCalc) {
    this.params = params;
    this.distCalc = distCalc;
  }

  /**
   * <p>
   * Implements the equation {@code eq:patch-farming-value}:
   * </p>
   * <p>
   * (slopeValue * fetilityParam * fertility) - (distanceParam * distance) - (lccParam * lcc)
   * </p>
   * {@inheritDoc}
   */
  @Override
  public double getValue(PatchOption targetPatch, GridPoint villageLocation) {
    // (slopeValue * fetilityParam * fertility) - (distanceParam * distance) - (lccParam * lcc)
    double slopeValue = targetPatch.getSlopeModValue();
    double fertility = this.params.getFertilityParam() * targetPatch.getFertility();
    double distance = this.params.getDistanceParam()
        * distCalc.propMaxDist(villageLocation, targetPatch.getLocation());
    double landCoverCoversionCost =
        this.params.getLandCoverCoversionParam() * targetPatch.getLandCoverConversionCost();

    return slopeValue * fertility - distance - landCoverCoversionCost;
  }

  @Override
  public String toString() {
    return "FarmingPatchEvaluator [params=" + params + ", distCalc=" + distCalc + "]";
  }

}
