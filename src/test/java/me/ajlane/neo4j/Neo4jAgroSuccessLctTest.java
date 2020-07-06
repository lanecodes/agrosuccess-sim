package me.ajlane.neo4j;

import static org.junit.Assert.assertEquals;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;

public class Neo4jAgroSuccessLctTest {

  private static File graphDir = new File("src/test/resources/databases/agrosuccess-test.db");

  private static List<Neo4jAgroSuccessLct> getLcts() {
    GraphDatabaseService graph = new EmbeddedGraphInstance(graphDir.getAbsolutePath());
    List<Neo4jAgroSuccessLct> lctNodes = new ArrayList<Neo4jAgroSuccessLct>();
    Label label = Label.label("LandCoverType");
    try (Transaction tx = graph.beginTx()) {
      try (ResourceIterator<Node> lcts = graph.findNodes(label)) {
        while (lcts.hasNext()) {
          lctNodes.add(new Neo4jAgroSuccessLct(lcts.next()));
        }
      }
    }
    graph.shutdown();
    return lctNodes;
  }

  @Test
  public void shouldFind9LandCoverTypesInGraph() {
    // confirms
    for (Neo4jAgroSuccessLct lct : getLcts()) {
      System.out.println(lct.getCode());
    }
    assertEquals(9, getLcts().size());
  }

}
