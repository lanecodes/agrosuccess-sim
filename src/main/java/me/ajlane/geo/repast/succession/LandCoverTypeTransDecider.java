package me.ajlane.geo.repast.succession;

import me.ajlane.geo.repast.soilmoisture.SoilMoistureDiscretiser;
import me.ajlane.neo4j.EmbeddedGraphInstance;


public class LandCoverTypeTransDecider {

  private CodedLcsTransitionMapOld transLookup;

  /**
   * @param graphDatabase Running connection to a graph database containing the model configuration
   * @param envStateAliasTranslator Model specific translator to go between aliases specifying
   *        combinations of environmental condition
   * @param modelID
   */
  public LandCoverTypeTransDecider(EmbeddedGraphInstance graphDatabase,
      EnvrStateAliasTranslator envStateAliasTranslator, 
      SoilMoistureDiscretiser smDiscretiser, String modelID) {

    this.transLookup = 
        new CodedLcsTransitionMapOld(graphDatabase, envStateAliasTranslator, modelID);

  } 

  /**
   * Classify soil moisture measured in mm to one of xeric, mesic or hydric using the schema
   * described in Millington et al. 2009. That is: - xeric <=500 mm - 500 mm< mesic <=1000 mm -
   * hydric >1000 mm
   * 
   * @param soilMoisture Cell soil moisture specified in mm
   * @return 0=xeric, 1=mesic, 2=hydric
   */
  int discretiseSoilMoisture(double soilMoisture) {
    if (soilMoisture <= 500) {
      return 0;
    } else if (soilMoisture <= 1000) {
      return 1;
    }
    return 2;
  }

  /**
   * @param currentLandCoverState Package of data specifying a cell's current land cover state and
   *        information needed to specify its trajectory
   * @param successionPathway Regeneration or secondary succession. regeneration=0, secondary=1
   * @param aspect north=0, south=1
   * @param pineSeeds Are pine seeds present? false=0, true=1
   * @param oakSeeds Are oak seeds present? false=0, true=1
   * @param deciduousSeeds Are deciduous seeds present? false=0, true=1
   * @param soilMoisture Soil moisture of cell in mm
   * @return Package of data specifying cell's transition state in the next timestep in
   *         consideration of its environmental state
   */
  public OldLandCoverStateTransitionMessage nextLandCoverTransitionState(
      OldLandCoverStateTransitionMessage lastLandCoverState, int successionPathway, int aspect,
      int pineSeeds, int oakSeeds, int deciduousSeeds, double soilMoisture) {

    int thisCurrentState, thisTimeInState, thisTargetState, thisTargetStateTransitionTime = -1;

    EnvrAntecedent<Integer, Integer, Integer, Integer, Integer, Integer, Integer> lastEnvConds =
        new EnvrAntecedent<Integer, Integer, Integer, Integer, Integer, Integer, Integer>(
            lastLandCoverState.getCurrentState(), successionPathway, aspect, pineSeeds, oakSeeds,
            deciduousSeeds, discretiseSoilMoisture(soilMoisture));

    // current environmental trajectory given combination of environmental conditions last read from
    // model
    EnvrConsequent<Integer> currentTargetState = transLookup.get(lastEnvConds);

    thisTimeInState = lastLandCoverState.getTimeInState() + 1;

    if (currentTargetState.getTargetState() == lastLandCoverState.getTargetState()) {
      // If environmental changes haven't caused change in target state, target transition time
      // stays the same
      thisTargetStateTransitionTime = lastLandCoverState.getTargetStateTransitionTime();
    } else {
      // If target state has changed due to changes in the environment, average target time WRT new
      // and old states
      thisTargetStateTransitionTime =
          (int) Math.round(0.5 * (double) (lastLandCoverState.getTargetStateTransitionTime()
              + currentTargetState.getTransitionTime()));

      if (thisTimeInState < thisTargetStateTransitionTime) {
        thisTimeInState = 1; // see Millington2009 statement 3, reset time in state count when
                             // target changes
      }

    }

    if (thisTimeInState >= thisTargetStateTransitionTime) {
      // Transition!
      thisCurrentState = currentTargetState.getTargetState();
      thisTimeInState = 1;

      // lookup the target state which will result from the change in actual land cover state
      EnvrAntecedent<Integer, Integer, Integer, Integer, Integer, Integer, Integer> thisEnvConds =
          new EnvrAntecedent<Integer, Integer, Integer, Integer, Integer, Integer, Integer>(
              thisCurrentState, successionPathway, aspect, pineSeeds, oakSeeds, deciduousSeeds,
              discretiseSoilMoisture(soilMoisture));

      EnvrConsequent<Integer> nextTargetState = transLookup.get(thisEnvConds);

      if (nextTargetState == null) {
        // handle case for when we implicitly stay in the same state because there is no rule to
        // leave
        // TODO check this rule thoroughly
        thisTargetState = thisCurrentState;
        thisTargetStateTransitionTime = 1;
      } else {
        thisTargetState = nextTargetState.getTargetState();
        thisTargetStateTransitionTime = nextTargetState.getTransitionTime();
      }

    } else {
      thisCurrentState = lastLandCoverState.getCurrentState();
      thisTargetState = currentTargetState.getTargetState();
    }

    if (thisCurrentState == -1 || thisTimeInState == -1 || thisTargetState == -1
        || thisTargetStateTransitionTime == -1) {
      throw new IllegalArgumentException("LandCoverStateTransitionMessage not initialised.");
    }

    return new OldLandCoverStateTransitionMessage(thisCurrentState, thisTimeInState, thisTargetState,
        thisTargetStateTransitionTime);
  }

  /*
   * private static HashMap<String, String> getLandCoverTransitionData( Transaction tx, String
   * model_ID ) { HashMap<String, String> map = new HashMap<String, String>(); StatementResult
   * result = tx.run(
   * "MATCH (lct1:LandCoverType)<-[:SOURCE]-(t:SuccessionTrajectory)-[:TARGET]->(lct2:LandCoverType) "
   * + "WHERE lct1.model_ID=$model_ID AND lct1.code<>lct2.code " +
   * "WITH lct1, lct2, t MATCH (e:EnvironCondition)-[:CAUSES]->(t) " +
   * "RETURN lct1.code as start_code, e.succession as succession, e.aspect as aspect," +
   * "e.pine as pine, e.oak as oak, e.deciduous as deciduous, e.water as water, " +
   * "lct2.code as end_code, e.delta_t as delta_t; ", parameters( "model_ID", model_ID ) );
   * 
   * // Loop through resulting combinations of environmental conditions, adding a k/v pair // to
   * `map` where the keys are concatenated string of conditions (specified in the order // of
   * start_code, succession, aspect, pine, oak, deciduous, water, delta_t) and the // values are the
   * code for the end state.
   * 
   * while ( result.hasNext() ) { Record thisResult = result.next(); // construct key string by
   * concatenating individual environmental conditions String key =
   * thisResult.get("start_code").toString().replaceAll("^\"|\"$", "") +
   * thisResult.get("succession").toString().replaceAll("^\"|\"$", "") +
   * thisResult.get("aspect").toString().replaceAll("^\"|\"$", "") +
   * String.valueOf(thisResult.get("pine")) + String.valueOf(thisResult.get("oak")) +
   * String.valueOf(thisResult.get("deciduous")) +
   * thisResult.get("water").toString().replaceAll("^\"|\"$", "") +
   * thisResult.get("delta_t").toString().replaceAll("^\"|\"$", ""); // map conditions to resulting
   * land cover type map.put(key, thisResult.get("end_code").toString().replaceAll("^\"|\"$", ""));
   * 
   * } return map; }
   * 
   * public String getNextLandCoverType(String startState, String succession, String aspect, Boolean
   * pine, Boolean oak, Boolean deciduous, String water, String delta_t) { String key = startState +
   * succession + aspect + String.valueOf(pine).toUpperCase() + String.valueOf(oak).toUpperCase() +
   * String.valueOf(deciduous).toUpperCase() + water + delta_t;
   * 
   * return this.transLookup.get(key); }
   * 
   * public static void main( String... args ) throws Exception { String user = args[0]; String
   * password = args[1]; try ( LandCoverTypeTransDecider decider = new LandCoverTypeTransDecider(
   * "bolt://localhost:7687", user, password, "AgroSuccess-dev" ) ) {
   * System.out.println(decider.getLandCoverTransitionMap());
   * System.out.println(decider.getNextLandCoverType("Burnt", "regeneration", "north", false, true,
   * true, "xeric", "2"));
   * 
   * // how to handle cases where environmental conditions change part way through transition? Refer
   * // back to Millington2009. Maybe this is an argument for passing the whole SimulationCell
   * object to // getNextLandCoverType? } }
   * 
   */
}
