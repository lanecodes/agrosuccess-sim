package repast.model.agrosuccess.anthro;

/**
 * Iterates over villages and households, allocating land patches to them to satisfy households
 * subsistence needs.
 *
 * @author Andrew Lane
 *
 */
public interface LandPatchAllocator {

  /**
   * Assign land cover patches to households.
   */
  public void allocatePatches();

}
