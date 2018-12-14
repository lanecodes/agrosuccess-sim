/**
 * 
 */
package me.ajlane.geo.repast.succession;

import org.apache.commons.collections15.BidiMap;
import org.apache.commons.collections15.bidimap.DualHashBidiMap;

/**
 * Provides access to a mapping between environmental states and corresponding numerical codes to be
 * used within simulations. The outer map's keys are the names of land cover state variables the
 * inner maps provide bidirectional access to either the numerical code for a specific state for
 * each environmental variable, given a state alias, or vice versa. For example, the numerical value
 * for transition forest could be retrieved with
 * {@code numericalValueFromAlias("landCoverState", "TransForest")}. The alias for the soil moisture
 * state with code 1 is given by {@code aliasFromNumericalValue("water", 1)}.
 * 
 * <h2>landCoverState</h2>
 * <ul>
 * <li>0 = WaterQuarry (Water or quarry)</li>
 * <li>1 = Burnt</li>
 * <li>2 = Barley</li>
 * <li>3 = Wheat</li>
 * <li>4 = DAL (Depleted agricultural land)</li>
 * <li>5 = Shrubland</li>
 * <li>6 = Pine (Pine forest)</li>
 * <li>7 = TransForest (transition forest)</li>
 * <li>8 = Deciduous (Deciduous forest)</li>
 * <li>9 = Oak (Oak forest)</li>
 * </ul>
 * 
 * <h2>succession</h2>
 * <ul>
 * <li>0 = regeneration</li>
 * <li>1 = secondary</li>
 * </ul>
 * 
 * <h2>aspect</h2>
 * <ul>
 * <li>0 = north</li>
 * <li>1 = south</li>
 * </ul>
 * 
 * <h2>seedPresence</h2>
 * <ul>
 * <li>0 = false</li>
 * <li>1 = true</li>
 * </ul>
 * 
 * <h2>water</h2>
 * <ul>
 * <li>0 = xeric</li>
 * <li>1 = mesic</li>
 * <li>2 = hydric</li>
 * </ul>
 * 
 * @author Andrew Lane
 * @see me.ajlane.geo.repast.succession.EnvrStateAliasTranslator#numericalValueFromAlias(String,
 *      String)
 * @see me.ajlane.geo.repast.succession.EnvrStateAliasTranslator#aliasFromNumericalValue(String,
 *      int)
 * 
 */
public class AgroSuccessEnvrStateAliasTranslator extends EnvrStateAliasTranslator {

   AgroSuccessEnvrStateAliasTranslator() {
    envStateMap.put("landCoverState", landCoverStateMap());
    envStateMap.put("succession", successionMap());
    envStateMap.put("aspect", aspectMap());
    envStateMap.put("seedPresence", seedPresenceMap());
    envStateMap.put("water", waterMap());
  }

  private BidiMap<String, Integer> landCoverStateMap() {
    BidiMap<String, Integer> map = new DualHashBidiMap<String, Integer>();

    map.put("WaterQuarry", 0);
    map.put("Burnt", 1);
    map.put("Barley", 2);
    map.put("Wheat", 3);
    map.put("DAL", 4);
    map.put("Shrubland", 5);
    map.put("Pine", 6);
    map.put("TransForest", 7);
    map.put("Deciduous", 8);
    map.put("Oak", 9);

    return map;
  }

  private BidiMap<String, Integer> successionMap() {
    BidiMap<String, Integer> map = new DualHashBidiMap<String, Integer>();

    map.put("regeneration", 0);
    map.put("secondary", 1);

    return map;
  }

  private BidiMap<String, Integer> aspectMap() {
    BidiMap<String, Integer> map = new DualHashBidiMap<String, Integer>();

    map.put("north", 0);
    map.put("south", 1);

    return map;
  }

  private BidiMap<String, Integer> seedPresenceMap() {
    BidiMap<String, Integer> map = new DualHashBidiMap<String, Integer>();

    map.put("false", 0);
    map.put("true", 1);

    return map;
  }

  private BidiMap<String, Integer> waterMap() {
    BidiMap<String, Integer> map = new DualHashBidiMap<String, Integer>();

    map.put("xeric", 0);
    map.put("mesic", 1);
    map.put("hydric", 2);

    return map;
  }
}
