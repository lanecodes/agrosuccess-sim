package me.ajlane.geo.repast.fire;

import static org.junit.Assert.*;
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
import repast.simphony.valueLayer.IGridValueLayer;
import repast.simphony.valueLayer.ValueLayer;

public class FireSpreaderTest {

  final static Logger logger = Logger.getLogger(FireSpreaderTest.class);

  private IGridValueLayer lct;
  private final SlopeRiskCalculator srCalc = getTestSlopeRiskCalculator();
  private final WindRiskCalculator wrCalc = new WindRiskCalculator();
  private final Map<Lct, Double> lcfMap = getTestLcfMap();
  private final Map<Direction, Double> windDirProbMap = getTestWindDirProbMap();
  private final Map<WindSpeed, Double> windSpeedProbMap = getTestWindSpeedProbMap();

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
    int[][] demArray = {
        {110, 110, 110, 110, 110},
        {107, 107, 107, 107, 107},
        {105, 105, 105, 105, 105},
        {104, 104, 104, 104, 104},
        {104, 104, 104, 104, 104},
    };
    ValueLayer dem = RepastGridUtils.arrayToGridValueLayer("dem", demArray);
    double gridSize = 25;

    return new SlopeRiskCalculator(dem, gridSize);
  }

  private static IGridValueLayer getTestLct() {
    int[][] lctArray = {
        {0, 0, 2, 2, 2},
        {1, 1, 3, 3, 3},
        {9, 3, 3, 3, 3},
        {9, 9, 9, 9, 9},
        {9, 9, 9, 9, 9},
    };
    return RepastGridUtils.arrayToGridValueLayer("dem", lctArray);
  }

  @Before
  public void setup() {
    this.lct = getTestLct();
  }

  @After
  public void tearDown() {
    this.lct = null;
  }

  @Test
  public void testInit() {
    new FireSpreader(this.lct, this.srCalc, this.wrCalc, this.lcfMap, this.windDirProbMap,
        this.windSpeedProbMap);
  }

  @Test
  public void testSpreadFire() {
    FireSpreader spreader =  new FireSpreader(this.lct, this.srCalc, this.wrCalc, this.lcfMap, this.windDirProbMap,
        this.windSpeedProbMap);

    LctProportionAggregator propAggregator = new LctProportionAggregator(this.lct);
    double initPropBurnt = propAggregator.getLctProportions().get(Lct.Burnt);
    logger.debug("Before fire: " + RepastGridUtils.gridValueLayerToString(this.lct) + "\n");

    GridPoint initialFire = new GridPoint(2, 2);
    spreader.spreadFire(initialFire);

    double finalPropBurnt = propAggregator.getLctProportions().get(Lct.Burnt);
    logger.debug("After fire: " + RepastGridUtils.gridValueLayerToString(this.lct) + "\n");

    assertTrue(finalPropBurnt > initPropBurnt);
  }

}
