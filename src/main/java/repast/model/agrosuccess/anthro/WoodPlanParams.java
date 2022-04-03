package repast.model.agrosuccess.anthro;

import repast.model.agrosuccess.params.ParameterOutOfBoundsException;

public class WoodPlanParams {
  private final double firewoodPerCapitaPerYear;
  private final double climaxForestBiomassDensity;
  private final double firewoodBiomassRemovalRate;

  /**
   * @param firewoodPerCapitaPerYear Mass of firewood (in kg) required per capita per year
   * @param climaxForestBiomassDensity Maximum density of biomass in climax forest (in kg/ha)
   * @param firewoodBiomassRemovalRate Proportion of biomass removed per year for firewood gathering
   *        purposes (between 0 and 1).
   */
  public WoodPlanParams(double firewoodPerCapitaPerYear, double climaxForestBiomassDensity,
      double firewoodBiomassRemovalRate) {
    this.firewoodPerCapitaPerYear = checkBounds("firewoodPerCapitaPerYear",
        firewoodPerCapitaPerYear, 0, Double.MAX_VALUE);
    this.climaxForestBiomassDensity = checkBounds("climaxForestBiomassDensity",
        climaxForestBiomassDensity, 0, Double.MAX_VALUE);
    this.firewoodBiomassRemovalRate = checkBounds("firewoodBiomassRemovalRate",
        firewoodBiomassRemovalRate, 0, 1);
  }

  private static double checkBounds(String paramName, double param, double lowerBound,
      double upperBound) {
    if (param < lowerBound || param > upperBound) {
      throw new ParameterOutOfBoundsException(paramName, param, lowerBound, upperBound);
    }
    return param;
  }

  /**
   * @return the firewoodPerCapitaPerYear
   */
  public double getFirewoodPerCapitaPerYear() {
    return firewoodPerCapitaPerYear;
  }

  /**
   * @return the climaxForestBiomassDensity
   */
  public double getClimaxForestBiomassDensity() {
    return climaxForestBiomassDensity;
  }

  /**
   * @return the firewoodBiomassRemovalRate
   */
  public double getFirewoodBiomassRemovalRate() {
    return firewoodBiomassRemovalRate;
  }

  @Override
  public String toString() {
    return "WoodPlanParams [firewoodPerCapitaPerYear=" + firewoodPerCapitaPerYear
        + ", climaxForestBiomassDensity=" + climaxForestBiomassDensity
        + ", firewoodBiomassRemovalRate=" + firewoodBiomassRemovalRate + "]";
  }

}
