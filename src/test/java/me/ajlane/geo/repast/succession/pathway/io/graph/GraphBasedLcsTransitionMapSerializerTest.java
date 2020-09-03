package me.ajlane.geo.repast.succession.pathway.io.graph;

import static org.junit.Assert.assertEquals;
import java.io.File;
import java.io.IOException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.neo4j.graphdb.GraphDatabaseService;
import me.ajlane.geo.repast.succession.pathway.coded.CodedLcsTransitionMap;
import me.ajlane.geo.repast.succession.pathway.convert.AgroSuccessEnvrStateAliasTranslator;
import me.ajlane.geo.repast.succession.pathway.convert.EnvrStateAliasTranslator;
import me.ajlane.geo.repast.succession.pathway.io.CodedLcsTransitionMapReader;
import me.ajlane.geo.repast.succession.pathway.io.SerializedCodedLcsTransitionMapReader;
import me.ajlane.neo4j.EmbeddedGraphInstance;

/**
 * This is an integration test
 *
 * @author Andrew Lane
 */
public class GraphBasedLcsTransitionMapSerializerTest {

  @Rule
  public TemporaryFolder tmpOutputDir = new TemporaryFolder();

  @Test
  public void testMapReadFromSerializedFileMatchesGraph() throws IOException {
    File databaseDir = GraphBasedLcsTransitionMapSerializer.DEFAULT_DATABASE_DIR;
    String modelID = GraphBasedLcsTransitionMapSerializer.DEFAULT_MODEL_ID;
    File outputFile = tmpOutputDir.newFile(GraphBasedLcsTransitionMapSerializer.DEFAULT_OUTPUT_FILE_NAME);

    GraphBasedLcsTransitionMapSerializer.writeSerializedLcsTransitionMap(databaseDir, modelID, outputFile);

    CodedLcsTransitionMapReader reader = new SerializedCodedLcsTransitionMapReader(outputFile);
    CodedLcsTransitionMap deserializedMap = reader.getCodedLcsTransitionMap();

    // Load map directly from graph database
    GraphDatabaseService graph = new EmbeddedGraphInstance(databaseDir.getAbsolutePath());
    EnvrStateAliasTranslator envrStateAliasTranslator = new AgroSuccessEnvrStateAliasTranslator();
    CodedLcsTransitionMapReader lcsGraphReader =
        new GraphBasedLcsTransitionMapReader(graph, modelID, envrStateAliasTranslator);

    CodedLcsTransitionMap mapFromGraph = lcsGraphReader.getCodedLcsTransitionMap();
    graph.shutdown();

    assertEquals(mapFromGraph, deserializedMap);
  }

}
