# AgroSuccess

[![DOI](https://zenodo.org/badge/DOI/10.5281/zenodo.4641430.svg)](https://doi.org/10.5281/zenodo.4641430)

Simulation model used for exploring influence of human decision-making on
land-cover change.


## Implemented study sites

- algendar
- atxuri
- charco\_da\_candieira
- monte\_areo\_mire
- navarres
- san_rafael

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
- Tested on Java 11 using [AdoptOpenJDK](https://adoptopenjdk.net/) HotSpot
  binaries.
- Dependencies are managed using Maven following [instructions][maven integration]
  found on the Repast mailing list. See also original discussion on repast-interest
   [mailing list]
- See `</dependencies>` within `pom.xml` for details of which dependencies are
  installed.
- Note that while we use Maven to install dependencies which Repase can subsequently
  link to, we don't use Maven to build. For that we rely on Repast.

[maven integration]: https://sourceforge.net/p/repast/mailman/message/35615878/#msg35615878
[mailing list]: http://repast.10935.n7.nabble.com/Building-a-Mavenized-Repast-model-td11831.html

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

## Generate empirical data

Use the [AgroSuccess data][agrosuccess-data-repo] project to prepare empirical
data.

Assuming the below file paths for the `agrosuccess-data` and `AgroSuccess`
projects, the simulation input data can be transferred with the following
commands:

```bash
AS_ROOT=/home/andrew/Documents/codes/java/AgroSuccessWS/AgroSuccess
AS_DATA_ROOT=/home/andrew/Documents/codes/agrosuccess-data

rsync \
	-av --progress \
	$AS_DATA_ROOT/outputs/ \
	$AS_ROOT/data/study-sites \
	--exclude test_data

rsync \
	-av --progress \
	$AS_DATA_ROOT/outputs/test_data/ \
	$AS_ROOT/data/test
```

[agrosuccess-data-repo]: https://bitbucket.org/ajlane50/agrosuccess-data

## Generate graph database

Use the project [AgroSuccess Graph][agrosuccess-graph] to generate a Neo4j
graph store holding all the socio-ecological transition rules needed for the
simulation model. Follow the instructions in the 'Setup' and 'Loading a model
into the database' sections of that project's `README.md`. This should have
created a Docker volume on your machine called `as-neo4j-data` which contains
the graph store holding our model.

The below commands:

1. Identify the file system location where Docker has placed our graph store
2. Copy the graph store into the AgroSuccess project
3. Set file permissions to my user account, so the agrosuccess program running
   under my username can access the graph store

```bash
AS_ROOT=/home/andrew/Documents/codes/java/AgroSuccessWS/AgroSuccess
AS_GRAPH_STORE=$(docker volume inspect as-neo4j-data | \
   grep "Mountpoint" | \
   sed 's/.*": "\(.*\)",.*/\1/')
rm -rf $AS_ROOT/data/graph
sudo rsync -av $AS_GRAPH_STORE/databases $AS_ROOT/data/graph
sudo chown -R andrew:andrew $AS_ROOT/data/graph/databases
```

To refresh the copy of the database used for running integration tests
against, fun the following in addition to the above.

```
rm -rf $AS_ROOT/src/test/resources/databases/agrosuccess-test.db
sudo rsync -av $AS_GRAPH_STORE/databases $AS_ROOT/src/test/resources
sudo chown -R andrew:andrew $AS_ROOT/src/test/resources/databases
mv $AS_ROOT/src/test/resources/databases/graph.db \
	$AS_ROOT/src/test/resources/databases/agrosuccess-test.db
```

The reason for maintaining separate test and production database copies is to
enable the production copy to be modified without interfering with the tests.

[agrosuccess-graph]: https://ajlane50@bitbucket.org/ajlane50/agrosuccess-graph

## Serialize graph database

Run the script

```bash
./scripts/serialize_lcs_transitions.sh
```

to read the copy of the graph database generated above and write a serialised
`CodedLcsTransitionMap` containing the transition rules to the file
`data/succession/agrosuccess.codedlcstrans`.

## Compile source code and install Maven dependencies

First compile sources

```bash
mvn clean compile
```

Install Maven dependencies into local directory

```bash
mvn dependency:copy-dependencies
```

## Copy custom build.xml script for batch runs

To account for differences in project strucutre between a standard Repast
Simphony project and AgroSuccess we make some changes to the the default Repast
Simphony build script. See `scripts/batch/README.md` for details.

To copy this script into the Repast Simphony plugin, run

```bash
./scripts/batch/copy-build-script.sh
```


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

### Logging

#### Using Repast Simphony GUI

Change `log4j` settings in `MessageCenter.log4j.properties`, e.g. to send
debug messages to the console. Logs from custom AgroSuccess code are sent
to the file `logs/agrosuccess.log`, whereas logs from Repast Simphony code
are sent to `logs/debug.log`.

#### Unit tests

Logs for unit tests are configured in `src/test/resources/log4j.properties`.
These logs are sent to the file `logs/tests.log`.

#### Using Repast Simphony headless

At time of writing on 2020-02-06, this is looking ahead. However it is
expected that when run headless, logs will be configured in
`src/main/resources/log4j.properties`, and will be sent to the file
`logs/agrosuccess.log`.

## Known issues

### 'illegal reflective access operation' warning

Repast Simphony 2.7 has [XStream](http://x-stream.github.io/) as a dependency.
XStream is used for serialising Java objects to XML. When running AgroSuccess
on Java 11 (but not Java 8), XStream triggers a warning which is known to its
developers and is the subject of a
[long-standing issue](https://github.com/x-stream/xstream/issues/101).
Importantly this is just a warning and should be ignored for the time being.

## loadcypher

### Using Neo4j driver

Start off by using the Neo4j driver for Java to query a database running on a locally hosted Neo4j server from within a Java application. See examples [here](https://neo4j.com/developer/java/)

#### TODOs
1. Create array of land cover types in database. Loop through and print out land cover conversion costs
2. Review Repast code, think about how to integrate graph into agent decision making
3. Read global parameters (e.g. Initial no agents, model ID etc) from database into Repast
