package me.ajlane.geo;

public class AsciiGridParams {

  private double llCornerX;
  private double llCornerY;
  private double cellSize;
  private int noDataValue;



  public AsciiGridParams(double llCornerX, double llCornerY, double cellSize, int noDataValue) {
    this.llCornerX = llCornerX;
    this.llCornerY = llCornerY;
    this.cellSize = cellSize;
    this.noDataValue = noDataValue;
  }

  public double getLlCornerX() {
    return this.llCornerX;
  }

  public double getLlCornerY() {
    return this.llCornerY;
  }

  public double getCellSize() {
    return this.cellSize;
  }

  public int getNoDataValue() {
    return this.noDataValue;
  }

}
