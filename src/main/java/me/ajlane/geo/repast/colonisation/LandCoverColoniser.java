package me.ajlane.geo.repast.colonisation;

/**
 * Models are expected to contain a raster layer for each land-cover type capable of colonisation,
 * e.g. oak, pine, and deciduous trees. These layers encode whether or not juvenile individuals
 * corresponding to that land-cover type are present in each grid cell of the raster grid. A
 * {@code LandCoverColoniser} is responsible for modelling the distribution of new juvenile
 * individuals among the grid cells during a simulation run.
 *
 * @author Andrew Lane
 *
 */
public interface LandCoverColoniser {

  /**
   * Update the value layers indicating the presence or absence of juvenile individuals of
   * colonising land-cover types for the current simulation time step.
   */
  void updateJuvenilePresenceLayers();

}
