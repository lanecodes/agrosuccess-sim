package repast.model.agrosuccess.anthro;

import java.util.Set;

/**
 * Given a collection of patches controlled by an agent, calculates the mass of wheat returned from
 * those patches over an annual simulation time step.
 *
 * @author Andrew Lane
 *
 */
public class FarmingReturnCalculator {

  private final double maxWheatYieldPerHaInKg;
  private final double rasterCellAreaInSqm;
  private final double maxFertility;

  /**
   * @param maxWheatYieldPerHaInKg Maximum wheat production per ha expressed as kg/ha
   * @param rasterCellAreaInSqm Area of each raster grid cell in m^2
   * @param maxFertility The maximum fertility value a grid cell can have. Used to scale fertility
   *        scores to the range [0, 1]
   */
  public FarmingReturnCalculator(double maxWheatYieldPerHaInKg, double rasterCellAreaInSqm,
      double maxFertility) {
    this.maxWheatYieldPerHaInKg = maxWheatYieldPerHaInKg;
    this.rasterCellAreaInSqm = rasterCellAreaInSqm;
    this.maxFertility = maxFertility;
  }

  /**
   * @param wheatPatches
   * @param precipitationMm
   * @return Wheat gathering returns from {@code wheatPatches} in kg
   */
  public double getReturns(Set<PatchOption> wheatPatches, double precipitationMm) {
    double result = 0;
    for (PatchOption patch : wheatPatches) {
      result += returnsForPatch(patch, precipitationMm);
    }
    return result;
  }

  /**
   * <p>
   * Implements equation <code>eq:wheat-returns</code> which reports the mass of wheat returned from
   * an individual wheat growing patch
   * </p>
   *
   * <pre>
   * \massWheatReturnFromCell{i} =
   *   \frac{\wheatProdRateInCell{i} \cdot \slopeModVal{i} \cdot \maxWheatYield \cdot \rasterCellArea}{10,000}
   * </pre>
   *
   * @param wheatPatch
   * @param precipitationMm
   * @return wheat gathering returns for {@code wheatPatch} in kg
   */
  private double returnsForPatch(PatchOption wheatPatch, double precipitationMm) {
    double prodRate = wheatProdRate(precipitationMm, wheatPatch.getFertility(), this.maxFertility);
    return prodRate * wheatPatch.getSlopeModValue() * this.maxWheatYieldPerHaInKg
        * this.rasterCellAreaInSqm / 10000;
  }

  /**
   * <p>
   * Implements <code>eq:wheat-production-rate-in-cell</code>, returning the wheat productivity rate
   * of a grid cell which depends on soil fertility and precipitation.
   * </p>
   *
   * <pre>
   * \wheatProdRateInCell{i} =
   *   \frac{(0.51 \ln(\precipInMm / 1000) + 1.03) + (0.19 \ln(\soilFertilityInCell{i}) + 1)}{2}
   * </pre>
   *
   * @param precipitationMm
   * @param fertility
   * @param maxFertility
   * @return wheat production rate
   */
  private static double wheatProdRate(double precipitationMm, double fertility,
      double maxFertility) {
    double precipTerm = 0.51 * Math.log(precipitationMm / 1000) + 1.03;
    double fertilityTerm = 0.19 * Math.log(fertility / maxFertility) + 1;
    return (precipTerm + fertilityTerm) / 2;
  }

  @Override
  public String toString() {
    return "FarmingReturnCalculator [maxWheatYieldPerHaInKg=" + maxWheatYieldPerHaInKg
        + ", rasterCellAreaInSqm=" + rasterCellAreaInSqm + "]";
  }

}
