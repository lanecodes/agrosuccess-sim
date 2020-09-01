#! /usr/bin/env bash

# -----------------------------------------------------------------------------
# Copy the file build.xml to replace the default in the Repast Simphony plugin.
#
# If the file already exists, append '.backup_<epoch-time>' to its name before
# copying its replacement.
# -----------------------------------------------------------------------------

ECLIPSE_PATH=/home/andrew/opt/eclipse
TGT=$ECLIPSE_PATH/plugins/repast.simphony.distributed.batch_2.7.0/bin/scripts/build.xml

if [ -f "$TGT" ]; then
    mv $TGT $TGT.backup_$(date +%s)
fi

cp build.xml $TGT
