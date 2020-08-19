package me.ajlane.geo.repast.fire;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.util.HashSet;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import me.ajlane.geo.repast.RepastGridUtils;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.valueLayer.ValueLayer;

public class DefaultFlammabilityCheckerTest {

  private Set<Integer> notFlammableCodes;
  ValueLayer lctLayer;

  @Before
  public void setUp() {
    this.notFlammableCodes = new HashSet<>();
    this.notFlammableCodes.add(0); // Water/ quarry
    this.notFlammableCodes.add(1); // Burnt

    this.lctLayer = RepastGridUtils.arrayToGridValueLayer("lct", new double[][] {
        {5, 5, 4},
        {1, 1, 5},
        {1, 0, 0}});
  }

  @Test
  public void testFlammableCellsIdentifiedAsSuch() {
    FlammabilityChecker<GridPoint> flamChecker =
        new DefaultFlammabilityChecker(this.lctLayer);

    assertTrue(flamChecker.isFlammable(new GridPoint(0, 2)));
    assertTrue(flamChecker.isFlammable(new GridPoint(1, 2)));
    assertTrue(flamChecker.isFlammable(new GridPoint(2, 2)));
    assertTrue(flamChecker.isFlammable(new GridPoint(2, 1)));
  }

  @Test
  public void testNonFlammableCellsIdentifiedAsSuch() {
    FlammabilityChecker<GridPoint> flamChecker =
        new DefaultFlammabilityChecker(this.lctLayer);

    assertFalse(flamChecker.isFlammable(new GridPoint(0, 0)));
    assertFalse(flamChecker.isFlammable(new GridPoint(1, 0)));
    assertFalse(flamChecker.isFlammable(new GridPoint(2, 0)));
    assertFalse(flamChecker.isFlammable(new GridPoint(0, 1)));
    assertFalse(flamChecker.isFlammable(new GridPoint(1, 1)));
  }

}
