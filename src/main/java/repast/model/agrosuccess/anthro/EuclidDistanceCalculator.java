package repast.model.agrosuccess.anthro;

import me.ajlane.geo.repast.GridPointLocator;
import repast.simphony.space.grid.GridPoint;

/**
 *
 * @author Andrew Lane
 *
 */
public class EuclidDistanceCalculator implements DistanceCalculator {

  double gridCellEdgeLength;
  GridPointLocator gridPointLoc;

  /**
   * @param gridCellEdgeLength Edge length of the grid cells in metres
   * @param gridPointLoc Object used to calculate the location of the cells relative to the grid
   */
  public EuclidDistanceCalculator(double gridCellEdgeLength, GridPointLocator gridPointLoc) {
    this.gridCellEdgeLength = gridCellEdgeLength;
    this.gridPointLoc = gridPointLoc;
  }

  /**
   * Implements the function d(P_1, P_2; \alpha) = \alpha * [(x_1 - x_2)^2 + (y_1 - y_2)^2]^(1/2)
   * where \alpha is the grid cell edge length.
   *
   * {@inheritDoc}
   */
  @Override
  public double distInMetres(GridPoint p1, GridPoint p2) {
    if (!(this.gridPointLoc.isInGrid(p1) && this.gridPointLoc.isInGrid(p2))) {
      throw new IndexOutOfBoundsException(
          p1 + " or " + p2 + " is not inside the grid specified by " + this.gridPointLoc);
    }
    double unitCellDist =
        Math.sqrt(Math.pow(p1.getX() - p2.getX(), 2) + Math.pow(p1.getY() - p2.getY(), 2));
    return this.gridCellEdgeLength * unitCellDist;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public double propMaxDist(GridPoint refPoint, GridPoint tgtPoint) {
    GridPoint furthestPoint = this.gridPointLoc.furthestPoints(refPoint).iterator().next();
    double maxDist = distInMetres(refPoint, furthestPoint);
    double dist = distInMetres(refPoint, tgtPoint);
    return dist / maxDist;
  }

}
