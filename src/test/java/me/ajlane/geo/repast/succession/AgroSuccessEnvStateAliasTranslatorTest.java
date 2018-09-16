/**
 * 
 */
package me.ajlane.geo.repast.succession;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author andrew
 *
 */
public class AgroSuccessEnvStateAliasTranslatorTest {

	@Test
	public void testAliasToNumber() {
		
		EnvironmentalStateAliasTranslator translator = new AgroSuccessEnvStateAliasTranslator();
		
		assertEquals(5, translator.numericalValueFromAlias("landCoverState", "Shrubland"));
		assertEquals(0, translator.numericalValueFromAlias("succession", "regeneration"));
		assertEquals(1, translator.numericalValueFromAlias("aspect", "south"));
		assertEquals(0, translator.numericalValueFromAlias("seedPresence", "false"));
		assertEquals(2, translator.numericalValueFromAlias("water", "hydric"));		
	}
	
	@Test
	public void testNumberToAlias() {
		
		EnvironmentalStateAliasTranslator translator = new AgroSuccessEnvStateAliasTranslator();
		
		assertEquals("Burnt", translator.aliasFromNumericalValue("landCoverState", 1));
		assertEquals("secondary", translator.aliasFromNumericalValue("succession", 1));
		assertEquals("north", translator.aliasFromNumericalValue("aspect", 0));
		assertEquals("true", translator.aliasFromNumericalValue("seedPresence", 1));
		assertEquals("mesic", translator.aliasFromNumericalValue("water", 1));		
	}

}
