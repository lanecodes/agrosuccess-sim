package me.ajlane.geo.repast.succession;

import java.util.Set;
import repast.model.agrosuccess.AgroSuccessCodeAliases.SeedPresence;

/**
 * Updates the presence of seeds in a grid cell in accordance with rule S4, see
 * {@link #updatedSeedState(SeedState, Integer, Integer)}.
 *
 * @author Andrew Lane
 * @see AgroSuccessLcsUpdateDecider
 */
public class SeedStateUpdater {

  private final Set<Integer> matureVegCodes;
  private final Integer noJuvenileCode = SeedPresence.False.getCode();
  private final SeedState noJuvenilesState =
      new SeedState(noJuvenileCode, noJuvenileCode, noJuvenileCode);

  /**
   * @param matureVegCodes Set of land-cover type codes specifying land-cover types considered to
   *        represent 'mature' vegetation communities.
   */
  public SeedStateUpdater(Set<Integer> matureVegCodes) {
    this.matureVegCodes = matureVegCodes;
  }

  /**
   * Accounts for rule S4 that states if a grid cell transitions to a mature land-cover type (e.g.
   * oak, pine, deciduous), all juvenile individuals in the cell are killed off.
   *
   * @param prevSeedState Grid cell's seed state prior to applying rule S4
   * @param prevLcs Code of the grid cell's land-cover state in the previous time step
   * @param newLcs Code o the grid cell's land-cover state in the current time step
   * @return New {@code SeedState} updated to reflect update rule S4
   */
  public SeedState updatedSeedState(SeedState prevSeedState, Integer prevLcs, Integer newLcs) {
    if (prevLcs == newLcs || !this.matureVegCodes.contains(newLcs)) {
      return prevSeedState;
    }
    return this.noJuvenilesState;
  }

}
