package repast.model.agrosuccess.anthro;

import org.apache.log4j.Logger;
import cern.jet.random.Binomial;

/**
 * Keeps track of a household's birth and death rates which emerge as a function of their degree of
 * success in gathering enough calories to feed its members. Provides the public method
 * {@link DynamicPopulationUpdateManager#newPopulation(int, double)}.
 *
 * @author Andrew Lane
 *
 */
public class DynamicPopulationUpdateManager implements PopulationUpdateManager {
  final static Logger logger = Logger.getLogger(DynamicPopulationUpdateManager.class);

  private final PopulationUpdateParams popUpdateParams;
  private final FarmingPlanParams farmingPlanParams;
  private final Binomial binomialDistr;

  private double currentBirthRate;
  private double currentDeathRate;

  public DynamicPopulationUpdateManager(PopulationUpdateParams popUpdateParams,
      FarmingPlanParams farmingPlanParams, Binomial binomialDistr) {
    this.popUpdateParams = popUpdateParams;
    this.farmingPlanParams = farmingPlanParams;
    this.binomialDistr = binomialDistr;

    this.currentBirthRate = popUpdateParams.getInitBirthRate();
    this.currentDeathRate = popUpdateParams.getInitDeathRate();
  }

  /**
   * <p>
   * Takes into account the household's current population and the mass of wheat harvested in the
   * previous year to update the instance's birth and death rate variables, generate births and
   * deaths for the year based on those values, and return the new household population.
   * </p>
   * <p>
   * Note that calling this function updates the state of the {@code DynamicPopulationUpdateManager} with
   * new birth and death rates calculated using the given household population and wheat obtained in
   * the year.
   * </p>
   *
   * @param currentPop Number of members of the household
   * @param wheatReturnsInKg Amount of wheat harvested during the year in kg
   * @return Updated household population taking into account births and deaths which occurred in
   *         the previous year
   */
  @Override
  public int newPopulation(int currentPop, double wheatReturnsInKg) {
    double calBuffer = calorieBuffer(currentPop);
    double calSurplus = calorieSurplus(currentPop, wheatReturnsInKg);
    this.currentBirthRate = newBirthRate(calSurplus, calBuffer);
    this.currentDeathRate = newDeathRate(calSurplus, calBuffer);
    logger.debug("currentBirthRate: " + this.currentBirthRate + ", currentDeathRate: "
        + this.currentDeathRate);

    // Check to see if birth/ death rates are 0, because if they are then 0 births/ deaths
    // are certain and avoids IllegalArgumentException
    // https://dst.lbl.gov/ACSSoftware/colt/api/cern/jet/random/Binomial.html
    int numBirths, numDeaths;
    if (this.currentBirthRate <= 0.0) {
      numBirths = 0;
    } else {
      numBirths = this.binomialDistr.nextInt(currentPop, this.currentBirthRate);
    }

    if (this.currentDeathRate <= 0.0) {
      numDeaths = 0;
    } else {
      numDeaths = this.binomialDistr.nextInt(currentPop, this.currentDeathRate);
    }

    return currentPop + numBirths - numDeaths;
  }

  /**
   * <p>
   * Implements Eq. {@code eq:calorie-buffer}
   * </p>
   *
   * <pre>
   * \Delta C_T = \targetYieldBufferFac{}  C_T P_h \times 365 \text{ days}
   * </pre>
   *
   * @param householdPop
   * @return Buffer around household's target number of calories used to determine impact on birth
   *         and death rates
   */
  double calorieBuffer(int householdPop) {
    return this.popUpdateParams.getTargetYieldBufferFactor()
        * this.farmingPlanParams.getEnergyPerPersonPerDay() * householdPop * 365;
  }

  double calorieSurplus(int householdPop, double wheatReturnsInKg) {
    double caloriesIn = this.farmingPlanParams.getEnergyPerKgWheat() * wheatReturnsInKg;
    double caloriesRequired =
        householdPop * this.farmingPlanParams.getEnergyPerPersonPerDay() * 365;
    return caloriesIn - caloriesRequired;
  }

  /**
   * Implements {@code eq:birth-rate-update-rule} and {@code eq:birth-rate-within-limits}
   *
   * @param calorieSurplus Annual calorie surplus for the household
   * @param calorieBuffer Annual calorie surplus buffer
   * @return Updated birth rate taking into account household's performance in the preceding year
   */
  double newBirthRate(double calorieSurplus, double calorieBuffer) {
    double rateChange = rateChangeMagnitude(calorieSurplus, calorieBuffer,
        this.popUpdateParams.getMaxBirthRateDelta());
    double candidateRate = this.currentBirthRate + rateChange;
    return thresholdedRate(candidateRate, this.popUpdateParams.getMinBirthRate(),
        this.popUpdateParams.getMaxBirthRate());
  }

  /**
   * Implements {@code eq:death-rate-update-rule} and {@code eq:death-rate-within-limits}
   *
   * @param calorieSurplus Annual calorie surplus for the household
   * @param calorieBuffer Annual calorie surplus buffer
   * @return Updated death rate taking into account household's performance in the preceding year
   */
  double newDeathRate(double calorieSurplus, double calorieBuffer) {
    double rateChange = rateChangeMagnitude(calorieSurplus, calorieBuffer,
        this.popUpdateParams.getMaxDeathRateDelta());
    double candidateRate = this.currentDeathRate - rateChange;
    return thresholdedRate(candidateRate, this.popUpdateParams.getMinDeathRate(),
        this.popUpdateParams.getMaxDeathRate());
  }

  /**
   * <p>
   * The amount by which a birth/ death rate should change in consideration of the household's
   * calorie surplus, calorie target buffer and the maximum amount the model allows the birth/ death
   * rate to change in a single year.
   * </p>
   * <p>
   * Note that this reflects the amount by which the amount (i.e. magnitude) by which the rate
   * should change, but not the direction of the change. This is to allow the calculation of birth
   * and death rates to both use this function in a polymorphic way.
   * </p>
   * <p>
   * For details of calculation see {@code eq:birth-rate-update-rule} and
   * {@code eq:death-rate-update-rule}.
   * </p>
   *
   * @param calorieSurplus
   * @param calorieBuffer
   * @param maxRateChange
   * @return Magnitude of amount by which birth/ death rate should change
   */
  private static double rateChangeMagnitude(double calorieSurplus, double calorieBuffer,
      double maxRateChange) {
    double surplusSign = Math.signum(calorieSurplus);
    double surplusMag = Math.abs(calorieSurplus);

    if (surplusMag > calorieBuffer) {
      return surplusSign * maxRateChange;
    }

    if (surplusMag > 0.25 * calorieBuffer && surplusMag <= calorieBuffer) {
      return (2.0 / 3.0) * surplusSign * maxRateChange;
    }

    if (surplusMag > 0.15 * calorieBuffer && surplusMag <= 0.25 * calorieBuffer) {
      return (1.0 / 2.0) * surplusSign * maxRateChange;
    }

    return 0.0;
  }

  /**
   * <p>
   * Given a candidate new birth/ death rate, calculate the thresholded rate.
   * </p>
   * <p>
   * For details for calculation see {@code eq:birth-rate-within-limits} and
   * {@code eq:death-rate-within-limits}.
   * </p>
   *
   * @param candidateRate
   * @param minRate
   * @param maxRate
   * @return Thresholded updated rate taking account of maximum and minimum allowed values for the
   *         rate
   */
  private static double thresholdedRate(double candidateRate, double minRate, double maxRate) {
    if (candidateRate > maxRate) {
      return maxRate;
    }

    if (candidateRate < minRate) {
      return minRate;
    }

    return candidateRate;
  }

  double getCurrentBirthRate() {
    return this.currentBirthRate;
  }

  double getCurrentDeathRate() {
    return this.currentDeathRate;
  }

}
