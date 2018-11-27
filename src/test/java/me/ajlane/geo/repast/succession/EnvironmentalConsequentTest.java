package me.ajlane.geo.repast.succession;

import static org.junit.Assert.*;

import org.junit.Test;

public class EnvironmentalConsequentTest {
	
		@Test
		public void similarEnvironmentalConsequentShouldBeEqual() {
			EnvrConsequent<String> aliasedEnvironmentalConsequent = 
					new EnvrConsequent<String>("Pine", 10);
			
			EnvrConsequent<String> otherEnvironmentalConsequent = 
					new EnvrConsequent<String>("Pine", 10);
			
					
			assertTrue(aliasedEnvironmentalConsequent.equals(otherEnvironmentalConsequent));
		}
		
		@Test
		public void disimilarEnvironmentalAntecedentsShouldNotBeEqual() {
			EnvrConsequent<String> aliasedEnvironmentalConsequent = 
					new EnvrConsequent<String>("Pine", 10);
			
			EnvrConsequent<String> otherEnvironmentalConsequent1 = 
					new EnvrConsequent<String>("Oak", 10);
			
			EnvrConsequent<String> otherEnvironmentalConsequent2 = 
					new EnvrConsequent<String>("Pine", 15);
			
					
			assertFalse(aliasedEnvironmentalConsequent.equals(otherEnvironmentalConsequent1));
			assertFalse(aliasedEnvironmentalConsequent.equals(otherEnvironmentalConsequent2));
		}

}
