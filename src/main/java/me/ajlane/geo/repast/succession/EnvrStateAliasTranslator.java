/**
 * 
 */
package me.ajlane.geo.repast.succession;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.collections15.BidiMap;

/**
 * Translates between the numerical values used internally in a simulation to specify environmental
 * state and the human readable descriptions of those states specified in the database.
 * 
 * @author Andrew Lane
 *
 */
public abstract class EnvrStateAliasTranslator {

  // BidiMap has format <alias, numericalID>
  protected Map<String, BidiMap<String, Integer>> envStateMap =
      new HashMap<String, BidiMap<String, Integer>>();

  String unmappableErrorMessage(String envStateName, int value) {
    return "Could not map variable " + envStateName + " with value \"" + value + "\" to alias";
  }

  String unmappableErrorMessage(String envStateName, String alias) {
    return "Could not map variable " + envStateName + " with alias \"" + alias
        + "\" to numberical value";
  }

  // Go from an integer state value to a human readable alias
  String aliasFromNumericalValue(String envStateName, int value) {
    try {
      return (String) envStateMap.get(envStateName).inverseBidiMap().get(value);
    } catch (NullPointerException e) {
      System.out.println(unmappableErrorMessage(envStateName, value));
      throw e;
    }
  }

  // Go from human readable alias to an integer state value
  int numericalValueFromAlias(String envStateName, String alias) {
    try {
      return (int) envStateMap.get(envStateName).get(alias);
    } catch (NullPointerException e) {
      System.out.println(unmappableErrorMessage(envStateName, alias));
      throw e;
    }    
  }
  
}
