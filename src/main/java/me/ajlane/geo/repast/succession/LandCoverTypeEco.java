package me.ajlane.geo.repast.succession;

/**
 * Features of a land-cover type relevant to its participation in ecological processes.
 *
 * @author Andrew Lane
 *
 */
public interface LandCoverTypeEco extends LandCoverTypeBase {

  /**
   * @return Whether or not the land-cover type represents a mature or 'climax' community in a
   *         <a href="https://en.wikipedia.org/wiki/Frederic_Clements">Clementsian</a> sense.
   */
  boolean getIsMatureVegetation();
}
