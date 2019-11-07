# -*- coding: utf-8 -*-
"""
Base class used by things which generate maps based a geotiff file as a template.
"""
from osgeo import gdal, osr

class MapGenerator(object):

    def __init__(self, template_tif):
        """Initialise MapGenerator with a path to a template file.
        
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

    @property
    def template_data(self):
        try:
            template = gdal.Open(self._template_tif)
        except IOError as e:
            print self._template_tif + ' couldn\'t be read'
            raise

        band = template.GetRasterBand(1)
        arr = band.ReadAsArray()

        return arr

    @property
    def geo_transform(self):
        return self._template_metadata(self._template_tif)['geo_transform']

    @property
    def projection(self):
        return self._template_metadata(self._template_tif)['projection']

    @property
    def ncols(self):
        return self._template_metadata(self._template_tif)['nCols']

    @property
    def nrows(self):
        return self._template_metadata(self._template_tif)['nrows']

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
