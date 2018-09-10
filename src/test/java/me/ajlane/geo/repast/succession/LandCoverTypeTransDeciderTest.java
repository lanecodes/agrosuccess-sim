package me.ajlane.geo.repast.succession;

import static org.junit.Assert.*;

import java.util.HashMap;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import me.ajlane.neo4j.EmbeddedGraphInstance;

public class LandCoverTypeTransDeciderTest {
	
	public static LandCoverTypeTransDecider lctTransDecider;
	public static EnvironmentalStateAliasTranslator envStateAliasTranslator  = new AgroSuccessEnvStateAliasTranslator();
	public static EmbeddedGraphInstance graph;
	private static String testDatabaseDirPath = "src/test/resources/databases/agrosuccess.db";
	
	public EnvironmentalAntecedent<String, String, String, Boolean, Boolean, Boolean, String> aliasedEnvironmentalAntecedent1;
	public EnvironmentalAntecedent<String, String, String, Boolean, Boolean, Boolean, String> aliasedEnvironmentalAntecedent2;
	public EnvironmentalAntecedent<String, String, String, Boolean, Boolean, Boolean, String> aliasedEnvironmentalAntecedent3;
	
	public EnvironmentalAntecedent<Integer,Integer,Integer,Integer,Integer,Integer,Integer> codedEnvironmentalAntecedent1;
	public EnvironmentalAntecedent<Integer,Integer,Integer,Integer,Integer,Integer,Integer> codedEnvironmentalAntecedent2;
	public EnvironmentalAntecedent<Integer,Integer,Integer,Integer,Integer,Integer,Integer> codedEnvironmentalAntecedent3;

	
	@BeforeClass
	public static void setUpBeforeClass() {
		graph = new EmbeddedGraphInstance(testDatabaseDirPath);
		lctTransDecider = new LandCoverTypeTransDecider(graph, envStateAliasTranslator, "AgroSuccess-dev");
	}
	
	/**	 		0 = Water/ Quarry
	 * 			1 = Burnt
	 * 			2 = Barley
	 *			3 = Wheat
	 *			4 = Depleted agricultural land
	 *			5 = Shrubland
	 *			6 = Pine forest
	 *			7 = Transition forest
	 *			8 = Deciduous forest
	 *			9 = Oak forest
	 **/
	@Before
	public void setUp() {
		
	aliasedEnvironmentalAntecedent1 
		= new EnvironmentalAntecedent<String, String, String, Boolean, Boolean, Boolean, String>(
				"Burnt", "regeneration", "south", true, true, true, "hydric");
	
	codedEnvironmentalAntecedent1 
		= new EnvironmentalAntecedent<Integer,Integer,Integer,Integer,Integer,Integer,Integer>(
				1, 0, 1, 1, 1, 1, 2);
	
	aliasedEnvironmentalAntecedent2 
		= new EnvironmentalAntecedent<String, String, String, Boolean, Boolean, Boolean, String>(
			"Shrubland", "regeneration", "south", true, true, false, "hydric"); // TransForest	15
	
	codedEnvironmentalAntecedent2 
		= new EnvironmentalAntecedent<Integer,Integer,Integer,Integer,Integer,Integer,Integer>(
				5, 0, 1, 1, 1, 0, 2);
	
	aliasedEnvironmentalAntecedent3 
		= new EnvironmentalAntecedent<String, String, String, Boolean, Boolean, Boolean, String>(
			"Pine", "secondary", "north", true, true, true, "xeric"); //TransForest	40	
	
	codedEnvironmentalAntecedent3 
		= new EnvironmentalAntecedent<Integer,Integer,Integer,Integer,Integer,Integer,Integer>(
				6, 1, 0, 1, 1, 1, 0);
			
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		graph.shutdown();
	}

	@Test
	public void conditionsOneShouldGoToShrublandInTwoYears() {
				
		HashMap<EnvironmentalAntecedent<Integer,Integer,Integer,Integer,Integer,Integer,Integer>, 
		  EnvironmentalConsequent<Integer>> transLookup = lctTransDecider.getTransLookup();	
		
		EnvironmentalConsequent<Integer> envConsequent = transLookup.get(codedEnvironmentalAntecedent1);
		assertEquals(5, (int)envConsequent.getTargetState()); // shrubland
		assertEquals(2, envConsequent.getTransitionTime());
		
	}

}
