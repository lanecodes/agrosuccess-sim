package me.ajlane.geo.repast.succession.pathway.io;

import java.io.File;
import org.neo4j.graphdb.GraphDatabaseService;
import me.ajlane.geo.repast.succession.pathway.convert.EnvrStateAliasTranslator;
import me.ajlane.geo.repast.succession.pathway.io.graph.GraphBasedLcsTransitionMapReader;

/**
 * Returns objects that read {@code CodedLcsTransitionMap}s specifying land-cover state transition
 * rules
 *
 * @author Andrew Lane
 */
public class CodedLcsTransitionMapReaderFactory {

  /**
   * Construct a {@code CodedLcsTransitionMapReader} from a model stored in a graph database
   *
   * @param graph Access point for the graph database containing the land-cover state transition
   *        model
   * @param modelID String matching the {@code model_ID} property identifying the model to use in
   *        the simulation
   * @param translator Translates between land-cover state transition rules expressed in terms of
   *        natural language aliases, and corresponding rules expressed as numerical codes
   * @return
   */
  public CodedLcsTransitionMapReader getCodedLcsTransitionMapReader(GraphDatabaseService graph,
      String modelID, EnvrStateAliasTranslator translator) {
    CodedLcsTransitionMapReader reader =
        new GraphBasedLcsTransitionMapReader(graph, modelID, translator);
    return reader;
  }

  /**
   * Construct a {@code CodedLcsTransitionMapReader} using a file on disk
   *
   * @implNote If another reader is added (e.g. for deserializing from JSON) that also only requires
   *           a {@code File} object to configure, the appropriate reader class should be selected
   *           using the file extension.
   * @param file The file containing the coded land-cover state transition pathway map
   * @return Configured {@code CodedLcsTransitionMapReader} object
   */
  public CodedLcsTransitionMapReader getCodedLcsTransitionMapReader(File file) {
    CodedLcsTransitionMapReader reader = new SerializedCodedLcsTransitionMapReader(file);
    return reader;
  }

}
