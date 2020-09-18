/**
 *
 */
package repast.model.agrosuccess.empirical;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.SubnodeConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.log4j.Logger;
import me.ajlane.geo.Direction;
import me.ajlane.geo.repast.GeoRasterValueLayer;
import me.ajlane.geo.repast.fire.WindSpeed;
import repast.model.agrosuccess.AgroSuccessCodeAliases.Lct;
import repast.model.agrosuccess.LscapeLayer;
import repast.simphony.space.Dimensions;
import repast.simphony.space.grid.StrictBorders;
import repast.simphony.valueLayer.GridValueLayer;
import repast.simphony.valueLayer.IGridValueLayer;
import repast.simphony.valueLayer.ValueLayer;

/**
 * Contains logic to load study site specific data and parameters.
 *
 * @author Andrew Lane
 *
 */
public class SiteDataLoader implements SiteAllData {

  final static Logger logger = Logger.getLogger(SiteDataLoader.class);

  File siteDataDir;
  private HierarchicalConfiguration siteConfig;

  // Used to check all rasters read by this class have the same dimensions
  private Dimensions prevRasterDimensions = null;

  /**
   * @param siteDataDir The directory in which the data required by the simulation for a
   *        <emph>specific</emph> study site resides. In particular the following files should be
   *        present in this directory:
   *        <ul>
   *        <li>site_parameters.xml</li>
   *        <li>slope.tif</li>
   *        <li>binary_aspect.tif</li>
   *        <li>flow_dir</li>
   *        <li>uniform_soil_map_{A, B, C, D}.tif</li>
   *        <li>init_lct_maps.zip</li>
   *        </ul>
   *
   * @throws ConfigurationException
   */
  public SiteDataLoader(File siteDataDir) throws ConfigurationException {
    this.siteDataDir = siteDataDir;
    File configFile = new File(siteDataDir, "site_parameters.xml");
    try {
      this.siteConfig = new XMLConfiguration(configFile);
    } catch (ConfigurationException e) {
      logger.error("Check XML configuration " + configFile.toString(), e);
      throw e;
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getSiteName() {
    return this.siteConfig.getString("siteName");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IGridValueLayer getDemMap() {
    File file = new File(this.siteDataDir, "hydrocorrect_dem.tif");
    GeoRasterValueLayer grvl =
        new GeoRasterValueLayer(file.getAbsolutePath(), LscapeLayer.Dem.name());

    IGridValueLayer layer = grvl.getValueLayer();
    checkDimensionsConsistentWithPrevious(layer);
    return layer;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IGridValueLayer getSlopeMap() {
    File file = new File(this.siteDataDir, "slope.tif");
    GeoRasterValueLayer grvl =
        new GeoRasterValueLayer(file.getAbsolutePath(), LscapeLayer.Slope.name());

    IGridValueLayer layer = grvl.getValueLayer();
    checkDimensionsConsistentWithPrevious(layer);
    return layer;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IGridValueLayer getAspectMap() {
    File file = new File(this.siteDataDir, "binary_aspect.tif");
    GeoRasterValueLayer grvl =
        new GeoRasterValueLayer(file.getAbsolutePath(), LscapeLayer.Aspect.name());

    IGridValueLayer layer = grvl.getValueLayer();
    checkDimensionsConsistentWithPrevious(layer);
    return layer;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IGridValueLayer getFlowDirMap() {
    File file = new File(this.siteDataDir, "flow_dir.tif");
    GeoRasterValueLayer grvl =
        new GeoRasterValueLayer(file.getAbsolutePath(), LscapeLayer.FlowDir.name());

    IGridValueLayer layer = grvl.getValueLayer();
    checkDimensionsConsistentWithPrevious(layer);
    return layer;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IGridValueLayer getSoilTypeMap() {
    return getSoilTypeMap("A");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IGridValueLayer getSoilTypeMap(String soilTypeLetter) {
    File file = new File(this.siteDataDir, "uniform_soil_map_" + soilTypeLetter + ".tif");
    GeoRasterValueLayer grvl =
        new GeoRasterValueLayer(file.getAbsolutePath(), LscapeLayer.SoilType.name());

    IGridValueLayer layer = grvl.getValueLayer();
    checkDimensionsConsistentWithPrevious(layer);
    return layer;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IGridValueLayer getLctMap() throws IOException {
    return getLctMap(0);
  }

  @Override
  public IGridValueLayer getNullLctMap(int[] gridDimensions, int[] gridOrigin) {
    IGridValueLayer uniformBurntLayer = new GridValueLayer(
        LscapeLayer.Lct.name(), Lct.Burnt.getCode(), true,
        new StrictBorders(), gridDimensions, gridOrigin);
    checkDimensionsConsistentWithPrevious(uniformBurntLayer);
    return uniformBurntLayer;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IGridValueLayer getLctMap(int mapNum) throws IOException {
    File file = extractInitialLandCoverMap(new File(this.siteDataDir, "init_lct_maps.zip"), mapNum);
    GeoRasterValueLayer grvl =
        new GeoRasterValueLayer(file.getAbsolutePath(), LscapeLayer.Lct.name());
    file.delete(); // Delete file LCT map temporarily extracted from the archive

    logger.info("Loaded NLM number " + mapNum);

    IGridValueLayer layer = grvl.getValueLayer();
    checkDimensionsConsistentWithPrevious(layer);
    return layer;
  }

  /**
   * Given a zip archive containing initial land cover maps, extract map number {@code mapNum} to a
   * temporary directory. Return a {@code File} representing the extracted GeoTiff file.
   *
   * @param zipFilePath Path to the zip file containing initial land cover maps.
   * @param mapNum The number of the initial land cover map to extract from the zip file.
   * @return Path to the extracted file, in a temporary directory.
   * @throws IOException
   */
  private static File extractInitialLandCoverMap(File zipFilePath, int mapNum) throws IOException {
    String lctFileName = "init-landcover" + Integer.valueOf(mapNum).toString() + ".tif";
    File outputLocation = extractInitialLandCoverMap(zipFilePath, lctFileName);
    return outputLocation;
  }

  /**
   * Given a zip archive containing initial land cover maps, extract the file named
   * {@code lctFileName} to a temporary directory. Return a {@code File} representing the extracted
   * GeoTiff file.
   *
   * Takes inspiration from
   * <a href= "https://stackoverflow.com/questions/5484158#answer-26257086">this</a> SO answer on
   * using zip files in Java.
   *
   * @param zipFilePath Path to the zip file containing initial land cover maps.
   * @param lctFileName Name of the file to extract from within the zip archive
   * @return Path to the extracted file, in a temporary directory.
   * @throws IOException
   */
  private static File extractInitialLandCoverMap(File zipFilePath, String lctFileName)
      throws IOException {
    Path tmpDir;
    try {
      tmpDir = Files.createTempDirectory(null);
    } catch (IOException e) {
      System.out
          .println("Could not create temporary directory to extract initial land cover map into.");
      throw e;
    }
    File outputLocation = new File(tmpDir.toFile(), lctFileName);
    Path zipFile = Paths.get(zipFilePath.toString());

    // Load zip file as filesystem
    try (FileSystem fileSystem = FileSystems.newFileSystem(zipFile, null)) {
      // copy file from zip file to output location
      Path source = fileSystem.getPath(lctFileName);
      Files.copy(source, outputLocation.toPath());
    } catch (NoSuchFileException e) {
      logger.error("Could not find init-landcover file in zip file. " + "Check '" + lctFileName
          + "' is included in archive " + zipFile.toString() + ".", e);
      throw e;
    } catch (FileSystemNotFoundException e) {
      logger.error("Could not find zip file " + zipFile.toString() + ".", e);
      throw e;
    }
    return outputLocation;
  }

  private void checkDimensionsConsistentWithPrevious(ValueLayer layer) {
    if (!dimensionsMatchPrevious(layer)) {
      throw new RuntimeException(layer.getName() + " has dimensions which don't match those "
          + "of previously loaded value layers.");
    }
  }

  /**
   * @param layer Value layer under test.
   * @return {@code true} if the dimensions of this value layer match those of the value layers
   *         previously loaded by this class.
   */
  private boolean dimensionsMatchPrevious(ValueLayer layer) {
    Dimensions dims = layer.getDimensions();
    boolean match;
    if (this.prevRasterDimensions == null) {
      this.prevRasterDimensions = layer.getDimensions();
      match = true;
    } else {
      if (this.prevRasterDimensions.equals(dims)) {
        match = true;
      } else {
        match = false;
      }
    }
    return match;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int[] getGridDimensions() {
    Dimensions dims = getDemMap().getDimensions();
    return new int[] {(int) dims.getWidth(), (int) dims.getHeight()};
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public double[] getGridCellPixelSize() {
    Double xSize = this.siteConfig.getDouble("geographic.raster.gridCellPixelSizeX");
    Double ySize = this.siteConfig.getDouble("geographic.raster.gridCellPixelSizeY");
    return new double[] {xSize, ySize};
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<WindSpeed, Double> getWindSpeedProb() {
    Map<WindSpeed, Double> windSpeedProbMap = new EnumMap<WindSpeed, Double>(WindSpeed.class);
    SubnodeConfiguration speedProbConfig =
        this.siteConfig.configurationAt("climate.wind.speedProb");
    Iterator<String> keys = speedProbConfig.getKeys();
    while (keys.hasNext()) {
      String speedName = keys.next();
      Double speedProb = speedProbConfig.getDouble(speedName);
      windSpeedProbMap.put(WindSpeed.valueOf(WordUtils.capitalize(speedName)), speedProb);
    }
    return windSpeedProbMap;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<Direction, Double> getWindDirectionProb() {
    Map<Direction, Double> windDirProbMap = new EnumMap<Direction, Double>(Direction.class);
    SubnodeConfiguration dirProbConfig =
        this.siteConfig.configurationAt("climate.wind.directionProb");
    Iterator<String> keys = dirProbConfig.getKeys();
    while (keys.hasNext()) {
      String dirName = keys.next();
      Double dirProb = dirProbConfig.getDouble(dirName);
      windDirProbMap.put(Direction.valueOf(WordUtils.capitalize(dirName)), dirProb);
    }
    return windDirProbMap;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public double getMeanMonthlyPrecipitation() {
    return this.siteConfig.getDouble("climate.meanMonthlyPrecipitation");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public double getTotalAnnualPrecipitation() {
    return this.siteConfig.getDouble("climate.meanAnnualPrecipitation");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public double getMeanAnnualTemperature() {
    return this.siteConfig.getDouble("climate.meanAnnualTemperature");
  }

}
