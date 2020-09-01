package me.ajlane.geo.repast.succession.pathway.coded;

import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import me.ajlane.geo.repast.succession.pathway.EnvrAntecedent;
import me.ajlane.geo.repast.succession.pathway.EnvrConsequent;
import me.ajlane.geo.repast.succession.pathway.coded.CodedLcsTransitionMapOld;

public class CodedLcsTransitionMapOldTest {

  @Before
  public void setup() {

  }

  @Test
  public void testCodedLandCoverStateTransitionMap() {
    CodedLcsTransitionMapOld transMap = new CodedLcsTransitionMapOld();

    EnvrAntecedent<Integer, Integer, Integer, Integer, Integer, Integer, Integer> a1 =
        new EnvrAntecedent<>(1, 0, 1, 1, 0, 0, 2);

    EnvrConsequent<Integer, Integer> c1 = new EnvrConsequent<>(2, 10);

    transMap.put(a1, c1);

    assertTrue(transMap.get(a1).equals(c1));

  }
}
