"""
make_geo_test_data.py
~~~~~~~~~~~~~~~~~~~~~

Generates GeoTiff files needed to test the AgroSuccess model.

Combines the functionality of two older scripts, obsoleting them. These were:

1. `derive-layers.sh`, used to generate flow direction, slope and aspect maps   
    given a DEM.
2. `make_geo_test_data.sh` which was used to produce 3x3 GeoTiff rasters 
    representing land cover and soil type maps.
"""
import sys
import os
import numpy as np

import demproc
from demproc.dummy import make_dummy_hydro_incorrect_dem
from demproc.dummy import create_dummy_geotiff_from_array as gtiff_from_array

# ----------- 3x3 DEMs used to test flow direction network classes ------------
# These classes are likely to be depreciated, but tests maintained in case a 
# use is found for them.
gtiff_from_array("hydro_correct_dummy.tif",
    np.array([[1, 1, 3], 
              [3, 3, 3], 
              [3, 4, 4]]))

gtiff_from_array("hydro_incorrect_dummy.tif",
    np.array([[1, 1, 5], 
              [3, 3, 3], 
              [3, 4, 4]]))

# ----------------------------- 3x3 Test Grids --------------------------------
# Used to test SiteBoundaryConds and inheriting classes
# DEM derived data
make_dummy_hydro_incorrect_dem("dummy_3x3_hydro_incorrect_dem.tif")
demproc.derive_all("dummy_3x3_hydro_incorrect_dem.tif", "dummy_3x3")

# Write lct map, mix of pine (6) , oak (9) and burnt(1)
gtiff_from_array("dummy_3x3_lct_oak_pine_burnt.tif",
    np.array([[6, 6, 1], [1, 1, 1], [9, 9, 9]]))

# write soil type map, all type A soil
gtiff_from_array("dummy_3x3_soil_type_uniform_A.tif", np.zeros((3, 3)))

# ----------------------------- 3x4 Test Grid ---------------------------------
# Used to test error is thrown if a grid within unexpected dimensions is loaded
# into AgroSuccess
gtiff_from_array("dummy_3x4_lct_oak_pine_burnt.tif",
    np.array([[6, 6, 1, 1], [1, 1, 1, 1], [9, 9, 9, 9]]))


                            
                                
                                

