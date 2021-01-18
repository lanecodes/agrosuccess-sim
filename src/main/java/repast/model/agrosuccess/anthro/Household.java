package repast.model.agrosuccess.anthro;

import java.util.Set;

/**
 * Agent representing an agricultural household.
 *
 * @author Andrew Lane
 *
 */
public interface Household {

  /**
   * @param availablePatches Set of patches currently available for households to claim
   * @return Patch selected by household
   */
  public PatchOption claimPatch(Set<PatchOption> availablePatches);

  /**
   * @return {@code true} if household has selected enough land patches to satisfy its subsistence
   *         plan
   */
  boolean subsPlanSatisfied();

  /**
   * Called at the end of the simulated year to cause the household to evaluate its performance in
   * accumulating sufficient calories. Its success or failure in this respect causes updates to the
   * household population.
   */
  public void updatePopulation();

  /**
   * Relinquish control of wood and wheat patches currently under the household's control, ready for
   * the next simulated year.
   */
  void releasePatches();

  /**
   * @return Unique identifier for household
   */
  public long getId();

}
