package me.ajlane.geo.repast.succession;

import static org.junit.Assert.*;

import java.util.HashMap;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import me.ajlane.geo.repast.soilmoisture.AgroSuccessSoilMoistureDiscretiser;
import me.ajlane.geo.repast.soilmoisture.SoilMoistureDiscretiser;
import me.ajlane.neo4j.EmbeddedGraphInstance;

public class LandCoverTypeTransDeciderTest {
	
	public static OldLandCoverTypeTransDecider lctTransDecider;
	public static EnvrStateAliasTranslator envStateAliasTranslator  = new AgroSuccessEnvrStateAliasTranslator();
	public static EmbeddedGraphInstance graph;
	public static SoilMoistureDiscretiser smDiscretiser = new AgroSuccessSoilMoistureDiscretiser(500, 1000);
	private static String testDatabaseDirPath = "src/test/resources/databases/agrosuccess.db";
	
	public EnvrAntecedent<String, String, String, Boolean, Boolean, Boolean, String> aliasedEnvironmentalAntecedent1;
	public EnvrAntecedent<String, String, String, Boolean, Boolean, Boolean, String> aliasedEnvironmentalAntecedent2;
	public EnvrAntecedent<String, String, String, Boolean, Boolean, Boolean, String> aliasedEnvironmentalAntecedent3;
	
	public EnvrAntecedent<Integer,Integer,Integer,Integer,Integer,Integer,Integer> codedEnvironmentalAntecedent1;
	public EnvrAntecedent<Integer,Integer,Integer,Integer,Integer,Integer,Integer> codedEnvironmentalAntecedent2;
	public EnvrAntecedent<Integer,Integer,Integer,Integer,Integer,Integer,Integer> codedEnvironmentalAntecedent3;

	
	@BeforeClass
	public static void setUpBeforeClass() {
		graph = new EmbeddedGraphInstance(testDatabaseDirPath);
		
		lctTransDecider = new OldLandCoverTypeTransDecider(graph, envStateAliasTranslator, 
		    smDiscretiser, "AgroSuccess-dev");
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
		= new EnvrAntecedent<String, String, String, Boolean, Boolean, Boolean, String>(
				"Burnt", "regeneration", "south", true, true, true, "hydric");
	
	codedEnvironmentalAntecedent1 
		= new EnvrAntecedent<Integer,Integer,Integer,Integer,Integer,Integer,Integer>(
				1, 0, 1, 1, 1, 1, 2);
	
	aliasedEnvironmentalAntecedent2 
		= new EnvrAntecedent<String, String, String, Boolean, Boolean, Boolean, String>(
			"Shrubland", "regeneration", "south", true, true, false, "hydric"); // TransForest	15
	
	codedEnvironmentalAntecedent2 
		= new EnvrAntecedent<Integer,Integer,Integer,Integer,Integer,Integer,Integer>(
				5, 0, 1, 1, 1, 0, 2);
	
	aliasedEnvironmentalAntecedent3 
		= new EnvrAntecedent<String, String, String, Boolean, Boolean, Boolean, String>(
			"Pine", "secondary", "north", true, true, true, "xeric"); //TransForest	40	
	
	codedEnvironmentalAntecedent3 
		= new EnvrAntecedent<Integer,Integer,Integer,Integer,Integer,Integer,Integer>(
				6, 1, 0, 1, 1, 1, 0);
			
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		graph.shutdown();
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
		
		OldLandCoverStateTransitionMessage currentLandCoverState 
		= new OldLandCoverStateTransitionMessage(1, 1, 5, 2);
		
		OldLandCoverStateTransitionMessage nextLandCoverState 
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
		
		OldLandCoverStateTransitionMessage currentLandCoverState 
		= new OldLandCoverStateTransitionMessage(3, 1, 5, 3); // initial time in state =1
		
		OldLandCoverStateTransitionMessage nextLandCoverState 
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
		OldLandCoverStateTransitionMessage currentLandCoverState 
			= new OldLandCoverStateTransitionMessage(5, 2, 7, 15); // initial time in state =1
		
		OldLandCoverStateTransitionMessage nextLandCoverState 
			= lctTransDecider.nextLandCoverTransitionState(currentLandCoverState, 1, 1, 1, 1, 0, 1500);
		
				
		assertEquals(1, nextLandCoverState.getTimeInState()); // new time in state is 1		
	}
	
	/**
	 * Test that if statement 4 from Millington2009 holds
	 * If Delta D(t) != Delta D(t-1) C(t) = C(t-1) AND Delta D()t) != Delta D(t-1) THEN Delta T = [DeltaT(t-1)+Delta T(t)]/2
	 * 
	 * Test conditions: 
	 * start_code	succession	aspect	pine	oak	deciduous	water	end_code	delta_t
	 * Shrubland	secondary	south	true	true	false	hydric	Pine		15
	 * 5			1			1		1		1		0		2		6			15
	 * 
	 * timeInState = 1
	 * suppose previous target state was Oak (coded 9) and it was going to take 20 years to get there. 
	 * Now transitioning from shrubland (coded 5) to to Pine and it should take (15+20)/2 = 17.5 -> 18 years.
	 * 
	 */
	@Test 
	public void statement4TestCase() {
		OldLandCoverStateTransitionMessage currentLandCoverState 
			= new OldLandCoverStateTransitionMessage(5, 2, 9, 20); // initial time in state =1
		
		OldLandCoverStateTransitionMessage nextLandCoverState 
			= lctTransDecider.nextLandCoverTransitionState(currentLandCoverState, 1, 1, 1, 1, 0, 1500);
		
				
		assertEquals(18, nextLandCoverState.getTargetStateTransitionTime()); // new time in state is 1		
	}
	
	/**
	 * Test that if statement 5 from Millington2009 holds
	 * If Tin(t) >= Delta T(t) THEN C(t+1) = Delta D
	 * 
	 * Test conditions: 
	 * start_code	succession	aspect	pine	oak	deciduous	water	end_code	delta_t
	 * Shrubland	secondary	south	true	true	false	hydric	Pine		15
	 * 5			1			1		1		1		0		2		6			15
	 * 
	 * timeInState = 14
	 * Previous target state was Pine (coded 6) and it was going to take 15 years to get there. 
	 * Current state of model has timeInState being 14 so the next increment will take us us up to 
	 * the 15 year tipping point. 
	 * 
	 * Hence we expect to transition to pine (state 6) in the next timestep
	 * 
	 */
	@Test 
	public void statement5TestCase() {
		OldLandCoverStateTransitionMessage currentLandCoverState 
			= new OldLandCoverStateTransitionMessage(5, 14, 6, 15); // initial state = 5
		
		OldLandCoverStateTransitionMessage nextLandCoverState 
			= lctTransDecider.nextLandCoverTransitionState(currentLandCoverState, 1, 1, 1, 1, 0, 1500);
		
				
		assertEquals(6, nextLandCoverState.getCurrentState()); // new state = 6		
	}
	
	/**
	 * Test that if statement 6 from Millington2009 holds
	 * If Tin(t) < Delta T(t) THEN C(t+1) = C(t)
	 * 
	 * Test conditions: 
	 * start_code	succession	aspect	pine	oak	deciduous	water	end_code	delta_t
	 * Shrubland	secondary	south	true	true	false	hydric	Pine		15
	 * 5			1			1		1		1		0		2		6			15
	 * 
	 * timeInState = 13
	 * Previous target state was Pine (coded 6) and it was going to take 15 years to get there. 
	 * Current state of model has timeInState being 13 so the next increment WON'T take us us up to 
	 * the 15 year tipping point. 
	 * 
	 * Hence we expect to transition to still be shrubland (state 5) in the next timestep
	 * 
	 */
	@Test 
	public void statement6TestCase() {
		OldLandCoverStateTransitionMessage currentLandCoverState 
			= new OldLandCoverStateTransitionMessage(5, 13, 6, 15); // initial state = 5
		
		OldLandCoverStateTransitionMessage nextLandCoverState 
			= lctTransDecider.nextLandCoverTransitionState(currentLandCoverState, 1, 1, 1, 1, 0, 1500);
		
				
		assertEquals(5, nextLandCoverState.getCurrentState()); // new state = 5		
	}


}
