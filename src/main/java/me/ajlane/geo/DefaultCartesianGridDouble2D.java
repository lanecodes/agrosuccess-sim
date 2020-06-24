package me.ajlane.geo;

import java.util.Arrays;
import java.util.StringJoiner;

/**
 * A 2-dimensional grid of {@code double} values.
 *
 * @see WriteableCartesianGridDouble2D
 * @author Andrew Lane
 */
public class DefaultCartesianGridDouble2D implements WriteableCartesianGridDouble2D {

  private double[][] array;
  private GridDimensions dimensions;

  /**
   * @param defaultValue Initial value used to fill every cell in the grid
   * @param dimensions Shape of the grid to generate
   */
  DefaultCartesianGridDouble2D(double defaultValue, GridDimensions dimensions) {
    this.array = new double[dimensions.getHeight()][dimensions.getWidth()];
    for (int i = 0; i < dimensions.getHeight(); i++) {
      for (int j = 0; j < dimensions.getWidth(); j++) {
        this.array[i][j] = defaultValue;
      }
    }
    this.dimensions = dimensions;
  }

  /**
   * @param array Array to convert to a grid. All rows must be the same length.
   */
  DefaultCartesianGridDouble2D(double[][] array) {
    this.array = array;
    this.dimensions = getDimensionsFromArray(array);
  }

  /**
   * Calculates grid dimensions from array.
   *
   * @exception IndexOutOfBoundsException Raised when rows have unequal lengths
   * @return Grid dimensions corresponding to passed array
   */
  private GridDimensions getDimensionsFromArray(double[][] array) {
    int nRow = array.length;
    int nCol = array[0].length;
    for (int j = 1; j < nRow; j++) {
      if (array[j].length != nCol) {
        throw new IndexOutOfBoundsException("Rows have unequal lengths");
      }
    }
    return new GridDimensions(nCol, nRow);
  }

  @Override
  public GridDimensions getDimensions() {
    return this.dimensions;
  }

  @Override
  public int getSize() {
    return this.dimensions.getWidth() * this.dimensions.getHeight();
  }

  @Override
  public double getValue(GridLoc loc) {
    return this.array[transformRow(loc.getRow())][loc.getCol()];
  }

  @Override
  public void setValue(double value, GridLoc loc) {
    this.array[transformRow(loc.getRow())][loc.getCol()] = value;
  }

  /**
   * @param requestedRow
   * @return Row number transformed to account for the fact that the 0th row in a {@code double[][]}
   *         array is the top row, whereas the 0th row in a Cartesian grid is the bottom row.
   */
  private int transformRow(int requestedRow) {
    return this.dimensions.getHeight() - 1 - requestedRow;
  }

  @Override
  public String toString() {
    StringJoiner sj = new StringJoiner(System.lineSeparator());
    for (double[] row : array) {
        sj.add(Arrays.toString(row));
    }
    return sj.toString();
  }

}
