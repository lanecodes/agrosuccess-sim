/**
 *
 */
package repast.model.agrosuccess;

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
import me.ajlane.geo.repast.GeoRasterValueLayer;
import repast.simphony.space.Dimensions;
import repast.simphony.valueLayer.GridValueLayer;
import me.ajlane.geo.repast.wind.WindDirection;
import me.ajlane.geo.repast.wind.WindSpeed;

/**
 * Contains logic to load study site specific data and parameters.
 *
 * @author Andrew Lane
 *
 */
public class StudySiteDataContainer {

  File siteDataDir;
  private HierarchicalConfiguration siteConfig;

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
  StudySiteDataContainer(File siteDataDir) throws ConfigurationException {
    this.siteDataDir = siteDataDir;
    File configFile = new File(siteDataDir, "site_parameters.xml");
    try {
      this.siteConfig = new XMLConfiguration(configFile);
    } catch (ConfigurationException e) {
      System.out.println("Check XML configuration " + configFile.toString());
      throw e;
    }
  }

  /**
   * @return UTF-8 encoded name of study site.
   */
  public String getSiteName() {
    return this.siteConfig.getString("siteName");
  }

  /**
   * @return {@link repast.simphony.valueLayer.GridValueLayer GridValueLayer} encoding each grid
   *         cell's percentage incline. See <a href=
   *         "https://github.com/lanecodes/demproc/blob/master/demproc/makelayers.py"><code>demproc</code></a>
   *         docs for encoding details.
   */
  public GridValueLayer getSlopeMap() {
    File file = new File(this.siteDataDir, "slope.tif");
    GeoRasterValueLayer grvl =
        new GeoRasterValueLayer(file.getAbsolutePath(), LscapeLayer.Slope.name());

    return grvl.getValueLayer();
  }

  /**
   * @return {@link repast.simphony.valueLayer.GridValueLayer GridValueLayer} encoding whether each
   *         grid cell has a northerly or southerly aspect. See <a href=
   *         "https://github.com/lanecodes/demproc/blob/master/demproc/makelayers.py"><code>demproc</code></a>
   *         docs for encoding details.
   */
  public GridValueLayer getAspectMap() {
    File file = new File(this.siteDataDir, "binary_aspect.tif");
    GeoRasterValueLayer grvl =
        new GeoRasterValueLayer(file.getAbsolutePath(), LscapeLayer.Aspect.name());

    return grvl.getValueLayer();
  }

  /**
   * @return {@link repast.simphony.valueLayer.GridValueLayer GridValueLayer} encoding the direction
   *         in which water flows over the landscape. See <a href=
   *         "https://github.com/lanecodes/demproc/blob/master/demproc/makelayers.py"><code>demproc</code></a>
   *         docs for encoding details.
   */
  public GridValueLayer getFlowDirectionMap() {
    File file = new File(this.siteDataDir, "flow_dir.tif");
    GeoRasterValueLayer grvl =
        new GeoRasterValueLayer(file.getAbsolutePath(), LscapeLayer.FlowDir.name());

    return grvl.getValueLayer();
  }

  /**
   * A homogeneous type A soil type map.
   *
   * @return {@link repast.simphony.valueLayer.GridValueLayer GridValueLayer} of homogeneous type A
   *         soil type.
   */
  public GridValueLayer getSoilTypeMap() {
    return getSoilTypeMap("A");
  }

  /**
   * @param soilTypeLetter One of A, B, C, or D. These correspond to the types used in Millington et
   *        al. 2009. It is assumed that soil type is homogeneous across the landscape. Encoded as 0
   *        -> type A, 1 -> type B, ..., 3 -> type D.
   *
   * @return {@link repast.simphony.valueLayer.GridValueLayer GridValueLayer} encoding soil type.
   */
  public GridValueLayer getSoilTypeMap(String soilTypeLetter) {
    File file = new File(this.siteDataDir, "uniform_soil_map_" + soilTypeLetter + ".tif");
    GeoRasterValueLayer grvl =
        new GeoRasterValueLayer(file.getAbsolutePath(), LscapeLayer.SoilType.name());

    return grvl.getValueLayer();
  }

  /**
   * Extract default land cover type map from the initial conditions archive for the simulation's
   * study site. Namely the file {@code init-landcover0.tif} within {@code init_lct_maps.zip}.
   *
   * @return {@link repast.simphony.valueLayer.GridValueLayer GridValueLayer} encoding land cover
   *         state.
   * @throws IOException
   */
  public GridValueLayer getLandcoverTypeMap() throws IOException {
    return getLandcoverTypeMap(0);
  }

  public GridValueLayer getLandcoverTypeMap(int mapNum) throws IOException {
    File file = extractInitialLandCoverMap(new File(this.siteDataDir, "init_lct_maps.zip"), mapNum);
    GeoRasterValueLayer grvl =
        new GeoRasterValueLayer(file.getAbsolutePath(), LscapeLayer.Lct.name());
    file.delete(); // Delete file LCT map temporarily extracted from the archive

    return grvl.getValueLayer();
  }

  /**
   * Given a zip archive containing initial land cover maps, extract map number {@code mapNum} to a
   * temporary directory. Return a {@code File} representing the extracted GeoTiff file.
   *
   * Takes inspiration from
   * <a href= "https://stackoverflow.com/questions/5484158#answer-26257086">this</a> SO answer on
   * using zip files in Java.
   *
   * @param zipFilePath Path to the zip file containing initial land cover maps.
   * @param mapNum The number of the initial land cover map to extract from the zip file.
   * @return Path to the extracted file, in a temporary directory.
   * @throws IOException
   */
  private static File extractInitialLandCoverMap(File zipFilePath, int mapNum) throws IOException {
    Path tmpDir;
    try {
      tmpDir = Files.createTempDirectory(null);
    } catch (IOException e) {
      System.out
      .println("Could not create temporary directory to extract initial land cover map into.");
      throw e;
    }
    String lctFileName = "init-landcover" + (new Integer(mapNum)).toString() + ".tif";
    File outputLocation = new File(tmpDir.toFile(), lctFileName);
    Path zipFile = Paths.get(zipFilePath.toString());

    // Load zip file as filesystem
    try (FileSystem fileSystem = FileSystems.newFileSystem(zipFile, null)) {
      // copy file from zip file to output location
      Path source = fileSystem.getPath(lctFileName);
      Files.copy(source, outputLocation.toPath());
    } catch (NoSuchFileException e) {
      System.out.println("Could not find init-landcover file in zip file. " + "Check map number "
          + mapNum + " is included in archive " + zipFile.toString() + ".");
      throw e;
    } catch (FileSystemNotFoundException e) {
      System.out.println("Could not find zip file " + zipFile.toString() + ".");
      throw e;
    }
    return outputLocation;
  }

  /**
   * @return Monthly precipitation in millimetres, averaged across the year.
   */
  public double getPrecipitation() {
    return this.siteConfig.getDouble("climate.meanMonthlyPrecipitation");
  }

  /**
   * Grid shape dimensions (how many cells along each axis). Calculated from the Slope map.
   *
   * TODO: Write some checks to confirm the dimensions of all the loaded value layers match.
   *
   * @return {@code int} array of grid dimensions in form [xDimension, yDimension]
   */
  public int[] getGridDimensions() {

    Dimensions slopeDimensions = getSlopeMap().getDimensions();
    return new int[] {(int) slopeDimensions.getWidth(), (int) slopeDimensions.getHeight()};
  }

  /**
   * (x,y) dimensions of the grid cell pixels, retrieved from site_parameters.xml these will me in
   * meters
   *
   * @return [Xpixel size, Ypixel size]
   */
  public double[] getGridCellPixelSize() {
    Double xSize = this.siteConfig.getDouble("geographic.raster.gridCellPixelSizeX");
    Double ySize = this.siteConfig.getDouble("geographic.raster.gridCellPixelSizeY");
    return new double[] {xSize, ySize};
  }

  /**
   * @return Map of {@link me.ajlane.geo.repast.wind.WindSpeed WindSpeed} enumeration constants to
   *         the probability of observing the wind speed corresponding to each member at the study
   *         site.
   */
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
   * @return Map of {@link me.ajlane.geo.repast.wind.WindDirection WindDirection} enumeration
   *         constants to the probability of observing the wind direction corresponding to each
   *         member at the study site.
   */
  public Map<WindDirection, Double> getWindDirectionProb() {
    Map<WindDirection, Double> windDirProbMap =
        new EnumMap<WindDirection, Double>(WindDirection.class);
    SubnodeConfiguration dirProbConfig =
        this.siteConfig.configurationAt("climate.wind.directionProb");
    Iterator<String> keys = dirProbConfig.getKeys();
    while (keys.hasNext()) {
      String dirName = keys.next();
      Double dirProb = dirProbConfig.getDouble(dirName);
      windDirProbMap.put(WindDirection.valueOf(WordUtils.capitalize(dirName)), dirProb);
    }
    return windDirProbMap;
  }
}
