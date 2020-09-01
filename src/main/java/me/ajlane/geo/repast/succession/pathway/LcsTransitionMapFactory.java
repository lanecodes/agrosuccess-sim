package me.ajlane.geo.repast.succession.pathway;

import me.ajlane.geo.repast.succession.pathway.aliased.AliasedLcsTransitionMap;
import me.ajlane.geo.repast.succession.pathway.coded.CodedLcsTransitionMap;

public interface LcsTransitionMapFactory {
  CodedLcsTransitionMap getCodedLcsTransitionMap();
  AliasedLcsTransitionMap getAliasedLcsTransitionMap();
}
