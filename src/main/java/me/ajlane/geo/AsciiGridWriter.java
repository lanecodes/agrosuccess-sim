package me.ajlane.geo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class AsciiGridWriter {

  private AsciiGridParams params;
  CartesianGridDouble2D grid;

  public AsciiGridWriter(CartesianGridDouble2D grid, AsciiGridParams params) {
    this.params = params;
    this.grid = grid;
  }

  public void write(File fileName) throws IOException {
    BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
    writer.write(header(this.grid.getDimensions(), this.params));
    writer.write(getGridString(this.grid));
    writer.close();
  }

  private String header(GridDimensions dims, AsciiGridParams params) {
    StringBuilder sb = new StringBuilder();
    sb.append("ncols         " + dims.getWidth());
    sb.append(System.lineSeparator());
    sb.append("nrows         " + dims.getHeight());
    sb.append(System.lineSeparator());
    sb.append("xllcorner     " + params.getLlCornerX());
    sb.append(System.lineSeparator());
    sb.append("yllcorner     " + params.getLlCornerY());
    sb.append(System.lineSeparator());
    sb.append("cellsize      " + params.getCellSize());
    sb.append(System.lineSeparator());
    sb.append("NODATA_value  " + params.getNoDataValue());
    sb.append(System.lineSeparator());
    return sb.toString();
  }

  private String getGridString(CartesianGridDouble2D grid) {
    GridDimensions dims = grid.getDimensions();
    StringBuilder sb1 = new StringBuilder();
    for(int y = dims.getHeight() - 1; y>= 0; y--) {
      StringBuilder sb2 = new StringBuilder();
      for(int x = 0; x < dims.getWidth(); x++) {
        sb2.append(grid.getValue(new GridLoc(x, y)));
        sb2.append(" ");
      }
      sb2.append(System.lineSeparator());
      sb1.append(sb2.toString());
    }
    sb1.append(System.lineSeparator());
    return sb1.toString();
  }

}
