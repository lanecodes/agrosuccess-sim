package me.ajlane.geo.repast;

import me.ajlane.geo.GridUtils;
import repast.simphony.valueLayer.GridValueLayer;

public class RepastGridUtils extends GridUtils {

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
   * @param intArray a 2D array in which all rows are assumed to have the same number of elements.
   * @return Equivalent repast GridValueLayer
   */
  public static GridValueLayer arrayToGridValueLayer(String valueLayerName, int[][] intArray) {
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

  public static String gridValueLayerToString(GridValueLayer gvl) {
    int nRows = Math.round((float) gvl.getDimensions().getHeight());
    int nCols = Math.round((float) gvl.getDimensions().getWidth());
    String string = "GridValueLayer " + gvl.getName() + ":\n";

    for (int y = nRows - 1; y >= 0; y--) {
      for (int x = 0; x < nCols; x++) {
        string = string + gvl.get(x, y) + getDelimeter(nCols, x);
      }
    }

    return string;
  }

  /**
   * @param gvl1
   * @param gvl2
   * @return
   */
  public static boolean gridValueLayersAreEqual(GridValueLayer gvl1, GridValueLayer gvl2) {

    if (!gvl1.getName().equals(gvl2.getName()))
      return false;

    int nCols1 = (int) gvl1.getDimensions().getWidth();
    int nRows1 = (int) gvl1.getDimensions().getHeight();

    int nCols2 = (int) gvl2.getDimensions().getWidth();
    int nRows2 = (int) gvl2.getDimensions().getHeight();

    if (nCols1 != nCols2 || nRows1 != nRows2)
      return false;

    for (int i = 0; i < nRows1; i++) {
      for (int j = 0; j < nCols1; j++) {
        if (gvl1.get(i, j) - gvl2.get(i, j) > 0.0001)
          return false;
      }
    }
    return true;
  }



}
