package repast.model.agrosuccess.anthro;

import repast.simphony.space.grid.GridPoint;

/**
 * Calculate the distance from the centre of one grid cell to the centre of another.
 *
 * @author Andrew Lane
 *
 */
public interface DistanceCalculator {

  /**
   * @param p1
   * @param p2
   * @return Distance between {@code p1} and {@code p2} measured in metres
   */
  double distInMetres(GridPoint p1, GridPoint p2);

  /**
   * <p>
   * Considering a journey from grid cell j to grid cell i, D_i^{(j)}, get the journey length as a
   * proportion of the longest possible journey from cell j to any other point on the grid,
   * \tilde{D}_i^{(j)} \in [0,1]. That is
   * </p>
   * <p>
   * \tilde{D}_i^{(j)} =: \frac{D_i^{(j)}}{\sup_i D_i^{(j)}}.
   * </p>
   * <p>
   * Note that unlike {@link DistanceCalculator#distInMetres} this quantity is not formally a metric
   * because in general i and j do not commute.
   * </p>
   *
   * @param refPoint
   * @param tgtPoint
   * @return Distance from {@code refPoint} to {@code tgtPoint} as a proportion of the maximum
   *         possible distance from {@code refPoint} to any other point on the grid (dimensionless)
   */
  double propMaxDist(GridPoint refPoint, GridPoint tgtPoint);

}
