package me.ajlane.geo.repast.soilmoisture;

import java.util.List;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import me.ajlane.geo.GridLoc;


/**
 * Representation of how water flows from grid cell to grid cell in an individual drainage basin, or
 * catchment. A catchment is a set of grid cells representing an area in which all water drains out
 * of the grid to a single point called the <emph>outlet</emph>.
 *
 * @author Andrew Lane
 */
public interface CatchmentFlow {

  /**
   * @return The grid cell through which all cells in the catchment drain out of the grid
   */
  GridLoc getOutlet();

  /**
   * @param cell
   * @return The set of cells which drain into {@code cell}
   */
  Set<GridLoc> getSourceCells(GridLoc cell);

  /**
   * The last element the returned list is the catchment's outlet cell. The method used to achieve
   * the required ordering is at the discretion of implementing classes, and clients should note
   * that in most cases there will be multiple orderings which are compatible with the follow source
   * dependency order.
   *
   * @return A list containing all the grid cells in the catchment ordered such that the cells which
   *         drain into cell i are guaranteed to be contained in the subset [0, i).
   */
  List<GridLoc> flowSourceDependencyOrder();

  /**
   * @deprecated Clients should use {@link #flowSourceDependencyOrder()} and
   *             {@link #getSourceCells(GridLoc)} to traverse the catchment network from ridges to
   *             outlet.
   * @return The flow source network of all the cells which drain out of the outlet specified by
   *         {@link #getOutlet()}. This is a directed simple tree-like graph in which node i is
   *         connected to node j if node j drains into node i.
   */
  @Deprecated
  Graph<GridLoc, DefaultEdge> getFlowSourceNetwork();

}
