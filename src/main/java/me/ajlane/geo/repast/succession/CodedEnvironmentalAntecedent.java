package me.ajlane.geo.repast.succession;

public class CodedEnvironmentalAntecedent
    extends EnvironmentalAntecedent<Integer, Integer, Integer, Integer, Integer, Integer, Integer> {

  public CodedEnvironmentalAntecedent(Integer startState, Integer successionPathway, Integer aspect,
      Integer pineSeeds, Integer oakSeeds, Integer deciduousSeeds, Integer water) {
    super(startState, successionPathway, aspect, pineSeeds, oakSeeds, deciduousSeeds, water);
  }
}
