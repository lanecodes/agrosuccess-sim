package me.ajlane.geo.repast.succession.pathway.io;

import me.ajlane.geo.repast.succession.pathway.coded.CodedLcsTransitionMap;

/**
 * Read a map specifying possible ecological succession pathways from persistent storage
 *
 * @author Andrew Lane
 */
public interface CodedLcsTransitionMapReader {

  /**
   * @return Map specifying possible ecological succession pathways using numerical codes
   */
  CodedLcsTransitionMap getCodedLcsTransitionMap();
}
