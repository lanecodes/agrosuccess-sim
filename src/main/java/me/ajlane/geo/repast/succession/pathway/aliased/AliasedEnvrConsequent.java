package me.ajlane.geo.repast.succession.pathway.aliased;

import me.ajlane.geo.repast.succession.pathway.EnvrConsequent;

public class AliasedEnvrConsequent extends EnvrConsequent<String, Integer> {
  private static final long serialVersionUID = -6359385916383328795L;

  public AliasedEnvrConsequent(String targetState, Integer transitionTime) {
    super(targetState, transitionTime);
  }
}
