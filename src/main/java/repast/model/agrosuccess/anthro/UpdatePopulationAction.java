package repast.model.agrosuccess.anthro;

import repast.simphony.engine.schedule.IAction;

/**
 * <p>
 * Repast Simphony {@code IAction} which the scheduler can interact with specifying how Household
 * agents should update their populations having calculated their farming returns.
 * <p>
 * <h4>TODO</h4>
 * <ul>
 * <li>Implement an extra constructor that allows us to specify a list of precipitation values to
 * enable us to explore the effect of time varying precipitation. See
 * {@link me.ajlane.geo.repast.soilmoisture.agrosuccess.SoilMoistureUpdateAction} for an example of
 * how this could be done.</li>
 * </ul>
 *
 * @author Andrew Lane
 */
public class UpdatePopulationAction implements IAction {
  private Household household;
  private double precipitationMm;

  /**
   * @param household The household to schedule the population update action on.
   * @param precipitationMm The constant total annual precipitation to supply in each simulated
   *        year.
   */
  public UpdatePopulationAction(Household household, double precipitationMm) {
    this.household = household;
    this.precipitationMm = precipitationMm;
  }

  @Override
  public void execute() {
    this.household.updatePopulation(precipitationMm);
  }

}
