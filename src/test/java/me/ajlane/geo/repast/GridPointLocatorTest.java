package me.ajlane.geo.repast;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import repast.simphony.space.grid.GridPoint;

public class GridPointLocatorTest {

  int[] gridDimension, gridOrigin;

  @Before
  public void setUp() {
    gridDimension = new int[] {3, 3};
    gridOrigin = new int[] {0, 0};
  }

  @Test
  public void testConstructor() {
    new GridPointLocator(this.gridDimension, this.gridOrigin);
  }

  @Test
  public void testIsInGrid() {
    GridPointLocator gpl = new GridPointLocator(gridDimension, gridOrigin);

    assertTrue(gpl.isInGrid(new GridPoint(0, 2)));
    assertFalse(gpl.isInGrid(new GridPoint(1, 3)));
  }

  @Test
  public void testFurthestPoints() {
    Set<GridPoint> furthestPoints;
    GridPointLocator gpl = new GridPointLocator(gridDimension, gridOrigin);

    // 1. Ref point bottom left, single furthest point upper right
    furthestPoints = gpl.furthestPoints(new GridPoint(0, 0));
    assertEquals(1, furthestPoints.size());
    assertTrue(furthestPoints.contains(new GridPoint(2, 2)));

    // 2. Ref point bottom right, single furthest point upper left
    furthestPoints = gpl.furthestPoints(new GridPoint(0, 2));
    assertEquals(1, furthestPoints.size());
    assertTrue(furthestPoints.contains(new GridPoint(2, 0)));

    // 3. Ref point bottom centre, two furthest points upper left and upper right
    furthestPoints = gpl.furthestPoints(new GridPoint(1, 0));
    assertEquals(2, furthestPoints.size());
    assertTrue(furthestPoints.contains(new GridPoint(0, 2)));
    assertTrue(furthestPoints.contains(new GridPoint(2, 2)));

    // 4. Ref point centre centre, four furthest points in all four corners
    furthestPoints = gpl.furthestPoints(new GridPoint(1, 1));
    assertEquals(4, furthestPoints.size());
    assertTrue(furthestPoints.contains(new GridPoint(0, 0)));
    assertTrue(furthestPoints.contains(new GridPoint(0, 2)));
    assertTrue(furthestPoints.contains(new GridPoint(2, 2)));
    assertTrue(furthestPoints.contains(new GridPoint(2, 0)));
  }

}
