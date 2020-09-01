# Notes on `build.xml`

The file
`./eclipse/plugins/repast.simphony.distributed.batch_2.7.0/bin/scripts/build.xml`
is used by Repast Simphony to construct a Model Archive `.jar` file that
contains all the code an data necessary to perform a simulation batch run.

There is a configuration mismatch between Repast Simphony which expects
compiled java sources to be in the `./bin` directory. Because we use Maven for
some project dependencies our compiled classes are in `target/classes`
instead. We also have maven dependencies in `./target/dependency`. Here we

## Outline of problem with default build.xml

If run with the default version of build.xml that is supplied with Repast
Simphony, when we use the 'Create Model Archive for Batch Runs' utility we get
the error

```
java.util.concurrent.ExecutionException: /home/andrew/opt/eclipse/plugins/repast.simphony.distributed.batch_2.7.0/bin/scripts/build.xml:83: The following error occurred while executing this line:
/home/andrew/opt/eclipse/plugins/repast.simphony.distributed.batch_2.7.0/bin/scripts/build.xml:191: /home/andrew/Documents/codes/java/AgroSuccessWS/AgroSuccess/bin does not exist.
```

The offending line in `build.xml` is

```
<jar destfile="${working.lib}/model.jar" basedir="${model.dir.bin}" />
```

This copies the class files contained in the directory referenced by
`model.dir.bin` into the `model.jar`. Since we use the project layout expected
by Maven (class files in `./target/classes`) rather than that expected by
Repast Simphony (class files in `./bin`) we need to update the line where
`model.dir.bin` is specified to point to the correct location.

## Update location of compiled project sources to comply with Maven conventions

We replace the line

```xml
<property name="model.dir.bin" location="${model.dir}/bin" />
```

with

```xml
<property name="model.dir.bin" location="${model.dir}/target/classes" />
```

Also change destination of `"fake_jar_to_avoid_watcher_init_warnings.jar"` to
avoid spurious warnings about not being able to load classes

```xml
<copy todir="${working.dir}/target/classes">
	<resources>
		<javaresource name="fake_jar_to_avoid_watcher_init_warnings.jar" />
	</resources>
</copy>
```

## Specify inclusion of Maven dependencies to build.xml

We need to copy the dependencies Maven has supplied us with during development
into the complete model jar. The Maven command

```bash
mvn dependency:copy-dependencies
```

can be used to copy the project dependencies into `./target/dependency`.

We add the following to build.xml to specify that maven dependencies should be
copied into the generated .jar.

```xml
<property name="model.maven.lib" location="${model.dir}/target/dependency" />
...
<copy todir="${working.lib}">
	...
	<fileset dir="${model.maven.lib}">
		<include name="*.jar" />
	</fileset>
</copy>
...
```
