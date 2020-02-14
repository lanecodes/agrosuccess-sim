package me.ajlane.geo.repast.fire;

import static org.junit.Assert.*;
import java.io.File;
import java.util.Map;
import org.junit.Ignore;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import me.ajlane.neo4j.EmbeddedGraphInstance;
import repast.model.agrosuccess.AgroSuccessCodeAliases.Lct;

public class LcfMapGetterFromGraphTest {

  private static final double TOLERANCE = 0.0001;
  private static final File graphDir = new File("/home/andrew/graphs/databases/test.db");
  private static final GraphDatabaseService graph =
      new EmbeddedGraphInstance(graphDir.getAbsolutePath());

  /**
   * Test method {@code getMap}.
   * <dl>
   * <dt>TODO:</dt>
   * <dd>
   * <ul>
   * <li>Remove ignore annotation when appropriate method implemented in
   * {@code LcfMapGetterFromGraph}</li>
   * </ul>
   * </dd>
   * </dl>
   * <
   */
  @Ignore
  @Test
  public void testGraphData() {
    LcfMapGetter defaultGetter = new LcfMapGetterFromGraph(LcfReplicate.Default, graph);
    Map<Lct, Double> defaultLcfMap = defaultGetter.getMap();
    assertEquals(0.23, defaultLcfMap.get(Lct.Pine), TOLERANCE);
    assertEquals(0.22, defaultLcfMap.get(Lct.Deciduous), TOLERANCE);

    LcfMapGetter tf4Getter = new LcfMapGetterFromGraph(LcfReplicate.TF4, graph);
    Map<Lct, Double> tf4LcfMap = tf4Getter.getMap();
    assertEquals(0.22, tf4LcfMap.get(Lct.TransForest), TOLERANCE);
    assertEquals(0.21, tf4LcfMap.get(Lct.Shrubland), TOLERANCE);
  }

}
