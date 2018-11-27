package me.ajlane.geo.repast.succession;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple map between sets of possible environmental conditions, and the succession trajectories
 * resulting from those sets of conditions.
 * 
 * @author Andrew Lane
 */
public class AliasedLcsTransitionMap {
  private Map<AliasedEnvrAntecedent, AliasedEnvrConsequent> transMap;

  AliasedLcsTransitionMap() {
    this.transMap = new HashMap<>();
  }

  AliasedEnvrConsequent getEnvrConsequent(AliasedEnvrAntecedent ante) {
    return transMap.get(ante);
  }

  void put(AliasedEnvrAntecedent ante, AliasedEnvrConsequent cons) {
    this.transMap.put(ante, cons);
  }
  
  int size() {
    return this.transMap.size();
  }

}
