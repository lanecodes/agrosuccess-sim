/**
 * 
 */
package me.ajlane.geo.repast.succession;

import java.util.Arrays;

import repast.simphony.valueLayer.GridValueLayer;

/**
 * Abstract class which sets the scene for classes which distribute seeds in
 * Repast GridValueLayer-s which store the presence of pine, oak, and deciduous
 * seeds.
 * 
 * @author andrew
 *
 */
public abstract class SeedDisperser {
	
	private GridValueLayer landCoverTypes;
	
	private GridValueLayer pineSeeds;
	private GridValueLayer oakSeeds;
	private GridValueLayer deciduousSeeds;
	
	int height; // number of cells vertically
	int width; // number of cells horizontally
	int n; // number of cells
	
	void checkValueLayersAccessible() {
		try {
			pineSeeds.getName();
		} catch (NullPointerException e) {
			System.out.println("SeedDisperser could not find 'pine seeds' layer in context");
		}
		
		try {
			oakSeeds.getName();
		} catch (NullPointerException e) {
			System.out.println("SeedDisperser could not find 'oak seeds' layer in context");
		}
		
		try {
			deciduousSeeds.getName();
		} catch (NullPointerException e) {
			System.out.println("SeedDisperser could not find 'deciduous seeds' layer in context");
		}
		
		try {
			landCoverTypes.getName();
		} catch (NullPointerException e) {
			System.out.println("SeedDisperser could not find 'lct' layer in context");
		}
		
	}
	
	void checkValueLayerDimensionsMatch() {
		int[] pineDims = {(int)pineSeeds.getDimensions().getWidth(), 
						  (int)pineSeeds.getDimensions().getHeight()};
		
		int[] oakDims = {(int)oakSeeds.getDimensions().getWidth(), 
				  		 (int)oakSeeds.getDimensions().getHeight()};
		
		int[] deciduousDims = {(int)deciduousSeeds.getDimensions().getWidth(), 
				  			   (int)deciduousSeeds.getDimensions().getHeight()};
		
		int[] landCoverTypesDims = {(int)landCoverTypes.getDimensions().getWidth(), 
				  					(int)landCoverTypes.getDimensions().getHeight()};
		
		if (!Arrays.equals(pineDims, oakDims) || !Arrays.equals(pineDims, deciduousDims) 
				|| !Arrays.equals(pineDims, landCoverTypesDims)) {
			throw new IllegalArgumentException("Dimensions of seed and land cover type " + 
											   "GridValueLayer-s don't match.");
		}		
	}
	
	void processGridShape() {
		height = (int)landCoverTypes.getDimensions().getHeight();
		width = (int)landCoverTypes.getDimensions().getWidth();
		n = height*width;
	}
	
	abstract void updateSeedLayers();	

}
