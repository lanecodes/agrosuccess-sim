# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).

## Unreleased

### 36 - Add meanNumFiresPerYear parameter - 2020-10-26

The meanNumFiresPerYear parameter replaces the old climateIgnitionScalingParam
parameter. The climate ignition scaling parameter assumed a specific model of
how climate relates to frequency of fire occurrence. During our work to
calibrate the wildfire model for the different study sites we found that the
holocene average climate data for some study sites were producing imnplausibly
low numbers of fire per year. To move to a different and more flexible
approach, we here move the logic of how the mean number of fires per year is
calculated out of the simulation model by specifying this as a model parameter
directly. The former approach of using a climate ignition scale parameter can
still be used, but the logic to calculate the mean number of fires will need to
be performed elsewhere.

#### CHANGED

- `AgroSuccess.rs/parameters.xml` updated to refer to `meanNumFiresPerYear`
  rather than `climateIgnitionScalingParam`
- Update `FireParams` class
- Update `ModelParamsRepastParser` to look for `meanNumFiresPerYear` in the
  Repast parameters rather than `climateIgnitionScalingParam`
- Update `AgroSuccessContextBuilder` so it no longer calculates mean number of
  fires from climate ignition scaling parameter and consumes mean number of
  fires directly. Also remove the method `initFireManager`'s dependence on site
  climate parameters, as these are no longer used in that method. Note however
  that the climate parameters are still used in other parts of the model,
  e.g. the soil moisture calculation.
- Update references to `climateIgnitionScalingParam` in test code

### 35 - Add ignition point to reported fires - 2020-10-22

Previously, for each fire occurring in a simulation, we reported only the
simulation tick at which the fire occurred and the total number of cells burnt
in the fire. However this turned out to be insufficient to enable us to
subsequently distinguish those fires that occurred within the 'experimental
zone', which is important for subsequent analyses. In this change we report
also the ignition point of the fire. Analysis code can use this information to
determine which fires should be counted as occurring within the experimental
zone.

#### CHANGED

- `me.ajlane.geo.repast.fire.FireSizeDataSource` updated so that it reports the
  ignition point of each fire in addition to the simulation tick when it
  occurred and its total size (number of burnt cells).

### 34 - Make modifications and additions to run on Rosalind - 2020-10-21

#### ADDED

- Scripts in `scripts/rosalind` developed by Nick Collier needed to run Repast
  Simphony models on HPCs using the Slurm scheduler.
- `scripts/rosalind/retrieve-outputs.sh` and `scripts/rosalind/upload-model.sh`
  to upload moodels to Rosalind and retrieve the combined results.
- `ZipFileExtractor` class to encapsulate the work of extracting land-cover
  type NLMs from their zip archives

#### MODIFIED

- `SiteDataLoader` now uses `ZipFileExtractor` to obtain neutral landscape
  model for simulation. This was done for compatibility with later versions of
  Java used on Rosalind. The problem here was caused by
  `FileSystems#newFileSystem(Path, Classloader)` being indistinguishable from
  the method `FileSystems#newFileSystem​(Path, Map<String,​?>)` introduced in
  Java 13 when the second argument is `null` (compare
  https://docs.oracle.com/javase/8/docs/api/java/nio/file/FileSystems.html and
  https://docs.oracle.com/en/java/javase/14/docs/api/java.base/java/nio/file/FileSystems.html).
  The new implementation is cleaner (better adherance to SRP) and doesn't
  suffer from this issue, so the model now runs on both Java 8 and Java 14.
- Replace `Integer`, `Long`, `Double`, and `Boolean` constructors with calls to
  `.valueOf` to avoid complaints about deprecation in later Java versions.

### 32 - Change scheduling order of environmental model processes - 2020-09-18

#### CHANGED

This was never clearly thought through and made explicit in the context
builder. The schedule order is now:

1. Land-cover colonisation
2. Update soil moisture
3. Run the fire ignition/ spread model
4. Land-cover update due to succession
5. Evaluate oak age and fire frequency, determine whether cells transition from
   regeneration to secondary succession

### 31 - Add logic to select random NLM or null lanc-cover type maps - 2020-09-17

#### ADDED

- `SiteRasterData` has a new `getNullLctMap(int[], int[])` method that
  generates a land-cover type map in which all cells are burnt.

#### CHANGED

- Whereas `AgroSuccessContextBuilder` used to select the default NLM every time
  (i.e. number 0), it now randomly samples one of the 100 pre-prepared NLMs in
  `init_lct_maps.zip`.
- Updated `SiteDataLoader` and `DummySiteData` to implement
  `getNullLctMap(int[], int[])`.
- Add `nLctNlms` parameter to `parameters.xml` to specify the number of NLMs in
  init_lct_maps.zip
- Add `useNullLctNlm` parameter to indicate model should use null NLM in which
  all cells start off in burnt state

### 30 - Refactor method scheduling to use IAction objects - 2020-09-08

While running batch runs I noticed land-cover states were not being updated,
and fires were not taking place. It turned out that using Repast's
`@ScheduleMethod` annotation worked during single model runs, when run in batch
mode using `complete_model.jar` these scheduled methods were not being called.

The workaround is to replace these `@ScheduleMethod` decorators with calls to
`ISchedule#schedule(ScheduleParameters, IAction)` in the context builder. This
requires the addition of new classes for each of the environmental model
pseudo-agents, but is preferable to using the
`ISchedule.schedule(ScheduleParameters, Object, String, Object...)` which
violates type safety by requiring reference to scheduled methods by their names
as strings. Another alternative could be to use anonymous classes that
implement `IAction` instead.

I would argue this workaround improves code readability anyway, as it avoids
the need to scatter information about when methods are scheduled to occur
around the model code. Now it's all in the context builder.

#### CHANGED

- `OakAgeUpdater`, `AgroSuccessLcsUpdater`, `DefaultFireManager`,
  `SpatiallyRandomSeedDisperser`, and `CompletelySpatiallyRandomColoniser` no
  longer have `@ScheduleMethod` decorators on their methods to schedule their
  actions
- Logging calls added to each of the above pseudo-agent classes to aid in
  debugging situations where it's not clear if/ when they are being called
- Calls to `ISchedule#schedule(ScheduleParameters, IAction)` added to the
  context builder for each of the added `IAction` classes.

#### ADDED

- `LandCoverColonisationAction`, `RunFireSeasonAction`, `OakAgeUpdateAction`,
  and `UpdateLandCoverStateAction` classes representing the actions carried out
  by each of the pseudo-agents

### 29 - Update batch run configuration - 2020-09-08

#### CHANGED

- Send land-cover proportions to output directory
- Batch outputs appear in `./outputs/outputs`

### 28 - Use Repast Simphony to report land-cover type proportions - 2020-09-08

### CHANGED

- Rather than using my own custom `LctProportionAggregator` to report
  land-cover proprtions, now use `LctProportionDataSource` that implements
  `NonAggregateDataSource`.

### ADDED

- New `LctProportionDataSource` class

### 27 - Remove reference to graph database from context builder - 2020-09-07

#### CHANGED

- Whereas previously a GraphDatabaseService object was added to the simulation
  context in the class `AgroSuccessContextBuilder`, this is now avoided.

### 26 - Read serialized land-cover transition map - 2020-09-07

Originally land-cover transition rules were read from a graph database. However
this led to errors when simulations were run in parallel because multiple
processes were attempting to access the graph store simultaneously. As a
workaround for this we add a class to read a serialised copy of the
`CodedLcsTransitionMap` that ultimately represents the land-cover transition
rules in the simulation from disk.

#### ADDED

- `SerialisedCodedLcsTransitionMapReader` class that reads the serialised
  transition rules file from disk
- `GraphBasedLcsTransitionMapSerializer` reads succession rules from graph
  database and writes them to serialised `CodedLcsTransitionMap` object
- `CodedLcsTransitionMapReaderFactory` to select class used to read coded
  land-cover state transition map

#### CHANGED

- Land-cover state transition map now read into the context builder using the
  `CodedLcsTransitionMapReaderFactory`
- Context builder uses land-cover transition rules loaded from the file
  `data/succession/agrosuccess.codedlcstrans`

### 25 - Separate integration tests with and without fire - 2020-09-07

Creates a new class for tests that need to be run in isolation from the fire
regime. This lets us test that land-cover state is updated due to the
succession rules on their own as expected.

#### ADDED

- `AgroSuccessNoFireIntegrationTest` class containing integration tests without
  the fire regime

#### CHANGED

-  `AgroSuccessEnvrIntegrationTest#landCoverStateShouldEvolveOverTime` moved to
  new `AgroSuccessNoFireIntegrationTest` class

### 24 - Fix reference equality bug in AgroSuccessLcsUpdateDecider - 2020-09-07

#### CHANGED

- There was a bug caused by doing reference comparison rather than value
  comparison on land-cover state values in `AgroSuccessLcsUpdateDecider`.
- This is now corrected by using primitive `int` values to represent land-cover
  states, time in state, and transition times internally within the class

### 23 - Refactor land-cover succession code - 2020-09-02

#### CHANGED

- Introduced various new packages under
  `me.ajlane.geo.repast.succession.pathway` to contain code used for loading
  and representing succession pathway models
- Rename `GraphBasedLcsTransitionMapFactory` to
  `GraphBasedLcsTransitionMapReader`
- Rename `LcsTransitionMapFactory` interface to `LcsTransitionMapReader`
- Make `CodedLcsTransitionMap` and `AliasedLcsTransitionMap` serializable
- Refactor `LcsTransitionMapReader` so it extends new
  `CodedLcsTransitionMapReader` and `AliasedLcsTransitionMapReader` interfaces
  (complies with interface segregation principle).

### 22 - Add custom build.xml - 2020-09-01

We add a custom build script for generating standalone model jars using Repast
Simphony. This is done to account for differences in project strucure between
AgroSuccess (which uses Maven) and a conventional Repast Simphony project.

#### ADDED

- `scripts/batch/build.xml` file containing a custom build script to be copied
  into the Repast Simphony plugin
- `scripts/batch/copy-build-script.sh` that inserts the custom build script into
  the Repast Simhony plugin
- `scripts/batch/README.md` containing an explanation of what the new build
  script does

#### CHANGED

- Update project-level `README.md` to document additional steps needed to build
  the project correctly resulting from the work described above

### 21 - Add Fire Size data set file sink to Repast model - 2020-08-05

#### ADDED

- `FireSizeDataSource` class

#### CHANGED

- fire spreader specified in `AgroSuccessContextBuilder` is now a
  `ReportingRepastFireSpreader`
- `FireReporter` added to context
- Repast Simphony configured to use `FireSizeDataSource`
- Model documentation on configuring custom data sources updated

### 20 - Add `ReportingRepastFireSpreader` - 2020-08-25

#### ADDED

- New `ReportingRepastFireSpreader` class whose instances communicate
  `FireEvents` to a `FireReporter` once fire spread is complete

### 19 - Refactor `FireSpreader` - 2020-08-24

#### ADDED

- `AgroSuccessFuelMoistureRiskTable` to convert vegetationMoistureParam to
  fuelMoistureRiskFactor
- `GridPointIterable` class added to `RepastGridUtils`

#### CHANGED

- `DefaultFireManager` receives grid dimensions in its constructor rather than
  needing to reference them through a `FireSpreader`, simplifying the
  `FireSpreader` interface
- `FireSpreader` is now an interface, old `FireSpreader` class now called
  `DefaultFireSpreader`
- `FireSpreader#spreadFire(GridPoint ignitionPoint), double
  vegetationMoistureParam)` changed to `FireSpreader#spreadFire(GridPoint
  ignitionPoint)`
- vegetationMoistureParam passed to `FireSpreader` through constructor
- vegetationMoistureParam removed from `FireManager` constructor as it's no
  longer needed by the code in this class
- `DefaultFireSpreader` uses `AgroSuccessFuelMoistureRiskTable` to do
  fuelMoistureRisk calculation rather than implementing the method itself
- Remove `DefaultFireSpreader#getLct` method as this violates encapsulation and
  was introduced previously as a hack
- `FireSpreader#spreadFire(GridPoint ignitionPoint)` returns `List<GridPoint>`
- `DefaultFireSpreader` uses a `FlammabilityChecker` to evaluate cell flammability

### 18 - Add `FireReporter` - 2020-08-23

#### ADDED

- `FireEvent` class with `tick`, `ignitionCell`, `burntCells` private methods
  with getters, as well as public method `size()`
- `FireReporter` class that collects information about fires and reports when
  required

### 17 - Refactor `FireManager` - 2020-08-23

#### ADDED

- New `FireManager` interface has a void `FireManager#startFires()` method
- `DefaultFireManager` updated to implement `FireManager`
- `FlammabilityChecker` and default implementation `DefaultFlammabilityChecker`
  that indicate whether a specific grid cell is flammable or not

#### CHANGED

- `FireManager` is now an interface and the old class of the same name is now
  called `DefaultFireManager`
- `DefaultFireManager#numFires()` now package protected
- `DefaultFireManager` now accepts a `FlammabilityChecker` in its constructor

### 16 - Replace land-cover colonisation model with CompletelySpatiallyRandomColoniser - 2020-08-13

We decided to use a simpler implementation of spatially random seed
distribution than was originally planned. We now don't incorporate any
information about the location of seed sources in the model of where seeds end
up being located.

#### ADDED

- `colonisation.csr` package
- `CompletelySpatiallyRandomColoniser` class implementing the new land-cover
  colonisation model
- `CompletelySpatiallyRandomParams` class for containing parameters for this
  model

#### CHANGED

- Updated `AgroSuccessContextBuilder` to use
  `CompletelySpatiallyRandomColoniser` rather than
  `SpatiallyRandomSeedDisperser`
- Updated `ModelParamsRepastParser` to generate default parameters for
  completely spatially random coloniser model

### 15 - Refactor seeddispersal package to be called colonisation - 2020-08-12

During the course of model development we have decided that the process we
previously referred to as 'seed dispersal' is better described as 'land-cover
colonisation'. Colonisation in this sense refers to the combined effect of seed
dispersal, seed germination, seedling establishment, and seedling survival
(Thompson, 2005). This refactoring and change of package name reflects this
change in the name of the relevant classes.

Thompson, J. D. (2005). Plant Evolution in the Mediterranean. Oxford: Oxford
University Press.Thompson, J. D. (2005). Plant Evolution in the
Mediterranean. Oxford: Oxford University Press.

#### ADDED

- `LandCoverColoniser` abstraction representing the inferface for
  `SeedDisperser` and `SpatiallyRandomSeedDisperser`.

#### CHANGED

- `seeddispersal` package renamed to `colonisation` to better reflect the
  modelled process it implements
- `LandCoverColoniser#updateSeedLayers` renamed to
  `LandCoverColoniser#updateJuvenilePresenceLayers`
- Classes used to implement random kernel seed distribution model moved to new
  `colonisation.randomkernel` package

### 14 - Add environmental update rules to AgroSuccessLcsUpdateDecider - 2020-08-10

These correspond to rules S4 and S5 in the thesis, and reflect the ability of
the transition of a cell to a mature land-cover state, or the occurrence of
high frequency fire to cause a cell to have juveniles killed, or to have the
succession pathway change from regeneration to succession

#### ADDED

- `SeedStateUpdater` and `SeedState` classes to implement rule S4
- Oak mortality rule S5 logic added to new `SuccessionPathwayUpdater` class
- `EnvrSimState` class added to contain grid cell data needed for succession
  model but not part of physical state of the grid cell
- `OakAgeUpdater` class added to increment oak age in cells depending on
  current land-cover type

#### CHANGED

- `LscapeLayer#FireCount` added. This tracks how many fires occurred in each
  grid cell during the simulation
- `LscapeLayer#OakAge` added. This tracks how many continuous years the grid
  cell has contained reproductively mature oak (i.e. oak or transition forest
  land-cover)
- `FireSpreader#burnCellAtPoint` increments `LscapeLayer#FireCount` landscape
  layer when a fire takes place in a cell.
- `SuccessionPathwayUpdater`, which is a dependency of
  `AgroSuccessLcsUpdateDecider`, switches regeneration to succession pathway
  depending on state of oak vegetation and frequency of fire. Switch to
  regeneration when oak lct is reached, switch to secondary if fire frequency
  exceeds level specified in Millington et al. 2009 Statements 7a and 7b. This
  corresponds to rule S5 in my thesis.

### 13 - Rule to kill juveniles when mature vegetation transition occurs - 2020-07-06

Here we add a new rule to kill juvenile plants (pine, oak and deciduous seeds)
in a grid cell when the cell transitions to a mature vegetation state. This is
to account for the fact that in the colonisation submodel, if seedlings are
deposited in grid cell $i$ at time $t$, they will stay there in subsequent time
steps $t+1, t+2, \dots$. The occurance of a grid cell transitioning to a mature
vegetation type is ecologically significant because it indicates that the group
to which the mature community corresponds---pine, oak or deciduous in
AgroSuccess---has out-competed individuals belonging to other groups. For
example, we might imagine mature oak woodland shading out pine saplings. We
therefore enforce a rule that in the timestep where a grid cell transitions to
a mature vegetation type, we ensure that the varibles indicating whether there
are juvenile plants in the cell are set to false.

#### CHANGED

- LcsUpdateMsg's methods have changed. It now returns the complete
  environmental state of the cell rather than just the current state of the
  cell. This enables the `LcsUpdateDecider` to modify the environmental
  conditions of a grid cell as well as determining when a land-cover state
  transition occurs.
- Constructor of `AgroSuccessLcsUpdateDecider` modified to require a set of
  numerical codes for land-cover types representing mature vegetation communities.
- Modifications made to `AgroSuccessLcsUpdater` to incorporate changes in
  succession rules into the grid layer updates.

#### ADDED
- Implementation of the rule described above added to
  `AgroSuccessLcsUpdateDecider`
- Added corresponding tests to both `AgroSuccessLcsUpdateDeciderTest` and
  `AgroSuccessLcsUpdaterTest`.

### 12 - Refactor `LandCoverType` interface - 2020-07-06

#### CHANGED

`LandCoverType` interface becomes `LandCoverTypeBase` and is extended by
`LandCoverTypeEco` and `LandCoverTypeAnthro` which provide all the methods
needed to charactise a land-cover type from ecological and anthropolocial
perspectives respectively.

`AgroSuccessLct` becomes `Neo4jAgroSuccessLct` to remove dependence on Neo4j
from classes which use `LandCoverType`.

These new interfaces will be used by classes loading information about
land-cover types from the graph database rather than using hard-coded values.

### 11 - Make AgroSuccessCodeAliases single source of truth for codes - 2020-07-06

#### CHANGED

Previously `AgroSuccessEnvrStateAliasTranslator` used hard coded values
for numerical codes to map between aliases and numerical codes used in
simulation grid cells for land-cover types and environmental conditions. Here
we change this so `AgroSuccessEnvrStateAliasTranslator` depends on
`AgroSuccessCodeAliases` and the enums it contains to serve as a single source of truth for these codes.

### 10 - Reflect updates in AgroSuccess-graph - 2020-07-03

#### CHANGED

Corresponding to `c6afb24` in agrosuccess-graph:

- Remove Barley land-cover type
- Remove `EcoEngineeringActivity`-s corresponding to agriculturalists doing
  barley agriculture
- Remove `digestible_matter` attribute from `AgroSuccessLct` class

Corresponding to `9be0811` in agrosuccess-graph:

- Add `is_mature_vegetation` attribute to `LandCoverType` nodes

### 9 - Update soil moisture calculation - 2020-07-03

There was an error in the previously implemented soil moisture calculation
based on a misunderstanding of the description of the model specified in
Millington, 2009. See `src/site/markdown/modelling-notes.md` for details of the
theory behind the new implementation.

#### ADDED

- `GridLocKingMove` class
- `JgraphtLandscapeFlow` and `JGraphTCatchmentFlow` aspatial representations
  of how water flows over the landscape

#### CHANGED

- `CurveNumberGenerator` interface and `AgroSuccessCurveNumberGenerator` class
  which map local land-cover state to a number characterising how much water is
  retained in the grid cell. This was formerly part of the soil moisture
  calculation class but has been refactored out.
- A new `SoilMoistureCalculator` class replacing the old one which implements a
  new `SoilMoistureUpdater` class.
- `AgroSuccessContextBuilder` now uses the new `SoilMoistureCalculator`

### 8 - Introduce local value layer interfaces - 2020-06-24

See javadoc for the new `CartesianGridDouble2D` interface for motivating
rationale for introducing a new interface which mirrors some Repast Simphony
features.

#### ADDED

- `CartesianGridDouble2D` and `WriteableCartesianGridDouble2D` interfaces
- Corresponding adapter classes `ValueLayerAdapter` and `GridValueLayerAdapter`
  to enable Repast `ValueLayer` and `IGridValueLayer` objects to provide these
  interfaces.

#### CHANGED

- `GridCell` now called `GridLoc`

#### FIXED

- Corrected error in `RepastGridUtils` which mixed up order of row/ column
  coordinates.

### 7 - Add fuel-moisture risk sensitivity to fire model - 2020-02-17

#### CHANGED

- `FireManager` and `FireSpreader` updated to accept a `fuelMoistureFactor`
  parameter which controls moisture in fuel
- `fuelMoistureFactor`, which in Millington et al. 2009 corresponds identically
  to the expected number of fires per year, is converted into fuel moisture
  tisk using Table 4.
- This feeds into the fire spread probability algorithm implemented in
  `FireSpreader.spreadFire`.

### 6 - Add fire parameters - 2020-02-17

#### Added

- `FireParams` class with the methods `getClimateIgnitionScalingParam`
  and `getLcfReplicate`.

#### Changed

- `EnvrModelParams` has a new  `getFireParams` method
- `climateIgnitionScalingParam` and `lcfReplicate` now specified in
  `parameters.xml`.

### 5 - Refactor AgroSuccessContextBuilder - 2020-02-17

#### Changed

- Created `repast.model.agrosuccess.params` package to contain all classes
  and interfaces realted to model parameters
- Finished implementing `ModelParamsRepastParser` which extracts AgroSuccess
  specific parameter objects from parameters.xml
- Clean up  `AgroSuccessContextBuilder#build` by moving code into dedicated
  functions. `build` can now be read from top to bottom without much
  difficulty.

### 4 - Fire spread model - 2020-02-14

#### Added

- `LcfReplicate` enumerator storing possible values for land-cover
  flammability replicates per Millington 2009 Table 6.
- `LcfMapGetter` interface for generating land-cover flammability data for
  land cover types in AgroSuccess.. This is implemented by
  `LcfMapGetterHardCoded` and `LcfMapGetterFroMGraph`.
- `SlopeRiskCalculator`
- `WindRiskCalculator`
- `WindOrientation`
- `GridPointKingMove` to represent movement from one cell to an adjacent cell
- `FireSpreader` which represents progress of an already ignited fire
- `FireManager` which manages fire ignitions and interacts with the Repast
  scheduler.

#### Changed

- Whereas previously empirical data was loaded into the model with
  `SiteBoundaryConds`, this is now done with `SiteAllData`.
- `AgroSuccessContextBuilder` has been refactored to make use of `SiteAllData`.
  Further refactoring needs to be done to tidy it up and make it readable,
  however. The root of these problems is the complexity of some of the objects
  needed to run the simulations. This will be simplified by abstracting these
  object construction steps using the Builder pattern.

### 3 - Implement logging - 2020-02-06

Replaces calls to `System.out.println` and `System.out.print` with calls to
proper logging functions using `log4j`. This massively cleans up the console
outputs making debugging simpler.

#### Changed

- A systematic find and replace to add `log4j` loggers to classes which need to
  log to console, remove calls to `System.out.*` and replace them with logging
  calls.
- Modified `MessageCenter.log4j.properties` so log messages raised in my own code
  are sent to the console and the logging file `logs/agrosuccess.log`

#### Added

- `src/test/resources/log4j.properties` file which controls logging config
  during unit tests. These logs are sent to the file `logs/tests.log`.
- `src/main/resources/log4j.properties` file which controls logging config
  during runs of model without Repast Simphony GUI. When running with the
  RI GUI, the logger config from the file `MessageCenter.log4j.properties`
  are used instead (see 'Changed').

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
