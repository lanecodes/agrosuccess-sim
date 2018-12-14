package me.ajlane.geo.repast.succession;

import org.neo4j.graphdb.GraphDatabaseService;

public class GraphBasedLcsTransitionMapFactory implements LcsTransitionMapFactory {
  GraphDatabaseService graph;
  String modelID;
  LcsTransitionMapConverter converter;
  AliasedLcsTransitionMap aliasedMap;
  
  GraphBasedLcsTransitionMapFactory (GraphDatabaseService graph, String modelID,
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
      System.out.println("Could not convert aliases to codes as no EnvrStateAliasTranslator "
          + "specified in GraphBasedLcsTransitionMapFactory constructor.");
      throw e;
    }
  }

}
