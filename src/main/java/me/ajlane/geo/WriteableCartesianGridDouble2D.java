package me.ajlane.geo;

/**
 * Extends the {@link CartesianGridDouble2D} by adding a method to set {@code double} values in the
 * grid.
 *
 * @author Andrew Lane
 */
public interface WriteableCartesianGridDouble2D extends CartesianGridDouble2D {

  /**
   * @param value Value to set
   * @param loc Address of grid cell to set value of
   */
  void setValue(double value, GridLoc loc);

}
