#!/bin/bash

module load apps/openjdk/12.0.1

# Remove symlink to data as otherwise we get a FileSystemLoopException
rm -f instance_*/data
java -Xmx512m -cp "lib/*" repast.simphony.batch.ClusterOutputCombiner . combined_data
