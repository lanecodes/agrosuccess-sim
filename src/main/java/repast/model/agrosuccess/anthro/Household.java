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
   * @return Unique identifier for household
   */
  public int getId();

}
