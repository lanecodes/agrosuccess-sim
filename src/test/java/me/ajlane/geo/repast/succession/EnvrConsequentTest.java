package me.ajlane.geo.repast.succession;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class EnvrConsequentTest {

  @Test
  public void similarEnvironmentalConsequentShouldBeEqual() {
    EnvrConsequent<String, Integer> aliasedEnvironmentalConsequent =
        new EnvrConsequent<>("Pine", 10);

    EnvrConsequent<String, Integer> otherEnvironmentalConsequent = new EnvrConsequent<>("Pine", 10);


    assertTrue(aliasedEnvironmentalConsequent.equals(otherEnvironmentalConsequent));
  }

  @Test
  public void disimilarEnvironmentalAntecedentsShouldNotBeEqual() {
    EnvrConsequent<String, Integer> aliasedEnvironmentalConsequent = new EnvrConsequent<>("Pine", 10);

    EnvrConsequent<String, Integer> otherEnvironmentalConsequent1 = new EnvrConsequent<>("Oak", 10);

    EnvrConsequent<String, Integer> otherEnvironmentalConsequent2 = new EnvrConsequent<>("Pine", 15);


    assertFalse(aliasedEnvironmentalConsequent.equals(otherEnvironmentalConsequent1));
    assertFalse(aliasedEnvironmentalConsequent.equals(otherEnvironmentalConsequent2));
  }

}
