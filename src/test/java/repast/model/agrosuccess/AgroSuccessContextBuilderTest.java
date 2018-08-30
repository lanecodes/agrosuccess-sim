/**
 * 
 */
package repast.model.agrosuccess;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import repast.simphony.context.Context;
import repast.simphony.context.DefaultContext;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.Schedule;
import repast.simphony.parameter.Parameters;
import repast.simphony.parameter.ParametersParser;
import repast.simphony.scenario.ScenarioUtils;

/**
 * @author andrew
 *
 */
public class AgroSuccessContextBuilderTest {
	static Parameters params;
	static Context<Object> context;

	/**
	 * @throws java.lang.Exception
	 */
	@SuppressWarnings("unchecked")
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		String scenarioDirString = "AgroSuccess.rs";
		ScenarioUtils.setScenarioDir(new File(scenarioDirString));
		File paramsFile = new File(ScenarioUtils.getScenarioDir(),
				"parameters.xml");
		ParametersParser pp = new ParametersParser(paramsFile);
		params = pp.getParameters();
		RunEnvironment.init(new Schedule(), null, params, true);
		context = new DefaultContext<Object>();
		ContextBuilder<Object> builder = new AgroSuccessContextBuilder();
		context = builder.build(context);			
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void geoDataDirShouldExist() {
		File siteGeoDataDir = new File((String)params.getValue("geoDataDirRootString"), 
				(String)params.getValue("studySite"));
		
		assertTrue(siteGeoDataDir.exists());
	}
}
