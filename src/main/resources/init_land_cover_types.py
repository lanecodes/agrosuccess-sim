# -*- coding: utf-8 -*-
"""
Provide tools to generate a starting land cover type map using a modified
random cluster algorithm.

Resulting maps will have integer values which correspond to the indices of the
provided land cover proportion lists. 

For example, if we want to generate a landscape with landcover proportions
given by 10% deciduous forest, 30% pine forest, 20% oak forest and 40%
shrubland

generator = RandomLandscapeGenerator('my_dem.tif')
landscape_cover = generator.match_proportions([0.1, 0.3, 0.2, 0.4])

Optionally we can also provide an upland land cover proportion list. This adds
an additional contraint to enforce that a speci

match_proportions(lct_props, iterations=10, upland_props=None, tree_line=None)

RandomLandscapeGenerator
Uses DEM template combined with land cover proportion constraints to generate a
random landscape using the NLMpy methods randomClusterNN and classifyArray
"""
from osgeo import gdal, osr

class LandscapeCoverage:

    def __init__(self, landcover_array, geo_transform, projection,
                 upland_array=None):
        self.landcover_array = landcover_array
        self.geo_transform = geo_transform
        self.projection = projection

        # boolean numpy array, true in upland cells, false in lowland
        self._upland_array = upland_array

    @property
    def upland_array(self):
        return self._upland_array

    @upland_array.setter
    def upland_array(self, array):
        if array.shape == self.landcover_array.shape:
            self._upland_array = array
        else:
            raise ValueError('dimensions of landcover_array, {0}, and '\
                             'upland_array, {1}, must match.'.format(
                                 self.landcover_array.shape, array.shape)    

    def landscape_proportions(self, subset='all'):
        """Return a dict assigning land cover proportion to land cover labels.
        
        Can choose to return proportions for the whole landscape, or looking
        at upland or lowland individually.
        """
        if subset == 'all':
            included_array = self.landcover_array
        elif subset == 'upland':
            included_array = self.landcover_array[self._upland_array]
        elif subset == 'lowland':
            included_array = self.landcover_array[~self._upland_array]
        else:
            raise ValueError('subset must be \'all\', \'upland\', or '\
                             '\'lowland\'')

        n = float(included_array.size)
        unique, counts = np.unique(included_array, return_counts=True)
        return dict(zip(unique, counts/n))

    def to_geotiff(self, filename):
        driver = gdal.GetDriverByName('GTiff')

        if (filename[-4:] == '.tif') or (filename[-5:] == '.tiff'): 
            out_file_name = filename
        else:
            out_file_name = filename + '.tif'
            
        outdata = driver.Create(out_file_name,
                                array.shape[0],
                                array.shape[1],
                                1, # number of bands in output file
                                gdal.GDT_UInt16) # data type

        outdata.SetGeoTransform(self.geo_transform)
        outdata.SetProjection(self.projection)
        outdata.GetRasterBand(1).WriteArray(self.landcover_array)
        outdata.FlushCache() # writes to disk
        

