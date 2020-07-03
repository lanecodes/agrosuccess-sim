package me.ajlane.geo.repast.soilmoisture;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import me.ajlane.geo.CartesianGridDouble2D;
import me.ajlane.geo.GridLoc;
import me.ajlane.geo.WriteableCartesianGridDouble2D;

/**
 * <p>
 * Differs to {@link LegacySoilMoistureCalculator} by implementing a flow routing algorithm in which
 * the runoff from cells is added to the total water input to the cells down stream of them. See
 * Section 3.4 in Millington et. al 2009.
 * </p>
 * <p>
 * Note that rather than dealing directly with a flow direction map, this class depends on an
 * aspatial abstraction in the form of a {@link LandscapeFlow} to account for how water flows over
 * the landscape.
 * </p>
 *
 * <h3>References</h3>
 * <p>
 * Millington, J. D. A., Wainwright, J., Perry, G. L. W., Romero-Calcerrada, R., & Malamud, B. D.
 * (2009). Modelling Mediterranean landscape succession-disturbance dynamics: A landscape
 * fire-succession model. Environmental Modelling and Software, 24(10), 1196–1208.
 * https://doi.org/10.1016/j.envsoft.2009.03.013
 * </p>
 * <p>
 * United States Department of Agriculture. (2004). Estimation of Direct Runoff from Storm Rainfall.
 * In National Engineering Handbook Part 630 (Hydrology). Retrieved from
 * https://directives.sc.egov.usda.gov/viewerFS.aspx?hid=21422
 * </p>
 *
 * <h3>TODO</h3>
 * <ul>
 * <li>Insert reference to soil moisture calculations in Andrew Lane's thesis once section number is
 * known.</li>
 * </ul>
 *
 * @author Andrew Lane
 */
public class SoilMoistureCalculator implements SoilMoistureUpdater {

  final static Logger logger = Logger.getLogger(SoilMoistureCalculator.class);

  private WriteableCartesianGridDouble2D soilMoisture;
  private CartesianGridDouble2D landCoverType;
  private CartesianGridDouble2D soilMap;
  private CartesianGridDouble2D slope;
  private LandscapeFlow landscapeFlow;
  private CurveNumberGenerator curveNumberGenerator;

  /**
   * Clients should be careful to ensure
   *
   * @param soilMoisture Spatial grid containing soil moisture values to be updated
   * @param landCoverType Spatial grid containing numerical values encoding land-cover types
   * @param soilMap Spatial grid containing numerical values encoding soil type
   * @param slope Spatial grid containing numerical values encoding slope in %
   * @param landscapeFlow Representation of how water flows over the landscape from one grid cell to
   *        another
   * @param curveNumberGenerator Maps slope, soil type and land-cover type to a single number
   *        characterising how much water is retained in each grid cell
   */
  public SoilMoistureCalculator(WriteableCartesianGridDouble2D soilMoisture,
      CartesianGridDouble2D landCoverType, CartesianGridDouble2D soilMap,
      CartesianGridDouble2D slope, LandscapeFlow landscapeFlow,
      CurveNumberGenerator curveNumberGenerator) {
    this.soilMoisture = soilMoisture;
    this.landCoverType = landCoverType;
    this.soilMap = soilMap;
    this.slope = slope;
    this.landscapeFlow = landscapeFlow;
    this.curveNumberGenerator = curveNumberGenerator;
  }

  /**
   * Modify the {@code soilMoisture} map passed to this class's constructor to reflect the
   * precipitation falling on the landscape in this time step.
   */
  @Override
  public void updateSoilMoisture(double annualPrecip) {
    for (CatchmentFlow catchment : this.landscapeFlow) {
      updateCatchment(annualPrecip, catchment);
    }
    logger.debug("Soil moisture updated");
  }

  /**
   * @param annualPrecip Water falling on grid cell in the catchment throughout the year [mm]
   * @param catchment Catchment for which soil moisture is to be updated
   */
  private void updateCatchment(double annualPrecip, CatchmentFlow catchment) {
    Map<GridLoc, Double> runoffMap = new HashMap<>();
    List<GridLoc> cells = catchment.flowSourceDependencyOrder();
    for (GridLoc cell : cells) {
      updateCell(annualPrecip, cell, catchment.getSourceCells(cell), runoffMap);
    }
  }

  /**
   * @param annualPrecip Water falling on grid cell in the year [mm]
   * @param cell Grid cell to be updated in {@code soilMoisture}
   * @param sourceCells Cells which drain into the cell for which soil moisture is being calculated
   * @param runoffMap Supporting data structure containing pre-calculated values of runoff from
   *        upstream grid cells
   */
  private void updateCell(double annualPrecip, GridLoc cell, Set<GridLoc> sourceCells,
      Map<GridLoc, Double> runoffMap) {
    double totalInput = calcTotalWaterInput(annualPrecip, sourceCells, runoffMap);
    double maxPotentialRetention = calcMaxPotentialRetention(getCurveNumber(cell));
    double runoff = calcRunoff(totalInput, maxPotentialRetention);
    runoffMap.put(cell, runoff);
    this.soilMoisture.setValue(calcSoilMoisture(totalInput, runoff), cell);
  }

  /**
   * $T_i = P + \sum_{j \in D_i} Q_j$ where $P$ is precipitation, Q_j is runoff (see
   * {@link #calcRunoff(double, double)}), and $D_i$ is the set of cells draining into cell $i$.
   *
   * @param precip Total annual precipitation [mm]
   * @param sourceCells Set of cells which drain into the cell for which total water input is being
   *        calculated
   * @param runoffMap Supporting data structure containing pre-calculated values for runoff for the
   *        cells upstream of the one for which total water input is being calculated
   * @return Total water input into a grid cell
   */
  private static double calcTotalWaterInput(double precip, Set<GridLoc> sourceCells,
      Map<GridLoc, Double> runoffMap) {
    double total = precip;
    for (GridLoc cell : sourceCells) {
      total += runoffMap.get(cell);
    }
    return total;
  }

  /**
   * $S_i = 25.4 (1000 / CN_i - 10)$. Depends on the cell's curve number, $CN_i$, which accounts for
   * variation in slope, land-cover and soil type. See United States Department of Agriculture,
   * 2004.
   *
   * @param curveNumber
   * @return Maximum potential water retention for a grid cell [mm].
   */
  static double calcMaxPotentialRetention(int curveNumber) {
    return 25.4 * ((1000.0 / curveNumber) - 10);
  }

  /**
   * @param cell Grid cell to calculate curve number for
   * @return Curve number characterising the amount of water retained by this grid cell depending on
   *         its slope and land-cover and soil types. See United States Department of Agriculture,
   *         2004.
   */
  private int getCurveNumber(GridLoc cell) {
    double slope = this.slope.getValue(cell);
    int soilType = (int) this.soilMap.getValue(cell);
    int landCoverType = (int) this.landCoverType.getValue(cell);
    return this.curveNumberGenerator.getCurveNumber(slope, soilType, landCoverType);
  }

  /**
   * $Q_i = (T_i - 0.2 * S_i)^2 / (T_i + 0.8 * S_i)$ where $T_i$ is total water input and $S_i$ is
   * maximum water retention.
   *
   * @param totalWaterInput Total amount of water entering the cell in mm, including both
   *        precipitation and runoff from other cells.
   * @param maximumPotentialWaterRetention Maximum water retention for the cell in mm. See
   *        {@link SoilMoistureCalculator#calcMaxPotentialRetention(int)}
   * @return Runoff from the cell in mm.
   */
  static double calcRunoff(double totalWaterInput, double maximumPotentialWaterRetention) {
    return Math.pow(totalWaterInput - (0.2 * maximumPotentialWaterRetention), 2)
        / (totalWaterInput + (0.8 * maximumPotentialWaterRetention));
  }

  /**
   * $M_i = T_i - Q_i$ where $T_i$ is total water input and $Q_i$ is runoff.
   *
   * @param totalWaterInput Total water entering the cell in a year [mm]
   * @param runoff Amount of water leaving the cell in a year [mm]
   * @return Soil moisture [mm]
   */
  static double calcSoilMoisture(double totalWaterInput, double runoff) {
    return totalWaterInput - runoff;
  }

}
