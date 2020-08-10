package me.ajlane.geo.repast.succession;

import java.util.Set;
import repast.model.agrosuccess.AgroSuccessLcsUpdater;
import repast.model.agrosuccess.LscapeLayer;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.valueLayer.IGridValueLayer;
import repast.simphony.valueLayer.ValueLayer;

/**
 * Updates the {@link LscapeLayer#OakAge} layer with the number of years
 *
 * @author Andrew Lane
 * @see AgroSuccessLcsUpdater
 */
public class OakAgeUpdater {
  private final IGridValueLayer oakAge;
  private final ValueLayer landCoverType;
  private final Set<Integer> matureOakCodes;
  private final int nRows, nCols;
  private final int nullValue;

  /**
   * {@code nullValue} defaults to -1
   *
   * @see #OakAgeUpdater(IGridValueLayer, ValueLayer, Set, int)
   */
  public OakAgeUpdater(IGridValueLayer oakAge, ValueLayer landCoverType,
      Set<Integer> matureOakCodes) {
    this(oakAge, landCoverType, matureOakCodes, -1);
  }

  /**
   * @param oakAge Value layer specifying the number of years each grid cell has contained mature
   *        oak vegetation
   * @param landCoverType Value layer specifying the land-cover type of each grid cell
   * @param matureOakCodes Land-cover type codes corresponding to land-cover types containing mature
   *        oak
   * @param nullValue The value used in {@code oakAge} to indicate a cell doesn't contain mature
   *        oak, and therefore does not have a corresponding 'oak age'
   */
  public OakAgeUpdater(IGridValueLayer oakAge, ValueLayer landCoverType,
      Set<Integer> matureOakCodes, int nullValue) {
    this.oakAge = oakAge;
    this.landCoverType = landCoverType;
    this.matureOakCodes = matureOakCodes;
    this.nullValue = nullValue;

    this.nRows = (int) oakAge.getDimensions().getHeight();
    this.nCols = (int) oakAge.getDimensions().getWidth();
  }

  /**
   * <p>
   * Update the oak age layer taking into account changes in the grid cells' land-cover states.
   * </p>
   *
   * <h3>Note</h3>
   * <p>
   * This should be scheduled to run <emph>after</emph>
   * {@link AgroSuccessLcsUpdater#updateLandscapeLcs()}
   * </p>
   */
  @ScheduledMethod(start = 1, interval = 1, priority = -1)
  public void update() {
    for (int x = 0; x < nCols; x++) {
      for (int y = 0; y < nRows; y++) {
        int newOakAge = getNewOakAgeForCell(
            matureOakCodes.contains((int) this.landCoverType.get(x, y)),
            (int) this.oakAge.get(x, y));
        this.oakAge.set(newOakAge, x, y);
      }
    }
  }

  /**
   * @param isMatureOakLandCover {@code true} if the land-cover type of the cell contains mature oak
   *        vegetation
   * @param prevCellOakAge Number of years the cell had contained mature oak vegetation in the
   *        previous time step
   * @return Oak age for the cell in the next time step
   */
  private int getNewOakAgeForCell(boolean isMatureOakLandCover, int prevCellOakAge) {
    if (!isMatureOakLandCover) {
      return this.nullValue;
    }
    if (prevCellOakAge == this.nullValue) {
      return 0;
    } else {
      return prevCellOakAge + 1;
    }
  }

}
