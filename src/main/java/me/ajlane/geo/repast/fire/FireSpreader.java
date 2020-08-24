package me.ajlane.geo.repast.fire;

import java.util.List;
import repast.simphony.space.grid.GridPoint;


/**
 * Modifies grid layers to simulate the spread of a wildfire given an ignition point
 *
 * @author Andrew Lane
 * @param <T> Type of object used to specify the address of a grid cell
 */
public interface FireSpreader<T> {

  /**
   * Spread a fire around the landscape from a starting point
   *
   * <p>
   * See <a href="https://doi.org/10.1016/j.envsoft.2009.03.013"> Millington et al. 2009</a> for the
   * inspiration behind the fire spread mechanism.
   * </p>
   *
   * @param ignitionPoint Point in the landscape where the fire starts
   * @return An object specifying the cells burnt during the fire
   */
  List<T> spreadFire(GridPoint ignitionPoint);

}
