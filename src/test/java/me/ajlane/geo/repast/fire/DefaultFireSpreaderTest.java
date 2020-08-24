package me.ajlane.geo.repast.fire;

import static org.junit.Assert.assertEquals;
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
import repast.model.agrosuccess.LscapeLayer;
import repast.model.agrosuccess.reporting.LctProportionAggregator;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.valueLayer.GridValueLayer;
import repast.simphony.valueLayer.IGridValueLayer;
import repast.simphony.valueLayer.ValueLayer;

/**
 * TODO: Add test to check {@link LscapeLayer#FireCount} is incremented
 *
 * @author Andrew Lane
 *
 */
public class DefaultFireSpreaderTest {

  final static Logger logger = Logger.getLogger(DefaultFireSpreaderTest.class);

  private IGridValueLayer lct;
  private IGridValueLayer fireCount;
  private final SlopeRiskCalculator srCalc = getTestSlopeRiskCalculator();
  private final WindRiskCalculator wrCalc = new WindRiskCalculator();
  private final Map<Lct, Double> lcfMap = getTestLcfMap();
  private final Map<Direction, Double> windDirProbMap = getTestWindDirProbMap();
  private final Map<WindSpeed, Double> windSpeedProbMap = getTestWindSpeedProbMap();
  private final double vegetationMoistureParam = 0.5; // lambda in thesis notation

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

  private static IGridValueLayer getTestLct() {
    int[][] lctArray =
        {{0, 0, 2, 2, 2}, {1, 1, 3, 3, 3}, {9, 3, 3, 3, 3}, {9, 9, 9, 9, 9}, {9, 9, 9, 9, 9},};
    return RepastGridUtils.arrayToGridValueLayer("lct", lctArray);
  }

  @Before
  public void setup() {
    this.lct = getTestLct();
    this.fireCount = new GridValueLayer("FireCount", 0, true, 5, 5);
  }

  @After
  public void tearDown() {
    this.lct = null;
  }

  @Test
  public void testInit() {
    new DefaultFireSpreader(this.lct, this.fireCount, this.srCalc, this.wrCalc, this.lcfMap,
        this.windDirProbMap, this.windSpeedProbMap, vegetationMoistureParam);
  }

  @Test
  public void testFireCountIncremented() {
    FireSpreader spreader = new DefaultFireSpreader(this.lct, this.fireCount, this.srCalc,
        this.wrCalc, this.lcfMap, this.windDirProbMap, this.windSpeedProbMap,
        this.vegetationMoistureParam);

    logger.debug("FireCount before fire: "
        + RepastGridUtils.valueLayerToString(this.fireCount) + "\n");
    GridPoint initialFire = new GridPoint(2, 2);
    spreader.spreadFire(initialFire);

    assertEquals(1, (int) this.fireCount.get(2, 2));
    logger.debug("FireCount after fire: "
        + RepastGridUtils.valueLayerToString(this.fireCount) + "\n");
  }

  @Test
  public void testSpreadFire() {
    FireSpreader spreader = new DefaultFireSpreader(this.lct, this.fireCount, this.srCalc,
        this.wrCalc, this.lcfMap, this.windDirProbMap, this.windSpeedProbMap,
        this.vegetationMoistureParam);

    LctProportionAggregator propAggregator = new LctProportionAggregator(this.lct);
    double initPropBurnt = propAggregator.getLctProportions().get(Lct.Burnt);
    logger.debug("Before fire: " + RepastGridUtils.valueLayerToString(this.lct) + "\n");

    GridPoint initialFire = new GridPoint(2, 2);
    spreader.spreadFire(initialFire);

    double finalPropBurnt = propAggregator.getLctProportions().get(Lct.Burnt);
    logger.debug("After fire: " + RepastGridUtils.valueLayerToString(this.lct) + "\n");

    assertTrue(finalPropBurnt > initPropBurnt);
  }

}
