package repast.model.agrosuccess;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import me.ajlane.geo.repast.GeoRasterValueLayer;
import repast.simphony.valueLayer.GridValueLayer;

/**
 * Given file names of GeoTiff files containing boundary condition raster grids, a grid pixel size
 * value and mean annual precipitation for a study site, this class will provide access to 
 * corresponding Repast {@code repast.simphony.valueLayer.GridValueLayer} objects. 
 * 
 * @author Andrew Lane
 *
 */
public class SiteBoundaryCondsHardCoded extends SiteBoundaryConds {
  
  Map<LscapeLayer, File> lscapeLayerFiles;  
  
  /**
   * @param meanAnnualPrecipitation
   * @param gridPixelSize
   * @param initialLandCoverMapFile
   * @param soilMapFile
   * @param slopeMapFile
   * @param aspectMapFile
   * @param flowDirMapFile
   */
  public SiteBoundaryCondsHardCoded(int meanAnnualPrecipitation, double gridPixelSize, 
      File initialLandCoverMapFile, File soilMapFile, File successionMapFile, File slopeMapFile, 
      File aspectMapFile, File flowDirMapFile) {
    
    this.meanAnnualPrecipitation = meanAnnualPrecipitation;
    this.gridPixelSize = gridPixelSize;
    
    this.rasterLayerMap = new HashMap<LscapeLayer, GridValueLayer>();
    
    this.lscapeLayerFiles = new HashMap<>();
    this.lscapeLayerFiles.put(LscapeLayer.Lct, initialLandCoverMapFile);
    this.lscapeLayerFiles.put(LscapeLayer.SoilType, soilMapFile);
    this.lscapeLayerFiles.put(LscapeLayer.OakRegen, successionMapFile);
    this.lscapeLayerFiles.put(LscapeLayer.Slope, slopeMapFile);
    this.lscapeLayerFiles.put(LscapeLayer.Aspect, aspectMapFile);
    this.lscapeLayerFiles.put(LscapeLayer.FlowDir, flowDirMapFile);
    
        
    try {
      initRasterLayerMapAndGridDimensions();
    } catch (IndexOutOfBoundsException e) {
      throw e;
    }

  }

  @Override
  void initRasterLayerMapAndGridDimensions() throws IndexOutOfBoundsException {
    boolean firstElement = true;
    for (Map.Entry<LscapeLayer, File> entry : this.lscapeLayerFiles.entrySet()) { 
      GeoRasterValueLayer grvl = 
          new GeoRasterValueLayer(entry.getValue().getPath(), entry.getKey().name());
      
      if (firstElement) {
        this.gridDimensions = grvl.getDimensions();
        firstElement = false;
      } else {
        // Check if the current grid's dimensions match the others
        if (this.gridDimensions.getHeight() != grvl.getDimensions().getHeight() 
            || this.gridDimensions.getWidth() != grvl.getDimensions().getWidth()) {
          throw new IndexOutOfBoundsException("The dimensions of the grid specified by " 
              + entry.getValue().getPath() + " don't match the dimensions of other grids."
              + "\nThis grid: " + grvl.getDimensions() + "\nOther grids: " + this.gridDimensions);
        }
      }
      this.rasterLayerMap.put(entry.getKey(), grvl.getValueLayer());
    }  
  }

  @Override
  void initMeanAnnualPrecipitation() {
    // Do nothing, mean annual precipitation set in constructor.
  }

  @Override
  void initGridPixelSize() {
    // Do nothing, grid pixel size set in constructor
  }

}

