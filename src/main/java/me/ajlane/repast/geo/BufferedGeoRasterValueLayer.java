package me.ajlane.repast.geo;

import java.awt.image.Raster;
import java.io.IOException;

import org.geotools.data.DataSourceException;
import org.geotools.geometry.jts.ReferencedEnvelope;

import repast.simphony.space.grid.GridPointTranslator;
import repast.simphony.valueLayer.BufferedGridValueLayer;

public class BufferedGeoRasterValueLayer extends AbstractGeoRasterValueLayer<BufferedGridValueLayer> {

	public BufferedGeoRasterValueLayer(String fileName, String layerName,
			double defaultValue, GridPointTranslator translator)
			throws DataSourceException, IOException {
		super(fileName, layerName, defaultValue, translator);
	}
	
	public BufferedGeoRasterValueLayer(String fileName, String layerName)
			throws DataSourceException, IOException {
		super(fileName, layerName);
	}
	
	public BufferedGeoRasterValueLayer(String fileName, String layerName, int[] dimensions,
			  ReferencedEnvelope envelope) {
		super(fileName, layerName, dimensions, envelope);
	}

	@Override
	protected BufferedGridValueLayer getValueLayer(String layerName, double defaultValue,
				GridPointTranslator translator) {
		return new BufferedGridValueLayer(layerName, defaultValue, true, translator, new int[]{width, height});
	}
	
	public void swap() { 
		((BufferedGridValueLayer)valueLayer).swap();
	}
	
	@Override
	protected void applyRasterToValueLayer(Raster raster) {
		// assumes raster only has one band (index 0)
		for (int i=0; i<width; i++) {
			for (int j=0; j<height; j++) {
				valueLayer.set(raster.getSampleDouble(i, j, 0), i, j);
			}
		}
		// swap read/ write buffers so we can read our initial values
		((BufferedGridValueLayer)valueLayer).swap();
	}
}