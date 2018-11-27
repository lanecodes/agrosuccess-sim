package me.ajlane.geo.repast.succession;

public class AliasedEnvrAntecedent
    extends EnvrAntecedent<String, String, String, String, String, String, String> {

  public AliasedEnvrAntecedent(String startState, String successionPathway, String aspect,
      String pineSeeds, String oakSeeds, String deciduousSeeds, String water) {
    super(startState, successionPathway, aspect, pineSeeds, oakSeeds, deciduousSeeds, water);
  }
}
