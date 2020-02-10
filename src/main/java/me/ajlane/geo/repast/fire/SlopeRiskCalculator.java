package me.ajlane.geo.repast.fire;

import me.ajlane.geo.Direction;
import me.ajlane.geo.repast.RepastGridUtils;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.valueLayer.ValueLayer;

/**
 * Calculates slope risks for a given Digital Elevation Map (DEM) after <a
 * href"https://doi.org/10.1016/j.envsoft.2009.03.013">Millintgon et al. 2009</a>.
 *
 * @author Andrew Lane
 *
 */
public class SlopeRiskCalculator {

  private ValueLayer dem;
  private final double edgeAdjacentRun;
  private final double cornerAdjacentRun;

  /**
   * @param dem Digital elevation model representing the landscape.
   * @param gridCellSize Edge length of each grid cell in the DEM. Percent slope is calculated as
   *        run/rise * 100%. The {@code gridCellSize} provides the run for edge adjacent cells, and
   *        {code gridCellSize * Math.sqrt(2)} gives the run for corner adjacent cells.
   */
  public SlopeRiskCalculator(ValueLayer dem, double gridCellSize) {
    this.dem = dem;
    this.edgeAdjacentRun = gridCellSize;
    this.cornerAdjacentRun = gridCellSize * Math.sqrt(2);
  }

  /**
   * The slope from cell i to cell j is given by:
   * </p>
   * <p>
   * s_{i \rightarrow j} = \frac{e_j - e_i}{\alpha_{ij} d} \times 100%
   * </p>
   * <p>
   * where e_i - e_j is the <emph>rise</emph> of the slope, and \alpha{ij} d is the <emph>run</emph>
   * from cell i to another cell in the 3 x 3 neighbourhood, j. \alpha{ij} = 1 if cell j is is
   * edge-adjacent to cell i, and \alpha{ij} = \sqrt{2} if cell j is corner-adjacent to cell i.
   * </p>
   * <p>
   * Slope risk is calculated from percentage slope using the values given in Table 3 by <a
   * href"https://doi.org/10.1016/j.envsoft.2009.03.013">Millintgon et al. 2009</a>.
   * </p>
   *
   * @see me.ajlane.geo.repast.fire.SlopeRiskCalculator#slopePctToRisk
   *
   * @param gridPoint Current point.
   * @param direction Direction of the target cell with respect to our current point.
   * @return Slope risk associated with a fire spreading from {@code gridPoint} to the target
   *         adjacent cell in direction {@code direction}. Note that if the target cell is not in
   *         the DEM grid associated with this {@SlopeRiskCalculator} this function will return
   *         {@code null}. It is the responsibility of the client code to handle this situation
   *         gracefully.
   */
  public Double getSlopeRisk(GridPoint gridPoint, Direction direction) {
    GridPoint targetPoint = RepastGridUtils.adjacentPointInDirection(gridPoint, direction);
    if (!RepastGridUtils.pointInValueLayer2D(targetPoint, this.dem)) {
      return null; // Return null if candidate target point is off the edge of the grid
    }
    double sourcePointElev = this.dem.get(gridPoint.getX(), gridPoint.getY());
    double targetPointElev = this.dem.get(targetPoint.getX(), targetPoint.getY());

    double slopePct =
        (targetPointElev - sourcePointElev) / runBetweenAdjacentPoints(direction) * 100;

    return new Double(slopePctToRisk(slopePct));
  }

  private double runBetweenAdjacentPoints(Direction direction) {
    double run;

    switch (direction) {
      case N:
        run = this.edgeAdjacentRun;
        break;
      case NE:
        run = this.cornerAdjacentRun;
        break;
      case E:
        run = this.edgeAdjacentRun;
        break;
      case SE:
        run = this.cornerAdjacentRun;
        break;
      case S:
        run = this.edgeAdjacentRun;
        break;
      case SW:
        run = this.cornerAdjacentRun;
        break;
      case W:
        run = this.edgeAdjacentRun;
        break;
      case NW:
        run = this.cornerAdjacentRun;
        break;
      default:
        throw new RuntimeException("Direction unexpectedly not recognised.");
    }

    return run;
  }

  /**
   * @param slopePct
   * @return Slope risk factor corresponding to {@code slopePct} after <a
   *         href"https://doi.org/10.1016/j.envsoft.2009.03.013">Millintgon et al. 2009</a>.
   */
  private double slopePctToRisk(double slopePct) {
    double risk;
    if (slopePct < -25) {
      risk = 0.8;
    } else if (slopePct < -15) {
      risk = 0.9;
    } else if (slopePct < -5) {
      risk = 0.95;
    } else if (slopePct < 5) {
      risk = 1.0;
    } else if (slopePct < 15) {
      risk = 1.05;
    } else if (slopePct < 25) {
      risk = 1.1;
    } else {
      risk = 1.2;
    }
    return risk;
  }

}
