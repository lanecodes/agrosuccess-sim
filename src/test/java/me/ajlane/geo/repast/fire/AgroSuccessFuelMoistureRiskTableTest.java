package me.ajlane.geo.repast.fire;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class AgroSuccessFuelMoistureRiskTableTest {

  private static double TOLERANCE = 0.0000001;

  @Test
  public void testFuelMoistureRiskValuesAreAsExpected() {
    assertEquals(AgroSuccessFuelMoistureRiskTable.fromVegetationMoistureParam(0.1),
        0.8, TOLERANCE);
    assertEquals(AgroSuccessFuelMoistureRiskTable.fromVegetationMoistureParam(0.25),
        0.9, TOLERANCE);
    assertEquals(AgroSuccessFuelMoistureRiskTable.fromVegetationMoistureParam(0.4),
        1.0, TOLERANCE);
    assertEquals(AgroSuccessFuelMoistureRiskTable.fromVegetationMoistureParam(0.55),
        1.1, TOLERANCE);
    assertEquals(AgroSuccessFuelMoistureRiskTable.fromVegetationMoistureParam(0.7),
        1.2, TOLERANCE);
  }

}
