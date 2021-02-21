package repast.model.agrosuccess.anthro;

import static org.easymock.EasyMock.anyDouble;
import static org.easymock.EasyMock.anyInt;
import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.easymock.EasyMockRule;
import org.easymock.Mock;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import repast.simphony.space.grid.GridPoint;

/**
 * We don't explicitly test {@link DefaultHousehold#calcSubsistencePlan} here because this method
 * simply calls {@link FarmingPlanCalculator#estimateNumWheatPatchesToFarm(int, double)}, which is
 * tested in {@link FarmingPlanCalculatorTest}.
 *
 * @author Andrew Lane
 */
public class DefaultHouseholdTest {

  @Rule
  public EasyMockRule rule = new EasyMockRule(this);

  @Mock
  private Village village;

  @Mock
  private FarmingPlanCalculator fpCalc;

  @Mock
  private FarmingReturnCalculator frCalc;

  @Mock
  private FarmingPlanParams farmingParams;

  @Mock
  private WoodReturnCalculator wrCalc;

  @Mock
  private PopulationUpdateManager popUpdateManager;

  @Mock
  private DefaultHousehold household;

  @Before
  public void setUpTestHousehold() {
    this.household = DefaultHousehold.builder()
        .initPopulation(5)
        .village(village)
        .farmingPlanCalculator(fpCalc)
        .wheatYieldParams(3500, 625)
        .farmingReturnCalculator(frCalc)
        .populationUpdateManager(popUpdateManager)
        .build();
  }

  @After
  public void tearDown() {
    this.village = null;
    this.household = null;
  }

  @Test
  public void testClaimPatch() {
    // village assumed to be at (1, 1) so the test patches are equidistant
    PatchOption goodFarmPatch = new PatchOption(new GridPoint(0, 0), 1, 0, 1, 0);
    PatchOption goodWoodPatch = new PatchOption(new GridPoint(2, 0), 0, 1, 0, 0);
    PatchOption badPatch = new PatchOption(new GridPoint(0, 2), 0, 0, 0, 0);

    expect(this.village.getOrderedWheatPatches())
        .andReturn(Arrays.asList(goodFarmPatch, goodWoodPatch, badPatch))
        .times(3);
    replay(this.village);

    Set<PatchOption> availPatches = new HashSet<>();
    availPatches.add(goodFarmPatch);
    availPatches.add(goodWoodPatch);
    availPatches.add(badPatch);

    assertEquals(goodFarmPatch, household.claimPatch(availPatches));
    availPatches.remove(goodFarmPatch);

    assertEquals(goodWoodPatch, household.claimPatch(availPatches));
    availPatches.remove(goodWoodPatch);

    assertEquals(badPatch, household.claimPatch(availPatches));
    availPatches.remove(badPatch);

    verify(this.village);
    reset(this.village);
  }

  @Test
  public void testSubsistencePlanIsSatisfied() {
    expect(this.fpCalc.estimateNumWheatPatchesToFarm(anyInt(), anyDouble()))
        .andReturn(10)
        .once();
    replay(this.fpCalc);

    List<PatchOption> orderedPatches = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      orderedPatches.add(new PatchOption(new GridPoint(0, -5 + i), 10 - i, 0, 1, 0));
    }
    expect(this.village.getOrderedWheatPatches()).andReturn(orderedPatches).times(10);
    replay(this.village);

    this.household.calcSubsistencePlan();
    Set<PatchOption> availPatchSet = new HashSet<>(orderedPatches);
    for (int i = 0; i < 9; i++) {
      PatchOption claimedPatch = this.household.claimPatch(availPatchSet);
      availPatchSet.remove(claimedPatch);
      assertFalse(this.household.subsistencePlanIsSatisfied());
    }

    PatchOption claimedPatch = this.household.claimPatch(availPatchSet);
    availPatchSet.remove(claimedPatch);
    assertTrue(this.household.subsistencePlanIsSatisfied());

    verify(this.fpCalc);
    verify(this.village);
  }

  /**
   * We want to know that after subsistencePlanIsSatisfied has returned true, a call to updatePopulation
   * causes the values of this.population and this.massWheatPerHaLastYear to be updated.
   */
  @Test
  public void testUpdatePopulation() {
    // Suppose the household has ill-advisedly decided it needs only one farming patch
    expect(this.fpCalc.estimateNumWheatPatchesToFarm(anyInt(), anyDouble())).andReturn(1).once();

    List<PatchOption> patchOptionList = Arrays.asList(
        new PatchOption(new GridPoint(0, 0), 1, 0, 1, 0));
    Set<PatchOption> availPatchSet = new HashSet<>(patchOptionList);
    expect(this.village.getOrderedWheatPatches()).andReturn(patchOptionList).times(1);

    expect(this.frCalc.getReturns(anyObject(), anyDouble())).andReturn(130.0);

    replay(this.fpCalc, this.frCalc, this.village);

    this.household.calcSubsistencePlan();
    PatchOption claimedPatch = this.household.claimPatch(availPatchSet);
    availPatchSet.remove(claimedPatch);
    assertTrue(this.household.subsistencePlanIsSatisfied());

    // One patch with area 625 m^2 isn't expected to be enough for a family of 5, expect population
    // to decrease
    int prevPop = this.household.population;
    double prevMassWheatPerHaLastYear = this.household.massWheatPerHaLastYear;

    this.household.updatePopulation(500);

    assertTrue(this.household.population < prevPop);
    assertTrue(this.household.massWheatPerHaLastYear != prevMassWheatPerHaLastYear);

    verify(this.fpCalc, this.frCalc, this.village);
  }

  @Test
  public void testReleasePatches() {
    expect(this.fpCalc.estimateNumWheatPatchesToFarm(anyInt(), anyDouble())).andReturn(1).once();

    List<PatchOption> patchOptionList = Arrays.asList(
        new PatchOption(new GridPoint(0, 0), 1, 0, 1, 0));
    Set<PatchOption> availPatchSet = new HashSet<>(patchOptionList);
    expect(this.village.getOrderedWheatPatches()).andReturn(patchOptionList).times(1);

    replay(this.fpCalc, this.village);

    this.household.calcSubsistencePlan();
    this.household.claimPatch(availPatchSet);
    assertTrue(this.household.subsistencePlanIsSatisfied());

    this.household.updatePopulation(500);
    assertEquals(availPatchSet, this.household.wheatPatchesForYear);
    this.household.releasePatches();
    assertTrue(this.household.wheatPatchesForYear.isEmpty());
  }

  @Test
  public void testGetId() {
    Household hhExplicitId = DefaultHousehold.builder()
        .initPopulation(5)
        .village(village)
        .farmingPlanCalculator(fpCalc)
        .wheatYieldParams(3500, 25)
        .farmingReturnCalculator(frCalc)
        .populationUpdateManager(popUpdateManager)
        .id(42)
        .build();

    Household hhRandomId = DefaultHousehold.builder()
        .initPopulation(5)
        .village(village)
        .farmingPlanCalculator(fpCalc)
        .wheatYieldParams(3500, 25)
        .farmingReturnCalculator(frCalc)
        .populationUpdateManager(popUpdateManager)
        .build();

    assertEquals(42, hhExplicitId.getId());
    assertTrue(hhRandomId.getId() != 0);
  }

}
