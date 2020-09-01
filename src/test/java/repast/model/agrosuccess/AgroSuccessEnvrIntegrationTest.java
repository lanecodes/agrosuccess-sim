package repast.model.agrosuccess;

import static me.ajlane.geo.repast.RepastGridUtils.gridValueLayerToArray;
import static me.ajlane.geo.repast.RepastGridUtils.hashGridValueLayerValues;
import static me.ajlane.geo.repast.RepastGridUtils.totalGridValueLayerValues;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import java.io.File;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsNot;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import me.ajlane.neo4j.EmbeddedGraphInstance;
import repast.simphony.context.Context;
import repast.simphony.context.DefaultContext;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ISchedule;
import repast.simphony.engine.schedule.Schedule;
import repast.simphony.parameter.Parameters;
import repast.simphony.parameter.ParametersParser;
import repast.simphony.scenario.ScenarioUtils;
import repast.simphony.valueLayer.GridValueLayer;

/**
 * @author Andrew Lane
 *
 */
public class AgroSuccessEnvrIntegrationTest {

  final static Logger logger = Logger.getLogger(AgroSuccessEnvrIntegrationTest.class);

  public Context<Object> context;
  public Schedule schedule;

  @BeforeClass
  public static void setUpLogging() {
    LogManager.getRootLogger().setLevel(Level.ALL);
    BasicConfigurator.configure();
  }

  @SuppressWarnings("unchecked")
  @Before
  public void setUp() throws Exception {
    String scenarioDirString = "AgroSuccess.rs";
    ScenarioUtils.setScenarioDir(new File(scenarioDirString));
    File paramsFile = new File(ScenarioUtils.getScenarioDir(), "parameters.xml");
    ParametersParser pp = new ParametersParser(paramsFile);
    Parameters params = pp.getParameters();

    // Ensure simulations run for sufficiently long for all of the tests, irrespective of
    // default value in parameters.xml
    params.setValue("nTicks", 100);

    params.setValue("useDummyData", new Boolean(true));

    schedule = new Schedule();
    RunEnvironment.init(schedule, null, params, true);

    context = new DefaultContext<Object>();
    ContextBuilder<Object> builder = new AgroSuccessContextBuilder();
    context = builder.build(context);

    // trigger the AgroSuccessContextBuilder's @ScheduledMethods to be added to the scheduler
    logger.debug("Scheduling methods...");
    for (Object agent: context.getObjects(Object.class)) {
      logger.debug(schedule.schedule(agent));
    }
    logger.debug("Finished scheduling methods");
  }

  @After
  public void tearDown() throws Exception {
    for (Object graph: context.getObjects(EmbeddedGraphInstance.class)) {
      ((GraphDatabaseService)graph).shutdown();
    }
    context = null;
    schedule = null;
  }

  @Test
  public void testAllValueLayersAccessibleFromContext() {
    assertTrue(context.getValueLayer(LscapeLayer.SoilMoisture.name()) instanceof GridValueLayer);
    assertTrue(context.getValueLayer(LscapeLayer.SoilType.name()) instanceof GridValueLayer);
    assertTrue(context.getValueLayer(LscapeLayer.Lct.name()) instanceof GridValueLayer);
    assertTrue(context.getValueLayer(LscapeLayer.Slope.name()) instanceof GridValueLayer);
    assertTrue(context.getValueLayer(LscapeLayer.Aspect.name()) instanceof GridValueLayer);
    assertTrue(context.getValueLayer(LscapeLayer.Pine.name()) instanceof GridValueLayer);
    assertTrue(context.getValueLayer(LscapeLayer.Oak.name()) instanceof GridValueLayer);
    assertTrue(context.getValueLayer(LscapeLayer.Deciduous.name()) instanceof GridValueLayer);
    assertTrue(context.getValueLayer(LscapeLayer.FlowDir.name()) instanceof GridValueLayer);
    assertTrue(context.getValueLayer(LscapeLayer.Dem.name()) instanceof GridValueLayer);
    assertTrue(context.getValueLayer(LscapeLayer.OakRegen.name()) instanceof GridValueLayer);
    assertTrue(context.getValueLayer(LscapeLayer.TimeInState.name()) instanceof GridValueLayer);
    assertTrue(context.getValueLayer(LscapeLayer.DeltaD.name()) instanceof GridValueLayer);
    assertTrue(context.getValueLayer(LscapeLayer.DeltaT.name()) instanceof GridValueLayer);
    assertTrue(context.getValueLayer(LscapeLayer.FireCount.name()) instanceof GridValueLayer);
    assertTrue(context.getValueLayer(LscapeLayer.OakAge.name()) instanceof GridValueLayer);
  }

  @Test
  public void soilMoistureValuesShouldDepartFromInitialConditions() {
    ISchedule schedule = RunEnvironment.getInstance().getCurrentSchedule();
    GridValueLayer sm = (GridValueLayer) context.getValueLayer(LscapeLayer.SoilMoisture.name());
    int startHash = hashGridValueLayerValues(sm);
    //System.out.println(RepastGridUtils.gridValueLayerToString(sm));

    for (int i=0; i<5; i++) {
      // run 5 timesteps to allow time for some spatial variation to emerge
      schedule.execute();
    }
    //System.out.println(RepastGridUtils.gridValueLayerToString(sm));
    assertNotEquals("Soil moisture values haven't changed from initial consitions, indicating "
        + "grid is not updating.", startHash, hashGridValueLayerValues(sm));
  }

  @Test
  public void seedsSouldBeDepositedOverTime() {
    // initially there are no seeds in the model, but these should be distributed by seed sources
    GridValueLayer pSeeds = (GridValueLayer) context.getValueLayer(LscapeLayer.Pine.name());
    GridValueLayer oSeeds = (GridValueLayer) context.getValueLayer(LscapeLayer.Oak.name());
    GridValueLayer dSeeds = (GridValueLayer) context.getValueLayer(LscapeLayer.Deciduous.name());

    for (int i=0; i<5; i++) {
      // run 5 timesteps to allow time for some spatial variation to emerge
      schedule.execute();
    }

    assertTrue("There are no pine seeds in model after 5 steps",
        totalGridValueLayerValues(pSeeds) > 0);
    assertTrue("There are no oak seeds in model after 5 steps",
        totalGridValueLayerValues(oSeeds) > 0);
    assertTrue("There are no deciduous seeds in model after 5 steps",
        totalGridValueLayerValues(dSeeds) > 0);
  }

  @Test
  public void landCoverStateShouldEvolveOverTime() {
    GridValueLayer lct = (GridValueLayer) context.getValueLayer(LscapeLayer.Lct.name());
    int[][] initialValues = gridValueLayerToArray(lct);

    for (int i=0; i<5; i++) {
      // run 5 timesteps to allow time for some spatial variation to emerge
      schedule.execute();
    }

    assertThat("Lct grid should evolve over time, but was unchanged after 5 time steps",
        initialValues, IsNot.not(IsEqual.equalTo(gridValueLayerToArray(lct))));
  }

  @Test
  public void oakAgeShouldEvolveOverTime() {
    GridValueLayer oakAge = (GridValueLayer) context.getValueLayer(LscapeLayer.OakAge.name());
    int[][] initialValues = gridValueLayerToArray(oakAge);

    for (int i=0; i<5; i++) {
      // run 5 timesteps to allow time for some spatial variation to emerge
      schedule.execute();
    }

    assertThat("OakAge grid should evolve over time, but was unchanged after 5 time steps",
        initialValues, IsNot.not(IsEqual.equalTo(gridValueLayerToArray(oakAge))));
  }

  @Test
  public void fireCountShouldEvolveOverTime() {
    GridValueLayer fireCount = (GridValueLayer) context.getValueLayer(LscapeLayer.FireCount.name());
    int[][] initialValues = gridValueLayerToArray(fireCount);

    for (int i=0; i<5; i++) {
      // run 5 timesteps to allow time for some spatial variation to emerge
      schedule.execute();
    }

    assertThat("FireCount grid should evolve over time, but was unchanged after 5 time steps",
        initialValues, IsNot.not(IsEqual.equalTo(gridValueLayerToArray(fireCount))));
  }

  @Test
  public void modelShouldRunSuccessfullyFor20TimeSteps() {
    for (int i=0; i<20; i++) { schedule.execute(); }
  }
}
