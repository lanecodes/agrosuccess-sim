package repast.model.agrosuccess.params;

import me.ajlane.geo.repast.colonisation.randomkernel.SeedDispersalParams;
import me.ajlane.geo.repast.colonisation.randomkernel.SeedViabilityParams;
import me.ajlane.geo.repast.fire.FireParams;
import me.ajlane.geo.repast.soilmoisture.SoilMoistureParams;

/**
 * @author Andrew Lane
 *
 */
public interface EnvrModelParams {

  /**
   * @return Parameters specifying the probability distributions which control how seeds are
   *         dispersed in the model.
   */
  public SeedDispersalParams getSeedDispersalParams();

  /**
   * @return Parameters specifying how long different types of seeds survive in the seed bank after
   *         being deposited.
   */
  public SeedViabilityParams getSeedViabilityParams();

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
