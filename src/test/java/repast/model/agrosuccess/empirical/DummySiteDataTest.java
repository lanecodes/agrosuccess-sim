package repast.model.agrosuccess.empirical;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import java.io.File;
import java.io.IOException;
import org.junit.Before;
import org.junit.Test;
import repast.model.agrosuccess.AgroSuccessCodeAliases.Lct;
import repast.simphony.valueLayer.IGridValueLayer;

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

  @Test
  public void nullLctMapShouldLoad() {
    SiteRasterData rasterData = siteData;
    int[] gridDims = {51, 51};
    int[] origin = {0, 0};
    IGridValueLayer nullLctMap = rasterData.getNullLctMap(gridDims, origin);

    int width = (int) nullLctMap.getDimensions().getWidth();
    int height = (int) nullLctMap.getDimensions().getHeight();

    assertEquals(51, width);
    assertEquals(51, height);
    assertEquals("Lct", nullLctMap.getName());

    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        assertEquals(Lct.Burnt.getCode(), (int) nullLctMap.get(x, y));
      }
    }
  }

}
