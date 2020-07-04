package me.ajlane.geo.repast.succession;

public class AliasedEnvrConsequent extends EnvrConsequent<String, Integer> {

  public AliasedEnvrConsequent(String targetState, Integer transitionTime) {
    super(targetState, transitionTime);
  }
}
