package me.ajlane.geo.repast.soilmoisture;

/**
 * Generate USDA curve numbers (United States Department of Agriculture, 2004) used to quantify the
 * amount of runoff expected on different surfaces following precipitation. See Millington, 2009 for
 * an illustration of this approach in a published model.
 *
 * <h3>References</h3>
 *
 * <p>
 * Millington, J. D. A., Wainwright, J., Perry, G. L. W., Romero-Calcerrada, R., & Malamud, B. D.
 * (2009). Modelling Mediterranean landscape succession-disturbance dynamics: A landscape
 * fire-succession model. Environmental Modelling and Software, 24(10), 1196–1208.
 * https://doi.org/10.1016/j.envsoft.2009.03.013
 * </p>
 *
 * <p>
 * United States Department of Agriculture. (2004). Estimation of Direct Runoff from Storm Rainfall.
 * In National Engineering Handbook Part 630 (Hydrology). Retrieved from
 * https://directives.sc.egov.usda.gov/viewerFS.aspx?hid=21422
 * </p>
 *
 * <h3>TODO</h3>
 * <p>
 * Investigate the use of enumeration types instead of integers to specify soil and land-cover
 * types. At the time of writing I cannot think how to do so in such a way that wouldn't restrict
 * potential future users to use the same land-cover and soil types as us.
 * </p>
 *
 * @author Andrew Lane
 */
public interface CurveNumberGenerator {

  /**
   * Care should be taken to ensure the integer representations of {@code soilType} and
   * {@code landCoverType} are be consistent with other numeric representations of these types used
   * elsewhere in the model.
   *
   * @param pctSlope Grid cell slope in percent
   * @param soilType Grid cell soil type
   * @param landCoverType Grid cell land-cover type
   * @return The curve number for specified slope, soil type and land-cover type. A returned value
   *         of -1 indicates that the curve number is undefined for the given grid cell attributes.
   */
  int getCurveNumber(double pctSlope, int soilType, int landCoverType);
}
