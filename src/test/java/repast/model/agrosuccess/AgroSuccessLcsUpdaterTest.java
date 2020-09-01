package repast.model.agrosuccess;

import static me.ajlane.geo.repast.RepastGridUtils.arrayToGridValueLayer;
import static me.ajlane.geo.repast.RepastGridUtils.gridValueLayersAreEqual;
import static me.ajlane.geo.repast.RepastGridUtils.valueLayerToString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.util.Arrays;
import java.util.HashSet;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import me.ajlane.geo.repast.soilmoisture.AgroSuccessSoilMoistureDiscretiser;
import me.ajlane.geo.repast.soilmoisture.SoilMoistureDiscretiser;
import me.ajlane.geo.repast.soilmoisture.SoilMoistureParams;
import me.ajlane.geo.repast.succession.AgroSuccessLcsUpdateDecider;
import me.ajlane.geo.repast.succession.LcsUpdateDecider;
import me.ajlane.geo.repast.succession.LcsUpdater;
import me.ajlane.geo.repast.succession.SeedStateUpdater;
import me.ajlane.geo.repast.succession.SuccessionPathwayUpdater;
import me.ajlane.geo.repast.succession.pathway.coded.CodedEnvrAntecedent;
import me.ajlane.geo.repast.succession.pathway.coded.CodedEnvrConsequent;
import me.ajlane.geo.repast.succession.pathway.coded.CodedLcsTransitionMap;
import repast.model.agrosuccess.AgroSuccessCodeAliases.Lct;
import repast.simphony.context.Context;
import repast.simphony.context.DefaultContext;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.environment.RunState;
import repast.simphony.engine.schedule.ISchedule;
import repast.simphony.engine.schedule.Schedule;
import repast.simphony.valueLayer.GridValueLayer;
import repast.simphony.valueLayer.IGridValueLayer;;

/**
 * Note: This is an integration test rather than a unit test. Checks {@link SeedStateUpdater},
 * {@link SuccessionPathwayUpdater}, {@link AgroSuccessLcsUpdateDecider}, and
 * {@link AgroSuccessLcsUpdater} work together properly.
 *
 * @author Andrew Lane
 *
 */
public class AgroSuccessLcsUpdaterTest {
  SoilMoistureDiscretiser smDiscretiser;
  LcsUpdateDecider updateDecider;

  SuccessionPathwayUpdater successionUpdater;
  SeedStateUpdater seedUpdater;

  Context<Object> context;

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
    transMap.put(new CodedEnvrAntecedent(Lct.Shrubland.getCode(), 0, 1, 1, 0, 1, 0),
        new CodedEnvrConsequent(Lct.Pine.getCode(), 15));

    // Add transition pathway 3 to transition map
    transMap.put(new CodedEnvrAntecedent(Lct.Shrubland.getCode(), 0, 1, 1, 0, 1, 2),
        new CodedEnvrConsequent(Lct.Oak.getCode(), 20));

    // Add transition pathway 4 to transition map
    transMap.put(new CodedEnvrAntecedent(Lct.Pine.getCode(), 0, 1, 1, 0, 1, 2),
        new CodedEnvrConsequent(Lct.TransForest.getCode(), 15));

    return transMap;
  }

  /**
   * In this context all grid cells contain the same value, and each layer's values correspond to
   * Scenario 1 time step 4 documented in
   * {@link me.ajlane.geo.repast.succession.AgroSuccessLcsUpdateDeciderTest}. *
   *
   * @return A newly minted context to use in below test cases
   */
  private Context<Object> addLayersToUniformContext(Context<Object> context) {
    // everywhere shrubland
    context.addValueLayer(new GridValueLayer(LscapeLayer.Lct.name(),
        Lct.Shrubland.getCode(), true, 3, 3));
    context.addValueLayer(new GridValueLayer(LscapeLayer.OakRegen.name(), 0, true, 3, 3));
    context.addValueLayer(new GridValueLayer(LscapeLayer.Aspect.name(), 1, true, 3, 3));
    context.addValueLayer(new GridValueLayer(LscapeLayer.Pine.name(), 1, true, 3, 3));
    context.addValueLayer(new GridValueLayer(LscapeLayer.Oak.name(), 0, true, 3, 3));
    context.addValueLayer(new GridValueLayer(LscapeLayer.Deciduous.name(), 1, true, 3, 3));
    // xeric
    context.addValueLayer(new GridValueLayer(LscapeLayer.SoilMoisture.name(), 200, true, 3, 3));
    context.addValueLayer(new GridValueLayer(LscapeLayer.TimeInState.name(), 14, true, 3, 3));
    context.addValueLayer(new GridValueLayer(LscapeLayer.DeltaD.name(),
        Lct.Pine.getCode(), true, 3, 3));
    context.addValueLayer(new GridValueLayer(LscapeLayer.DeltaT.name(), 15, true, 3, 3));
    context.addValueLayer(new GridValueLayer(LscapeLayer.FireCount.name(), 0, true, 3, 3));
    context.addValueLayer(new GridValueLayer(LscapeLayer.OakAge.name(), 0, true, 3, 3));

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
  private Context<Object> addLayersToHeteroContext(Context<Object> context) {
    int shrubCode = Lct.Shrubland.getCode();
    int pineCode = Lct.Pine.getCode();
    int oakCode = Lct.Oak.getCode();

    context.addValueLayer(arrayToGridValueLayer(LscapeLayer.Lct.name(),
        new int[][] {
            {shrubCode, shrubCode, shrubCode},
            {shrubCode, shrubCode, shrubCode},
            {shrubCode, shrubCode, shrubCode}}));

    context.addValueLayer(arrayToGridValueLayer(LscapeLayer.OakRegen.name(),
        new int[][] {{0, 0, 0}, {0, 0, 0}, {0, 0, 0}}));

    context.addValueLayer(arrayToGridValueLayer(LscapeLayer.Aspect.name(),
        new int[][] {{1, 1, 1}, {1, 1, 1}, {1, 1, 1}}));

    context.addValueLayer(arrayToGridValueLayer(LscapeLayer.Pine.name(),
        new int[][] {{1, 1, 1}, {1, 1, 1}, {1, 1, 1}}));

    context.addValueLayer(arrayToGridValueLayer(LscapeLayer.Oak.name(),
        new int[][] {{0, 0, 0}, {0, 0, 0}, {0, 0, 0}}));

    context.addValueLayer(arrayToGridValueLayer(LscapeLayer.Deciduous.name(),
        new int[][] {{1, 1, 1}, {1, 1, 1}, {1, 1, 1}}));

    context.addValueLayer(arrayToGridValueLayer(LscapeLayer.SoilMoisture.name(),
        new int[][] {{100, 100, 100}, {100, 2000, 2000}, {2000, 2000, 2000}}));

    context.addValueLayer(arrayToGridValueLayer(LscapeLayer.TimeInState.name(),
        new int[][] {{15, 15, 14}, {14, 19, 18}, {18, 18, 18}}));

    context.addValueLayer(arrayToGridValueLayer(LscapeLayer.DeltaD.name(),
        new int[][] {
            {pineCode, pineCode, pineCode},
            {pineCode, oakCode, oakCode},
            {oakCode, oakCode, oakCode}}));

    context.addValueLayer(arrayToGridValueLayer(LscapeLayer.DeltaT.name(),
        new int[][] {{15, 15, 15}, {15, 20, 20}, {20, 20, 20}}));

    context.addValueLayer(arrayToGridValueLayer(LscapeLayer.FireCount.name(),
        new int[][] {{0, 0, 0}, {0, 1, 1}, {1, 1, 2}}));

    context.addValueLayer(arrayToGridValueLayer(LscapeLayer.OakAge.name(),
        new int[][] {{0, 0, 0}, {0, 0, 0}, {0, 0, 0}}));

    return context;
  }

  @Before
  public void setUp() {
    ISchedule schedule = new Schedule();
    RunEnvironment.init(schedule, null, null, true);
    context = new DefaultContext<>();
    RunState.init().setMasterContext(context);

    smDiscretiser = new AgroSuccessSoilMoistureDiscretiser(new SoilMoistureParams(500, 1000));
    successionUpdater = new SuccessionPathwayUpdater(200.);
    seedUpdater = new SeedStateUpdater(new HashSet<Integer>(Arrays.asList(Lct.Pine.getCode(),
        Lct.Oak.getCode(), Lct.Deciduous.getCode())));
    updateDecider = new AgroSuccessLcsUpdateDecider(makeTestCodedLcsTransitionMap(),
        successionUpdater, seedUpdater);
  }

  @After
  public void tearDown() {
    smDiscretiser = null;
    updateDecider = null;
  }

  private String errorStr(int row, int col, double value, String layerName) {
    return "row " + row + ", column " + col + " in layer " + layerName + " should be " + value
        + " but isn't.";
  }

  @Test
  public void testUniformContextAfter1Timestep() {
    Context<Object> context = addLayersToUniformContext(this.context);
    LcsUpdater lcsUpdater = new AgroSuccessLcsUpdater(context, updateDecider, smDiscretiser);

    lcsUpdater.updateLandscapeLcs();

    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        assertEquals(errorStr(i, j, Lct.Shrubland.getCode(), LscapeLayer.Lct.name()),
            Lct.Shrubland.getCode(),
            (int) context.getValueLayer(LscapeLayer.Lct.name()).get(i, j));

        // Expect pine and deciduous seeds to be present
        assertEquals(errorStr(i, j, 1, LscapeLayer.Pine.name()), 1,
            (int) context.getValueLayer(LscapeLayer.Pine.name()).get(i, j));
        assertEquals(errorStr(i, j, 1, LscapeLayer.Deciduous.name()), 1,
            (int) context.getValueLayer(LscapeLayer.Deciduous.name()).get(i, j));

        assertEquals(errorStr(i, j, 15, LscapeLayer.TimeInState.name()), 15,
            (int) context.getValueLayer(LscapeLayer.TimeInState.name()).get(i, j));

        assertEquals(errorStr(i, j, 15, LscapeLayer.DeltaT.name()), 15,
            (int) context.getValueLayer(LscapeLayer.DeltaT.name()).get(i, j));

        assertEquals(errorStr(i, j, Lct.Pine.getCode(), LscapeLayer.DeltaD.name()),
            Lct.Pine.getCode(),
            (int) context.getValueLayer(LscapeLayer.DeltaD.name()).get(i, j));
      }
    }

  }

  /**
   * Test demonstrates a land cover transition taking place.
   *
   * Because the transition is to a mature land-cover type (pine) we expect all juvenile plants
   * (pine, oak, and deciduous seeds) to have been removed.
   *
   * Also shows how -1 should be used to encode 'no target land cover state' and 'no target land
   * cover transition time'
   */
  @Test
  public void testUniformContextAfter2Timesteps() {
    Context<Object> context = addLayersToUniformContext(this.context);
    LcsUpdater lcsUpdater = new AgroSuccessLcsUpdater(context, updateDecider, smDiscretiser);

    lcsUpdater.updateLandscapeLcs();
    lcsUpdater.updateLandscapeLcs();

    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        assertEquals(errorStr(i, j, Lct.Pine.getCode(), LscapeLayer.Lct.name()), Lct.Pine.getCode(),
            (int) context.getValueLayer(LscapeLayer.Lct.name()).get(i, j));

        // Expect pine and deciduous seeds to have been removed due to transition to mature
        // land-cover type
        assertEquals(errorStr(i, j, 0, LscapeLayer.Pine.name()), 0,
            (int) context.getValueLayer(LscapeLayer.Pine.name()).get(i, j));
        assertEquals(errorStr(i, j, 0, LscapeLayer.Deciduous.name()), 0,
            (int) context.getValueLayer(LscapeLayer.Deciduous.name()).get(i, j));

        assertEquals(errorStr(i, j, 1, LscapeLayer.TimeInState.name()), 1,
            (int) context.getValueLayer(LscapeLayer.TimeInState.name()).get(i, j));

        assertEquals(errorStr(i, j, -1, LscapeLayer.DeltaT.name()), -1,
            (int) context.getValueLayer(LscapeLayer.DeltaT.name()).get(i, j));

        assertEquals(errorStr(i, j, -1, LscapeLayer.DeltaD.name()), -1,
            (int) context.getValueLayer(LscapeLayer.DeltaD.name()).get(i, j));
      }
    }
  }

  @Test
  public void testUniformContextAfter3Timesteps() {
    Context<Object> context = addLayersToUniformContext(this.context);
    LcsUpdater lcsUpdater = new AgroSuccessLcsUpdater(context, updateDecider, smDiscretiser);

    lcsUpdater.updateLandscapeLcs();
    lcsUpdater.updateLandscapeLcs();
    lcsUpdater.updateLandscapeLcs();

    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        assertEquals(errorStr(i, j, Lct.Pine.getCode(), LscapeLayer.Lct.name()),
            Lct.Pine.getCode(),
            (int) context.getValueLayer(LscapeLayer.Lct.name()).get(i, j));

        assertEquals(errorStr(i, j, 2, LscapeLayer.TimeInState.name()), 2,
            (int) context.getValueLayer(LscapeLayer.TimeInState.name()).get(i, j));

        assertEquals(errorStr(i, j, -1, LscapeLayer.DeltaT.name()), -1,
            (int) context.getValueLayer(LscapeLayer.DeltaT.name()).get(i, j));

        assertEquals(errorStr(i, j, -1, LscapeLayer.DeltaD.name()), -1,
            (int) context.getValueLayer(LscapeLayer.DeltaD.name()).get(i, j));
      }
    }
  }

  private String gvlErrorStr(IGridValueLayer expectedGvl, IGridValueLayer actualGvl) {
    return "expected:\n" + valueLayerToString(expectedGvl) + "but got:\n"
        + valueLayerToString(actualGvl);
  }

  @Test
  public void testHeteroContextAfter1TimestepLctUpdates() {
    Context<Object> context = addLayersToHeteroContext(this.context);
    LcsUpdater lcsUpdater = new AgroSuccessLcsUpdater(context, updateDecider, smDiscretiser);
    lcsUpdater.updateLandscapeLcs();

    IGridValueLayer expectedGvl = arrayToGridValueLayer(LscapeLayer.Lct.name(),
        new int[][] {
      {Lct.Pine.getCode(), Lct.Pine.getCode(), Lct.Shrubland.getCode()},
      {Lct.Shrubland.getCode(), Lct.Shrubland.getCode(), Lct.Shrubland.getCode()},
      {Lct.Shrubland.getCode(), Lct.Shrubland.getCode(), Lct.Shrubland.getCode()}});

    IGridValueLayer actualGvl = (IGridValueLayer) context.getValueLayer(LscapeLayer.Lct.name());

    assertTrue(gvlErrorStr(expectedGvl, actualGvl),
        gridValueLayersAreEqual(expectedGvl, actualGvl));
  }

  @Test
  public void testHeteroContextAfter1TimestepDeltaDUpdates() {
    Context<Object> context = addLayersToHeteroContext(this.context);
    LcsUpdater lcsUpdater = new AgroSuccessLcsUpdater(context, updateDecider, smDiscretiser);
    lcsUpdater.updateLandscapeLcs();

    IGridValueLayer expectedGvl = arrayToGridValueLayer(LscapeLayer.DeltaD.name(),
        new int[][] {
      {-1, -1, Lct.Pine.getCode()},
      {Lct.Pine.getCode(), Lct.Oak.getCode(), Lct.Oak.getCode()},
      {Lct.Oak.getCode(), Lct.Oak.getCode(), Lct.Oak.getCode()}});

    GridValueLayer actualGvl = (GridValueLayer) context.getValueLayer(LscapeLayer.DeltaD.name());

    assertTrue(gvlErrorStr(expectedGvl, actualGvl),
        gridValueLayersAreEqual(expectedGvl, actualGvl));
  }

  @Test
  public void testHeteroContextAfter1TimestepDeltaTUpdates() {
    Context<Object> context = addLayersToHeteroContext(this.context);
    LcsUpdater lcsUpdater = new AgroSuccessLcsUpdater(context, updateDecider, smDiscretiser);
    lcsUpdater.updateLandscapeLcs();

    IGridValueLayer expectedGvl = arrayToGridValueLayer(LscapeLayer.DeltaT.name(),
        new int[][] {{-1, -1, 15}, {15, 20, 20}, {20, 20, 20}});

    GridValueLayer actualGvl = (GridValueLayer) context.getValueLayer(LscapeLayer.DeltaT.name());

    assertTrue(gvlErrorStr(expectedGvl, actualGvl),
        gridValueLayersAreEqual(expectedGvl, actualGvl));
  }

  @Test
  public void testHeteroContextAfter1TimestepTimeInStateUpdates() {
    Context<Object> context = addLayersToHeteroContext(this.context);
    LcsUpdater lcsUpdater = new AgroSuccessLcsUpdater(context, updateDecider, smDiscretiser);
    lcsUpdater.updateLandscapeLcs();

    IGridValueLayer expectedGvl = arrayToGridValueLayer(LscapeLayer.TimeInState.name(),
        new int[][] {{1, 1, 15}, {15, 20, 19}, {19, 19, 19}});

    GridValueLayer actualGvl =
        (GridValueLayer) context.getValueLayer(LscapeLayer.TimeInState.name());

    assertTrue(gvlErrorStr(expectedGvl, actualGvl),
        gridValueLayersAreEqual(expectedGvl, actualGvl));
  }

  @Test
  public void testHeteroContextAfter2TimestepsLctUpdates() {
    Context<Object> context = addLayersToHeteroContext(this.context);
    LcsUpdater lcsUpdater = new AgroSuccessLcsUpdater(context, updateDecider, smDiscretiser);
    lcsUpdater.updateLandscapeLcs();
    lcsUpdater.updateLandscapeLcs();

    IGridValueLayer expectedGvl = arrayToGridValueLayer(LscapeLayer.Lct.name(),
        new int[][] {
      {Lct.Pine.getCode(), Lct.Pine.getCode(), Lct.Pine.getCode()},
      {Lct.Pine.getCode(), Lct.Oak.getCode(), Lct.Shrubland.getCode()},
      {Lct.Shrubland.getCode(), Lct.Shrubland.getCode(), Lct.Shrubland.getCode()}});

    GridValueLayer actualGvl = (GridValueLayer) context.getValueLayer(LscapeLayer.Lct.name());

    assertTrue(gvlErrorStr(expectedGvl, actualGvl),
        gridValueLayersAreEqual(expectedGvl, actualGvl));
  }

  @Test
  public void testHeteroContextAfter2TimestepsDeltaDUpdates() {
    Context<Object> context = addLayersToHeteroContext(this.context);
    LcsUpdater lcsUpdater = new AgroSuccessLcsUpdater(context, updateDecider, smDiscretiser);
    lcsUpdater.updateLandscapeLcs();
    lcsUpdater.updateLandscapeLcs();

    IGridValueLayer expectedGvl = arrayToGridValueLayer(LscapeLayer.DeltaD.name(),
        new int[][] {
      {-1, -1, -1},
      {-1, -1, Lct.Oak.getCode()},
      {Lct.Oak.getCode(), Lct.Oak.getCode(), Lct.Oak.getCode()}});

    GridValueLayer actualGvl = (GridValueLayer) context.getValueLayer(LscapeLayer.DeltaD.name());

    assertTrue(gvlErrorStr(expectedGvl, actualGvl),
        gridValueLayersAreEqual(expectedGvl, actualGvl));
  }

  @Test
  public void testHeteroContextAfter2TimestepsDeltaTUpdates() {
    Context<Object> context = addLayersToHeteroContext(this.context);
    LcsUpdater lcsUpdater = new AgroSuccessLcsUpdater(context, updateDecider, smDiscretiser);
    lcsUpdater.updateLandscapeLcs();
    lcsUpdater.updateLandscapeLcs();

    IGridValueLayer expectedGvl = arrayToGridValueLayer(LscapeLayer.DeltaT.name(),
        new int[][] {{-1, -1, -1}, {-1, -1, 20}, {20, 20, 20}});

    GridValueLayer actualGvl = (GridValueLayer) context.getValueLayer(LscapeLayer.DeltaT.name());

    assertTrue(gvlErrorStr(expectedGvl, actualGvl),
        gridValueLayersAreEqual(expectedGvl, actualGvl));
  }

  @Test
  public void testHeteroContextAfter2TimestepsTimeInStateUpdates() {
    Context<Object> context = addLayersToHeteroContext(this.context);
    LcsUpdater lcsUpdater = new AgroSuccessLcsUpdater(context, updateDecider, smDiscretiser);
    lcsUpdater.updateLandscapeLcs();
    lcsUpdater.updateLandscapeLcs();

    IGridValueLayer expectedGvl = arrayToGridValueLayer(LscapeLayer.TimeInState.name(),
        new int[][] {{2, 2, 1}, {1, 1, 20}, {20, 20, 20}});

    GridValueLayer actualGvl =
        (GridValueLayer) context.getValueLayer(LscapeLayer.TimeInState.name());

    assertTrue(gvlErrorStr(expectedGvl, actualGvl),
        gridValueLayersAreEqual(expectedGvl, actualGvl));
  }

}

