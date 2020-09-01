package me.ajlane.geo.repast.succession.pathway.aliased;

import me.ajlane.geo.repast.succession.pathway.EnvrConsequent;

public class AliasedEnvrConsequent extends EnvrConsequent<String, Integer> {

  public AliasedEnvrConsequent(String targetState, Integer transitionTime) {
    super(targetState, transitionTime);
  }
}
