package repast.model.agrosuccess;

import java.io.File;
import java.io.IOException;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;
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
import repast.model.agrosuccess.AgroSuccessCodeAliases.Lct;
import repast.model.agrosuccess.reporting.EnumRecordCsvWriter;
import repast.model.agrosuccess.reporting.LctProportionAggregator;
import repast.model.agrosuccess.reporting.RecordWriter;
import repast.model.agrosuccess.reporting.SimulationID;
import repast.simphony.context.Context;
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ISchedule;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.parameter.Parameters;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.SimpleGridAdder;
import repast.simphony.space.grid.StrictBorders;
import repast.simphony.valueLayer.GridValueLayer;

/**
 * @author Andrew Lane
 *
 */
public class AgroSuccessContextBuilder implements ContextBuilder<Object> {
  /*
   * (non-Javadoc)
   *
   * @see repast.simphony.dataLoader.ContextBuilder#build(repast.simphony.context.Context)
   */

  final static Logger logger = Logger.getLogger(AgroSuccessContextBuilder.class);

  // pseudo-agents modify GridValueLayer-s
  SoilMoistureCalculator soilMoistureCalculator;
  SeedDisperser seedDisperser;

  GridValueLayer soilMoisture; // name = "soil moisture"
  GridValueLayer soilTypeMap; // see SoilMoistureCalculator, name = "soil"
  GridValueLayer landCoverTypeMap; // name = "lct"
  GridValueLayer succession;

  GridValueLayer slopeMap; // name = "slope"
  GridValueLayer aspect; // name = "aspect"

  GridValueLayer pineSeeds; // name = "pine seeds"
  GridValueLayer oakSeeds; // name = "oak seeds"
  GridValueLayer deciduousSeeds; // name = "deciduous seeds"

  GridValueLayer timeInState;
  GridValueLayer deltaD;
  GridValueLayer deltaT;


  int[] gridOrigin = new int[] {0, 0}; // vector space origin for all spatial grids

  /**
   * Generate required {@code repast.simphony.valueLayer.GridValueLayer} objects representing
   * landscape variables and make the Repast context aware of them.
   *
   * @Override
   * @param context
   * @param studySiteData
   */
  private void initialiseGridValueLayers(Context<Object> context, SiteBoundaryConds studySiteData) {

    int[] gridDimensions = studySiteData.getGridDimensions();

    GridValueLayer soilMoisture = new GridValueLayer(LscapeLayer.SoilMoisture.name(), 0, true,
        new StrictBorders(), gridDimensions, gridOrigin);
    context.addValueLayer(soilMoisture);

    soilTypeMap = studySiteData.getSoilMap(); // "soil"
    context.addValueLayer(soilTypeMap);

    landCoverTypeMap = studySiteData.getInitialLandCoverMap(); // "lct"
    context.addValueLayer(landCoverTypeMap);

    try {
      succession = studySiteData.getOakRegenMap();
      context.addValueLayer(succession);
    } catch (NullPointerException e) {
      logger.warn("Could not load succession state from file. "
                  + "Defaulting to homogenous secondary succession state.");
      succession = new GridValueLayer(LscapeLayer.OakRegen.name(), 0, true, new StrictBorders(),
          gridDimensions, gridOrigin);
      context.addValueLayer(succession);
    }

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

    deciduousSeeds = new GridValueLayer(LscapeLayer.Deciduous.name(), 0, true, new StrictBorders(),
        gridDimensions, gridOrigin);
    context.addValueLayer(deciduousSeeds);

    // TODO Consider whether these initial conditions layers should be specified more granularly
    deltaD = new GridValueLayer(LscapeLayer.DeltaD.name(), -1, true, new StrictBorders(),
        gridDimensions, gridOrigin);
    context.addValueLayer(deltaD);

    deltaT = new GridValueLayer(LscapeLayer.DeltaT.name(), -1, true, new StrictBorders(),
        gridDimensions, gridOrigin);
    context.addValueLayer(deltaT);

    timeInState = new GridValueLayer(LscapeLayer.TimeInState.name(), 0, true, new StrictBorders(),
        gridDimensions, gridOrigin);
    context.addValueLayer(timeInState);

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

    double[] gridPixelSize =
        {(double) studySiteData.getGridPixelSize(), (double) studySiteData.getGridPixelSize()};

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
    SoilMoistureCalculator smCalc = new SoilMoistureCalculator(studySiteData.getFlowDirMap(),
        studySiteData.getMeanAnnualPrecipitation(), context);
    return smCalc;
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
    LcsTransitionMapFactory fac = new GraphBasedLcsTransitionMapFactory(graph, modelID, translator);
    CodedLcsTransitionMap codedMap = fac.getCodedLcsTransitionMap();
    LcsUpdateDecider updateDecider = new AgroSuccessLcsUpdateDecider(codedMap);

    SoilMoistureDiscretiser smDiscretiser =
        new AgroSuccessSoilMoistureDiscretiser(soilMoistureParams);

    LcsUpdater lcsUpdater = new AgroSuccessLcsUpdater(context, updateDecider, smDiscretiser);
    return lcsUpdater;
  }

  private SiteBoundaryConds getDummySiteBoundaryConds() {
    File testDataDir = new File("data/test");
    // specify precipitation, grid pixel size
    SiteBoundaryConds sbcs = new SiteBoundaryCondsHardCoded(50, 10,
        new File(testDataDir, "dummy_51x51_lct_oak_pine_burnt.tif"),
        new File(testDataDir, "dummy_51x51_soil_type_uniform_A.tif"),
        new File(testDataDir, "dummy_51x51_succession_state_mix.tif"),
        new File(testDataDir, "dummy_51x51_slope.tif"),
        new File(testDataDir, "dummy_51x51_binary_aspect.tif"),
        new File(testDataDir, "dummy_51x51_flowdir.tif"));
    return sbcs;
  }

  /**
   * <h1>Note on implementation</h1> The conversion of {@code ConfigurationException} and
   * {@code IOException} instances which might legitimately arise while reading study site data at
   * the beginning of a simulation is a hack. This is necessary because it does not seem to be
   * possible to specify that {@code Context.build} throws these types of checked exceptions. There
   * is no way to recover from such an error, and the programmer must work out why these files can't
   * be read and correct the problem. Therefore runtime exceptions are <emph>just about</emph>
   * appropriate. However it would be bettter practice to find a way to propagate these specific
   * checked exceptions to the top of the stack.
   *
   * @param params Repast Simphony parameters specified in parameters.xml.
   * @return Object representing the study site specific boundary conditions for the simulation.
   */
  private SiteBoundaryConds getSiteBoundaryCondsFromData(Parameters params) {
    String dataDir = params.getValueAsString("siteDataRoot");
    String siteName = params.getValueAsString("studySite");
    SiteBoundaryConds studySiteData;
    try {
      StudySiteDataContainer siteDataContainer =
          new StudySiteDataContainer(new File(dataDir, siteName));
      studySiteData = new SiteBoundaryCondsFromData(siteDataContainer);
    } catch (ConfigurationException e) {
      throw new RuntimeException("Could not load study site configuration.");
    } catch (IOException e) {
      throw new RuntimeException("Could not load study site data.");
    }
    return studySiteData;
  }


  public void endMethod(Context<Object> context, RecordWriter<Lct, Double> lctWriter) {
    logger.info("End of the simulation");
    for (Object graph : context.getObjects(EmbeddedGraphInstance.class)) {
      ((GraphDatabaseService) graph).shutdown();
    }

    try {
      lctWriter.flush();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void updateLctWriter(LctProportionAggregator aggregator,
      RecordWriter<Lct, Double> lctWriter) {
    lctWriter.add(aggregator.getLctProportions());
  }

  public void printLctProportion(LctProportionAggregator lctPropAgg) {
    logger.debug(lctPropAgg.getLctProportions());
  }

  @Override
  public Context<Object> build(Context<Object> context) {

    RunEnvironment modelCore = RunEnvironment.getInstance();
    Parameters params = modelCore.getParameters();
    modelCore.endAt(params.getInteger("nTicks"));
    SimulationID simulationID = new SimulationID(params.getString("studySite"));

    // TODO Add parameters required by ModelParamsRepastParser to parameters.xml
    // EnvrModelParams envrModelParams = new ModelParamsRepastParser(params);

    File databaseDir = new File((String) params.getValue("graphPath"), "graph.db");
    GraphDatabaseService graph = new EmbeddedGraphInstance(databaseDir.getAbsolutePath());
    // make the context aware of the graph database service
    context.add(graph);

    // SiteBoundaryConds studySiteData = getDummySiteBoundaryConds(); // useful for testing
    SiteBoundaryConds studySiteData = getSiteBoundaryCondsFromData(params);

    GridBuilderParameters<Object> gridParams = new GridBuilderParameters<>(new StrictBorders(),
        new SimpleGridAdder<Object>(), false, studySiteData.getGridDimensions(), new int[] {0, 0});
    GridFactoryFinder.createGridFactory(null).createGrid("Agent Grid", context, gridParams);

    initialiseGridValueLayers(context, studySiteData);

    // TODO Update seedDispersalParams and seedViabilityParams so they're read from config file
    context.add(initialiseSeedDisperser(context, studySiteData, new SeedViabilityParams(7),
        new SeedDispersalParams(3.844, 0.851, 550, 5, 75, 100)));

    context.add(initialiseSoilMoistureCalculator(context, studySiteData));

    // TODO update soilMoistureParams so it's read from config file (via the Parameters object)
    context.add(initialiseLcsUpdater(context, databaseDir, "AgroSuccess-dev",
        new SoilMoistureParams(500, 1000), graph));

    ISchedule sche = RunEnvironment.getInstance().getCurrentSchedule();

    try {
      LctProportionAggregator lctPropAgg = new LctProportionAggregator(landCoverTypeMap);
      RecordWriter<Lct, Double> lctWriter;
      lctWriter = new EnumRecordCsvWriter<Lct, Double>(Lct.class,
          new File("output", simulationID.toString() + "_lct-props.csv"));
      ScheduleParameters printProps = ScheduleParameters.createRepeating(0, 1, -10);
      sche.schedule(printProps, this, "updateLctWriter", lctPropAgg, lctWriter);

      // call method at the end of the simulation run. See
      // https://martavallejophd.wordpress.com/2012/03/26/run-a-method-at-the-end-of-the-simulation/
      ScheduleParameters stop = ScheduleParameters.createAtEnd(ScheduleParameters.LAST_PRIORITY);
      sche.schedule(stop, this, "endMethod", context, lctWriter);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    return context;
  }

}
