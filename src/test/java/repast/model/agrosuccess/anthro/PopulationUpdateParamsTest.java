package repast.model.agrosuccess.anthro;

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import repast.model.agrosuccess.params.ParameterOutOfBoundsException;

public class PopulationUpdateParamsTest {

  private static double TOLERANCE = 0.0001;
  private PopulationUpdateParams testParams;

  @Before
  public void setUp() {
    this.testParams = PopulationUpdateParams.builder().birthRateParams(0.5, 0.1, 0.2, 0.8)
        .deathRateParams(0.6, 0.15, 0.1, 0.9).targetYieldBufferFactor(0.1).build();
  }

  @Test
  public void testGetInitBirthRate() {
    assertEquals(0.5, this.testParams.getInitBirthRate(), TOLERANCE);
  }

  @Test
  public void testGetInitDeathRate() {
    assertEquals(0.6, this.testParams.getInitDeathRate(), TOLERANCE);
  }

  @Test
  public void testGetTargetYieldBufferFactor() {
    assertEquals(0.1, this.testParams.getTargetYieldBufferFactor(), TOLERANCE);
  }

  @Test
  public void testGetMaxBirthRateDelta() {
    assertEquals(0.1, this.testParams.getMaxBirthRateDelta(), TOLERANCE);
  }

  @Test
  public void testGetMaxBirthRate() {
    assertEquals(0.8, this.testParams.getMaxBirthRate(), TOLERANCE);
  }

  @Test
  public void testGetMinBirthRate() {
    assertEquals(0.2, this.testParams.getMinBirthRate(), TOLERANCE);
  }

  @Test
  public void testGetMaxDeathRateDelta() {
    assertEquals(0.15, this.testParams.getMaxDeathRateDelta(), TOLERANCE);
  }

  @Test
  public void testGetMaxDeathRate() {
    assertEquals(0.9, this.testParams.getMaxDeathRate(), TOLERANCE);
  }

  @Test
  public void testGetMinDeathRate() {
    assertEquals(0.1, this.testParams.getMinDeathRate(), TOLERANCE);
  }

  @Test(expected = ParameterOutOfBoundsException.class)
  public void testErrorInitBirthRateOutOfBounds() {
    PopulationUpdateParams.builder().birthRateParams(1.1, 0, 0, 0).deathRateParams(0, 0, 0, 0)
        .targetYieldBufferFactor(0).build();
  }

}
