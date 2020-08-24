package me.ajlane.geo.repast.fire;

/**
 * Maps the model's vegetation moisture parameter to a fuel moisture risk value
 *
 * <p>
 * The fuel moisture parameter is a unitless quantity that characterises the typical
 * vegetation moisture during the simulation. In AgroSuccess it is a linear function of the ratio of
 * temperature to precipitation. As the vegetation moisture parameter increases, the likelihood that
 * the vegetation in any particular cell will catch fire also increases. Following the Millington
 * LFSM (Millington et al. 2009), we equate the vegetation moisture parameter with the parameter
 * used to control the expected number of fires in each simulated year, λ. This class represents a
 * mapping between the vegetation moisture parameter and a multiplicative factor that can be used to
 * determine the probability of a fire spreading to a new cell. It corresponds to Table 4 in
 * Millington et al. 2009.
 * </p>
 *
 * <h3>References</h3>
 * <p>
 * Millington, J. D. A., Wainwright, J., Perry, G. L. W., Romero-Calcerrada, R., & Malamud, B. D.
 * (2009). Modelling Mediterranean landscape succession-disturbance dynamics: A landscape
 * fire-succession model. Environmental Modelling and Software, 24(10), 1196–1208.
 * https://doi.org/10.1016/j.envsoft.2009.03.013
 * </p>
 *
 * @author Andrew Lane
 * @see FireSpreader
 */
public final class AgroSuccessFuelMoistureRiskTable {

  private AgroSuccessFuelMoistureRiskTable() {}

  public static double fromVegetationMoistureParam(double vegetationMoisture) {
    double fuelMoistureRisk;
    if (vegetationMoisture < 0.2) {
      fuelMoistureRisk = 0.8;
    } else if (vegetationMoisture < 0.3) {
      fuelMoistureRisk = 0.9;
    } else if (vegetationMoisture < 0.5) {
      fuelMoistureRisk = 1.0;
    } else if (vegetationMoisture < 0.6) {
      fuelMoistureRisk = 1.1;
    } else {
      fuelMoistureRisk = 1.2;
    }
    return fuelMoistureRisk;
  }

}
