/**
 * 
 */
package me.ajlane.geo.repast.succession;

import java.util.Map;

/**
 * @author Andrew Lane
 *
 */
public class LcsTransitionMapConverter {
  EnvrAntecedentConverter anteTrans;
  EnvrConsequentConverter consTrans;
  /**
   * 
   * @param translator 
   */
  LcsTransitionMapConverter(EnvrStateAliasTranslator translator) {
    this.anteTrans = new EnvrAntecedentConverter(translator);
    this.consTrans = new EnvrConsequentConverter(translator);
  }

  /**
   * 
   * @param aliasedMap 
   * @return 
   */
  public CodedLcsTransitionMap convert(AliasedLcsTransitionMap aliasedMap) {
    CodedLcsTransitionMap convMap = new CodedLcsTransitionMap();
    for(Map.Entry<AliasedEnvrAntecedent, AliasedEnvrConsequent> transRule: aliasedMap.entrySet()) {
      convMap.put(anteTrans.convert(transRule.getKey()), consTrans.convert(transRule.getValue()));
    }
    return convMap;
  }

  /**
   * 
   * @param codedMap 
   * @return 
   */
  public AliasedLcsTransitionMap convert(CodedLcsTransitionMap codedMap) {
    AliasedLcsTransitionMap convMap = new AliasedLcsTransitionMap();
    for(Map.Entry<CodedEnvrAntecedent, CodedEnvrConsequent> transRule: codedMap.entrySet()) {
      convMap.put(anteTrans.convert(transRule.getKey()), consTrans.convert(transRule.getValue()));
    }
    return convMap;
  }

}

