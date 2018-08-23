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
import os
import subprocess

from six import iteritems
import numpy as np
from osgeo import gdal, osr


class UniformSoilMapGenerator:

    def __init__(self, template_tif):
        """Initialise UniformSoilMapGenerator with a path to a template file.
        
        Args:
            template_tif (str): A file path to the template geotiff file to be
            used to construct the template.
        """
        self._template_tif = template_tif

    @property
    def template_tif(self):
        return self._template_tif

    @template_tif.setter
    def template_tif(self, new_template_tif):
        if os.path.isfile(new_template_tif):
            self._template_tif = new_template_tif
        else:
            raise ValueError(new_template_tif + " is not a readable file.")

    def _soil_type_letter_to_number(self, soil_type_letter):
        # Map soil type letters to numerical representation. See
        # ../java/me/ajlane/geo/repast/succession/SoilMoistureCalculator.java
        soil_types = {"A": 0, "B": 1, "C": 2, "D": 3}

        try:
            soil_type_number = soil_types[soil_type_letter.upper()]
        except KeyError as e:
            print 'Soil type \'{0}\' not supported. Exiting...'.format(soil_type)
            raise

        return soil_type_number

    def _template_metadata(self, template_tif):
        metadata = {}

        try:
            template = gdal.Open(template_tif)
        except IOError as e:
            print template_tif + ' couldn\'t be read'
            raise

        arr = template.GetRasterBand(1).ReadAsArray()
        metadata['nCols'] = arr.shape[0]
        metadata['nRows'] = arr.shape[1]
        del arr

        metadata['geo_transform'] = template.GetGeoTransform()
        metadata['projection'] = template.GetProjection()

        return metadata

    def to_geotiff(self, filename, soil_type_letter):
        geo_metadata = self._template_metadata(self._template_tif)
        soil_type_number = self._soil_type_letter_to_number(soil_type_letter)
        
        # create dummy data
        arr = np.full((geo_metadata['nRows'], geo_metadata['nCols']),
                      soil_type_number, dtype='uint16')

        # write to output geotiff
        driver = gdal.GetDriverByName('GTiff')

        if (filename[-4:] == '.tif') or (filename[-5:] == '.tiff'): 
            out_file_name = filename
        else:
            out_file_name = filename + '.tif'
            
        outdata = driver.Create(out_file_name,
                                geo_metadata['nRows'],
                                geo_metadata['nCols'],
                                1, # number of bands in output file
                                gdal.GDT_UInt16) # data type

        outdata.SetGeoTransform(geo_metadata['geo_transform'])
        outdata.SetProjection(geo_metadata['projection'])
        outdata.GetRasterBand(1).WriteArray(arr)
        outdata.FlushCache() # writes to disk

if __name__ == '__main__':
    template_tif = sys.argv[1]
    soil_type = sys.argv[2]

    generator = UniformSoilMapGenerator(template_tif)
    generator.to_geotiff('uniform_soil_map_'+soil_type.upper()+'.tif', soil_type)
    
    print 'Done!'
