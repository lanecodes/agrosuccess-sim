package me.ajlane.geo;

public class GridDimensions {

  private int nCol;
  private int nRow;

  /**
   * @param nCol Width of grid
   * @param nRow Height of grid
   */
  public GridDimensions(int nCol, int nRow) {
    this.nCol = nCol;
    this.nRow = nRow;
  }

  /**
   * @return Width of grid, i.e. number of columns
   */
  public int getWidth() {
    return this.nCol;
  }

  /**
   * @return Height of grid, i.e. number of rows
   */
  public int getHeight() {
    return this.nRow;
  }

  @Override
  public String toString() {
    return "GridDimensions [nCol=" + nCol + ", nRow=" + nRow + "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + nCol;
    result = prime * result + nRow;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    GridDimensions other = (GridDimensions) obj;
    if (nCol != other.nCol)
      return false;
    if (nRow != other.nRow)
      return false;
    return true;
  }

}
