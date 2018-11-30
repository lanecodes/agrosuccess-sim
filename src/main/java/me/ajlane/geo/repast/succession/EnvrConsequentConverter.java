/**
 * 
 */
package me.ajlane.geo.repast.succession;

/**
 * Convert a CodedEnvrConsequent to an AliasedEnvrConsequent or vice versa
 * 
 * @author Andrew Lane
 *
 */
public class EnvrConsequentConverter {
  EnvrStateAliasTranslator translator;

  EnvrConsequentConverter(EnvrStateAliasTranslator translator) {
    this.translator = translator;
  }

  CodedEnvrConsequent convert(AliasedEnvrConsequent cons) {
    EnvrStateAliasTranslator tr = this.translator;
    CodedEnvrConsequent convCons =
        new CodedEnvrConsequent(tr.numericalValueFromAlias("landCoverState", cons.getTargetState()),
            cons.getTransitionTime());

    return convCons;
  }

  AliasedEnvrConsequent convert(CodedEnvrConsequent cons) {
    EnvrStateAliasTranslator tr = this.translator;
    AliasedEnvrConsequent convCons = new AliasedEnvrConsequent(
        tr.aliasFromNumericalValue("landCoverState", cons.getTargetState()),
        cons.getTransitionTime());

    return convCons;
  }

}
