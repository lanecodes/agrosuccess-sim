/**
 *
 */
package me.ajlane.geo;

/**
 * Address of a grid cell in a 2D grid.
 *
 * @author Andrew Lane
 *
 */
public class GridLoc {

  private int row;
  private int col;

  public GridLoc(int col, int row) {
    this.col = col;
    this.row = row;
  }

  public int getRow() {
    return row;
  }

  public int getCol() {
    return col;
  }

  @Override
  public String toString() {
    return String.format("(%d, %d)", col, row);
  }

  @Override
  public int hashCode() {
    int hashCode = 17;
    hashCode = 37 * hashCode + row;
    hashCode = 37 * hashCode + col;
    return hashCode;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this)
      return true;
    if (!(obj instanceof GridLoc)) {
      return false;
    }
    GridLoc other = (GridLoc) obj;
    if (other.getRow() != this.row || other.getCol() != col) {
      return false;
    }
    return true;
    }

}
