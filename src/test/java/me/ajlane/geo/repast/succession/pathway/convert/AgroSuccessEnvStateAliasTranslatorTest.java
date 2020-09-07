/**
 *
 */
package me.ajlane.geo.repast.succession.pathway.convert;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import me.ajlane.geo.repast.succession.pathway.convert.AgroSuccessEnvrStateAliasTranslator;
import me.ajlane.geo.repast.succession.pathway.convert.EnvrStateAliasTranslator;

/**
 * @author andrew
 *
 */
public class AgroSuccessEnvStateAliasTranslatorTest {

	@Test
	public void testAliasToNumber() {

		EnvrStateAliasTranslator translator = new AgroSuccessEnvrStateAliasTranslator();

		assertEquals(4, translator.numericalValueFromAlias("landCoverState", "Shrubland"));
		assertEquals(0, translator.numericalValueFromAlias("succession", "regeneration"));
		assertEquals(1, translator.numericalValueFromAlias("aspect", "south"));
		assertEquals(0, translator.numericalValueFromAlias("seedPresence", "false"));
		assertEquals(2, translator.numericalValueFromAlias("water", "hydric"));
	}

	@Test
	public void testNumberToAlias() {

		EnvrStateAliasTranslator translator = new AgroSuccessEnvrStateAliasTranslator();

		assertEquals("Burnt", translator.aliasFromNumericalValue("landCoverState", 1));
		assertEquals("secondary", translator.aliasFromNumericalValue("succession", 1));
		assertEquals("north", translator.aliasFromNumericalValue("aspect", 0));
		assertEquals("true", translator.aliasFromNumericalValue("seedPresence", 1));
		assertEquals("mesic", translator.aliasFromNumericalValue("water", 1));
	}

}
