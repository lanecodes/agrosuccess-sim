package me.ajlane.geo.repast.succession;

import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;
import static java.lang.Math.toIntExact;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.GraphDatabaseService;

public class LcsGraphReader {

  final static Logger logger = Logger.getLogger(LcsGraphReader.class);

  /**
   * @param modelID The identifier to be used to identify the nodes belonging to the desired model
   *        stored within the graph passed to the {@code read} methods.
   * @return String specifying the Cypher query which will be run against the database. If a
   *         parameter is required to accommodate the modelID this is accounted for.
   */
  private static String getLandCoverTrantisionQuery(String modelID) {
    String whereClause;
    if (modelID == null) {
      whereClause = "WHERE lct1.code<>lct2.code ";
    } else {
      whereClause = "WHERE lct1.model_ID=$model_ID AND lct1.code<>lct2.code ";
    }

    return "MATCH (lct1:LandCoverType)<-[:SOURCE]-(t:SuccessionTrajectory)-[:TARGET]->(lct2:LandCoverType) "
        + whereClause + "WITH lct1, lct2, t MATCH (e:EnvironCondition)-[:CAUSES]->(t) "
        + "RETURN lct1.code as start_code, e.succession as succession, e.aspect as aspect,"
        + "e.pine as pine, e.oak as oak, e.deciduous as deciduous, e.water as water, "
        + "lct2.code as end_code, e.delta_t as delta_t; ";
  }

  /**
   * Convert data from database stream into a native data structure: the AliasedLcsTransitionMap
   *
   * @param res Neo4j result stream object. This needs to be consumed before completing the database
   *        transaction. It is expected that the calling method handles this, here we only process
   *        the incoming stream.
   * @return a map representation of the returned data from the graph
   */
  private static AliasedLcsTransitionMap processDbQueryResult(Result res) {

    AliasedLcsTransitionMap transMap = new AliasedLcsTransitionMap();

    while (res.hasNext()) {
      Map<String, Object> transData = res.next();

      AliasedEnvrAntecedent ante = new AliasedEnvrAntecedent(transData.get("start_code").toString(),
          transData.get("succession").toString(), transData.get("aspect").toString(),
          transData.get("pine").toString(), transData.get("oak").toString(),
          transData.get("deciduous").toString(), transData.get("water").toString());

      AliasedEnvrConsequent cons = new AliasedEnvrConsequent(transData.get("end_code").toString(),
          toIntExact((long) transData.get("delta_t")));

      transMap.put(ante, cons);

    }
    return transMap;
  }


  /**
   *
   * @param graph Graph database where land cover state model is stored
   * @param modelID The identifier to be used to identify the nodes belonging to the desired model
   *        stored within the graph
   * @return Object mapping combinations of environmental conditions to outcome trajectories
   */
  public static AliasedLcsTransitionMap read(GraphDatabaseService graph, String modelID) {
    String landCoverTransitionQuery = getLandCoverTrantisionQuery(modelID);
    Result landCoverTransitionResults;
    try (Transaction tx = graph.beginTx()) {
      if (modelID == null) {
        landCoverTransitionResults = graph.execute(landCoverTransitionQuery);
      } else {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("model_ID", modelID);
        landCoverTransitionResults = graph.execute(landCoverTransitionQuery, params);
      }
      AliasedLcsTransitionMap transMap = processDbQueryResult(landCoverTransitionResults);
      tx.success();

      if (transMap.size() == 0) {
        logger.warn("0 land cover transition rules found in graph database.");
      }
      return transMap;
    }
  }

  /**
   *
   * @param graph Graph database where land cover state model is stored
   * @return Object mapping combinations of environmental conditions to outcome trajectories
   */
  public static AliasedLcsTransitionMap read(GraphDatabaseService graph) {
    return read(graph, null);
  }

}
