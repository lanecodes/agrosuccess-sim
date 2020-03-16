package repast.model.agrosuccess.anthro;

import java.util.Set;

/**
 * Given a collection of patches controlled by an agent, calculates the mass of wood returned from
 * those patches over an annual simulation time step.
 *
 * @author Andrew Lane
 *
 */
public class WoodReturnCalculator {

  private final double woodGatheringIntensityInKgPerSqm;
  private final double rasterCellAreaInSqm;

  /**
   * @param woodGatheringIntensityInKgPerSqm Mass of wood per square metre agents extract
   * @param rasterCellAreaInSqm Area of each raster grid cell in m^2
   */
  public WoodReturnCalculator(double woodGatheringIntensityInKgPerSqm, double rasterCellAreaInSqm) {
    this.woodGatheringIntensityInKgPerSqm = woodGatheringIntensityInKgPerSqm;
    this.rasterCellAreaInSqm = rasterCellAreaInSqm;
  }

  /**
   * Note that the amount of wood gathered from each patch is independent of the specific land cover
   * type of the patch <emph>but</emph> the patches used for wood gathering should have some
   * non-zero wood value.
   *
   * @param woodPatches
   * @return Mass of wood extracted from the patches in kg
   */
  public double getReturns(Set<PatchOption> woodPatches) {
    double woodPerCell = this.woodGatheringIntensityInKgPerSqm * this.rasterCellAreaInSqm;
    double result = 0;
    for (PatchOption patch : woodPatches) {
      if (patch.getWoodValue() > 0) {
        result += woodPerCell;
      } else {
        throw new MissingEnvrResourceException(
            "Wood gathering patch unexpectedly had zero wood value: " + patch);
      }
    }
    return result;
  }

  @Override
  public String toString() {
    return "WoodReturnCalculator [woodGatheringIntensityInKgPerSqm="
        + woodGatheringIntensityInKgPerSqm + ", rasterCellAreaInSqm=" + rasterCellAreaInSqm + "]";
  }

}
