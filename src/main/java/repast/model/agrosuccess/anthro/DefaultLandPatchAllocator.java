package repast.model.agrosuccess.anthro;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Logger;
import repast.simphony.random.RandomHelper;
import repast.simphony.util.SimUtilities;

/**
 * <ul>
 * <li>LandPatchAllocator is provided with a set of villages in the simulation in its
 * constructor</li>
 * <li>In each time step the LandPatchAllocator:
 * <ol>
 * <li>Generates a list of all available land-cover patches
 * <li>
 * <li>Selects a random ordering of the villages</li>
 * <li>For each village:
 * <ol>
 * <li>Ask it to appraise wood and wheat patches</li>
 * <li>Get a list of households</li>
 * <li>For each household:
 * <ol>
 * <li>Offer it a list of all currently available patches and ask it to choose one</li>
 * <li>Remove the selected patch from available patches</li>
 * <li>Ask household if its subsistence plan if satisfied. If it is, remove it from the list of
 * households</li>
 * </ol>
 * <li>If there are still households in the list of households, run through the list of households
 * again</li>
 * </ol>
 * </ol>
 * </ul>
 *
 * TODO implement DefaultLandPatchAllocator
 *
 * @author Andrew Lane
 */
public class DefaultLandPatchAllocator implements LandPatchAllocator {
  final static Logger logger = Logger.getLogger(DefaultLandPatchAllocator.class);

  Collection<Village> villages;
  PatchOptionGenerator patchOptionGenerator;

  public DefaultLandPatchAllocator(Collection<Village> villages,
      PatchOptionGenerator patchOptionGenerator) {
    this.villages = villages;
    this.patchOptionGenerator = patchOptionGenerator;
  }

  @Override
  public void allocatePatches() {
    Set<PatchOption> availablePatches = this.patchOptionGenerator.generateAllPatchOptions();
    List<Village> orderedVillages = getRandomOrdering(this.villages);
    for (Village village : orderedVillages) {
      logger.debug("Starting to allocate to " + village);
      village.appraisePatches(availablePatches);
      logger.debug("Finished appraising patches");
      List<Household> orderedHouseholds = getRandomOrdering(village.getHouseholds());
      int numHouseholdsToAllocate = orderedHouseholds.size();
      while (numHouseholdsToAllocate > 0) {
        logger.debug(numHouseholdsToAllocate + " households left to allocate");
        List<Household> completedHouseholds = new ArrayList<>();
        for (Household household : orderedHouseholds) {
          if (household.subsistencePlanIsSatisfied()) {
            logger.debug(household.toString() + " subsistence plan satisfied");
            completedHouseholds.add(household);
          } else {
            PatchOption selectedPatch = household.claimPatch(availablePatches);
            availablePatches.remove(selectedPatch);
          }
        }
        orderedHouseholds.removeAll(completedHouseholds);
        numHouseholdsToAllocate -= completedHouseholds.size();
      }
    }

  }

  /**
   * Select a random ordering of a collection (villages or households) so that first pick of
   * land-cover patches changes from year to year
   *
   * @param <T> Type of objects in the collection, e.g {@code Household} or {@code Village}.
   *
   * @param collectionToShuffle
   * @return Shuffled copy of selection
   */
  private <T> List<T> getRandomOrdering(Collection<T> collectionToShuffle) {
    List<T> list = new ArrayList<>(collectionToShuffle);
    // Collections.copy(list, collectionToShuffle.stream().collect(Collectors.toList()));
    SimUtilities.shuffle(list, RandomHelper.createUniform());
    return list;
  }

}
