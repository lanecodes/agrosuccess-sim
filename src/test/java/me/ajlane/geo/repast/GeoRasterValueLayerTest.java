package me.ajlane.geo.repast;

import static org.junit.Assert.*;
import java.io.File;
import org.junit.Test;
import repast.simphony.valueLayer.GridValueLayer;

public class GeoRasterValueLayerTest {
  
  File testDataDir = new File("src/test/resources");
  
  @Test
  public void shouldBeAbleToLoadDem() {
    File testFile = new File(this.testDataDir, "hydro_correct_dummy.tif");
    GeoRasterValueLayer testGrvl = new GeoRasterValueLayer(testFile.getPath(), "test layer");
    assertEquals("test layer", testGrvl.getName());
  }
  
  @Test
  public void shouldGetExtractCorrectValuesFromDem() {
    File testFile = new File(this.testDataDir, "hydro_correct_dummy.tif");
    GeoRasterValueLayer testGrvl = new GeoRasterValueLayer(testFile.getPath(), "test layer");
    // bottom row
    assertEquals(3, testGrvl.get(0,0), 0.001);
    assertEquals(4, testGrvl.get(1,0), 0.001);
    assertEquals(4, testGrvl.get(2,0), 0.001);
    // middle row
    assertEquals(3, testGrvl.get(0,1), 0.001);
    assertEquals(3, testGrvl.get(1,1), 0.001);
    assertEquals(3, testGrvl.get(2,1), 0.001);
    // top row
    assertEquals(1, testGrvl.get(0,2), 0.001);
    assertEquals(1, testGrvl.get(1,2), 0.001);
    assertEquals(3, testGrvl.get(2,2), 0.001);    
  }
  
  @Test 
  public void shouldBeAbleToExtractGridValueLayerFromGeoTiff() {
    File testFile = new File(this.testDataDir, "hydro_correct_dummy.tif");
    GeoRasterValueLayer testGrvl = new GeoRasterValueLayer(testFile.getPath(), "test layer");
    assertTrue(testGrvl.getValueLayer() instanceof GridValueLayer);  
  }   

}
