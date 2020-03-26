package repast.model.agrosuccess.anthro;

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import repast.model.agrosuccess.params.ParameterOutOfBoundsException;

public class FarmingPlanParamsTest {

  private static double TOLERANCE = 0.001;
  private FarmingPlanParams testParams;

  @Before
  public void setUp() {
    this.testParams = new FarmingPlanParams(2500, 3540, 0.75, 0.15);
  }

  @Test
  public void testGetEnergyPerPersonPerDay() {
    assertEquals(2500., this.testParams.getEnergyPerPersonPerDay(), TOLERANCE);
  }

  @Test
  public void testGetEnergyPerKgWheat() {
    assertEquals(3540., this.testParams.getEnergyPerKgWheat(), TOLERANCE);
  }

  @Test
  public void testGetFarmerConScalar() {
    assertEquals(0.75, this.testParams.getFarmerConScalar(), TOLERANCE);
  }

  @Test
  public void testGetCropReseedProp() {
    assertEquals(0.15, this.testParams.getCropReseedProp(), TOLERANCE);
  }

  @Test(expected = ParameterOutOfBoundsException.class)
  public void testErrorCropReseedProp() {
    new FarmingPlanParams(2500, 3540, 0.75, 1.1);
  }

  @Test(expected = ParameterOutOfBoundsException.class)
  public void testErrorEnergyPerPersonPerDay() {
    new FarmingPlanParams(-40, 3540, 0.75, 0.15);
  }

}
