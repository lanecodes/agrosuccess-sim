package repast.model.agrosuccess.empirical;

import static org.junit.Assert.*;
import java.io.File;
import java.io.IOException;
import org.junit.Before;
import org.junit.Test;

public class DummySiteDataTest {

  private SiteAllData siteData;

  @Before
  public void setUp() {
    siteData = new DummySiteData(new File("data/test"));
  }

  @Test
  public void testFlowDir() {
    SiteRasterData rasterData = siteData;
    assertNotNull(rasterData.getDemMap());
    assertNotNull(rasterData.getSlopeMap());
    assertNotNull(rasterData.getAspectMap());
    assertNotNull(rasterData.getFlowDirMap());
    assertNotNull(rasterData.getSoilTypeMap());
    try {
      assertNotNull(rasterData.getLctMap());
    } catch (IOException e) {
      throw new RuntimeException();
    }
  }

}
