package repast.model.agrosuccess.anthro;

/**
 * Dummy population update manager that always returns the same population size. Used to approximate
 * constant household populations.
 *
 * @author Andrew Lane
 */
public class StaticPopulationUpdateManager implements PopulationUpdateManager {

  @Override
  public int newPopulation(int currentPop, double wheatReturnsInKg) {
    return currentPop;
  }

}
