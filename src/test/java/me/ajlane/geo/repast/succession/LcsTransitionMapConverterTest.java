package me.ajlane.geo.repast.succession;

import static org.junit.Assert.assertEquals;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class LcsTransitionMapConverterTest {

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {}

  @AfterClass
  public static void tearDownAfterClass() throws Exception {}

  @Before
  public void setUp() throws Exception {}

  @After
  public void tearDown() throws Exception {}

  @Test
  public void shouldCovertCodedToAliased() {
    LcsTransitionMapConverter converter =
        new LcsTransitionMapConverter(new AgroSuccessEnvrStateAliasTranslator());

    CodedLcsTransitionMap testCodedMap = new CodedLcsTransitionMap();
    testCodedMap.put(new CodedEnvrAntecedent(5, 0, 0, 0, 1, 1, 2), new CodedEnvrConsequent(8, 10));
    // "Pine", "regeneration", "north", "false", "true", "true", "hydric"
    // "Oak", 10

    testCodedMap.put(new CodedEnvrAntecedent(7, 0, 1, 1, 0, 0, 0), new CodedEnvrConsequent(5, 15));
    // "Deciduous", "regeneration", "south", "true", "false", "false", "xeric"
    // "Pine", 15

    AliasedLcsTransitionMap testAliasMap = converter.convert(testCodedMap);

    AliasedEnvrAntecedent inAnte1 = new AliasedEnvrAntecedent("Pine", "regeneration", "north",
        "false", "true", "true", "hydric");
    AliasedEnvrConsequent expCons1 = new AliasedEnvrConsequent("Oak", 10);

    AliasedEnvrAntecedent inAnte2 = new AliasedEnvrAntecedent("Deciduous", "regeneration", "south",
        "true", "false", "false", "xeric");
    AliasedEnvrConsequent expCons2 = new AliasedEnvrConsequent("Pine", 15);

    assertEquals(expCons1, testAliasMap.getEnvrConsequent(inAnte1));
    assertEquals(expCons2, testAliasMap.getEnvrConsequent(inAnte2));
  }

  @Test
  public void shouldCovertAliasedToCoded() {
    LcsTransitionMapConverter converter =
        new LcsTransitionMapConverter(new AgroSuccessEnvrStateAliasTranslator());

    AliasedLcsTransitionMap testAliasedMap = new AliasedLcsTransitionMap();

    AliasedEnvrAntecedent inAnte1 = new AliasedEnvrAntecedent("Pine", "regeneration", "north",
        "false", "true", "true", "hydric");
    AliasedEnvrConsequent inCons1 = new AliasedEnvrConsequent("Oak", 10);

    AliasedEnvrAntecedent inAnte2 = new AliasedEnvrAntecedent("Deciduous", "regeneration", "south",
        "true", "false", "false", "xeric");
    AliasedEnvrConsequent inCons2 = new AliasedEnvrConsequent("Pine", 15);

    testAliasedMap.put(inAnte1, inCons1);
    // "Pine", "regeneration", "north", "false", "true", "true", "hydric"
    // "Oak", 10

    testAliasedMap.put(inAnte2, inCons2);
    // "Deciduous", "regeneration", "south", "true", "false", "false", "xeric"
    // "Pine", 15

    CodedLcsTransitionMap testCodedMap = converter.convert(testAliasedMap);

    assertEquals(new CodedEnvrConsequent(8, 10),
        testCodedMap.getEnvrConsequent(new CodedEnvrAntecedent(5, 0, 0, 0, 1, 1, 2)));

    assertEquals(new CodedEnvrConsequent(5, 15),
        testCodedMap.getEnvrConsequent(new CodedEnvrAntecedent(7, 0, 1, 1, 0, 0, 0)));
  }

}
