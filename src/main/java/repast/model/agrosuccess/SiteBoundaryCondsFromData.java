package repast.model.agrosuccess;

import java.io.File;
import java.io.IOException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import repast.simphony.valueLayer.GridValueLayer;

/**
 * Provides access to study site boundary conditions. Designed to provide the same interface as
 * {@link repast.model.agrosuccess.SiteBoundaryCondsHardCoded SiteBoundaryCondsHardCoded} to
 * make it easy to switch between using full resolution data corresponding to study sites, or
 * smaller dummy landscapes used for testing purposes.
 *
 * @author Andrew Lane
 *
 */
public class SiteBoundaryCondsFromData extends SiteBoundaryConds {

  Map<LscapeLayer, File> lscapeLayerFiles;
  StudySiteDataContainer siteData;

  /**
   * @param siteData Empirical data for the study site represented in the simulation.
   * @throws IOException
   * @throws IndexOutOfBoundsException
   */
  public SiteBoundaryCondsFromData(StudySiteDataContainer siteData)
      throws IndexOutOfBoundsException, IOException {

    this.siteData = siteData;
    this.rasterLayerMap = new HashMap<LscapeLayer, GridValueLayer>();
    initRasterLayerMapAndGridDimensions();

  }

  @Override
  void initRasterLayerMapAndGridDimensions() throws IndexOutOfBoundsException, IOException {
    this.rasterLayerMap = getRasterLayerMap();

    boolean firstElement = true;
    for (Map.Entry<LscapeLayer, GridValueLayer> entry : this.rasterLayerMap.entrySet()) {
      GridValueLayer gvl = entry.getValue();

      if (firstElement) {
        this.gridDimensions = gvl.getDimensions();
        firstElement = false;
      } else {
        // Check if the current grid's dimensions match the others
        if (this.gridDimensions.getHeight() != gvl.getDimensions().getHeight()
            || this.gridDimensions.getWidth() != gvl.getDimensions().getWidth()) {
          throw new IndexOutOfBoundsException("The dimensions of the grid specified by "
              + entry.getKey().name() + " don't match the dimensions of other grids."
              + "\nThis grid: " + gvl.getDimensions() + "\nOther grids: " + this.gridDimensions);
        }
      }
    }
  }

  private Map<LscapeLayer, GridValueLayer> getRasterLayerMap() throws IOException {
    Map<LscapeLayer, GridValueLayer> rlMap =
        new EnumMap<LscapeLayer, GridValueLayer>(LscapeLayer.class);
    rlMap.put(LscapeLayer.Lct, this.siteData.getLandcoverTypeMap());
    rlMap.put(LscapeLayer.SoilType, this.siteData.getSoilTypeMap());
    rlMap.put(LscapeLayer.Slope, this.siteData.getSlopeMap());
    rlMap.put(LscapeLayer.Aspect, this.siteData.getAspectMap());
    rlMap.put(LscapeLayer.FlowDir, this.siteData.getFlowDirectionMap());
    return rlMap;
  }


  @Override
  void initMeanAnnualPrecipitation() {
    this.meanAnnualPrecipitation = (int) Math.round(this.siteData.getPrecipitation());
  }

  @Override
  void initGridPixelSize() {
    this.gridPixelSize = this.siteData.getGridCellPixelSize()[0];
  }

}
