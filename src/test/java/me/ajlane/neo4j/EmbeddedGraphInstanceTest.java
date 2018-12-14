package me.ajlane.neo4j;
import static org.junit.Assert.*;

import static java.lang.Math.toIntExact;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;

public class EmbeddedGraphInstanceTest {
	
	public static EmbeddedGraphInstance graph;
	private static String testDatabaseDirPath = "src/test/resources/databases/agrosuccess.db"; 
	
	@Before
	public void setUp() {
		graph = new EmbeddedGraphInstance(testDatabaseDirPath);
	}
	
	@After
	public void tearDown() {
		try (Transaction tx = graph.beginTx()) {
			graph.execute("MATCH (n:Test) DETACH DELETE n;");	
			tx.success();
			
		}
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
	
	@Test
	public void AgroSuccessShouldHave10LandCoverTypes() {
		int numLCT;
		try (Transaction tx = graph.beginTx()) {
			Result result = graph.execute(
					"MATCH (n:LandCoverType) RETURN count(n) AS num_lct");
			//System.out.println(result.resultAsString());
			numLCT = toIntExact((long)result.next().get("num_lct"));
			tx.success();
		}
		assertEquals(10, numLCT);		
	}
	
	@Test
	public void AgroSuccessShouldHave18EcoEngineeringActivites() {
		int numEEA;
		try (Transaction tx = graph.beginTx()) {
			Result result = graph.execute(
					"MATCH (n:EcoEngineeringActivity) RETURN count(n) AS num_eea");
			numEEA = toIntExact((long)result.next().get("num_eea"));
			result.close();
			tx.success();
		}
		assertEquals(18, numEEA);		
	}
}
