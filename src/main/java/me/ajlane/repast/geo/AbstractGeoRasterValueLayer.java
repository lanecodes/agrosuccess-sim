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
	protected ReferencedEnvelope envelope;
	protected int height;
	protected int width;
	protected T valueLayer;
	 
	
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
	
	abstract protected void applyRasterToValueLayer(Raster raster);
	
	// inheriting concrete class implementation will create instance of 
	// appropriate ValueLayer type
	abstract protected T getValueLayer(String layerName, double defaultValue, 
			GridPointTranslator translator);
	
	public String getFileName() {
		return this.fileName;
	}

	@Override
	abstract public String getName();

	@Override
	abstract public double get(double... coordinates);

	@Override
	abstract public Dimensions getDimensions();

	@Override
	abstract public void set(double value, int... coordinate); 
}
