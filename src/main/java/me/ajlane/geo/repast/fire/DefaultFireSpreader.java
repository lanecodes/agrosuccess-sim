package me.ajlane.geo.repast.fire;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import org.apache.commons.math3.distribution.EnumeratedDistribution;
import org.apache.commons.math3.util.Pair;
import org.apache.log4j.Logger;
import me.ajlane.geo.Direction;
import me.ajlane.geo.repast.GridPointKingMove;
import me.ajlane.geo.repast.GridPointMove;
import me.ajlane.geo.repast.RepastGridUtils;
import repast.model.agrosuccess.AgroSuccessCodeAliases.Lct;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.valueLayer.IGridValueLayer;

/**
 * Given an ignition point, consider wind direction and speed, slope, land cover flammability and
 * fuel moisture to spread a fire mechanistically.
 *
 * @see #spreadFire
 *
 * @author Andrew Lane
 *
 */
public class DefaultFireSpreader implements FireSpreader<GridPoint> {

  final static Logger logger = Logger.getLogger(DefaultFireSpreader.class);

  private IGridValueLayer lct;
  private IGridValueLayer fireCount;
  private SlopeRiskCalculator srCalc;
  private WindRiskCalculator wrCalc;
  private FlammabilityChecker<GridPoint> flammabilityChecker;
  private Map<Lct, Double> lcfMap;
  private final double fuelMoistureRiskFactor;
  private EnumeratedDistribution<Direction> windDirSampler;
  private EnumeratedDistribution<WindSpeed> windSpeedSampler;

  public DefaultFireSpreader(IGridValueLayer lct, IGridValueLayer fireCount,
      SlopeRiskCalculator srCalc,
      WindRiskCalculator wrCalc, FlammabilityChecker<GridPoint> flammabilityChecker,
      Map<Lct, Double> lcfMap, Map<Direction, Double> windDirProbMap,
      Map<WindSpeed, Double> windSpeedProbMap, double vegetationMoistureParam) {
    this.lct = lct;
    this.fireCount = fireCount;
    this.srCalc = srCalc;
    this.wrCalc = wrCalc;
    this.flammabilityChecker = flammabilityChecker;
    this.lcfMap = lcfMap;
    this.fuelMoistureRiskFactor = AgroSuccessFuelMoistureRiskTable.fromVegetationMoistureParam(
        vegetationMoistureParam);
    this.windDirSampler = probMapToEnumeratedDistribution(windDirProbMap);
    this.windSpeedSampler = probMapToEnumeratedDistribution(windSpeedProbMap);
  }

  private static <T> EnumeratedDistribution<T> probMapToEnumeratedDistribution(Map<T, Double> map) {
    List<Pair<T, Double>> pairList = new ArrayList<>();
    for (Map.Entry<T, Double> entry : map.entrySet()) {
      pairList.add(new Pair<>(entry.getKey(), entry.getValue()));
    }
    return new EnumeratedDistribution<T>(pairList);
  }

  /**
   * Spread a fire around the landscape from a starting point. See
   * <a href="https://doi.org/10.1016/j.envsoft.2009.03.013"> Millington et al. 2009</a> for the
   * inspiration behind the fire spread mechanism.
   *
   * @param ignitionPoint Point in the landscape where the fire starts
   */
  @Override
  public List<GridPoint> spreadFire(GridPoint ignitionPoint) {
    // Sample concrete wind speed and direction from PMF
    WindSpeed wSpeed = this.windSpeedSampler.sample();
    Direction wDir = this.windDirSampler.sample();

    List<GridPoint> allBurntCells = new ArrayList<>();
    Queue<GridPoint> activeFires = new LinkedList<>();
    activeFires.add(ignitionPoint);

    for (GridPoint currentFirePoint; (currentFirePoint = activeFires.poll()) != null;) {
      burnCellAtPoint(currentFirePoint);
      allBurntCells.add(currentFirePoint);
      spreadFireToNeighbours(currentFirePoint, wSpeed, wDir,
          this.fuelMoistureRiskFactor, activeFires);
    }

    return allBurntCells;
  }

  /**
   * Convert land cover type at {@code gridPoint} to burnt. Increment the {@code fireCount} layer.
   *
   * @param lct Land cover type
   * @param gridPoint Point on grid
   */
  private void burnCellAtPoint(GridPoint gridPoint) {
    this.lct.set(Lct.Burnt.getCode(), gridPoint.getX(), gridPoint.getY());
    int prevBurnCount = (int) this.fireCount.get(gridPoint.getX(), gridPoint.getY());
    this.fireCount.set(prevBurnCount + 1, gridPoint.getX(), gridPoint.getY());
  }

  /**
   * For a single point in the landscape which is currently burning, survey its neighbours and
   * spread the fire to them with a probability determined by wind speed, slope and land cover type.
   *
   * @param currentFirePoint Point which is currently burning
   * @param wSpeed Wind speed
   * @param wDir Wind direction
   * @param fuelMoistureRiskFactor Multiplicative factor reflecting fire spread risk
   * @param activeFires List of currently active fires. If the fire spreads to new cells these will
   *        be added to this list.
   */
  private void spreadFireToNeighbours(GridPoint currentFirePoint, WindSpeed wSpeed, Direction wDir,
      double fuelMoistureRiskFactor, Queue<GridPoint> activeFires) {
    for (Direction fireSpreadDir : Direction.values()) {
      GridPointMove fireFront = new GridPointKingMove(currentFirePoint, fireSpreadDir);
      boolean targetIsInGrid =
          RepastGridUtils.pointInValueLayer2D(fireFront.getEndPoint(), this.lct);
      boolean targetIsInActiveFires = activeFires.contains(fireFront.getEndPoint());
      if (targetIsInGrid && this.flammabilityChecker.isFlammable(fireFront.getEndPoint())
          && !targetIsInActiveFires) {
        Double probFireSpread = probFireSpread(fireFront, wSpeed, wDir, fuelMoistureRiskFactor);
        if (probFireSpread > RandomHelper.nextDouble()) {
          activeFires.add(fireFront.getEndPoint());
        }
      }
    }
  }

  /**
   * Calculate the probability of a fire spreading from a cell to a specific neighbour. This
   * corresponds to Eq. (8) in Millington et al. 2009.
   *
   * @param fireFront Point at which the fire has the possibility of spreading from a cell to one of
   *        its neighbours.
   * @param wSpeed Wind speed
   * @param wDir Wind direction
   * @param fuelMoistureRiskFactor Dimensionless factor parameterising the amount of moisture in the
   *        fuel at the time of the fire
   *
   * @return Probability of a fire spreading along the {@code fireFront}.
   */
  private Double probFireSpread(GridPointMove fireFront, WindSpeed wSpeed, Direction wDir,
      double fuelMoistureRiskFactor) {
    // TODO consider having a local map between int codes and enums.
    Lct endPointLct = lctForGridPoint(fireFront.getEndPoint());
    Double landCoverFlammability = this.lcfMap.get(endPointLct);
    Double slopeRisk =
        this.srCalc.getSlopeRisk(fireFront.getStartPoint(), fireFront.getDirection());
    Double windRisk = this.wrCalc.getRisk(wSpeed, wDir, fireFront.getDirection());
    if (landCoverFlammability == null) {
      throw new RuntimeException("landCoverFlammability unexpectedly null");
    }
    if (slopeRisk == null) {
      throw new RuntimeException("slopeRisk unexpectedly null");
    }
    if (windRisk == null) {
      throw new RuntimeException("windRisk unexpectedly null");
    }

    return landCoverFlammability * slopeRisk * fuelMoistureRiskFactor * windRisk;
  }

  private Lct lctForGridPoint(GridPoint gridPoint) {
    return lctCodeToEnumConst((int) this.lct.get(gridPoint.getX(), gridPoint.getY()));
  }

  private static Lct lctCodeToEnumConst(int lctCode) {
    Lct correctLct = null;
    for (Lct lct : Lct.values()) {
      if (lct.getCode() == lctCode) {
        correctLct = lct;
        break;
      }
    }

    if (correctLct == null) {
      throw new RuntimeException("Lct code unexpectedly didn't match Lct enumeration constant.");
    }
    return correctLct;
  }

}
