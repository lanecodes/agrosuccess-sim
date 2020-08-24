package me.ajlane.geo.repast.fire;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.getCurrentArguments;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import org.apache.log4j.Logger;
import org.easymock.EasyMockRule;
import org.easymock.IAnswer;
import org.easymock.Mock;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import me.ajlane.geo.Direction;
import me.ajlane.geo.repast.RepastGridUtils;
import me.ajlane.geo.repast.RepastGridUtils.GridPointIterable;
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
  private final double vegetationMoistureParam = 0.25; // lambda in thesis notation

  @Rule
  public EasyMockRule rule = new EasyMockRule(this);

  @Mock
  private FlammabilityChecker<GridPoint> flammabilityChecker;

  // Mocks logic for identifying flammable grid cells
  private IAnswer<Boolean> flammableCellRule = new IAnswer<Boolean>() {
    @Override
    public Boolean answer() throws Throwable {
      GridPoint pt = (GridPoint) getCurrentArguments()[0];
      int lctCode = (int) lct.get(pt.getX(), pt.getY());
      if (lctCode == Lct.WaterQuarry.getCode() || lctCode == Lct.Burnt.getCode()) {
        return false;
      }
      return true;
    }
  };

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
    this.fireCount = null;
  }

  @Test
  public void testInit() {
    new DefaultFireSpreader(this.lct, this.fireCount, this.srCalc, this.wrCalc,
        this.flammabilityChecker,
        this.lcfMap, this.windDirProbMap, this.windSpeedProbMap, vegetationMoistureParam);
  }

  @Test
  public void testFireCountIncremented() {
    FireSpreader<GridPoint> spreader = new DefaultFireSpreader(this.lct, this.fireCount,
        this.srCalc, this.wrCalc, this.flammabilityChecker, this.lcfMap, this.windDirProbMap,
        this.windSpeedProbMap, this.vegetationMoistureParam);

    expect(this.flammabilityChecker.isFlammable(anyObject(GridPoint.class)))
        .andAnswer(flammableCellRule).atLeastOnce();

    replay(this.flammabilityChecker);

    logger.debug("FireCount before fire: "
        + RepastGridUtils.valueLayerToString(this.fireCount) + "\n");
    GridPoint initialFire = new GridPoint(2, 2);
    spreader.spreadFire(initialFire);

    assertEquals(1, (int) this.fireCount.get(2, 2));
    logger.debug("FireCount after fire: "
        + RepastGridUtils.valueLayerToString(this.fireCount) + "\n");

    verify(this.flammabilityChecker);
  }

  @Test
  public void testSpreadFire() {
    FireSpreader<GridPoint> spreader = new DefaultFireSpreader(this.lct, this.fireCount,
        this.srCalc, this.wrCalc, this.flammabilityChecker, this.lcfMap, this.windDirProbMap,
        this.windSpeedProbMap, this.vegetationMoistureParam);

    expect(this.flammabilityChecker.isFlammable(anyObject(GridPoint.class)))
        .andAnswer(flammableCellRule).atLeastOnce();

    LctProportionAggregator propAggregator = new LctProportionAggregator(this.lct);
    double initPropBurnt = propAggregator.getLctProportions().get(Lct.Burnt);
    logger.debug("Before fire: " + RepastGridUtils.valueLayerToString(this.lct) + "\n");

    replay(this.flammabilityChecker);

    GridPoint initialFire = new GridPoint(2, 2);
    spreader.spreadFire(initialFire);

    double finalPropBurnt = propAggregator.getLctProportions().get(Lct.Burnt);
    logger.debug("After fire: " + RepastGridUtils.valueLayerToString(this.lct) + "\n");

    assertTrue(finalPropBurnt > initPropBurnt);

    verify(this.flammabilityChecker);
  }

  @Test
  public void testFireEventReturned() {
    FireSpreader<GridPoint> spreader = new DefaultFireSpreader(this.lct, this.fireCount,
        this.srCalc, this.wrCalc, this.flammabilityChecker, this.lcfMap, this.windDirProbMap,
        this.windSpeedProbMap, this.vegetationMoistureParam);

    expect(this.flammabilityChecker.isFlammable(anyObject(GridPoint.class)))
        .andAnswer(flammableCellRule).atLeastOnce();

    replay(this.flammabilityChecker);

    GridPoint initialFire = new GridPoint(2, 2);
    List<GridPoint> burntCells = spreader.spreadFire(initialFire);

    // System.out.println(RepastGridUtils.valueLayerToString(this.fireCount));
    // System.out.println(burntCells);

    Consumer<GridPoint> testBurntCellsMatchFireCount = gridPoint -> {
      if (burntCells.contains(gridPoint)) {
        assertEquals(1, (int) this.fireCount.get(gridPoint.getX(), gridPoint.getY()));
      } else {
        assertEquals(0, (int) this.fireCount.get(gridPoint.getX(), gridPoint.getY()));
      }
    };

    Iterable<GridPoint> gridCells = new GridPointIterable(this.lct);
    gridCells.forEach(testBurntCellsMatchFireCount);

    verify(this.flammabilityChecker);
  }

}
