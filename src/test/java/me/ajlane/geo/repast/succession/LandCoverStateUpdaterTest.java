/**
 * 
 */
package me.ajlane.geo.repast.succession;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import me.ajlane.geo.DummyAspectLayer3x3;
import me.ajlane.geo.DummyLandCoverTypeLayer3x3;
import me.ajlane.geo.DummySeedLayer3x3;
import me.ajlane.geo.DummySoilMoistureLayer3x3;
import me.ajlane.geo.repast.succession.AgroSuccessEnvrStateAliasTranslator;
import me.ajlane.geo.repast.succession.EnvrStateAliasTranslator;
import me.ajlane.geo.repast.succession.LandCoverStateUpdater;
import me.ajlane.neo4j.EmbeddedGraphInstance;
import repast.simphony.context.Context;
import repast.simphony.context.DefaultContext;
import repast.simphony.valueLayer.GridValueLayer;

/**
 * @author andrew
 *
 */
public class LandCoverStateUpdaterTest {
	private static Context<Object> context = new DefaultContext<Object>();
	public LandCoverStateUpdater landCoverStateUpdater;
	private static EnvrStateAliasTranslator stateAliasTranslator;
	public static EmbeddedGraphInstance graph;
	private static String testDatabaseDirPath = "src/test/resources/databases/agrosuccess.db";
	
	static void printGridValueLayer(GridValueLayer gvl){
		for (int y=(int)gvl.getDimensions().getHeight()-1; y>=0; y--) {
			for (int x=0; x<gvl.getDimensions().getWidth(); x++) {			
				//System.out.format("x: %d, y: %d, value: %.0f\n", x, y, gvl.get(x, y));
				System.out.format("%.2f\t", (double)Math.round(gvl.get(x, y)*100)/100);
			}
			System.out.print("\n");
		}
	}	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		graph = new EmbeddedGraphInstance(testDatabaseDirPath);
		stateAliasTranslator = new AgroSuccessEnvrStateAliasTranslator();
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
	public void test() {
		int t = 0;
		//fail("Not yet implemented");
		context.addValueLayer(new DummyLandCoverTypeLayer3x3("lct", "pine forest"));
		context.addValueLayer(new DummySoilMoistureLayer3x3("soil moisture", "all hydric"));
		context.addValueLayer(new DummySeedLayer3x3("pine seeds", "all seeds"));
		context.addValueLayer(new DummySeedLayer3x3("oak seeds", "all seeds"));
		context.addValueLayer(new DummySeedLayer3x3("deciduous seeds", "all seeds"));
		context.addValueLayer(new DummyAspectLayer3x3("aspect ", "all north"));	
		
		landCoverStateUpdater = new LandCoverStateUpdater(graph, stateAliasTranslator, 
				"AgroSuccess-dev", context);
		
		while (t < 40) {
			System.out.println("t = " + t);
			printGridValueLayer((GridValueLayer)context.getValueLayer("lct"));
			landCoverStateUpdater.updateLandCoverState();
			System.out.print("\n");
			t++;			
		}		
	}

}
