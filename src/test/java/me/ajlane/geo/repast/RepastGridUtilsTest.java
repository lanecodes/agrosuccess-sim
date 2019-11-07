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
    int[][] array = {{1, 2}, {3, 4}};

    GridValueLayer gvl = RepastGridUtils.arrayToGridValueLayer("test layer", array);
    assertEquals(3, (int) gvl.get(0, 0));
    assertEquals(4, (int) gvl.get(1, 0));
    assertEquals(1, (int) gvl.get(0, 1));
    assertEquals(2, (int) gvl.get(1, 1));
  }

  @Test
  public void testGridValueLayerToString() {
    int[][] array = {{1, 2}, {3, 4}};
    GridValueLayer gvl = RepastGridUtils.arrayToGridValueLayer("test layer", array);
    assertEquals("GridValueLayer test layer:\n1.0  2.0\n3.0  4.0\n",
        RepastGridUtils.gridValueLayerToString(gvl));
  }

  @Test
  public void equalityTestShouldDetectChangeInGridValues() {

    GridValueLayer g1 = new GridValueLayer("something", 5, true, 3, 3);

    GridValueLayer g2 = new GridValueLayer("something", 5, true, 3, 3);
    g1.set(6, 0, 2);

    GridValueLayer g3 = new GridValueLayer("something", 5, true, 3, 3);
    g3.set(6, 0, 2);

    assertFalse(RepastGridUtils.gridValueLayersAreEqual(g1, g2));
    assertTrue(RepastGridUtils.gridValueLayersAreEqual(g2, g3));

  }

  @Test
  public void equalityTestShouldDetectChangeInName() {
    GridValueLayer g1 = new GridValueLayer("something", 5, true, 3, 3);
    GridValueLayer g2 = new GridValueLayer("something else", 5, true, 3, 3);

    assertFalse(RepastGridUtils.gridValueLayersAreEqual(g1, g2));
  }
  
  @Test
  public void arrayShouldCorrespondCorrectlyToGridValueLayer() {
    GridValueLayer g = new GridValueLayer("test layer", 0, true, 2, 2);
    g.set(3, 0, 0); // bottom left
    g.set(4, 1, 0); // bottom right
    g.set(1, 0, 1); // top left
    g.set(2, 1, 1); // top right
    
    int[][] expectedArray = new int[][]{
      { 1, 2 },
      { 3, 4 }
    }; 
    
    assertArrayEquals(expectedArray, RepastGridUtils.gridValueLayerToArray(g));
  }

}
