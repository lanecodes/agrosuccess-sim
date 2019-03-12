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
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.SimpleGridAdder;
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
   *   @Override
   * @param context
   * @param studySiteData
   */
  private void initialiseGridValueLayers(Context<Object> context, 
      SiteBoundaryConds studySiteData) {
    
    int[] gridDimensions = studySiteData.getGridDimensions();

    GridValueLayer soilMoisture = new GridValueLayer(LscapeLayer.SoilMoisture.name(), 0, true, 
        new StrictBorders(), gridDimensions, gridOrigin);
    context.addValueLayer(soilMoisture);

    soilTypeMap = studySiteData.getSoilMap(); // "soil"
    context.addValueLayer(soilTypeMap);

    landCoverTypeMap = studySiteData.getInitialLandCoverMap(); // "lct"
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
      SiteBoundaryConds studySiteData, SeedViabilityParams seedViabilityParams, 
      SeedDispersalParams seedDispersalParams) {
    
    double[] gridPixelSize = {(double)studySiteData.getGridPixelSize(), 
        (double)studySiteData.getGridPixelSize()};       
      
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
      SiteBoundaryConds studySiteData) {
    return new SoilMoistureCalculator(studySiteData.getFlowDirMap(), 
        studySiteData.getMeanAnnualPrecipitation(), context);    
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
      SoilMoistureParams soilMoistureParams, GraphDatabaseService graph) {
   
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
  
  private SiteBoundaryConds getSiteBoundaryConds() {
    File testDataDir = new File("src/test/resources");
    SiteBoundaryConds sbcs = new SiteBoundaryCondsHardCoded(50, 10, 
        new File(testDataDir, "dummy_51x51_lct_oak_pine_burnt.tif"),
        new File(testDataDir, "dummy_51x51_soil_type_uniform_A.tif" ),
        new File(testDataDir, "dummy_51x51_slope.tif"),
        new File(testDataDir, "dummy_51x51_binary_aspect.tif"),
        new File(testDataDir, "dummy_51x51_flowdir.tif"));
    return sbcs;
  }
  
  @Override
  public Context<Object> build(Context<Object> context) {

    Parameters params = RunEnvironment.getInstance().getParameters();
    
    // TODO Add parameters required by ModelParamsRepastParser to parameters.xml
    // EnvrModelParams envrModelParams = new ModelParamsRepastParser(params);

    // directory containing study site-specific data needed to run simulations
    //File siteGeoDataDir = new File((String)params.getValue("geoDataDirRootString"), 
    //    (String)params.getValue("studySite"));
    
    // TODO add databaseDir parameter to paramaters.xml
    // File databaseDir = new File((String)params.getValue("databaseDir"));
    File databaseDir = new File("/home/andrew/graphs/databases/prod.db");
    GraphDatabaseService graph = new EmbeddedGraphInstance(databaseDir.getAbsolutePath()); 
    // make the context aware of the graph database service
    context.add(graph);

    SiteBoundaryConds studySiteData = getSiteBoundaryConds();    
    
    GridBuilderParameters<Object> gridParams = new GridBuilderParameters<>(new StrictBorders(),
        new SimpleGridAdder<Object>(), false, studySiteData.getGridDimensions(), new int[] {0, 0});
    GridFactoryFinder.createGridFactory(null).createGrid("Agent Grid", context, gridParams);
    
    initialiseGridValueLayers(context, studySiteData);
    
    // TODO Update seedDispersalParams and seedViabilityParams so they're read from config file
    initialiseSeedDisperser(context, studySiteData, new SeedViabilityParams(7), 
        new SeedDispersalParams(3.844, 0.851, 550, 5, 75, 100));
    
    initialiseSoilMoistureCalculator(context, studySiteData);
    
    // TODO update soilMoistureParams so it's read from config file (via the Parameters object)
    initialiseLcsUpdater(context, databaseDir, "AgroSuccess-dev", 
        new SoilMoistureParams(500, 1000), graph);

    return context;
  }
  
}
