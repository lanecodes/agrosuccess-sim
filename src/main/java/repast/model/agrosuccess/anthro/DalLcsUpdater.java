package repast.model.agrosuccess.anthro;

import me.ajlane.geo.repast.succession.LcsUpdater;
import repast.model.agrosuccess.AgroSuccessCodeAliases.Lct;
import repast.simphony.valueLayer.IGridValueLayer;

/**
 * Updates wheat patches that have been farmed greater than a predefined number of times to become
 * depleted. This models how soils eventually become depleted over time and require time to recover.
 *
 * @author Andrew Lane
 */
public class DalLcsUpdater implements LcsUpdater {

  IGridValueLayer landCoverType, timeInState, timeFarmed;

  int nRows, nCols;

  int depletedThresholdTime;

  public DalLcsUpdater(IGridValueLayer landCoverType, IGridValueLayer timeInState,
      IGridValueLayer timeFarmed, int depletedThresholdTime) {
    this.landCoverType = landCoverType;
    this.timeInState = timeInState;
    this.timeFarmed = timeFarmed;

    this.nRows = (int) landCoverType.getDimensions().getHeight();
    this.nCols = (int) landCoverType.getDimensions().getWidth();

    this.depletedThresholdTime = depletedThresholdTime;
  }

  @Override
  public void updateLandscapeLcs() {
    for (int x = 0; x < nCols; x++) {
      for (int y = 0; y < nRows; y++) {
        if ((int) this.landCoverType.get(x, y) == Lct.Wheat.getCode()
            && this.timeFarmed.get(x, y) >= this.depletedThresholdTime) {
          depleteCell(x, y);
        }
      }
    }
  }

  private void depleteCell(int x, int y) {
    this.landCoverType.set(Lct.Dal.getCode(), x, y);
    this.timeInState.set(0, x, y);
    this.timeFarmed.set(0, x, y);
  }

}
