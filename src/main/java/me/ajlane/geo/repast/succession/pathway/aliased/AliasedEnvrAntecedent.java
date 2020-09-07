package me.ajlane.geo.repast.succession.pathway.aliased;

import me.ajlane.geo.repast.succession.pathway.EnvrAntecedent;

public class AliasedEnvrAntecedent
    extends EnvrAntecedent<String, String, String, String, String, String, String> {
  private static final long serialVersionUID = -7019428621830504053L;

  public AliasedEnvrAntecedent(String startState, String successionPathway, String aspect,
      String pineSeeds, String oakSeeds, String deciduousSeeds, String water) {
    super(startState, successionPathway, aspect, pineSeeds, oakSeeds, deciduousSeeds, water);
  }
}
