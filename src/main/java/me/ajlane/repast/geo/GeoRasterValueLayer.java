package me.ajlane.repast.geo;

import java.io.IOException;

import org.geotools.data.DataSourceException;
import org.geotools.geometry.jts.ReferencedEnvelope;

import repast.simphony.space.grid.GridPointTranslator;
import repast.simphony.valueLayer.GridValueLayer;

public class GeoRasterValueLayer extends AbstractGeoRasterValueLayer<GridValueLayer> {

	public GeoRasterValueLayer(String fileName, String layerName,
			double defaultValue, GridPointTranslator translator)
			throws DataSourceException, IOException {
		super(fileName, layerName, defaultValue, translator);
	}
	
	public GeoRasterValueLayer(String fileName, String layerName)
			throws DataSourceException, IOException {
		super(fileName, layerName);
	}
	
	public GeoRasterValueLayer(String fileName, String layerName, int[] dimensions,
			  ReferencedEnvelope envelope) {
		super(fileName, layerName, dimensions, envelope);
	}

	@Override
	protected GridValueLayer getValueLayer(String layerName, double defaultValue,
				GridPointTranslator translator) {
		return new GridValueLayer(layerName, defaultValue, true, translator, new int[]{width, height});
	}
	
}
