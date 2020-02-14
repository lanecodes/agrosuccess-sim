package repast.model.agrosuccess.empirical;

import static org.junit.Assert.*;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import org.apache.commons.configuration.ConfigurationException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import me.ajlane.geo.Direction;
import me.ajlane.geo.repast.fire.WindSpeed;
import repast.model.agrosuccess.empirical.SiteDataLoader;
import repast.simphony.valueLayer.IGridValueLayer;

public class SiteDataLoaderTest {

  private SiteDataLoader dataLoader;

  @Before
  public void setUp() throws ConfigurationException {
    File dataDirRoot = new File("data/study-sites");
    dataLoader = new SiteDataLoader(new File(dataDirRoot, "navarres"));
  }

  @After
  public void tearDown() {
    dataLoader = null;
  }

  @Test
  public void navarresSiteNameShouldBeCorrect() {
    assertEquals("Navarrés", dataLoader.getSiteName());
  }

  @Test
  public void navarresMonthlyPrecipitationShouldBe41() {
    assertEquals(41.0, dataLoader.getMeanMonthlyPrecipitation(), 0.01);
  }

  @Test
  public void navarresAnnualPrecipitationShouldBe496() {
    assertEquals(496.0, dataLoader.getTotalAnnualPrecipitation(), 0.01);
  }

  @Test
  public void navarresMeanAnnualTemberaturShouldBe15point9() {
    assertEquals(15.9, dataLoader.getMeanAnnualTemperature(), 0.01);
  }

  @Test
  public void navarresDemShouldInitialise() {
    IGridValueLayer dem = dataLoader.getDemMap();
    assertNotNull(dem);
  }

  @Test
  public void navarresSlopeShouldInitialise() {
    IGridValueLayer slope = dataLoader.getSlopeMap();
    assertNotNull(slope);
  }

  @Test
  public void navarresAspectShouldInitialise() {
    IGridValueLayer aspect = dataLoader.getAspectMap();
    assertNotNull(aspect);
  }

  @Test
  public void navarresFlowDirectionShouldInitialise() {
    IGridValueLayer flowDirection = dataLoader.getFlowDirMap();
    assertNotNull(flowDirection);
  }

  @Test
  public void navarresSoilTypeShouldInitialise() {
    IGridValueLayer soilType = dataLoader.getSoilTypeMap();
    assertNotNull(soilType);
  }

  @Test
  public void navarresLandcoverTypeShouldInitialise() throws IOException {
    IGridValueLayer landcoverType = dataLoader.getLctMap();
    assertNotNull(landcoverType);
  }

  @Test
  public void navarresShouldLoadAllRasterMaps() throws IOException {
    dataLoader.getDemMap();
    dataLoader.getSlopeMap();
    dataLoader.getAspectMap();
    dataLoader.getFlowDirMap();
    dataLoader.getSoilTypeMap();
    dataLoader.getLctMap();
  }

  @Test
  public void navarresWindSpeedShouldInitialise() {
    Map<WindSpeed, Double> windSpeedProbMap = dataLoader.getWindSpeedProb();
    assertNotNull(windSpeedProbMap);
    assertEquals(0.657654, windSpeedProbMap.get(WindSpeed.Low), 0.000001);
  }

  @Test
  public void navarresWindDirectionShouldInitialise() {
    Map<Direction, Double> windDirProbMap = dataLoader.getWindDirectionProb();
    assertNotNull(windDirProbMap);
    assertEquals(0.211618, windDirProbMap.get(Direction.E), 0.000001);
  }

}
