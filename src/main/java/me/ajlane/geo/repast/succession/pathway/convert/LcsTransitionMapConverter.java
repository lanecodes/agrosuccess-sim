/**
 *
 */
package me.ajlane.geo.repast.succession.pathway.convert;

import java.util.Map;
import me.ajlane.geo.repast.succession.pathway.aliased.AliasedEnvrAntecedent;
import me.ajlane.geo.repast.succession.pathway.aliased.AliasedEnvrConsequent;
import me.ajlane.geo.repast.succession.pathway.aliased.AliasedLcsTransitionMap;
import me.ajlane.geo.repast.succession.pathway.coded.CodedEnvrAntecedent;
import me.ajlane.geo.repast.succession.pathway.coded.CodedEnvrConsequent;
import me.ajlane.geo.repast.succession.pathway.coded.CodedLcsTransitionMap;

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
  public LcsTransitionMapConverter(EnvrStateAliasTranslator translator) {
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

