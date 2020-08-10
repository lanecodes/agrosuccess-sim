package me.ajlane.geo.repast.succession;

import static me.ajlane.geo.repast.RepastGridUtils.arrayToGridValueLayer;
import static org.junit.Assert.assertEquals;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import repast.simphony.valueLayer.IGridValueLayer;
import repast.simphony.valueLayer.ValueLayer;

/**
 *
 * @author Andrew Lane
 *
 */
public class OakAgeUpdaterTest {

  OakAgeUpdater oakAgeUpdater;
  IGridValueLayer oakAgeLayer;

  private enum TestLct {
    Shrubland(4), TransForest(6), Pine(5), Oak(8);

    private int code;

    TestLct(int code) {
      this.code = code;
    }

    public int getCode() {
      return this.code;
    }
  }

  @Before
  public void setUp() {
    Set<Integer> matureOakCodes = new HashSet<>(
        Arrays.asList(TestLct.Oak.getCode(), TestLct.TransForest.getCode()));
    this.oakAgeLayer = getTestOakAgeLayer();
    this.oakAgeUpdater = new OakAgeUpdater(this.oakAgeLayer, getTestLandCoverTypeLayer(),
        matureOakCodes, -1);
  }

  @Test
  public void testUpdate() {
    this.oakAgeUpdater.update();
    assertEquals(11, (int) this.oakAgeLayer.get(0, 2));
    assertEquals(12, (int) this.oakAgeLayer.get(1, 2));
    assertEquals(-1, (int) this.oakAgeLayer.get(2, 2));
    assertEquals(3, (int) this.oakAgeLayer.get(0, 1));
    assertEquals(10, (int) this.oakAgeLayer.get(1, 1));
    assertEquals(-1, (int) this.oakAgeLayer.get(2, 1));
    assertEquals(3, (int) this.oakAgeLayer.get(0, 0));
    assertEquals(-1, (int) this.oakAgeLayer.get(1, 0));
    assertEquals(-1, (int) this.oakAgeLayer.get(2, 0));
  }

  private ValueLayer getTestLandCoverTypeLayer() {
    return arrayToGridValueLayer("Lct", new double[][] {
        {TestLct.Oak.getCode(), TestLct.Oak.getCode(), TestLct.Pine.getCode()},
        {TestLct.TransForest.getCode(), TestLct.Oak.getCode(), TestLct.Pine.getCode()},
        {TestLct.TransForest.getCode(), TestLct.Shrubland.getCode(), TestLct.Pine.getCode()}});
  }

  private IGridValueLayer getTestOakAgeLayer() {
    return arrayToGridValueLayer("OakAge", new double[][] {
        {10, 11, -1},
        {2, 9, -1},
        {2, -1, -1}});
  }

}
