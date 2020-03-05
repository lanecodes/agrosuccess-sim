package repast.model.agrosuccess.anthro;

import repast.simphony.space.grid.GridPoint;

/**
 * Quantifies the suitability of land cover patches for some subsistence application.
 *
 * @author Andrew Lane
 *
 */
public interface PatchEvaluator {

  /**
   * @param targetPatch The target patch for which we want to calculate the value
   * @param villageLocation The location of the village which is evaluating value of the
   *        patch to its households
   * @return Numerical value of the {@code targetPatch}
   */
  public double getValue(PatchOption targetPatch, GridPoint villageLocation);

}
