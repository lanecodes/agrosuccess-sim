package me.ajlane.geo.repast.soilmoisture.agrosuccess;

import org.apache.log4j.Logger;
import me.ajlane.geo.repast.soilmoisture.SoilMoistureUpdater;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.IAction;
import repast.simphony.engine.schedule.ISchedule;

/**
 * Repast Simphony {@code IAction} which the scheduler can interact with specifying what to do in
 * each time step to update soil moisture in the model. Accounts for both situations where
 * precipitation is constant throughout the simulation, and where precipitation varies from time
 * step to time step.
 *
 * To enable (potentially) variable precipitation, this class follows the example given
 * <a href="https://sourceforge.net/p/repast/mailman/message/36056305/">here</a> on the Repast
 * Simphony mailing list.
 *
 * @author Andrew Lane
 */
public class SoilMoistureUpdateAction implements IAction {

  final static Logger logger = Logger.getLogger(SoilMoistureUpdateAction.class);
  private SoilMoistureUpdater smUpdater;
  private double constantAnnualPrecip;
  private double[] variableAnnualPrecip = null;
  private final ISchedule sche = RunEnvironment.getInstance().getCurrentSchedule();

  /**
   * @param smUpdater Soil moisture updater which maintains a reference to the state of the model's
   *        soil moisture layer and knows how to update it in response to precipitation
   * @param annualPrecip Annual precipitation in mm. This constant amount of precipitation will be
   *        applied in every time step
   */
  public SoilMoistureUpdateAction(SoilMoistureUpdater smUpdater, double annualPrecip) {
    this.smUpdater = smUpdater;
    this.constantAnnualPrecip = annualPrecip;
  }

  /**
   * @param smUpdater Soil moisture updater which maintains a reference to the state of the model's
   *        soil moisture layer and knows how to update it in response to precipitation
   * @param annualPrecip Annual precipitation in mm. The ith element of the array is the
   *        precipitation in the ith time step. If the siulation runs for more time steps than there are
   *        elements in the array, a {@code ArrayIndexOutOfBoundsException} will occur
   */
  public SoilMoistureUpdateAction(SoilMoistureUpdater smUpdater, double[] annualPrecip) {
    this.smUpdater = smUpdater;
    this.variableAnnualPrecip = annualPrecip;
  }

  @Override
  public void execute() {
    if (annualPrecipIsVariable()) {
      int tick = (int) sche.getTickCount();
      try {
        this.smUpdater.updateSoilMoisture(variableAnnualPrecip[tick - 1]);
      } catch (ArrayIndexOutOfBoundsException e) {
        logger.error("Simulation tick has exceeded number of years for which precipitation "
            + "is specified", e);
        throw e;
      }
    } else {
      this.smUpdater.updateSoilMoisture(constantAnnualPrecip);
    }
  }

  private boolean annualPrecipIsVariable() {
    if (this.variableAnnualPrecip != null) {
      return true;
    }
    return false;
  }

}
