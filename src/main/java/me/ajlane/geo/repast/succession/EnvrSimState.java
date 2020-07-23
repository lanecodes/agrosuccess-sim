package me.ajlane.geo.repast.succession;

/**
 * Endogenous quantities which arise during simulation runs used in the ecological succession model.
 *
 * @author Andrew Lane
 *
 */
public class EnvrSimState {

  private Integer timeInState;
  private Double fireFrequency;
  private Integer oakAge;

  /**
   * @param timeInState Number of years the cell has been in its current land-cover state
   * @param fireFrequency Number of fires per year in the cell during the simulation
   * @param oakAge Number of years the grid cell has contained mature oak vegetation
   */
  public EnvrSimState(Integer timeInState, Double fireFrequency, Integer oakAge) {
    this.timeInState = timeInState;
    this.fireFrequency = fireFrequency;
    this.oakAge = oakAge;
  }

  /**
   * @return Number of years the cell has been in its current land-cover state
   */
  public Integer getTimeInState() {
    return timeInState;
  }

  /**
   * @return Number of fires per year in the cell during the simulation
   */
  public Double getFireFrequency() {
    return fireFrequency;
  }

  /**
   * @return The number of years the cell has contained mature oak vegetation. {@code null} if the
   *         cell does not contain mature oak vegetation
   */
  public Integer getOakAge() {
    return oakAge;
  }

  @Override
  public String toString() {
    return "EnvrSimState [timeInState=" + timeInState + ", fireFrequency=" + fireFrequency
        + ", oakAge=" + oakAge + "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((fireFrequency == null) ? 0 : fireFrequency.hashCode());
    result = prime * result + ((oakAge == null) ? 0 : oakAge.hashCode());
    result = prime * result + ((timeInState == null) ? 0 : timeInState.hashCode());
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
    EnvrSimState other = (EnvrSimState) obj;
    if (fireFrequency == null) {
      if (other.fireFrequency != null)
        return false;
    } else if (!fireFrequency.equals(other.fireFrequency))
      return false;
    if (oakAge == null) {
      if (other.oakAge != null)
        return false;
    } else if (!oakAge.equals(other.oakAge))
      return false;
    if (timeInState == null) {
      if (other.timeInState != null)
        return false;
    } else if (!timeInState.equals(other.timeInState))
      return false;
    return true;
  }

}
