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
import org.apache.commons.math3.distribution.LogNormalDistribution;
import org.apache.commons.math3.distribution.RealDistribution;
import org.apache.commons.math3.distribution.ExponentialDistribution;

/**
 * Abstract class which sets the scene for classes which distribute seeds in
 * Repast GridValueLayer-s which store the presence of pine, oak, and deciduous
 * seeds.
 * 
 * <strong>Seed dispersal kernels</strong> 
 * The probability of an Oak acorn being present in a cell at a given time is specified by the 
 * lognormal distribution where x is the distance from the cell to the nearest oak-containing pixel: 
 * \[ 
 * p(x) = \frac{1}{x \sigma \sqrt{2 \pi}} \exp\left[- \frac{(\ln(x) - \mu)^2}{2\sigma^2}\right] 
 * \]
 * 
 * Following Millington et al. (2009) we set $\sigma = 2.34m$ and $\mu = 46.7m$
 * 
 * The probability of a wind dispersed seed being found in a cell at a given time is
 * determined by the following probability-assigning procedure:
 * 
 *  p(x) = 0.95 if x <= ED
 *  p(x) = b/MD * \exp^{-b/MD * x} if  ED<x<=MD
 *  p(x) = 0.001 if x>MD 
 * 
 * Following Millington et al. (2009) we set $b=5$ is the distance-decrease parameter,
 * $ED=75$m is the minimum distance for which seed dispersal is exponentially 
 * distributed, and $MD=100$m is the maximum distance for which seeds are exponentially
 * distributed. 
 * 
 * <strong>References</strong> 
 * Millington, J. D. A., Wainwright, J., Perry, G.
 * L. W., Romero-Calcerrada, R., & Malamud, B. D. (2009). Modelling
 * Mediterranean landscape succession-disturbance dynamics: A landscape
 * fire-succession model. Environmental Modelling and Software, 24(10),
 * 1196–1208. https://doi.org/10.1016/j.envsoft.2009.03.013
 * 
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
	
	// acorn distribution parameters
	private double acornMean = 46.7;
	private double acornStd = 2.34;	
	private double maxLognormalDistance = 550; // maximum distance from seed source for probability to be lognormal
	RealDistribution acornLogNormalDistribution = new LogNormalDistribution(acornMean, acornStd);	
	
	// wind distributed seed parameters
	private double distanceDecreaseParam = 5;
	private double minExponentialDistance = 75;
	private double maxExponentialDistance = 100;
	RealDistribution windExpDistribution = new ExponentialDistribution(distanceDecreaseParam/ maxExponentialDistance);

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
	
	/**
	 * See Millington2009
	 * @param distToClosestSeedSource
	 * @return
	 */
	double acornProbability(double distToClosestSeedSource) {
		if (distToClosestSeedSource <= maxLognormalDistance){
			return acornLogNormalDistribution.density(distToClosestSeedSource);			
		} else {
			return minProb;
		}		
	}
	
	/**
	 * See Millington2009
	 * @param distToClosestSeedSource
	 * @return
	 */
	double windDispersedProbability(double distToClosestSeedSource) {
		if (distToClosestSeedSource <= minExponentialDistance) {
			return 0.95;
		} else if (distToClosestSeedSource <= maxExponentialDistance) {
			return windExpDistribution.density(distToClosestSeedSource);
		} else {
			return minProb;
		}
	}

	abstract void updateSeedLayers();

}
