package me.ajlane.geo.repast.succession;

public interface LcsUpdateDecider {
  /**
   * Consider simulation cell's current physical state, the amount of time it's been in that state,
   * and the state it is currently transitioning towards. Decide whether or not a transition occurs,
   * how long it's been in its state, and any changes to its target trajectory. Return an
   * {@code LcsUpdateMsg} encoding this information.
   * 
   * @param currentEnvrState Cell's current physical state
   * @param timeInState The number of years the cell has been in its physical state
   * @param targetEnvrTrans The cell's current target trajectory
   * @return Signal indicating how succession influences changes in the cell's variables in the next
   *         time step.
   */
  LcsUpdateMsg getLcsUpdateMsg(CodedEnvrAntecedent currentEnvrState, Integer timeInState,
      CodedEnvrConsequent targetEnvrTrans);

}
