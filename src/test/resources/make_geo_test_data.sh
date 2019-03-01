#!/usr/bin/env bash

function write_gtiff {
    FNAME=$1; DAT=$2
    HEADER_DAT="ncols         3
nrows         3
xllcorner     0.0
yllcorner     0.0
cellsize      50.0
NODATA_value  -9999\n"
    printf "$HEADER_DAT" > tmp.asc
    printf "$DAT" >> tmp.asc
    gdal_translate -of "GTiff" tmp.asc $FNAME
    rm tmp.asc
}

# write hydrologically correct 3x3 demo data
write_gtiff hydro_correct_dummy.tif "1 1 3\n3 3 3\n3 4 4"

# write hydrologically incorrect 3x3 demo data (no sink)
write_gtiff hydro_incorrect_dummy.tif "1 1 5\n3 3 3\n3 4 4"
