package me.ajlane.geo.repast.succession;

public interface LcsUpdater {
  /**
   * Iterate through all simulation cells, updating their state variables to account for the effects
   * of ecological succession.
   */
  void updateLandscapeLcs();
}
