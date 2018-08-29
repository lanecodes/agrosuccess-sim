/**
 * 
 */
package repast.model.agrosuccess;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

//import org.geotools.factory.Hints;
//import org.geotools.gce.geotiff.GeoTiffReader;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

//import me.ajlane.geo.FlowConnectivityNetwork;
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
	private Document parsedXML;
	
	/**
	 * @param siteDataDir
	 * 		The directory in which the data (geo and otherwise) required for 
	 * 		a <emph>specific</emph> study site resides.
	 */
	StudySiteDataContainer(File siteDataDir) {
		this.siteDataDir = siteDataDir;
		this.parsedXML = parseXMLFile(new File(siteDataDir, "site_parameters.xml"));
	}

	private static Document parseXMLFile(File fileName) {
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		   
		try {
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			return documentBuilder.parse(fileName);
		} catch (ParserConfigurationException e) {
			System.out.println("study site config site_parameters.xml parser "+
							   "incorrectly configured");
			throw new RuntimeException(e);
			
		} catch (IOException e) {
			System.out.println("could not read site_parameters.xml");
			throw new RuntimeException(e);
		} catch (SAXException e) {
			throw new RuntimeException(e);
		}		  
	  }
	
	/**
	 * Internal helper method
	 * 
	 * @param filenameTag
	 * 		The XML tag which specifies the geotiff file in question, e.g. 
	 * 		"digitalElevationModel"
	 * @return
	 * 		The geotiff file associated with the given XML tag factoring 
	 * 		in the structure of site_parameters.xml.
	 * 
	 */
	private File getGeoTiffFile(String filenameTag) {
		String geotiffFilename = parsedXML.getElementsByTagName(filenameTag)
										  .item(0).getTextContent();
		return new File(siteDataDir, geotiffFilename);
		
	}
	
	public GridValueLayer getSlopeMap() {
		File file = getGeoTiffFile("slope");
		GeoRasterValueLayer grvl = 
				new GeoRasterValueLayer(file.getAbsolutePath(), "slope");
		
		return grvl.getValueLayer();
	}
	
	public GridValueLayer getAspectMap() {
		File file = getGeoTiffFile("binaryAspect");
		GeoRasterValueLayer grvl = 
				new GeoRasterValueLayer(file.getAbsolutePath(), "aspect");
		
		return grvl.getValueLayer();
	}
	
	public GridValueLayer getFlowDirectionMap(){
		File file = getGeoTiffFile("flowDirection");
		GeoRasterValueLayer grvl = 
				new GeoRasterValueLayer(file.getAbsolutePath(), "flow direction");
		
		return grvl.getValueLayer();
	}
	
	public GridValueLayer getSoilTypeMap() {
		File file = getGeoTiffFile("soilType");
		GeoRasterValueLayer grvl = 
				new GeoRasterValueLayer(file.getAbsolutePath(), "soil");
		
		return grvl.getValueLayer();
	}
	
	public GridValueLayer getLandcoverTypeMap(){
		File file = getGeoTiffFile("initialLandcover");
		GeoRasterValueLayer grvl = 
				new GeoRasterValueLayer(file.getAbsolutePath(), "lct");
		
		return grvl.getValueLayer();
	}		
	
	public double getPrecipitation() {
		String precipitationValueString = parsedXML
				.getElementsByTagName("meanMonthlyPrecipitation")
				.item(0).getTextContent();
		return Double.parseDouble(precipitationValueString);
	}
	
	/* DEPRECIATED. FlowConnectivityNetwork approach failed because network
	 * grid required indices greater than Java's maximal int value.
	 * 
	public FlowConnectivityNetwork getFlowConnectivityNetwork() {
		File flowDir = getGeoTiffFile("flowDirection");
		try {
			
			GeoTiffReader reader = new GeoTiffReader(flowDir, 
					new Hints(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, Boolean.TRUE));
			
			return new FlowConnectivityNetwork(reader.read(null));
		
		} catch (IOException e) {
			System.out.println("Couldn't read hydrologically correct DEM:\n" + 
					flowDir.getAbsolutePath() + 
					".\n Consequently it was not possible to construct a " +
					"flow connectivity network");
			throw new RuntimeException(e);
		}		
	}
	*/
	
	public int[] getGridDimensions() {
		//TODO should do some checking to make sure the dimensions of all the
		// loaded value layers match
		Dimensions slopeDimensions = getSlopeMap().getDimensions();
		return new int[]{(int)slopeDimensions.getWidth(), (int)slopeDimensions.getHeight()};
	}
	
	/** (x,y) dimensions of the grid cell pixels, retrieved from site_parameters.xml
	 * 	these will me in meters 
	 * @return
	 * 		[Xpixel size, Ypixel size]
	 */
	public double[] getGridCellPixelSize() {
		String valueStringX = parsedXML
				.getElementsByTagName("gridCellPixelSizeX")
				.item(0).getTextContent();
		
		String valueStringY = parsedXML
				.getElementsByTagName("gridCellPixelSizeY")
				.item(0).getTextContent();
		
		return new double[]{Double.parseDouble(valueStringX), Double.parseDouble(valueStringY)};
	}
	
}
