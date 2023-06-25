package me.ajlane.geo.repast.succession.pathway.io.graph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import me.ajlane.geo.repast.succession.pathway.aliased.AliasedEnvrAntecedent;
import me.ajlane.geo.repast.succession.pathway.aliased.AliasedEnvrConsequent;
import me.ajlane.geo.repast.succession.pathway.aliased.AliasedLcsTransitionMap;
import me.ajlane.neo4j.EmbeddedGraphInstance;

public class LcsGraphReaderTest {
  private static String testDatabaseDirPath = "src/test/resources/databases/agrosuccess-test.db";
  private static String correctModelID = "AgroSuccess-dev";
  public static EmbeddedGraphInstance graph;

  @Before
  public void setUp() {
    graph = new EmbeddedGraphInstance(testDatabaseDirPath);
  }

  @After
  public void tearDown() {
    graph.shutdown();
  }

  @Test
  public void shouldBeAbleToConnectToDatabase() {
    assertTrue(LcsGraphReader.read(graph) instanceof AliasedLcsTransitionMap);
  }

  /**
   * The number of records is the total number of environmental transitions which are possible in
   * the model. That is, every combination of start and end state for which a specific combination
   * of environmental conditions will permit a transition from one to the other. The result should
   * be equivalent to the following Cypher query, where the parameter
   * {@code model_ID="AgroSuccess-dev"}
   *
   * <pre>
   * MATCH (lct1:LandCoverType)<-[:SOURCE]-(t:SuccessionTrajectory)-[:TARGET]->(lct2:LandCoverType)
   * WHERE lct1.model_ID=$model_ID AND lct1.code<>lct2.code
   * WITH lct1, lct2, t
   * MATCH (e:EnvironCondition)-[:CAUSES]->(t)
   * RETURN count(e)
   * </pre>
   */
  @Test
  public void shouldFindExpectedNumberOfPossibleTransitions() {
    AliasedLcsTransitionMap map = LcsGraphReader.read(graph);
    assertEquals(572, map.size());
  }

  @Test
  public void shouldMatchCorrectConsequent1() {
    AliasedEnvrAntecedent testAnte =
        new AliasedEnvrAntecedent("Oak", "regeneration", "south", "true", "true", "true", "xeric");

    AliasedEnvrConsequent expectedCons = new AliasedEnvrConsequent("TransForest", 30);

    AliasedLcsTransitionMap map = LcsGraphReader.read(graph);

    AliasedEnvrConsequent actualCons = map.getEnvrConsequent(testAnte);
    // System.out.println(actualCons.toString());
    assertEquals(expectedCons, actualCons);
  }

  @Test
  public void shouldWorkWhenCorrectModelIDIsUsed() {
    AliasedLcsTransitionMap map = LcsGraphReader.read(graph, correctModelID);
    assertEquals(572, map.size());
  }

  @Test
  public void shouldWorkWithWarningWhenWrongModelIDIsUsed() {
    AliasedLcsTransitionMap map = LcsGraphReader.read(graph, "blah");
    assertEquals(0, map.size());
  }
}
