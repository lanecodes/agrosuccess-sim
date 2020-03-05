package repast.model.agrosuccess.anthro;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.easymock.EasyMockRule;
import org.easymock.Mock;
import org.junit.Rule;
import org.junit.Test;
import repast.simphony.space.grid.GridPoint;

public class DefaultVillageTest {

  @Rule
  public EasyMockRule rule = new EasyMockRule(this);

  @Mock
  private PatchEvaluator woodPlotEval;

  @Mock
  private PatchEvaluator farmPlotEval;

  @Mock
  private Household household1;

  @Mock
  private Household household2;

  @Test
  public void testConstructor() {
    new DefaultVillage(new GridPoint(1, 1), woodPlotEval, farmPlotEval);
  }

  @Test
  public void testAddHousehold() {
    Village village = new DefaultVillage(new GridPoint(1, 1), woodPlotEval, farmPlotEval);
    village.addHousehold(household1);
    village.addHousehold(household2);

    assertEquals(2, village.getHouseholds().size());
  }

  @Test
  public void wheatPatchOrdering() {
    GridPoint villageLoc = new GridPoint(0, 0);

    Set<PatchOption> patchOptions = new LinkedHashSet<>();
    PatchOption goodPatch = new PatchOption(new GridPoint(0, 0), 1, 0, 1, 0);
    PatchOption badPatch = new PatchOption(new GridPoint(1, 1), 0.1, 0, 0.25, 0);
    // LinkedHashSet preserves insertion order, start off wrong way around
    patchOptions.add(badPatch);
    patchOptions.add(goodPatch);

    expect(farmPlotEval.getValue(goodPatch, villageLoc)).andReturn(10.);
    expect(farmPlotEval.getValue(badPatch, villageLoc)).andReturn(5.);
    expect(woodPlotEval.getValue(goodPatch, villageLoc)).andReturn(10.);
    expect(woodPlotEval.getValue(badPatch, villageLoc)).andReturn(5.);
    replay(farmPlotEval, woodPlotEval);

    Village village = new DefaultVillage(villageLoc, woodPlotEval, farmPlotEval);
    village.appraisePatches(patchOptions);
    List<PatchOption> sortedWheatValue = village.getOrderedWheatPatches();

    assertEquals(sortedWheatValue.get(0), goodPatch);
    assertEquals(sortedWheatValue.get(1), badPatch);
    verify(farmPlotEval, woodPlotEval);
  }

  @Test
  public void woodPatchOrdering() {
    GridPoint villageLoc = new GridPoint(1, 1);

    Set<PatchOption> patchOptions = new LinkedHashSet<>();
    PatchOption goodPatch = new PatchOption(new GridPoint(0, 1), 0.1, 5, 1, 0);
    PatchOption badPatch = new PatchOption(new GridPoint(2, 1), 0.1, 1, 1, 0);
    // LinkedHashSet preserves insertion order, start off wrong way around
    patchOptions.add(badPatch);
    patchOptions.add(goodPatch);

    expect(farmPlotEval.getValue(goodPatch, villageLoc)).andReturn(10.);
    expect(farmPlotEval.getValue(badPatch, villageLoc)).andReturn(5.);
    expect(woodPlotEval.getValue(goodPatch, villageLoc)).andReturn(10.);
    expect(woodPlotEval.getValue(badPatch, villageLoc)).andReturn(5.);
    replay(farmPlotEval, woodPlotEval);

    Village village = new DefaultVillage(villageLoc, woodPlotEval, farmPlotEval);
    village.appraisePatches(patchOptions);
    List<PatchOption> sortedWheatValue = village.getOrderedWheatPatches();

    assertEquals(sortedWheatValue.get(0), goodPatch);
    assertEquals(sortedWheatValue.get(1), badPatch);
    verify(farmPlotEval, woodPlotEval);
  }

}
