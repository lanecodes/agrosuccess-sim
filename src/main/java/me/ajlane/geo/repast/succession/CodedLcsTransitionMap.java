package me.ajlane.geo.repast.succession;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Simple map between sets of possible environmental conditions, and the succession trajectories
 * resulting from those sets of conditions.
 * 
 * @author Andrew Lane
 */
public class CodedLcsTransitionMap {
  private Map<CodedEnvrAntecedent, CodedEnvrConsequent> transMap;

  public CodedLcsTransitionMap() {
    this.transMap = new HashMap<>();
  }

  CodedEnvrConsequent getEnvrConsequent(CodedEnvrAntecedent ante) {
    return transMap.get(ante);
  }

  public void put(CodedEnvrAntecedent ante, CodedEnvrConsequent cons) {
    this.transMap.put(ante, cons);
  }
  
  public int size() {
    return this.transMap.size();
  }
  
  public Set<Map.Entry<CodedEnvrAntecedent, CodedEnvrConsequent>> entrySet() {
    return this.transMap.entrySet();
  }

}
