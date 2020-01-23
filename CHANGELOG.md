# CHANGELOG.md

## 2 - Generate empirical data externally - 2020-01-23

- Remove scripts used to prepare empirical data for simulation
  to separate repository.
- See [AgroSuccess data][agrosuccess-data-repo] project to prepare empirical
  data.

[agrosuccess-data-repo]: https://bitbucket.org/ajlane50/agrosuccess-data

## 1 - Upgrade AgroSuccess to Repast 2.7 - 2019-11-06
Move all code specific to AgroSuccess directly into the appropriate
place. In case of merge conflicts, the resolution strategy was to 
accept all 2.7 related changes, and also things from my previous work
which were deliberately added to the Repast/ Eclipse configuration files.

### Add
#### Maven
- Used for retrieving depenencies which don't come with Repast.
- Also useful for generating documentation sites. 

#### External data
- Set geographic data directory to `/AgroSuccess/data/geographic` in
  `AgroSuccess.rs/context.xml`.

#### Study site
- Set `studySite` to `navarres` in `AgroSuccess.rs/context.xml`.

#### Projections added to context
- `Agent Grid`, `Lct`, and `SoilMoisture` projections added to 
  `AgroSuccess.rs/context.xml`.
  
#### Data
- `geoDataDirRootString` and `studySite` parameters added to `parameters.xml`.

### Change
#### Maven related
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

