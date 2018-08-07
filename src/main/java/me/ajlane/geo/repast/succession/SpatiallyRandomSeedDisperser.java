/**
 * 
 */
package me.ajlane.geo.repast.succession;

import repast.simphony.context.Context;
import repast.simphony.valueLayer.GridValueLayer;

/**
 * Number of <strong>new</strong> seeds given by N_{\sigma} - No cells currently
 * occupied by seeds of this species. 
 * 
 * 
 * @author andrew
 *
 */
public class SpatiallyRandomSeedDisperser extends SeedDisperser {
	
	SeedViabilityMonitor svm;
	
	/**@param xCellSize
	 * 		Horizontal length of raster grid cells
	 * @param yCellSize 
	 * 		Vertical length of raster grid cells
	 * @param pineSeedLifetime
	 * 		The lifetime (in model time units) of pine seeds
	 * @param oakSeedLifetime
	 * 		The lifetime (in model time units) of oak seeds
	 * @param deciduousSeedLifetime
	 * 		The lifetime (in model time units) of deciduous seeds
	 * @param context
	 * 		Repast Context the SpatiallyRandomSeedDisperser belongs to.
	 * 		This is used in the constructor to retrieve references to the 
	 * 		GridValueLayer-s storing the spatial configuration of each type
	 * 		of seed.
	 */
	SpatiallyRandomSeedDisperser(float xCellSize, float yCellSize, int pineSeedLifetime, 
			int oakSeedLifetime, int deciduousSeedLifetime, Context<Object> context){
		
		landCoverType = (GridValueLayer)context.getValueLayer("lct");
		
		pineSeeds = (GridValueLayer)context.getValueLayer("pine seeds");
		oakSeeds = (GridValueLayer)context.getValueLayer("oak seeds");
		deciduousSeeds = (GridValueLayer)context.getValueLayer("deciduous seeds");
		
		// check GridValueLayer-s are valid and extract grid dimensions
		checkValueLayersAccessible();
		checkValueLayerDimensionsMatch();
		processGridShape(xCellSize, yCellSize);		
	}
	
	/**
	 * @param speciesName
	 * 		Species (pine, oak, deciduous) whose seed sources we want to count.
	 * @return
	 * 		Number of seed source pixels for given species
	 */
	int getSeedSourceCount(String speciesName) {
		int seedSourceCount = 0; 		
		for (int i=0; i<height; i++) {
			for (int j=0; j<width; j++) {
				if (seedSourceMap.get(speciesName).contains(landCoverType.get(i, j))) {
					seedSourceCount++;
				}
			}
		}
		
		return seedSourceCount;		
	}
	
	/**
	 * <emph>Assuming</emph> a spatially uniform distribution of seed sources
	 * across the spatial grid, calculate the average distance between a
	 * seed source and a non-seed source cell. This is given as:
	 * \[
	 * d_{\sigma} = \frac{N_{\sigma}}{2\sqrt{N_{tot}}} l
	 * \]
	 * where $N_{\sigma}$ is the number of seed sources for species $\sigma$ and
  	 * $N_{tot}$ is the total number of model cells.
	 * 
	 * @param speciesName
	 * 		Name of species for which we want to calculate the typical
	 * 		distance from one of its seed sources
	 * @return
	 * 		Typical distance between a seed source of {@code speciesName},
	 * 		and a cell which is not a source of that species, assuming 
	 * 		seed sources are evenly and uniformly distributed in the 
	 * 		model grid. 
	 * 		
	 */
	double typicalDistanceToSeedSource(String speciesName) {
		int numSeedSources = getSeedSourceCount(speciesName);
		return 0.5 * numSeedSources * cellSize / Math.sqrt(n);
	}

	/* (non-Javadoc)
	 * @see me.ajlane.geo.repast.succession.SeedDisperser#updateSeedLayers()
	 */
	@Override
	void updateSeedLayers() {
		// TODO Auto-generated method stub

	}

}
