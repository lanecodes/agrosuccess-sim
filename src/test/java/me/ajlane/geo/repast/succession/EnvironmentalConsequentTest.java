package me.ajlane.geo.repast.succession;

import static org.junit.Assert.*;

import org.junit.Test;

public class EnvironmentalConsequentTest {
	
		@Test
		public void similarEnvironmentalConsequentShouldBeEqual() {
			EnvironmentalConsequent<String> aliasedEnvironmentalConsequent = 
					new EnvironmentalConsequent<String>("Pine", 10);
			
			EnvironmentalConsequent<String> otherEnvironmentalConsequent = 
					new EnvironmentalConsequent<String>("Pine", 10);
			
					
			assertTrue(aliasedEnvironmentalConsequent.equals(otherEnvironmentalConsequent));
		}
		
		@Test
		public void disimilarEnvironmentalAntecedentsShouldNotBeEqual() {
			EnvironmentalConsequent<String> aliasedEnvironmentalConsequent = 
					new EnvironmentalConsequent<String>("Pine", 10);
			
			EnvironmentalConsequent<String> otherEnvironmentalConsequent1 = 
					new EnvironmentalConsequent<String>("Oak", 10);
			
			EnvironmentalConsequent<String> otherEnvironmentalConsequent2 = 
					new EnvironmentalConsequent<String>("Pine", 15);
			
					
			assertFalse(aliasedEnvironmentalConsequent.equals(otherEnvironmentalConsequent1));
			assertFalse(aliasedEnvironmentalConsequent.equals(otherEnvironmentalConsequent2));
		}

}
