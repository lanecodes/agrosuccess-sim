package me.ajlane.geo.repast.succession;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class EnvrAntecedentConverterTest {

  @Test
  public void shouldConvertAliasedToCoded() {
    EnvrAntecedentConverter converter =
        new EnvrAntecedentConverter(new AgroSuccessEnvrStateAliasTranslator());

    AliasedEnvrAntecedent testAnte =
        new AliasedEnvrAntecedent("Wheat", "secondary", "north", "true", "false", "true", "mesic");
    CodedEnvrAntecedent expCodedAnte = new CodedEnvrAntecedent(2, 1, 0, 1, 0, 1, 1);

    assertEquals(expCodedAnte, converter.convert(testAnte));
  }

  @Test
  public void shouldConvertCodedToAliased() {
    EnvrAntecedentConverter converter =
        new EnvrAntecedentConverter(new AgroSuccessEnvrStateAliasTranslator());

    CodedEnvrAntecedent testAnte = new CodedEnvrAntecedent(4, 0, 1, 0, 0, 1, 2);
    AliasedEnvrAntecedent expAliasedAnte =
        new AliasedEnvrAntecedent("Shrubland", "regeneration", "south", "false", "false", "true", "hydric");

    assertEquals(expAliasedAnte, converter.convert(testAnte));
  }

}
