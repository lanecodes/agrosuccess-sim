package repast.model.agrosuccess.anthro;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import java.util.LinkedHashSet;
import java.util.Set;
import org.easymock.EasyMockRule;
import org.easymock.Mock;
import org.junit.Rule;
import org.junit.Test;
import me.ajlane.geo.repast.GridPointLocator;
import repast.simphony.space.grid.GridPoint;

public class EuclidDistanceCalculatorTest {

  double METRE_ERROR = 0.001;

  @Rule
  public EasyMockRule rule = new EasyMockRule(this);

  @Mock
  private GridPointLocator gridPointLoc;

  @Test
  public void testConstructor() {
    double gridCellEdgeLength = 25.;
    new EuclidDistanceCalculator(gridCellEdgeLength, this.gridPointLoc);
  }

  /*
   * The mocked GridPointLocator behaves as if it was configured with a 5 x 5 grid with origin at
   * (0,0).
   */
  @Test
  public void testDistInMetres() {
    double gridCellEdgeLength = 25.;

    GridPoint gp1 = new GridPoint(0, 0);
    GridPoint gp2 = new GridPoint(0, 1);
    GridPoint gp3 = new GridPoint(1, 1);
    GridPoint gp4 = new GridPoint(2, 3);

    expect(this.gridPointLoc.isInGrid(gp1)).andReturn(true);
    expectLastCall().times(2);
    expect(this.gridPointLoc.isInGrid(gp2)).andReturn(true);
    expectLastCall().times(2);
    expect(this.gridPointLoc.isInGrid(gp3)).andReturn(true);
    expectLastCall().times(2);
    expect(this.gridPointLoc.isInGrid(gp4)).andReturn(true);
    expectLastCall().times(2);
    replay(this.gridPointLoc);

    DistanceCalculator distCalc =
        new EuclidDistanceCalculator(gridCellEdgeLength, this.gridPointLoc);

    assertEquals(distCalc.distInMetres(gp1, gp2), 25., this.METRE_ERROR);
    assertEquals(distCalc.distInMetres(gp2, gp1), 25., this.METRE_ERROR);
    assertEquals(distCalc.distInMetres(gp3, gp4), 55.902, this.METRE_ERROR);
    assertEquals(distCalc.distInMetres(gp4, gp3), 55.902, this.METRE_ERROR);

    verify(this.gridPointLoc);
  }

  /*
   * The mocked GridPointLocator behaves as if it was configured with a 5 x 5 grid with origin at
   * (0,0).
   */
  @Test(expected = IndexOutOfBoundsException.class)
  public void testDistInMetresThrowsError() {
    double gridCellEdgeLength = 25.;

    GridPoint gp1 = new GridPoint(0, 1);
    GridPoint outOfGridPoint = new GridPoint(6, 5);

    expect(this.gridPointLoc.isInGrid(gp1)).andReturn(true);
    expect(this.gridPointLoc.isInGrid(outOfGridPoint)).andReturn(false);
    replay(this.gridPointLoc);

    DistanceCalculator distCalc =
        new EuclidDistanceCalculator(gridCellEdgeLength, this.gridPointLoc);

    distCalc.distInMetres(gp1, outOfGridPoint);

    verify(this.gridPointLoc);
  }

  /*
   * The mocked GridPointLocator behaves as if it was configured with a 5 x 5 grid with origin at
   * (0,0).
   */
  @Test
  public void testPropMaxDist() {
    double gridCellEdgeLength = 25.;

    GridPoint refPoint = new GridPoint(2, 1); // two furthest points at (0,4) and (4,4)
    GridPoint tgtPoint = new GridPoint(1, 3);

    GridPoint fp1 = new GridPoint(0, 4);
    GridPoint fp2 = new GridPoint(4, 4);
    Set<GridPoint> furthestPoints = new LinkedHashSet<>();
    furthestPoints.add(fp1);
    furthestPoints.add(fp2);

    expect(this.gridPointLoc.isInGrid(refPoint)).andReturn(true);
    expectLastCall().times(2);
    expect(this.gridPointLoc.isInGrid(tgtPoint)).andReturn(true);
    expect(this.gridPointLoc.isInGrid(fp1)).andReturn(true);
    expect(this.gridPointLoc.furthestPoints(refPoint)).andReturn(furthestPoints);
    replay(this.gridPointLoc);


    DistanceCalculator distCalc =
        new EuclidDistanceCalculator(gridCellEdgeLength, this.gridPointLoc);

    // maxDist (ref to furthest) = [(0 - 2)^2 + (4 - 1)^2]^(1/2) = [4 + 9]^(1/2) = 3.6056
    // dist (ref to target) = [(1 - 2)^2 + (3 - 1)^2]^(1/2) = [1 + 4]^(1/2) = 2.2361
    // propMaxDist = 2.2361 / 3.6056 = 0.6202
    assertEquals(0.620, distCalc.propMaxDist(refPoint, tgtPoint), this.METRE_ERROR);

    verify(this.gridPointLoc);
  }

  /*
   * The mocked GridPointLocator behaves as if it was configured with a 5 x 5 grid with origin at
   * (0,0).
   */
  @Test
  public void testPropMaxDistWhenTargetIsAFurthestPoint() {
    double gridCellEdgeLength = 25.;

    GridPoint refPoint = new GridPoint(2, 1); // two furthest points at (0,4) and (4,4)
    GridPoint tgtPoint = new GridPoint(4, 4);

    GridPoint fp1 = new GridPoint(0, 4);
    GridPoint fp2 = new GridPoint(4, 4);
    Set<GridPoint> furthestPoints = new LinkedHashSet<>();
    furthestPoints.add(fp1);
    furthestPoints.add(fp2);

    expect(this.gridPointLoc.isInGrid(refPoint)).andReturn(true);
    expectLastCall().times(2);
    expect(this.gridPointLoc.isInGrid(tgtPoint)).andReturn(true);
    expect(this.gridPointLoc.isInGrid(fp1)).andReturn(true);
    expect(this.gridPointLoc.furthestPoints(refPoint)).andReturn(furthestPoints);
    replay(this.gridPointLoc);

    DistanceCalculator distCalc =
        new EuclidDistanceCalculator(gridCellEdgeLength, this.gridPointLoc);

    // target point is as far away as it can be from the reference point within the grid
    assertEquals(1.000, distCalc.propMaxDist(refPoint, tgtPoint), this.METRE_ERROR);

    verify(this.gridPointLoc);
  }

  /**
   * Intended to test that cached values for the furthest point from the reference point are used
   * correctly.
   */
  @Test
  public void testMultipleCallsReturnSameResult() {
    double gridCellEdgeLength = 25.;

    GridPoint refPoint = new GridPoint(2, 1); // two furthest points at (0,4) and (4,4)
    GridPoint tgtPoint = new GridPoint(4, 4);

    GridPoint fp1 = new GridPoint(0, 4);
    GridPoint fp2 = new GridPoint(4, 4);
    Set<GridPoint> furthestPoints = new LinkedHashSet<>();
    furthestPoints.add(fp1);
    furthestPoints.add(fp2);

    expect(this.gridPointLoc.isInGrid(refPoint)).andReturn(true).anyTimes();
    expect(this.gridPointLoc.isInGrid(tgtPoint)).andReturn(true).anyTimes();
    expect(this.gridPointLoc.isInGrid(fp1)).andReturn(true).anyTimes();
    expect(this.gridPointLoc.furthestPoints(refPoint)).andReturn(furthestPoints).once();
    replay(this.gridPointLoc);

    DistanceCalculator distCalc =
        new EuclidDistanceCalculator(gridCellEdgeLength, this.gridPointLoc);

    // target point is as far away as it can be from the reference point within the grid
    assertEquals(1.000, distCalc.propMaxDist(refPoint, tgtPoint), this.METRE_ERROR);
    // this.gridPointLoc.furthestPoints(refPoint) only called once but the same distance returned
    // multiple times by using cached values.
    assertEquals(1.000, distCalc.propMaxDist(refPoint, tgtPoint), this.METRE_ERROR);
    assertEquals(1.000, distCalc.propMaxDist(refPoint, tgtPoint), this.METRE_ERROR);
    assertEquals(1.000, distCalc.propMaxDist(refPoint, tgtPoint), this.METRE_ERROR);

    verify(this.gridPointLoc);
  }

}
