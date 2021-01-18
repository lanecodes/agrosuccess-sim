package repast.model.agrosuccess.anthro;

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

public class FarmingPlanCalculatorTest {

  private FarmingPlanParams farmingPlanParams;

  @Before
  public void setUp() {
    // Default farming plan parameters specified in the thesis
    this.farmingPlanParams = new FarmingPlanParams(2500, 3540, 0.75, 0.15);
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

}
