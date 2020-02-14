package repast.model.agrosuccess.empirical;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import me.ajlane.geo.Direction;
import me.ajlane.geo.repast.GeoRasterValueLayer;
import me.ajlane.geo.repast.fire.WindSpeed;
import repast.model.agrosuccess.LscapeLayer;
import repast.simphony.valueLayer.IGridValueLayer;

public class DummySiteData implements SiteAllData {

  final static Logger logger = Logger.getLogger(DummySiteData.class);

  private File testDataDir;

  public DummySiteData(File testDataDir) {
    this.testDataDir = testDataDir;
  }

  @Override
  public double getMeanMonthlyPrecipitation() {
    return 50;
  }

  @Override
  public double getTotalAnnualPrecipitation() {
    return 600;
  }

  @Override
  public double getMeanAnnualTemperature() {
    return 20;
  }

  @Override
  public String getSiteName() {
    return "DUMMY";
  }

  @Override
  public IGridValueLayer getDemMap() {
    File file = new File(this.testDataDir, "dummy_51x51_hydrocorrect_dem.tif");
    return readDummyMap(file, LscapeLayer.Dem);
  }

  @Override
  public IGridValueLayer getSlopeMap() {
    File file = new File(this.testDataDir, "dummy_51x51_slope.tif");
    return readDummyMap(file, LscapeLayer.Slope);
  }

  @Override
  public IGridValueLayer getAspectMap() {
    File file = new File(this.testDataDir, "dummy_51x51_binary_aspect.tif");
    return readDummyMap(file, LscapeLayer.Aspect);
  }

  @Override
  public IGridValueLayer getFlowDirMap() {
    File file = new File(this.testDataDir, "dummy_51x51_flowdir.tif");
    return readDummyMap(file, LscapeLayer.FlowDir);
  }

  @Override
  public IGridValueLayer getSoilTypeMap() {
    // TODO Auto-generated method stub
    return getSoilTypeMap("A");
  }

  @Override
  public IGridValueLayer getSoilTypeMap(String soilTypeLetter) {
    String name =  "dummy_51x51_soil_type_uniform_" + soilTypeLetter + ".tif";
    File file = new File(this.testDataDir, name);
    return readDummyMap(file, LscapeLayer.SoilType);
  }

  @Override
  public IGridValueLayer getLctMap(int mapNum) throws IOException {
    logger.warn("getLctMap(int) returns the same map no matter the argument");
    return getLctMap();
  }

  @Override
  public IGridValueLayer getLctMap() {
    File file = new File(this.testDataDir, "dummy_51x51_lct_oak_pine_burnt.tif");
    return readDummyMap(file, LscapeLayer.Lct);
  }

  private IGridValueLayer readDummyMap(File dummyFile, LscapeLayer layerType) {
    GeoRasterValueLayer grvl =
        new GeoRasterValueLayer(dummyFile.getAbsolutePath(), layerType.name());

    IGridValueLayer layer = grvl.getValueLayer();
    return layer;
  }

  @Override
  public int[] getGridDimensions() {
    return new int[] {51, 51};
  }

  @Override
  public double[] getGridCellPixelSize() {
    return new double[] {10, 10};
  }

  @Override
  public Map<WindSpeed, Double> getWindSpeedProb() {
    Map<WindSpeed, Double> m = new HashMap<>();
    m.put(WindSpeed.Low, 0.6);
    m.put(WindSpeed.Medium, 0.3);
    m.put(WindSpeed.High, 0.1);
    return m;
  }

  @Override
  public Map<Direction, Double> getWindDirectionProb() {
    Map<Direction, Double> m = new HashMap<>();
    m.put(Direction.N, 0.1);
    m.put(Direction.NE, 0.1);
    m.put(Direction.E, 0.1);
    m.put(Direction.SE, 0.2);
    m.put(Direction.S, 0.2);
    m.put(Direction.SW, 0.1);
    m.put(Direction.W, 0.1);
    m.put(Direction.NW, 0.1);
    return m;
  }

}
