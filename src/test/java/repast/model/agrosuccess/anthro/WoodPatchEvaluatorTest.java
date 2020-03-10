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

public class WoodPatchEvaluatorTest {

  private static final double TOLERANCE = 0.00001;

  WoodPlotValueParams testParams1 = new WoodPlotValueParams(1.);
  WoodPlotValueParams testParams2 = new WoodPlotValueParams(0.5);

  @Rule
  public EasyMockRule rule = new EasyMockRule(this);

  @Mock
  private DistanceCalculator distCalc;

  @Test
  public void testConstructor() {
    WoodPlotValueParams params = new WoodPlotValueParams(1.);
    new WoodPatchEvaluator(params, this.distCalc);
  }

  @Test
  public void testGetValueParamsAllUnity() {
    GridPoint villageLoc = new GridPoint(2, 2);
    GridPoint tgtPatch1 = new GridPoint(0, 0);
    GridPoint tgtPatch2 = new GridPoint(0, 1);

    PatchOption worsePatch = new PatchOption(tgtPatch1, 0.5, 0.2, 0.75, 0.2);
    PatchOption betterPatch = new PatchOption(tgtPatch2, 0.9, 0.9, 1.0, 0.1);

    expect(this.distCalc.propMaxDist(villageLoc, tgtPatch1)).andReturn(0.5);
    expect(this.distCalc.propMaxDist(villageLoc, tgtPatch2)).andReturn(0.1);
    replay(this.distCalc);

    PatchEvaluator woodPatchEval = new WoodPatchEvaluator(this.testParams1, this.distCalc);

    double worsePatchVal = woodPatchEval.getValue(worsePatch, villageLoc);
    double betterPatchVal = woodPatchEval.getValue(betterPatch, villageLoc);

    assertTrue(worsePatchVal < betterPatchVal);

    // (1 / 1 + distanceParam) * (woodValue + distanceParam * (1 - distance))
    // worsePatch: (1 / 1 + 1) * (0.2 + 1 * (1 - 0.5)) = 0.35
    assertEquals(0.35, worsePatchVal, TOLERANCE);
    // betterPatch: (1 / 1 + 1) * (0.9 + 1 * (1 - 0.1)) = 0.9
    assertEquals(0.9, betterPatchVal, TOLERANCE);

    verify(this.distCalc);
  }

  @Test
  public void testGetValueNonUnityParams() {
    GridPoint villageLoc = new GridPoint(2, 2);
    GridPoint tgtPatch1 = new GridPoint(0, 0);
    GridPoint tgtPatch2 = new GridPoint(0, 1);

    PatchOption worsePatch = new PatchOption(tgtPatch1, 0.5, 0.2, 0.75, 0.2);
    PatchOption betterPatch = new PatchOption(tgtPatch2, 0.9, 0.9, 1.0, 0.1);

    expect(this.distCalc.propMaxDist(villageLoc, tgtPatch1)).andReturn(0.5);
    expect(this.distCalc.propMaxDist(villageLoc, tgtPatch2)).andReturn(0.1);
    replay(this.distCalc);

    PatchEvaluator woodPatchEval = new WoodPatchEvaluator(this.testParams2, this.distCalc);

    double worsePatchVal = woodPatchEval.getValue(worsePatch, villageLoc);
    double betterPatchVal = woodPatchEval.getValue(betterPatch, villageLoc);

    assertTrue(worsePatchVal < betterPatchVal);

    // (1 / 1 + distanceParam) * (woodValue + distanceParam * (1 - distance))
    // worsePatch: (1 / 1 + 0.5) * (0.2 + 0.5 * (1 - 0.5)) = 0.3
    assertEquals(0.3, worsePatchVal, TOLERANCE);
    // betterPatch: (1 / 1 + 0.5) * (0.9 + 0.5 * (1 - 0.1)) = 0.9
    assertEquals(0.9, betterPatchVal, TOLERANCE);

    verify(this.distCalc);
  }

}
