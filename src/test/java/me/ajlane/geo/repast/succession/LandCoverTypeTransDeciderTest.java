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
	
	@Test 
	public void decisionTestCase1() {
		// Barley	regeneration	north	false	false	false	hydric	Shrubland	3
		int currentState = 2; // barley
		int timeInState = 2;
		int targetState = 5; // shrubland
		int targetStateTransitionTime = 3;
		LandCoverStateTransitionMessage currentLandCoverState 
			= new LandCoverStateTransitionMessage(currentState, timeInState, targetState, targetStateTransitionTime);
		
		LandCoverStateTransitionMessage nextLandCoverState 
			= lctTransDecider.nextLandCoverTransitionState(currentLandCoverState, 0, 0, 0, 0, 0, 1500); // hydric
		
		//System.out.println(nextLandCoverState);

	}
	
	/**
	 * Test that if statement 1 from Millington2009 holds
	 * If C(t) = C(t-1) THEN Tin(t) = 1
	 * 
	 * Test conditions: 
	 * start_code	succession	aspect	pine	oak	deciduous	water	end_code	delta_t
	 * Burnt	regeneration	north	true	false	false	hydric	Shrubland	2
	 * 1		0				0		1		0		0		2		5			2
	 * 
	 * timeInState = 1
	 * 
	 */
	@Test 
	public void statement1TestCase() {
		
		LandCoverStateTransitionMessage currentLandCoverState 
		= new LandCoverStateTransitionMessage(1, 1, 5, 2);
		
		LandCoverStateTransitionMessage nextLandCoverState 
		= lctTransDecider.nextLandCoverTransitionState(currentLandCoverState, 0, 0, 1, 0, 0, 1500);
		
		assertEquals(1, nextLandCoverState.getTimeInState());		
	}
	
	/**
	 * Test that if statement 2 from Millington2009 holds
	 * If C(t) = C(t-1) AND Delta D(t) = Delta D(t-1) THEN Tin(t) = Tin(t-1) + 1
	 * 
	 * Test conditions: 
	 * start_code	succession	aspect	pine	oak	deciduous	water	end_code	delta_t
	 * Wheat	regeneration	north	false	true	false	hydric	Shrubland	3
	 * 3		0				0		0		1		0		2		5			3
	 * 
	 * timeInState = 1
	 * 
	 */
	@Test 
	public void statement2TestCase() {
		
		LandCoverStateTransitionMessage currentLandCoverState 
		= new LandCoverStateTransitionMessage(3, 1, 5, 3); // initial time in state =1
		
		LandCoverStateTransitionMessage nextLandCoverState 
		= lctTransDecider.nextLandCoverTransitionState(currentLandCoverState, 0, 0, 0, 1, 0, 1500);
		
		assertEquals(2, nextLandCoverState.getTimeInState()); // new time in state is 2
	}
	
	/**
	 * Test that if statement 3 from Millington2009 holds
	 * If C(t) = C(t-1) AND Delta D(t) != Delta D(t-1) THEN Tin(t) = 1
	 * 
	 * Test conditions: 
	 * start_code	succession	aspect	pine	oak	deciduous	water	end_code	delta_t
	 * Shrubland	secondary	south	true	true	false	hydric	Pine		15
	 * 5			1			1		1		1		0		2		6			15
	 * 
	 * timeInState = 1
	 * suppose previous target state was TransForest (coded 7). Change in environmental conditions
	 * means we're now tranitioning towards pine
	 * 
	 */
	@Test 
	public void statement3TestCase() {
		LandCoverStateTransitionMessage currentLandCoverState 
			= new LandCoverStateTransitionMessage(5, 2, 7, 15); // initial time in state =1
		
		LandCoverStateTransitionMessage nextLandCoverState 
			= lctTransDecider.nextLandCoverTransitionState(currentLandCoverState, 1, 1, 1, 1, 0, 1500);
		
				
		assertEquals(1, nextLandCoverState.getTimeInState()); // new time in state is 1		
	}

}
