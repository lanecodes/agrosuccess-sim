package me.ajlane.geo.repast.fire;


/**
 * Checks whether a grid cell is flammable or not depending on its land-cover type
 *
 * <p>
 * Objects of this type are useful because multiple classes are liable to need to be able to
 * identify whether or not grid cells are flammable. Centralising this logic into classes implementing this
 * interface promotes code reuse.
 * </p>
 *
 * @author Andrew Lane
 * @param <T> Type of object capable of specifying the address of a simulation grid cell
 */
public interface FlammabilityChecker<T> {

  /**
   * Checks whether a simulation grid cell is flammable
   *
   * @param gridCell Simulation grid cell to test for flammability
   * @return {@code true} if the grid cell is flammable, {@code false} otherwise
   */
  boolean isFlammable(T gridCell);

}
