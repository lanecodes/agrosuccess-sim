/**
 * 
 */
package me.ajlane.geo.repast.succession;

/**
 * Convert a CodedEnvrAntecedent to an AliasedEnvrAntecedent or vice versa
 * 
 * @author Andrew Lane
 *
 */
public class EnvrAntecedentConverter {
  EnvrStateAliasTranslator translator;
  
  EnvrAntecedentConverter(EnvrStateAliasTranslator translator) {
    this.translator = translator;    
  }
  
  CodedEnvrAntecedent convert(AliasedEnvrAntecedent ante) {
    EnvrStateAliasTranslator tr = this.translator;
    CodedEnvrAntecedent convAnte = 
        new CodedEnvrAntecedent(tr.numericalValueFromAlias("landCoverState", ante.getStartState()), 
            tr.numericalValueFromAlias("succession", ante.getSuccessionPathway()), 
            tr.numericalValueFromAlias("aspect", ante.getAspect()), 
            tr.numericalValueFromAlias("seedPresence", ante.getPineSeeds()), 
            tr.numericalValueFromAlias("seedPresence", ante.getOakSeeds()), 
            tr.numericalValueFromAlias("seedPresence", ante.getDeciduousSeeds()),
            tr.numericalValueFromAlias("water", ante.getWater()));
    
    return convAnte;
  }
  
  AliasedEnvrAntecedent convert(CodedEnvrAntecedent ante) {
    EnvrStateAliasTranslator tr = this.translator;
    AliasedEnvrAntecedent convAnte = 
        new AliasedEnvrAntecedent(tr.aliasFromNumericalValue("landCoverState", ante.getStartState()), 
            tr.aliasFromNumericalValue("succession", ante.getSuccessionPathway()), 
            tr.aliasFromNumericalValue("aspect", ante.getAspect()), 
            tr.aliasFromNumericalValue("seedPresence", ante.getPineSeeds()), 
            tr.aliasFromNumericalValue("seedPresence", ante.getOakSeeds()), 
            tr.aliasFromNumericalValue("seedPresence", ante.getDeciduousSeeds()),
            tr.aliasFromNumericalValue("water", ante.getWater()));
    
    return convAnte;
  }

}