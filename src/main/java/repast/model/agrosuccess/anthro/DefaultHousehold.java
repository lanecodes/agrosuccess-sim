package repast.model.agrosuccess.anthro;

import java.util.Set;
import java.util.UUID;

/**
 * If no {@code id} is specified for the household, one will be randomly generated.
 *
 * @author Andrew Lane
 *
 */
public class DefaultHousehold implements Household {

  private final FarmingReturnCalculator farmingReturnCalc;
  private final FarmingPlanParams farmingPlanParams;
  private final WoodReturnCalculator woodReturnCalc;
  private final PopulationUpdateManager popUpdateManager;

  private final long id;
  private final Village village;

  private int population;
  private double massWheatPerHaLastYear;

  public static PopulationStep builder() {
    return new Builder();
  }

  public interface PopulationStep {
    VillageStep initPopulation(int initPopulation);
  }

  public interface VillageStep {
    FarmingReturnCalcStep village(Village village);
  }

  public interface FarmingReturnCalcStep {
    FarmingPlanParamsStep farmingReturnCalculator(FarmingReturnCalculator farmingReturnCalc);
  }

  public interface FarmingPlanParamsStep {
    WoodReturnCalcStep farmingPlanParams(FarmingPlanParams farmingPlanParams);
  }

  public interface WoodReturnCalcStep {
    PopulationUpdateManagerStep woodReturnCalculator(WoodReturnCalculator woodReturnCalc);
  }

  public interface PopulationUpdateManagerStep {
    BuildStep populationUpdateManager(PopulationUpdateManager popUpdateManager);
  }

  public interface BuildStep {
    DefaultHousehold build();
    BuildStep id(long id);
  }

  private static class Builder implements PopulationStep, VillageStep, FarmingReturnCalcStep,
      FarmingPlanParamsStep, WoodReturnCalcStep, PopulationUpdateManagerStep, BuildStep {
    private FarmingReturnCalculator farmingReturnCalc;
    private FarmingPlanParams farmingPlanParams;
    private WoodReturnCalculator woodReturnCalc;
    private PopulationUpdateManager popUpdateManager;

    private Long id;
    private Village village;
    private int initPopulation;

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
    public FarmingReturnCalcStep village(Village village) {
      this.village = village;
      return this;
    }

    @Override
    public FarmingPlanParamsStep farmingReturnCalculator(
        FarmingReturnCalculator farmingReturnCalc) {
      this.farmingReturnCalc = farmingReturnCalc;
      return this;
    }

    @Override
    public WoodReturnCalcStep farmingPlanParams(FarmingPlanParams farmingPlanParams) {
      this.farmingPlanParams = farmingPlanParams;
      return this;
    }

    @Override
    public PopulationUpdateManagerStep woodReturnCalculator(WoodReturnCalculator woodReturnCalc) {
      this.woodReturnCalc = woodReturnCalc;
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
    this.farmingReturnCalc = builder.farmingReturnCalc;
    this.farmingPlanParams = builder.farmingPlanParams;
    this.woodReturnCalc = builder.woodReturnCalc;
    this.popUpdateManager = builder.popUpdateManager;
  }

  @Override
  public PatchOption claimPatch(Set<PatchOption> availablePatches) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean subsPlanSatisfied() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public long getId() {
    return this.id;
  }

}
