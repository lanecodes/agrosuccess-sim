package repast.model.agrosuccess.anthro;

import java.util.List;
import java.util.Set;

/**
 * The location of a collection of households.
 *
 * @author Andrew Lane
 *
 */
public interface Village {

  /**
   * @return The set of all the households in the village
   */
  public Set<Household> getHouseholds();

  /**
   * Add a household agent to the village
   *
   * @param hhold Household to add
   */
  public void addHousehold(Household hhold);

  /**
   * Village comes together and decides on the relative value of all the patches in the grid for
   * wheat farming or wood gathering, in consideration of distance to village, slope of the patches,
   * soil fertility, wood value, and land cover conversion cost.
   *
   * Patches need to be re-appraised in each time step to account for the fact that land cover types
   * will change over time. All households in the same village value farming and firewood patches
   * the same as each other within each time step. Different villages will evaluate patches
   * differently because of the differing distance from the village to each of the other cells.
   *
   * @param allPatches Set characterising all patches in the grid in terms of variables relevant for
   *        appraising their suitability for agriculture or wood gathering.
   */
  public void appraisePatches(Set<PatchOption> allPatches);

  /**
   * @return List of land cover patches sorted in order of decreasing value for wheat farming to
   *         households in the village (first item is most valuable patch).
   */
  public List<PatchOption> getOrderedWheatPatches();

  /**
   * @return List of land cover patches sorted in order of decreasing value for wood gathering to
   *         households in the village (first item is most valuable patch).
   */
  public List<PatchOption> getOrderedWoodPatches();

}
