package me.ajlane.repast.geo;

import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;

import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.data.DataSourceException;
import org.geotools.factory.Hints;
import org.geotools.gce.geotiff.GeoTiffReader;
import org.geotools.geometry.jts.ReferencedEnvelope;

import repast.simphony.space.Dimensions;
import repast.simphony.space.grid.GridPointTranslator;
import repast.simphony.space.grid.StrictBorders;
import repast.simphony.valueLayer.IGridValueLayer;

import me.ajlane.repast.geo.DisaggregatedGeoRaster;

/**
 * Abstraction to support the loading of data from geotagged raster data 
 * sources (initially limited to GeoTiff files) into a data structure 
 * which supports by Repast's IGridValueLayer interface. This will make it 
 * easier to use such data in Repast contexts.
 * 
 * @author Andrew Lane
 */
public abstract class AbstractGeoRasterValueLayer<T> implements IGridValueLayer {
	
	private String fileName;
	private ReferencedEnvelope envelope;
	protected IGridValueLayer valueLayer;
	protected int height;
	protected int width;
	
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
	  public AbstractGeoRasterValueLayer(String fileName, String layerName)
			  throws DataSourceException, IOException {
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
			double defaultValue, GridPointTranslator translator)
			throws DataSourceException, IOException {
		
		DisaggregatedGeoRaster data = extractGeoTiffFileData(new File(fileName));
		envelope = data.getEnvelope();
		height = data.getRaster().getHeight();
		width = data.getRaster().getWidth();	
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
	 *  
	 * @throws DataSourceException 
	 */
	private DisaggregatedGeoRaster extractGeoTiffFileData(File file) 
				throws DataSourceException, IOException {
		
		GeoTiffReader reader = new GeoTiffReader(file, 
				new Hints(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, Boolean.TRUE));
		
		GridCoverage2D coverage = (GridCoverage2D) reader.read(null);
		ReferencedEnvelope env = new ReferencedEnvelope(coverage.getEnvelope2D());	
		Raster gridData = coverage.getRenderedImage().getData();
		
		return new DisaggregatedGeoRaster(env, gridData);
		
	}
	
	// inheriting concrete class implementation will create instance of 
	// appropriate ValueLayer type
	abstract protected IGridValueLayer getValueLayer(String layerName, double defaultValue, 
			GridPointTranslator translator);
	
	protected void applyRasterToValueLayer(Raster raster) {
		// assumes raster only has one band (index 0)
		for (int i=0; i<width; i++) {
			for (int j=0; j<height; j++) {
				valueLayer.set(raster.getSampleDouble(i, j, 0), i, j);
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
