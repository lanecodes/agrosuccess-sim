#!/usr/bin/env bash
#------------------------------------------------------------------------------
# derive-layers.sh
#
# Given a GeoTiff file as input, calculate the following outputs:
# 1. Hydrologically correct (i.e. pits removed) DEM, replacing old one
# 2. Flow direction map (1=E, 2=NE, 3=N,..., 8=SE)
# 3. Slope (units are percent incline)
# 4. Continuous aspect (0 deg=E, 90 deg=N, 180 deg=W, 270 deg=S)
# 5. Binary aspect (0 if northerly, 1 if southerly)
#
# Horizontal units must be the same as vertical units in DEM for this to work
#
# Dependencies:
# - TauDEM, in particular binaries for pitremove and d8flowdir
# - gdal, specifically gdaldem and gdal_calc.py
#------------------------------------------------------------------------------

DEMFILE=$1
DEMBASENAME=${DEMFILE%.*}

# Get hydrologically corrected DEM
printf "Calculating hydrologically corrected DEM...\n\n"
mpiexec -n 2 pitremove -z $DEMBASENAME.tif -fel $DEMBASENAME-hydrocorrect.tif

# Replace DEM with pits with hydrologically correct one
mv $DEMBASENAME.tif $DEMBASENAME-old.tif
mv $DEMBASENAME-hydrocorrect.tif $DEMBASENAME.tif

# Get flow direction
printf "\nCalculating flow directions...\n\n"
mpiexec -n 2 d8flowdir -fel $DEMBASENAME.tif -sd8 tmp.tif \
	-p $DEMBASENAME-flowdir.tif
rm tmp.tif # remove d8 slope direction map we don't need

# Get slope
printf "\nCalculating slope...\n\n"
gdaldem slope -p $DEMBASENAME.tif $DEMBASENAME-slope.tif

# Get continuous aspect (0 deg=E, 90 deg=N, 180 deg=W, 270 deg=S)
printf "\nCalculating aspect...\n\n"
gdaldem aspect $DEMBASENAME.tif $DEMBASENAME-aspect.tif \
	-trigonometric -zero_for_flat

# Get binary aspect (all cells 0 for Northerly or 1 for Southerly)
printf "\nCalculating binary aspect...\n\n"
gdal_calc.py -A $DEMBASENAME-aspect.tif --calc='0*(A<180)+1*(A>=180)' \
	     --type='Byte' --outfile=$DEMBASENAME-binaryaspect.tif
# remove continuous aspect to save space
rm $DEMBASENAME-aspect.tif
