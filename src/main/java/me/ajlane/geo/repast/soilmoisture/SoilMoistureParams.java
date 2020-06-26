/**
 *
 */
package me.ajlane.geo.repast.soilmoisture;

/**
 * <p>
 * Objects of this class hold parameters needed to parameterise the AgroSuccess model's soil
 * moisture discretisation procedure.
 * </p>
 *
 * <p>
 * Following the scheme specified in Millington et al. 2009, there are three categories of soil
 * moisture:
 * </p>
 *
 * <ul>
 * <li> xeric if soil moisure &le; mesicThreshold (mm)</li>
 * <li> mesic if mesicThreshold (mm) &lt soil moisure &le; hydricThreshold (mm)</li>
 * <li> hydric if soil moisure &gt hydricThreshold (mm)</li>
 * </ul>
 *
 * <p>
 * Millington et al. 2009 used the values mesicThreshold = 500 mm, hydricThreshold = 1000 mm.
 * </p>
 *
 * <h3>References</h3>
 * <p>
 * Millington, J. D. A., Wainwright, J., Perry, G.
 * L. W., Romero-Calcerrada, R., & Malamud, B. D. (2009). Modelling
 * Mediterranean landscape succession-disturbance dynamics: A landscape
 * fire-succession model. Environmental Modelling and Software, 24(10),
 * 1196–1208. https://doi.org/10.1016/j.envsoft.2009.03.013
 * <p>
 *
 * @author Andrew Lane
 *
 */
public class SoilMoistureParams {
  private int mesicThreshold, hydricThreshold;

  /**
   * @param mesicThreshold Soil moisture threshold below which soil conditions are considered xeric.
   * @param hydricThreshold Soil moisture threshold above which soil conditions are considered
   *        hydric.
   */
  public SoilMoistureParams(int mesicThreshold, int hydricThreshold) {
    this.mesicThreshold = mesicThreshold;
    this.hydricThreshold = hydricThreshold;
  }

  public int getMesicThreshold() {
    return mesicThreshold;
  }

  public int getHydricThreshold() {
    return hydricThreshold;
  }

  @Override
  public String toString() {
    return "SoilMoistureParams [mesicThreshold=" + mesicThreshold + ", hydricThreshold="
        + hydricThreshold + "]";
  }

  @Override
  public boolean equals(Object other){
    if (other == null) return false;
    if (other == this) return true;
    if (!(other instanceof SoilMoistureParams)) return false;
    SoilMoistureParams otherSmp = (SoilMoistureParams)other;
    if (otherSmp.getMesicThreshold() == this.getMesicThreshold()
        && otherSmp.getHydricThreshold() == this.getHydricThreshold()) {
      return true;
    } else return false;
    }

}
