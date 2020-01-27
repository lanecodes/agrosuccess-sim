package me.ajlane.geo.repast.succession;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import me.ajlane.neo4j.EmbeddedGraphInstance;

public class GraphBasedLcsTransitionMapFactoryTest {
  
  private GraphDatabaseService graph;  
  
  // TODO Depends on version of model. Should definitely be improved with proper test database  
  private static String testDatabaseDirPath = "src/test/resources/databases/agrosuccess-test.db";
  private static String correctModelID = "AgroSuccess-dev";
  private static int numTransRulesInGraph = 537;   

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
    System.out.println("Output from " + 
        "GraphBasedLcsTransitionMapFactoryTest.throwsExceptionIfCodedMapRequestedWhenTranslatorNotProvided:");
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
    LcsTransitionMapFactory fac = new GraphBasedLcsTransitionMapFactory(graph, correctModelID, 
        translator);
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
    LcsTransitionMapFactory fac = new GraphBasedLcsTransitionMapFactory(graph, correctModelID, 
        translator);
    CodedLcsTransitionMap codedMap = fac.getCodedLcsTransitionMap();
    assertEquals(codedMap.size(), numTransRulesInGraph);
  }

}
