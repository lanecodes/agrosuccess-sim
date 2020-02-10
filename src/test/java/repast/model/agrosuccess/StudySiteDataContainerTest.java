package repast.model.agrosuccess;

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
import repast.simphony.valueLayer.GridValueLayer;

public class StudySiteDataContainerTest {

  private StudySiteDataContainer dataContainer;

  @Before
  public void setUp() throws ConfigurationException {
    File dataDirRoot = new File("data/study-sites");
    dataContainer = new StudySiteDataContainer(new File(dataDirRoot, "navarres"));
  }

  @After
  public void tearDown() {
    dataContainer = null;
  }

  @Test
  public void navarresSiteNameShouldBeCorrect() {
    assertEquals("Navarrés", dataContainer.getSiteName());
  }

  @Test
  public void navarresPrecipitationShouldBe41() {
    assertEquals(41.0, dataContainer.getPrecipitation(), 0.01);
  }

  @Test
  public void navarresSlopeShouldInitialise() {
    GridValueLayer slope = dataContainer.getSlopeMap();
    assertNotNull(slope);
  }

  @Test
  public void navarresAspectShouldInitialise() {
    GridValueLayer aspect = dataContainer.getAspectMap();
    assertNotNull(aspect);
  }

  @Test
  public void navarresFlowDirectionShouldInitialise() {
    GridValueLayer flowDirection = dataContainer.getFlowDirectionMap();
    assertNotNull(flowDirection);
  }

  @Test
  public void navarresSoilTypeShouldInitialise() {
    GridValueLayer soilType = dataContainer.getSoilTypeMap();
    assertNotNull(soilType);
  }

  @Test
  public void navarresLandcoverTypeShouldInitialise() throws IOException {
    GridValueLayer landcoverType = dataContainer.getLandcoverTypeMap();
    assertNotNull(landcoverType);
  }

  @Test
  public void navarresWindSpeedShouldInitialise() {
    Map<WindSpeed, Double> windSpeedProbMap = dataContainer.getWindSpeedProb();
    assertNotNull(windSpeedProbMap);
    assertEquals(0.657654, windSpeedProbMap.get(WindSpeed.Low), 0.000001);
  }

  @Test
  public void navarresWindDirectionShouldInitialise() {
    Map<Direction, Double> windDirProbMap = dataContainer.getWindDirectionProb();
    assertNotNull(windDirProbMap);
    assertEquals(0.211618, windDirProbMap.get(Direction.E), 0.000001);
  }

}
