/**
 *
 */
package me.ajlane.geo.repast.succession.pathway.convert;

import org.apache.commons.collections15.BidiMap;
import org.apache.commons.collections15.bidimap.DualHashBidiMap;
import repast.model.agrosuccess.AgroSuccessCodeAliases;
import repast.model.agrosuccess.AgroSuccessCodeAliases.Aspect;
import repast.model.agrosuccess.AgroSuccessCodeAliases.Lct;
import repast.model.agrosuccess.AgroSuccessCodeAliases.SeedPresence;
import repast.model.agrosuccess.AgroSuccessCodeAliases.Succession;
import repast.model.agrosuccess.AgroSuccessCodeAliases.Water;

/**
 * Provides access to a mapping between environmental states and corresponding numerical codes to be
 * used within simulations. The outer map's keys are the names of land cover state variables the
 * inner maps provide bidirectional access to either the numerical code for a specific state for
 * each environmental variable, given a state alias, or vice versa. For example, the numerical value
 * for transition forest could be retrieved with
 * {@code numericalValueFromAlias("landCoverState", "TransForest")}. The alias for the soil moisture
 * state with code 1 is given by {@code aliasFromNumericalValue("water", 1)}.
 *
 * <h3>landCoverState</h3>
 * <ul>
 * <li>0 = WaterQuarry (Water or quarry)</li>
 * <li>1 = Burnt</li>
 * <li>2 = Wheat</li>
 * <li>3 = DAL (Depleted agricultural land)</li>
 * <li>4 = Shrubland</li>
 * <li>5 = Pine (Pine forest)</li>
 * <li>6 = TransForest (transition forest)</li>
 * <li>7 = Deciduous (Deciduous forest)</li>
 * <li>8 = Oak (Oak forest)</li>
 * </ul>
 *
 * <h3>succession</h3>
 * <ul>
 * <li>0 = regeneration</li>
 * <li>1 = secondary</li>
 * </ul>
 *
 * <h3>aspect</h3>
 * <ul>
 * <li>0 = north</li>
 * <li>1 = south</li>
 * </ul>
 *
 * <h3>seedPresence</h3>
 * <ul>
 * <li>0 = false</li>
 * <li>1 = true</li>
 * </ul>
 *
 * <h3>water</h3>
 * <ul>
 * <li>0 = xeric</li>
 * <li>1 = mesic</li>
 * <li>2 = hydric</li>
 * </ul>
 *
 * <h3>Notes</h3>
 * <p>
 * It is important that the numerical codes used to represent land-cover states in the ecological
 * succession model are consistent with those used to refer to different land-cover states in the
 * different packages which comprise the overall model (e.g. in {@code DefaultFireManager#isFlammable}). To
 * achieve this we use the enumeration {@link AgroSuccessCodeAliases.Lct#getCode()} as the single
 * source of truth for these values. We similarly use enumerations to supply numerical encodings for
 * succession pathway, aspect, seed presence, and soil moisture maps (see implementation below). We
 * reason that strong coupling between this class and {@link AgroSuccessCodeAliases} is acceptable
 * because both are specific the the AgroSuccess model.
 * </p>
 *
 * @author Andrew Lane
 * @see me.ajlane.geo.repast.succession.pathway.convert.EnvrStateAliasTranslator#numericalValueFromAlias(String,
 *      String)
 * @see me.ajlane.geo.repast.succession.pathway.convert.EnvrStateAliasTranslator#aliasFromNumericalValue(String,
 *      int)
 *
 */
public class AgroSuccessEnvrStateAliasTranslator extends EnvrStateAliasTranslator {

  public AgroSuccessEnvrStateAliasTranslator() {
    envStateMap.put("landCoverState", landCoverStateMap());
    envStateMap.put("succession", successionMap());
    envStateMap.put("aspect", aspectMap());
    envStateMap.put("seedPresence", seedPresenceMap());
    envStateMap.put("water", waterMap());
  }

  private BidiMap<String, Integer> landCoverStateMap() {
    BidiMap<String, Integer> map = new DualHashBidiMap<String, Integer>();
    for (Lct lct : Lct.values()) {
      map.put(lct.getAlias(), lct.getCode());
    }
    return map;
  }

  private BidiMap<String, Integer> successionMap() {
    BidiMap<String, Integer> map = new DualHashBidiMap<String, Integer>();
    for (Succession s : Succession.values()) {
      map.put(s.getAlias(), s.getCode());
    }
    return map;
  }

  private BidiMap<String, Integer> aspectMap() {
    BidiMap<String, Integer> map = new DualHashBidiMap<String, Integer>();
    for (Aspect a : Aspect.values()) {
      map.put(a.getAlias(), a.getCode());
    }
    return map;
  }

  private BidiMap<String, Integer> seedPresenceMap() {
    BidiMap<String, Integer> map = new DualHashBidiMap<String, Integer>();
    for (SeedPresence sp : SeedPresence.values()) {
      map.put(sp.getAlias(), sp.getCode());
    }
    return map;
  }

  private BidiMap<String, Integer> waterMap() {
    BidiMap<String, Integer> map = new DualHashBidiMap<String, Integer>();
    for (Water w : Water.values()) {
      map.put(w.getAlias(), w.getCode());
    }
    return map;
  }
}
