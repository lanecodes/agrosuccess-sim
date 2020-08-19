package me.ajlane.geo.repast.fire;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import repast.model.agrosuccess.AgroSuccessCodeAliases.Lct;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.valueLayer.ValueLayer;

/**
 * Checks whether a grid cell is flammable or not depending on its land-cover type
 *
 * <p>
 * This implementation is tied to the {@link GridPoint} cell addressing class and {@link ValueLayer}
 * value layer interface from Repast Simphony. It also depends on the {@link Lct} enum specific to
 * the AgroSuccess model.
 * </p>
 *
 * @author Andrew Lane
 */
public class DefaultFlammabilityChecker implements FlammabilityChecker<GridPoint> {

  private final ValueLayer lctLayer;
  private final static Set<Integer> notFlammableCodes =
      new HashSet<>(Arrays.asList(Lct.WaterQuarry.getCode(), Lct.Burnt.getCode()));

  /**
   * @param lctLayer Value layer specifying land-cover types as numerical codes
   */
  public DefaultFlammabilityChecker(ValueLayer lctLayer) {
    this.lctLayer = lctLayer;
  }

  @Override
  public boolean isFlammable(GridPoint gridCell) {
    int lctCode = (int) this.lctLayer.get(gridCell.getX(), gridCell.getY());
    return !notFlammableCodes.contains(lctCode);
  }

}
