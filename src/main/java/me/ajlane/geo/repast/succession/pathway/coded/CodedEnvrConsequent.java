package me.ajlane.geo.repast.succession.pathway.coded;

import me.ajlane.geo.repast.succession.pathway.EnvrConsequent;

public class CodedEnvrConsequent extends EnvrConsequent<Integer, Integer> {
  private static final long serialVersionUID = 8000285165547623493L;

  public CodedEnvrConsequent(Integer targetState, Integer transitionTime) {
    super(targetState, transitionTime);
  }
}
