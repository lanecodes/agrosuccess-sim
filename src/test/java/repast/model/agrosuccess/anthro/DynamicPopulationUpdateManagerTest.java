package repast.model.agrosuccess.anthro;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import org.easymock.EasyMockRule;
import org.easymock.Mock;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import cern.jet.random.Binomial;

public class DynamicPopulationUpdateManagerTest {

  private static double TOLERANCE = 0.0001;
  private static double ANNUAL_HOUSEHOLD_CALORIES = 4562500;
  private static double CALORIE_BUFFER = ANNUAL_HOUSEHOLD_CALORIES * 0.1;

  @Rule
  public EasyMockRule rule = new EasyMockRule(this);

  @Mock
  private PopulationUpdateParams popUpdateParams;

  @Mock
  private FarmingPlanParams farmingPlanParams;

  @Mock
  private Binomial binomialDistr;

  private DynamicPopulationUpdateManager popUpdateManager;

  /**
   * Scenarios assume the target number of calories for the household is 365 days x 5 people x 2500
   * kcal/person/day = 4,562,500 kcal. The calorie buffer is 10% of that value, 456,250. The surplus
   * amounts associated with each scenario are chosen to test the birth and death rate logic in the
   * class under test.
   * <ul>
   * <li>If the surplus is over the target plus the buffer ({@code OVER_BUFFER}), there will be the
   * maximum increase in birth rate and decrease in death rate.</li>
   * <li>For {@code ON_BUDGET} there should be no change in rates.</li>
   * <li>For {@code FIRST_TIER_UNDER_BUDGET}, the surplus is between 15% and 25% of the calorie
   * buffer <emph>below</emph> the target.</li>
   * <li>For {@code SECOND_TIER_UNDER_BUDGET} the surplus is between 25% and 100% of the calorie
   * buffer <emph>below</emph> the target.</li>
   * </ul>
   *
   */
  private enum Scenario {
    OVER_BUFFER(500000), ON_BUDGET(0), FIRST_TIER_UNDER_BUDGET(-100000), SECOND_TIER_UNDER_BUDGET(
        -200000);

    private final double surplus;

    Scenario(double surplus) {
      this.surplus = surplus;
    }

    public double surplus() {
      return this.surplus;
    }
  }

  @Before
  public void setUp() {
    expect(this.popUpdateParams.getInitBirthRate()).andReturn(0.066);
    expect(this.popUpdateParams.getInitDeathRate()).andReturn(0.055);
    replay(this.popUpdateParams);

    this.popUpdateManager = new DynamicPopulationUpdateManager(this.popUpdateParams,
        this.farmingPlanParams, this.binomialDistr);

    verify(this.popUpdateParams);
    reset(this.popUpdateParams);

  }

  @After
  public void tearDown() {
    this.popUpdateManager = null;
  }

  @Test
  public void testCalorieBuffer() {
    int householdPop = 5;
    double buffer;

    // expect(this.popUpdateParams.getTargetYieldBufferFactor()).andReturn(0.1);
    // expect(this.popUpdateParams.getTargetYieldBufferFactor()).andReturn(0.1);
    expect(this.popUpdateParams.getTargetYieldBufferFactor()).andReturn(0.1);
    expect(this.farmingPlanParams.getEnergyPerPersonPerDay()).andReturn(2500.);
    // expect(this.popUpdateParams.getTargetYieldBufferFactor()).andReturn(0.1);
    // expect(this.popUpdateParams.getTargetYieldBufferFactor()).andReturn(0.1);
    replay(this.popUpdateParams, this.farmingPlanParams);
    // replay(this.popUpdateParams, this.farmingPlanParams);

    buffer = this.popUpdateManager.calorieBuffer(householdPop);
    assertEquals(456250., buffer, TOLERANCE);

    // verify(this.popUpdateParams, this.farmingPlanParams);
    verify(this.farmingPlanParams);
  }

  @Test
  public void testCalorieSurplus() {
    int householdPop = 5;
    // calculated mass of wheat needed to produce a surplus in calories
    double wheatReturnsInKg =
        (1.0 / 3540.0) * (Scenario.OVER_BUFFER.surplus() + ANNUAL_HOUSEHOLD_CALORIES);
    double surplus;

    expect(this.farmingPlanParams.getEnergyPerKgWheat()).andReturn(3540.);
    expect(this.farmingPlanParams.getEnergyPerPersonPerDay()).andReturn(2500.);
    replay(this.farmingPlanParams);

    surplus = this.popUpdateManager.calorieSurplus(householdPop, wheatReturnsInKg);
    assertEquals(500000, surplus, TOLERANCE);
    verify(this.farmingPlanParams);
  }

  /**
   * Check returned birth rate when the calories gathered by the household are more than enough
   */
  @Test
  public void testNewBirthRateOverBuffer() {
    double birthRate = calcBirthRateUpdate(Scenario.OVER_BUFFER);
    // 0.066 + 0.2 = 0.266, where 0.066 is previous (initial) birth rate
    assertEquals(0.266, birthRate, TOLERANCE);
    verify(this.popUpdateParams);
  }

  /**
   * Check returned birth rate when the calories gathered by the household are just enough
   */
  @Test
  public void testNewBirthRateOnBudget() {
    double birthRate = calcBirthRateUpdate(Scenario.ON_BUDGET);
    // 0.066, where 0.066 is previous (initial) birth rate
    assertEquals(0.066, birthRate, TOLERANCE);
    verify(this.popUpdateParams);
  }

  /**
   * Check returned birth rate when the calories gathered by the household are slightly under budget
   */
  @Test
  public void testNewBirthRateFirstTierUnderBudget() {
    double birthRate = calcBirthRateUpdate(Scenario.FIRST_TIER_UNDER_BUDGET);
    // 0.066 - 1/2 * 0.2 = -0.034, where 0.066 is previous (initial) birth rate
    // as this is < min birth rate (0), expect new birth rate to be 0
    assertEquals(0, birthRate, TOLERANCE);
    verify(this.popUpdateParams);
  }

  /**
   * Assume:
   * <ul>
   * <li>Previous birth rate = 0.066</li>
   * <li>Min birth rate = 0.0</li>
   * <li>Max birth rate = 0.6</li>
   * <li>Max change to birth rate in one step = 0.2</li>
   * <li>The calorie buffer is as defined in this test class's static variable
   * {@code CALORIE_BUFFER}</li>
   * </ul>
   *
   * @param surplusScenario
   * @return Birth rate generated by the {@code DynamicPopulationUpdateManager}
   */
  private double calcBirthRateUpdate(Scenario surplusScenario) {
    expect(this.popUpdateParams.getMinBirthRate()).andReturn(0.);
    expect(this.popUpdateParams.getMaxBirthRate()).andReturn(0.6);
    expect(this.popUpdateParams.getMaxBirthRateDelta()).andReturn(0.2);
    replay(this.popUpdateParams);

    double calorieSurplus = surplusScenario.surplus();
    double calorieBuffer = CALORIE_BUFFER;

    return this.popUpdateManager.newBirthRate(calorieSurplus, calorieBuffer);
  }

  @Test
  public void testNewDeathRateOverBuffer() {
    double deathRate = calcDeathRateUpdate(Scenario.OVER_BUFFER);
    // 0.055 - 0.3 = -0.245, where 0.055 is previous (initial) death rate
    // because -0.245 is below the minimum death rate (0), the returned death
    // rate should be equal to 0.
    assertEquals(0., deathRate, TOLERANCE);
    verify(this.popUpdateParams);
  }

  @Test
  public void testNewDeathRateOnBudget() {
    double deathRate = calcDeathRateUpdate(Scenario.ON_BUDGET);
    // 0.055, where 0.055 is previous (initial) death rate
    assertEquals(0.055, deathRate, TOLERANCE);
    verify(this.popUpdateParams);
  }

  @Test
  public void testNewDeathRateFirstTierUnderBudget() {
    double deathRate = calcDeathRateUpdate(Scenario.FIRST_TIER_UNDER_BUDGET);
    // 0.055 + 1/2 * 0.3 = 0.205, where 0.055 is previous (initial) death rate
    assertEquals(0.205, deathRate, TOLERANCE);
    verify(this.popUpdateParams);
  }

  @Test
  public void testNewDeathRateSecondTierUnderBudget() {
    double deathRate = calcDeathRateUpdate(Scenario.SECOND_TIER_UNDER_BUDGET);
    // 0.055 + 2/3 * 0.3 = 0.255, where 0.055 is previous (initial) death rate
    assertEquals(0.255, deathRate, TOLERANCE);
    verify(this.popUpdateParams);
  }

  /**
   * Assume:
   * <ul>
   * <li>Previous death rate = 0.055</li>
   * <li>Min death rate = 0.0</li>
   * <li>Max death rate = 0.8</li>
   * <li>Max change to death rate in one step = 0.3</li>
   * <li>The calorie buffer is as defined in this test class's static variable
   * {@code CALORIE_BUFFER}</li>
   * </ul>
   *
   * @param surplusScenario
   * @return Death rate generated by the {@code DynamicPopulationUpdateManager}
   */
  private double calcDeathRateUpdate(Scenario surplusScenario) {
    expect(this.popUpdateParams.getMinDeathRate()).andReturn(0.);
    expect(this.popUpdateParams.getMaxDeathRate()).andReturn(0.8);
    expect(this.popUpdateParams.getMaxDeathRateDelta()).andReturn(0.3);
    replay(this.popUpdateParams);

    double calorieSurplus = surplusScenario.surplus();
    double calorieBuffer = CALORIE_BUFFER;

    return this.popUpdateManager.newDeathRate(calorieSurplus, calorieBuffer);
  }

  @Test
  public void testNewPopulation() {

    int currentPop = 5;
    // calculated mass of wheat needed to produce a surplus in calories
    double wheatReturnsInKg =
        (1.0 / 3540.0) * (Scenario.OVER_BUFFER.surplus() + ANNUAL_HOUSEHOLD_CALORIES);
    int newPop;

    // calculate calorie buffer
    expect(this.popUpdateParams.getTargetYieldBufferFactor()).andReturn(0.1);
    expect(this.farmingPlanParams.getEnergyPerPersonPerDay()).andReturn(2500.);
    // calculate calorie surplus
    expect(this.farmingPlanParams.getEnergyPerKgWheat()).andReturn(3540.);
    expect(this.farmingPlanParams.getEnergyPerPersonPerDay()).andReturn(2500.);
    // calculate updated birth rate
    expect(this.popUpdateParams.getMinBirthRate()).andReturn(0.);
    expect(this.popUpdateParams.getMaxBirthRate()).andReturn(0.6);
    expect(this.popUpdateParams.getMaxBirthRateDelta()).andReturn(0.2);
    // calculate updated death rate
    expect(this.popUpdateParams.getMinDeathRate()).andReturn(0.);
    expect(this.popUpdateParams.getMaxDeathRate()).andReturn(0.8);
    expect(this.popUpdateParams.getMaxDeathRateDelta()).andReturn(0.3);

    // Confirm initial birth and death rates are as expected
    assertEquals(0.066, this.popUpdateManager.getCurrentBirthRate(), TOLERANCE);
    assertEquals(0.055, this.popUpdateManager.getCurrentDeathRate(), TOLERANCE);

    double expectedNewBirthRate = 0.266;
    double expectedNewDeathRate = 0.;

    // new birth rate
    expect(this.binomialDistr.nextInt(currentPop, expectedNewBirthRate)).andReturn(1);
    // new death rate
    // The binomial distribution is not hit to get the number of deaths, because the
    // death rate is expected to have fallen to 0. Since Binomial.nextInt(5, 0.0) is an
    // IllegalArgumentException (second argument must be > 0), a deterministic value of
    // 0 deaths will be returned.

    replay(this.binomialDistr, this.farmingPlanParams, this.popUpdateParams);

    newPop = popUpdateManager.newPopulation(currentPop, wheatReturnsInKg);
    // Check birth and death rates have been updated
    assertEquals(expectedNewBirthRate, this.popUpdateManager.getCurrentBirthRate(), TOLERANCE);
    assertEquals(expectedNewDeathRate, this.popUpdateManager.getCurrentDeathRate(), TOLERANCE);
    assertEquals(6, newPop);
    verify(this.binomialDistr, this.farmingPlanParams, this.popUpdateParams);
  }

}
