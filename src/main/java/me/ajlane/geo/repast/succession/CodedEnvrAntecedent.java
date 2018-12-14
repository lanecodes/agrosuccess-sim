package me.ajlane.geo.repast.succession;

public class CodedEnvrAntecedent
    extends EnvrAntecedent<Integer, Integer, Integer, Integer, Integer, Integer, Integer> {

  public CodedEnvrAntecedent(Integer startState, Integer successionPathway, Integer aspect,
      Integer pineSeeds, Integer oakSeeds, Integer deciduousSeeds, Integer water) {
    super(startState, successionPathway, aspect, pineSeeds, oakSeeds, deciduousSeeds, water);
  }
}
