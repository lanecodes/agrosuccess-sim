package me.ajlane.geo.repast.soilmoisture;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.util.List;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.junit.Before;
import org.junit.Test;
import me.ajlane.geo.GridLoc;


/**
 * The test catchment used in this unit test is the single outlet test grid shown in
 * <a href="#fig-flow-network">Figure 1</a>.
 *
 * <figure id="fig-flow-network">
 * <img src="./doc-files/flow-drain-graph-1-outlet.svg" style="max-width: 500px;"/><figcaption>
 * <span>Figure 1:</span class=fig-name> Flow source network used in this test. </figcaption>
 * </figure>
 *
 * @author Andrew Lane
 *
 */
public class JgraphtCatchmentFlowTest {

  private CatchmentFlow testCatchment;

  @Before
  public void setUp() {
    Graph<GridLoc, DefaultEdge> graph = new SimpleDirectedGraph<>(DefaultEdge.class);
    for (int x = 0; x < 3; x++) {
      for (int y = 0; y < 3; y++) {
        graph.addVertex(new GridLoc(x, y));
      }
    }
    graph.addEdge(new GridLoc(1, 1), new GridLoc(2, 1));
    graph.addEdge(new GridLoc(1, 1), new GridLoc(1, 0));
    graph.addEdge(new GridLoc(1, 1), new GridLoc(2, 0));
    graph.addEdge(new GridLoc(1, 2), new GridLoc(1, 1));
    graph.addEdge(new GridLoc(1, 2), new GridLoc(2, 2));
    graph.addEdge(new GridLoc(0, 1), new GridLoc(0, 0));
    graph.addEdge(new GridLoc(0, 2), new GridLoc(0, 1));
    graph.addEdge(new GridLoc(0, 2), new GridLoc(1, 2));

    testCatchment = new JgraphtCatchmentFlow(new GridLoc(0, 2), graph);
  }

  @Test
  public void testGetOutlet() {
    assertEquals(new GridLoc(0, 2), testCatchment.getOutlet());
  }

  @Test
  public void testGetSourceCells() {
    assertTrue(testCatchment.getSourceCells(new GridLoc(1, 1)).contains(new GridLoc(2, 1)));
    assertTrue(testCatchment.getSourceCells(new GridLoc(1, 1)).contains(new GridLoc(1, 0)));
    assertTrue(testCatchment.getSourceCells(new GridLoc(1, 1)).contains(new GridLoc(2, 0)));
    assertEquals(3, testCatchment.getSourceCells(new GridLoc(1, 1)).size());

    assertTrue(testCatchment.getSourceCells(new GridLoc(0, 2)).contains(new GridLoc(0, 1)));
    assertTrue(testCatchment.getSourceCells(new GridLoc(0, 2)).contains(new GridLoc(1, 2)));
    assertEquals(2, testCatchment.getSourceCells(new GridLoc(0, 2)).size());

    assertTrue(testCatchment.getSourceCells(new GridLoc(0, 1)).contains(new GridLoc(0, 0)));
    assertEquals(1, testCatchment.getSourceCells(new GridLoc(0, 1)).size());
  }

  @Test
  public void testRidgeCellsReturnEmptySets() {
    assertEquals(0, testCatchment.getSourceCells(new GridLoc(0, 0)).size());
    assertEquals(0, testCatchment.getSourceCells(new GridLoc(1, 0)).size());
    assertEquals(0, testCatchment.getSourceCells(new GridLoc(2, 0)).size());
    assertEquals(0, testCatchment.getSourceCells(new GridLoc(2, 1)).size());
    assertEquals(0, testCatchment.getSourceCells(new GridLoc(2, 2)).size());
  }

  @Test
  public void testFlowSourceDependencyOrder() {
    List<GridLoc> testOrder = testCatchment.flowSourceDependencyOrder();
    assertTrue(testOrder.indexOf(new GridLoc(1, 1)) > testOrder.indexOf(new GridLoc(2, 1)));
    assertTrue(testOrder.indexOf(new GridLoc(1, 2)) > testOrder.indexOf(new GridLoc(1, 0)));
    assertTrue(testOrder.indexOf(new GridLoc(1, 2)) > testOrder.indexOf(new GridLoc(2, 2)));
    assertTrue(testOrder.indexOf(new GridLoc(0, 1)) > testOrder.indexOf(new GridLoc(0, 0)));
    assertEquals(new GridLoc(0, 2), testOrder.get(testOrder.size() - 1));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testExceptionIfGraphNotDirected() {
    Graph<GridLoc, DefaultEdge> undirectedGraph = new SimpleGraph<>(DefaultEdge.class);
    GridLoc outlet = new GridLoc(0, 0);
    undirectedGraph.addVertex(outlet);
    new JgraphtCatchmentFlow(outlet, undirectedGraph);
  }

}
