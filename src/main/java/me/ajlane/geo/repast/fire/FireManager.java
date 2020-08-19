package me.ajlane.geo.repast.fire;

/**
 * Runs the wildfire model for a simulation time step
 *
 * <p>
 * Classes implementing this interface represent a wildfire model that accounts for the number of
 * fires occurring in a year, how fire spreads in the landscape, and so on.
 * </p>
 *
 * @author Andrew Lane
 */
public interface FireManager {

  /**
   * Updates all relevant value layers to reflect the impact of an annual fire season
   */
  void startFires();

}
