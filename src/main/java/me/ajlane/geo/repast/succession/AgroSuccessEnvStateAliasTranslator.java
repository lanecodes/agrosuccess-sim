/**
 * 
 */
package me.ajlane.geo.repast.succession;

import org.apache.commons.collections15.BidiMap;
import org.apache.commons.collections15.bidimap.DualHashBidiMap;

/**
 * @author andrew
 *
 */
public class AgroSuccessEnvStateAliasTranslator
		extends EnvironmentalStateAliasTranslator {
	
	public AgroSuccessEnvStateAliasTranslator() {
		envStateMap.put("landCoverState", landCoverStateMap());
		envStateMap.put("succession", successionMap());
		envStateMap.put("aspect", aspectMap());
		envStateMap.put("seedPresence", seedPresenceMap());
		envStateMap.put("water", waterMap());		
	}
	
	/**	 		0 = Water/ Quarry
	 * 			1 = Burnt
	 * 			2 = Barley
	 *			3 = Wheat
	 *			4 = Depleted agricultural land
	 *			5 = Shrubland
	 *			6 = Pine forest
	 *			7 = Transition forest
	 *			8 = Deciduous forest
	 *			9 = Oak forest
	 * @return
	 */
	private BidiMap<String,Integer> landCoverStateMap() {
		BidiMap<String,Integer> map = new DualHashBidiMap<String, Integer>();
		
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
	
	private BidiMap<String,Integer> successionMap() {
		BidiMap<String,Integer> map = new DualHashBidiMap<String, Integer>();
		
		map.put("regeneration", 0);
		map.put("secondary", 1);
		
		return map;		
	}
	
	private BidiMap<String,Integer> aspectMap() {
		BidiMap<String,Integer> map = new DualHashBidiMap<String, Integer>();
		
		map.put("north", 0);
		map.put("south", 1);
		
		return map;		
	}
	
	private BidiMap<String,Integer> seedPresenceMap() {
		BidiMap<String,Integer> map = new DualHashBidiMap<String, Integer>();
		
		map.put("false", 0);
		map.put("true", 1);
		
		return map;	
	}
	
	private BidiMap<String,Integer> waterMap() {
		BidiMap<String,Integer> map = new DualHashBidiMap<String, Integer>();
		
		map.put("xeric", 0);
		map.put("mesic", 1);
		map.put("hydric", 2);
		
		return map;	
	}
}
