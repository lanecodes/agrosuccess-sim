package me.ajlane.geo.repast.succession.pathway.aliased;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Simple map between sets of possible environmental conditions, and the succession trajectories
 * resulting from those sets of conditions.
 *
 * @author Andrew Lane
 */
public class AliasedLcsTransitionMap {
  private Map<AliasedEnvrAntecedent, AliasedEnvrConsequent> transMap;

  public AliasedLcsTransitionMap() {
    this.transMap = new HashMap<>();
  }

  public AliasedEnvrConsequent getEnvrConsequent(AliasedEnvrAntecedent ante) {
    return transMap.get(ante);
  }

  public void put(AliasedEnvrAntecedent ante, AliasedEnvrConsequent cons) {
    this.transMap.put(ante, cons);
  }

  public int size() {
    return this.transMap.size();
  }

  public Set<Map.Entry<AliasedEnvrAntecedent, AliasedEnvrConsequent>> entrySet() {
    return this.transMap.entrySet();
  }

}
