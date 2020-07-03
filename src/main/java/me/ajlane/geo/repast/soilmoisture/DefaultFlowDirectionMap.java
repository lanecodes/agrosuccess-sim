package me.ajlane.geo.repast.soilmoisture;

import java.util.HashMap;
import me.ajlane.geo.Direction;

/**
 * Corresponds to the direction codes used in the output of
 * <a href="https://hydrology.usu.edu/taudem/taudem5/help53/D8FlowDirections.html">TauDem D8
 * Flow</a>.
 *
 * <h3>TODO</h3>
 * <ul>
 * <li>Make the returned map unmodifiable</li>
 * </ul>
 *
 * @author Andrew Lane
 */
public class DefaultFlowDirectionMap extends HashMap<Integer, Direction> {

  private static final long serialVersionUID = 6004044969105175311L;

  public DefaultFlowDirectionMap() {
    this.put(1, Direction.E);
    this.put(2, Direction.NE);
    this.put(3, Direction.N);
    this.put(4, Direction.NW);
    this.put(5, Direction.W);
    this.put(6, Direction.SW);
    this.put(7, Direction.S);
    this.put(8, Direction.SE);
  }
}
