package repast.model.agrosuccess.anthro;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.apache.log4j.Logger;
import repast.simphony.random.RandomHelper;

/**
 * Household agent in the AgroSuccess simulation model.
 *
 * Like household agents in the MedLanD model, households develop a subsistence plan at the
 * beginning of each simulated year, assume control of land-cover patches and farm them to meet
 * their subsistence needs, and then update their populations to reflect their degree of success in
 * obtaining sufficient calories.
 *
 * This class builds on {@link repast.model.agrosuccess.anthro.DefaultHousehold} by adding household
 * wood gathering behaviour.
 *
 * If no {@code id} is specified for the household, one will be randomly generated.
 *
 * @author Andrew Lane
 *
 */
public class WoodGatheringHousehold implements Household {
  final static Logger logger = Logger.getLogger(WoodGatheringHousehold.class);

  private final FarmingPlanCalculator farmingPlanCalc;
  private final WheatPatchConverter wheatPatchConverter;
  private final FarmingReturnCalculator farmingReturnCalc;
  private final WoodPlanCalculator woodPlanCalc;
  private final PopulationUpdateManager popUpdateManager;

  private final long id;
  private final Village village;

  int population; // Default access specifier to facilitate unit testing
  double massWheatPerHaLastYear;
  private double rasterCellAreaInSqm;
  private int wheatSubsistencePlan;
  private int woodSubsistencePlan;

  Set<PatchOption> wheatPatchesForYear = new HashSet<>();
  Set<PatchOption> woodPatchesForYear = new HashSet<>();

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
    WheatPatchConverterStep farmingPlanCalculator(FarmingPlanCalculator farmingPlanCalc);
  }

  public interface WheatPatchConverterStep {
    WheatYieldParamsStep wheatPatchConverter(WheatPatchConverter wheatPatchConverter);
  }

  public interface WheatYieldParamsStep {
    FarmingReturnCalcStep wheatYieldParams(double initMassWheatPerHaLastYear,
        double rasterCellAreaInSqm);
  }

  public interface FarmingReturnCalcStep {
    WoodPlanCalcStep farmingReturnCalculator(FarmingReturnCalculator farmingReturnCalc);
  }

  public interface WoodPlanCalcStep {
    PopulationUpdateManagerStep woodPlanCalculator(WoodPlanCalculator woodPlanCalc);
  }

  public interface PopulationUpdateManagerStep {
    BuildStep populationUpdateManager(PopulationUpdateManager popUpdateManager);
  }

  public interface BuildStep {
    WoodGatheringHousehold build();

    BuildStep id(long id);
  }

  private static class Builder implements PopulationStep, VillageStep, FarmingPlanCalcStep,
      WheatPatchConverterStep, WheatYieldParamsStep, FarmingReturnCalcStep, WoodPlanCalcStep,
      PopulationUpdateManagerStep, BuildStep {
    private FarmingPlanCalculator farmingPlanCalc;
    private WheatPatchConverter wheatPatchConverter;
    private FarmingReturnCalculator farmingReturnCalc;
    private WoodPlanCalculator woodPlanCalc;
    private PopulationUpdateManager popUpdateManager;

    private Long id;
    private Village village;
    private int initPopulation;
    private double initMassWheatPerHaLastYear;
    private double rasterCellAreaInSqm;

    @Override
    public WoodGatheringHousehold build() {
      return new WoodGatheringHousehold(this);
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
    public WheatPatchConverterStep farmingPlanCalculator(
        FarmingPlanCalculator farmingPlanCalc) {
      this.farmingPlanCalc = farmingPlanCalc;
      return this;
    }

    @Override
    public WheatYieldParamsStep wheatPatchConverter(WheatPatchConverter wheatPatchConverter) {
      this.wheatPatchConverter = wheatPatchConverter;
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
    public WoodPlanCalcStep farmingReturnCalculator(
        FarmingReturnCalculator farmingReturnCalc) {
      this.farmingReturnCalc = farmingReturnCalc;
      return this;
    }

    @Override
    public PopulationUpdateManagerStep woodPlanCalculator(WoodPlanCalculator woodPlanCalc) {
      this.woodPlanCalc = woodPlanCalc;
      return this;
    }

    @Override
    public BuildStep populationUpdateManager(PopulationUpdateManager popUpdateManager) {
      this.popUpdateManager = popUpdateManager;
      return this;
    }

  }

  private enum SubsistenceRequirement {
    FOOD, WOOD
  }

  private WoodGatheringHousehold(Builder builder) {
    if (builder.id != null) {
      this.id = builder.id;
    } else {
      this.id = UUID.randomUUID().getLeastSignificantBits();
    }
    this.population = builder.initPopulation;
    this.village = builder.village;
    this.farmingPlanCalc = builder.farmingPlanCalc;
    this.wheatPatchConverter = builder.wheatPatchConverter;
    this.massWheatPerHaLastYear = builder.initMassWheatPerHaLastYear;
    this.rasterCellAreaInSqm = builder.rasterCellAreaInSqm;
    this.farmingReturnCalc = builder.farmingReturnCalc;
    this.woodPlanCalc = builder.woodPlanCalc;
    this.popUpdateManager = builder.popUpdateManager;
  }

  @Override
  public void calcSubsistencePlan() {
    logger.debug(this.toString() + " calculating wheat subsistence plan");
    this.wheatSubsistencePlan = this.farmingPlanCalc.estimateNumWheatPatchesToFarm(this.population,
        this.massWheatPerHaLastYear);
    logger.debug(this.toString() + " calculating wood subsistence plan");
    this.woodSubsistencePlan = this.woodPlanCalc.estimateNumWoodPatchesRequired(this.population);
  }

  @Override
  public boolean subsistencePlanIsSatisfied() {
    logger.debug(this.toString() + " wheat subsistence plan: " + this.wheatSubsistencePlan
        + ", wood subsistence plan: " + this.woodSubsistencePlan);
    boolean wheatSubsistencePlanIsSatisfied = this.wheatPatchesForYear
        .size() >= this.wheatSubsistencePlan;
    boolean woodSubsistencePlanIsSatisfied = this.woodPatchesForYear
        .size() >= this.woodSubsistencePlan;
    if (wheatSubsistencePlanIsSatisfied && woodSubsistencePlanIsSatisfied) {
      return true;
    } else {
      return false;
    }
  }

  @Override
  public PatchOption claimPatch(Set<PatchOption> availablePatches) {
    SubsistenceRequirement priorityRequirement = chooseNextSubsistenceTarget();
    switch (priorityRequirement) {
      case FOOD:
        return claimWheatPatch(availablePatches);
      default:
        return claimWoodPatch(availablePatches);
    }
  }

  /**
   * Check whether we have currently satisfied a greater proportion of our food or wood subsistence
   * plans. Prioritise the subsistence requirement type for which the lowest proportion is
   * satisfied. In the case of a tie, choose to prioritise food or wood randomly with equal
   * probability.
   *
   * @return The subsistence target to prioritise in the next round of land patch selection.
   */
  private SubsistenceRequirement chooseNextSubsistenceTarget() {
    double propFoodSatisfied = this.wheatPatchesForYear.size() / this.wheatSubsistencePlan;
    double propWoodSatisfied = this.woodPatchesForYear.size() / this.woodSubsistencePlan;
    if (propFoodSatisfied < propWoodSatisfied) {
      return SubsistenceRequirement.FOOD;
    } else if (propWoodSatisfied < propFoodSatisfied) {
      return SubsistenceRequirement.WOOD;
    } else {
      if (RandomHelper.nextDouble() < 0.5) {
        return SubsistenceRequirement.FOOD;
      } else {
        return SubsistenceRequirement.WOOD;
      }
    }
  }

  private PatchOption claimWheatPatch(Set<PatchOption> availablePatches) {
    PatchOption chosenPatch = null;
    for (PatchOption patch : this.village.getOrderedWheatPatches()) {
      if (availablePatches.contains(patch)) {
        chosenPatch = patch;
        break;
      }
    }
    if (chosenPatch == null) {
      logger.warn(this + " could not identify an available wheat patch to select");
    } else {
      logger.debug(this + " selected wheat patch: " + chosenPatch);
    }
    this.wheatPatchesForYear.add(chosenPatch);
    this.wheatPatchConverter.convertToWheat(chosenPatch);
    return chosenPatch;
  }

  private PatchOption claimWoodPatch(Set<PatchOption> availablePatches) {
    PatchOption chosenPatch = null;
    for (PatchOption patch : this.village.getOrderedWoodPatches()) {
      if (availablePatches.contains(patch)) {
        chosenPatch = patch;
        break;
      }
    }
    if (chosenPatch == null) {
      logger.warn(this + " could not identify an available wood patch to select");
    } else {
      logger.debug(this + " selected wood patch: " + chosenPatch);
    }
    // Claiming a wood patch prevents it from being used for wheat agriculture,
    // but doesn't change then land-cover type of the patch.
    this.woodPatchesForYear.add(chosenPatch);
    return chosenPatch;
  }

  @Override
  public void updatePopulation(double precipitationMm) {
    double wheatReturnsInKg = this.farmingReturnCalc
        .getReturns(this.wheatPatchesForYear, precipitationMm);
    this.population = this.popUpdateManager
        .newPopulation(this.population, wheatReturnsInKg);
    // TODO think about whether {@code updatePopulation} should be renamed to reflect the fact that
    // it's updating multiple aspects of the household's state in response to its economic
    // performance in the previous year.
    updateWheatYieldPerHaLastYear(wheatReturnsInKg);
  }

  private void updateWheatYieldPerHaLastYear(double wheatReturnsInKg) {
    double numHaFarmedThisYear = this.wheatSubsistencePlan * this.rasterCellAreaInSqm / 10000;
    this.massWheatPerHaLastYear = wheatReturnsInKg / numHaFarmedThisYear;
  }

  @Override
  public void releasePatches() {
    this.wheatPatchesForYear.clear();
  }

  @Override
  public long getId() {
    return this.id;
  }

  public int getPopulation() {
    return this.population;
  }

  public double getWheatSubsistencePlan() {
    return this.wheatSubsistencePlan;
  }

  public int getNumWheatPatchesForYear() {
    return this.wheatPatchesForYear.size();
  }

}
