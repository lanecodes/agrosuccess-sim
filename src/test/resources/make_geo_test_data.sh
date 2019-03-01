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

# Write lct map, mix of pine (6) , oak (9) and burnt(1)
write_gtiff lct_map_mix_oak_pine_burnt_dummy.tif "6 6 1\n1 1 1\n9 9 9"

# write soil type map, all type A soil
write_gtiff soil_type_uniform_A_dummy.tif "0 0 0\n0 0 0\n0 0 0"
