package repast.model.agrosuccess.empirical;

import java.util.Map;
import me.ajlane.geo.Direction;
import me.ajlane.geo.repast.fire.WindSpeed;

public interface SiteWindData {

  /**
   * @return Map of {@link me.ajlane.geo.repast.fire.WindSpeed WindSpeed} enumeration constants to
   *         the probability of observing the wind speed corresponding to each member at the study
   *         site.
   */
  Map<WindSpeed, Double> getWindSpeedProb();

  /**
   * @return Map of {@link me.ajlane.geo.Direction WindDirection} enumeration
   *         constants to the probability of observing the wind direction corresponding to each
   *         member at the study site.
   */
  Map<Direction, Double> getWindDirectionProb();

}
