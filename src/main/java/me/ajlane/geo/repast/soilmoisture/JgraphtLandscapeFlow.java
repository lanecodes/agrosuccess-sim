package me.ajlane.geo.repast.soilmoisture;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.AsSubgraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import me.ajlane.geo.CartesianGridDouble2D;
import me.ajlane.geo.Direction;
import me.ajlane.geo.GridDimensions;
import me.ajlane.geo.GridLoc;
import me.ajlane.geo.GridLocKingMove;

/**
 * <p>
 * Represents the way water flows over a raster grid by representing each catchment in the landscape
 * using a simple directed tree-like graph.
 * </p>
 * <p>
 * Catchments are discovered during construction and used to create {@link JgraphtCatchmentFlow}
 * instances for each of the outlets in the provided flow direction map. We use
 * {@link org.jgrapht.Graph} objects to store and manipulate the graphs. This is because the
 * <a href="https://jgrapht.org/">JGraphT</a> project provide well-tested graph traversal algorithms
 * for these objects which we require and prefer to avoid re-implementing ourselves. We store
 * {@link GridLoc} objects in these graphs and note that they satisfy the
 * <a href="https://jgrapht.org/guide/VertexAndEdgeTypes#equals-and-hashcode">requirements</a>
 * necessary for them to be used as graph keys in a {@code org.jgrapht.Graph}.
 * </p>
 * <p>
 * Our reasons for not using the network implementation in Repast Simphony for this purpose are as
 * follows. The design of Repast network API E.g. in North et al. 2013 the network is used by agents
 * to modify their behaviour in response to changes in the state of their neighbours within their
 * local vicinity in the network. In contrast our use case in the analysis of the flow direction
 * network requires us to analyse the overall structure of the network, in particular using breadth
 * first search which is implemented in JGraphT but not Repast Simphony. We store {@link GridLoc}
 * objects in the graph, rather than decision-making agents as networks in Repast Simphony are
 * designed to represent the relationships between. We are using the graph data structure as an
 * artifact of the implementation/programming domain (i.e. a means to an end) rather than to
 * represent an important aspect of the application domain.
 * </p>
 *
 * <h3>References</h3>
 * <p>
 * North, M. M. J., Collier, N. T. N., Ozik, J., Tatara, E. R., Macal, C. M., Bragen, M., & Sydelko,
 * P. (2013). Complex adaptive systems modeling with repast simphony. Complex Adaptive Systems
 * Modeling, 1(1), 1–26. https://doi.org/10.1186/2194-3206-1-3
 * </p>
 *
 * <h3>TODO</h3>
 * <ul>
 * <li>Refactor {@link #buildFlowSourceNetworkFromFlowDirectionMap()} and
 * {@link #separateCatchments(Set, Graph)} to improve performance. Profiling shows that when
 * building a {@link JgraphtLandscapeFlow} 92% of execution time is spent in
 * org.jgrapht.graph.AsSubgraph.initialize (java.util.Set, java.util.Set). Consider just building
 * separate trees by working backwards from the outlets.</li>
 * <li>Refactor validation code to an optional JgraphtLandscapeFlowValidator object with a
 * JgraphtLandscapeFlowValidator#validate method.</li>
 * </ul>
 *
 * @see LandscapeFlow
 * @author Andrew Lane
 */
public class JgraphtLandscapeFlow extends LandscapeFlow implements Iterable<CatchmentFlow> {

  private Set<CatchmentFlow> catchmentNetworks;

  /**
   * @param flowDirMap Raster grid in which each cell contains a numerical code specifying the
   *        direction in which water flows out of it, e.g. N, SE etc.
   * @param dirMap Mapping between numerical codes and cardinal directions represented by
   *        {@code Direction} constants.
   */
  public JgraphtLandscapeFlow(CartesianGridDouble2D flowDirMap, Map<Integer, Direction> dirMap) {
    this.flowDirMap = flowDirMap;
    this.dirMap = dirMap;

    Set<GridLoc> outlets = getOutlets();
    Graph<GridLoc, DefaultEdge> landscapeFlowSourceNetwork =
        buildFlowSourceNetworkFromFlowDirectionMap();
    this.catchmentNetworks = separateCatchments(outlets, landscapeFlowSourceNetwork);
  }

  /**
   * @return Flow source network for whole landscape
   */
  private Graph<GridLoc, DefaultEdge> buildFlowSourceNetworkFromFlowDirectionMap() {
    GridDimensions dims = flowDirMap.getDimensions();
    Graph<GridLoc, DefaultEdge> graph = new SimpleDirectedGraph<>(DefaultEdge.class);

    for (int x = 0; x < dims.getWidth(); x++) {
      for (int y = 0; y < dims.getHeight(); y++) {
        GridLoc upslope = new GridLoc(x, y);
        graph.addVertex(upslope);
        Direction flowDir = dirMap.get((int) flowDirMap.getValue(upslope));
        GridLoc downslope = new GridLocKingMove(upslope, flowDir).getEndPoint();
        if (pointInGrid(downslope, dims)) {
          graph.addVertex(downslope);
          graph.addEdge(downslope, upslope);
        }
      }
    }

    return graph;
  }

  /**
   * @param outlets Set of grid cells through which water drains out of the raster grid
   * @param landscapeFlowSourceNetwork Tree-like graph encoding which cells drain into which other
   *        cells for the whole landscape
   * @return A set of {@link CatchmentFlow} objects representing the flow of water through each
   *         individual catchment.
   */
  private Set<CatchmentFlow> separateCatchments(Set<GridLoc> outlets,
      Graph<GridLoc, DefaultEdge> landscapeFlowSourceNetwork) {
    Set<CatchmentFlow> catchments = new HashSet<>();
    ConnectivityInspector<GridLoc, DefaultEdge> connInspector =
        new ConnectivityInspector<>(landscapeFlowSourceNetwork);
    for (GridLoc outlet : outlets) {
      Set<GridLoc> outletSet = connInspector.connectedSetOf(outlet);
      Graph<GridLoc, DefaultEdge> catchmentGraph =
          new AsSubgraph<>(landscapeFlowSourceNetwork, outletSet);
      catchments.add(new JgraphtCatchmentFlow(outlet, catchmentGraph));
    }

    validateCatchmentSet(catchments);

    return catchments;
  }

  private void validateCatchmentSet(Set<CatchmentFlow> catchments) {
    checkCatchmentsAreUnique(catchments);
    checkCatchmentsAccountForAllGridCells(catchments);
  }

  /**
   * Catch cases where multiple outlets end up in the same connected component
   *
   * @param catchments
   */
  private void checkCatchmentsAreUnique(Set<CatchmentFlow> catchments) {
    Set<List<GridLoc>> drainageLists = new HashSet<>();
    for (CatchmentFlow nwk : catchments) {
      drainageLists.add(nwk.flowSourceDependencyOrder());
    }
    if (drainageLists.size() < catchments.size()) {
      throw new IllegalStateException("Multiple outlets allocated to same connected component.");
    }
  }

  /**
   * Catch cases where some grid cells are omitted from all catchments
   *
   * @param catchments
   */
  private void checkCatchmentsAccountForAllGridCells(Set<CatchmentFlow> catchments) {
    int totalGraphCells = 0;
    GridDimensions dims = this.flowDirMap.getDimensions();
    int totalGridCells = dims.getHeight() * dims.getWidth();
    for (CatchmentFlow nwk : catchments) {
      totalGraphCells += nwk.flowSourceDependencyOrder().size();
    }
    if (totalGraphCells != totalGridCells) {
      throw new IllegalStateException("Numbers of cells in graph (" + totalGraphCells
          + ") doesn't match the number of cells in the grid (" + totalGridCells + ")");
    }
  }

  @Override
  public Iterator<CatchmentFlow> iterator() {
    return this.catchmentNetworks.iterator();
  }

  @Override
  public String toString() {
    return "LandscapeNetwork [flowDirMap=" + flowDirMap + ", dirMap=" + dirMap
        + ", catchmentNetworks=" + catchmentNetworks + "]";
  }

}
