package me.ajlane.geo.repast.succession.pathway.io;

/**
 * Read maps specifying possible ecological succession pathways from persistent storage
 *
 * <p>
 * Implementing classes are able to supply maps expressing ecological succession pathways in terms
 * of <emph>both</emph> numerical codes and natural language aliases.
 * </p>
 *
 * @author Andrew Lane
 */
public interface LcsTransitionMapReader extends CodedLcsTransitionMapReader, AliasedLcsTransitionMapReader {
}
