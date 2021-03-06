package me.ajlane.geo.repast.soilmoisture;

/**
 * Converts continuous values for soil moisture to discrete levels used to determine whether soil is
 * xeric, mesic or hydric, per the AgroSuccess model. This model follows the scheme specified in
 * Millington et al. 2009. That is:
 * 
 * xeric <=500 mm; 500 mm< mesic <=1000 mm; hydric >1000 mm
 * 
 * @author Andrew Lane
 */
public class AgroSuccessSoilMoistureDiscretiser implements SoilMoistureDiscretiser {
  private int mesicThreshold, hydricThreshold;

  /**
   * @param soilMoistureParams Parameters needed to determine how to discretise continuous soil 
   *    moisture values into discrete states. 
   * @see me.ajlane.geo.repast.soilmoisture.SoilMoistureParams
   */
  public AgroSuccessSoilMoistureDiscretiser(SoilMoistureParams soilMoistureParams) {
    this.mesicThreshold = soilMoistureParams.getMesicThreshold();
    this.hydricThreshold = soilMoistureParams.getHydricThreshold();
  }
 

  /**
   * @return 0=xeric, 1=mesic, 2=hydric
   * @see me.ajlane.geo.repast.soilmoisture.SoilMoistureDiscretiser#getSoilMoistureLevel(int)
   */
  @Override
  public int getSoilMoistureLevel(int soilMoistureMM) {
    if (soilMoistureMM <= this.mesicThreshold) {
      return 0;
    } else if (soilMoistureMM <= this.hydricThreshold) {
      return 1;
    }
    return 2;
  }

  /**
   * @return 0=xeric, 1=mesic, 2=hydric
   * @see me.ajlane.geo.repast.soilmoisture.SoilMoistureDiscretiser#getSoilMoistureLevel(double)
   */
  @Override
  public int getSoilMoistureLevel(double soilMoistureMM) {
    return getSoilMoistureLevel((int) Math.round(soilMoistureMM));
  }
}
