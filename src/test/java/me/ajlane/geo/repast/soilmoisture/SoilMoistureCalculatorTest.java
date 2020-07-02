package me.ajlane.geo.repast.soilmoisture;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import org.easymock.EasyMockRule;
import org.easymock.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import me.ajlane.geo.CartesianGridDouble2D;
import me.ajlane.geo.DefaultCartesianGridDouble2D;
import me.ajlane.geo.GridLoc;
import me.ajlane.geo.repast.GridValueLayerAdapter;
import repast.model.agrosuccess.LscapeLayer;
import repast.simphony.space.Dimensions;
import repast.simphony.space.grid.StrictBorders;
import repast.simphony.valueLayer.GridValueLayer;
import repast.simphony.valueLayer.IGridValueLayer;

/**
 * @author Andrew Lane
 */
public class SoilMoistureCalculatorTest {

  private IGridValueLayer soilMoistureLayer;
  private SoilMoistureUpdater soilMoistureCalc;

  @Rule
  public EasyMockRule rule = new EasyMockRule(this);

  @Mock
  private LandscapeFlow landscapeFlow;

  @Mock
  private Iterator<CatchmentFlow> catchmentFlowIterator;

  @Mock
  private CatchmentFlow catchmentFlow1;

  @Mock
  private CatchmentFlow catchmentFlow2;

  @Mock
  private CurveNumberGenerator curveNumberGen;

  @Before
  public void setUp() {
    soilMoistureLayer = new GridValueLayer(LscapeLayer.SoilMoisture.name(), 0, true,
        new StrictBorders(), new int[] {3, 3}, new int[] {0, 0});
  }

  /**
   * Update soil moisture layer and check its values are as expected after a single time step.
   *
   * The relevant quantities in this test are related by the following equations, where $i$ indexes
   * in individual grid cell.
   *
   * <ul>
   * <li>Maximum water retention, $S_i = 25.4 (1000 / CN_i - 10)$</li>
   * <li>Total water input, $T_i = P + \sum_{j \in D_i} Q_j$ where $P$ is precipitation and $D_i$ is
   * the set of cells which drain into cell $i$</li>
   * <li>Runoff, $Q_i = (T_i - 0.2 * S_i)^2 / (T_i + 0.8 * S_i)$</li>
   * <li>Soil moisture, $M_i = T_i - Q_i$</li>
   * </ul>
   *
   * The flow network used as an example in this test is shown in <a href="#fig-flow-network">Figure
   * 1</a>.
   *
   * <figure id="fig-flow-network">
   * <img src="./doc-files/flow-drain-graph-2-outlets.svg" style="max-width: 500px;"/> <figcaption>
   * <span>Figure 1:</span class=fig-name> Flow source network used in this test. </figcaption> </figure>
   *
   * We assume the following 3 x 3 slope map
   *
   * <pre>
   * slope [%] = 5    5    5
   *             4    4    4
   *             2    2    2
   * </pre>
   *
   * And curve number map
   *
   * <pre>
   * CN [dimensionless] = 39   39   39
   *                      39   39   39
   *                      35   35   35
   * </pre>
   *
   * Resulting in a maximum runoff map of
   *
   * <pre>
   * S [mm] = 397.3 397.3 397.3
   *          397.3 397.3 397.3
   *          471.7 471.7 471.7
   * </pre>
   *
   * We choose the following enumeration of cells in the grid
   *
   * <pre>
   * 7    8    9
   * 4    5    6
   * 1    2    3
   * </pre>
   *
   * For the test we introduce 496 mm of precipitation into the landscape specified above. The
   * resulting runoff and soil moisture values expected according to the equations given above are
   * as follows.
   *
   * <table>
   * <tbody>
   * <tr>
   * <td>Cell #</td>
   * <td>Total water [mm]</td>
   * <td>Runoff [mm]</td>
   * <td>Soil Moisture [mm]</td>
   * </tr>
   * <tr>
   * <td>1</td>
   * <td>496.00</td>
   * <td>184.72</td>
   * <td>311.28</td>
   * </tr>
   * <tr>
   * <td>2</td>
   * <td>496.00</td>
   * <td>184.72</td>
   * <td>311.28</td>
   * </tr>
   * <tr>
   * <td>3</td>
   * <td>496.00</td>
   * <td>184.72</td>
   * <td>311.28</td>
   * </tr>
   * <tr>
   * <td>4</td>
   * <td>680.70</td>
   * <td>362.00</td>
   * <td>318.70</td>
   * </tr>
   * <tr>
   * <td>5</td>
   * <td>865.45</td>
   * <td>522.09</td>
   * <td>343.36</td>
   * </tr>
   * <tr>
   * <td>6</td>
   * <td>496.00</td>
   * <td>213.19</td>
   * <td>282.81</td>
   * </tr>
   * <tr>
   * <td>7</td>
   * <td>1071.21</td>
   * <td>708.09</td>
   * <td>363.12</td>
   * </tr>
   * <tr>
   * <td>8</td>
   * <td>496.00</td>
   * <td>213.19</td>
   * <td>282.81</td>
   * </tr>
   * <tr>
   * <td>9</td>
   * <td>1231.28</td>
   * <td>856.41</td>
   * <td>374.87</td>
   * </tr>
   * </tbody>
   * </table>
   *
   * <h3>Notes</h3>
   * <p>
   * Approach to mocking iterables relies on deconstructing the syntactic sugar constituting the
   * enhanced {@code for} loop. See <a href=
   * "https://bophelomkhwanazi.wordpress.com/2017/11/02/java-mocking-an-advanced-for-loop-with-mockito/">here</a>.
   * </p>
   */
  @Test
  public void testSoilMoistureAsExpectedAfterOneTimeStep() {
    // Pine everywhere
    CartesianGridDouble2D landCover =
        new DefaultCartesianGridDouble2D(new double[][] {{6, 6, 6}, {6, 6, 6}, {6, 6, 6}});

    // Type A everywhere
    CartesianGridDouble2D soilMap =
        new DefaultCartesianGridDouble2D(new double[][] {{0, 0, 0}, {0, 0, 0}, {0, 0, 0}});

    // Slope increases from bottom to top
    CartesianGridDouble2D slope =
        new DefaultCartesianGridDouble2D(new double[][] {{5, 5, 5}, {4, 4, 4}, {2, 2, 2}});

    soilMoistureCalc = new SoilMoistureCalculator(new GridValueLayerAdapter(soilMoistureLayer),
        landCover, soilMap, slope, landscapeFlow, curveNumberGen);

    // Assume conventional soil type and land cover type codes used in AgroSuccess
    expect(curveNumberGen.getCurveNumber(2, 0, 6)).andReturn(35).times(3);
    expect(curveNumberGen.getCurveNumber(4, 0, 6)).andReturn(39).times(3);
    expect(curveNumberGen.getCurveNumber(5, 0, 6)).andReturn(39).times(3);

    expect(landscapeFlow.iterator()).andReturn(catchmentFlowIterator);
    expect(catchmentFlowIterator.hasNext()).andReturn(true);
    expect(catchmentFlowIterator.next()).andReturn(catchmentFlow1);

    expect(catchmentFlow1.flowSourceDependencyOrder()).andReturn(new ArrayList<GridLoc>(
        Arrays.asList(new GridLoc(0, 0), new GridLoc(0, 1), new GridLoc(1, 2), new GridLoc(0, 2))));

    expect(catchmentFlow1.getSourceCells(new GridLoc(0, 0))).andReturn(new HashSet<GridLoc>());
    expect(catchmentFlow1.getSourceCells(new GridLoc(0, 1)))
        .andReturn(new HashSet<GridLoc>(Arrays.asList(new GridLoc(0, 0))));
    expect(catchmentFlow1.getSourceCells(new GridLoc(1, 2))).andReturn(new HashSet<GridLoc>());
    expect(catchmentFlow1.getSourceCells(new GridLoc(0, 2)))
        .andReturn(new HashSet<GridLoc>(Arrays.asList(new GridLoc(0, 1), new GridLoc(1, 2))));

    expect(catchmentFlowIterator.hasNext()).andReturn(true);
    expect(catchmentFlowIterator.next()).andReturn(catchmentFlow2);

    expect(catchmentFlow2.flowSourceDependencyOrder())
        .andReturn(new ArrayList<GridLoc>(Arrays.asList(new GridLoc(1, 0), new GridLoc(2, 0),
            new GridLoc(2, 1), new GridLoc(1, 1), new GridLoc(2, 2))));
    expect(catchmentFlow2.getSourceCells(new GridLoc(1, 0))).andReturn(new HashSet<GridLoc>());
    expect(catchmentFlow2.getSourceCells(new GridLoc(2, 0))).andReturn(new HashSet<GridLoc>());
    expect(catchmentFlow2.getSourceCells(new GridLoc(2, 1))).andReturn(new HashSet<GridLoc>());
    expect(catchmentFlow2.getSourceCells(new GridLoc(1, 1)))
        .andReturn(new HashSet<GridLoc>(Arrays.asList(new GridLoc(1, 0), new GridLoc(2, 0))));
    expect(catchmentFlow2.getSourceCells(new GridLoc(2, 2)))
        .andReturn(new HashSet<GridLoc>(Arrays.asList(new GridLoc(1, 1), new GridLoc(2, 1))));

    expect(catchmentFlowIterator.hasNext()).andReturn(false);

    replay(landscapeFlow, catchmentFlowIterator, curveNumberGen, catchmentFlow1, catchmentFlow2);

    // Calculated by hand using rules specified in thesis
    double[][] expectedSoilMoisture =
        {{363.12, 282.81, 374.87}, {318.70, 343.36, 282.81}, {311.28, 311.28, 311.28}};

    // Update soil moisture layer with 496 mm of precipitation
    soilMoistureCalc.updateSoilMoisture(496.0);

    Dimensions dims = soilMoistureLayer.getDimensions();
    for (int y = 0; y < dims.getHeight(); y++) {
      for (int x = 0; x < dims.getWidth(); x++) {
        assertEquals(expectedSoilMoisture[(int) dims.getHeight() - y - 1][x],
            soilMoistureLayer.get(x, y), 0.1);
      }
    }
    verify(landscapeFlow, catchmentFlowIterator, curveNumberGen, catchmentFlow1, catchmentFlow2);
  }

}
