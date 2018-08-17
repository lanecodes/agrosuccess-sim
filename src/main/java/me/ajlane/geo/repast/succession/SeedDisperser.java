/**
 * 
 */
package me.ajlane.geo.repast.succession;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

	GridValueLayer landCoverType;
	
	// track seeds in model
	GridValueLayer pineSeeds, oakSeeds, deciduousSeeds;
	SeedViabilityMonitor svm;
	
	// track model time step
	int time;
	
	// maps seed source names (pine, oak, deciduous) to set of IDs
	Map<String,Set<Integer>> seedSourceMap = getSeedSourceMap();
	
	int height; // number of cells vertically
	int width; // number of cells horizontally
	int n; // number of cells
	double cellSize; // extent of raster grid cells in grid units, e.g. meters
	
	double minProb; // minimum probability for a cell to contain a species' seeds
	// objects which generate probability of seed of each dispersal type (acorn vs wind
	// dispersed being found in a cell.
	AcornPresenceProbGenerator acornPresenceProbGenerator;	
	WindSeedPresenceProbGenerator windSeedPresenceProbGenerator;

	void checkValueLayersAccessible() {
		try {
			pineSeeds.getName();
		} catch (NullPointerException e) {
			System.out.println(
					"SeedDisperser could not find 'pine seeds' layer in context");
		}

		try {
			oakSeeds.getName();
		} catch (NullPointerException e) {
			System.out.println(
					"SeedDisperser could not find 'oak seeds' layer in context");
		}

		try {
			deciduousSeeds.getName();
		} catch (NullPointerException e) {
			System.out.println(
					"SeedDisperser could not find 'deciduous seeds' layer in context");
		}

		try {
			landCoverType.getName();
		} catch (NullPointerException e) {
			System.out.println(
					"SeedDisperser could not find 'lct' layer in context");
		}

	}
	
	/**
	 * @param ar
	 * 		Array whose elements will be added to the set
	 * @return
	 * 		Set whose elements were in ar
	 */
	Set<Integer> intArrayToSet(int[] ar) {
		Set<Integer> set = new HashSet<Integer>();		
		for (int i=0; i<ar.length; i++) {
			set.add(ar[i]);
		}
		return set;
	}
	
	/**
	 * landCoverType
	 * 		0 = Water/ Quarry
	 * 		1 = Burnt
	 * 		2 = Barley
	 *		3 = Wheat
	 *		4 = Depleted agricultural land
	 *		5 = Shrubland
	 *		6 = Pine forest
	 *		7 = Transition forest
	 *		8 = Deciduous forest
	 *		9 = Oak forest	  
	 */
	Map<String,Set<Integer>> getSeedSourceMap() {
		Map<String,Set<Integer>> map = new HashMap<String,Set<Integer>>();
		map.put("pine", intArrayToSet(new int[] {6, 7}));
		map.put("oak", intArrayToSet(new int[] {9, 7}));
		map.put("deciduous", intArrayToSet(new int[] {8, 7}));
		return map;
	}

	void checkValueLayerDimensionsMatch() {
		int[] pineDims = { (int) pineSeeds.getDimensions().getWidth(),
				(int) pineSeeds.getDimensions().getHeight() };

		int[] oakDims = { (int) oakSeeds.getDimensions().getWidth(),
				(int) oakSeeds.getDimensions().getHeight() };

		int[] deciduousDims = { (int) deciduousSeeds.getDimensions().getWidth(),
				(int) deciduousSeeds.getDimensions().getHeight() };

		int[] landCoverTypesDims = {
				(int) landCoverType.getDimensions().getWidth(),
				(int) landCoverType.getDimensions().getHeight() };

		if (!Arrays.equals(pineDims, oakDims)
				|| !Arrays.equals(pineDims, deciduousDims)
				|| !Arrays.equals(pineDims, landCoverTypesDims)) {
			throw new IllegalArgumentException(
					"Dimensions of seed and land cover type "
							+ "GridValueLayer-s don't match.");
		}
	}
	
	/**
	 * @param x
	 * 		Length in x dimension 		
	 * @param y
	 * 		Length in y dimension
	 * @return
	 * 		Geometric mean in 2-dimensions
	 */
	double geometricMean(double x, double y) {
		return Math.sqrt(x*y);
	}

	void processGridShape(double xCellSize, double yCellSize) {
		height = (int) landCoverType.getDimensions().getHeight();
		width = (int) landCoverType.getDimensions().getWidth();
		n = height * width;
		cellSize = geometricMean(xCellSize, yCellSize);
	}

	abstract void updateSeedLayers();

}
