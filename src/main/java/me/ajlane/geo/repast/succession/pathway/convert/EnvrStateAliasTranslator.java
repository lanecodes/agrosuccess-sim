/**
 *
 */
package me.ajlane.geo.repast.succession.pathway.convert;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.collections15.BidiMap;
import org.apache.log4j.Logger;

/**
 * Translates between the numerical values used internally in a simulation to specify environmental
 * state and the human readable descriptions of those states specified in the database.
 *
 * @author Andrew Lane
 *
 */
public abstract class EnvrStateAliasTranslator {

  final static Logger logger = Logger.getLogger(EnvrStateAliasTranslator.class);

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
  public String aliasFromNumericalValue(String envStateName, int value) {
    try {
      return envStateMap.get(envStateName).inverseBidiMap().get(value);
    } catch (NullPointerException e) {
      logger.error(unmappableErrorMessage(envStateName, value), e);
      throw e;
    }
  }

  // Go from human readable alias to an integer state value
  public int numericalValueFromAlias(String envStateName, String alias) {
    try {
      return envStateMap.get(envStateName).get(alias);
    } catch (NullPointerException e) {
      logger.error(unmappableErrorMessage(envStateName, alias), e);
      throw e;
    }
  }

}
