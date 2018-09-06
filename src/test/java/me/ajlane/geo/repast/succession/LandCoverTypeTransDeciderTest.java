package me.ajlane.geo.repast.succession;

import static org.junit.Assert.*;

import java.util.HashMap;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import me.ajlane.neo4j.EmbeddedGraphInstance;

public class LandCoverTypeTransDeciderTest {
	
	public static EmbeddedGraphInstance graph;
	private static String testDatabaseDirPath = "src/test/resources/databases/agrosuccess.db";
	
	@BeforeClass
	public static void setUpBeforeClass() {
		graph = new EmbeddedGraphInstance(testDatabaseDirPath);
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		graph.shutdown();
	}

	@Test
	public void test() {
		LandCoverTypeTransDecider lctTransDecider = new LandCoverTypeTransDecider(graph);
		
		HashMap<EnvironmentalAntecedent<String, String, String, Boolean, Boolean, Boolean, String>, 
		  EnvironmentalConsequent<String>> transLookup = lctTransDecider.getTransLookup();
		
		EnvironmentalAntecedent<String, String, String, Boolean, Boolean, Boolean, String> aliasedEnvironmentalAntecedent = 
				new EnvironmentalAntecedent<String, String, String, Boolean, Boolean, Boolean, String>(
						"Burnt", "regeneration", "south", true, true, true, "hydric");
		
		System.out.println(aliasedEnvironmentalAntecedent.toString());
		
		EnvironmentalConsequent<String> envConsequent = transLookup.get(aliasedEnvironmentalAntecedent);
		System.out.println(envConsequent.toString());	
		
	}

}
