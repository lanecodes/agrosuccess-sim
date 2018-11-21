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
    CodedLandCoverStateTransitionMap transMap = new CodedLandCoverStateTransitionMap(); 
    
    EnvironmentalAntecedent<Integer, Integer, Integer, Integer, Integer, Integer, Integer> a1 =
        new EnvironmentalAntecedent<>(1, 0, 1, 1, 0, 0, 2);
    
    EnvironmentalConsequent<Integer> c1 = new EnvironmentalConsequent<>(2, 10);
    
    transMap.put(a1, c1);
    
    assertTrue(transMap.get(a1).equals(c1));

  }
}
