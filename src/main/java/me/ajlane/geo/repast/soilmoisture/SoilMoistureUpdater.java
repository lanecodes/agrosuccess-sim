package me.ajlane.geo.repast.soilmoisture;

/**
 * Update soil moisture in the landscape.
 *
 * @author Andrew Lane
 */
public interface SoilMoistureUpdater {

  /**
   * @param annualPrecip Total precipitation for the annual time step (in mm) falling uniformly on
   *        the landscape.
   */
  void updateSoilMoisture(double annualPrecip);

}
