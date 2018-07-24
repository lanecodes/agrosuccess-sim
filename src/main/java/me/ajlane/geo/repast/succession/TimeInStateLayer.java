/**
 * 
 */
package me.ajlane.geo.repast.succession;

import java.io.IOException;

import org.geotools.data.DataSourceException;

import me.ajlane.geo.repast.GeoRasterValueLayer;
import repast.simphony.space.grid.GridPointTranslator;

/**
 * @author andrew
 *
 */
public class TimeInStateLayer extends GeoRasterValueLayer {

	/**
	 * @param fileName
	 * @param layerName
	 * @param defaultValue
	 * @param translator
	 * @throws DataSourceException
	 * @throws IOException
	 */
	public TimeInStateLayer(String fileName, String layerName,
			double defaultValue, GridPointTranslator translator)
			throws DataSourceException, IOException {
		super(fileName, layerName, defaultValue, translator);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param fileName
	 * @param layerName
	 * @throws DataSourceException
	 * @throws IOException
	 */
	public TimeInStateLayer(String fileName, String layerName)
			throws DataSourceException, IOException {
		super(fileName, layerName);
		// TODO Auto-generated constructor stub
	}

}
