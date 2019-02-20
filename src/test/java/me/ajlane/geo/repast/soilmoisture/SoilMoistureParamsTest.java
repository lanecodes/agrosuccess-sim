package me.ajlane.geo.repast.soilmoisture;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class SoilMoistureParamsTest {

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {}

  @AfterClass
  public static void tearDownAfterClass() throws Exception {}

  @Before
  public void setUp() throws Exception {}

  @After
  public void tearDown() throws Exception {}

  @Test
  public void testToStringYieldsExpectedResult() {
    SoilMoistureParams smp = new SoilMoistureParams(500, 1000); 
    String expStr = "SoilMoistureParams [mesicThreshold=500, hydricThreshold=1000]";
    assertEquals(expStr, smp.toString());
  }

}
