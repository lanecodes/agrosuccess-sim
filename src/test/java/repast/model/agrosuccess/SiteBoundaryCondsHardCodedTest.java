package repast.model.agrosuccess;

import static org.junit.Assert.*;
import java.io.File;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import repast.simphony.valueLayer.GridValueLayer;

public class SiteBoundaryCondsHardCodedTest {
  
  File testDataDir = new File("src/test/resources");
  File initialLandCoverMapFile, soilMapFile, slopeMapFile, aspectMapFile, flowDirMapFile;

  @Before
  public void setUp() {
    this.initialLandCoverMapFile = new File(testDataDir, "dummy_3x3_lct_oak_pine_burnt.tif");
    this.soilMapFile = new File(testDataDir, "dummy_3x3_soil_type_uniform_A.tif");
    this.slopeMapFile = new File(testDataDir, "dummy_3x3_slope.tif");
    this.aspectMapFile = new File(testDataDir, "dummy_3x3_binary_aspect.tif");
    this.flowDirMapFile = new File(testDataDir, "dummy_3x3_flowdir.tif");
  }

  @After
  public void tearDown() {
    this.initialLandCoverMapFile = null;
    this.soilMapFile = null;
    this.slopeMapFile = null;
    this.aspectMapFile = null;
    this.flowDirMapFile = null;
  }

  @Test
  public void shouldRetrieveExpectedValueLayers() {
    SiteBoundaryConds boundaryConds = new SiteBoundaryCondsHardCoded(50, 25.0, 
        this.initialLandCoverMapFile, this.soilMapFile, this.slopeMapFile, this.aspectMapFile, 
        this.flowDirMapFile);
    
    assertTrue(boundaryConds.getInitialLandCoverMap() instanceof GridValueLayer);
    assertTrue(boundaryConds.getSoilMap() instanceof GridValueLayer);
    assertTrue(boundaryConds.getSlopeMap() instanceof GridValueLayer);
    assertTrue(boundaryConds.getAspectMap() instanceof GridValueLayer);
    assertTrue(boundaryConds.getFlowDirMap() instanceof GridValueLayer);
  }
  
  @Test
  public void shouldRetrieveExpectedNumericalValues() {
    SiteBoundaryConds boundaryConds = new SiteBoundaryCondsHardCoded(50, 25.0, 
        this.initialLandCoverMapFile, this.soilMapFile, this.slopeMapFile, this.aspectMapFile, 
        this.flowDirMapFile);
    
    assertEquals(50, boundaryConds.getMeanAnnualPrecipitation());
    assertEquals(25.0, boundaryConds.getGridPixelSize(), 0.0001); 
  }

}
