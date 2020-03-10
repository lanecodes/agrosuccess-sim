package repast.model.agrosuccess.anthro;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.easymock.EasyMockRule;
import org.easymock.Mock;
import org.junit.Rule;
import org.junit.Test;
import repast.simphony.space.grid.GridPoint;

public class FarmingPatchEvaluatorTest {

  private static final double TOLERANCE = 0.00001;

  FarmPlotValueParams testParams1 = new FarmPlotValueParams(1., 1., 1.);
  FarmPlotValueParams testParams2 = new FarmPlotValueParams(0.5, 1., 1.5);

  @Rule
  public EasyMockRule rule = new EasyMockRule(this);

  @Mock
  private DistanceCalculator distCalc;

  @Test
  public void testConstructor() {
    FarmPlotValueParams params = new FarmPlotValueParams(1., 1., 1.);
    new FarmingPatchEvaluator(params, this.distCalc);
  }

  @Test
  public void testGetValueParamsAllUnity() {
    GridPoint villageLoc = new GridPoint(2, 2);
    GridPoint tgtPatch1 = new GridPoint(0, 0);
    GridPoint tgtPatch2 = new GridPoint(0, 1);

    PatchOption worsePatch = new PatchOption(tgtPatch1, 0.5, 0.7, 0.75, 0.2);
    PatchOption betterPatch = new PatchOption(tgtPatch2, 0.9, 0.7, 1.0, 0.1);

    expect(this.distCalc.propMaxDist(villageLoc, tgtPatch1)).andReturn(0.5);
    expect(this.distCalc.propMaxDist(villageLoc, tgtPatch2)).andReturn(0.1);
    replay(this.distCalc);

    PatchEvaluator farmPatchEval = new FarmingPatchEvaluator(this.testParams1, this.distCalc);

    double worsePatchVal = farmPatchEval.getValue(worsePatch, villageLoc);
    double betterPatchVal = farmPatchEval.getValue(betterPatch, villageLoc);

    assertTrue(worsePatchVal < betterPatchVal);

    // (slopeValue * fetilityParam * fertility) - (distanceParam * distance) - (lccParam * lcc)
    // worsePatch: (0.75 * 1 * 0.5) - (1 * 0.5) - (1 * 0.2) = -0.325
    assertEquals(-0.325, worsePatchVal, TOLERANCE);
    // betterPatch: (1 * 1 * 0.9) - (1 * 0.1) - (1 * 0.1) = 0.7
    assertEquals(0.7, betterPatchVal, TOLERANCE);

    verify(this.distCalc);
  }

  @Test
  public void testGetValueNonUnityParams() {
    GridPoint villageLoc = new GridPoint(2, 2);
    GridPoint tgtPatch1 = new GridPoint(0, 0);
    GridPoint tgtPatch2 = new GridPoint(0, 1);

    PatchOption worsePatch = new PatchOption(tgtPatch1, 0.5, 0.7, 0.75, 0.2);
    PatchOption betterPatch = new PatchOption(tgtPatch2, 0.9, 0.7, 1.0, 0.1);

    expect(this.distCalc.propMaxDist(villageLoc, tgtPatch1)).andReturn(0.5);
    expect(this.distCalc.propMaxDist(villageLoc, tgtPatch2)).andReturn(0.1);
    replay(this.distCalc);

    PatchEvaluator farmPatchEval = new FarmingPatchEvaluator(this.testParams2, this.distCalc);

    double worsePatchVal = farmPatchEval.getValue(worsePatch, villageLoc);
    double betterPatchVal = farmPatchEval.getValue(betterPatch, villageLoc);

    assertTrue(worsePatchVal < betterPatchVal);

    // (slopeValue * fetilityParam * fertility) - (distanceParam * distance) - (lccParam * lcc)
    // worsePatch: (0.75 * 1 * 0.5) - (0.5 * 0.5) - (1.5 * 0.2) = -0.175
    assertEquals(-0.175, worsePatchVal, TOLERANCE);
    // betterPatch: (1 * 1 * 0.9) - (0.5 * 0.1) - (1.5 * 0.1) = 0.7
    assertEquals(0.7, betterPatchVal, TOLERANCE);

    verify(this.distCalc);
  }

}
