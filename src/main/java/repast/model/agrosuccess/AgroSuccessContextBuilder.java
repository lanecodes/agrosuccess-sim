package repast.model.agrosuccess;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.GraphDatabaseService;
import me.ajlane.geo.repast.fire.FireManager;
import me.ajlane.geo.repast.fire.FireSpreader;
import me.ajlane.geo.repast.fire.LcfMapGetter;
import me.ajlane.geo.repast.fire.LcfMapGetterHardCoded;
import me.ajlane.geo.repast.fire.LcfReplicate;
import me.ajlane.geo.repast.fire.SlopeRiskCalculator;
import me.ajlane.geo.repast.fire.WindRiskCalculator;
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
import repast.model.agrosuccess.empirical.SiteRasterData;
import repast.model.agrosuccess.empirical.SiteWindData;
import repast.model.agrosuccess.empirical.SiteAllData;
import repast.model.agrosuccess.empirical.SiteAllDataFactory;
import repast.model.agrosuccess.empirical.SiteClimateData;
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
import repast.simphony.parameter.IllegalParameterException;
import repast.simphony.parameter.Parameters;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.SimpleGridAdder;
import repast.simphony.space.grid.StrictBorders;
import repast.simphony.valueLayer.GridValueLayer;
import repast.simphony.valueLayer.IGridValueLayer;
import repast.simphony.valueLayer.ValueLayer;

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
    SimulationID simulationID = new SimulationID(params.getString("studySite"));
    modelCore.endAt(params.getInteger("nTicks"));

    // TODO Add parameters required by ModelParamsRepastParser to parameters.xml
    // EnvrModelParams envrModelParams = new ModelParamsRepastParser(params);

    File databaseDir = new File((String) params.getValue("graphPath"), "graph.db");
    GraphDatabaseService graph = new EmbeddedGraphInstance(databaseDir.getAbsolutePath());
    // make the context aware of the graph database service
    context.add(graph);

    SiteAllData siteData = siteAllData(params);

    GridBuilderParameters<Object> gridParams = new GridBuilderParameters<>(new StrictBorders(),
        new SimpleGridAdder<Object>(), false, siteData.getGridDimensions(), new int[] {0, 0});
    GridFactoryFinder.createGridFactory(null).createGrid("Agent Grid", context, gridParams);

    initGridValueLayers(context, siteData);

    SeedDisperser seedDisperser = initSeedDisperser(context, siteData, params);
    context.add(seedDisperser);

    SoilMoistureCalculator smCalc = initSoilMoistureCalculator(context, siteData);
    context.add(smCalc);

    LcsUpdater lcsUpdater = initLcsUpdater(context, graph, params);
    context.add(lcsUpdater);

    // TODO Configure land cover flammability replicate in parameters.xml
    FireManager fireManager = initFireManager(context.getValueLayer(LscapeLayer.Dem.name()),
        (IGridValueLayer) context.getValueLayer(LscapeLayer.Lct.name()),
        siteData, siteData, siteData, LcfReplicate.Default);
    context.add(fireManager);

    // StudySiteDataContainer siteDataContainer =
    // new StudySiteDataContainer(new File(params.getValueAsString("siteDataRoot"),
    // params.getValueAsString("studySite")));
    // FireManager fireManager = initialiseFireManager(context,
    // siteDataContainer.getWindDirectionProb(), siteDataContainer.getWindSpeedProb(),
    // siteDataContainer.getGridCellPixelSize()[0], 0, 0, null);

    ISchedule sche = RunEnvironment.getInstance().getCurrentSchedule();

    try {
      LctProportionAggregator lctPropAgg = new LctProportionAggregator(context.getValueLayer(LscapeLayer.Lct.name()));
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

  private SiteAllData siteAllData(Parameters params) {
    File dataDir;
    SiteAllData siteData;
    boolean useDummyData = params.getBoolean("useDummyData");

    if (useDummyData) {
      dataDir = new File(params.getValueAsString("dummyDataRoot"));
    } else {
      File siteDataRoot = new File(params.getValueAsString("siteDataRoot"));
      dataDir = new File(siteDataRoot, params.getValueAsString("studySite"));
    }

    try {
      siteData = new SiteAllDataFactory().getSiteAllData(dataDir, useDummyData);
    } catch (ConfigurationException e) {
      throw new RuntimeException("Could not load study site configuration.");
    }
    logger.info("Loaded data for site " + siteData.getSiteName());
    return siteData;
  }

  /**
   * Generate required {@code repast.simphony.valueLayer.GridValueLayer} objects representing
   * landscape variables and make the Repast context aware of them.
   *
   * @param context
   * @param studyRasterData
   */
  private void initGridValueLayers(Context<Object> context, SiteRasterData siteRasterData) {
    int[] gridDimensions = siteRasterData.getGridDimensions();
    int[] gridOrigin = new int[] {0, 0}; // Vector space origin for all spatial grids

    List<IGridValueLayer> layerList = new LinkedList<>();
    layerList.add(uniformDefaultLayer(LscapeLayer.SoilMoisture, 0, gridDimensions, gridOrigin));
    layerList.add(siteRasterData.getSoilTypeMap());
    // TODO Think about whether uniform secondary succession is a good idea
    layerList.add(uniformDefaultLayer(LscapeLayer.OakRegen, 0, gridDimensions, gridOrigin));
    layerList.add(siteRasterData.getSlopeMap());
    layerList.add(siteRasterData.getFlowDirMap());
    layerList.add(siteRasterData.getDemMap());
    layerList.add(siteRasterData.getAspectMap());
    layerList.add(uniformDefaultLayer(LscapeLayer.Pine, 0, gridDimensions, gridOrigin));
    layerList.add(uniformDefaultLayer(LscapeLayer.Oak, 0, gridDimensions, gridOrigin));
    layerList.add(uniformDefaultLayer(LscapeLayer.Deciduous, 0, gridDimensions, gridOrigin));
    // TODO Consider whether these initial conditions layers should be specified more granularly
    layerList.add(uniformDefaultLayer(LscapeLayer.DeltaD, -1, gridDimensions, gridOrigin));
    layerList.add(uniformDefaultLayer(LscapeLayer.DeltaT, -1, gridDimensions, gridOrigin));
    layerList.add(uniformDefaultLayer(LscapeLayer.TimeInState, 0, gridDimensions, gridOrigin));

    try {
      layerList.add(siteRasterData.getLctMap());
    } catch (IOException e) {
      throw new RuntimeException("Could not load initial land cover type map.");
    }

    for (IGridValueLayer layer : layerList) {
      context.addValueLayer(layer);
    }

  }

  /**
   * @param layerType LscapeLayer this {@code GridValueLayer} will correspond to
   * @param defaultValue Value which will be applied everywhere in this new layer
   * @param gridDimensions [x, y] dimensions of the new layer
   * @param gridOrigin [x, y] coordinates of the origin of the new layer
   * @return Uniform {@code GridValueLayer} specified by the parameters
   */
  private IGridValueLayer uniformDefaultLayer(LscapeLayer layerType, int defaultValue,
      int[] gridDimensions, int[] gridOrigin) {
    String name = layerType.name();
    return new GridValueLayer(name, defaultValue, true, new StrictBorders(), gridDimensions,
        gridOrigin);
  }

  /**
   * Initialise the seed disperser agent, and makes it aware of the Repast context.
   *
   * The seed disperser agent modifies the seed presence layers in the model based on the location
   * of seed generating sources.
   *
   * @param context Simulation context object
   * @param siteRasterData Raster data for study site
   * @param params Repast simulation parameters (specified in {@code parameters.xml})
   * @returns Configured SeedDisperser
   */
  private SeedDisperser initSeedDisperser(Context<Object> context, SiteRasterData siteRasterData,
      Parameters params) {

    double[] gridPixelSize = siteRasterData.getGridCellPixelSize();

    Map<String, Double> seedParams = new HashMap<>();
    seedParams.put("seedLifetime", 7.0);
    seedParams.put("acornLocationParam", 3.844);
    seedParams.put("acornScaleParam", 0.851);
    seedParams.put("acornMaxLognormalDist", 550.0);
    seedParams.put("windDistDecreaseParam", 5.0);
    seedParams.put("windMinExpDist", 75.0);
    seedParams.put("windMaxExpDist", 100.0);

    for (Map.Entry<String, Double> entry : seedParams.entrySet()) {
      try {
        entry.setValue(params.getDouble(entry.getKey()));
      } catch (IllegalParameterException e) {
        logger.warn("Could not find entry for '" + entry.getKey() + "' in parameters."
            + " Defaulting to default value " + entry.getValue());
      }
    }

    SeedViabilityParams svParams =
        new SeedViabilityParams(seedParams.get("seedLifetime").intValue());
    SeedDispersalParams sdParams = new SeedDispersalParams(seedParams.get("acornLocationParam"),
        seedParams.get("acornScaleParam"), seedParams.get("acornMaxLognormalDist"),
        seedParams.get("windDistDecreaseParam"), seedParams.get("windMinExpDist"),
        seedParams.get("windMaxExpDist"));

    SeedDisperser seedDisperser = new SpatiallyRandomSeedDisperser(gridPixelSize[0],
        gridPixelSize[1], svParams, sdParams, context);

    return seedDisperser;
  }

  /**
   * Initialise the soil moisture calculator and make it aware of the Repast context. This will
   * Update the spatially varying soil moisture in the landscape in response to precipitation.
   *
   * @param context
   * @param studySiteData
   * @return Configured soil moisture calculator
   */
  private SoilMoistureCalculator initSoilMoistureCalculator(Context<Object> context,
      SiteClimateData climateData) {
    SoilMoistureCalculator smCalc =
        new SoilMoistureCalculator(climateData.getTotalAnnualPrecipitation(), context);
    return smCalc;
  }

  /**
   * Initialise the land cover state updater (take account of evolving environmental state variables
   * and update land cover in response) and make it aware of the Repast context.
   *
   * @param context Simulation context
   * @param graph Graph database service containing model configuration
   * @param params Repast model parameters
   * @return Configured LcsUpdater object
   */
  private LcsUpdater initLcsUpdater(Context<Object> context, GraphDatabaseService graph,
      Parameters params) {

    String modelID;
    try {
      modelID = params.getString("graphModelID");
    } catch (IllegalParameterException e) {
      logger.error("Could not find 'graphModelID' in parameters. Check configuration.");
      throw e;
    }

    Map<String, Integer> smParams = new HashMap<>();
    smParams.put("mesicThreshold", 500);
    smParams.put("hydricThreshold", 1000);

    for (Map.Entry<String, Integer> entry : smParams.entrySet()) {
      try {
        entry.setValue(params.getInteger(entry.getKey()));
      } catch (IllegalParameterException e) {
        logger.warn("Could not find entry for '" + entry.getKey() + "' in parameters."
            + " Defaulting to default value " + entry.getValue());
      }
    }

    SoilMoistureParams soilMoistureParams =
        new SoilMoistureParams(smParams.get("mesicThreshold"), smParams.get("hydricThreshold"));

    EnvrStateAliasTranslator translator = new AgroSuccessEnvrStateAliasTranslator();
    LcsTransitionMapFactory fac = new GraphBasedLcsTransitionMapFactory(graph, modelID, translator);
    CodedLcsTransitionMap codedMap = fac.getCodedLcsTransitionMap();
    LcsUpdateDecider updateDecider = new AgroSuccessLcsUpdateDecider(codedMap);

    SoilMoistureDiscretiser smDiscretiser =
        new AgroSuccessSoilMoistureDiscretiser(soilMoistureParams);

    LcsUpdater lcsUpdater = new AgroSuccessLcsUpdater(context, updateDecider, smDiscretiser);
    return lcsUpdater;
  }

  private FireManager initFireManager(ValueLayer demLayer, IGridValueLayer lctLayer,
      SiteWindData windData, SiteRasterData rasterData, SiteClimateData climateData,
      LcfReplicate lcfReplicate) {
    double[] gridCellSize = rasterData.getGridCellPixelSize();
    double aveGridCellSize = (gridCellSize[0] + gridCellSize[1]) / 2;
    LcfMapGetter lcfGetter = new LcfMapGetterHardCoded(lcfReplicate);
    SlopeRiskCalculator srCalc = new SlopeRiskCalculator(demLayer, aveGridCellSize);
    WindRiskCalculator wrCalc = new WindRiskCalculator();
    FireSpreader fireSpreader = new FireSpreader(lctLayer, srCalc, wrCalc, lcfGetter.getMap(),
        windData.getWindDirectionProb(), windData.getWindSpeedProb());
    // Millington et al.2009 eq 7
    double meanNumFires =
        12 * (climateData.getMeanAnnualTemperature() / climateData.getTotalAnnualPrecipitation());
    return new FireManager(meanNumFires, fireSpreader);
  }

}
