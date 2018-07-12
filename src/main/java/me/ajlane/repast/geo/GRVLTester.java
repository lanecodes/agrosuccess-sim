package me.ajlane.repast.geo;

import java.io.IOException;

import org.geotools.data.DataSourceException;

import repast.simphony.space.grid.StrictBorders;

public class GRVLTester {


	public static void main(String[] args) throws DataSourceException, IOException {
		GeoRasterValueLayer dem;
		dem = new GeoRasterValueLayer("/home/andrew/Dropbox/codes/python/notebooks/download_site_elevation_data/data/navarres.tif", 
				"navarres-dem", -99999, new StrictBorders());
		System.out.println(dem.getDimensions());
		System.out.println(dem.get(200,200));
		System.out.println(dem.get(200,250));
		
	}

}
