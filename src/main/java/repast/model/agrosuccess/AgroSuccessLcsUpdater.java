package repast.model.agrosuccess;

import me.ajlane.geo.repast.soilmoisture.SoilMoistureDiscretiser;
import me.ajlane.geo.repast.succession.CodedEnvrAntecedent;
import me.ajlane.geo.repast.succession.CodedEnvrConsequent;
import me.ajlane.geo.repast.succession.LcsUpdateDecider;
import me.ajlane.geo.repast.succession.LcsUpdateMsg;
import me.ajlane.geo.repast.succession.LcsUpdater;
import repast.simphony.context.Context;

public class AgroSuccessLcsUpdater implements LcsUpdater {
  Context<Object> context;
  LcsUpdateDecider updateDecider;
  SoilMoistureDiscretiser smDiscretiser;
  int nRows, nCols;

  public AgroSuccessLcsUpdater(Context<Object> context, LcsUpdateDecider lcsUpdateDecider,
      SoilMoistureDiscretiser smDiscretiser) {
    this.context = context;
    this.updateDecider = lcsUpdateDecider;
    this.smDiscretiser = smDiscretiser;
    this.nRows = (int) context.getValueLayer(LscapeLayer.Lct.name()).getDimensions().getHeight();
    this.nCols = (int) context.getValueLayer(LscapeLayer.Lct.name()).getDimensions().getWidth();

  }

  /**
   * @param x
   * @param y
   * @return a CodedEnvrAntecedent object specifying the current land cover state of cell in
   *         location (x,y).
   */
  private CodedEnvrAntecedent getCellEnvrState(int x, int y) {
    return new CodedEnvrAntecedent((int) context.getValueLayer(LscapeLayer.Lct.name()).get(x, y),
        (int) context.getValueLayer(LscapeLayer.OakRegen.name()).get(x, y),
        (int) context.getValueLayer(LscapeLayer.Aspect.name()).get(x, y),
        (int) context.getValueLayer(LscapeLayer.Pine.name()).get(x, y),
        (int) context.getValueLayer(LscapeLayer.Oak.name()).get(x, y),
        (int) context.getValueLayer(LscapeLayer.Deciduous.name()).get(x, y),
        (int) context.getValueLayer(LscapeLayer.SoilMoisture.name()).get(x, y));
  }

  private void setCellEnrvState(LcsUpdateMsg updateMsg) {
    // TODO apply current state from update message to various layers
  }

  /**
   * @param x
   * @param y
   * @return a CodedEnvrConsequent object specifying cell (x, y)'s current target state according to
   *         the state of the value layers before applying any update rules.
   */
  private CodedEnvrConsequent getCellTgtState(int x, int y) {
    int deltaD = (int) context.getValueLayer(LscapeLayer.DeltaD.name()).get(x, y);
    int deltaT = (int) context.getValueLayer(LscapeLayer.DeltaT.name()).get(x, y);

    if (deltaD == -1 || deltaT == -1) {
      assert deltaD == -1 && deltaT == -1;
      return null;
    }
    return new CodedEnvrConsequent(deltaD, deltaT);
  }

  private void setCellTgtState(LcsUpdateMsg updateMsg) {
    // TODO apply update message to delta T and delta D layers
  }

  /**
   * @param x
   * @param y
   * @return the amount of time cell (x, y) has been in its current state.
   */
  private int getCellTimeInState(int x, int y) {
    return (int) context.getValueLayer(LscapeLayer.TimeInState.name()).get(x, y);
  }

  private void setCellTimeInState(LcsUpdateMsg updateMsg) {
    // TODO apply update message to time in state layer
  }

  @Override
  public void updateLandscapeLcs() {
    LcsUpdateMsg updateMsg;
    for (int x = 0; x < nCols; x++) {
      for (int y = 0; y < nRows; y++) {
        updateMsg = updateDecider.getLcsUpdateMsg(getCellEnvrState(x, y), getCellTimeInState(x, y),
            getCellTgtState(x, y));

        setCellEnrvState(updateMsg);
        setCellTimeInState(updateMsg);
        setCellTgtState(updateMsg);
      }
    }
  }

}
