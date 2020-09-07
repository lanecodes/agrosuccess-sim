package me.ajlane.geo.repast.succession.pathway;

import java.io.Serializable;

/**
 * Stores the information about the target state resulting from a particular combination of
 * environmental conditions stored in an EnvironmentalAntecedent
 *
 * @param <T> type of the start state specifier (e.g Integer or String)
 * @author Andrew Lane
 */
public class EnvrConsequent<T1, T2> implements Serializable {
  private static final long serialVersionUID = 5284769189330757617L;
  private T1 targetState; // the land cover state this consequent represents
  private T2 transitionTime; // the number of years taken to transition to targetState

  public EnvrConsequent(T1 targetState, T2 transitionTime) {
    this.targetState = targetState;
    this.transitionTime = transitionTime;
  }

  public T1 getTargetState() {
    return targetState;
  }

  public T2 getTransitionTime() {
    return transitionTime;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((targetState == null) ? 0 : targetState.hashCode());
    result = prime * result + ((transitionTime == null) ? 0 : transitionTime.hashCode());
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
    EnvrConsequent<?,?> other = (EnvrConsequent<?,?>) obj;
    if (targetState == null) {
      if (other.targetState != null)
        return false;
    } else if (!targetState.equals(other.targetState))
      return false;
    if (transitionTime == null) {
      if (other.transitionTime != null)
        return false;
    } else if (!transitionTime.equals(other.transitionTime))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "EnvironmentalConsequent [targetState=" + targetState + ", transitionTime="
        + transitionTime + "]";
  }

}
