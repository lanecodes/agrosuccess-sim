package me.ajlane.geo.repast.succession;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple map between sets of possible environmental conditions, and the succession trajectories
 * resulting from those sets of conditions.
 * 
 * @author Andrew Lane
 */
public class CodedLcsTransitionMap {
  private Map<CodedEnvrAntecedent, CodedEnvrConsequent> transMap;

  CodedLcsTransitionMap() {
    this.transMap = new HashMap<>();
  }

  CodedEnvrConsequent getEnvrConsequent(CodedEnvrAntecedent ante) {
    return transMap.get(ante);
  }

  void put(CodedEnvrAntecedent ante, CodedEnvrConsequent cons) {
    this.transMap.put(ante, cons);
  }
  
  int size() {
    return this.transMap.size();
  }

}
