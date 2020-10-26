package repast.model.agrosuccess;

import static me.ajlane.geo.repast.RepastGridUtils.gridValueLayerToArray;
import static org.junit.Assert.assertThat;
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
import repast.simphony.engine.schedule.Schedule;
import repast.simphony.parameter.Parameters;
import repast.simphony.parameter.ParametersParser;
import repast.simphony.scenario.ScenarioUtils;
import repast.simphony.valueLayer.GridValueLayer;

/**
 * Test land-cover state is being updated in the model, independent of the fire regime
 *
 * <p>
 * This is achieved by calling {@link AgroSuccessContextBuilder#build(Context)} as usual, but
 * setting {@code meanNumFiresPerYear} to 0, thus preventing any fires from taking place.
 * </p>
 *
 * <p>
 * For integration tests including the fire regime see {@link AgroSuccessEnvrIntegrationTest}. These
 * tests are kept separate as they have distinct setup steps.
 * </p>
 *
 * @author Andrew Lane
 */
public class AgroSuccessNoFireIntegrationTest {

  final static Logger logger = Logger.getLogger(AgroSuccessNoFireIntegrationTest.class);

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

    params.setValue("useDummyData", Boolean.TRUE);

    // Prevent fires from occurring during tests
    params.setValue("meanNumFiresPerYear", 0);

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
  public void landCoverStateShouldEvolveOverTime() {
    GridValueLayer lct = (GridValueLayer) context.getValueLayer(LscapeLayer.Lct.name());
    int[][] initialValues = gridValueLayerToArray(lct);

    for (int i = 0; i < 5; i++) {
      // run 5 timesteps to allow time for some spatial variation to emerge
      schedule.execute();
    }

    assertThat("Lct grid should evolve over time, but was unchanged after 5 time steps",
        initialValues, IsNot.not(IsEqual.equalTo(gridValueLayerToArray(lct))));
  }

}
