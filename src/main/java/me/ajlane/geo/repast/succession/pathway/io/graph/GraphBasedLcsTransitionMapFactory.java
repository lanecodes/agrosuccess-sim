package me.ajlane.geo.repast.succession.pathway.io.graph;

import org.apache.log4j.Logger;
import org.neo4j.graphdb.GraphDatabaseService;
import me.ajlane.geo.repast.succession.pathway.LcsTransitionMapFactory;
import me.ajlane.geo.repast.succession.pathway.aliased.AliasedLcsTransitionMap;
import me.ajlane.geo.repast.succession.pathway.coded.CodedLcsTransitionMap;
import me.ajlane.geo.repast.succession.pathway.convert.EnvrStateAliasTranslator;
import me.ajlane.geo.repast.succession.pathway.convert.LcsTransitionMapConverter;

public class GraphBasedLcsTransitionMapFactory implements LcsTransitionMapFactory {

  final static Logger logger = Logger.getLogger(GraphBasedLcsTransitionMapFactory.class);

  GraphDatabaseService graph;
  String modelID;
  LcsTransitionMapConverter converter;
  AliasedLcsTransitionMap aliasedMap;

  public GraphBasedLcsTransitionMapFactory (GraphDatabaseService graph, String modelID,
      EnvrStateAliasTranslator translator) {
    this.graph = graph;
    this.modelID = modelID;
    this.converter = new LcsTransitionMapConverter(translator);
  }

  GraphBasedLcsTransitionMapFactory (GraphDatabaseService graph,
      EnvrStateAliasTranslator translator) {
    this(graph, null, translator);
  }

  GraphBasedLcsTransitionMapFactory(GraphDatabaseService graph) {
    this(graph, null, null);
  }

  GraphBasedLcsTransitionMapFactory(GraphDatabaseService graph, String modelID) {
    this(graph, modelID, null);
  }

  @Override
  public AliasedLcsTransitionMap getAliasedLcsTransitionMap() {
    if (aliasedMap == null) {
      aliasedMap = LcsGraphReader.read(graph, modelID);
    }
    return aliasedMap;
  }

  @Override
  public CodedLcsTransitionMap getCodedLcsTransitionMap() {
    try {
    return converter.convert(getAliasedLcsTransitionMap());
    } catch (NullPointerException e) {
      logger.error("Could not convert aliases to codes as no EnvrStateAliasTranslator "
                   + "specified in GraphBasedLcsTransitionMapFactory constructor.", e);
      throw e;
    }
  }

}
