package me.ajlane.geo.repast.seeddispersal;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class SeedDispersalParamsTest {

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {}

  @AfterClass
  public static void tearDownAfterClass() throws Exception {}

  @Before
  public void setUp() throws Exception {}

  @After
  public void tearDown() throws Exception {}

  @Test
  public void shouldGenerateExpectedStringRepresentation() {
    SeedDispersalParams sdp = new SeedDispersalParams(3.844, 0.851, 550, 5, 75, 100);
    String expStr = "SeedDispersalParams[acornLocationParam=3.844, acornScaleParam=0.851, "
        + "acornMaxLognormalDist=550.000, windDistDecreaseParam=5.000, windMinExpDist=75.000, "
        + "windMaxExpDist=100.000]";
        
    assertEquals(expStr,  sdp.toString());   
  }

}
