package me.ajlane.geo.repast.soilmoisture;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import me.ajlane.geo.CartesianGridDouble2D;
import me.ajlane.geo.DefaultCartesianGridDouble2D;
import me.ajlane.geo.Direction;
import me.ajlane.geo.GridLoc;

public class JgraphtLandscapeFlowTest {

  CartesianGridDouble2D flowDirMapOneOutlet;
  CartesianGridDouble2D flowDirMapTwoOutlets;
  CartesianGridDouble2D flowDirMapOneOrphanOutlet;
  Map<Integer, Direction> dirMap;

  /**
   * Directions are encoded as 1 -East, 2 - Northeast, 3 - North, 4 - Northwest, 5 - West, 6 -
   * Southwest, 7 - South, 8 - Southeast.
   */
  @Before
  public void setUp() {
    // Cartesian cell (0, 2) is an outlet
    flowDirMapOneOutlet =
        new DefaultCartesianGridDouble2D(new double[][] {{3, 5, 5}, {3, 3, 5}, {3, 3, 4}});
    // Cartesian cells (0, 2) and (2, 2) are outlets
    flowDirMapTwoOutlets =
        new DefaultCartesianGridDouble2D(new double[][] {{3, 5, 1}, {3, 2, 3}, {3, 3, 4}});
    // Cartesian cells (0, 2) and (2, 2) are outlets, (2, 2) has no inputs
    flowDirMapOneOrphanOutlet =
        new DefaultCartesianGridDouble2D(new double[][] {{3, 5, 1}, {3, 3, 4}, {3, 3, 4}});

    // Corresponds to TauDem D8 flow direction output
    // https://hydrology.usu.edu/taudem/taudem5/help53/D8FlowDirections.html
    dirMap = new HashMap<>();
    dirMap.put(1, Direction.E);
    dirMap.put(2, Direction.NE);
    dirMap.put(3, Direction.N);
    dirMap.put(4, Direction.NW);
    dirMap.put(5, Direction.W);
    dirMap.put(6, Direction.SW);
    dirMap.put(7, Direction.S);
    dirMap.put(8, Direction.SE);
  }

  @Test
  public void testGetOutletsWithOneOutlet() {
    LandscapeFlow nwk1 = new JgraphtLandscapeFlow(flowDirMapOneOutlet, dirMap);
    assertEquals(1, nwk1.getOutlets().size());
    assertTrue(nwk1.getOutlets().contains(new GridLoc(0, 2)));
  }

  @Test
  public void testConstructedGraphsWitheOneOutlet() {
    LandscapeFlow nwk1 = new JgraphtLandscapeFlow(flowDirMapOneOutlet, dirMap);
    List<CatchmentFlow> catchments = new ArrayList<>();
    for (CatchmentFlow catchment : nwk1) {
      catchments.add(catchment);
    }
    assertEquals(1, catchments.size());
    // outlet is correct
    assertEquals(new GridLoc(0, 2), catchments.get(0).getOutlet());
    // Spot checks some edges are correct
    CatchmentFlow catchmentNwk = catchments.get(0);
    assertTrue(catchmentNwk.getSourceCells(new GridLoc(0, 1)).contains(new GridLoc(0, 0)));
    assertTrue(catchmentNwk.getSourceCells(new GridLoc(1, 1)).contains(new GridLoc(2, 0)));
    // Check node degrees as expected
    assertEquals(3, catchmentNwk.getSourceCells(new GridLoc(1, 1)).size());
    assertEquals(2, catchmentNwk.getSourceCells(new GridLoc(1, 2)).size());
  }

  @Test
  public void testGetOutletsWithTwoOutlets() {
    LandscapeFlow nwk2 = new JgraphtLandscapeFlow(flowDirMapTwoOutlets, dirMap);
    assertEquals(2, nwk2.getOutlets().size());
    assertTrue(nwk2.getOutlets().contains(new GridLoc(0, 2)));
    assertTrue(nwk2.getOutlets().contains(new GridLoc(2, 2)));
  }

  @Test
  public void testConstructedGraphsWitheTwoOutlets() {
    LandscapeFlow nwk2 = new JgraphtLandscapeFlow(flowDirMapTwoOutlets, dirMap);
    Map<GridLoc, CatchmentFlow> catchments = new HashMap<>();
    for (CatchmentFlow catchment : nwk2) {
      catchments.put(catchment.getOutlet(), catchment);
    }
    assertEquals(2, catchments.size()); // Two catchments
    // Correct numbers of cells drain into each catchment
    assertEquals(4, catchments.get(new GridLoc(0, 2)).flowSourceDependencyOrder().size());
    assertEquals(5, catchments.get(new GridLoc(2, 2)).flowSourceDependencyOrder().size());
  }

  /**
   * Catch issue where outlets which don't have any inputs weren't included in network and caused
   * errors.
   */
  @Test
  public void testOutletWithNoInput() {
    LandscapeFlow nwk3 = new JgraphtLandscapeFlow(flowDirMapOneOrphanOutlet, dirMap);
    Map<GridLoc, CatchmentFlow> catchments = new HashMap<>();
    for (CatchmentFlow catchment : nwk3) {
      catchments.put(catchment.getOutlet(), catchment);
    }
    assertEquals(2, catchments.size()); // Two catchments
    // Correct numbers of cells drain into each catchment
    assertEquals(8, catchments.get(new GridLoc(0, 2)).flowSourceDependencyOrder().size());
    assertEquals(1, catchments.get(new GridLoc(2, 2)).flowSourceDependencyOrder().size());
  }

}
