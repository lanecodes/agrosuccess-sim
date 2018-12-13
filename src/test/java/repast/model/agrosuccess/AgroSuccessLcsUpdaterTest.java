package repast.model.agrosuccess;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import me.ajlane.geo.repast.soilmoisture.AgroSuccessSoilMoistureDiscretiser;
import me.ajlane.geo.repast.soilmoisture.SoilMoistureDiscretiser;
import me.ajlane.geo.repast.succession.AgroSuccessLcsUpdateDecider;
import me.ajlane.geo.repast.succession.CodedEnvrAntecedent;
import me.ajlane.geo.repast.succession.CodedEnvrConsequent;
import me.ajlane.geo.repast.succession.CodedLcsTransitionMap;
import me.ajlane.geo.repast.succession.LcsUpdateDecider;
import me.ajlane.geo.repast.succession.LcsUpdater;
import repast.simphony.context.Context;
import repast.simphony.context.DefaultContext;
import repast.simphony.valueLayer.GridValueLayer;
import static me.ajlane.geo.repast.RepastGridUtils.arrayToGridValueLayer;
import static me.ajlane.geo.repast.RepastGridUtils.gridValueLayerToString;
import static me.ajlane.geo.repast.RepastGridUtils.gridValueLayersAreEqual;;

/**
 * @author Andrew Lane
 *
 */
public class AgroSuccessLcsUpdaterTest {
  SoilMoistureDiscretiser smDiscretiser;
  LcsUpdateDecider updateDecider;

  /**
   * 
   * Note that no entry is made for pathway 2, as it has no target state so is not expected to be
   * found in the transition map
   * 
   * @return The transition map which will be used for each test case, according to the transition
   *         pathways described in the javadoc for this class.
   * 
   * @see me.ajlane.geo.repast.succession.AgroSuccessLcsUpdateDeciderTest#makeTestCodedLcsTransitionMap()
   */
  private CodedLcsTransitionMap makeTestCodedLcsTransitionMap() {
    CodedLcsTransitionMap transMap = new CodedLcsTransitionMap();

    // Add transition pathway 1 to transition map
    transMap.put(new CodedEnvrAntecedent(5, 0, 1, 1, 0, 1, 0), new CodedEnvrConsequent(6, 15));

    // Add transition pathway 3 to transition map
    transMap.put(new CodedEnvrAntecedent(5, 0, 1, 1, 0, 1, 2), new CodedEnvrConsequent(9, 20));

    // Add transition pathway 4 to transition map
    transMap.put(new CodedEnvrAntecedent(6, 0, 1, 1, 0, 1, 2), new CodedEnvrConsequent(7, 15));

    return transMap;
  }

  /**
   * In this context all grid cells contain the same value, and each layer's values correspond to
   * Scenario 1 time step 4 documented in
   * {@link me.ajlane.geo.repast.succession.AgroSuccessLcsUpdateDeciderTest}. *
   * 
   * @return A newly minted context to use in below test cases
   */
  private Context<Object> getUniformContext() {
    Context<Object> context = new DefaultContext<>();

    context.addValueLayer(new GridValueLayer("lct", 5, true, 3, 3)); // everywhere shrubland
    context.addValueLayer(new GridValueLayer("oak regeneration", 0, true, 3, 3));
    context.addValueLayer(new GridValueLayer("aspect", 1, true, 3, 3));
    context.addValueLayer(new GridValueLayer("pine seeds", 1, true, 3, 3));
    context.addValueLayer(new GridValueLayer("oak seeds", 0, true, 3, 3));
    context.addValueLayer(new GridValueLayer("deciduous seeds", 1, true, 3, 3));
    context.addValueLayer(new GridValueLayer("soil moisture", 200, true, 3, 3)); // xeric
    context.addValueLayer(new GridValueLayer("time in lcs", 14, true, 3, 3));
    context.addValueLayer(new GridValueLayer("target lcs", 6, true, 3, 3));
    context.addValueLayer(new GridValueLayer("lcs transition time", 15, true, 3, 3));

    return context;
  }

  /**
   * <b>Time evolution of heteroContext</b></br>
   * 
   * <pre class="example">
   * t        lct                ΔD                ΔT              T_{in}            pathway
   *    
   *    +----+----+----+  +----+----+----+  +----+----+----+  +----+----+----+  +----+----+----+
   *    |  5 |  5 |  5 |  |  6 |  6 |  6 |  | 15 | 15 | 15 |  | 15 | 15 | 14 |  |  1 |  1 |  1 |    
   *    +----+----+----+  +----+----+----+  +----+----+----+  +----+----+----+  +----+----+----+    
   * 0  |  5 |  5 |  5 |  |  6 |  9 |  9 |  | 15 | 20 | 20 |  | 14 | 19 | 18 |  |  1 |  3 |  3 |    
   *    +----+----+----+  +----+----+----+  +----+----+----+  +----+----+----+  +----+----+----+    
   *    |  5 |  5 |  5 |  |  9 |  9 |  9 |  | 20 | 20 | 20 |  | 18 | 18 | 18 |  |  3 |  3 |  3 |    
   *    +----+----+----+  +----+----+----+  +----+----+----+  +----+----+----+  +----+----+----+
   *    
   *    +----+----+----+  +----+----+----+  +----+----+----+  +----+----+----+  +----+----+----+
   *    |  6 |  6 |  5 |  | -1 | -1 |  6 |  | -1 | -1 | 15 |  |  1 |  1 | 15 |  | -1 | -1 |  1 |
   *    +----+----+----+  +----+----+----+  +----+----+----+  +----+----+----+  +----+----+----+
   * 1  |  5 |  5 |  5 |  |  6 |  9 |  9 |  | 15 | 20 | 20 |  | 15 | 20 | 19 |  |  1 |  3 |  3 |
   *    +----+----+----+  +----+----+----+  +----+----+----+  +----+----+----+  +----+----+----+
   *    |  5 |  5 |  5 |  |  9 |  9 |  9 |  | 20 | 20 | 20 |  | 19 | 19 | 19 |  |  3 |  3 |  3 |
   *    +----+----+----+  +----+----+----+  +----+----+----+  +----+----+----+  +----+----+----+
   * 
   *    +----+----+----+  +----+----+----+  +----+----+----+  +----+----+----+  +----+----+----+
   *    |  6 |  6 |  6 |  | -1 | -1 | -1 |  | -1 | -1 | -1 |  |  2 |  2 |  1 |  | -1 | -1 | -1 |
   *    +----+----+----+  +----+----+----+  +----+----+----+  +----+----+----+  +----+----+----+
   * 2  |  6 |  9 |  5 |  | -1 | -1 |  9 |  | -1 | -1 | 20 |  |  1 |  1 | 20 |  | -1 | -1 |  3 |
   *    +----+----+----+  +----+----+----+  +----+----+----+  +----+----+----+  +----+----+----+
   *    |  5 |  5 |  5 |  |  9 |  9 |  9 |  | 20 | 20 | 20 |  | 20 | 20 | 20 |  |  3 |  3 |  3 |
   *    +----+----+----+  +----+----+----+  +----+----+----+  +----+----+----+  +----+----+----+
   * </pre>
   * 
   * @return
   */
  private Context<Object> getHeteroContext() {
    Context<Object> context = new DefaultContext<>();

    context
        .addValueLayer(arrayToGridValueLayer("lct", new int[][] {{5, 5, 5}, {5, 5, 5}, {5, 5, 5}}));

    context.addValueLayer(
        arrayToGridValueLayer("oak regeneration", new int[][] {{0, 0, 0}, {0, 0, 0}, {0, 0, 0}}));

    context.addValueLayer(
        arrayToGridValueLayer("aspect", new int[][] {{1, 1, 1}, {1, 1, 1}, {1, 1, 1}}));

    context.addValueLayer(
        arrayToGridValueLayer("pine seeds", new int[][] {{1, 1, 1}, {1, 1, 1}, {1, 1, 1}}));

    context.addValueLayer(
        arrayToGridValueLayer("oak seeds", new int[][] {{0, 0, 0}, {0, 0, 0}, {0, 0, 0}}));

    context.addValueLayer(
        arrayToGridValueLayer("deciduous seeds", new int[][] {{1, 1, 1}, {1, 1, 1}, {1, 1, 1}}));

    context.addValueLayer(arrayToGridValueLayer("soil moisture",
        new int[][] {{100, 100, 100}, {100, 2000, 2000}, {2000, 2000, 2000}}));

    context.addValueLayer(arrayToGridValueLayer("time in lcs",
        new int[][] {{15, 15, 14}, {14, 19, 18}, {18, 18, 18}}));

    context.addValueLayer(
        arrayToGridValueLayer("target lcs", new int[][] {{6, 6, 6}, {6, 9, 9}, {9, 9, 9}}));

    context.addValueLayer(arrayToGridValueLayer("lcs transition time",
        new int[][] {{15, 15, 15}, {15, 20, 20}, {20, 20, 20}}));

    return context;
  }

  @Before
  public void setUp() {
    smDiscretiser = new AgroSuccessSoilMoistureDiscretiser(500, 1000);
    updateDecider = new AgroSuccessLcsUpdateDecider(makeTestCodedLcsTransitionMap());
  }

  @After
  public void tearDown() {
    smDiscretiser = null;
    updateDecider = null;
  }

  private String errorStr(int row, int col, double value, String layerName) {
    return "row " + row + ", column " + col + "in layer " + layerName + " should be " + value
        + " but isn't.";
  }

  @Test
  public void testUniformContextAfter1Timestep() {
    Context<Object> context = getUniformContext();
    LcsUpdater lcsUpdater = new AgroSuccessLcsUpdater(context, updateDecider, smDiscretiser);

    lcsUpdater.updateLandscapeLcs();

    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        assertEquals(errorStr(i, j, 5, "lct"), 5, (int) context.getValueLayer("lct").get(i, j));

        assertEquals(errorStr(i, j, 15, "time in lcs"), 15,
            (int) context.getValueLayer("time in lcs").get(i, j));

        assertEquals(errorStr(i, j, 15, "lcs transition time"), 15,
            (int) context.getValueLayer("lcs transition time").get(i, j));

        assertEquals(errorStr(i, j, 6, "target lcs"), 6,
            (int) context.getValueLayer("target lcs").get(i, j));
      }
    }

  }

  /**
   * Test demonstrates a land cover transition taking place.
   * 
   * Also shows how -1 should be used to encode 'no target land cover state' and 'no target land
   * cover transition time'
   */
  @Test
  public void testUniformContextAfter2Timesteps() {
    Context<Object> context = getUniformContext();
    LcsUpdater lcsUpdater = new AgroSuccessLcsUpdater(context, updateDecider, smDiscretiser);

    lcsUpdater.updateLandscapeLcs();
    lcsUpdater.updateLandscapeLcs();

    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        assertEquals(errorStr(i, j, 6, "lct"), 6, (int) context.getValueLayer("lct").get(i, j));

        assertEquals(errorStr(i, j, 1, "time in lcs"), 1,
            (int) context.getValueLayer("time in lcs").get(i, j));

        assertEquals(errorStr(i, j, -1, "lcs transition time"), -1,
            (int) context.getValueLayer("lcs transition time").get(i, j));

        assertEquals(errorStr(i, j, -1, "target lcs"), -1,
            (int) context.getValueLayer("target lcs").get(i, j));
      }
    }
  }

  @Test
  public void testUniformContextAfter3Timesteps() {
    Context<Object> context = getUniformContext();
    LcsUpdater lcsUpdater = new AgroSuccessLcsUpdater(context, updateDecider, smDiscretiser);

    lcsUpdater.updateLandscapeLcs();
    lcsUpdater.updateLandscapeLcs();
    lcsUpdater.updateLandscapeLcs();

    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        assertEquals(errorStr(i, j, 6, "lct"), 6, (int) context.getValueLayer("lct").get(i, j));

        assertEquals(errorStr(i, j, 2, "time in lcs"), 2,
            (int) context.getValueLayer("time in lcs").get(i, j));

        assertEquals(errorStr(i, j, -1, "lcs transition time"), -1,
            (int) context.getValueLayer("lcs transition time").get(i, j));

        assertEquals(errorStr(i, j, -1, "target lcs"), -1,
            (int) context.getValueLayer("target lcs").get(i, j));
      }
    }
  }

  private String gvlErrorStr(GridValueLayer expectedGvl, GridValueLayer actualGvl) {
    return "expected:\n" + gridValueLayerToString(expectedGvl) + "but got:\n"
        + gridValueLayerToString(actualGvl);
  }

  @Test
  public void testHeteroContextAfter1TimestepLctUpdates() {
    Context<Object> context = getHeteroContext();
    LcsUpdater lcsUpdater = new AgroSuccessLcsUpdater(context, updateDecider, smDiscretiser);
    lcsUpdater.updateLandscapeLcs();

    GridValueLayer expectedGvl =
        arrayToGridValueLayer("lct", new int[][] {{6, 6, 5}, {5, 5, 5}, {5, 5, 5}});

    GridValueLayer actualGvl = (GridValueLayer) context.getValueLayer("lct");

    assertTrue(gvlErrorStr(expectedGvl, actualGvl),
        gridValueLayersAreEqual(expectedGvl, actualGvl));
  }

  @Test
  public void testHeteroContextAfter1TimestepDeltaDUpdates() {
    Context<Object> context = getHeteroContext();
    LcsUpdater lcsUpdater = new AgroSuccessLcsUpdater(context, updateDecider, smDiscretiser);
    lcsUpdater.updateLandscapeLcs();

    GridValueLayer expectedGvl =
        arrayToGridValueLayer("target lcs", new int[][] {{-1, -1, 6}, {6, 9, 9}, {9, 9, 9}});

    GridValueLayer actualGvl = (GridValueLayer) context.getValueLayer("target lcs");

    assertTrue(gvlErrorStr(expectedGvl, actualGvl),
        gridValueLayersAreEqual(expectedGvl, actualGvl));
  }

  @Test
  public void testHeteroContextAfter1TimestepDeltaTUpdates() {
    Context<Object> context = getHeteroContext();
    LcsUpdater lcsUpdater = new AgroSuccessLcsUpdater(context, updateDecider, smDiscretiser);
    lcsUpdater.updateLandscapeLcs();

    GridValueLayer expectedGvl = arrayToGridValueLayer("lcs transition time",
        new int[][] {{1, 1, 15}, {15, 20, 19}, {19, 19, 19}});

    GridValueLayer actualGvl = (GridValueLayer) context.getValueLayer("lcs transition time");

    assertTrue(gvlErrorStr(expectedGvl, actualGvl),
        gridValueLayersAreEqual(expectedGvl, actualGvl));
  }

  @Test
  public void testHeteroContextAfter1TimestepTimeInStateUpdates() {
    Context<Object> context = getHeteroContext();
    LcsUpdater lcsUpdater = new AgroSuccessLcsUpdater(context, updateDecider, smDiscretiser);
    lcsUpdater.updateLandscapeLcs();

    GridValueLayer expectedGvl = arrayToGridValueLayer("time in lcs",
        new int[][] {{1, 1, 15}, {15, 20, 19}, {19, 19, 19}});

    GridValueLayer actualGvl = (GridValueLayer) context.getValueLayer("time in lcs");

    assertTrue(gvlErrorStr(expectedGvl, actualGvl),
        gridValueLayersAreEqual(expectedGvl, actualGvl));
  }
  
  @Test
  public void testHeteroContextAfter2TimestepsLctUpdates() {
    Context<Object> context = getHeteroContext();
    LcsUpdater lcsUpdater = new AgroSuccessLcsUpdater(context, updateDecider, smDiscretiser);
    lcsUpdater.updateLandscapeLcs();

    GridValueLayer expectedGvl =
        arrayToGridValueLayer("lct", new int[][] {{6, 6, 6}, {6, 9, 5}, {5, 5, 5}});

    GridValueLayer actualGvl = (GridValueLayer) context.getValueLayer("lct");

    assertTrue(gvlErrorStr(expectedGvl, actualGvl),
        gridValueLayersAreEqual(expectedGvl, actualGvl));
  }

  @Test
  public void testHeteroContextAfter2TimestepsDeltaDUpdates() {
    Context<Object> context = getHeteroContext();
    LcsUpdater lcsUpdater = new AgroSuccessLcsUpdater(context, updateDecider, smDiscretiser);
    lcsUpdater.updateLandscapeLcs();

    GridValueLayer expectedGvl =
        arrayToGridValueLayer("target lcs", new int[][] {{-1, -1, -1}, {-1, -1, 9}, {9, 9, 9}});

    GridValueLayer actualGvl = (GridValueLayer) context.getValueLayer("target lcs");

    assertTrue(gvlErrorStr(expectedGvl, actualGvl),
        gridValueLayersAreEqual(expectedGvl, actualGvl));
  }

  @Test
  public void testHeteroContextAfter2TimestepsDeltaTUpdates() {
    Context<Object> context = getHeteroContext();
    LcsUpdater lcsUpdater = new AgroSuccessLcsUpdater(context, updateDecider, smDiscretiser);
    lcsUpdater.updateLandscapeLcs();

    GridValueLayer expectedGvl = arrayToGridValueLayer("lcs transition time",
        new int[][] {{-1, -1, -1}, {-1, -1, 20}, {20, 20, 20}});

    GridValueLayer actualGvl = (GridValueLayer) context.getValueLayer("lcs transition time");

    assertTrue(gvlErrorStr(expectedGvl, actualGvl),
        gridValueLayersAreEqual(expectedGvl, actualGvl));
  }

  @Test
  public void testHeteroContextAfter2TimestepsTimeInStateUpdates() {
    Context<Object> context = getHeteroContext();
    LcsUpdater lcsUpdater = new AgroSuccessLcsUpdater(context, updateDecider, smDiscretiser);
    lcsUpdater.updateLandscapeLcs();

    GridValueLayer expectedGvl = arrayToGridValueLayer("time in lcs",
        new int[][] {{2, 2, 1}, {1, 1, 20}, {20, 20, 20}});

    GridValueLayer actualGvl = (GridValueLayer) context.getValueLayer("time in lcs");

    assertTrue(gvlErrorStr(expectedGvl, actualGvl),
        gridValueLayersAreEqual(expectedGvl, actualGvl));
  }

}

