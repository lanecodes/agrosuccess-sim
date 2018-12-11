package me.ajlane.geo.repast.succession;

public class AgroSuccessLcsUpdateDecider implements LcsUpdateDecider {
  CodedLcsTransitionMap transMap;

  public AgroSuccessLcsUpdateDecider(CodedLcsTransitionMap transMap) {
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

  /**
   * If a change in land cover state is to take place in this time step, produce a new
   * {@code CodedEnvrAntecedent} object which reflects this change, otherwise return the old one.
   * 
   * @param physicalEnvrState The physical state of the simulation cell as it was in the last time
   *        step
   * @param newLcs Numerical code of the new land cover state determined based on the physical state
   *        of the simulation cell, and whether or not the cell has been in that state long enough
   *        for a land cover transition to take place.
   * @return The physical state of the simulation cell correcting for if a land cover transition has
   *         taken place in this time step.
   */
  private CodedEnvrAntecedent refreshPhysicalEnvrState(CodedEnvrAntecedent physicalEnvrState,
      Integer newLcs) {
    if (newLcs != physicalEnvrState.getStartState()) {
      physicalEnvrState = new CodedEnvrAntecedent(newLcs, physicalEnvrState.getSuccessionPathway(),
          physicalEnvrState.getAspect(), physicalEnvrState.getPineSeeds(),
          physicalEnvrState.getOakSeeds(), physicalEnvrState.getDeciduousSeeds(),
          physicalEnvrState.getWater());
      return physicalEnvrState;
    } else {
      return physicalEnvrState;
    }
  }


  @Override
  public LcsUpdateMsg getLcsUpdateMsg(CodedEnvrAntecedent currentEnvrState, Integer timeInState,
      CodedEnvrConsequent targetEnvrTrans) {
    // store relevant details about previous state
    Integer prevDeltaT = (targetEnvrTrans == null) ? null : targetEnvrTrans.getTransitionTime();
    Integer prevDeltaD = (targetEnvrTrans == null) ? null : targetEnvrTrans.getTargetState();
    Integer prevLcs = currentEnvrState.getStartState();

    // work out land cover state for this timestep and update record of physical state of cell
    Integer thisLcs = getThisLcs(timeInState, prevDeltaT, prevLcs, prevDeltaD);
    currentEnvrState = refreshPhysicalEnvrState(currentEnvrState, thisLcs);

    // target transition state based on cell's physical attributes
    CodedEnvrConsequent physAttribTrans = transMap.getEnvrConsequent(currentEnvrState);
    Integer physAttribDeltaT =
        (physAttribTrans == null) ? null : physAttribTrans.getTransitionTime();
    Integer physAttribDeltaD = (physAttribTrans == null) ? null : physAttribTrans.getTargetState();

    // get updated time in state accounting for if any land cover transitions/ trajectory changes 
    // have occurred
    Integer thisTimeInState =
        getThisTimeInState(timeInState, prevLcs, thisLcs, prevDeltaD, physAttribDeltaD);

    // get updated target transition time
    Integer thisDeltaT =
        getThisDeltaT(prevDeltaD, physAttribDeltaD, prevLcs, thisLcs, prevDeltaT, physAttribDeltaT);

    return new LcsUpdateMsg((int) thisLcs, (int) thisTimeInState, physAttribDeltaD, thisDeltaT);
  }

}
