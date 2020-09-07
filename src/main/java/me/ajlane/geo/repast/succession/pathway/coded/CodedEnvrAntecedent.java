package me.ajlane.geo.repast.succession.pathway.coded;

import me.ajlane.geo.repast.succession.pathway.EnvrAntecedent;

public class CodedEnvrAntecedent
    extends EnvrAntecedent<Integer, Integer, Integer, Integer, Integer, Integer, Integer> {
  private static final long serialVersionUID = -168063047344858773L;

  public CodedEnvrAntecedent(Integer startState, Integer successionPathway, Integer aspect,
      Integer pineSeeds, Integer oakSeeds, Integer deciduousSeeds, Integer water) {
    super(startState, successionPathway, aspect, pineSeeds, oakSeeds, deciduousSeeds, water);
  }
}
