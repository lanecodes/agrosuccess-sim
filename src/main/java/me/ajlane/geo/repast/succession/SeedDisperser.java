/**
 * 
 */
package me.ajlane.geo.repast.succession;

import java.util.Arrays;

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
	GridValueLayer pineSeeds, oakSeeds, deciduousSeeds;
	
	int height; // number of cells vertically
	int width; // number of cells horizontally
	int n; // number of cells
	
	// acorn distribution parameters
	double acornMean = 46.7;
	double acornStd = 2.34;	
	RealDistribution acornLogNormalDistribution = new LogNormalDistribution(acornMean, acornStd);	
	
	// wind distributed seed parameters
	double distanceDecreaseParam = 5;
	double minExponentialDistance = 75;
	double maxExponentialDistance = 100;
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

	void processGridShape() {
		height = (int) landCoverType.getDimensions().getHeight();
		width = (int) landCoverType.getDimensions().getWidth();
		n = height * width;
	}
	
	double acornProbability(double distToClosestSeedSource) {
		return acornLogNormalDistribution.density(distToClosestSeedSource);
	}
	
	double windDispersedProbability(double distToClosestSeedSource) {
		if (distToClosestSeedSource <= minExponentialDistance) {
			return 0.95;
		} else if (distToClosestSeedSource <= maxExponentialDistance) {
			return windExpDistribution.density(distToClosestSeedSource);
		} else {
			return 0.001;
		}
	}

	abstract void updateSeedLayers();

}
