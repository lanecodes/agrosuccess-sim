package repast.model.agrosuccess.anthro;



public interface PopulationUpdateManager {

  /**
   * <p>
   * Takes into account the household's current population and the mass of wheat harvested in the
   * previous year to update the instance's birth and death rate variables, generate births and
   * deaths for the year based on those values, and return the new household population.
   * </p>
   *
   * @param currentPop Number of members of the household
   * @param wheatReturnsInKg Amount of wheat harvested during the year in kg
   * @return Updated household population taking into account births and deaths which occurred in
   *         the previous year
   */
  int newPopulation(int currentPop, double wheatReturnsInKg);

}
