# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).

## Unreleased

### 2 - Run environmental model with study site data - 2020-02-06

AgroSuccess environmental model now runs with full resolution data for my
study sites.

#### Changed

- Repast model runs with empirical data from study sites instead of test data.
- Time evolution of land cover proportions are saved in files named
  `outputs/<sitecode>_<date_and_time>_lct-props.csv`
- Sundry changes to `.classpath`, `.settings/`, and `Repast.settings` which
  Repast and Maven have made along the way. These all seem to be compatible
  with both JUnit and Repast, so I will incorporate them into version control
  at this stage.

#### Added

- `SiteBoundaryCondsFromData` class used to provide study site boundary
  condition data to the context with an interface consistent with
  `SiteBoundaryCondsHardCoded` to support easy switching between test and
  production scale simulations.
- `RecordWriter` interface and `EnumRecordCsvWriter` class used to write land
  cover proportions to csv.
- `SimulationID` class which holds and reports unique ID for simulations.
- New dependency on `commons-collections`. This was done to prevent a
  `ClassNotFound` exception on
  `org.apache.commons.configuration.XMLConfiguration` which occurred when
  running Repast Simphony but not unit tests.

### 1 - Generate input data externally - 2020-01-27

#### Changed

- Remove scripts used to prepare empirical data for simulation
  to separate [AgroSuccess data][agrosuccess-data-repo] project. See
  instructions there to prepare empirical data.
- See [AgroSuccess graph][agrosuccess-graph-repo] project to prepare model
  configuration graph store.
- See this project's README.md for explicit steps for how to copy empirical
  data and graph data store into the AgroSuccess simulation project.

[agrosuccess-data-repo]: https://bitbucket.org/ajlane50/agrosuccess-data
[agrosuccess-graph-repo]: https://bitbucket.org/ajlane50/agrosuccess-graph

### 0 - Upgrade AgroSuccess to Repast 2.7 - 2019-11-06
Move all code specific to AgroSuccess directly into the appropriate
place. In case of merge conflicts, the resolution strategy was to
accept all 2.7 related changes, and also things from my previous work
which were deliberately added to the Repast/ Eclipse configuration files.

#### Added

##### Maven
- Used for retrieving depenencies which don't come with Repast.
- Also useful for generating documentation sites.

##### External data
- Set geographic data directory to `/AgroSuccess/data/geographic` in
  `AgroSuccess.rs/context.xml`.

##### Study site
- Set `studySite` to `navarres` in `AgroSuccess.rs/context.xml`.

##### Projections added to context
- `Agent Grid`, `Lct`, and `SoilMoisture` projections added to
  `AgroSuccess.rs/context.xml`.

##### Data
- `geoDataDirRootString` and `studySite` parameters added to `parameters.xml`.

#### Changed

##### Maven related
- Maven and associated settings added to `.classpath`.
- Add Maven nature to `.project`.
- Make `org.eclipse.m2e.launchconfig.classpathProvider` the launch
  configuration's `CLASSPATH_PROVIDER` in
  `Build Installer for AgroSuccess Model.launch` and
  `launchers/Batch AgroSuccess Model.launch`.
- Make `org.eclipse.m2e.launchconfig.sourcepathProvider`
  `SOURCE_PATH_PROVIDER`  in
  `Build Installer for AgroSuccess Model.launch` and
  `launchers/Batch AgroSuccess Model.launch`.
- Add Maven classpath container to CLASSPATH following instructions
  [here][SF add Maven].
- Agent path changed from `"../bin"` to `"../target/classes"` in
  `user_path.xml`.

[SF add Maven]: https://sourceforge.net/p/repast/mailman/message/35615878/#msg35615878
