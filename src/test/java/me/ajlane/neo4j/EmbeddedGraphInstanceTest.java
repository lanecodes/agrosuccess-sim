package me.ajlane.neo4j;
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;

public class EmbeddedGraphInstanceTest {
	
	public EmbeddedGraphInstance graph;
	
	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();
	
	@Before
	public void setUp() throws Exception {
		graph = new EmbeddedGraphInstance(tempFolder.getRoot().getPath());	
	}

	@After
	public void tearDown() throws Exception {
		graph.shutdown();
	}

	@Test
	public void threeNodesShouldBeCreated() {
    	try ( Transaction tx = graph.beginTx() ) {
    		String queryIn = "MERGE (:Test {number:1})" +
    						 "MERGE (:Test {number:2})" +
    						 "MERGE (:Test {number:3});";
    		
    		String queryOut = "MATCH (n:Test) RETURN n";
    		
    		graph.execute(queryIn);
    		Result result = graph.execute(queryOut);
    		int resultCount = 0;    		
    		while (result.hasNext()) {
    			result.next();
    			resultCount++;
    			}
    		tx.success();
    		assertEquals(3, resultCount);
    	}
	}
	
	//public void dataShouldNotBeDuplicated() {
		// this is to test that running insertExternalCypher()
		// multiple times shouldn't produce duplicate data
	//}

}
