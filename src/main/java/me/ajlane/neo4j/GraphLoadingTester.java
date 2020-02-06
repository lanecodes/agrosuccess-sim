package me.ajlane.neo4j;

import org.apache.log4j.Logger;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;

import me.ajlane.neo4j.GraphLoaderFactory;
import me.ajlane.neo4j.GraphLoaderType;

/** Manual tests used to help understand how to use the Neo4j embedded
 * 	graph database. This logic should be moved into formal unit tests.
 *
 * @author andrew
 *
 */
public class GraphLoadingTester {

    final static Logger logger = Logger.getLogger(GraphLoadingTester.class);

	static String projectRoot = "/home/andrew/AgroSuccess/";
	static String cypherRoot = projectRoot + "views";
	static String fnameSuffix = "_w";
	static String globalParamFile = projectRoot + "global_parameters.json";

    public static GraphLoaderType constructGraphLoader() {

    	GraphLoaderFactory factory = new GraphLoaderFactory();
    	GraphLoaderType graphLoader = factory.create(cypherRoot, fnameSuffix,
    											globalParamFile);

    	return graphLoader;
    }

    /** Prints out the queries provided by a GraphLoaderType without
     * putting them anywhere near a database. The check here is whether
     * or not the correct *number* of queries are coming out of the graph
     * loader.
     *
     */
    private static void print(GraphLoaderType graphLoader) {

    	Boolean moreQueries = true;
    	int queryNo = 0;
    	while (moreQueries) {
    		String nextQuery = graphLoader.getNextQuery();

    		if (nextQuery == null) {
    			logger.debug("Null query!"); //moreQueries = false;
    			moreQueries  = false;
    		} else {
        		logger.debug("Query No " + queryNo + ":\n" + nextQuery);
        		queryNo++;
    		}
    	}
    }

    /**
     * Checks that, after loading real data into the embedded database,
     * the expected number of nodes are present.
     *
     * @param graph
     */
    private static void testQueryOnData(EmbeddedGraphInstance graph) {
    	try ( Transaction tx = graph.beginTx() ) {
    		String query = "MATCH (n:LandCoverType {model_ID:\"AgroSuccess-dev\"}) RETURN n;";
    		Result res = graph.execute(query);
    		while (res.hasNext()) {
    			logger.debug(res.next());
    		}
    		tx.success();
    	}

    }


    public static void main(String[] args) {

    	boolean printLoadedQueries = false;
    	boolean loadData = false;

    	EmbeddedGraphInstance graph = new EmbeddedGraphInstance("output/databases/agrosuccess.db");

    	if (loadData) {
    		graph.insertExternalCypher(cypherRoot, fnameSuffix, globalParamFile);
    	}

    	if (printLoadedQueries) {
    		print(constructGraphLoader());
    	}

    	testQueryOnData(graph);
    	graph.shutdown();
    }
}
