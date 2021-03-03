package repast.model.agrosuccess.anthro;

import repast.model.agrosuccess.AgroSuccessCodeAliases.Lct;
import repast.simphony.valueLayer.IGridValueLayer;

public class WheatPatchConverter {

  private IGridValueLayer landCoverType, timeInState, timeFarmed;

  public WheatPatchConverter(IGridValueLayer landCoverType, IGridValueLayer timeInState,
      IGridValueLayer timeFarmed) {
    this.landCoverType = landCoverType;
    this.timeInState = timeInState;
    this.timeFarmed = timeFarmed;
  }

  public void convertToWheat(PatchOption patch) {
    int x = patch.location.getX();
    int y = patch.location.getY();
    if ((int) this.landCoverType.get(x, y) != Lct.Wheat.getCode()) {
      this.landCoverType.set(Lct.Wheat.getCode(), x, y);
      this.timeInState.set(0, x, y);
    } else {
      this.timeInState.set(this.timeInState.get(x, y) + 1, x, y);
    }
    this.timeFarmed.set(this.timeFarmed.get(x, y) + 1, x, y);
  }

}
