package me.ajlane.geo.repast;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import me.ajlane.geo.CartesianGridDouble2D;
import me.ajlane.geo.GridDimensions;
import me.ajlane.geo.GridLoc;
import repast.simphony.valueLayer.ValueLayer;

public class ValueLayerAdapterTest {

  static double TOLERANCE = 0.00001;

  @Test
  public void testGridShapeCorrect() {
    double[][] testArray1 = {{1.0, 2.0, 3.0}, {4.0, 5.0, 6.0}};
    ValueLayer testLayer1 = RepastGridUtils.arrayToGridValueLayer("test layer", testArray1);
    CartesianGridDouble2D grid1 = new ValueLayerAdapter(testLayer1);

    double[][] testArray2 = {{1.0, 2.0}, {3.0, 4.0}, {5.0, 6.0}, {7.0, 8.0}};
    ValueLayer testLayer2 = RepastGridUtils.arrayToGridValueLayer("test layer", testArray2);
    CartesianGridDouble2D grid2 = new ValueLayerAdapter(testLayer2);

    assertEquals(new GridDimensions(3, 2), grid1.getDimensions());
    assertEquals(6, grid1.getSize());

    assertEquals(new GridDimensions(2, 4), grid2.getDimensions());
    assertEquals(8, grid2.getSize());
  }

  @Test
  public void testGridValuesCorrect() {
    double[][] testArray = {{1.0, 2.0, 3.0}, {4.0, 5.0, 6.0}};
    ValueLayer testLayer = RepastGridUtils.arrayToGridValueLayer("test layer", testArray);
    CartesianGridDouble2D grid = new ValueLayerAdapter(testLayer);

    System.out.println(testLayer);

    assertEquals(4.0, grid.getValue(new GridLoc(0, 0)), TOLERANCE);
    assertEquals(1.0, grid.getValue(new GridLoc(0, 1)), TOLERANCE);
    assertEquals(5.0, grid.getValue(new GridLoc(1, 0)), TOLERANCE);
    assertEquals(2.0, grid.getValue(new GridLoc(1, 1)), TOLERANCE);
    assertEquals(6.0, grid.getValue(new GridLoc(2, 0)), TOLERANCE);
    assertEquals(3.0, grid.getValue(new GridLoc(2, 1)), TOLERANCE);
  }

}
