package me.ajlane.geo.repast.fire;

import java.util.Map;
import org.neo4j.graphdb.GraphDatabaseService;
import repast.model.agrosuccess.AgroSuccessCodeAliases.Lct;

/**
 * Get land cover flammability data from graph database.
 *
 * @see me.ajlane.geo.repast.fire.LcfMapGetter
 *
 * @author Andrew Lane
 *
 */
public class LcfMapGetterFromGraph implements LcfMapGetter {

  LcfReplicate replicate;
  GraphDatabaseService graph;

  public LcfMapGetterFromGraph(LcfReplicate replicate, GraphDatabaseService graph) {
    this.replicate = replicate;
    this.graph = graph;
  }

  public Map<Lct, Double> getMap() {
    throw new UnsupportedOperationException(
        "Generation of LCF Map using data from graph database not yet implemented.");
  }

}
