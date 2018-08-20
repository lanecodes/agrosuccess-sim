/**
 * 
 */
package repast.model.agrosuccess;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.geotools.factory.Hints;
import org.geotools.gce.geotiff.GeoTiffReader;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import me.ajlane.geo.FlowConnectivityNetwork;
import me.ajlane.geo.repast.GeoRasterValueLayer;
import repast.simphony.space.Dimensions;
import repast.simphony.valueLayer.GridValueLayer;

/**
 * Helper class used to handle the retrieval of study site specific data and 
 * parameters
 * 
 * @author andrew
 *
 */
public class StudySiteDataContainer {
	
	File siteDataDir;
	private Document parsedXMLDocument;
	
	StudySiteDataContainer(File siteDataDir) {
		this.siteDataDir = siteDataDir;
		this.parsedXMLDocument = parseXMLFile(siteDataDir);
	}

	private static Document parseXMLFile(File fileName) {
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		   
		try {
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			return documentBuilder.parse(fileName);
		} catch (ParserConfigurationException e) {
			System.out.println("study site config site_parameters.xml parser incorrectly configured");
			throw new RuntimeException(e);
			
		} catch (IOException e) {
			System.out.println("could not read site_parameters.xml");
			throw new RuntimeException(e);
		} catch (SAXException e) {
			throw new RuntimeException(e);
		}		  
	  }
	
	public double getPrecipitation() {
		String valueString = parsedXMLDocument.getElementsByTagName("meanMonthlyPrecipitation").item(0).getTextContent();
		return Double.parseDouble(valueString);
	}
	
	public FlowConnectivityNetwork getFlowConnectivityNetwork() {
		File hydroCorrectDEM = new File(siteDataDir, "dem.tif");
		try {
			
			GeoTiffReader reader = new GeoTiffReader(hydroCorrectDEM, 
					new Hints(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, Boolean.TRUE));
			
			return new FlowConnectivityNetwork(reader.read(null));
		
		} catch (IOException e) {
			System.out.println("Couldn't read hydrologically correct DEM:\n" + 
					hydroCorrectDEM.getAbsolutePath() + 
					".\n Consequently it was not possible to construct a " +
					"flow connectivity network");
			throw new RuntimeException(e);
		}		
	}
	
	public GridValueLayer getSlope(String layerName) {
		GeoRasterValueLayer grvl = new GeoRasterValueLayer(
				new File(siteDataDir, "dem-slope.tif").getAbsolutePath(), 
				layerName);
		
		return grvl.getValueLayer();
	}
	
	public GridValueLayer getSoilTypeMap(String layerName){
		GeoRasterValueLayer grvl = new GeoRasterValueLayer(
				new File(siteDataDir, "soil-type.tif").getAbsolutePath(), 
				layerName);
		
		return grvl.getValueLayer();
	}
	
	public GridValueLayer getLandCoverTypeMap(String layerName){
		GeoRasterValueLayer grvl = new GeoRasterValueLayer(
				new File(siteDataDir, "land-cover-type.tif").getAbsolutePath(), 
				layerName);
		
		return grvl.getValueLayer();
	}
	
	public int[] getGridDimensions() {
		//TODO should do some checking to make sure the dimensions of all the
		// loaded value layers match
		Dimensions slopeDimensions = getSlope("slope").getDimensions();
		return new int[]{(int)slopeDimensions.getWidth(), (int)slopeDimensions.getHeight()};
	}
	
	/** (x,y) dimensions of the grid cell pixels, retrieved from site_parameters.xml
	 * 	these will me in meters 
	 * @return
	 * 		[Xpixel size, Ypixel size]
	 */
	public double[] getGridCellPixelSize() {
		String valueStringX = parsedXMLDocument.getElementsByTagName(
				"gridCellPixelSizeX").item(0).getTextContent();
		String valueStringY = parsedXMLDocument.getElementsByTagName(
				"gridCellPixelSizeY").item(0).getTextContent();
		
		return new double[]{Double.parseDouble(valueStringX), Double.parseDouble(valueStringY)};
	}
	
}
