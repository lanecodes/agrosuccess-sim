# loadcypher

## Using Neo4j driver

Start off by using the Neo4j driver for Java to query a database running on a locally hosted Neo4j server from within a Java application. See examples [here](https://neo4j.com/developer/java/)

### TODOs 
1. Create array of land cover types in database. Loop through and print out land cover conversion costs 
2. Review Repast code, think about how to integrate graph into agent decision making
3. Read global parameters (e.g. Initial no agents, model ID etc) from database into Repast

## ENHANCEMENT Embedding Neo4j into a Java application

Eventually I will want to embed the functionality of `cymod` into a Repast application so that a simulation can be pointed to a collection of `.cql` views which are automatically ingested into an _embedded_ Neo4j database. This will make it possible to run these simulations on machines which don't have a Neo4j server installed on them. This will involve adding Neo4j itself as a [Maven dependency](https://neo4j.com/docs/java-reference/current/tutorials-java-embedded/#_maven) 

To help with this, see the embedded Neo4j database [tutorial](https://neo4j.com/docs/java-reference/current/tutorials-java-embedded/) (especially the [source code](https://github.com/neo4j/neo4j-documentation/blob/3.4/embedded-examples/src/main/java/org/neo4j/examples/EmbeddedNeo4j.java)), and this [trick](https://github.com/technige/neo4j-jython-example) for using Neo4j embedded from Jython.



