package repast.model.agrosuccess.anthro;

import org.apache.log4j.Logger;

/**
 * Estimates the number of wheat patches a household needs to farm to meet its calorie requirements.
 *
 * <p>
 * Takes into account the number of members of the household, as well as the mass of wheat per
 * hectare of land farmed in the previous simulated year. Implements eq:my-num-wheat-plots.
 * </p>
 *
 * @author Andrew Lane
 */
public class FarmingPlanCalculator {
  final static Logger logger = Logger.getLogger(FarmingPlanCalculator.class);

  private final FarmingPlanParams farmingPlanParams;
  private final double rasterCellAreaInSqm;

  public FarmingPlanCalculator(FarmingPlanParams farmingPlanParams, double rasterCellAreaInSqm) {
    this.farmingPlanParams = farmingPlanParams;
    this.rasterCellAreaInSqm = rasterCellAreaInSqm;
  }

  /**
   * @param population Number of household members
   * @param massWheatPerHaLastYear Mass of wheat per hectare obtained in the previous simulated year
   *        (units kg/ha)
   * @return Household's estimate of the number of wheat plots they should aim to farm in the next
   *         simulated year
   */
  public int estimateNumWheatPatchesToFarm(int population, double massWheatPerHaLastYear) {
    double scalingFactor = 3.65 * 1000000;
    int targetNumPatches = (int) Math.round(
        (scalingFactor * this.farmingPlanParams.getEnergyPerPersonPerDay()
            * population * (1 + this.farmingPlanParams.getCropReseedProp()))
            / (this.farmingPlanParams.getEnergyPerKgWheat() * massWheatPerHaLastYear
                * this.rasterCellAreaInSqm * this.farmingPlanParams.getFarmerConScalar()));
    int maxNumPatches = maxNumPatchesGivenLabourAvail(population);
    if (targetNumPatches <= maxNumPatches) {
      return targetNumPatches;
    } else {
      logger.debug("Household is labour limited");
      return maxNumPatches;
    }
  }

  int maxNumPatchesGivenLabourAvail(int population) {
    double totPersonHoursForYear = this.farmingPlanParams.getLabourAvailability() * population;
    double maxHectaresOfWheat = totPersonHoursForYear / this.farmingPlanParams
        .getLabourRequirementWheat();
    double maxNumGridCellsOfWheat = maxHectaresOfWheat * 10000 / this.rasterCellAreaInSqm;
    return (int) Math.round(maxNumGridCellsOfWheat);
  }

}
