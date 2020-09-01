package me.ajlane.geo.repast.succession.pathway.io.graph;

import static org.junit.Assert.assertEquals;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import me.ajlane.geo.repast.succession.pathway.LcsTransitionMapFactory;
import me.ajlane.geo.repast.succession.pathway.aliased.AliasedLcsTransitionMap;
import me.ajlane.geo.repast.succession.pathway.coded.CodedLcsTransitionMap;
import me.ajlane.geo.repast.succession.pathway.convert.AgroSuccessEnvrStateAliasTranslator;
import me.ajlane.geo.repast.succession.pathway.convert.EnvrStateAliasTranslator;
import me.ajlane.geo.repast.succession.pathway.io.graph.GraphBasedLcsTransitionMapFactory;
import me.ajlane.neo4j.EmbeddedGraphInstance;

/**
 * The number of transition rules in the graph, {@code numTransRulesInGraph} is the total number of
 * environmental transitions which are possible in the model. That is, every combination of start
 * and end state for which a specific combination of environmental conditions will permit a
 * transition from one to the other. The result should be equivalent to the following Cypher query,
 * where the parameter {@code model_ID="AgroSuccess-dev"}
 *
 * <pre>
 * MATCH (lct1:LandCoverType)<-[:SOURCE]-(t:SuccessionTrajectory)-[:TARGET]->(lct2:LandCoverType)
 * WHERE lct1.model_ID=$model_ID AND lct1.code<>lct2.code
 * WITH lct1, lct2, t
 * MATCH (e:EnvironCondition)-[:CAUSES]->(t)
 * RETURN count(e)
 * </pre>
 *
 * @see LcsGraphReaderTest
 */
public class GraphBasedLcsTransitionMapFactoryTest {

  private GraphDatabaseService graph;

  // TODO Depends on version of model. Should definitely be improved with proper test database
  private static String testDatabaseDirPath = "src/test/resources/databases/agrosuccess-test.db";
  private static String correctModelID = "AgroSuccess-dev";
  private static int numTransRulesInGraph = 489;

  @Before
  public void setUp() throws Exception {
    this.graph = new EmbeddedGraphInstance(testDatabaseDirPath);
  }

  @After
  public void tearDown() throws Exception {
    this.graph.shutdown();
    this.graph = null;
  }

  @Test
  public void retrievesAliasedLcsTransitionMapFromGraph() {
    LcsTransitionMapFactory fac = new GraphBasedLcsTransitionMapFactory(graph);
    assertEquals(fac.getAliasedLcsTransitionMap().getClass(), AliasedLcsTransitionMap.class);
  }

  @Test
  public void throwsExceptionIfCodedMapRequestedWhenTranslatorNotProvided() {
    System.out.println("Output from "
        + "GraphBasedLcsTransitionMapFactoryTest.throwsExceptionIfCodedMapRequestedWhenTranslatorNotProvided:");
    LcsTransitionMapFactory fac = new GraphBasedLcsTransitionMapFactory(graph);
    try {
      fac.getCodedLcsTransitionMap();
    } catch (Exception e) {
      assertEquals(e.getClass(), NullPointerException.class);
    }
  }

  @Test
  public void retrievesCodedLcsTransitionMapFromGraph() {
    EnvrStateAliasTranslator translator = new AgroSuccessEnvrStateAliasTranslator();
    LcsTransitionMapFactory fac = new GraphBasedLcsTransitionMapFactory(graph, translator);
    assertEquals(fac.getCodedLcsTransitionMap().getClass(), CodedLcsTransitionMap.class);
  }

  @Test
  public void shouldBeAbleToSpecifyAModelId() {
    LcsTransitionMapFactory fac = new GraphBasedLcsTransitionMapFactory(graph, correctModelID);
    assertEquals(fac.getAliasedLcsTransitionMap().getClass(), AliasedLcsTransitionMap.class);
  }

  @Test
  public void shouldBeAbleToSpecifyAModelIdAndTranslator() {
    EnvrStateAliasTranslator translator = new AgroSuccessEnvrStateAliasTranslator();
    LcsTransitionMapFactory fac =
        new GraphBasedLcsTransitionMapFactory(graph, correctModelID, translator);
    assertEquals(fac.getCodedLcsTransitionMap().getClass(), CodedLcsTransitionMap.class);
  }

  @Test
  public void shouldHaveCorrectNumberOfRulesInAliasedMap() {
    LcsTransitionMapFactory fac = new GraphBasedLcsTransitionMapFactory(graph, correctModelID);
    AliasedLcsTransitionMap aliasedMap = fac.getAliasedLcsTransitionMap();
    assertEquals(aliasedMap.size(), numTransRulesInGraph);
  }

  @Test
  public void shouldHaveCorrectNumberOfRulesInCodedMap() {
    EnvrStateAliasTranslator translator = new AgroSuccessEnvrStateAliasTranslator();
    LcsTransitionMapFactory fac =
        new GraphBasedLcsTransitionMapFactory(graph, correctModelID, translator);
    CodedLcsTransitionMap codedMap = fac.getCodedLcsTransitionMap();
    assertEquals(codedMap.size(), numTransRulesInGraph);
  }

}
