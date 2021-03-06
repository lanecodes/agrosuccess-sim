/**
 *
 */
package me.ajlane.neo4j;

import java.io.File;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.QueryExecutionException;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.ResourceIterable;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.event.KernelEventHandler;
import org.neo4j.graphdb.event.TransactionEventHandler;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.factory.GraphDatabaseSettings;
import org.neo4j.graphdb.index.IndexManager;
import org.neo4j.graphdb.schema.Schema;
import org.neo4j.graphdb.traversal.BidirectionalTraversalDescription;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.kernel.StoreLockException;

/**
 *
 * In addition to providing all the functionality guaranteed by the GraphDatabaseService interface,
 * this class also provides methods to use a GraphLoader object to add data to an embedded
 * org.neo4j.graphdb.GraphDatabaseService. NOTE this violates the single responsibility principle. A possible
 * future improvement could be to refactor into two classes:
 *
 * <ol>
 * <li>EmbeddedGraphInstance which initialises
 * an embedded graph database using data from a specific location on disk, choosing appropriate
 * parameters and establishing a shutdown hook</li>
 * <li>ExternalCypherLoader which, given a GraphDatabaseService, loads Cypher from files on disk.</li>
 * </ol>
 *
 * This might have been implemented more elegantly by inheriting from a concrete class provided by
 * the Neo4j API, however no such object exits. In order to make use of the provided
 * GraphDatabaseFactory, I have satisfied the interface requirements by passing method calls onto a
 * GraphDatabaseService object held within each instance of this class.
 *
 * @author Andrew Lane
 *
 */
public class EmbeddedGraphInstance implements GraphDatabaseService {

  final static Logger logger = Logger.getLogger(EmbeddedGraphInstance.class);

  private GraphDatabaseService graphDb;

  /**
   * @param databaseDirectory File path to the directory where the database should be located. If a
   *        database already exists in that location it will be appended to *
   */
  public EmbeddedGraphInstance(String databaseDirectory) {
    this(databaseDirectory, false);
  }

  /**
   * @param databaseDirectory File path to the directory where the database should be located.
   * @param overwriteDatabase Specify if an existing database in the location specified by
   *        databaseDirectory should be deleted and overwritten if it exists.
   */
  public EmbeddedGraphInstance(String databaseDirectory, boolean overwriteDatabase) {

    if (overwriteDatabase) {
      deleteDir(new File(databaseDirectory));
    }

    try {

      graphDb = new GraphDatabaseFactory().newEmbeddedDatabaseBuilder(new File(databaseDirectory))
          .setConfig(GraphDatabaseSettings.pagecache_memory, "512M")
          .setConfig(GraphDatabaseSettings.string_block_size, "60")
          .setConfig(GraphDatabaseSettings.array_block_size, "300").newGraphDatabase();

      registerShutdownHook(graphDb);
    } catch (StoreLockException e) {
      logger.error("Couldn't lock database at " + databaseDirectory + "\n"
                   + "Check it isn't being accessed by another process.", e);
      throw e;
    }
  }

  public static boolean deleteDir(File dir) {
    if (dir.isDirectory()) {
      String[] children = dir.list();
      for (int i = 0; i < children.length; i++) {
        boolean success = deleteDir(new File(dir, children[i]));
        if (!success) {
          return false;
        }
      }
    }
    return dir.delete(); // if (deleteDir(File)...) {..} else {error, directory couldn't be deletd}
  }

  private static void registerShutdownHook(final GraphDatabaseService graphDb) {
    // Registers a shutdown hook for the Neo4j instance so that it
    // shuts down nicely when the VM exits (even if you "Ctrl-C" the
    // running application).
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        graphDb.shutdown();
      }
    });
  }

  public void insertExternalCypher(String cypherRoot, String fnameSuffix, String globalParamFile) {

    System.out.print("Loading Cypher files into database:\n" + "Cypher root: " + cypherRoot + "\n"
        + "Filename suffix: " + fnameSuffix + "\n" + "Parameter file: " + globalParamFile
        + "\n...");

    GraphLoaderFactory factory = new GraphLoaderFactory();
    GraphLoaderType graphLoader = factory.create(cypherRoot, fnameSuffix, globalParamFile);

    Boolean moreQueries = true;

    try (Transaction tx = graphDb.beginTx()) {

      while (moreQueries) {
        String nextQuery = graphLoader.getNextQuery();

        if (nextQuery == null) {
          moreQueries = false;
        } else {
          Result res = graphDb.execute(nextQuery);
          res.close();
        }
      }
      tx.success();
    }
    logger.debug("Finished loading Cypher from " + cypherRoot);
  }

  @Override
  public Transaction beginTx() {
    return graphDb.beginTx();
  }

  @Override
  public Transaction beginTx(long arg0, TimeUnit arg1) {
    return graphDb.beginTx(arg0, arg1);
  }

  @Override
  public BidirectionalTraversalDescription bidirectionalTraversalDescription() {
    return graphDb.bidirectionalTraversalDescription();
  }

  @Override
  public Node createNode() {
    return graphDb.createNode();
  }

  @Override
  public Node createNode(Label... arg0) {
    return graphDb.createNode(arg0);
  }

  @Override
  public Long createNodeId() {
    return graphDb.createNodeId();
  }

  @Override
  public Result execute(String arg0) throws QueryExecutionException {
    return graphDb.execute(arg0);
  }

  @Override
  public Result execute(String arg0, Map<String, Object> arg1) throws QueryExecutionException {
    return graphDb.execute(arg0, arg1);
  }

  @Override
  public Result execute(String arg0, long arg1, TimeUnit arg2) throws QueryExecutionException {
    return graphDb.execute(arg0, arg1, arg2);
  }

  @Override
  public Result execute(String arg0, Map<String, Object> arg1, long arg2, TimeUnit arg3)
      throws QueryExecutionException {
    return graphDb.execute(arg0, arg1, arg2, arg3);
  }

  @Override
  public Node findNode(Label arg0, String arg1, Object arg2) {
    return graphDb.findNode(arg0, arg1, arg2);
  }

  @Override
  public ResourceIterator<Node> findNodes(Label arg0) {
    return graphDb.findNodes(arg0);
  }

  @Override
  public ResourceIterator<Node> findNodes(Label arg0, String arg1, Object arg2) {
    return graphDb.findNodes(arg0, arg1, arg2);
  }

  @Override
  public ResourceIterable<Label> getAllLabels() {
    return graphDb.getAllLabels();
  }

  @Override
  public ResourceIterable<Label> getAllLabelsInUse() {
    return graphDb.getAllLabelsInUse();
  }

  @Override
  public ResourceIterable<Node> getAllNodes() {
    return graphDb.getAllNodes();
  }

  @Override
  public ResourceIterable<String> getAllPropertyKeys() {
    return graphDb.getAllPropertyKeys();
  }

  @Override
  public ResourceIterable<RelationshipType> getAllRelationshipTypes() {
    return graphDb.getAllRelationshipTypes();
  }

  @Override
  public ResourceIterable<RelationshipType> getAllRelationshipTypesInUse() {
    return graphDb.getAllRelationshipTypesInUse();
  }

  @Override
  public ResourceIterable<Relationship> getAllRelationships() {
    return graphDb.getAllRelationships();
  }

  @Override
  public Node getNodeById(long arg0) {
    return graphDb.getNodeById(arg0);
  }

  @Override
  public Relationship getRelationshipById(long arg0) {
    return graphDb.getRelationshipById(arg0);
  }

  @Override
  public IndexManager index() {
    return graphDb.index();
  }

  @Override
  public boolean isAvailable(long arg0) {
    return graphDb.isAvailable(arg0);
  }

  @Override
  public KernelEventHandler registerKernelEventHandler(KernelEventHandler arg0) {
    return graphDb.registerKernelEventHandler(arg0);
  }

  @Override
  public <T> TransactionEventHandler<T> registerTransactionEventHandler(
      TransactionEventHandler<T> arg0) {
    return graphDb.registerTransactionEventHandler(arg0);
  }

  @Override
  public Schema schema() {
    return graphDb.schema();
  }

  @Override
  public void shutdown() {
    graphDb.shutdown();
  }

  @Override
  public TraversalDescription traversalDescription() {
    return graphDb.traversalDescription();
  }

  @Override
  public KernelEventHandler unregisterKernelEventHandler(KernelEventHandler arg0) {
    return graphDb.unregisterKernelEventHandler(arg0);
  }

  @Override
  public <T> TransactionEventHandler<T> unregisterTransactionEventHandler(
      TransactionEventHandler<T> arg0) {
    return graphDb.unregisterTransactionEventHandler(arg0);
  }

}
