package me.ajlane.neo4j;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;

import me.ajlane.neo4j.GraphLoaderFactory;
import me.ajlane.neo4j.GraphLoaderType;

public class GraphLoadingTester {

    private static void print(GraphLoaderType graphLoader) {

    	Boolean moreQueries = true;   
    	int queryNo = 0;
    	while (moreQueries) {    		
    		String nextQuery = graphLoader.getNextQuery();
    		
    		if (nextQuery == null) {
    			System.out.println("Null query!"); //moreQueries = false;
    			moreQueries  = false;
    		} else {
        		System.out.println("Query No " + queryNo + ":\n" + nextQuery);   
        		queryNo++;
    		} 	
    				
    	}
        
    }
    
    private static void testQuery(EmbeddedGraphInstance graph) {
    	try ( Transaction tx = graph.beginTx() ) {
    		String query = "MATCH (n:LandCoverType {model_ID:\"AgroSuccess-dev\"}) RETURN n;";
    		//String queryIn = "MERGE (n:Test {message:\"Hey!\"});";
    		//String queryOut = "MATCH (n:Test) RETURN n;";
    		
    		//Result res = graph.execute(queryIn);
    		Result res = graph.execute(query);
    		while (res.hasNext()) {
    			System.out.println(res.next());
    		}
    		tx.success();
    	}
    	
    }


    public static void main(String[] args) {
    	
    	String projectRoot = "/home/andrew/AgroSuccess/";
    	String cypherRoot = projectRoot + "views";
    	String fnameSuffix = "_w";
    	String globalParamFile = projectRoot + "global_parameters.json";    			
    	
    	//GraphLoaderFactory factory = new GraphLoaderFactory();
    	//GraphLoaderType graphLoader = factory.create(cypherRoot, fnameSuffix, 
    	//										globalParamFile);
    	
    	EmbeddedGraphInstance graph = new EmbeddedGraphInstance("output/databases/agrosuccess.db"); 
    	//graph.insertExternalCypher(cypherRoot, fnameSuffix, globalParamFile);
    	//print(graphLoader);
    	testQuery(graph);
    	graph.shutdown();
    	
    }
}