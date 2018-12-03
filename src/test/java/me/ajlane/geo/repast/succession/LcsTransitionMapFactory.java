package me.ajlane.geo.repast.succession;

public interface LcsTransitionMapFactory {
  CodedLcsTransitionMap getCodedLcsTransitionMap();
  AliasedLcsTransitionMap getAliasedLcsTransitionMap();
}
