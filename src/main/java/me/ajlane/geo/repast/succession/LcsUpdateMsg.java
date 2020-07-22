package me.ajlane.geo.repast.succession;

import repast.model.agrosuccess.AgroSuccessLcsUpdater;

/**
 * Container providing all the information needed to determine a simulation cell's next land cover
 * state, determined according to the succession rules enforced by an {@link LcsUpdateDecider}.
 * These messages are consumed by {@link AgroSuccessLcsUpdater}.
 *
 * @author Andrew Lane
 *
 */
public class LcsUpdateMsg {

  private final CodedEnvrAntecedent currentState;
  private final int timeInState;
  private final CodedEnvrConsequent targetState;

  /**
   * @param currentState The land-cover state and combination of environmental conditions in the
   *        grid cell in the next time step
   * @param timeInState Number of years the cell has been on its current transition trajectory
   * @param targetState The land-cover state the grid cell is currently in the process of
   *        transitioning to, and the total amount of time the transition will take
   */
  LcsUpdateMsg(CodedEnvrAntecedent currentState, int timeInState, CodedEnvrConsequent targetState) {
    this.currentState = currentState;
    this.timeInState = timeInState;
    this.targetState = targetState;
  }

  /**
   * @return The land-cover state and combination of environmental conditions in the grid cell in
   *         the next time step
   */
  public CodedEnvrAntecedent getCurrentState() {
    return currentState;
  }

  /**
   * @return Number of years the cell has been on its current transition trajectory
   */
  public int getTimeInState() {
    return timeInState;
  }

  /**
   * @return The land-cover state the grid cell is currently in the process of transitioning to, and
   *         the total amount of time the transition will take
   */
  public CodedEnvrConsequent getTargetTransition() {
    return targetState;
  }

}
