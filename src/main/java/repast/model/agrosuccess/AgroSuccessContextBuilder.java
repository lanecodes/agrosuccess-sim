package repast.model.agrosuccess;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;
import me.ajlane.geo.CartesianGridDouble2D;
import me.ajlane.geo.WriteableCartesianGridDouble2D;
import me.ajlane.geo.repast.GridValueLayerAdapter;
import me.ajlane.geo.repast.ValueLayerAdapter;
import me.ajlane.geo.repast.colonisation.LandCoverColonisationAction;
import me.ajlane.geo.repast.colonisation.LandCoverColoniser;
import me.ajlane.geo.repast.colonisation.csr.CompletelySpatiallyRandomColoniser;
import me.ajlane.geo.repast.colonisation.csr.CompletelySpatiallyRandomParams;
import me.ajlane.geo.repast.fire.DefaultFireManager;
import me.ajlane.geo.repast.fire.DefaultFireSpreader;
import me.ajlane.geo.repast.fire.DefaultFlammabilityChecker;
import me.ajlane.geo.repast.fire.FireManager;
import me.ajlane.geo.repast.fire.FireParams;
import me.ajlane.geo.repast.fire.FireReporter;
import me.ajlane.geo.repast.fire.FireSpreader;
import me.ajlane.geo.repast.fire.FlammabilityChecker;
import me.ajlane.geo.repast.fire.LcfMapGetter;
import me.ajlane.geo.repast.fire.LcfMapGetterHardCoded;
import me.ajlane.geo.repast.fire.ReportingRepastFireSpreader;
import me.ajlane.geo.repast.fire.RunFireSeasonAction;
import me.ajlane.geo.repast.fire.SlopeRiskCalculator;
import me.ajlane.geo.repast.fire.WindRiskCalculator;
import me.ajlane.geo.repast.soilmoisture.AgroSuccessSoilMoistureDiscretiser;
import me.ajlane.geo.repast.soilmoisture.DefaultFlowDirectionMap;
import me.ajlane.geo.repast.soilmoisture.JgraphtLandscapeFlow;
import me.ajlane.geo.repast.soilmoisture.SoilMoistureCalculator;
import me.ajlane.geo.repast.soilmoisture.SoilMoistureDiscretiser;
import me.ajlane.geo.repast.soilmoisture.SoilMoistureParams;
import me.ajlane.geo.repast.soilmoisture.SoilMoistureUpdater;
import me.ajlane.geo.repast.soilmoisture.agrosuccess.AgroSuccessCurveNumberGenerator;
import me.ajlane.geo.repast.soilmoisture.agrosuccess.SoilMoistureUpdateAction;
import me.ajlane.geo.repast.succession.AgroSuccessLcsUpdateDecider;
import me.ajlane.geo.repast.succession.LcsUpdateDecider;
import me.ajlane.geo.repast.succession.LcsUpdater;
import me.ajlane.geo.repast.succession.OakAgeUpdateAction;
import me.ajlane.geo.repast.succession.OakAgeUpdater;
import me.ajlane.geo.repast.succession.SeedStateUpdater;
import me.ajlane.geo.repast.succession.SuccessionPathwayUpdater;
import me.ajlane.geo.repast.succession.pathway.coded.CodedLcsTransitionMap;
import me.ajlane.geo.repast.succession.pathway.io.CodedLcsTransitionMapReaderFactory;
import repast.model.agrosuccess.AgroSuccessCodeAliases.Lct;
import repast.model.agrosuccess.empirical.SiteAllData;
import repast.model.agrosuccess.empirical.SiteAllDataFactory;
import repast.model.agrosuccess.empirical.SiteClimateData;
import repast.model.agrosuccess.empirical.SiteRasterData;
import repast.model.agrosuccess.empirical.SiteWindData;
import repast.model.agrosuccess.params.EnvrModelParams;
import repast.model.agrosuccess.params.ModelParamsRepastParser;
import repast.model.agrosuccess.reporting.EnumRecordCsvWriter;
import repast.model.agrosuccess.reporting.LctProportionAggregator;
import repast.model.agrosuccess.reporting.RecordWriter;
import repast.model.agrosuccess.reporting.SimulationID;
import repast.simphony.context.Context;
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.IAction;
import repast.simphony.engine.schedule.ISchedule;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.parameter.Parameters;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.GridPoint;
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

  @Override
  public Context<Object> build(Context<Object> context) {

    RunEnvironment modelCore = RunEnvironment.getInstance();
    Parameters params = modelCore.getParameters();
    // SimulationID simulationID = new SimulationID(params.getString("studySite"));
    ISchedule schedule = RunEnvironment.getInstance().getCurrentSchedule();
    modelCore.endAt(params.getInteger("nTicks"));

    EnvrModelParams envrModelParams = new ModelParamsRepastParser(params);

    SiteAllData siteData = siteAllData(params);

    GridBuilderParameters<Object> gridParams = new GridBuilderParameters<>(new StrictBorders(),
        new SimpleGridAdder<Object>(), false, siteData.getGridDimensions(), new int[] {0, 0});
    GridFactoryFinder.createGridFactory(null).createGrid("Agent Grid", context, gridParams);

    initGridValueLayers(context, siteData, params);

    LandCoverColoniser landCoverColoniser = initSeedDisperser(context, siteData,
        envrModelParams.getLandCoverColoniserParams());
    IAction landCoverColonisation = new LandCoverColonisationAction(landCoverColoniser);
    // context.add(landCoverColoniser);
    schedule.schedule(ScheduleParameters.createRepeating(1, 1, 3), landCoverColonisation);

    SoilMoistureUpdater smCalc = initSoilMoistureCalculator(context);
    IAction updateSM = new SoilMoistureUpdateAction(smCalc, siteData.getTotalAnnualPrecipitation());
    schedule.schedule(ScheduleParameters.createRepeating(1, 1, 2), updateSM);

    FireReporter<GridPoint> fireReporter = new FireReporter<>();
    FireManager fireManager = initFireManager(context.getValueLayer(LscapeLayer.Dem.name()),
        (IGridValueLayer) context.getValueLayer(LscapeLayer.Lct.name()),
        (IGridValueLayer) context.getValueLayer(LscapeLayer.FireCount.name()), siteData, siteData,
        siteData, envrModelParams.getFireParams(), fireReporter, schedule);
    IAction runFireSeason = new RunFireSeasonAction(fireManager);
    schedule.schedule(ScheduleParameters.createRepeating(1, 1, 1), runFireSeason);
    // context.add(fireManager);
    context.add(fireReporter);

    LcsUpdater lcsUpdater = initLcsUpdater(context, new File(params.getString("lcsTransMapFile")),
        envrModelParams.getSoilMoistureParams());
    IAction updateLandCoverState = new UpdateLandCoverStateAction(lcsUpdater);
    schedule.schedule(ScheduleParameters.createRepeating(1, 1, 0), updateLandCoverState);
    // context.add(lcsUpdater);

    OakAgeUpdater oakAgeUpdater = initOakAgeUpdater(
        (IGridValueLayer) context.getValueLayer(LscapeLayer.OakAge.name()),
        context.getValueLayer(LscapeLayer.Lct.name()));
    IAction updateOakAge = new OakAgeUpdateAction(oakAgeUpdater);
    schedule.schedule(ScheduleParameters.createRepeating(1, 1, -1), updateOakAge);
    // context.add(oakAgeUpdater);

    // initLctReporters(context, simulationID);
    LctProportionAggregator lctPropAggregator =
        new LctProportionAggregator(context.getValueLayer(LscapeLayer.Lct.name()));
    context.add(lctPropAggregator);

    logger.debug(schedule.getActionCount() + " actions scheduled");

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
   * @param Repast Simphony model parameters
   */
  private void initGridValueLayers(Context<Object> context, SiteRasterData siteRasterData,
      Parameters params) {
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
    layerList.add(uniformDefaultLayer(LscapeLayer.FireCount, 0, gridDimensions, gridOrigin));
    layerList.add(uniformDefaultLayer(LscapeLayer.OakAge, -1, gridDimensions, gridOrigin));

    if (params.getBoolean("useNullLctNlm")) {
      layerList.add(siteRasterData.getNullLctMap(gridDimensions, gridOrigin));
    } else {
      layerList.add(chooseRandomLctMap(siteRasterData, params.getInteger("nLctNlms")));
    }

    for (IGridValueLayer layer : layerList) {
      context.addValueLayer(layer);
    }

  }

  /**
   * @param siteRasterData Spatially varying data for study site
   * @param nLctNlms Total number of land-cover NLMs in the input file {@code init_lct_maps.zip}
   * @return Land-cover type map representing the state of the landscape at the beginning of the
   *         simulation
   */
  private IGridValueLayer chooseRandomLctMap(SiteRasterData siteRasterData, int nLctNlms) {
    try {
      int maxLctMapIndex = nLctNlms - 1;
      int lctMapNum = RandomHelper.nextIntFromTo(0, maxLctMapIndex);
      return siteRasterData.getLctMap(lctMapNum);
    } catch (IOException e) {
      throw new RuntimeException("Could not load initial land cover type map.");
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
   * @param colonisationParams Parameters for the land-cover colonisation model
   * @returns Configured land-cover coloniser model
   */
  private LandCoverColoniser initSeedDisperser(Context<Object> context,
      SiteRasterData siteRasterData,
      CompletelySpatiallyRandomParams colonisationParams) {

    CartesianGridDouble2D landCoverType = new GridValueLayerAdapter((IGridValueLayer) context
        .getValueLayer(LscapeLayer.Lct.name()));
    WriteableCartesianGridDouble2D juvenilePine = new GridValueLayerAdapter(
        (IGridValueLayer) context.getValueLayer(LscapeLayer.Pine.name()));
    WriteableCartesianGridDouble2D juvenileOak = new GridValueLayerAdapter((IGridValueLayer) context
        .getValueLayer(LscapeLayer.Oak.name()));
    WriteableCartesianGridDouble2D juvenileDeciduous = new GridValueLayerAdapter(
        (IGridValueLayer) context.getValueLayer(LscapeLayer.Deciduous.name()));
    LandCoverColoniser landCoverColoniser = new CompletelySpatiallyRandomColoniser(landCoverType,
        juvenilePine, juvenileOak, juvenileDeciduous, colonisationParams);

    return landCoverColoniser;
  }

  /**
   * Initialise the soil moisture calculator and make it aware of the Repast context. This will
   * Update the spatially varying soil moisture in the landscape in response to precipitation.
   *
   * @param context
   * @param studySiteData
   * @return Configured soil moisture calculator
   */
  private SoilMoistureUpdater initSoilMoistureCalculator(Context<Object> context) {
    SoilMoistureUpdater smCalc = new SoilMoistureCalculator(
        new GridValueLayerAdapter(
            (IGridValueLayer) context.getValueLayer(LscapeLayer.SoilMoisture.name())),
        new ValueLayerAdapter(context.getValueLayer(LscapeLayer.Lct.name())),
        new ValueLayerAdapter(context.getValueLayer(LscapeLayer.SoilType.name())),
        new ValueLayerAdapter(context.getValueLayer(LscapeLayer.Slope.name())),
        new JgraphtLandscapeFlow(
            new ValueLayerAdapter(context.getValueLayer(LscapeLayer.FlowDir.name())),
            new DefaultFlowDirectionMap()),
        new AgroSuccessCurveNumberGenerator());

    return smCalc;
  }

  /**
   * Initialise the land cover state updater (take account of evolving environmental state variables
   * and update land cover in response) and make it aware of the Repast context.
   *
   * @param context Simulation context
   * @param lcsTransMapFile The file containing the coded land-cover state transition pathway map
   * @return Configured LcsUpdater object
   */
  private LcsUpdater initLcsUpdater(Context<Object> context, File lcsTransMapFile,
      SoilMoistureParams smParams) {

    CodedLcsTransitionMap codedMap = new CodedLcsTransitionMapReaderFactory()
        .getCodedLcsTransitionMapReader(lcsTransMapFile)
        .getCodedLcsTransitionMap();

    SuccessionPathwayUpdater successionUpdater = new SuccessionPathwayUpdater(200.);
    SeedStateUpdater seedUpdater = new SeedStateUpdater(
        new HashSet<Integer>(Arrays.asList(Lct.Pine.getCode(), Lct.Oak.getCode(), Lct.Deciduous
            .getCode())));
    LcsUpdateDecider updateDecider = new AgroSuccessLcsUpdateDecider(codedMap, successionUpdater,
        seedUpdater);

    SoilMoistureDiscretiser smDiscretiser = new AgroSuccessSoilMoistureDiscretiser(smParams);

    LcsUpdater lcsUpdater = new AgroSuccessLcsUpdater(context, updateDecider, smDiscretiser);
    return lcsUpdater;
  }

  /**
   * Creates a DefaultFireManager pseudo-agent and configure it to run the fire regime in the
   * simulation.
   *
   * TODO Refactor this method into a {@code AgroSuccessFireManagerBuilder} to encapsulate this
   * complexity
   *
   * @param demLayer Digital Elevation Model as a {@code ValueLayer}
   * @param lctLayer Land cover type as a {@code IGridValueLayer}
   * @param fireCount Number of times each cell has been burnt as a {@code IGridValueLayer}
   * @param windData Site-specific wind speed and direction data
   * @param rasterData Information about site's raster grids, used for cell size
   * @param climateData Site-specific climate data (temperature and precipitation)
   * @param fireParams Parameters needed to specify the fire ignition and spread model
   * @param fireReporter
   * @param schedule
   * @return Configured DefaultFireManager
   */
  private FireManager initFireManager(ValueLayer demLayer, IGridValueLayer lctLayer,
      IGridValueLayer fireCount, SiteWindData windData, SiteRasterData rasterData,
      SiteClimateData climateData, FireParams fireParams, FireReporter<GridPoint> fireReporter,
      ISchedule schedule) {
    double[] gridCellSize = rasterData.getGridCellPixelSize();
    double aveGridCellSize = (gridCellSize[0] + gridCellSize[1]) / 2;
    LcfMapGetter lcfGetter = new LcfMapGetterHardCoded(fireParams.getLcfReplicate());
    SlopeRiskCalculator srCalc = new SlopeRiskCalculator(demLayer, aveGridCellSize);
    WindRiskCalculator wrCalc = new WindRiskCalculator();
    FlammabilityChecker<GridPoint> flamChecker = new DefaultFlammabilityChecker(lctLayer);
    // Millington et al. 2009 eq 7
    double meanNumFires = fireParams.getClimateIgnitionScalingParam()
        * (climateData.getMeanAnnualTemperature() / climateData.getTotalAnnualPrecipitation());
    double vegetationMoistureParam = meanNumFires; // lambda parameterises fuel moisture as well as
                                                   // number of fires
    FireSpreader<GridPoint> baseFireSpreader = new DefaultFireSpreader(lctLayer, fireCount, srCalc,
        wrCalc, flamChecker, lcfGetter.getMap(), windData.getWindDirectionProb(),
        windData.getWindSpeedProb(), vegetationMoistureParam);
    FireSpreader<GridPoint> reportingFireSpreader =
        new ReportingRepastFireSpreader(baseFireSpreader, fireReporter, schedule);

    return new DefaultFireManager(reportingFireSpreader, flamChecker, lctLayer.getDimensions(),
        meanNumFires);
  }

  /**
   * @param oakAgeLayer Value layer storing the number of years a grid cell has contained a
   *        land-cover type which includes reproductively mature oak
   * @param landCoverTypeLayer Value layer storing each cell's current land-cover type
   * @return {@code OakAgeUpdater} configured with the land-cover types which are considered to
   *         contain reproductively mature oak vegetation, namely {@code Lct.Oak} and
   *         {@code Lct.TransForest}
   */
  private OakAgeUpdater initOakAgeUpdater(IGridValueLayer oakAgeLayer,
      ValueLayer landCoverTypeLayer) {
    Set<Integer> matureVegCodes = new HashSet<>(
        Arrays.asList(Lct.TransForest.getCode(), Lct.Oak.getCode()));
    return new OakAgeUpdater(oakAgeLayer, landCoverTypeLayer, matureVegCodes, -1);
  }

  /**
   * Create objects used for reporting land cover type proportions to disk. Make the Repast
   * scheduler aware of their methods and configure them to run at appropriate times.
   *
   * @param context Simulation context
   * @param id Simulation ID, used to name output files.
   */
  private void initLctReporters(Context<Object> context, SimulationID id) {
    ISchedule sche = RunEnvironment.getInstance().getCurrentSchedule();
    // call method at the end of the simulation run. See
    // https://martavallejophd.wordpress.com/2012/03/26/run-a-method-at-the-end-of-the-simulation/
    ScheduleParameters stop = ScheduleParameters.createAtEnd(ScheduleParameters.LAST_PRIORITY);
    ScheduleParameters updateReporters = ScheduleParameters.createRepeating(0, 1, -10);

    LctProportionAggregator lctPropAgg =
        new LctProportionAggregator(context.getValueLayer(LscapeLayer.Lct.name()));
    RecordWriter<Lct, Double> lctWriter = initLctWriter(id);

    sche.schedule(updateReporters, this, "updateLctWriter", lctPropAgg, lctWriter);
    sche.schedule(stop, this, "finaliseSimulation", context, lctWriter);
  }

  /**
   * @param id ID of simulation model, used to generate name for output file.
   * @return Writer object responsible for committing land cover type proportion results to disk.
   */
  private RecordWriter<Lct, Double> initLctWriter(SimulationID id) {
    RecordWriter<Lct, Double> lctWriter;
    try {
      lctWriter = new EnumRecordCsvWriter<Lct, Double>(Lct.class,
          new File("output", id.toString() + "_lct-props.csv"));
    } catch (IOException e) {
      throw new RuntimeException("Could not initialise land cover type writer.");
    }
    return lctWriter;
  }


  /**
   * Code to run at the end of the simulation. Releases resources and reports outputs to disk.
   *
   * @param context Simulation concept
   * @param lctWriter Writer used to send output to disk file.
   */
  public void finaliseSimulation(Context<Object> context, RecordWriter<Lct, Double> lctWriter) {
    logger.info("End of the simulation");
  }

  /**
   * Add current land cover type proportions to object which will write them to disk.
   *
   * @param lctAggregator Object aware of current land cover type proportions
   * @param lctWriter Commits information about land cover type proportion to disk
   */
  public void updateLctWriter(LctProportionAggregator lctAggregator,
      RecordWriter<Lct, Double> lctWriter) {
    lctWriter.add(lctAggregator.getLctProportions());
  }

  /**
   * Write current land cover type proportions to console.
   *
   * @param lctAggregator Object aware of current land cover type proportions
   */
  public void printLctProportion(LctProportionAggregator lctAggregator) {
    logger.debug(lctAggregator.getLctProportions());
  }

}
