package me.ajlane.geo.repast.succession.pathway.io.graph;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.GraphDatabaseService;
import me.ajlane.geo.repast.succession.pathway.coded.CodedLcsTransitionMap;
import me.ajlane.geo.repast.succession.pathway.convert.AgroSuccessEnvrStateAliasTranslator;
import me.ajlane.geo.repast.succession.pathway.convert.EnvrStateAliasTranslator;
import me.ajlane.geo.repast.succession.pathway.io.CodedLcsTransitionMapReader;
import me.ajlane.neo4j.EmbeddedGraphInstance;

/**
 * Read local copy of graph database and write resulting map to serialised Java object
 *
 * @author Andrew Lane
 */
public class GraphBasedLcsTransitionMapSerializer {

  static final File DEFAULT_DATABASE_DIR = new File("data/graph/databases/graph.db");
  static final String DEFAULT_MODEL_ID = "AgroSuccess-dev";
  static final File DEFAULT_OUTPUT_DIR = new File("data/succession");
  static final String DEFAULT_OUTPUT_FILE_NAME = "agrosuccess.codedlcstrans";

  /**
   * @param args Optional command line arguments. First argument is the name of the output file
   *        (defaults to 'agrosuccess.codedlcstrans'). Second argument is the modelID to load from
   *        the graph database (defaults to 'AgroSuccess-dev'. Assumes the output file is written to
   *        the directory 'data/succession' and that the graph database is located in
   *        'data/graph/databases/graph.db'
   * @throws FileNotFoundException
   * @throws IOException
   */
  public static void main(String[] args) throws FileNotFoundException, IOException {

    String modelID = getModelIDFromArgs(args);
    File outputFile = new File(DEFAULT_OUTPUT_DIR, getOutputFileNameFromArgs(args));

    writeSerializedLcsTransitionMap(DEFAULT_DATABASE_DIR, modelID, outputFile);
  }

  private static String getOutputFileNameFromArgs(String[] args) {
    String outputFileName;
    if (args.length > 0) {
      outputFileName = args[0];
    } else {
      outputFileName = DEFAULT_OUTPUT_FILE_NAME;
    }
    return outputFileName;
  }

  private static String getModelIDFromArgs(String[] args) {
    String modelID;
    if (args.length > 1) {
      modelID = args[1];
    } else {
      modelID = DEFAULT_MODEL_ID;
    }
    return modelID;
  }

  public static void writeSerializedLcsTransitionMap(File databaseDir, String modelID,
      File outputFile)
      throws FileNotFoundException, IOException {
    ensureOutputFileDirectoryExists(outputFile);

    AgroSuccessLcsTransitionMapGraphReader reader =
        new AgroSuccessLcsTransitionMapGraphReader(databaseDir, modelID);

    ObjectOutputStream objectOutputStream =
        new ObjectOutputStream(new FileOutputStream(outputFile));
    objectOutputStream.writeObject(reader.read());
    objectOutputStream.flush();
    objectOutputStream.close();
  }

  private static void ensureOutputFileDirectoryExists(File outputFile) {
    File outputDir = outputFile.getParentFile();
    if (!outputDir.isDirectory()) {
      outputDir.mkdirs();
    }
  }

  private static class AgroSuccessLcsTransitionMapGraphReader {
    final static Logger logger = Logger.getLogger(AgroSuccessLcsTransitionMapGraphReader.class);
    private File databaseDir;
    private String modelID;

    AgroSuccessLcsTransitionMapGraphReader(File databaseDir, String modelID) {
      this.databaseDir = databaseDir;
      this.modelID = modelID;
    }

    CodedLcsTransitionMap read() {
      GraphDatabaseService graph = new EmbeddedGraphInstance(this.databaseDir.getAbsolutePath());
      EnvrStateAliasTranslator envrStateAliasTranslator = new AgroSuccessEnvrStateAliasTranslator();
      CodedLcsTransitionMapReader lcsGraphReader =
          new GraphBasedLcsTransitionMapReader(graph, this.modelID, envrStateAliasTranslator);
      CodedLcsTransitionMap map = lcsGraphReader.getCodedLcsTransitionMap();
      logger.info(map.size() + " transition rules loaded for model '" + this.modelID
          + "' from graph at " + this.databaseDir.getAbsolutePath());
      graph.shutdown();
      return map;
    }

  }

}
