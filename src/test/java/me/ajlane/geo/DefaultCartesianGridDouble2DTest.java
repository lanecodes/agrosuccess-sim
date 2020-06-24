package me.ajlane.geo;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class DefaultCartesianGridDouble2DTest {

  static double TOLERANCE = 0.00001;

  @Test
  public void testArrayConstructor() {
    double[][] array = {{1.0, 2.0, 3.0}, {4.0, 5.0, 6.0},};
    CartesianGridDouble2D grid = new DefaultCartesianGridDouble2D(array);

    assertEquals(6, grid.getSize());
    assertEquals(2, grid.getDimensions().getHeight());
    assertEquals(3, grid.getDimensions().getWidth());
    assertEquals(4.0, grid.getValue(new GridLoc(0, 0)), TOLERANCE);
    assertEquals(5.0, grid.getValue(new GridLoc(1, 0)), TOLERANCE);
    assertEquals(6.0, grid.getValue(new GridLoc(2, 0)), TOLERANCE);
    assertEquals(1.0, grid.getValue(new GridLoc(0, 1)), TOLERANCE);
    assertEquals(2.0, grid.getValue(new GridLoc(1, 1)), TOLERANCE);
    assertEquals(3.0, grid.getValue(new GridLoc(2, 1)), TOLERANCE);
  }

  @Test
  public void testDefaultValueConstructor() {
    CartesianGridDouble2D grid = new DefaultCartesianGridDouble2D(3.0, new GridDimensions(2, 3));

    assertEquals(6, grid.getSize());
    assertEquals(3, grid.getDimensions().getHeight());
    assertEquals(2, grid.getDimensions().getWidth());
    assertEquals(3.0, grid.getValue(new GridLoc(0, 0)), TOLERANCE);
    assertEquals(3.0, grid.getValue(new GridLoc(1, 2)), TOLERANCE);
  }

  @Test
  public void testAssignment() {
    GridLoc tgtCell = new GridLoc(1, 1);
    WriteableCartesianGridDouble2D grid =
        new DefaultCartesianGridDouble2D(3.0, new GridDimensions(2, 2));
    assertEquals(3.0, grid.getValue(tgtCell), TOLERANCE);
    grid.setValue(2.0, tgtCell);
    assertEquals(2.0, grid.getValue(tgtCell), TOLERANCE);
  }

  @Test
  public void testToString() {
    CartesianGridDouble2D grid = new DefaultCartesianGridDouble2D(new double[][] {{1.0, 2.0}, {3.0, 4.0}});
    String s = "[1.0, 2.0]" + System.lineSeparator() + "[3.0, 4.0]";
    assertEquals(s, grid.toString());
  }

}
