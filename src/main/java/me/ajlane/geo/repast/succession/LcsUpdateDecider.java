package me.ajlane.geo.repast.succession;

import me.ajlane.geo.repast.succession.pathway.coded.CodedEnvrAntecedent;
import me.ajlane.geo.repast.succession.pathway.coded.CodedEnvrConsequent;

public interface LcsUpdateDecider {

  /**
   * Consider simulation cell's current physical state, the amount of time it's been in that state,
   * and the state it is currently transitioning towards. Decide whether or not a transition occurs,
   * how long it's been in its state, and any changes to its target trajectory. Return an
   * {@code LcsUpdateMsg} encoding this information.
   *
   * @param currentEnvrState Cell's current physical state
   * @param envrSimState Emergent quantities in the model relevant for the ecological succession
   *        model
   * @param targetEnvrTrans The cell's current target trajectory
   * @return Signal indicating how succession influences changes in the cell's variables in the next
   *         time step.
   */
  LcsUpdateMsg getLcsUpdateMsg(CodedEnvrAntecedent currentEnvrState, EnvrSimState envrSimState,
      CodedEnvrConsequent targetEnvrTrans);

}
