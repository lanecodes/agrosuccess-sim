package me.ajlane.geo.repast.soilmoisture;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import me.ajlane.geo.CartesianGridDouble2D;
import me.ajlane.geo.Direction;
import me.ajlane.geo.GridDimensions;
import me.ajlane.geo.GridLoc;

/**
 * <p>
 * Representation of the way water flows over a landscape represented by a raster grid.
 * </p>
 * <p>
 * A landscape is composed of a collection of drainage basins or catchments. These are areas in
 * which all water drains to the same point. Each {@link LandscapeFlow} object represents a collection of
 * {@link CatchmentFlow} objects which specify how water flows from grid cell to grid cell within each
 * catchment.
 * </p>
 *
 * @see CatchmentFlow
 * @author Andrew Lane
 */
public abstract class LandscapeFlow implements Iterable<CatchmentFlow> {

  protected CartesianGridDouble2D flowDirMap;
  protected Map<Integer, Direction> dirMap;

  protected Set<GridLoc> getOutlets() {
    GridDimensions dims = this.flowDirMap.getDimensions();
    Set<GridLoc> outlets = new HashSet<>();
    for (GridLoc cell : findEdgeCells(dims)) {
      Direction flowDir = this.dirMap.get((int) this.flowDirMap.getValue(cell));
      if (isOutlet(cell, flowDir, dims)) {
        outlets.add(cell);
      }
    }

    return outlets;
  }

  /**
   * @param cell Cell to test for whether it is an outlet for the grid
   * @param dir Direction of flow for {@code cell}
   * @param dims Grid dimensions
   * @return {@code true} if {@code cell} is a hydrological outlet for the grid
   */
  private boolean isOutlet(GridLoc cell, Direction dir, GridDimensions dims) {
    boolean isNortherly =
        dir.equals(Direction.NE) || dir.equals(Direction.N) || dir.equals(Direction.NW);
    boolean isEasterly =
        dir.equals(Direction.E) || dir.equals(Direction.NE) || dir.equals(Direction.SE);
    boolean isSoutherly =
        dir.equals(Direction.SW) || dir.equals(Direction.S) || dir.equals(Direction.SE);
    boolean isWesterly =
        dir.equals(Direction.NW) || dir.equals(Direction.W) || dir.equals(Direction.SW);

    int thisCol = cell.getCol();
    int thisRow = cell.getRow();

    if (thisRow == dims.getHeight() - 1 && isNortherly) {
      return true;
    } else if (thisCol == dims.getWidth() - 1 && isEasterly) {
      return true;
    } else if (thisRow == 0 && isSoutherly) {
      return true;
    } else if (thisCol == 0 && isWesterly) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * @param dims Grid dimensions
   * @return Set of cells constituting the edge of the grid
   */
  private Set<GridLoc> findEdgeCells(GridDimensions dims) {
    Set<GridLoc> edgeCells = new HashSet<>();
    for (int x = 0; x < dims.getWidth(); x++) {
      edgeCells.add(new GridLoc(x, 0));
      edgeCells.add(new GridLoc(x, dims.getHeight() - 1));
    }

    for (int y = 0; y < dims.getHeight(); y++) {
      edgeCells.add(new GridLoc(0, y));
      edgeCells.add(new GridLoc(dims.getWidth() - 1, y));
    }

    return edgeCells;
  }

  protected boolean pointInGrid(GridLoc loc, GridDimensions dims) {
    boolean outsideTop = loc.getRow() > (dims.getHeight() - 1);
    boolean outsideRight = loc.getCol() > (dims.getWidth() - 1);
    boolean outsideBottom = loc.getRow() < 0;
    boolean outsideLeft = loc.getCol() < 0;
    if (outsideTop || outsideRight || outsideBottom || outsideLeft) {
      return false;
    }
    return true;
  }

  @Override
  public abstract Iterator<CatchmentFlow> iterator();

}

