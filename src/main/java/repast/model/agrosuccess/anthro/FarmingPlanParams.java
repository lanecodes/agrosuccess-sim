package repast.model.agrosuccess.anthro;

import repast.model.agrosuccess.params.ParameterOutOfBoundsException;

/**
 * Parameters which Households need to determine how many land patches they need to convert to
 * farming to provide themselves with enough calories to live on.
 *
 * @author Andrew Lane
 *
 */
public class FarmingPlanParams {

  private final double energyPerPersonPerDay;
  private final double energyPerKgWheat;
  private final double farmerConScalar;
  private final double cropReseedProp;

  /**
   * @param energyPerPersonPerDay Energy in kcal which each member of a household needs to consume
   *        per day
   * @param energyPerKgWheat Energy the members of the household can extract by digesting 1 kg wheat
   * @param farmerConScalar Unitless parameter > 0 controlling how confident farmers are. Larger
   *        values of {@code farmerConScalar} will result in farmers choosing to plant less wheat.
   *        <bold>Note:</bold> Ullah 2013 documents that increasing {@code farmerConScalar}
   *        <emph>increases</emph> the amount of wheat planted, but this is inconsistent with Eq.
   *        4.1 from that report.
   * @param cropReseedProp Proportion of the crop \in [0,1] held back to reseed in the next year.
   */
  public FarmingPlanParams(double energyPerPersonPerDay, double energyPerKgWheat, double farmerConScalar,
      double cropReseedProp) {
    this.energyPerPersonPerDay =
        checkBounds("energyPerPersonPerDay", energyPerPersonPerDay, 0, Double.MAX_VALUE);
    this.energyPerKgWheat = checkBounds("energyPerKgWheat", energyPerKgWheat, 0, Double.MAX_VALUE);
    this.farmerConScalar = checkBounds("farmerConScalar", farmerConScalar, 0, Double.MAX_VALUE);
    this.cropReseedProp = checkBounds("cropReseedProp", cropReseedProp, 0, 1);
  }

  private static double checkBounds(String paramName, double param, double lowerBound,
      double upperBound) {
    if (param < lowerBound || param > upperBound) {
      throw new ParameterOutOfBoundsException(paramName, param, lowerBound, upperBound);
    }
    return param;
  }

  /**
   * @return the energyPerPersonPerDay
   */
  public double getEnergyPerPersonPerDay() {
    return energyPerPersonPerDay;
  }

  /**
   * @return the energyPerKgWheat
   */
  public double getEnergyPerKgWheat() {
    return energyPerKgWheat;
  }

  /**
   * @return the farmerConScalar
   */
  public double getFarmerConScalar() {
    return farmerConScalar;
  }

  /**
   * @return the cropReseedProp
   */
  public double getCropReseedProp() {
    return cropReseedProp;
  }

  @Override
  public String toString() {
    return "FarmingPlanParams [energyPerPersonPerDay=" + energyPerPersonPerDay
        + ", energyPerKgWheat=" + energyPerKgWheat + ", farmerConScalar=" + farmerConScalar
        + ", cropReseedProp=" + cropReseedProp + "]";
  }

}
