package me.ajlane.geo.repast.succession.pathway.aliased;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Simple map between sets of possible environmental conditions, and the succession trajectories
 * resulting from those sets of conditions.
 *
 * @author Andrew Lane
 */
public class AliasedLcsTransitionMap implements Serializable {
  private static final long serialVersionUID = 1762418779199930268L;
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

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((transMap == null) ? 0 : transMap.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    AliasedLcsTransitionMap other = (AliasedLcsTransitionMap) obj;
    if (transMap == null) {
      if (other.transMap != null)
        return false;
    } else if (!transMap.equals(other.transMap))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "AliasedLcsTransitionMap [transMap=" + transMap + "]";
  }

}
