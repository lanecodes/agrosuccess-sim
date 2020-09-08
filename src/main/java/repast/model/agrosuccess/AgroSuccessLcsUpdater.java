package repast.model.agrosuccess;

import org.apache.log4j.Logger;
import me.ajlane.geo.repast.soilmoisture.SoilMoistureDiscretiser;
import me.ajlane.geo.repast.succession.EnvrSimState;
import me.ajlane.geo.repast.succession.LcsUpdateDecider;
import me.ajlane.geo.repast.succession.LcsUpdateMsg;
import me.ajlane.geo.repast.succession.LcsUpdater;
import me.ajlane.geo.repast.succession.pathway.coded.CodedEnvrAntecedent;
import me.ajlane.geo.repast.succession.pathway.coded.CodedEnvrConsequent;
import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ISchedule;
import repast.simphony.valueLayer.IGridValueLayer;

public class AgroSuccessLcsUpdater implements LcsUpdater {
  final static Logger logger = Logger.getLogger(AgroSuccessLcsUpdater.class);
  LcsUpdateDecider updateDecider;
  SoilMoistureDiscretiser smDiscretiser;
  int nRows, nCols;
  IGridValueLayer landCoverType, oakRegen, aspect, pine, oak, deciduous, soilMoisture, timeInState,
      deltaD, deltaT, fireCount, oakAge;
  ISchedule schedule;

  public AgroSuccessLcsUpdater(Context<Object> context, LcsUpdateDecider lcsUpdateDecider,
      SoilMoistureDiscretiser smDiscretiser) {

    this.landCoverType = getValueLayer(context, LscapeLayer.Lct);
    this.oakRegen = getValueLayer(context, LscapeLayer.OakRegen);
    this.aspect = getValueLayer(context, LscapeLayer.Aspect);
    this.pine = getValueLayer(context, LscapeLayer.Pine);
    this.oak = getValueLayer(context, LscapeLayer.Oak);
    this.deciduous = getValueLayer(context, LscapeLayer.Deciduous);
    this.soilMoisture = getValueLayer(context, LscapeLayer.SoilMoisture);
    this.timeInState = getValueLayer(context, LscapeLayer.TimeInState);
    this.deltaD = getValueLayer(context, LscapeLayer.DeltaD);
    this.deltaT = getValueLayer(context, LscapeLayer.DeltaT);
    this.fireCount = getValueLayer(context, LscapeLayer.FireCount);
    this.oakAge = getValueLayer(context, LscapeLayer.OakAge);

    this.nRows = (int) landCoverType.getDimensions().getHeight();
    this.nCols = (int) landCoverType.getDimensions().getWidth();

    this.updateDecider = lcsUpdateDecider;
    this.smDiscretiser = smDiscretiser;

    this.schedule = RunEnvironment.getInstance().getCurrentSchedule();
  }

  private IGridValueLayer getValueLayer(Context<Object> context, LscapeLayer layerId) {
    IGridValueLayer layer = (IGridValueLayer) context.getValueLayer(layerId.name());
    if (layer == null) {
      throw new NullPointerException("No " + layerId + " layer found in context");
    }
    return layer;
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

  private double getCellFireFrequency(int x, int y) {
    return fireCount.get(x, y) / schedule.getTickCount();
  }

  private int getCellOakAge(int x, int y) {
    return (int) oakAge.get(x, y);
  }

  @Override
  // @ScheduledMethod(start = 1, interval = 1, priority = 0)
  public void updateLandscapeLcs() {
    LcsUpdateMsg updateMsg;
    for (int x = 0; x < nCols; x++) {
      for (int y = 0; y < nRows; y++) {
        CodedEnvrAntecedent prevEnvrState = getCellEnvrState(x, y);
        EnvrSimState envrSimState = new EnvrSimState(getCellTimeInState(x, y),
            getCellFireFrequency(x, y), getCellOakAge(x, y));
        updateMsg =
            updateDecider.getLcsUpdateMsg(prevEnvrState, envrSimState, getCellTgtState(x, y));

        landCoverType.set(updateMsg.getCurrentState().getStartState(), x, y);
        timeInState.set(updateMsg.getTimeInState(), x, y);

        if (updateMsg.getTargetTransition() == null) {
          deltaD.set(-1, x, y);
          deltaT.set(-1, x, y);
        } else {
          deltaD.set(updateMsg.getTargetTransition().getTargetState(), x, y);
          deltaT.set(updateMsg.getTargetTransition().getTransitionTime(), x, y);
        }

        // Ensure seed presence is correct if land cover state updated
        if (updateMsg.getCurrentState().getStartState() != prevEnvrState.getStartState()) {
          this.pine.set(updateMsg.getCurrentState().getPineSeeds(), x, y);
          this.oak.set(updateMsg.getCurrentState().getOakSeeds(), x, y);
          this.deciduous.set(updateMsg.getCurrentState().getDeciduousSeeds(), x, y);
        }
      }
    }
    logger.debug("land-cover state updated in tick " + this.schedule.getTickCount());
  }

}
