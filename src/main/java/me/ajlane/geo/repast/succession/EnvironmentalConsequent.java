package me.ajlane.geo.repast.succession;

/**
 * Stores the information about the target state resulting from a particular combination of
 * environmental conditions stored in an EnvironmentalAntecedent
 *
 * @param <T> type of the start state specifier (e.g Integer or String)
 * @author Andrew Lane
 */
class EnvironmentalConsequent<T> {

  private T targetState; // the land cover state this consequent represents
  private int transitionTime; // the number of years taken to transition to targetState

  public EnvironmentalConsequent(T targetState, int transitionTime) {
    this.targetState = targetState;
    this.transitionTime = transitionTime;
  }

  public T getTargetState() {
    return targetState;
  }

  public int getTransitionTime() {
    return transitionTime;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((targetState == null) ? 0 : targetState.hashCode());
    result = prime * result + transitionTime;
    return result;
  }

  @SuppressWarnings("unchecked")
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    EnvironmentalConsequent<T> other = (EnvironmentalConsequent<T>) obj;
    if (targetState == null) {
      if (other.targetState != null)
        return false;
    } else if (!targetState.equals(other.targetState))
      return false;
    if (transitionTime != other.transitionTime)
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "EnvironmentalConsequent [targetState=" + targetState + ", transitionTime="
        + transitionTime + "]";
  }

}
