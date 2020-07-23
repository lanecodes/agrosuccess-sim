package me.ajlane.geo.repast.succession;

import repast.model.agrosuccess.AgroSuccessCodeAliases.Succession;

/**
 * Update succession pathway in accordance with rule S5. See
 * {@link #updatedSuccessionPathway(EnvrSimState, Integer)}.
 *
 * @author Andrew Lane
 * @see AgroSuccessLcsUpdateDecider
 */
public class SuccessionPathwayUpdater {

  private final Double oakMortalityScaling;
  private final Integer successionPathwayCode = Succession.Secondary.getCode();

  /**
   * Oak mortality scaling parameter has units year^2 / fire. It is a model parameter that controls
   * regenerative oaks' resilience to frequent fire. A larger value for oak mortality scaling
   * parameter, enables smaller fire frequencies to cause oak mortality.
   *
   * @param oakMortalityScaling Oak mortality scaling parameter
   */
  public SuccessionPathwayUpdater(Double oakMortalityScaling) {
    this.oakMortalityScaling = oakMortalityScaling;
  }

  /**
   * Implements rule S5 which states that if fire frequency is sufficiently high, grid cells
   * transition from regeneration succession pathway to secondary succession.
   *
   * @param envrSimState Information about the grid cell's state: how long it has been in its
   *        current state, the frequency of fire in the cell and the age of the mature oak
   *        vegetation in the cell
   * @param prevSuccessionPathway The cell's succession pathway in the previous time step
   * @return Updated succession pathway respecting rule S5
   */
  Integer updatedSuccessionPathway(EnvrSimState envrSimState, Integer prevSuccessionPathway) {
    Integer timeAsOakPrime = (envrSimState.getOakAge() < 100) ? envrSimState.getOakAge() : 100;
    Double fireFrequency = envrSimState.getFireFrequency();
    boolean oakMortalityOccurs = fireFrequency > timeAsOakPrime / oakMortalityScaling;
    if (oakMortalityOccurs) {
      return this.successionPathwayCode;
    }
    return prevSuccessionPathway;
  }

}

