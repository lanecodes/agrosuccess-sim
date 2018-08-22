#! /usr/bin/env python
 # -*- coding: utf-8 -*-
"""Produce a dummy soil type map using a DEM as template.

The soil moisture calculation in the AgroSuccess model requires soils to be
characterised in terms of soil types A, B, C or D (see Millington et al. 2009).

Ultimately I will seek to obtain soil maps for the study sites. As an
approximation to expediate model development I will provide dummy .tif files
with cells which match the grographical extents and cell size of a template
dem.tif for a given study site, but have uniform soil type. 

Usage:
$ python make_dummy_soil_type_map.py <template.tif> <soil type letter>

E.g.

$python make_dummy_soil_type_map.py dem.tif B

References: 

Millington, J. D. A., Wainwright, J., Perry, G. L. W., Romero-Calcerrada, R., &
Malamud, B. D. (2009). Modelling Mediterranean landscape succession-disturbance
dynamics: A landscape fire-succession model. Environmental Modelling and
Software, 24(10), 1196–1208. https://doi.org/10.1016/j.envsoft.2009.03.013

"""
import sys
import subprocess

from six import iteritems
import numpy as np
from osgeo import gdal, osr

template_tif = sys.argv[1]
soil_type = sys.argv[2]

# Map soil type letters to numerical representation. See
# ../java/me/ajlane/geo/repast/succession/SoilMoistureCalculator.java
soil_types = {"A": 0, "B": 1, "C": 2, "D": 3}

try:
    soil_type_number = soil_types[soil_type.upper()]
except KeyError:
    print 'Soil type \'{0}\' not supported. Exiting...'.format(soil_type)
    sys.exit()

print 'Creating a uniform field of {0}-s using the following .tif as a ' \
      'template:\n{1}'.format(soil_type_number, template_tif)

def get_template_metadata(template_file):
    metadata = {}
    template = gdal.Open(template_file)

    arr = template.GetRasterBand(1).ReadAsArray()
    metadata['nCols'] = arr.shape[0]
    metadata['nRows'] = arr.shape[1]
    del arr

    metadata['geo_transform'] = template.GetGeoTransform()
    metadata['projection'] = template.GetProjection()

    return metadata

try:
    geo_metadata = get_template_metadata(template_tif)
except IOError:
    print template_tif + ' couldn\'t be read. Exiting...'
    sys.exit()    

# create dummy data
arr = np.full((geo_metadata['nRows'], geo_metadata['nCols']), soil_type_number,
              dtype='uint16')

# write to output geotiff
driver = gdal.GetDriverByName('GTiff')
out_file_name = 'dummy_soil_map_'+soil_type.upper() + '.tif'
outdata = driver.Create(out_file_name,
                        geo_metadata['nRows'],
                        geo_metadata['nCols'],
                        1, # number of bands in output file
                        gdal.GDT_UInt16) # data type

outdata.SetGeoTransform(geo_metadata['geo_transform'])
outdata.SetProjection(geo_metadata['projection'])
outdata.GetRasterBand(1).WriteArray(arr)
outdata.FlushCache() # writes to disk

print 'Done!'
    

    


    

