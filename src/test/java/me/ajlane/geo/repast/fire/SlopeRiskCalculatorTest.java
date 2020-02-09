package me.ajlane.geo.repast.fire;

import static org.junit.Assert.*;
import org.junit.Test;
import me.ajlane.geo.Direction;
import me.ajlane.geo.repast.RepastGridUtils;
import repast.simphony.valueLayer.IGridValueLayer;
import repast.simphony.valueLayer.ValueLayer;

public class SlopeRiskCalculatorTest {

  private final static double TOLERANCE = 0.00001;
  private final static double GRID_CELL_SIZE = 10;  // Edge length of grid cells

  @Test
  public void testSlopeRiskCalc() {
    ValueLayer dem = testDemGvl();
    SlopeRiskCalculator srCalc = new SlopeRiskCalculator(dem, GRID_CELL_SIZE);

    assertEquals(1.0, srCalc.getSlopeRisk(1, 1, Direction.E), TOLERANCE);  // > 0%, < 5%
    assertEquals(1.05, srCalc.getSlopeRisk(1, 1, Direction.NE), TOLERANCE);  // > 5%, < 15%
    assertEquals(1.10, srCalc.getSlopeRisk(1, 1, Direction.N), TOLERANCE);  // > 15%, < 25%
    assertEquals(1.20, srCalc.getSlopeRisk(1, 1, Direction.NW), TOLERANCE);  // > 25%

    assertEquals(1.0, srCalc.getSlopeRisk(1, 1, Direction.W), TOLERANCE);  // > -5%, < 0%
    assertEquals(0.95, srCalc.getSlopeRisk(1, 1, Direction.SW), TOLERANCE);  // > -15%, < 5%
    assertEquals(0.90, srCalc.getSlopeRisk(1, 1, Direction.S), TOLERANCE);  // > -25%, < -15%
    assertEquals(0.80, srCalc.getSlopeRisk(1, 1, Direction.SE), TOLERANCE);  // < -25%

  }

  @Test
  public void testSlopeIsNullAtEdge() {
    ValueLayer dem = testDemGvl();
    SlopeRiskCalculator srCalc = new SlopeRiskCalculator(dem, GRID_CELL_SIZE);

    assertNull(srCalc.getSlopeRisk(0, 2, Direction.W));
    assertNull(srCalc.getSlopeRisk(0, 2, Direction.N));
    assertNotNull(srCalc.getSlopeRisk(0, 2, Direction.E));

    assertNull(srCalc.getSlopeRisk(0, 1, Direction.W));
    assertNotNull(srCalc.getSlopeRisk(0, 1, Direction.E));

    assertNull(srCalc.getSlopeRisk(2, 1, Direction.E));
    assertNotNull(srCalc.getSlopeRisk(2, 1, Direction.W));

    assertNull(srCalc.getSlopeRisk(0, 1, Direction.S));
    assertNotNull(srCalc.getSlopeRisk(0, 1, Direction.E));

  }

  /**
   * <p>
   * Assuming a DEM has a square cell edge length of 10 m. If the cell in the centre of a 3 x 3
   * neighbourhood has an elevation of 100 m. Call the centre cell i. The <emph>run</emph> from
   * cell i to another cell in the neighbourhood j is 10 m \times \alpha{ij}, where \alpha{ij} = 1
   * if cell j is is edge-adjacent to cell i, and \alpha{ij} = \sqrt{2} if cell j is
   * corner-adjacent to cell i. The slope from cell i to cell j is given by:
   * </p>
   * <p>
   * s_{i \rightarrow j} = \frac{e_i - e_j}{\alpha_{ij} d} \times 100%
   * </p>
   * <p>
   * where e_i - e_j is the <emph>rise</emph> of the slope. The rises needed to produce a slope of 5 m, 15 m, and 25 m
   * are shown in Table 1.
   * </p>
   *
   * <table style="width:300px">
   * <caption>Table 1: Rise over 10 m run needed to produce corresponding slope percentages for both edge-adjacent and corner-adjacent cells</caption>
   * <tr>
   *  <th>Slope [%]</th>
   *  <th>Rise over 10 m run [m]</th>
   *  <th>Rise over 10 \times \sqrt{2} m run [m]</th>
   * </tr>
   * <tr>
   *  <td>5</td>
   *  <td>0.5</td>
   *  <td>0.7</td>
   * </tr>
   * <tr>
   *  <td>15</td>
   *  <td>1.5</td>
   *  <td>2.1</td>
   * </tr>
   * <tr>
   *  <td>25</td>
   *  <td>2.5</td>
   *  <td>3.5</td>
   * </tr>
   * </table>
   *
   * <p>
   * The following cells in the test array used in this function correspond to specific percentage
   * slopes:
   *
   * <ul>
   * <li>{@code array[1][2]}: 100.3 m < 5% increase on 100 m</li>
   * <li>{@code array[0][2]}: 5% < 101 m < 15% increase on 100 m</li>
   * <li>{@code array[0][1]}: 15% < 102 m < 25% increase on 100 m</li>
   * <li>{@code array[0][0]}: 104 m > 25% increase on 100 m</li>
   * </p>
   *
   * @return 3 x 3 test grid to use to test the slope risk calculation.
   */
  private IGridValueLayer testDemGvl() {
    double array[][] = {
        {104.0, 102.0, 101.0},
        {99.7,  100.0, 100.3},
        {99.0,  98.0,  96.0 }
    };

    return RepastGridUtils.arrayToGridValueLayer("testDem", array);
  }

}
