package me.ajlane.geo.repast.succession.pathway.coded;

import static java.lang.Math.toIntExact;
import java.util.HashMap;
import java.util.Map;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;
import me.ajlane.geo.repast.succession.pathway.EnvrAntecedent;
import me.ajlane.geo.repast.succession.pathway.EnvrConsequent;
import me.ajlane.geo.repast.succession.pathway.convert.EnvrStateAliasTranslator;
import me.ajlane.neo4j.EmbeddedGraphInstance;

/**
 *
 * @author Andrew Lane
 */
public class CodedLcsTransitionMapOld extends
    HashMap<EnvrAntecedent<Integer, Integer, Integer, Integer, Integer, Integer, Integer>, EnvrConsequent<Integer, Integer>> {

  private static final long serialVersionUID = 4682382255681752353L;

  CodedLcsTransitionMapOld(EmbeddedGraphInstance graphDatabase,
      EnvrStateAliasTranslator envStateAliasTranslator, String modelID) {

    setMapFromGraph(graphDatabase, modelID, envStateAliasTranslator);

  }

  CodedLcsTransitionMapOld() {
    // Do nothing, assume data provided by some means other than a graph database connection.
  }

  /**
   * @param graph An established connection to a running EmbeddedGraphInstance database containing
   *        succession pathway data.
   * @param modelID The identifier for the succession model in the graph database. Only nodes with
   *        value "model_ID"=modelID will be included.
   * @param envStateAliasTranslator Container holding data for converting between a human readable
   *        database value for an environmental variable to a coded value for use in simulations.
   */
  protected void setMapFromGraph(EmbeddedGraphInstance graph, String modelID,
      EnvrStateAliasTranslator envStateAliasTranslator) {

    HashMap<String, Object> params = new HashMap<String, Object>();
    params.put("model_ID", modelID);

    String landCoverTransitionQuery =
        "MATCH (lct1:LandCoverType)<-[:SOURCE]-(t:SuccessionTrajectory)-[:TARGET]->(lct2:LandCoverType) "
            + "WHERE lct1. AND lct1.code<>lct2.code "
            + "WITH lct1, lct2, t MATCH (e:EnvironCondition)-[:CAUSES]->(t) "
            + "RETURN lct1.code as start_code, e.succession as succession, e.aspect as aspect,"
            + "e.pine as pine, e.oak as oak, e.deciduous as deciduous, e.water as water, "
            + "lct2.code as end_code, e.delta_t as delta_t; ";

    try (Transaction tx = graph.beginTx()) {
      Result landCoverTransitionResults = graph.execute(landCoverTransitionQuery, params);
      while (landCoverTransitionResults.hasNext()) {
        Map<String, Object> transData = landCoverTransitionResults.next();

        EnvrAntecedent<Integer, Integer, Integer, Integer, Integer, Integer, Integer> envAntecedent =
            new EnvrAntecedent<>(
                envStateAliasTranslator.numericalValueFromAlias("landCoverState",
                    transData.get("start_code").toString()),
                envStateAliasTranslator.numericalValueFromAlias("succession",
                    transData.get("succession").toString()),
                envStateAliasTranslator.numericalValueFromAlias("aspect",
                    transData.get("aspect").toString()),
                envStateAliasTranslator.numericalValueFromAlias("seedPresence",
                    transData.get("pine").toString()),
                envStateAliasTranslator.numericalValueFromAlias("seedPresence",
                    transData.get("oak").toString()),
                envStateAliasTranslator.numericalValueFromAlias("seedPresence",
                    transData.get("deciduous").toString()),
                envStateAliasTranslator.numericalValueFromAlias("water",
                    transData.get("water").toString()));

        EnvrConsequent<Integer, Integer> envConsequent =
            new EnvrConsequent<>(
                envStateAliasTranslator.numericalValueFromAlias("landCoverState",
                    transData.get("end_code").toString()),
                toIntExact((long) transData.get("delta_t")));

        put(envAntecedent, envConsequent);
        tx.success();
      }
    }
  }
}
