package repast.model.agrosuccess.empirical;

import java.io.IOException;
import repast.simphony.valueLayer.IGridValueLayer;

public interface SiteRasterData {

  /**
   * @return {@link repast.simphony.valueLayer.GridValueLayer GridValueLayer} encoding each grid
   *         cell's elevation in meters.
   */
  IGridValueLayer getDemMap();

  /**
   * @return {@link repast.simphony.valueLayer.GridValueLayer GridValueLayer} encoding each grid
   *         cell's percentage incline. See <a href=
   *         "https://github.com/lanecodes/demproc/blob/master/demproc/makelayers.py"><code>demproc</code></a>
   *         docs for encoding details.
   */
  IGridValueLayer getSlopeMap();

  /**
   * @return {@link repast.simphony.valueLayer.GridValueLayer GridValueLayer} encoding whether each
   *         grid cell has a northerly or southerly aspect. See <a href=
   *         "https://github.com/lanecodes/demproc/blob/master/demproc/makelayers.py"><code>demproc</code></a>
   *         docs for encoding details.
   */
  IGridValueLayer getAspectMap();

  /**
   * @return {@link repast.simphony.valueLayer.GridValueLayer GridValueLayer} encoding the direction
   *         in which water flows over the landscape. See <a href=
   *         "https://github.com/lanecodes/demproc/blob/master/demproc/makelayers.py"><code>demproc</code></a>
   *         docs for encoding details.
   */
  IGridValueLayer getFlowDirMap();

  /**
   * A homogeneous type A soil type map.
   *
   * @see #getSoilTypeMap(String)
   *
   * @return {@link repast.simphony.valueLayer.GridValueLayer GridValueLayer} of homogeneous type A
   *         soil type.
   */
  IGridValueLayer getSoilTypeMap();

  /**
   * @param soilTypeLetter One of A, B, C, or D. These correspond to the types used in Millington et
   *        al. 2009. It is assumed that soil type is homogeneous across the landscape. Encoded as:
   *        <ul>
   *        <li>0 -> type A</li>
   *        <li>1 -> type B</li>
   *        <li>2 -> type C</li>
   *        <li>3 -> type D</li>
   *        </ul>
   *
   * @return {@link repast.simphony.valueLayer.GridValueLayer GridValueLayer} encoding soil type.
   */
  IGridValueLayer getSoilTypeMap(String soilTypeLetter);

  /**
   * @see #getLandcoverTypeMap(int)
   *
   * @return Default 0th {@link repast.simphony.valueLayer.GridValueLayer GridValueLayer} encoding
   *         land cover state.
   * @throws IOException
   */
  IGridValueLayer getLctMap() throws IOException;

  /**
   *
   * Extract land cover type map from the initial conditions archive for the simulation's study
   * site. Namely the file {@code init-landcover<mapNum>.tif} within {@code init_lct_maps.zip}.
   *
   * @param mapNum Number of land cover type map within an {@code init_lct_maps.zip} archive to use.
   * @return {@link repast.simphony.valueLayer.GridValueLayer GridValueLayer} encoding land cover
   *         state.
   * @throws IOException
   */
  IGridValueLayer getLctMap(int mapNum) throws IOException;

  /**
   * Grid shape dimensions (how many cells along each axis). Calculated from the Slope map.
   *
   * @return {@code int} array of grid dimensions in form [xDimension, yDimension]
   */
  int[] getGridDimensions();

  /**
   * (x,y) dimensions of the grid cell pixels, retrieved from site_parameters.xml these will me in
   * meters
   *
   * @return [Xpixel size, Ypixel size]
   */
  double[] getGridCellPixelSize();

}
