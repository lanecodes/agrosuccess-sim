package me.ajlane.geo.repast.succession;

public class AgroSuccessLcsUpdateDecider implements LcsUpdateDecider {
  CodedLcsTransitionMap transMap;

  AgroSuccessLcsUpdateDecider(CodedLcsTransitionMap transMap) {
    this.transMap = transMap;
  }

  /**
   * <p>
   * We must first work out whether or not a transition will take place in this timestep, i.e decide
   * on C(t), using statements 5 and 6. This is because rules 1-4 depend on comparing quantities
   * /between/ time steps. Calculating C(t) makes it possible to then compare it to C(t-1) allowing
   * us to calculate ΔT(t) and t_{in}(t). Note that ΔD(t) is determined by the physical state of the
   * land cover cell. See statements 5 and 6 in
   * <a href="http://doi.org/10.1016/j.envsoft.2009.03.013">Millington et al. 2009</a>.
   * </p>
   * 
   * <p>
   * I have added a condition here not included in Millinton et al. 2009: if the previous time step
   * did not have a target state because no rule is provided describing its transition out of its
   * current land cover state with current physical conditions, the cell should remain in the
   * current land cover state.
   * </p>
   * 
   * @param prevTin Number of years cell had been in current land cover state in the previous
   *        timestep
   * @param prevDeltaT Total time until land cover transition takes place expected in the previous
   *        timestep
   * @param prevLcs The cell's land cover type in the previous timestep
   * @param prevDeltaD The cell's expected target state in the previous time step
   * @return The cell's land cover type in the next timestep
   */
  private Integer getThisLcs(Integer prevTin, Integer prevDeltaT, Integer prevLcs,
      Integer prevDeltaD) {
    if (prevDeltaT == null && prevDeltaD == null) {
      // statement 7, not present in Millington et al. 2009
      return prevLcs;
    } else if (prevTin < prevDeltaT) {
      // statement 6
      return prevLcs;
    } else {
      // statement 5
      return prevDeltaD;
    }
  }

  /**
   * <p>
   * Here we calculate the time the cell has been in its current state based on whether or not
   * either a transition has occurred or a new target state has been decided upon. See statements
   * 1-4 in <a href="http://doi.org/10.1016/j.envsoft.2009.03.013">Millington et al. 2009.</a>
   * </p>
   * 
   * <p>
   * Note that ~this_delta_D~ is determined based on the transition target implied by the latest
   * snapshot of the cell's physical state. By contrast ~prev_delta_D~ is the transition target
   * state the cell was heading towards before its latest physical state was taken into
   * consideration.
   * </p>
   * 
   * @param prevTin Number of years cell had been in current land cover state in the previous
   *        timestep
   * @param prevLcs The cell's land cover type in the previous timestep
   * @param thisLcs The cell's land cover type in the next timestep
   *        {@link #getThisLcs(Integer, Integer, Integer, Integer)}
   * @param prevDeltaD The cell's expected target state in the previous time step
   * @param thisDeltaD The cell's target state for the next timestep
   * @return Get the amount of time cell will have been in its state in the next timestep
   */
  private Integer getThisTimeInState(Integer prevTin, Integer prevLcs, Integer thisLcs,
      Integer prevDeltaD, Integer thisDeltaD) {
    if (thisLcs != prevLcs) {
      // statement 1
      return 1;
    } else {
      if (thisDeltaD == prevDeltaD) {
        // statement 2
        return prevTin + 1;
      } else {
        // statement 3
        return 1;
      }
    }
  }

  /**
   * Calculate the total time after which the cell will need to remain in its current state to
   * complete the next transition.
   * 
   * @param prevDeltaD The cell's expected target state in the previous time step
   * @param thisDeltaD The cell's target state for the next timestep
   * @param prevLcs prevLcs The cell's land cover type in the previous timestep
   * @param thisLcs thisLcs The cell's land cover type in the next timestep
   * @param prevDeltaT Total time until land cover transition takes place expected in the previous
   *        timestep
   * @param thisDeltaT The cell's land cover type in the next timestep
   * @return Total time until land cover transition takes place expected in the next timestep
   */
  private Integer getThisDeltaT(Integer prevDeltaD, Integer thisDeltaD, Integer prevLcs,
      Integer thisLcs, Integer prevDeltaT, Integer thisDeltaT) {
    if (prevDeltaT == null && thisDeltaT != null) {
      // statement 4.1, not in Millington et al. 2009
      return new Integer((int) Math.round((1 + thisDeltaT) / 2.0));
    } else if (thisDeltaD != prevDeltaD && thisLcs == prevLcs) {
      // statement 4'
      return new Integer((int) Math.round((prevDeltaT + thisDeltaT) / 2.0));
    } else if (thisDeltaD == prevDeltaD && thisLcs == prevLcs) {
      // statement 4.2, not in Millington et al. 2009
      return prevDeltaT;
    } else {
      return thisDeltaT;
    }
  }


  @Override
  public LcsUpdateMsg getLcsUpdateMsg(CodedEnvrAntecedent currentEnvrState, Integer timeInState,
      CodedEnvrConsequent targetEnvrTrans) {
    Integer prevDeltaT;
    Integer prevDeltaD;

    CodedEnvrConsequent physAttribTrans; // target transition state based on physical attributes
    Integer physAttribDeltaT;
    Integer physAttribDeltaD;

    Integer prevLcs = currentEnvrState.getStartState();

    if (targetEnvrTrans == null) {
      prevDeltaT = null;
      prevDeltaD = null;
    } else {
      prevDeltaT = targetEnvrTrans.getTransitionTime();
      prevDeltaD = targetEnvrTrans.getTargetState();
    }

    Integer thisLcs =
        getThisLcs(timeInState, prevDeltaT, currentEnvrState.getStartState(), prevDeltaD);

    physAttribTrans = transMap.getEnvrConsequent(currentEnvrState);
    if (physAttribTrans == null) {
      physAttribDeltaT = null;
      physAttribDeltaD = null;
    } else {
      physAttribDeltaT = physAttribTrans.getTransitionTime();
      physAttribDeltaD = physAttribTrans.getTargetState();
    }

    Integer thisTimeInState = getThisTimeInState(timeInState, currentEnvrState.getStartState(),
        thisLcs, prevDeltaD, physAttribDeltaD);

    Integer thisDeltaT =
        getThisDeltaT(prevDeltaD, physAttribDeltaD, prevLcs, thisLcs, prevDeltaT, physAttribDeltaT);
    
    return new LcsUpdateMsg((int) thisLcs, (int) thisTimeInState, physAttribDeltaD, thisDeltaT);
  }

}
