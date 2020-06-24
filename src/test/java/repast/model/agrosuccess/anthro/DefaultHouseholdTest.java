package repast.model.agrosuccess.anthro;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.util.HashSet;
import java.util.Set;
import org.easymock.EasyMockRule;
import org.easymock.Mock;
import org.junit.Rule;
import org.junit.Test;
import repast.simphony.space.grid.GridPoint;

public class DefaultHouseholdTest {

  @Rule
  public EasyMockRule rule = new EasyMockRule(this);

  @Mock
  private Village village;

  @Mock
  private FarmingReturnCalculator frCalc;

  @Mock
  private FarmingPlanParams farmingParams;

  @Mock
  private WoodReturnCalculator wrCalc;

  @Mock
  private PopulationUpdateManager popUpdateManager;

  /**
   * Assumption is that household should first select patches which are useful for fulfilling food
   * quota and, if there aren't any of those available, they should then look to fulfil wood quota.
   */
  @Test
  public void testClaimPatch() {
    Household household = DefaultHousehold.builder().initPopulation(5).village(village)
        .farmingReturnCalculator(frCalc).farmingPlanParams(farmingParams)
        .woodReturnCalculator(wrCalc).populationUpdateManager(popUpdateManager).id(1).build();

    // village assumed to be at (1, 1) so the test patches are equidistant
    PatchOption goodFarmPatch = new PatchOption(new GridPoint(0, 0), 1, 0, 1, 0);
    PatchOption goodWoodPatch = new PatchOption(new GridPoint(2, 0), 0, 1, 0, 0);
    PatchOption badPatch = new PatchOption(new GridPoint(0, 2), 0, 0, 0, 0);

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

  }

  @Test
  public void testSubsPlanSatisfied() {
    fail("Not yet implemented");
  }

  @Test
  public void testGetId() {
    Household hhExplicitId = DefaultHousehold.builder().initPopulation(5).village(village)
        .farmingReturnCalculator(frCalc).farmingPlanParams(farmingParams)
        .woodReturnCalculator(wrCalc).populationUpdateManager(popUpdateManager).id(42).build();

    Household hhRandomId = DefaultHousehold.builder().initPopulation(5).village(village)
        .farmingReturnCalculator(frCalc).farmingPlanParams(farmingParams)
        .woodReturnCalculator(wrCalc).populationUpdateManager(popUpdateManager).build();

    assertEquals(42, hhExplicitId.getId());
    assertTrue(hhRandomId.getId() != 0);
  }

}
