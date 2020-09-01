package me.ajlane.geo.repast.succession.pathway.coded;

import me.ajlane.geo.repast.succession.pathway.EnvrConsequent;

public class CodedEnvrConsequent extends EnvrConsequent<Integer, Integer> {

  public CodedEnvrConsequent(Integer targetState, Integer transitionTime) {
    super(targetState, transitionTime);
  }
}
