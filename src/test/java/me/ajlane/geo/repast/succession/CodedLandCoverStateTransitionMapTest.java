package me.ajlane.geo.repast.succession;

import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

public class CodedLandCoverStateTransitionMapTest {
  
  @Before
  public void setup() {
     
  }

  @Test
  public void testCodedLandCoverStateTransitionMap() {
    CodedLcsTransitionMap transMap = new CodedLcsTransitionMap(); 
    
    EnvrAntecedent<Integer, Integer, Integer, Integer, Integer, Integer, Integer> a1 =
        new EnvrAntecedent<>(1, 0, 1, 1, 0, 0, 2);
    
    EnvrConsequent<Integer> c1 = new EnvrConsequent<>(2, 10);
    
    transMap.put(a1, c1);
    
    assertTrue(transMap.get(a1).equals(c1));

  }
}
