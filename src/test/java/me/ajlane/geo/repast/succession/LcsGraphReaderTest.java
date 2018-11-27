package me.ajlane.geo.repast.succession;

import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;
import me.ajlane.neo4j.EmbeddedGraphInstance;

public class LcsGraphReaderTest {
  private static String testDatabaseDirPath = "src/test/resources/databases/agrosuccess.db";
  private static String correctModelID = "AgroSuccess-dev";
  public static EmbeddedGraphInstance graph;
  
  @BeforeClass
  public static void setUpBeforeClass() {
    graph = new EmbeddedGraphInstance(testDatabaseDirPath);
  }

  @Test
  public void shouldBeAbleToConnectToDatabase() {
    assertTrue(LcsGraphReader.read(graph) instanceof AliasedLcsTransitionMap);
  }
  
  @Test
  public void shouldFind537Records() {
    AliasedLcsTransitionMap map = LcsGraphReader.read(graph);    
    assertEquals(537, map.size());
  }  
  
  @Test
  public void shouldMatchCorrectConsequent1() {
    AliasedEnvrAntecedent testAnte =
        new AliasedEnvrAntecedent("Oak", "regeneration", "north", "true", "false", "true", "xeric");
    
    AliasedEnvrConsequent expectedCons = new AliasedEnvrConsequent("Pine", 25);
    
    AliasedLcsTransitionMap map = LcsGraphReader.read(graph);
    
    AliasedEnvrConsequent actualCons = map.getEnvrConsequent(testAnte);
    // System.out.println(actualCons.toString());
    assertEquals(expectedCons, actualCons);
  }  
  
  @Test 
  public void shouldWorkWhenCorrectModelIDIsUsed() {
    AliasedLcsTransitionMap map = LcsGraphReader.read(graph, correctModelID); 
    assertEquals(537, map.size());
  }
  
  @Test 
  public void shouldWorkWithWarningWhenWrongModelIDIsUsed() {
    AliasedLcsTransitionMap map = LcsGraphReader.read(graph, "blah"); 
    assertEquals(0, map.size());
  }
}
