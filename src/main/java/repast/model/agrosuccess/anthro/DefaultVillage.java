package repast.model.agrosuccess.anthro;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Logger;
import repast.simphony.space.grid.GridPoint;

/**
 * Default village implementation
 *
 * @author Andrew Lane
 *
 */
public class DefaultVillage implements Village {
  final static Logger logger = Logger.getLogger(DefaultVillage.class);

  private GridPoint location;
  private Set<Household> households = new HashSet<>();
  private List<PatchOption> sortedFarmValue, sortedWoodValue;

  private PatchEvaluator woodPlotEval, farmPlotEval;

  /**
   * @param location Point in the grid where the village is located
   * @param woodPlotEval Object used to calculate the value of land patches to households in the
   *        village for firewood gathering
   * @param farmPlotEval Object used to calculate the value of land patches to households in the
   *        village for farming
   */
  public DefaultVillage(GridPoint location, PatchEvaluator woodPlotEval,
      PatchEvaluator farmPlotEval) {
    this.location = location;
    this.woodPlotEval = woodPlotEval;
    this.farmPlotEval = farmPlotEval;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Set<Household> getHouseholds() {
    return this.households;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addHousehold(Household hhold) {
    this.households.add(hhold);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void appraisePatches(Set<PatchOption> allPatches) {
    Comparator<PatchOption> farmComparator = new PatchComparator(this.farmPlotEval);
    this.sortedFarmValue = patchOptionsAsSortedList(allPatches, farmComparator);
    logger.debug("Finished sorting patches by farm value");

    // Comparator<PatchOption> woodComparator = new PatchComparator(this.woodPlotEval);
    this.sortedWoodValue = new ArrayList<>(allPatches); // DUMMY, wood value not used TODO remove
    // this.sortedWoodValue = patchOptionsAsSortedList(allPatches, woodComparator);
  }

  /**
   * Convert an unsorted collection of {@code PatchOption} objects according to a rule specified by
   * {@code comp}. Follows the pattern described
   * <a href="https://stackoverflow.com/questions/740299">here</a>.
   *
   * @param col Collection of {@code PatchOption}-s
   * @param comp Specifies the rule by which the patches are sorted
   * @return List holding the sorted version of the collection
   */
  private static List<PatchOption> patchOptionsAsSortedList(Collection<PatchOption> col,
      Comparator<PatchOption> comp) {
    List<PatchOption> list = new ArrayList<PatchOption>(col);
    java.util.Collections.sort(list, comp);
    return list;
  }

  private class PatchComparator implements Comparator<PatchOption> {
    PatchEvaluator evaluator;

    PatchComparator(PatchEvaluator evaluator) {
      this.evaluator = evaluator;
    }

    /**
     * Place the first argument before the second argument iff {@code PatchEvaluator#getValue}
     * yields a larger value for the first argument than for the second
     */
    @Override
    public int compare(PatchOption o1, PatchOption o2) {
      if (o1.equals(o2)) {
        return 0;
      }

      double o1Value = this.evaluator.getValue(o1, location);
      double o2Value = this.evaluator.getValue(o2, location);

      if (o1Value > o2Value) {
        // Higher value -> appears first in sorted list
        return -1;
      }

      return 1;
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<PatchOption> getOrderedWheatPatches() {
    return this.sortedFarmValue;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<PatchOption> getOrderedWoodPatches() {
    return this.sortedWoodValue;
  }

}
