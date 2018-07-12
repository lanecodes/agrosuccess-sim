package me.ajlane.repast.geo;

import java.awt.image.Raster;

import org.geotools.geometry.jts.ReferencedEnvelope;

/**
 * Simple convenience class whose objects act as containers for  
 * i. a raster image representing geographical raster data, and
 * ii. the corresponding geographical bounding envelope.
 * 
 * @author: Andrew Lane
 */
public class DisaggregatedGeoRaster {
	private ReferencedEnvelope envelope;
	private Raster raster;
	
	public DisaggregatedGeoRaster(ReferencedEnvelope env, Raster ras){
		this.envelope = env;
		this.raster = ras;		
	}
	
	public ReferencedEnvelope getEnvelope() {
		return this.envelope;
	}
	
	public Raster getRaster() {
		return this.raster;
	}

}
