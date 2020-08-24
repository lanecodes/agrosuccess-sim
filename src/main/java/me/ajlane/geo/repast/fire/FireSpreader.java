package me.ajlane.geo.repast.fire;

import repast.simphony.space.grid.GridPoint;


public interface FireSpreader {

  /**
   * Spread a fire around the landscape from a starting point. See
   * <a href="https://doi.org/10.1016/j.envsoft.2009.03.013"> Millington et al. 2009</a> for the
   * inspiration behind the fire spread mechanism.
   *
   * @param ignitionPoint Point in the landscape where the fire starts
   * @param fuelMoistureFactor Dimensionless factor parameterising the amount of moisture in the
   *        fuel at the time of the fire
   */
  void spreadFire(GridPoint ignitionPoint, double fuelMoistureFactor);

}
