package repast.model.agrosuccess;

import java.io.File;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import me.ajlane.neo4j.EmbeddedGraphInstance;
import repast.simphony.context.Context;
import repast.simphony.context.DefaultContext;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.Schedule;
import repast.simphony.parameter.Parameters;
import repast.simphony.parameter.ParametersParser;
import repast.simphony.scenario.ScenarioUtils;
import repast.simphony.valueLayer.GridValueLayer;

public class AgroSuccessLctUpdateIntegrationTest {


  final static Logger logger = Logger.getLogger(AgroSuccessLctUpdateIntegrationTest.class);

  public Context<Object> context;
  public Schedule schedule;

//  @BeforeClass
//  public static void setUpLogging() {
//    LogManager.getRootLogger().setLevel(Level.ALL);
//    BasicConfigurator.configure();
//  }

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

    // params.setValue("useDummyData", new Boolean(false));
    params.setValue("useDummyData", Boolean.TRUE);

    params.setValue("meanNumFiresPerYear", 10);

    schedule = new Schedule();
    RunEnvironment.init(schedule, null, params, true);

    context = new DefaultContext<Object>();
    ContextBuilder<Object> builder = new AgroSuccessContextBuilder();
    context = builder.build(context);

    // trigger the AgroSuccessContextBuilder's @ScheduledMethods to be added to the scheduler
    logger.debug("Scheduling methods...");
    for (Object agent : context.getObjects(Object.class)) {
      logger.debug("Scheduling " + agent);
      schedule.schedule(agent);
    }
    logger.debug("Finished scheduling methods");
  }

  @After
  public void tearDown() throws Exception {
    for (Object graph : context.getObjects(EmbeddedGraphInstance.class)) {
      ((GraphDatabaseService) graph).shutdown();
    }
    context = null;
    schedule = null;
  }

  @Test
  public void testAllCellsHaveTargetStateAfterFirstTick() {
    GridValueLayer deltaD = (GridValueLayer) context.getValueLayer(LscapeLayer.DeltaD.name());
    GridValueLayer deltaT = (GridValueLayer) context.getValueLayer(LscapeLayer.DeltaT.name());

    logger.debug(deltaT.getDimensions().getWidth() * deltaT.getDimensions().getHeight()
        + " cells in the simulation");

    for (int t = 0; t < 20; t++) {
      schedule.execute();
    }

    for (int x = 0; x < deltaD.getDimensions().getWidth(); x++) {
      for (int y = 0; y < deltaD.getDimensions().getHeight(); y++) {
        // TODO
//        assertNotEquals(-1, (int) deltaD.get(x, y));
//        assertNotEquals(-1, (int) deltaT.get(x, y));
      }
    }

  }

}
