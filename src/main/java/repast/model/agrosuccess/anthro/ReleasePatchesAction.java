package repast.model.agrosuccess.anthro;

import repast.simphony.engine.schedule.IAction;

/**
 * Repast Simphony {@code IAction} which the scheduler can interact with specifying how Household
 * agents should release land patches after they have farmed them, ready for the next simulated year.
 *
 * @author Andrew Lane
 */
public class ReleasePatchesAction implements IAction {
  private Household household;

  /**
   * @param household The household to schedule the patch release action on.
   */
  public ReleasePatchesAction(Household household) {
    this.household = household;
  }

  @Override
  public void execute() {
    this.household.releasePatches();
  }

}
