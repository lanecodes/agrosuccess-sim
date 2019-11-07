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

[maven integration]: https://sourceforge.net/p/repast/mailman/message/35615878/#msg35615878

## Build
Setup Python environment

```bash
conda env create -f agrosuccess_env.yml
```
- [Download Maven dependencies][mvn dependencies] with `mvn dependency:copy-dependencies dependency:resolve-plugins clean`
- ??? TODO
- Use Repast's model installer to [export the Repast model] as a jar.

[mvn dependencies]: https://stackoverflow.com/questions/34203179
[export the Repast model]: https://stackoverflow.com/questions/45871020/

## Repository layout

```
.
├── AgroSuccess.rs
│  ├── context.xml            <- Specify model context hierarchy and projections [1]
│  ├── parameters.xml         <- Stores model configuration parameters used by GUI [1]
│  ├── scenario.xml           <- Specify model run scenarios
│  └── user_path.xml          <- Configure where to look for model's compiled Java classes
├── batch                      <- Configuration for batch run within Repast Simphony
├── data                       <- Model data for Repast Simphony to copy into model jars
│  ├── graph-store            <- Neo4j graph store files [2]
│  ├── study-sites            <- Bio-geographic/ morphological study site data
│  └── test                   <- Data for test cases
├── data_src                   <- Scripts used to obtain and prepare data for simulation
├── docs                       <- Defunct, see `target/site` instead
├── freezedried_data           <- Used for storing intermediate model results (???)
├── icons                      <- Model icon files
├── installer                  <- Files needed to generate installer for model
├── integration                <- Repast Simphony data file description files (???)
├── launchers                  <- Repast Simphony Eclipse launcher configuration
├── lib                        <- External Java library files made available to Repast (jars)
├── license.txt
├── model_description.txt
├── output
│  ├── csv                    <- CSV files containing simulation results
│  └── graph-store            <- Neo4j graph store containing simulation results
├── repast-licenses
├── scripts                   <- Miscellaneous scripts used during development
├── src
│  ├── main                   <- Java classes specifying model
│  ├── site                   <- Model documentation files [3]
│  └── test                   <- Unit test code for Java classes
└── target
   ├── classes                <- Compiled Java classes for model
   └── site                   <- User documentation for AgroSuccess model


Footnotes
---------
 [1]: See `target/site/model-config.html` for more details.
 [2]: This should be populated with includes land cover transition rules before
      the simulation is run. It will be updated with model outputs during the
      course of the simulation.
 [3]: Used by `mvn site` to generate a website documenting the model which can
      be found at `target/site`.

```

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


## TODO

### Priority

- Look at a demo model and describe in `model-config.md` my understanding of
  the difference in function between `AgroSuccess.rs/context.xml` and
  `AgroSuccess.rs/parameters.xml`. I suspect `context.xml` is more closely
  related to the GUI, whereas `parameters.xml` is used by the
  `ContextBuilder`.
- Remove attribute with `id=geoDataDir` from `AgroSuccess.rs/context.xml`. A
  search of source code on 2019-11-07 showed this doesn't seem to be used
  anywhere in the model code, and was presumably introduced when I was
  experimenting prior to introducing `geoDataDirRootString` to
  `AgroSuccess.rs/parameters.xml`. Test that removing `geoDataDir` doesn't
  break anything when running Repast Simphony GUI.
- Remove attribute with `id=studySite` from `AgroSuccess.rs/context.xml`. This
  has been superceded by `studySite` in `AgroSuccess.rs/parameters.xml`.
- Rebuild the scenarios specified in `AgroSuccess.rs/scenario.xml`,
  `AgroSuccess.rs/repast.simphony.action.display_1.xml`, and
  `AgroSuccess.rs/repast.simphony.action.display_2.xml`. These were created
  using Repast version 2.6 and may not work properly with 2.7.
- Remove the parameters `configCypherFilesDir`, `cypherFileSuffix`,
  `graphDatabaseGloablParamFile`. These relate to processing
- Add `data-src` directory to model to hold source data. This differs from
  `data` which holds data ready to be consumed by the simulation.


### Data

- Move logic from 
  `~/Documents/codes/python/notebooks/download_site_elevation_data/get_elev/download_site_elevation_data.py`
  (and corresponding `__main__.py`) to `data_src/raster`
- Use `demproc` to generate other land cover maps (slope, aspect etc) using
  DEMs obtained with `download_site_elevation_data.py`.
- Generate `site_parameters.xml` (precipitation etc) by script in `data_src` and
  strip out of git repo.
- Obtain target initial land cover proportions for each study site from
  `~/Documents/codes/python/notebooks/modified_random_clusters/implement_modified_random_clusters.ipynb`,
  and combine with `data_src/lct_nlm/generate_landcover_maps.py` to generate caches of nlms for each
  study site.

### Minor

- Replace DOS style line endings in `installation/installation_coordinator.xml`
  using `M-% C-q C-m RET RET` (see [Dos To Unix]).
- Add a proper license in `license.txt`.
- Update `model_description.txt` so that it directs users to look in
  `target/site` OR investigate how to configure `mvn site` to output generated
  site files in `docs`.
- Delete commit ff1b9b7 | * Add test data to resources. This added `.tif.aux.xml`
  files to the repo needlessly.

[Dos To Unix]: https://www.emacswiki.org/emacs/DosToUnix
