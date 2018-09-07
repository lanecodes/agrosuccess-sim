/**
 * 
 */
package me.ajlane.geo.repast.succession;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections15.BidiMap;

/**
 * Translates between the numerical values used internally in a simulation
 * to specify environmental state and the human readable descriptions of those
 * states specified in the database.
 *  
 * @author andrew
 *
 */
public abstract class EnvironmentalStateAliasTranslator {
	
	// BidiMap has format <alias, numericalID>
	protected Map<String,BidiMap<String,Integer>> envStateMap = new HashMap<String, BidiMap<String,Integer>>();	
	
	// Go from an integer state value to a human readable alias
	String aliasFromNumericalValue(String envStateName, int value) {
		return (String)envStateMap.get(envStateName).inverseBidiMap().get(value);
	}
	
	// Go from human readable alias to an integer state value
	int numericalValueFromAlias(String envStateName, String alias) {
		return (int)envStateMap.get(envStateName).get(alias);
	}	
}
