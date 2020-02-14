package me.ajlane.geo.repast;

import static org.junit.Assert.*;
import org.junit.Test;
import me.ajlane.geo.Direction;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.space.grid.StrictBorders;
import repast.simphony.valueLayer.GridValueLayer;
import repast.simphony.valueLayer.IGridValueLayer;
import repast.simphony.valueLayer.ValueLayer;

public class RepastGridUtilsTest {

  private static final double TOLERANCE = 0.00001;

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
  public void intArrayToGridValueLayerShouldMakeCorrectGridValueLayer() {
    int[][] array = {{1, 2}, {3, 4}};

    IGridValueLayer gvl = RepastGridUtils.arrayToGridValueLayer("test layer", array);
    assertEquals(3, (int) gvl.get(0, 0));
    assertEquals(4, (int) gvl.get(1, 0));
    assertEquals(1, (int) gvl.get(0, 1));
    assertEquals(2, (int) gvl.get(1, 1));
  }

  /**
   * Check that {@code RepastGridUtils.arrayToGridValueLayer} correctly dispatches on the type of
   * the supplied double array.
   */
  @Test
  public void doubleArrayToGridValueLayerShouldMakeCorrectGridValueLayer() {
    double[][] array = {{1.0, 2.0}, {3.0, 4.0}};

    IGridValueLayer gvl = RepastGridUtils.arrayToGridValueLayer("test layer", array);
    assertEquals(3, gvl.get(0, 0), TOLERANCE);
    assertEquals(4, gvl.get(1, 0), TOLERANCE);
    assertEquals(1, gvl.get(0, 1), TOLERANCE);
    assertEquals(2, gvl.get(1, 1), TOLERANCE);
  }

  @Test
  public void testValueLayerToString() {
    int[][] array = {{1, 2}, {3, 4}};
    IGridValueLayer gvl = RepastGridUtils.arrayToGridValueLayer("test layer", array);
    assertEquals("ValueLayer test layer:\n1.0  2.0\n3.0  4.0\n",
        RepastGridUtils.valueLayerToString(gvl));
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

    int[][] expectedArray = new int[][] {{1, 2}, {3, 4}};

    assertArrayEquals(expectedArray, RepastGridUtils.gridValueLayerToArray(g));
  }

  @Test
  public void testAdjacentPointInDirection() {
    assertEquals(new GridPoint(0, 1),
        RepastGridUtils.adjacentPointInDirection(new GridPoint(0, 0), Direction.N));
    assertEquals(new GridPoint(-2, 1),
        RepastGridUtils.adjacentPointInDirection(new GridPoint(-3, 0), Direction.NE));
    assertEquals(new GridPoint(2, -4),
        RepastGridUtils.adjacentPointInDirection(new GridPoint(2, -3), Direction.S));
    assertEquals(new GridPoint(3, 4),
        RepastGridUtils.adjacentPointInDirection(new GridPoint(4, 4), Direction.W));
    assertEquals(new GridPoint(-1, 1),
        RepastGridUtils.adjacentPointInDirection(new GridPoint(0, 0), Direction.NW));
  }

  @Test
  public void testPointInValueLayer2D() {
    ValueLayer testLayerDefaultOrigin =
        new GridValueLayer("Test default", 0, true, new int[] {10, 10});
    ValueLayer testLayerCustomOrigin = new GridValueLayer("Test custom", 0, true,
        new StrictBorders(), new int[] {10, 10}, new int[] {-2, 2});

    GridPoint testPoint;

    testPoint = new GridPoint(9, 9);
    assertTrue(RepastGridUtils.pointInValueLayer2D(testPoint, testLayerDefaultOrigin));
    assertFalse(RepastGridUtils.pointInValueLayer2D(testPoint, testLayerCustomOrigin));

    testPoint = new GridPoint(0, 2);
    assertTrue(RepastGridUtils.pointInValueLayer2D(testPoint, testLayerDefaultOrigin));
    assertTrue(RepastGridUtils.pointInValueLayer2D(testPoint, testLayerCustomOrigin));

    testPoint = new GridPoint(-1, 4);
    assertFalse(RepastGridUtils.pointInValueLayer2D(testPoint, testLayerDefaultOrigin));
    assertTrue(RepastGridUtils.pointInValueLayer2D(testPoint, testLayerCustomOrigin));

    testPoint = new GridPoint(10, 10);
    assertFalse(RepastGridUtils.pointInValueLayer2D(testPoint, testLayerDefaultOrigin));
    assertFalse(RepastGridUtils.pointInValueLayer2D(testPoint, testLayerCustomOrigin));

  }

}
