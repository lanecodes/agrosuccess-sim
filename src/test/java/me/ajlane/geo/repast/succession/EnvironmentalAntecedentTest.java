package me.ajlane.geo.repast.succession;

import static org.junit.Assert.*;

import java.util.HashMap;

import org.junit.Test;

public class EnvironmentalAntecedentTest {

	@Test
	public void similarEnvironmentalAntecedentsShouldBeEqual() {
		EnvironmentalAntecedent<String, String, String, Boolean, Boolean, Boolean, String> aliasedEnvironmentalAntecedent = 
				new EnvironmentalAntecedent<String, String, String, Boolean, Boolean, Boolean, String>(
						"Burnt", "regeneration", "aspect", true, true, true, "hydric");
				
		EnvironmentalAntecedent<String, String, String, Boolean, Boolean, Boolean, String> otherEnvironmentalAntecedent = 
				new EnvironmentalAntecedent<String, String, String, Boolean, Boolean, Boolean, String>(
						"Burnt", "regeneration", "aspect", true, true, true, "hydric");
				
		assertTrue(aliasedEnvironmentalAntecedent.equals(otherEnvironmentalAntecedent));
	}
	
	@Test
	public void disimilarEnvironmentalAntecedentsShouldNotBeEqual() {
		EnvironmentalAntecedent<String, String, String, Boolean, Boolean, Boolean, String> aliasedEnvironmentalAntecedent = 
				new EnvironmentalAntecedent<String, String, String, Boolean, Boolean, Boolean, String>(
						"Burnt", "regeneration", "aspect", true, true, true, "hydric");
				
		EnvironmentalAntecedent<String, String, String, Boolean, Boolean, Boolean, String> otherEnvironmentalAntecedent = 
				new EnvironmentalAntecedent<String, String, String, Boolean, Boolean, Boolean, String>(
						"Burnt", "regeneration", "aspect", true, true, true, "xeric");
		
		assertFalse(aliasedEnvironmentalAntecedent.equals(otherEnvironmentalAntecedent));
	}
	
	@Test
	public void environmentalAntecedentShouldBeMatchedInHashMap() {
		HashMap<EnvironmentalAntecedent<String, String, String, Boolean, Boolean, Boolean, String>, String> testMap
			= new  HashMap<EnvironmentalAntecedent<String, String, String, Boolean, Boolean, Boolean, String>, String>();
		
	
		EnvironmentalAntecedent<String, String, String, Boolean, Boolean, Boolean, String> envAntecedent1 = 
				new EnvironmentalAntecedent<String, String, String, Boolean, Boolean, Boolean, String>(
						"Burnt", "regeneration", "aspect", true, true, true, "hydric");
		
		EnvironmentalAntecedent<String, String, String, Boolean, Boolean, Boolean, String> envAntecedent2 = 
				new EnvironmentalAntecedent<String, String, String, Boolean, Boolean, Boolean, String>(
						"Burnt", "regeneration", "aspect", true, true, true, "xeric");
		
		testMap.put(envAntecedent1, "one");
		testMap.put(envAntecedent2, "two");
		
		assertEquals("one", testMap.get(envAntecedent1));
		assertEquals("two", testMap.get(envAntecedent2));		
	}

}
