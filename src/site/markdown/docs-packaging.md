# Documentation and packaging


## Documentation

We would like to use Maven to both generate this site and manage javadoc generation
via the `<reporting>` [section of the POM](https://maven.apache.org/plugins/maven-javadoc-plugin/usage.html). However this will not be possible until all dependencies currently included as
Repast Simphony Development Libraries in Eclipse are also added as Maven dependencies. Below we document how we add Repast Simphony itself as a Maven dependency.

## Circumvent issue of RS not being in Maven Central

The last version of Repast Simphony [added to Maven Central](https://mvnrepository.com/artifact/org.opensimulationsystems.cabsf/repast.simphony.bin_and_src) was v2.3.1, uploaded in 2016 by someone other than the Repast maintainers. We use 2.7 and have had problems created by the fact we can't use Repast Simphony as a Maven dependency. A workaround can be achieved by creating a local Maven repository, installing a jar containing the required RS classes there, and specifying that as our dependency in `pom.xml` as usual. Happily Repast Simphony keeps a copy of a jar containing all RS source and binaries on [its GitHub](https://github.com/Repast/repast.simphony) and can be downloaded from [repast.simphony.bin<sub>and</sub><sub>src.jar</sub>](https://github.com/Repast/repast.simphony/blob/v2.7.0/repast.simphony.bin_and_src/repast.simphony.bin_and_src.jar). There is a description of how to do this in the blog post [Adding a custom jar as a maven dependency](https://blog.valdaris.com/post/custom-jar/) which outlines the structure of this solution. This approach enables Maven to include classes from RS in uber-jars if builds for my model, which wouldn't happen if the RS jar was added directly as a dependency. A way in which I would propose deviating from the blog post's approach is that instead of creating a local maven repository within their project and checking these files into VCS, I'd be inclined to have a script download the file and create the local Maven repository somewhere else. This might even be possible to do in the POM itself using  [Bash Maven Plugin](https://stackoverflow.com/questions/8433652) although this is probably overkill for my purposes I should just clearly document how the local repository is created and RS installed into it. This could always be built on later after consulting with the repast-interest mailing list.

[Guide](https://www.theserverside.com/news/1364121/Setting-Up-a-Maven-Repository) on how to set up local Maven repository *server*. Note that there is a local Maven repository which holds artifacts cached from both local and remote servers.

See also [Steps to add external jar to local maven repository](https://www.testingtools.co/maven/steps-to-add-external-jar-to-local-maven-repository)

Recommended by Valdaris

    mvn install:install-file -Dfile=<path-to-file> -DgroupId=<myGroup> \
        -DartifactId=<myArtifactId> -Dversion=<myVersion> \
        -Dpackaging=<myPackaging> -DlocalRepositoryPath=<path>

Maven `install:install-file` documentation is [here](https://maven.apache.org/plugins/maven-install-plugin/install-file-mojo.html).
Mkyong [points out](https://mkyong.com/maven/how-to-include-library-manully-into-maven-local-repository) we can just install into our local Maven repository.

    mvn install:install-file -Dfile=<path-to-file> -DgroupId=<myGroup> \
    	-DartifactId=<myArtifactId> -Dversion=<myVersion> \
    	-Dpackaging=<myPackaging>

The above work of adding Repast Simphony jar to the local Maven repository is achieved in the script
`./scripts/dependencies.py`.
