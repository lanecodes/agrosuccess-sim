package me.ajlane.geo.repast.succession;

import static org.junit.Assert.*;

import java.util.HashMap;

import org.junit.Test;
import me.ajlane.geo.repast.succession.pathway.EnvrAntecedent;

public class EnvrAntecedentTest {

	@Test
	public void similarEnvironmentalAntecedentsShouldBeEqual() {
		EnvrAntecedent<String, String, String, Boolean, Boolean, Boolean, String> aliasedEnvironmentalAntecedent = 
				new EnvrAntecedent<String, String, String, Boolean, Boolean, Boolean, String>(
						"Burnt", "regeneration", "aspect", true, true, true, "hydric");
				
		EnvrAntecedent<String, String, String, Boolean, Boolean, Boolean, String> otherEnvironmentalAntecedent = 
				new EnvrAntecedent<String, String, String, Boolean, Boolean, Boolean, String>(
						"Burnt", "regeneration", "aspect", true, true, true, "hydric");
				
		assertTrue(aliasedEnvironmentalAntecedent.equals(otherEnvironmentalAntecedent));
	}
	
	@Test
	public void disimilarEnvironmentalAntecedentsShouldNotBeEqual() {
		EnvrAntecedent<String, String, String, Boolean, Boolean, Boolean, String> aliasedEnvironmentalAntecedent = 
				new EnvrAntecedent<String, String, String, Boolean, Boolean, Boolean, String>(
						"Burnt", "regeneration", "aspect", true, true, true, "hydric");
				
		EnvrAntecedent<String, String, String, Boolean, Boolean, Boolean, String> otherEnvironmentalAntecedent = 
				new EnvrAntecedent<String, String, String, Boolean, Boolean, Boolean, String>(
						"Burnt", "regeneration", "aspect", true, true, true, "xeric");
		
		assertFalse(aliasedEnvironmentalAntecedent.equals(otherEnvironmentalAntecedent));
	}
	
	@Test
	public void environmentalAntecedentShouldBeMatchedInHashMap() {
		HashMap<EnvrAntecedent<String, String, String, Boolean, Boolean, Boolean, String>, String> testMap
			= new  HashMap<EnvrAntecedent<String, String, String, Boolean, Boolean, Boolean, String>, String>();
		
	
		EnvrAntecedent<String, String, String, Boolean, Boolean, Boolean, String> envAntecedent1 = 
				new EnvrAntecedent<String, String, String, Boolean, Boolean, Boolean, String>(
						"Burnt", "regeneration", "aspect", true, true, true, "hydric");
		
		EnvrAntecedent<String, String, String, Boolean, Boolean, Boolean, String> envAntecedent2 = 
				new EnvrAntecedent<String, String, String, Boolean, Boolean, Boolean, String>(
						"Burnt", "regeneration", "aspect", true, true, true, "xeric");
		
		testMap.put(envAntecedent1, "one");
		testMap.put(envAntecedent2, "two");
		
		assertEquals("one", testMap.get(envAntecedent1));
		assertEquals("two", testMap.get(envAntecedent2));		
	}

}
