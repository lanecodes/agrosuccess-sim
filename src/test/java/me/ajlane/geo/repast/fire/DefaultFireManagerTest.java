package me.ajlane.geo.repast.fire;

import static org.junit.Assert.assertTrue;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import me.ajlane.geo.Direction;
import me.ajlane.geo.repast.RepastGridUtils;
import repast.model.agrosuccess.AgroSuccessCodeAliases.Lct;
import repast.model.agrosuccess.reporting.LctProportionAggregator;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.valueLayer.GridValueLayer;
import repast.simphony.valueLayer.IGridValueLayer;
import repast.simphony.valueLayer.ValueLayer;

/**
 * This is mainly an integration test
 *
 * @author Andrew Lane
 */
public class DefaultFireManagerTest {

  final static Logger logger = Logger.getLogger(DefaultFireManagerTest.class);

  private final SlopeRiskCalculator srCalc = getTestSlopeRiskCalculator();
  private final WindRiskCalculator wrCalc = new WindRiskCalculator();
  private final Map<Lct, Double> lcfMap = getTestLcfMap();
  private final Map<Direction, Double> windDirProbMap = getTestWindDirProbMap();
  private final Map<WindSpeed, Double> windSpeedProbMap = getTestWindSpeedProbMap();

  private IGridValueLayer lct;
  private IGridValueLayer fireCount;
  private FireSpreader<GridPoint> fireSpreader;
  private FlammabilityChecker<GridPoint> flamChecker;
  private Double vegetationMoistureParam;

  @Before
  public void setUp() {
    this.lct = getTestLct();
    this.fireCount = new GridValueLayer("FireCount", 0, true, 5, 5);
    this.vegetationMoistureParam = 0.5; // lambda in thesis notation
    this.flamChecker = new DefaultFlammabilityChecker(this.lct);
    this.fireSpreader =
        new DefaultFireSpreader(lct, fireCount, srCalc, wrCalc, flamChecker, lcfMap,
            windDirProbMap, windSpeedProbMap, vegetationMoistureParam);
  }

  private static IGridValueLayer getTestLct() {
    int[][] lctArray =
        {{0, 0, 2, 2, 2}, {1, 1, 3, 3, 3}, {9, 3, 3, 3, 3}, {9, 9, 9, 9, 9}, {9, 9, 9, 9, 9},};
    return RepastGridUtils.arrayToGridValueLayer("lct", lctArray);
  }

  private static Map<Direction, Double> getTestWindDirProbMap() {
    Map<Direction, Double> m = new HashMap<>();
    m.put(Direction.N, 0.1);
    m.put(Direction.NE, 0.1);
    m.put(Direction.E, 0.1);
    m.put(Direction.SE, 0.2);
    m.put(Direction.S, 0.2);
    m.put(Direction.SW, 0.1);
    m.put(Direction.W, 0.1);
    m.put(Direction.NW, 0.1);
    return m;
  }

  private static Map<WindSpeed, Double> getTestWindSpeedProbMap() {
    Map<WindSpeed, Double> m = new HashMap<>();
    m.put(WindSpeed.Low, 0.6);
    m.put(WindSpeed.Medium, 0.3);
    m.put(WindSpeed.High, 0.1);
    return m;
  }

  private static Map<Lct, Double> getTestLcfMap() {
    LcfMapGetter defaultGetter = new LcfMapGetterHardCoded(LcfReplicate.Default);
    return defaultGetter.getMap();
  }

  private static SlopeRiskCalculator getTestSlopeRiskCalculator() {
    int[][] demArray = {{110, 110, 110, 110, 110}, {107, 107, 107, 107, 107},
        {105, 105, 105, 105, 105}, {104, 104, 104, 104, 104}, {104, 104, 104, 104, 104},};
    ValueLayer dem = RepastGridUtils.arrayToGridValueLayer("dem", demArray);
    double gridSize = 25;

    return new SlopeRiskCalculator(dem, gridSize);
  }

  @After
  public void tearDown() {
    this.fireSpreader = null;
    this.lct = null;
    this.vegetationMoistureParam = null;
  }

  @Test
  public void testInit() {
    double meanNumFiresPerYear = 32.0;
    new DefaultFireManager(this.fireSpreader, this.flamChecker,
        this.lct.getDimensions(), meanNumFiresPerYear);
  }

  @Test
  public void testNumFires() {
    DefaultFireManager fireManager = new DefaultFireManager(this.fireSpreader, this.flamChecker,
        this.lct.getDimensions(), 10.1);
    int n = fireManager.numFires();
    logger.debug("Num fires sampled: " + n);
    assertTrue(n > 0);
  }

  @Test
  public void testFiresInitiated() {
    FireManager fireManager = new DefaultFireManager(this.fireSpreader, this.flamChecker,
        this.lct.getDimensions(), 5.0);

    LctProportionAggregator propAggregator = new LctProportionAggregator(this.lct);
    double initPropBurnt = propAggregator.getLctProportions().get(Lct.Burnt);
    logger.error("Before fire: " + RepastGridUtils.valueLayerToString(this.lct) + "\n");

    fireManager.startFires();

    double finalPropBurnt = propAggregator.getLctProportions().get(Lct.Burnt);
    logger.error("After fire: " + RepastGridUtils.valueLayerToString(this.lct) + "\n");

    assertTrue(finalPropBurnt > initPropBurnt);

  }

}
