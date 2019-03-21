/**
 * 
 */
package repast.model.agrosuccess;

import static org.junit.Assert.*;
import java.io.File;
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

/**
 * @author Andrew Lane
 *
 */
public class AgroSuccessContextBuilderTest {
  static Parameters params;
  static Context<Object> context;

  /**
   * @throws java.lang.Exception
   */
  @SuppressWarnings("unchecked")
  @Before
  public void setUp() throws Exception {
    String scenarioDirString = "AgroSuccess.rs";
    ScenarioUtils.setScenarioDir(new File(scenarioDirString));
    File paramsFile = new File(ScenarioUtils.getScenarioDir(), "parameters.xml");
    ParametersParser pp = new ParametersParser(paramsFile);
    params = pp.getParameters();
    RunEnvironment.init(new Schedule(), null, params, true);
    context = new DefaultContext<Object>();
    ContextBuilder<Object> builder = new AgroSuccessContextBuilder();
    context = builder.build(context);
  }

  @After
  public void tearDown() throws Exception {
    for (Object graph : context.getObjects(EmbeddedGraphInstance.class)) {
      ((GraphDatabaseService) graph).shutdown();
    }
    context = null;
  }

  @Test
  public void geoDataDirShouldExist() {
    File siteGeoDataDir = new File((String) params.getValue("geoDataDirRootString"),
        (String) params.getValue("studySite"));

    assertTrue(siteGeoDataDir.exists());
  }
}
