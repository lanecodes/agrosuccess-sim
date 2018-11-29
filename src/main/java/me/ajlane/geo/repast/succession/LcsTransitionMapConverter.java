/**
 * 
 */
package me.ajlane.geo.repast.succession;

/**
 * @author Andrew Lane
 *
 */
public class LcsTransitionMapConverter {
  EnvrStateAliasTranslator translator;
  /**
   * 
   * @param translator 
   */
  LcsTransitionMapConverter(EnvrStateAliasTranslator translator) {
    this.translator = translator;
  }

  /**
   * 
   * @param aliasedMap 
   * @return 
   */
  public CodedLcsTransitionMap convert(AliasedLcsTransitionMap aliasedMap) {
    // TODO use translator to convert aliased map to a coded one
  }

  /**
   * 
   * @param codedMap 
   * @return 
   */
  public AliasedLcsTransitionMap convert(CodedLcsTransitionMap codedMap) {
    //TODO 
  }
};

