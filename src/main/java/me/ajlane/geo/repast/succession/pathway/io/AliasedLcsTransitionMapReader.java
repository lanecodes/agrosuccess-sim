package me.ajlane.geo.repast.succession.pathway.io;

import me.ajlane.geo.repast.succession.pathway.aliased.AliasedLcsTransitionMap;

/**
 * Read a map specifying possible ecological succession pathways from persistent storage
 *
 * @author Andrew Lane
 */
public interface AliasedLcsTransitionMapReader {

  /**
   * @return Map specifying possible ecological succession pathways using natural language aliases
   */
  AliasedLcsTransitionMap getAliasedLcsTransitionMap();
}
