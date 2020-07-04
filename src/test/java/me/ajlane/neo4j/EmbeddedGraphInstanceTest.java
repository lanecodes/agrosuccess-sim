package me.ajlane.neo4j;
import static java.lang.Math.toIntExact;
import static org.junit.Assert.assertEquals;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;

public class EmbeddedGraphInstanceTest {

	public static EmbeddedGraphInstance graph;
	private static String testDatabaseDirPath = "src/test/resources/databases/agrosuccess-test.db";

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
	public void AgroSuccessShouldHave9LandCoverTypes() {
		int numLCT;
		try (Transaction tx = graph.beginTx()) {
			Result result = graph.execute(
					"MATCH (n:LandCoverType) RETURN count(n) AS num_lct");
			numLCT = toIntExact((long)result.next().get("num_lct"));
			tx.success();
		}
		assertEquals(9, numLCT);
	}

	@Test
	public void AgroSuccessShouldHave8EcoEngineeringActivites() {
		int numEEA;
		try (Transaction tx = graph.beginTx()) {
			Result result = graph.execute(
					"MATCH (n:EcoEngineeringActivity) RETURN count(n) AS num_eea");
			numEEA = toIntExact((long)result.next().get("num_eea"));
			result.close();
			tx.success();
		}
		assertEquals(8, numEEA);
	}
}
