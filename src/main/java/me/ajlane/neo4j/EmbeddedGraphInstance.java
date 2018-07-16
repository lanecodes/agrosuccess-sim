/**
 * 
 */
package me.ajlane.neo4j;

import java.io.File;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
import org.neo4j.graphdb.index.IndexManager;
import org.neo4j.graphdb.schema.Schema;
import org.neo4j.graphdb.traversal.BidirectionalTraversalDescription;
import org.neo4j.graphdb.traversal.TraversalDescription;

/**
 * 
 * In addition to providing all the functionality guaranteed by the 
 * GraphDatabaseService interface, this class also provides methods 
 * to use a GraphLoader object to add data to an embedded 
 * org.neo4j.graphdb.GraphDatabaseService. 
 * 
 * This might have been implemented more elegantly by inheriting from
 * a concrete class provided by the Neo4j API, however no such object
 * exits. In order to make use of the provided GraphDatabaseFactory, 
 * I have satisfied the interface requirements by passing method calls
 * onto a GraphDatabaseService object held within each instance of this
 * class. 
 * 
 * @author Andrew Lane
 *
 */
public class EmbeddedGraphInstance implements GraphDatabaseService {
	
	private GraphDatabaseService graphDb;
	
	public EmbeddedGraphInstance(String databaseDirectory) {
		
		graphDb = new GraphDatabaseFactory().newEmbeddedDatabase( new File(databaseDirectory) );
		registerShutdownHook( graphDb );
		
	}
	
	private static void registerShutdownHook( final GraphDatabaseService graphDb )
	{
	    // Registers a shutdown hook for the Neo4j instance so that it
	    // shuts down nicely when the VM exits (even if you "Ctrl-C" the
	    // running application).
	    Runtime.getRuntime().addShutdownHook( new Thread()
	    {
	        @Override
	        public void run()
	        {
	            graphDb.shutdown();
	        }
	    } );
	}
	
	public void insertExternalCypher(String cypherRoot, String fnameSuffix, 
				String globalParamFile ) {
    	
		GraphLoaderFactory factory = new GraphLoaderFactory();
    	GraphLoaderType graphLoader = factory.create(cypherRoot, fnameSuffix, 
    											globalParamFile);
    	
    	Boolean moreQueries = true;   
    	int queryNo = 0;
    	
    	try ( Transaction tx = graphDb.beginTx() ) {
    		
        	while (moreQueries) {    		
        		String nextQuery = graphLoader.getNextQuery();
        		
        		if (nextQuery == null) {
        			System.out.println("Loaded " + queryNo + " Cypher queries"); 
        			moreQueries  = false;
        		} else {
            		System.out.println("Loading query\n:" + nextQuery);
            		Result res = graphDb.execute(nextQuery); res.close();       		
            		queryNo++;
        		}         		
        	}
        	tx.success();
    	}
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
	public Result execute(String arg0, Map<String, Object> arg1)
			throws QueryExecutionException {
		return graphDb.execute(arg0, arg1);
	}

	@Override
	public Result execute(String arg0, long arg1, TimeUnit arg2)
			throws QueryExecutionException {
		return graphDb.execute(arg0, arg1, arg2);
	}

	@Override
	public Result execute(String arg0, Map<String, Object> arg1, long arg2,
			TimeUnit arg3) throws QueryExecutionException {
		return graphDb.execute(arg0, arg1, arg2, arg3);
	}

	@Override
	public Node findNode(Label arg0, String arg1, Object arg2) {
		return graphDb.findNode(arg0,  arg1,  arg2);
	}

	@Override
	public ResourceIterator<Node> findNodes(Label arg0) {
		return graphDb.findNodes(arg0);
	}

	@Override
	public ResourceIterator<Node> findNodes(Label arg0, String arg1,
			Object arg2) {
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
	public KernelEventHandler registerKernelEventHandler(
			KernelEventHandler arg0) {
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
	public KernelEventHandler unregisterKernelEventHandler(
			KernelEventHandler arg0) {
		return graphDb.unregisterKernelEventHandler(arg0);
	}

	@Override
	public <T> TransactionEventHandler<T> unregisterTransactionEventHandler(
			TransactionEventHandler<T> arg0) {
		return graphDb.unregisterTransactionEventHandler(arg0);
	}

}
