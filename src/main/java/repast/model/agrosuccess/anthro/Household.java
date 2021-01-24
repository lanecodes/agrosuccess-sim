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
   * Calculate the number of wheat patches the household should aim to farm this year. Takes the
   * household's population and the wheat yield (mass of wheat per ha) in the previous simulated
   * year.
   */
  public void calcSubsistencePlan();


  /**
   * @return {@code true} if household has selected enough land patches to satisfy its subsistence
   *         plan
   */
  boolean subsistencePlanIsSatisfied();


  /**
   * Called at the end of the simulated year to cause the household to evaluate its performance in
   * accumulating sufficient calories. Its success or failure in this respect causes updates to the
   * household population.
   *
   * @param precipitationMm Total precipitation in mm in the simulated year.
   */
  public void updatePopulation(double precipitationMm);

  /**
   * Relinquish control of wood and wheat patches currently under the household's control, ready for
   * the next simulated year.
   */
  public void releasePatches();

  /**
   * @return Unique identifier for household
   */
  public long getId();

}
