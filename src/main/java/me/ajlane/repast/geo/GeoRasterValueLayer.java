package me.ajlane.repast.geo;

import java.awt.image.Raster;
import java.io.IOException;

import org.geotools.data.DataSourceException;

import repast.simphony.space.Dimensions;
import repast.simphony.space.grid.GridPointTranslator;
import repast.simphony.valueLayer.GridValueLayer;

public class GeoRasterValueLayer extends AbstractGeoRasterValueLayer<GridValueLayer> {

	public GeoRasterValueLayer(String fileName, String layerName,
			double defaultValue, GridPointTranslator translator)
			throws DataSourceException, IOException {
		super(fileName, layerName, defaultValue, translator);
	}

	@Override
	protected GridValueLayer getValueLayer(String layerName, double defaultValue,
				GridPointTranslator translator) {
		return new GridValueLayer(layerName, defaultValue, true, translator, new int[]{width, height});
	}
	
	@Override
	protected void applyRasterToValueLayer(Raster raster) {
		// assumes raster only has one band (index 0)
		for (int i=0; i<width; i++) {
			for (int j=0; j<height; j++) {
				valueLayer.set(raster.getSampleDouble(i, j, 0), i, j);
			}
		}
	}

	@Override
	public String getName() {
		return valueLayer.getName();
	}

	@Override
	public double get(double... coordinates) {
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
