package me.ajlane.geo.repast;

import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import org.apache.log4j.Logger;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.factory.Hints;
import org.geotools.gce.geotiff.GeoTiffReader;
import org.geotools.geometry.jts.ReferencedEnvelope;

import me.ajlane.geo.repast.DisaggregatedGeoRaster;
import repast.simphony.space.Dimensions;
import repast.simphony.space.grid.GridPointTranslator;
import repast.simphony.space.grid.StrictBorders;
import repast.simphony.valueLayer.IGridValueLayer;

/**
 * Abstraction to support the loading of data from geotagged raster data
 * sources (initially limited to GeoTiff files) into a data structure
 * which supports by Repast's IGridValueLayer interface. This will make it
 * easier to use such data in Repast contexts.
 *
 * @author Andrew Lane
 */
public abstract class AbstractGeoRasterValueLayer<T> implements IGridValueLayer {

    final static Logger logger = Logger.getLogger(AbstractGeoRasterValueLayer.class);

	private String fileName;
	private ReferencedEnvelope envelope;
	IGridValueLayer valueLayer;
	int height;
	int width;
	int minX;
	int minY;

	 /**
	  * Creates a GridValueLayer with the specified name, density, and dimensions.
	  * The default value of every cell in the grid will be -99999.0. The default border
	  * behavior is strict.
	  *
	  * @param fileName
	  * 		the path to the georeferenced data file to be used to construct
	  *	    	the layer
	  * @param layerName
	  *          the name of the value layer
	  */
	  public AbstractGeoRasterValueLayer(String fileName, String layerName) {
		  this(fileName, layerName, -99999.0, new StrictBorders());
	  }

	/**
	 * Creates a AbstractGeoRasterValueLayer with the specified fileName layerName,
	 * defaultValue and grid translator.
	 *
	 * Layer assumed to be dense, and dimensions are inferred from the provided
	 * input file.
	 *
	 * @param fileName
	 * 			the path to the georeferenced data file to be used to construct
	 * 			the layer
	 * @param layerName
	 *          the name of the value layer
	 * @param defaultValue
	 *          the default value that each grid cell will contain if it has not
	 *          been set yet
	 * @param translator
	 *          the translator used
	 */
	public AbstractGeoRasterValueLayer(String fileName, String layerName,
			double defaultValue, GridPointTranslator translator) {

		DisaggregatedGeoRaster data = extractGeoTiffFileData(new File(fileName));
		envelope = data.getEnvelope();
		height = data.getRaster().getHeight();
		width = data.getRaster().getWidth();
		minX = data.getRaster().getMinX();
		minY = data.getRaster().getMinY();
		valueLayer = getValueLayer(layerName, defaultValue, translator);
		applyRasterToValueLayer(data.getRaster());

	}

	 /**
	  * An alternative constructor for an AbstractGeoRasterValueLayer intended to be
	  * used in situations where we know what geographical bounding box and dimensions
	  * we require for the ValueLayer, but don't have a file which we intend to use to
	  * construct one. Instead, we might decide to initialise its values using some sort
	  * of application logic contained in a specialised subclass. In any case, we allow
	  * that subclass freedom to implement that however is appropriate.
	  *
	  * The default value of every cell in the grid will be -99999.0. The default border
	  * behaviour is strict.
	  *
	  * @param fileName
	  * 		The path which should be used if the layer is ever saved to a georeferenced
	  * 		data file.
	  * @param layerName
	  *         The name of the value layer.
	  * @param dimensions
	  * 		The x, y dimensions of the ValueLayer
	  * @param envelope
	  * 		The (geo) ReferencedEnvelope specifying the geographical extent of the
	  * 		value layer (for potential use in subsequent GIS applications).
	  */
	  public AbstractGeoRasterValueLayer(String fileName, String layerName, int[] dimensions,
			  ReferencedEnvelope envelope) {
		  this.envelope = envelope;
		  width = dimensions[0];
		  height = dimensions[1];
		  valueLayer = getValueLayer(layerName, -99999.0, new StrictBorders());
	  }

	/**
	 * Reads fileName and pulls out a georeferenced envelope for the area
	 * covered by the file and a Raster image representing the contained
	 * data. Returns a DisaggregatedGeoRaster containing this information.
	 */
	private DisaggregatedGeoRaster extractGeoTiffFileData(File file) {
		try {
			GeoTiffReader reader = new GeoTiffReader(file,
					new Hints(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, Boolean.TRUE));

			GridCoverage2D coverage = (GridCoverage2D) reader.read(null);
			ReferencedEnvelope env = new ReferencedEnvelope(coverage.getEnvelope2D());
			Raster gridData = coverage.getRenderedImage().getData();

			return new DisaggregatedGeoRaster(env, gridData);
		} catch (IOException e) {
			logger.error("Could not read " + file.getAbsolutePath(), e);
			throw new RuntimeException(e);
		}
	}

	// inheriting concrete class implementation will create instance of
	// appropriate ValueLayer type
	abstract protected IGridValueLayer getValueLayer(String layerName, double defaultValue,
			GridPointTranslator translator);

	/**
	 * NOTE: Raster images use a coordinate sytem with the origin in the
	 * <a href="https://docs.oracle.com/javase/7/docs/api/java/awt/image/Raster.html">top left hand corner</a>
	 * with <a href="https://docs.oracle.com/javase/tutorial/2d/overview/coordinate.html">x values increasing
	 * to the right and y values increasing down</a>.
	 *
	 * By contrast Repast models use Cartesian coordinates, conventionally represented in visualisations
	 * such that the origin is in the bottom left. Hence while the (horizontal) x axis is unaffected,
	 * to ensure images are represented as expected in repast it is necessary to invert the y axis.
	 *
	 * @param raster
	 */
	void applyRasterToValueLayer(Raster raster) {
		// assumes raster only has one band (index 0)
		for (int x=0; x<width; x++) {
			for (int y=0; y<height; y++) {
				valueLayer.set(raster.getSampleDouble(x, y, 0), x, height-1-y);
			}
		}
	}

	public String getFileName() {
		// Get name of file used to create the layer
		return fileName;
	}

	public ReferencedEnvelope getEnvelope() {
		return envelope;
	}

	@Override
	public String getName() {
		// Get name of layer
		return valueLayer.getName();
	}

	@Override
	public double get(double... coordinates) {
		// Get values given coordinates
		return valueLayer.get(coordinates);

	}

	@Override
	public Dimensions getDimensions() {
		return valueLayer.getDimensions();

	}

	@Override
	public void set(double value, int... coordinate) {
		valueLayer.set(value, coordinate);
	}

}
