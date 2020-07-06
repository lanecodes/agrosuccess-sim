package me.ajlane.geo.repast.succession;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class EnvrConsequentConverterTest {

  @Test
  public void shouldConvertAliasedToCoded() {
    EnvrConsequentConverter converter =
        new EnvrConsequentConverter(new AgroSuccessEnvrStateAliasTranslator());

    AliasedEnvrConsequent testCons =
        new AliasedEnvrConsequent("Wheat", 5);
    CodedEnvrConsequent expCodedAnte = new CodedEnvrConsequent(2, 5);

    assertEquals(expCodedAnte, converter.convert(testCons));
  }

  @Test
  public void shouldConvertCodedToAliased() {
    EnvrConsequentConverter converter =
        new EnvrConsequentConverter(new AgroSuccessEnvrStateAliasTranslator());

    CodedEnvrConsequent testCons = new CodedEnvrConsequent(3, 5);
    AliasedEnvrConsequent expAliasedAnte =
        new AliasedEnvrConsequent("DAL", 5);

    assertEquals(expAliasedAnte, converter.convert(testCons));
  }

}
