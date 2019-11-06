# AgroSuccess


## Documentation
User documentation should be written in `/src/site/markdown` and built by
[running][Maven site]

```bash
mvn site
```
Note that this requires the `maven-site-plugin` plugin specified in `pom.xml`.
This will generate a user documentation site in `target/site/`.

Developer documentation is generated directly from source using `javadoc`. The
generated site can be found in `site`.

[Maven site]: https://maven.apache.org/guides/mini/guide-site.html

## Dependencies
- Dependencies are managed using Maven following [instructions][maven integration] 
  found on the Repast mailing list.
- See `</dependencies>` within `pom.xml` for details of which dependencies are
  installed.
- Note that while we use Maven to install dependencies which Repase can subsequently
  link to, we don't use Maven to build. For that we rely on Repast.

[maven integration]: https://sourceforge.net/p/repast/mailman/message/35615878/

## Build
- ??? TODO
- Use Repast's model installer to [export the Repast model] as a jar.

[export the Repast model]: https://stackoverflow.com/questions/45871020/

## Data

###  Model inputs/ boundary conditions
- Modified Random clusters analysis done with /home/andrew/Documents/codes/python/notebooks/modified_random_clusters
- Files stored in
  /home/andrew/Documents/codes/java/Repast/AgroSuccess/data/geographic/<site_name>

### Control parameters
- Path to graph database
- Path to Geographic data

### Model parameters
???

## loadcypher

### Using Neo4j driver

Start off by using the Neo4j driver for Java to query a database running on a locally hosted Neo4j server from within a Java application. See examples [here](https://neo4j.com/developer/java/)

#### TODOs 
1. Create array of land cover types in database. Loop through and print out land cover conversion costs 
2. Review Repast code, think about how to integrate graph into agent decision making
3. Read global parameters (e.g. Initial no agents, model ID etc) from database into Repast

### ENHANCEMENT Embedding Neo4j into a Java application

Eventually I will want to embed the functionality of `cymod` into a Repast application so that a simulation can be pointed to a collection of `.cql` views which are automatically ingested into an _embedded_ Neo4j database. This will make it possible to run these simulations on machines which don't have a Neo4j server installed on them. This will involve adding Neo4j itself as a [Maven dependency](https://neo4j.com/docs/java-reference/current/tutorials-java-embedded/#_maven) 

To help with this, see the embedded Neo4j database [tutorial](https://neo4j.com/docs/java-reference/current/tutorials-java-embedded/) (especially the [source code](https://github.com/neo4j/neo4j-documentation/blob/3.4/embedded-examples/src/main/java/org/neo4j/examples/EmbeddedNeo4j.java)), and this [trick](https://github.com/technige/neo4j-jython-example) for using Neo4j embedded from Jython.


