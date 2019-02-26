package repast.model.agrosuccess;

import java.io.File;
import org.neo4j.graphdb.GraphDatabaseService;
import me.ajlane.geo.repast.seeddispersal.SeedDispersalParams;
import me.ajlane.geo.repast.seeddispersal.SeedDisperser;
import me.ajlane.geo.repast.seeddispersal.SeedViabilityParams;
import me.ajlane.geo.repast.seeddispersal.SpatiallyRandomSeedDisperser;
import me.ajlane.geo.repast.soilmoisture.AgroSuccessSoilMoistureDiscretiser;
import me.ajlane.geo.repast.soilmoisture.SoilMoistureCalculator;
import me.ajlane.geo.repast.soilmoisture.SoilMoistureDiscretiser;
import me.ajlane.geo.repast.soilmoisture.SoilMoistureParams;
import me.ajlane.geo.repast.succession.AgroSuccessEnvrStateAliasTranslator;
import me.ajlane.geo.repast.succession.AgroSuccessLcsUpdateDecider;
import me.ajlane.geo.repast.succession.CodedLcsTransitionMap;
import me.ajlane.geo.repast.succession.EnvrStateAliasTranslator;
import me.ajlane.geo.repast.succession.GraphBasedLcsTransitionMapFactory;
import me.ajlane.geo.repast.succession.LcsTransitionMapFactory;
import me.ajlane.geo.repast.succession.LcsUpdateDecider;
import me.ajlane.geo.repast.succession.LcsUpdater;
import me.ajlane.neo4j.EmbeddedGraphInstance;
import repast.simphony.context.Context;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;
import repast.simphony.space.grid.StrictBorders;
import repast.simphony.valueLayer.GridValueLayer;

public class AgroSuccessContextBuilder implements ContextBuilder<Object> {
  /*
   * (non-Javadoc)
   * 
   * @see repast.simphony.dataLoader.ContextBuilder#build(repast.simphony.context.Context)
   */

  // pseudo-agents modify GridValueLayer-s
  SoilMoistureCalculator soilMoistureCalculator;
  SeedDisperser seedDisperser;

  GridValueLayer soilMoisture; // name = "soil moisture"
  GridValueLayer soilTypeMap; // see SoilMoistureCalculator, name = "soil"
  GridValueLayer landCoverTypeMap; // name = "lct"

  GridValueLayer slopeMap; // name = "slope"
  GridValueLayer aspect; // name = "aspect"

  GridValueLayer pineSeeds; // name = "pine seeds"
  GridValueLayer oakSeeds; // name = "oak seeds"
  GridValueLayer deciduousSeeds; // name = "deciduous seeds"

  int[] gridOrigin = new int[] {0, 0}; // vector space origin for all spatial grids
  
  /**
   * Generate required {@code repast.simphony.valueLayer.GridValueLayer} objects representing
   * landscape variables and make the Repast context aware of them.
   * 
   * @param context
   * @param studySiteData
   */
  private Context<Object> initialiseGridValueLayers(Context<Object> context, 
      StudySiteDataContainer studySiteData) {
    
    int[] gridDimensions = studySiteData.getGridDimensions();

    GridValueLayer soilMoisture = new GridValueLayer(LscapeLayer.SoilMoisture.name(), 0, true, 
        new StrictBorders(), gridDimensions, gridOrigin);
    context.addValueLayer(soilMoisture);

    soilTypeMap = studySiteData.getSoilTypeMap(); // "soil"
    context.addValueLayer(soilTypeMap);

    landCoverTypeMap = studySiteData.getLandcoverTypeMap(); // "lct"
    context.addValueLayer(landCoverTypeMap);

    slopeMap = studySiteData.getSlopeMap();
    context.addValueLayer(slopeMap);

    aspect = studySiteData.getAspectMap(); // "aspect"
    context.addValueLayer(aspect);

    pineSeeds = new GridValueLayer(LscapeLayer.Pine.name(), 0, true, new StrictBorders(), 
        gridDimensions, gridOrigin);
    context.addValueLayer(pineSeeds);

    oakSeeds = new GridValueLayer(LscapeLayer.Oak.name(), 0, true, new StrictBorders(), 
        gridDimensions, gridOrigin);
    context.addValueLayer(oakSeeds);

    deciduousSeeds = new GridValueLayer(LscapeLayer.Deciduous.name(), 0, true, 
        new StrictBorders(), gridDimensions, gridOrigin);
    context.addValueLayer(deciduousSeeds);
    
    return context;
  }
  
  /**
   * Initialise the seed disperser agent and make it aware of the Repast context. This modifies the
   * seed presence layers in the model based on the location of seed generating sources.
   * 
   * @param context
   * @param studySiteData
   * @param seedViabilityParams
   * @param seedDispersalParams
   * @return
   */
  private SeedDisperser initialiseSeedDisperser(Context<Object> context, 
      StudySiteDataContainer studySiteData, SeedViabilityParams seedViabilityParams, 
      SeedDispersalParams seedDispersalParams) {
    
    double[] gridPixelSize = studySiteData.getGridCellPixelSize();       
      
    seedDisperser = new SpatiallyRandomSeedDisperser(gridPixelSize[0], gridPixelSize[1], 
        seedViabilityParams, seedDispersalParams, context); 
    return seedDisperser;
  }
  
  /**
   * Initialise the soil moisture calculator and make it aware of the Repast context. This will 
   * Update the spatially varying soil moisture in the landscape in response to precipitation.
   * 
   * @param context
   * @param studySiteData
   * @return
   */
  private SoilMoistureCalculator initialiseSoilMoistureCalculator(Context<Object> context, 
      StudySiteDataContainer studySiteData) {
    // TODO Implement SiteBoundaryConds.getFlowDirMap() 
    //soilMoistureCalculator = 
    //    new SoilMoistureCalculator(studySiteData.getFlowDirMap(), 0, context);  
    return null;
    
  }
  
  /**
   * Initialise the land cover state updater (take account of evolving environmental state variables
   * and update land cover in response) and make it aware of the Repast context.
   * 
   * @param context
   * @param databaseDir
   * @param modelID
   * @param soilMoistureParams
   * @return
   */
  private LcsUpdater initialiseLcsUpdater(Context<Object> context, File databaseDir, String modelID, 
      SoilMoistureParams soilMoistureParams) {
    
    GraphDatabaseService graph = new EmbeddedGraphInstance(databaseDir.getName()); 
    EnvrStateAliasTranslator translator = new AgroSuccessEnvrStateAliasTranslator(); 
    LcsTransitionMapFactory fac = new GraphBasedLcsTransitionMapFactory(graph, modelID,
        translator); 
    CodedLcsTransitionMap codedMap = fac.getCodedLcsTransitionMap(); 
    LcsUpdateDecider updateDecider = new AgroSuccessLcsUpdateDecider(codedMap);

    SoilMoistureDiscretiser smDiscretiser = 
        new AgroSuccessSoilMoistureDiscretiser(soilMoistureParams);

    LcsUpdater lcsUpdater = new AgroSuccessLcsUpdater(context, updateDecider, smDiscretiser); 
    return lcsUpdater;
  }
  

  public Context<Object> build(Context<Object> context) {

    Parameters params = RunEnvironment.getInstance().getParameters();
    
    // TODO Add parameters required by ModelParamsRepastParser to parameters.xml
    // EnvrModelParams envrModelParams = new ModelParamsRepastParser(params);

    // directory containing study site-specific data needed to run simulations
    File siteGeoDataDir = new File((String)params.getValue("geoDataDirRootString"), 
        (String)params.getValue("studySite"));
    
    // TODO add databaseDir parameter to paramaters.xml
    // File databaseDir = new File((String)params.getValue("databaseDir"));
    File databaseDir = new File("~/graphs/databases/prod.db");

    StudySiteDataContainer studySiteData = new StudySiteDataContainer(siteGeoDataDir);      
    
    context = initialiseGridValueLayers(context, studySiteData);
    
    // TODO Update seedDispersalParams and seedViabilityParams so they're read from config file
    initialiseSeedDisperser(context, studySiteData, new SeedViabilityParams(7), 
        new SeedDispersalParams(3.844, 0.851, 550, 5, 75, 100));
    
    initialiseSoilMoistureCalculator(context, studySiteData);
    
    // TODO update soilMoistureParams so it's read from config file
    initialiseLcsUpdater(context, databaseDir, "AgroSuccess-dev", 
        new SoilMoistureParams(500, 1000));

    return context;
  }
  


}
