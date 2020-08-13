package repast.model.agrosuccess.params;

import me.ajlane.geo.repast.colonisation.csr.CompletelySpatiallyRandomParams;
import me.ajlane.geo.repast.fire.FireParams;
import me.ajlane.geo.repast.soilmoisture.SoilMoistureParams;

/**
 * @author Andrew Lane
 *
 */
public interface EnvrModelParams {

  /**
   * @return Parameters needed for the land-cover colonisation model
   */
  public CompletelySpatiallyRandomParams getLandCoverColoniserParams();

  /**
   * @return Parameters needed to determine how to discretise continuous soil moisture values into
   *         discrete states.
   */
  public SoilMoistureParams getSoilMoistureParams();

  /**
   * @return Parameters needed to run the fire ignition and spread model.
   */
  public FireParams getFireParams();

}
