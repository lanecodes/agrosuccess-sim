package me.ajlane.geo.repast.fire;

import java.util.List;

/**
 * A fire event in the simulation
 *
 * @author Andrew Lane
 *
 * @param <T> Object capable of specifying the address of a simulation grid cell
 */
public class FireEvent<T> {
  private final int tick;
  private final T ignitionPoint;
  private final List<T> burntCells;

  /**
   * @param tick Simulation tick in which the fire event took place
   * @param ignitionPoint Address of the grid cell in which the fire started
   * @param burntCells Addresses of all grid cells burnt during the fire, including the ignition
   *        point
   */
  public FireEvent(int tick, T ignitionPoint, List<T> burntCells) {
    this.tick = tick;
    this.ignitionPoint = ignitionPoint;
    this.burntCells = burntCells;
  }

  /**
   * @return Simulation tick in which the fire event took place
   */
  int getTick() {
    return this.tick;
  }

  /**
   * @return Address of the grid cell in which the fire started
   */
  T getIgnitionPoint() {
    return this.ignitionPoint;
  }

  /**
   * @return Addresses of all grid cells burnt during the fire, including the ignition point
   */
  List<T> getBurntCells() {
    return this.burntCells;
  }

  /**
   * @return Total number of cells burnt in the fire, including the ignition point
   */
  int size() {
    return this.burntCells.size();
  }

  @Override
  public String toString() {
    return "FireEvent [tick=" + tick + ", ignitionPoint=" + ignitionPoint + ", burntCells="
        + burntCells + "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((burntCells == null) ? 0 : burntCells.hashCode());
    result = prime * result + ((ignitionPoint == null) ? 0 : ignitionPoint.hashCode());
    result = prime * result + tick;
    return result;
  }

  @SuppressWarnings("unchecked")
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    FireEvent<T> other = (FireEvent<T>) obj;
    if (burntCells == null) {
      if (other.burntCells != null)
        return false;
    } else if (!burntCells.equals(other.burntCells))
      return false;
    if (ignitionPoint == null) {
      if (other.ignitionPoint != null)
        return false;
    } else if (!ignitionPoint.equals(other.ignitionPoint))
      return false;
    if (tick != other.tick)
      return false;
    return true;
  }

}
