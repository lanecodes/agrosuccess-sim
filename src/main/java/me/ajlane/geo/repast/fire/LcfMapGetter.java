package me.ajlane.geo.repast.fire;

import java.util.Map;
import repast.model.agrosuccess.AgroSuccessCodeAliases.Lct;

/**
 * Generates land-cover flammability data. See Table 6 in
 * <a href="https://doi.org/10.1016/j.envsoft.2009.03.013">Millinton et al. 2009</a>.
 *
 * @author Andrew Lane
 *
 */
public interface LcfMapGetter {
  Map<Lct, Double> getMap();
}
