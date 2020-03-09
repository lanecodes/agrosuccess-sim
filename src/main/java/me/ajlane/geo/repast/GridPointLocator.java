package me.ajlane.geo.repast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import repast.simphony.space.grid.GridPoint;

/**
 * <p>
 * Finds points in a grid relative to a reference point.
 * </p>
 *
 * <p>
 * A grid is specified by an origin and a set of dimensions.
 * </p>
 *
 * @author Andrew Lane
 *
 */
public class GridPointLocator {

  int[] gridDimension, gridOrigin;

  /**
   * @param gridDimension Array with 2 elements specifying the [x, y] dimensions of the grid
   * @param gridOrigin Array with 2 elements specifying the [x, y] origin of the grid
   */
  public GridPointLocator(int[] gridDimension, int[] gridOrigin) {
    this.gridDimension = gridDimension;
    this.gridOrigin = gridOrigin;
  }

  /**
   * @param gridPoint Point for which to check whether or not it is in the grid
   * @return {@code true} if the point is in the grid specified in this class's constructor
   */
  public boolean isInGrid(GridPoint gridPoint) {
    return RepastGridUtils.pointInValueLayer2D(gridPoint, this.gridDimension, this.gridOrigin);
  }

  /**
   * @param gridPoint A point in the grid specified in this class's constructor
   * @return Set of points which are as far away from {@code gridPoint} as anywhere else in the grid
   */
  public Set<GridPoint> furthestPoints(GridPoint gridPoint) {
    Set<GridPoint> fp = new HashSet<>();
    List<GridPoint> perimeter = perimPoints(this.gridDimension, this.gridOrigin);

    double eqThresh = 0.0001;
    double topDist = 0;
    double trialDist = 0;
    double improvement = 0;

    // Initialise with first point in perimeter cell list
    topDist = distance(gridPoint, perimeter.get(0));
    fp.add(perimeter.get(0));

    for (int i = 1; i < perimeter.size(); i++) {
      trialDist = distance(gridPoint, perimeter.get(i));
      improvement = trialDist - topDist;
      if (Math.abs(improvement) < eqThresh) {
        // GridPoint is the same distance from gridPoint as the current furthest point
        fp.add(perimeter.get(i));
      } else {
        if (improvement > eqThresh) {
          // Grid point is further away from from gridPoint than the current furthest point
          fp.removeAll(fp);
          topDist = trialDist;
          fp.add(perimeter.get(i));
        }
      }
    }
    return fp;
  }

  /**
   * @param gridDimension
   * @param gridOrigin
   * @return List of grid points comprising the perimeter of the grid specified by
   *         {@code gridDimension} and {@code gridOrigin}
   */
  private static List<GridPoint> perimPoints(int[] gridDimension, int[] gridOrigin) {
    List<GridPoint> points = new ArrayList<>();
    int minX = gridOrigin[0];
    int maxX = minX + gridDimension[0] - 1;
    int minY = gridOrigin[1];
    int maxY = minY + gridDimension[1] - 1;

    for (int i = 0; i <= maxX; i++) {
      points.add(new GridPoint(i, maxY)); // top edge
      points.add(new GridPoint(i, 0)); // bottom edge
    }

    for (int j = 0; j <= maxX; j++) {
      points.add(new GridPoint(0, j)); // left edge
      points.add(new GridPoint(maxX, j)); // right edge
    }

    return points;
  }

  private double distance(GridPoint p1, GridPoint p2) {
    return Math.sqrt(Math.pow(p1.getX() - p2.getX(), 2) + Math.pow(p1.getY() - p2.getY(), 2));
  }

}
