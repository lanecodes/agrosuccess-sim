package me.ajlane.geo.repast;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import me.ajlane.geo.CartesianGridDouble2D;
import me.ajlane.geo.GridDimensions;
import me.ajlane.geo.GridLoc;
import me.ajlane.geo.WriteableCartesianGridDouble2D;
import repast.simphony.valueLayer.GridValueLayer;
import repast.simphony.valueLayer.IGridValueLayer;
import repast.simphony.valueLayer.ValueLayer;

/**
 * <h1>Note</h1>
 * <p>
 * With the exception of {@link #testAssignment} all tests here correspond exactly to
 * {@link ValueLayerAdapterTest}. This is due to the need to duplicate code from that
 * {@link ValueLayerAdapter} in {@link GridValueLayerAdapter}. See javadoc in the latter for details
 * of why this is necessary.
 * </p>
 *
 * @author Andrew Lane
 */
public class GridValueLayerAdapterTest {

  static double TOLERANCE = 0.00001;

  @Test
  public void testGridShapeCorrect() {
    double[][] testArray1 = {{1.0, 2.0, 3.0}, {4.0, 5.0, 6.0}};
    IGridValueLayer testLayer1 = RepastGridUtils.arrayToGridValueLayer("test layer", testArray1);
    CartesianGridDouble2D grid1 = new GridValueLayerAdapter(testLayer1);

    double[][] testArray2 = {{1.0, 2.0}, {3.0, 4.0}, {5.0, 6.0}, {7.0, 8.0}};
    IGridValueLayer testLayer2 = RepastGridUtils.arrayToGridValueLayer("test layer", testArray2);
    CartesianGridDouble2D grid2 = new GridValueLayerAdapter(testLayer2);

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

  @Test
  public void testAssignment() {
    IGridValueLayer gvl = new GridValueLayer("test layer", true, 2, 2);
    WriteableCartesianGridDouble2D grid = new GridValueLayerAdapter(gvl);

    assertEquals(0.0, grid.getValue(new GridLoc(0, 0)), TOLERANCE);
    assertEquals(0.0, grid.getValue(new GridLoc(1, 0)), TOLERANCE);
    assertEquals(0.0, grid.getValue(new GridLoc(0, 1)), TOLERANCE);
    assertEquals(0.0, grid.getValue(new GridLoc(1, 1)), TOLERANCE);

    grid.setValue(1.0, new GridLoc(0, 0));
    grid.setValue(2.0, new GridLoc(1, 0));
    grid.setValue(3.0, new GridLoc(0, 1));
    grid.setValue(4.0, new GridLoc(1, 1));

    assertEquals(1.0, grid.getValue(new GridLoc(0, 0)), TOLERANCE);
    assertEquals(2.0, grid.getValue(new GridLoc(1, 0)), TOLERANCE);
    assertEquals(3.0, grid.getValue(new GridLoc(0, 1)), TOLERANCE);
    assertEquals(4.0, grid.getValue(new GridLoc(1, 1)), TOLERANCE);
  }

}
