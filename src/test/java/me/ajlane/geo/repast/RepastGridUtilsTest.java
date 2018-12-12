package me.ajlane.geo.repast;

import static org.junit.Assert.*;
import org.junit.Test;
import repast.simphony.valueLayer.GridValueLayer;

public class RepastGridUtilsTest {

  @Test
  public void arrayToGridValueLayerShouldThrowExceptionIfArrayIsRagged() {
    int[][] array = {{1, 2, 3}, {4, 5}, {6, 7, 8}}; 
    try {
      RepastGridUtils.arrayToGridValueLayer("test layer", array);
      fail("IllegalArgumentException should have been thrown");
    } catch (Exception e) {
      assertEquals(e.getClass(), IllegalArgumentException.class);
    }
  }
  
  @Test
  public void arrayToGridValueLayerShouldMakeCorrectGridValueLayer() {
    int[][] array = {
        {1, 2}, 
        {3, 4}
        };
    
    GridValueLayer gvl = RepastGridUtils.arrayToGridValueLayer("test layer", array);
    assertEquals(3, (int)gvl.get(0, 0));
    assertEquals(4, (int)gvl.get(1, 0));
    assertEquals(1, (int)gvl.get(0, 1));
    assertEquals(2, (int)gvl.get(1, 1));
  }
  
  @Test
  public void testGridValueLayerToString() {
    int[][] array = {
        {1, 2}, 
        {3, 4}
        };
    GridValueLayer gvl = RepastGridUtils.arrayToGridValueLayer("test layer", array);
    assertEquals("1.0  2.0\n3.0  4.0\n", RepastGridUtils.gridValueLayerToString(gvl));
  }

}
