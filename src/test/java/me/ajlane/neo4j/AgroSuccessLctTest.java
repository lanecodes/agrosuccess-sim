package me.ajlane.neo4j;

import static org.junit.Assert.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;

public class AgroSuccessLctTest {
  
  private static File graphDir = new File("/home/andrew/graphs/databases/test.db");
  
  private static List<AgroSuccessLct> getLcts() {
    GraphDatabaseService graph = new EmbeddedGraphInstance(graphDir.getAbsolutePath());
    List<AgroSuccessLct> lctNodes = new ArrayList<AgroSuccessLct>();
    Label label = Label.label("LandCoverType");
    try (Transaction tx = graph.beginTx()) {
      try (ResourceIterator<Node> lcts = graph.findNodes(label)) {        
        while (lcts.hasNext()) {
          lctNodes.add(new AgroSuccessLct(lcts.next()));
        }
      }
    }
    graph.shutdown();
    return lctNodes;
  }

  @Test
  public void shouldFind10LandCoverTypesInGraph() {
    // confirms 
    assertEquals(10, getLcts().size());
  }

}
