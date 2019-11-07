package repast.model.agrosuccess;

import java.util.Map;
import repast.simphony.space.Dimensions;
import repast.simphony.valueLayer.GridValueLayer;

/**
 * This class represents and provides access to all the site-specific data the AgroSuccess model
 * needs. Note that concrete classes which extend this one must implement methods to actually load
 * this data.
 * 
 * This class provides the {@code checkGridShapesMatch()} method which is called in its constructor.
 * Its purpose is to confirm that all the raster grids loaded have matching grid dimensions. 
 * 
 * @author Andrew Lane
 *
 */

public abstract class SiteBoundaryConds {
  
  Map<LscapeLayer, GridValueLayer> rasterLayerMap;
  Dimensions gridDimensions;
  int meanAnnualPrecipitation;
  double gridPixelSize;

  /**
   * Populate {@code this.rasterLayerMap} with site specific data. Check the dimensions of each 
   * grid matches those of the others
   */
  abstract void initRasterLayerMapAndGridDimensions() throws IndexOutOfBoundsException; 
  
  /**
   * Read mean annual precipitation value and initialise {@code this.meanAnnualPrecipitation}.
   */
  abstract void initMeanAnnualPrecipitation();
  
  /**
   * Read grid pixel size value and initialise {@code this.gridPixelSize}.
   */
  abstract void initGridPixelSize();  
  
  /**
   * @return Mean annual precipitation at the study site
   */
  public int getMeanAnnualPrecipitation() {
    return this.meanAnnualPrecipitation;
  }
  
  /**
   * @return Length of grid pixel's edge
   */
  public double getGridPixelSize() {
    return this.gridPixelSize;
  }
  
  
  /**
   * @return Land cover types used at the start of the simulation.
   */
  public GridValueLayer getInitialLandCoverMap() {
    return rasterLayerMap.get(LscapeLayer.Lct);
  }
  
  /**
   * @return Soil types, 0=Type A, 1=Type B, 2=Type C, 3=Type D.
   */
  public GridValueLayer getSoilMap() {
    return rasterLayerMap.get(LscapeLayer.SoilType);
  }
  
  /**
   * @return Succession state of the cell. 0=regeneration, 1=secondary.
   */
  public GridValueLayer getOakRegenMap() {
    return rasterLayerMap.get(LscapeLayer.OakRegen);
  }  
    
  /**
   * @return Slope, expressed as percent slope.
   */
  public GridValueLayer getSlopeMap() {
    return rasterLayerMap.get(LscapeLayer.Slope);
  }
  
  /**
   * @return Binary aspect north (0) or south (1).
   */
  public GridValueLayer getAspectMap() {
    return rasterLayerMap.get(LscapeLayer.Aspect);
  }
  
  /**
   * @return Direction water flows out of each cell. 1=E, 2=NE, 3=N,..., 8=SE.
   */
  public GridValueLayer getFlowDirMap() {
    return rasterLayerMap.get(LscapeLayer.FlowDir);
  }
  
  /**
   * @return Grid dimensions. First element is dimension in x direction, second in y direction.
   * @see repast.simphony.space.grid.GridDimensions.GridDimensions
   */
  public int[] getGridDimensions() {
    Dimensions dims = this.getInitialLandCoverMap().getDimensions();
    int [] arr =  {(int)dims.getWidth(), (int)dims.getHeight()};
    return arr;
  }

}
