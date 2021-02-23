package repast.model.agrosuccess.anthro;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

public class FarmingPlanCalculatorTest {

  private FarmingPlanParams farmingPlanParams;

  @Before
  public void setUp() {
    // Default farming plan parameters specified in the thesis
    this.farmingPlanParams = new FarmingPlanParams(2500, 3540, 0.75, 0.15, 365, 50);
  }

  @Test
  public void testEstimateNumWheatPatchesToFarm() {
    FarmingPlanCalculator farmingPlanCalc = new FarmingPlanCalculator(farmingPlanParams, 625.0);
    int population = 5;
    double massWheatPerHaGrownPrevYear = 3500;

    double numerator = 3.65 * 1000000 * 2500 * 5 * (1 + 0.15);
    double denominator = 3540 * 3500 * 625.0 * 0.75;
    int expectedNum = (int) Math.round(numerator / denominator);
    assertEquals(expectedNum,
        farmingPlanCalc.estimateNumWheatPatchesToFarm(population, massWheatPerHaGrownPrevYear));
  }

  /**
   * <ul>
   * <li>Estimated labour requirement of \SI{50}{\person\day\per\ha\per\year} for wheat
   * agriculture.</li>
   * <li>Labour availability of \SI{365}{\person\day\per\year}</li>
   * <li>As a worked example, consider a household of 5 individuals at the Navarrés study site.</li>
   * <li>The simulation grid for the study site has grid cell area \text{\SI{707}{\metre^2}} =
   * \text{\SI{0.0707}{\hectare}</li>
   * <li>The household has a maximum labour resource of $\text{SI{5}{\person} \times
   * \text{SI{365}{\day\per\year}} = \text{\SI{1825}{\person\day\per\year}$, which implies the
   * maximum area of land they can cultivate for wheat is \SI{36.5}{\hectare}. This corresponds to
   * 516 simulation grid cells.</li>
   */
  @Test
  public void testMaxNumPatchesGivenLabourAvail() {
    FarmingPlanCalculator farmingPlanCalc = new FarmingPlanCalculator(farmingPlanParams, 707);
    assertEquals(516, farmingPlanCalc.maxNumPatchesGivenLabourAvail(5));
  }

  /**
   * Test that when the number of cells that can be farmed is labour limited, this is reflected in
   * the calculated farming plan.
   */
  @Test
  public void testLabourLimitedEstimateNumWheatPatchesToFarm() {
    // Suppose wheat took 5000 person hours per ha per year to raise
    farmingPlanParams = new FarmingPlanParams(2500, 3540, 0.75, 0.15, 365, 5000);

    FarmingPlanCalculator farmingPlanCalc = new FarmingPlanCalculator(farmingPlanParams, 625.0);
    int population = 5;
    double massWheatPerHaGrownPrevYear = 3500;

    double numerator = 3.65 * 1000000 * 2500 * 5 * (1 + 0.15);
    double denominator = 3540 * 3500 * 625.0 * 0.75;
    int nonLabourLimitedExpectedNum = (int) Math.round(numerator / denominator);
    // 365 * 5 / 5000 * 10000 / 625
    assertTrue(farmingPlanCalc.estimateNumWheatPatchesToFarm(population,
        massWheatPerHaGrownPrevYear) < nonLabourLimitedExpectedNum);
    assertEquals(6, farmingPlanCalc.estimateNumWheatPatchesToFarm(population,
        massWheatPerHaGrownPrevYear));
  }

}
