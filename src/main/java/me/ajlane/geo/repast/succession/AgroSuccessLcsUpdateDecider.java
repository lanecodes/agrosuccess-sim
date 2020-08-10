package me.ajlane.geo.repast.succession;

/**
 * <p>
 * Implements the ecological succession rules for AgroSuccess.
 * </p>
 *
 * <p>
 * Through the {@link #getLcsUpdateMsg(CodedEnvrAntecedent, Integer, CodedEnvrConsequent)} method,
 * this class can be used to determine what the state of an individual grid cell should be in the
 * next time step, taking into account its environmental state and current transition trajectory.
 * </p>
 *
 * @author Andrew Lane
 */
public class AgroSuccessLcsUpdateDecider implements LcsUpdateDecider {
  private final CodedLcsTransitionMap transMap;
  private final SuccessionPathwayUpdater successionUpdater;
  private final SeedStateUpdater seedUpdater;

  /**
   * Note that the integer codes used to encode land-cover types in {@code transMap} must correspond
   * to the codes used in {@code matureVegMap}.
   *
   * @param transMap Maps environmental state (current land-cover type, slope, soil moisture etc) to
   *        a target transition state and time. Land-cover type is encoded as an integer.
   * @param matureVegMap Maps land-cover type codes to a boolean value indicating whether the
   *        corresponding land-cover type represents a mature vegetation community or not.
   */
  public AgroSuccessLcsUpdateDecider(CodedLcsTransitionMap transMap,
      SuccessionPathwayUpdater successionUpdater, SeedStateUpdater seedUpdater) {
    this.transMap = transMap;
    this.successionUpdater = successionUpdater;
    this.seedUpdater = seedUpdater;
  }

  /**
   * <p>
   * We must first work out whether or not a transition will take place in this timestep, i.e decide
   * on C(t), using S3a and S3b. This is because rules S1a--S1c and S2a--S2e depend on comparing
   * quantities /between/ time steps. Calculating C(t) makes it possible to then compare it to
   * C(t-1) allowing us to calculate ΔT(t) and t_{in}(t). Note that ΔD(t) is determined by the
   * physical state of the land cover cell.
   * </p>
   *
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
      // S3b (handling null targets not represented in Millington et al. 2009)
      return prevLcs;
    } else if (prevTin < prevDeltaT) {
      // S3b
      return prevLcs;
    } else {
      // S3a
      return prevDeltaD;
    }
  }

  /**
   * <p>
   * Here we calculate the time the cell has been in its current state based on whether or not
   * either a transition has occurred or a new target state has been decided upon. See S1a--S1c and
   * S2a--S2e in thesis.
   * </p>
   *
   * <p>
   * Note that {@code thisDeltaD} is determined based on the transition target implied by the latest
   * snapshot of the cell's physical state. By contrast {@code prevDeltaD} is the transition target
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
      // S1a
      return 1;
    } else {
      if (thisDeltaD == prevDeltaD) {
        // S1b
        return prevTin + 1;
      } else {
        // S1c
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
      // S2b
      return new Integer((int) Math.round((1 + thisDeltaT) / 2.0));
    } else if (thisDeltaD != prevDeltaD && thisLcs == prevLcs && thisDeltaT != null) {
      // S2a
      return new Integer((int) Math.round((prevDeltaT + thisDeltaT) / 2.0));
    } else if (thisDeltaD == prevDeltaD && thisLcs == prevLcs) {
      // S2c
      return prevDeltaT;
    } else {
      // TODO Check if this covers both S2d /and/ S2e
      return thisDeltaT;
    }
  }

  /**
   * TODO Refactor {@link #getLcsUpdateMsg} for readability. There are multiple functions in here.
   */
  @Override
  public LcsUpdateMsg getLcsUpdateMsg(CodedEnvrAntecedent currentEnvrState,
      EnvrSimState envrSimState, CodedEnvrConsequent targetEnvrTrans) {
    // store relevant details about previous state
    Integer prevDeltaT = (targetEnvrTrans == null) ? null : targetEnvrTrans.getTransitionTime();
    Integer prevDeltaD = (targetEnvrTrans == null) ? null : targetEnvrTrans.getTargetState();
    Integer prevLcs = currentEnvrState.getStartState();

    // work out land cover state for this timestep and update record of physical state of cell
    Integer thisLcs = getThisLcs(envrSimState.getTimeInState(), prevDeltaT, prevLcs, prevDeltaD);
    CodedEnvrAntecedent nextEnvrState = updatePhysicalState(currentEnvrState, thisLcs, envrSimState);

    // target transition state based on cell's physical attributes
    CodedEnvrConsequent physAttribTrans = transMap.getEnvrConsequent(nextEnvrState);
    Integer physAttribDeltaT =
        (physAttribTrans == null) ? null : physAttribTrans.getTransitionTime();
    Integer physAttribDeltaD = (physAttribTrans == null) ? null : physAttribTrans.getTargetState();

    // get updated time in state accounting for if any land cover transitions/ trajectory changes
    // have occurred
    Integer thisTimeInState =
        getThisTimeInState(envrSimState.getTimeInState(), prevLcs, thisLcs, prevDeltaD,
            physAttribDeltaD);

    // get updated target transition time
    Integer thisDeltaT =
        getThisDeltaT(prevDeltaD, physAttribDeltaD, prevLcs, thisLcs, prevDeltaT, physAttribDeltaT);

    CodedEnvrConsequent nextTargetState;
    if (thisDeltaT == physAttribDeltaT) {
      nextTargetState = physAttribTrans; // Can be null
    } else {
      nextTargetState = new CodedEnvrConsequent(physAttribTrans.getTargetState(), thisDeltaT);
    }

    return new LcsUpdateMsg(nextEnvrState, thisTimeInState, nextTargetState);
  }

  /**
   * <p>
   * As well as changes to the land-cover state itself (Shrubland, Pine, etc), there are also rules
   * in the ecological model that control the presence of seeds in a cell (S4) and whether the cell
   * is on a secondary or regeneration succession pathway (S5). These processes are closely related
   * to the land-cover state update because:
   * </p>
   *
   * <ol>
   * <li>Rule S4 is sensitive to whether a land-cover transition has occurred in the current time
   * step, as well as the details of the specific land-cover transition undertaken.</li>
   * <li>The cell's new target state in the next time step is potentially sensitive to the seed
   * presence and succession pathway updates specified by rules S4 and S5.
   * </ol>
   *
   * <p>
   * Consequently it is not obvious how to separate the code for updating land-cover state and
   * target state, and the code for updating other aspects of the physical environment relevant for
   * ecological succession. This method encapsulates the logic for updating physical attributes
   * <emph>excluding</emph> the land-cover state itself. The updated environmental state is
   * evaluated after the new land-cover state is determined, but before the new target state is
   * assigned.
   * </p>
   * @param prevPhysicalState The physical state of the simulation cell as it was in the last time
   *        step
   * @param newLcs The numerical code of the land-cover state for the grid cell in the next time
     *        step as determined by the succession rules
   * @param envrSimState Simulation state relevant to the physical state update rules
   * @return Updated environmental state of the simulation cell after applying the environmental update rules
   *
   * @see SeedStateUpdater
   * @see SuccessionPathwayUpdater
   **/
  private CodedEnvrAntecedent updatePhysicalState(CodedEnvrAntecedent prevPhysicalState,
      Integer newLcs, EnvrSimState envrSimState) {
    SeedState prevSeedState = new SeedState(prevPhysicalState.getPineSeeds(),
        prevPhysicalState.getOakSeeds(), prevPhysicalState.getDeciduousSeeds());
    SeedState newSeedState =
        this.seedUpdater.updatedSeedState(prevSeedState, prevPhysicalState.getStartState(), newLcs);
    Integer newSuccessionPathway = this.successionUpdater.updatedSuccessionPathway(envrSimState,
        prevPhysicalState.getSuccessionPathway());
    return new CodedEnvrAntecedent(newLcs, newSuccessionPathway,
        prevPhysicalState.getAspect(), newSeedState.getPineSeeds(), newSeedState.getOakSeeds(),
        newSeedState.getDeciduousSeeds(), prevPhysicalState.getWater());
  }

}
