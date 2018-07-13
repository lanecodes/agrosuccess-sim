package me.ajlane.repast.geo;

import java.io.IOException;

import org.geotools.data.DataSourceException;

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

	@Override
	protected GridValueLayer getValueLayer(String layerName, double defaultValue,
				GridPointTranslator translator) {
		return new GridValueLayer(layerName, defaultValue, true, translator, new int[]{width, height});
	}
	
}
