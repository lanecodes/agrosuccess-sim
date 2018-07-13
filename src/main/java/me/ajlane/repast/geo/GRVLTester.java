package me.ajlane.repast.geo;

import java.io.IOException;

import org.geotools.data.DataSourceException;

import repast.simphony.context.Context;
import repast.simphony.context.DefaultContext;
import repast.simphony.valueLayer.IGridValueLayer;

public class GRVLTester {
	
	static Context<Object> ctxt = new DefaultContext<Object>("landscape");
	static String geoDataRoot = "/home/andrew/Dropbox/codes/python/notebooks/download_site_elevation_data/data/";

	public static void main(String[] args) throws DataSourceException, IOException {
		
		
		// test we can get and set values on a GeoRasterValueLayer
		ctxt.addValueLayer(new GeoRasterValueLayer(geoDataRoot + "navarres.tif", "navarres-dem"));
		ctxt.addValueLayer(new BufferedGeoRasterValueLayer(geoDataRoot + "navarres.tif", "navarres-dem-buff"));
		
		System.out.println("Grid dimensions:\n" + ctxt.getValueLayer("navarres-dem").getDimensions());
		System.out.println("Value at point 200,200\n" + ctxt.getValueLayer("navarres-dem").get(200,200));
		System.out.println("Setting value of point 200, 200 to 75.0 ...");
		((IGridValueLayer)ctxt.getValueLayer("navarres-dem")).set(75.0, 200, 200);
		System.out.println("Checking new value of point 200, 200...\n" + ctxt.getValueLayer("navarres-dem").get(200,200));		
		
		//  BufferedGeoRasterValueLayer tests
		BufferedGeoRasterValueLayer buffDem  = (BufferedGeoRasterValueLayer)ctxt.getValueLayer("navarres-dem-buff");
		
		System.out.println("\nBufferedGeoRasterValueLayer tests\nValue at 200,200:");
		System.out.println(buffDem.get(200,200));
		System.out.println("Setting value of cell 200, 200 to 50.0 ...");
		buffDem.set(50.0, 200, 200);
		System.out.println("Try to read without swapping buffers...");
		System.out.println(buffDem.get(200, 200));
		System.out.println("Swapping read/ write buffers...");
		buffDem.swap();
		System.out.println("Print new value, retrieving layer from context...");
		System.out.println(((BufferedGeoRasterValueLayer)ctxt.getValueLayer("navarres-dem-buff")).get(200, 200));
		
	}

}
