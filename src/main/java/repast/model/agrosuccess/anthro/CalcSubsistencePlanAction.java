package repast.model.agrosuccess.anthro;

import repast.simphony.engine.schedule.IAction;

/**
 * Repast Simphony {@code IAction} which the scheduler can interact with specifying how Household
 * agents should calculate their subsistence plans.
 *
 * @author Andrew Lane
 */
public class CalcSubsistencePlanAction implements IAction {
  private Household household;

  /**
   * @param household The household to schedule the subsistence plan calculation action on.
   */
  public CalcSubsistencePlanAction(Household household) {
    this.household = household;
  }

  @Override
  public void execute() {
    this.household.calcSubsistencePlan();
  }

}
