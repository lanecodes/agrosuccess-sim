package repast.model.agrosuccess;

import me.ajlane.geo.repast.soilmoisture.SoilMoistureDiscretiser;
import me.ajlane.geo.repast.succession.CodedEnvrAntecedent;
import me.ajlane.geo.repast.succession.CodedEnvrConsequent;
import me.ajlane.geo.repast.succession.LcsUpdateDecider;
import me.ajlane.geo.repast.succession.LcsUpdateMsg;
import me.ajlane.geo.repast.succession.LcsUpdater;
import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.valueLayer.IGridValueLayer;

public class AgroSuccessLcsUpdater implements LcsUpdater {
  LcsUpdateDecider updateDecider;
  SoilMoistureDiscretiser smDiscretiser;
  int nRows, nCols;
  IGridValueLayer landCoverType, oakRegen, aspect, pine, oak, deciduous, soilMoisture, timeInState,
      deltaD, deltaT;

  public AgroSuccessLcsUpdater(Context<Object> context, LcsUpdateDecider lcsUpdateDecider,
      SoilMoistureDiscretiser smDiscretiser) {

    this.landCoverType = (IGridValueLayer) context.getValueLayer(LscapeLayer.Lct.name());
    this.oakRegen = (IGridValueLayer) context.getValueLayer(LscapeLayer.OakRegen.name());
    this.aspect = (IGridValueLayer) context.getValueLayer(LscapeLayer.Aspect.name());
    this.pine = (IGridValueLayer) context.getValueLayer(LscapeLayer.Pine.name());
    this.oak = (IGridValueLayer) context.getValueLayer(LscapeLayer.Oak.name());
    this.deciduous = (IGridValueLayer) context.getValueLayer(LscapeLayer.Deciduous.name());
    this.soilMoisture = (IGridValueLayer) context.getValueLayer(LscapeLayer.SoilMoisture.name());
    this.timeInState = (IGridValueLayer) context.getValueLayer(LscapeLayer.TimeInState.name());
    this.deltaD = (IGridValueLayer) context.getValueLayer(LscapeLayer.DeltaD.name());
    this.deltaT = (IGridValueLayer) context.getValueLayer(LscapeLayer.DeltaT.name());

    this.nRows = (int) landCoverType.getDimensions().getHeight();
    this.nCols = (int) landCoverType.getDimensions().getWidth();

    this.updateDecider = lcsUpdateDecider;
    this.smDiscretiser = smDiscretiser;
  }

  /**
   * @param x
   * @param y
   * @return a CodedEnvrAntecedent object specifying the current land cover state of cell in
   *         location (x,y).
   */
  private CodedEnvrAntecedent getCellEnvrState(int x, int y) {
    return new CodedEnvrAntecedent((int) landCoverType.get(x, y), (int) oakRegen.get(x, y),
        (int) aspect.get(x, y), (int) pine.get(x, y), (int) oak.get(x, y),
        (int) deciduous.get(x, y), smDiscretiser.getSoilMoistureLevel(soilMoisture.get(x, y)));
  }

  /**
   * @param x
   * @param y
   * @return a CodedEnvrConsequent object specifying cell (x, y)'s current target state according to
   *         the state of the value layers before applying any update rules.
   */
  private CodedEnvrConsequent getCellTgtState(int x, int y) {
    int cellDeltaD = (int) deltaD.get(x, y);
    int cellDeltaT = (int) deltaT.get(x, y);

    if (cellDeltaD == -1 || cellDeltaT == -1) {
      assert cellDeltaD == -1 && cellDeltaT == -1;
      return null;
    }
    return new CodedEnvrConsequent(cellDeltaD, cellDeltaT);
  }

  /**
   * @param x
   * @param y
   * @return the amount of time cell (x, y) has been in its current state.
   */
  private int getCellTimeInState(int x, int y) {
    return (int) timeInState.get(x, y);
  }

  @Override
  @ScheduledMethod(start = 1, interval = 1, priority = 0)
  public void updateLandscapeLcs() {
    LcsUpdateMsg updateMsg;
    for (int x = 0; x < nCols; x++) {
      for (int y = 0; y < nRows; y++) {
        updateMsg = updateDecider.getLcsUpdateMsg(getCellEnvrState(x, y), getCellTimeInState(x, y),
            getCellTgtState(x, y));

        landCoverType.set(updateMsg.getCurrentState().getStartState(), x, y);
        timeInState.set(updateMsg.getTimeInState(), x, y);

        if (updateMsg.getTargetTransition() == null) {
          deltaD.set(-1, x, y);
          deltaT.set(-1, x, y);
        } else {
          deltaD.set(updateMsg.getTargetTransition().getTargetState(), x, y);
          deltaT.set(updateMsg.getTargetTransition().getTransitionTime(), x, y);
        }
      }
    }
  }

}
