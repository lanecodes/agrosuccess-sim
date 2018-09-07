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
	public static EmbeddedGraphInstance graph;
	private static String testDatabaseDirPath = "src/test/resources/databases/agrosuccess.db";
	
	public EnvironmentalAntecedent<String, String, String, Boolean, Boolean, Boolean, String> aliasedEnvironmentalAntecedent1;
	public EnvironmentalAntecedent<String, String, String, Boolean, Boolean, Boolean, String> aliasedEnvironmentalAntecedent2;
	public EnvironmentalAntecedent<String, String, String, Boolean, Boolean, Boolean, String> aliasedEnvironmentalAntecedent3;
	
	@BeforeClass
	public static void setUpBeforeClass() {
		graph = new EmbeddedGraphInstance(testDatabaseDirPath);
		lctTransDecider = new LandCoverTypeTransDecider(graph);
	}
	
	@Before
	public void setUp() {
		
	aliasedEnvironmentalAntecedent1 
		= new EnvironmentalAntecedent<String, String, String, Boolean, Boolean, Boolean, String>(
				"Burnt", "regeneration", "south", true, true, true, "hydric");
	
	aliasedEnvironmentalAntecedent2 
	= new EnvironmentalAntecedent<String, String, String, Boolean, Boolean, Boolean, String>(
			"Shrubland", "regeneration", "south", true, true, false, "hydric"); // TransForest	15
	
	aliasedEnvironmentalAntecedent3 
	= new EnvironmentalAntecedent<String, String, String, Boolean, Boolean, Boolean, String>(
			"Pine", "secondary", "north", true, true, true, "xeric"); //TransForest	40	
			
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		graph.shutdown();
	}

	@Test
	public void conditionsOneShouldGoToShrublandInTwoYears() {
				
		HashMap<EnvironmentalAntecedent<String, String, String, Boolean, Boolean, Boolean, String>, 
		  EnvironmentalConsequent<String>> transLookup = lctTransDecider.getTransLookup();
		
		
		
		//System.out.println(aliasedEnvironmentalAntecedent1.toString());
		
		EnvironmentalConsequent<String> envConsequent = transLookup.get(aliasedEnvironmentalAntecedent1);
		assertEquals("Shrubland", envConsequent.getTargetState());
		assertEquals(2, envConsequent.getTransitionTime());
		//System.out.println(envConsequent.toString());	
		
	}

}
