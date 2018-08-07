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
	float pineSeedDensity, oakSeedDensity, deciduousSeedDensity;
	
	/**
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
	SpatiallyRandomSeedDisperser(int pineSeedLifetime, float pineSeedDensity,
			int oakSeedLifetime, float oakSeedDensity, 
			int deciduousSeedLifetime, float deciduousSeedDensity,
			Context<Object> context){
		
		this.pineSeedDensity = pineSeedDensity;
		this.oakSeedDensity = oakSeedDensity;
		this.deciduousSeedDensity = deciduousSeedDensity;		
		
		landCoverType = (GridValueLayer)context.getValueLayer("lct");
		
		pineSeeds = (GridValueLayer)context.getValueLayer("pine seeds");
		oakSeeds = (GridValueLayer)context.getValueLayer("oak seeds");
		deciduousSeeds = (GridValueLayer)context.getValueLayer("deciduous seeds");
		
		checkValueLayersAccessible();
		checkValueLayerDimensionsMatch();
		processGridShape();
		
		
	}

	/* (non-Javadoc)
	 * @see me.ajlane.geo.repast.succession.SeedDisperser#updateSeedLayers()
	 */
	@Override
	void updateSeedLayers() {
		// TODO Auto-generated method stub

	}

}
