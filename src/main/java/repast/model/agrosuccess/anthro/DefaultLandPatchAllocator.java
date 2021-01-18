package repast.model.agrosuccess.anthro;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import repast.simphony.random.RandomHelper;
import repast.simphony.util.SimUtilities;

/**
 * <ul>
 * <li>LandPatchAllocator is provided with a set of villages in the simulation in its
 * constructor</li>
 * <li>In each time step the LandPatchAllocator:
 *   <ol>
 *      <li>Generates a list of all available land-cover patches<li>
 *      <li>Selects a random ordering of the villages</li>
 *      <li>For each village:
 *        <ol>
 *          <li>Ask it to appraise wood and wheat patches</li>
 *          <li>Get a list of households</li>
 *          <li>For each household:
 *            <ol>
 *              <li>Offer it a list of all currently available patches and ask it to choose one</li>
 *              <li>Remove the selected patch from available patches</li>
 *              <li>Ask household if its subsistence plan if satisfied. If it is, remove it from the list of households</li>
 *            </ol>
 *          <li>If there are still households in the list of households, run through the list of households again</li>
 *        </ol>
 *    </ol>
 * </ul>
 *
 * TODO implement DefaultLandPatchAllocator
 *
 * @author Andrew Lane
 */
public class DefaultLandPatchAllocator implements LandPatchAllocator {

  Collection<Village> villages;

  public DefaultLandPatchAllocator(Collection<Village> villages) {
    this.villages = villages;
  }

  @Override
  public void allocatePatches() {
    // TODO Auto-generated method stub
    List<Village> orderedVillages = getRandomVillageOrdering(this.villages);

  }

  /**
   * Select a random ordering of villages so that first pick of land-cover patches changes from year
   * to year
   *
   * @param villages
   * @return
   */
  private List<Village> getRandomVillageOrdering(Collection<Village> villages) {
    List<Village> villageList = new ArrayList<>();
    Collections.copy(villageList, (List<Village>) villages);
    SimUtilities.shuffle(villageList, RandomHelper.createUniform());
    return villageList;
  }

}
