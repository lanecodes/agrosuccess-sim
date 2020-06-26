package me.ajlane.geo.repast.soilmoisture;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.traverse.BreadthFirstIterator;
import me.ajlane.geo.GridLoc;

/**
 * Representation of how water flows from cell to cell in a single catchment. Implemented by
 * representing the catchment as a directed simple tree-like graph in which node i is connected to
 * node j if node j drains into node i. The nodes are the all the {@code GridLoc}-s in the landscape
 * which drain out of the outlet cell specified by {@link #getOutlet()}.
 *
 * @author Andrew Lane
 */
public class JgraphtCatchmentFlow implements CatchmentFlow {

  private final GridLoc outlet;
  private final Graph<GridLoc, DefaultEdge> flowSourceNetwork;
  private List<GridLoc> cachedFlowSourceDependencyOrder;

  JgraphtCatchmentFlow(GridLoc outlet, Graph<GridLoc, DefaultEdge> flowSourceNetwork) {
    validateFlowSourceNetwork(flowSourceNetwork);
    this.outlet = outlet;
    this.flowSourceNetwork = flowSourceNetwork;
    this.cachedFlowSourceDependencyOrder = null;
  }

  private void validateFlowSourceNetwork(Graph<GridLoc, DefaultEdge> flowSourceNetwork) {
    if (!flowSourceNetwork.getType().isDirected()) {
      throw new IllegalArgumentException("flowSourceNetwork must be a directed graph");
    }
  }

  @Override
  public GridLoc getOutlet() {
    return this.outlet;
  }

  @Override
  public Set<GridLoc> getSourceCells(GridLoc cell) {
    Set<GridLoc> sourceCells = new HashSet<>();
    for (DefaultEdge edge : this.flowSourceNetwork.outgoingEdgesOf(cell)) {
      sourceCells.add(this.flowSourceNetwork.getEdgeTarget(edge));
    }
    return sourceCells;
  }

  /**
   * A list of all the cells in the catchment in order of decreasing distance to the outlet. The
   * furthest cell from the outlet is the first element, and the outlet itself is the last element.
   * Subsets of cells which are equidistant from the outlet are ordered arbitrarily.
   */
  @Override
  public List<GridLoc> flowSourceDependencyOrder() {
    if (this.cachedFlowSourceDependencyOrder != null) {
      return this.cachedFlowSourceDependencyOrder;
    }
    List<GridLoc> order = new ArrayList<>();
    Iterator<GridLoc> bfsIterator = new BreadthFirstIterator<>(this.flowSourceNetwork, this.outlet);
    while (bfsIterator.hasNext()) {
      order.add(bfsIterator.next());
    }
    Collections.reverse(order);
    this.cachedFlowSourceDependencyOrder = order;
    return order;
  }

  @Override
  public String toString() {
    return "CatchmentNetwork [outlet=" + outlet + ", flowSourceNetwork=" + flowSourceNetwork + "]";
  }

  /**
   * @return The flow source network of all the cells which drain out of the outlet specified by
   *         {@link #getOutlet()}. This is a directed simple tree-like graph in which node i is
   *         connected to node j if node j drains into node i.
   */
  @Override
  public Graph<GridLoc, DefaultEdge> getFlowSourceNetwork() {
    return this.flowSourceNetwork;
  }

}
