package repast.model.agrosuccess.anthro;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.apache.log4j.Logger;

/**
 * Household agent in the AgroSuccess simulation model.
 *
 * Like household agents in the MedLanD model, households develop a subsistence plan at the
 * beginning of each simulated year, assume control of land-cover patches and farm them to meet
 * their subsistence needs, and then update their populations to reflect their degree of success in
 * obtaining sufficient calories.
 *
 * Unlike household agents in the MedLanD model Household agents in AgroSuccess do not explicitly
 * include gathering of fire wood in their subsistence plan, instead focusing exclusively on wheat
 * agriculture. The decision to exclude wood gathering from the subsistence plan is based on a
 * review of the MedLanD model specification and model code that revealed wood gathering yield
 * didn't impact household population or land-cover state. Wood gathering could be included as an
 * extension in later versions of AgroSuccess. See discussion in thesis document.
 *
 * If no {@code id} is specified for the household, one will be randomly generated.
 *
 * @author Andrew Lane
 *
 */
public class DefaultHousehold implements Household {
  final static Logger logger = Logger.getLogger(DefaultHousehold.class);

  private final FarmingPlanCalculator farmingPlanCalc;
  private final FarmingReturnCalculator farmingReturnCalc;
  private final PopulationUpdateManager popUpdateManager;

  private final long id;
  private final Village village;

  private int population;
  private double massWheatPerHaLastYear;
  private double rasterCellAreaInSqm;
  private int subsistencePlan;

  private Set<PatchOption> wheatPatchesForYear = new HashSet<>();

  public static PopulationStep builder() {
    return new Builder();
  }

  public interface PopulationStep {
    VillageStep initPopulation(int initPopulation);
  }

  public interface VillageStep {
    FarmingPlanCalcStep village(Village village);
  }

  public interface FarmingPlanCalcStep {
    WheatYieldParamsStep farmingPlanCalculator(FarmingPlanCalculator farmingPlanCalc);
  }

  public interface WheatYieldParamsStep {
    FarmingReturnCalcStep wheatYieldParams(double initMassWheatPerHaLastYear,
        double rasterCellAreaInSqm);
  }

  public interface FarmingReturnCalcStep {
    PopulationUpdateManagerStep farmingReturnCalculator(FarmingReturnCalculator farmingReturnCalc);
  }

  public interface PopulationUpdateManagerStep {
    BuildStep populationUpdateManager(PopulationUpdateManager popUpdateManager);
  }

  public interface BuildStep {
    DefaultHousehold build();

    BuildStep id(long id);
  }

  private static class Builder implements PopulationStep, VillageStep, FarmingPlanCalcStep,
      WheatYieldParamsStep, FarmingReturnCalcStep, PopulationUpdateManagerStep,
      BuildStep {
    private FarmingPlanCalculator farmingPlanCalc;
    private FarmingReturnCalculator farmingReturnCalc;
    private PopulationUpdateManager popUpdateManager;

    private Long id;
    private Village village;
    private int initPopulation;
    private double initMassWheatPerHaLastYear;
    private double rasterCellAreaInSqm;

    @Override
    public DefaultHousehold build() {
      return new DefaultHousehold(this);
    }

    @Override
    public BuildStep id(long id) {
      this.id = id;
      return this;
    }

    @Override
    public VillageStep initPopulation(int initPopulation) {
      this.initPopulation = initPopulation;
      return this;
    }

    @Override
    public FarmingPlanCalcStep village(Village village) {
      this.village = village;
      return this;
    }

    @Override
    public WheatYieldParamsStep farmingPlanCalculator(
        FarmingPlanCalculator farmingPlanCalc) {
      this.farmingPlanCalc = farmingPlanCalc;
      return this;
    }

    @Override
    public FarmingReturnCalcStep wheatYieldParams(double initMassWheatPerHaLastYear,
        double rasterCellAreaInSqm) {
      this.initMassWheatPerHaLastYear = initMassWheatPerHaLastYear;
      this.rasterCellAreaInSqm = rasterCellAreaInSqm;
      return this;
    }

    @Override
    public PopulationUpdateManagerStep farmingReturnCalculator(
        FarmingReturnCalculator farmingReturnCalc) {
      this.farmingReturnCalc = farmingReturnCalc;
      return this;
    }

    @Override
    public BuildStep populationUpdateManager(PopulationUpdateManager popUpdateManager) {
      this.popUpdateManager = popUpdateManager;
      return this;
    }

  }

  private DefaultHousehold(Builder builder) {
    if (builder.id != null) {
      this.id = builder.id;
    } else {
      this.id = UUID.randomUUID().getLeastSignificantBits();
    }
    this.population = builder.initPopulation;
    this.village = builder.village;
    this.farmingPlanCalc = builder.farmingPlanCalc;
    this.massWheatPerHaLastYear = builder.initMassWheatPerHaLastYear;
    this.rasterCellAreaInSqm = builder.rasterCellAreaInSqm;
    this.farmingReturnCalc = builder.farmingReturnCalc;
    this.popUpdateManager = builder.popUpdateManager;
  }

  @Override
  public void calcSubsistencePlan() {
    logger.debug(this.toString() + " calculating subsistence plan");
    this.subsistencePlan = this.farmingPlanCalc.estimateNumWheatPatchesToFarm(this.population,
        this.massWheatPerHaLastYear);
  }

  @Override
  public boolean subsistencePlanIsSatisfied() {
    logger.debug(this.toString() + " subsistence plan: " + this.subsistencePlan);
    if (this.wheatPatchesForYear.size() >= this.subsistencePlan) {
      return true;
    } else {
      return false;
    }
  }

  @Override
  public PatchOption claimPatch(Set<PatchOption> availablePatches) {
    PatchOption chosenPatch = null;
    for (PatchOption patch : this.village.getOrderedWheatPatches()) {
      if (availablePatches.contains(patch)) {
        chosenPatch = patch;
        break;
      }
    }
    this.wheatPatchesForYear.add(chosenPatch);
    return chosenPatch;
  }

  @Override
  public void updatePopulation(double precipitationMm) {
    double wheatReturnsInKg = this.farmingReturnCalc
        .getReturns(this.wheatPatchesForYear, precipitationMm);
    this.population = this.popUpdateManager
        .newPopulation(this.population, wheatReturnsInKg);
    // Also update the wheat yield in the previous year.
    // TODO think about whether this method should be renamed to reflect the fact that it's updating
    // multiple aspects of the household's state in response to its economic performance in the
    // previous year.
    updateWheatYieldPerHaLastYear(wheatReturnsInKg);
  }

  private void updateWheatYieldPerHaLastYear(double wheatReturnsInKg) {
    this.massWheatPerHaLastYear = wheatReturnsInKg / (this.subsistencePlan
        * this.rasterCellAreaInSqm);
  }

  @Override
  public void releasePatches() {
    this.wheatPatchesForYear.clear();
  }

  @Override
  public long getId() {
    return this.id;
  }

}
