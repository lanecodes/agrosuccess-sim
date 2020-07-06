package me.ajlane.geo.repast.succession;

/**
 * A characterisation of the type of land-cover at a particular location.
 *
 * @author Andrew Lane
 *
 */
public interface LandCoverTypeBase {
  /**
   * @return A unique alias used to identify the land-cover type
   */
  String getCode();

  /**
   * @return A natural language description of the land-cover type
   */
  String getDescr();

  /**
   * @return A numerical identifier for the land-cover type
   */
  int getNum();
}
