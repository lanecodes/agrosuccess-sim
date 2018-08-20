package repast.model.agrosuccess;

import java.io.File;

import me.ajlane.geo.repast.succession.SeedDisperser;
import me.ajlane.geo.repast.succession.SoilMoistureCalculator;
import me.ajlane.geo.repast.succession.SpatiallyRandomSeedDisperser;
import repast.simphony.context.Context;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;
import repast.simphony.space.grid.StrictBorders;
import repast.simphony.valueLayer.GridValueLayer;

public class AgroSuccessContextBuilder implements ContextBuilder<Object> {
	/* (non-Javadoc)
	 * @see repast.simphony.dataLoader.ContextBuilder#build(repast.simphony.context.Context)
	 */
	
	// pseudo-agents modify GridValueLayer-s
	SoilMoistureCalculator soilMoistureCalculator;
	SeedDisperser seedDisperser;	
	
	GridValueLayer soilMoisture; // name = "soil moisture"
	GridValueLayer soilTypeMap; //see SoilMoistureCalculator, name = "soil"
	GridValueLayer landCoverTypeMap; // name = "lct"
	
	GridValueLayer slopeMap; // name = "slope"
	
	GridValueLayer pineSeeds; // name = "pine seeds"
	GridValueLayer oakSeeds; // name = "oak seeds"
	GridValueLayer deciduousSeeds; // name = "deciduous seeds"
	
	int[] gridOrigin = new int[]{0,0}; // vector space origin for all spatial grids	

	public Context<Object> build(Context<Object> context) {
		  
		Parameters params = RunEnvironment.getInstance().getParameters();
		  
		// directory containing study site-specific data needed to run simulations
		File siteGeoDataDir = new File((String)params.getValue("geoDataDirRootString"), 
				(String)params.getValue("studySiteNameString"));
		  
		StudySiteDataContainer studySiteData = new StudySiteDataContainer(siteGeoDataDir);
		
		int[] gridDimensions = studySiteData.getGridDimensions();
		double[] gridPixelSize = studySiteData.getGridCellPixelSize();
		
		// initialise GridValueLayer-s 
		soilMoisture = new GridValueLayer("soil moisture", 0, true, new StrictBorders(), 
				gridDimensions, gridOrigin);
		context.addValueLayer(soilMoisture);
		
		soilTypeMap = studySiteData.getSoilTypeMap("soil");
		context.addValueLayer(soilTypeMap);
		
		landCoverTypeMap = studySiteData.getLandCoverTypeMap("lct");
		context.addValueLayer(landCoverTypeMap);
		
		slopeMap = studySiteData.getSlope("slope");
		context.addValueLayer(slopeMap);
		
		pineSeeds = new GridValueLayer("pine seeds", 0, true, new StrictBorders(), 
				gridDimensions, gridOrigin);
		context.addValueLayer(pineSeeds);
		
		oakSeeds = new GridValueLayer("oak seeds", 0, true, new StrictBorders(), 
				gridDimensions, gridOrigin);
		context.addValueLayer(oakSeeds);
		
		deciduousSeeds = new GridValueLayer("deciduous seeds", 0, true, 
				new StrictBorders(), gridDimensions, gridOrigin);
		context.addValueLayer(deciduousSeeds);
		
		// initialise pseudo agents
		soilMoistureCalculator = new SoilMoistureCalculator(
				studySiteData.getFlowConnectivityNetwork(), 0, context);
		
		seedDisperser = new SpatiallyRandomSeedDisperser(gridPixelSize[0], 
				gridPixelSize[1], 9, 9, 9, context);	
		
		// TODO still need LCTTransDecider/ LandCoverUpdater

		return context;
	}

}
