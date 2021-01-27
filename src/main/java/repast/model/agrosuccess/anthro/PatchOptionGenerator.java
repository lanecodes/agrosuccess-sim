package repast.model.agrosuccess.anthro;

import java.util.Set;

/**
 * Generates a set of all land-cover patches available for farming or wood gathering in the
 * landscape.
 *
 * @author Andrew Lane
 */
public interface PatchOptionGenerator {

  /**
   * @return Generate a set of all land-cover patches available for farming or wood gathering in the
   *         landscape. Expected to be called every time step as the state of grid cells and thier
   *         associated value for farming will change during the course of the simulation run.
   */
  public Set<PatchOption> generateAllPatchOptions();
}
