package me.ajlane.geo.repast.colonisation;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class SeedViabilityParamsTest {

  @Test
  public void constructorWithOneValueShouldWork() {
    SeedViabilityParams svp = new SeedViabilityParams(7);
    assertEquals(7, svp.getOakSeedLifetime());
    assertEquals(7, svp.getPineSeedLifetime());
    assertEquals(7, svp.getDeciduousSeedLifetime());
  }

  @Test
  public void stringMethodShouldReturnExpectedResult() {
    SeedViabilityParams svp = new SeedViabilityParams(3, 7, 10);
    assertEquals("SeedViabilityParams[oakSeedLifetime=3, "
        + "pineSeedLifetime=7, deciduousSeedLifetime=10]", svp.toString());
  }

}
