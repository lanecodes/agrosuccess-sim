package me.ajlane.geo.repast.succession;

public class LcsUpdateMsg {

  private int currentState;
  private int timeInState;
  private Integer targetState;
  private Integer targetStateTransitionTime;

  /**
   * @param currentState Numerical ID of current land cover state
   * @param timeInState Time (in years) a cell has been in its current land cover state
   * @param targetState Numerical ID of the land cover state which the cell is currently
   *        transitioning towards.
   * @param targetStateTransitionTime The time such that when timeInState >=
   *        targetStateTransitionTime the cell will transition from currentState to targetState
   */
  public LcsUpdateMsg(int currentState, int timeInState, Integer targetState,
      Integer targetStateTransitionTime) {

    this.currentState = currentState;
    this.timeInState = timeInState;
    this.targetState = targetState;
    this.targetStateTransitionTime = targetStateTransitionTime;
  }

  public int getCurrentState() {
    return currentState;
  }

  public int getTimeInState() {
    return timeInState;
  }

  public Integer getTargetState() {
    return targetState;
  }

  public Integer getTargetStateTransitionTime() {
    return targetStateTransitionTime;
  }

  @Override
  public String toString() {
    return "LandCoverStateTransitionMessage [currentState=" + currentState + ", timeInState="
        + timeInState + ", targetState=" + targetState + ", targetStateTransitionTime="
        + targetStateTransitionTime + "]";
  }

}
