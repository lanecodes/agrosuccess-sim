package me.ajlane.geo.repast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import me.ajlane.geo.Direction;
import me.ajlane.geo.GridUtils;
import repast.simphony.space.Dimensions;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.valueLayer.GridCell;
import repast.simphony.valueLayer.GridValueLayer;
import repast.simphony.valueLayer.IGridValueLayer;
import repast.simphony.valueLayer.ValueLayer;

/**
 * Utility methods and classes for working with Repast grids
 *
 * @author Andrew Lane
 */
public class RepastGridUtils extends GridUtils {

  /**
   * @param startPoint Current grid location.
   * @param direction Cardinal direction from which we will leave the {@code startPoint}.
   * @return The grid point we would end up in after leaving {@code startPoint} in
   *         {@code direction}.
   */
  public static GridPoint adjacentPointInDirection(GridPoint startPoint, Direction direction) {
    int x = startPoint.getX();
    int y = startPoint.getY();
    GridPoint endPoint;

    switch (direction) {
      case N:
        endPoint = new GridPoint(x, y + 1);
        break;
      case NE:
        endPoint = new GridPoint(x + 1, y + 1);
        break;
      case E:
        endPoint = new GridPoint(x + 1, y);
        break;
      case SE:
        endPoint = new GridPoint(x + 1, y - 1);
        break;
      case S:
        endPoint = new GridPoint(x, y - 1);
        break;
      case SW:
        endPoint = new GridPoint(x - 1, y - 1);
        break;
      case W:
        endPoint = new GridPoint(x - 1, y);
        break;
      case NW:
        endPoint = new GridPoint(x - 1, y + 1);
        break;
      default:
        throw new RuntimeException("Direction unexpectedly not recognised.");
    }

    return endPoint;
  }

  private static boolean allColumnsHaveSameLength(double[][] doubleArray) {
    int nRows = doubleArray.length;
    boolean answer = true;
    for (int i = 1; i < nRows; i++) {
      if (doubleArray[i].length != doubleArray[0].length) {
        answer = false;
      }
    }
    return answer;
  }

  private static boolean allColumnsHaveSameLength(int[][] intArray) {
    int nRows = intArray.length;
    boolean answer = true;
    for (int i = 1; i < nRows; i++) {
      if (intArray[i].length != intArray[0].length) {
        answer = false;
      }
    }
    return answer;
  }

  /**
   * @param valueLayerName Name of the returned {@code GridValueLayer}
   * @param doubleArray A 2D array in which all rows are assumed to have the same number of
   *        elements.
   *
   * @return Equivalent repast GridValueLayer.
   */
  public static IGridValueLayer arrayToGridValueLayer(String valueLayerName,
      double[][] doubleArray) {
    if (!allColumnsHaveSameLength(doubleArray)) {
      throw new IllegalArgumentException("All rows of input array must have the same length.");
    }
    int nRows = doubleArray.length;
    int nCols = doubleArray[0].length;
    GridValueLayer gvl = new GridValueLayer(valueLayerName, true, nCols, nRows);
    for (int i = 0; i < nRows; i++) {
      for (int j = 0; j < nCols; j++) {
        // By convention, Java array indexed top to bottom, Repast GridValueLayer bottom to top
        gvl.set(doubleArray[i][j], j, (nRows - 1) - i);
      }
    }
    return gvl;
  }

  /**
   * @param intArray a 2D array in which all rows are assumed to have the same number of elements.
   * @return Equivalent repast GridValueLayer
   */
  public static IGridValueLayer arrayToGridValueLayer(String valueLayerName, int[][] intArray) {
    if (!allColumnsHaveSameLength(intArray)) {
      throw new IllegalArgumentException();
    }
    int nRows = intArray.length;
    int nCols = intArray[0].length;
    GridValueLayer gvl = new GridValueLayer(valueLayerName, true, nRows, nCols);
    for (int i = 0; i < nRows; i++) {
      for (int j = 0; j < nCols; j++) {
        // By convention, Java array indexed top to bottom, Repast GridValueLayer bottom to top
        gvl.set(intArray[i][j], j, (nRows - 1) - i);
      }
    }
    return gvl;
  }

  /**
   * Helper function which determines correct delimeter between numbers in printed representation of
   * the GridValueLayer
   */
  private static String getDelimeter(int nCols, int x) {
    if (x < nCols - 1) {
      return "  ";
    }
    return "\n";
  }

  /**
   * Compare two repast GridValueLayer-s, return a list of GridCell objects representing the cells
   * where the value layers differ.
   *
   * @param gvl1 first grid value layer
   * @param gvl2 second grid value layer. Must have the same dimensions as {@code gvl1}
   * @param delta maximum (absolute) value by which the value at a given coordinate can differ
   *        between the two value layers while still being considered equal.
   * @return list of grid cells in which the grid value layers differ.
   */
  public static List<GridCell> getDifferingCells(IGridValueLayer gvl1, IGridValueLayer gvl2,
      double delta) {
    Dimensions dims = gvl1.getDimensions();
    if (!dims.equals(gvl2.getDimensions())) {
      throw new IndexOutOfBoundsException(
          "Value layers have different dimensions, so values " + "cannot be directly compared.");
    }
    List<GridCell> diffCells = new ArrayList<>();

    int w = (int) dims.getWidth();
    int h = (int) dims.getHeight();
    for (int x = 0; x < w; x++) {
      for (int y = 0; y < h; y++) {
        double diff = gvl1.get(x, y) - gvl2.get(x, y);
        if (Math.abs(diff) > delta) {
          diffCells.add(new GridCell(diff, x, y));
        }
      }
    }

    if (diffCells.size() > 0) {
      return Collections.unmodifiableList(diffCells);
    } else {
      return Collections.emptyList();
    }
  }

  /**
   * @param gvl1
   * @param gvl2
   * @return
   */
  public static boolean gridValueLayersAreEqual(IGridValueLayer gvl1, IGridValueLayer gvl2) {

    if (!gvl1.getName().equals(gvl2.getName())) {
      return false;
    }

    int nCols1 = (int) gvl1.getDimensions().getWidth();
    int nRows1 = (int) gvl1.getDimensions().getHeight();

    int nCols2 = (int) gvl2.getDimensions().getWidth();
    int nRows2 = (int) gvl2.getDimensions().getHeight();

    if (nCols1 != nCols2 || nRows1 != nRows2) {
      return false;
    }

    for (int i = 0; i < nRows1; i++) {
      for (int j = 0; j < nCols1; j++) {
        if (gvl1.get(i, j) - gvl2.get(i, j) > 0.0001) {
          return false;
        }
      }
    }
    return true;
  }

  public static int[][] gridValueLayerToArray(IGridValueLayer gvl) {
    int nRows = (int) gvl.getDimensions().getHeight();
    int nCols = (int) gvl.getDimensions().getWidth();
    int[][] array = new int[nRows][nCols];
    for (int i = 0; i < nRows; i++) {
      for (int j = 0; j < nCols; j++) {
        // By convention, Java array indexed top to bottom, Repast GridValueLayer bottom to top
        array[i][j] = (int) gvl.get(j, (nRows - 1) - i);
      }
    }
    return array;
  }

  public static String valueLayerToString(ValueLayer layer) {
    int nRows = Math.round((float) layer.getDimensions().getHeight());
    int nCols = Math.round((float) layer.getDimensions().getWidth());
    String string = "ValueLayer " + layer.getName() + ":\n";

    for (int y = nRows - 1; y >= 0; y--) {
      for (int x = 0; x < nCols; x++) {
        string = string + layer.get(x, y) + getDelimeter(nCols, x);
      }
    }

    return string;
  }

  /**
   * Generates an integer hash value useful for determining whether two Repast GridValueLayer-s have
   * the same values.
   *
   * @param gvl
   * @return Hash value
   */
  public static int hashGridValueLayerValues(GridValueLayer gvl) {
    double runningTotal = 0;
    int w = (int) gvl.getDimensions().getWidth();
    int h = (int) gvl.getDimensions().getHeight();
    // prevent numbers from getting too large when working with large grids
    int constDivisor = w * h;
    for (int x = 0; x < w; x++) {
      for (int y = 0; y < h; y++) {
        runningTotal += gvl.get(x, y) / constDivisor;
      }
    }
    return (Double.valueOf(runningTotal)).hashCode();
  }

  /**
   * @param testPoint Point to test
   * @param layer {@code ValueLayer} in which to test {@code testPoint} against.
   * @return {@code True} if {@code testPoint} falls within {@code layer}, {@code False} otherwise.
   */
  public static boolean pointInValueLayer2D(GridPoint testPoint, ValueLayer layer) {
    Dimensions dims = layer.getDimensions();
    int xOrigin = (int) dims.getOrigin(0);
    int xExtent = (int) dims.getWidth();

    int yOrigin = (int) dims.getOrigin(1);
    int yExtent = (int) dims.getHeight();

    return pointInExtent1D(testPoint.getX(), xOrigin, xExtent)
        & pointInExtent1D(testPoint.getY(), yOrigin, yExtent);
  }

  /**
   * @param testPoint Point to test
   * @param extent The [x, y] extent of the value layer
   * @param origin The [x, y] origin of the value layer
   * @return {@code True} if {@code testPoint} falls within {@code layer}, {@code False} otherwise.
   */
  public static boolean pointInValueLayer2D(GridPoint testPoint, int[] extent, int[] origin) {
    int xOrigin = origin[0];
    int xExtent = extent[0];

    int yOrigin = origin[1];
    int yExtent = extent[1];

    return pointInExtent1D(testPoint.getX(), xOrigin, xExtent)
        & pointInExtent1D(testPoint.getY(), yOrigin, yExtent);
  }

  /**
   * @param point Point along a number line to test for inclusion within {@code extent}.
   * @param origin Origin of the number line.
   * @param extent Number of integer stops on the number line.
   * @return {@code True} if {@code point} falls along the number line running from the
   *         {@code origin} up to {@code origin + extent}. Note that because the extent (number of
   *         integers on the number line) includes the origin, if {point = extent} then it does not
   *         fall <emph>within</emph> the extent.
   */
  private static boolean pointInExtent1D(int point, int origin, int extent) {
    boolean insideLower = point >= origin;
    boolean insideUpper = point < origin + extent;
    return insideUpper & insideLower;
  }

  /**
   * Get a simple total of all values in a GridValueLayer.
   *
   * @param gvl
   * @return
   */
  public static double totalGridValueLayerValues(GridValueLayer gvl) {
    double runningTotal = 0;
    int w = (int) gvl.getDimensions().getWidth();
    int h = (int) gvl.getDimensions().getHeight();
    for (int x = 0; x < w; x++) {
      for (int y = 0; y < h; y++) {
        runningTotal += gvl.get(x, y);
      }
    }
    return runningTotal;
  }

  /**
   * Iterable over the {@code GridPoint}s contained in a 2-dimensional {@link ValueLayer}
   */
  public static class GridPointIterable implements Iterable<GridPoint> {
    final Dimensions gridDims;

    /**
     * @param valueLayer Value layer whose {@code GridPoint}s will be iterated over
     */
    public GridPointIterable(ValueLayer valueLayer) {
      this.gridDims = valueLayer.getDimensions();
    }

    @Override
    public Iterator<GridPoint> iterator() {
      return new GridPointIterator();
    }

    private class GridPointIterator implements Iterator<GridPoint> {

      private int x, y;

      private GridPointIterator() {
        this.x = 0;
        this.y = 0;
      }

      @Override
      public boolean hasNext() {
        if (this.x < gridDims.getWidth() && this.y < gridDims.getHeight()) {
          return true;
        }
        return false;
      }

      @Override
      public GridPoint next() {
        GridPoint nextPoint = new GridPoint(this.x, this.y);
        if (this.x < gridDims.getWidth() - 1) {
          this.x += 1;
        } else {
          this.x = 0;
          this.y += 1;
        }
        return nextPoint;
      }

    }
  }

}
