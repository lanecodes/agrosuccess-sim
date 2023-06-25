package repast.model.agrosuccess.anthro;

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

public class WoodPlanCalculatorTest {

  private WoodPlanParams woodPlanParams;

  @Before
  public void setUp() {
    // Default wood plan params
    this.woodPlanParams = new WoodPlanParams(1100, 300000, 0.1);
  }

  @Test
  public void testEstimateNumWoodPatchesRequired() {
    WoodPlanCalculator woodPlanCalc = new WoodPlanCalculator(woodPlanParams, 625.0);
    int population = 5;

    int expectedNum = 3;
    assertEquals(expectedNum, woodPlanCalc.estimateNumWoodPatchesRequired(population));
  }

}
