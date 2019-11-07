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
landscape_cover.to_geotiff('my_random_landscape.tif')

Optionally we can also provide an upland land cover proportion list. This adds
an additional contraint to enforce that a speci

match_proportions(lct_props, iterations=10, upland_props=None, tree_line=None)

RandomLandscapeGenerator
Uses DEM template combined with land cover proportion constraints to generate a
random landscape using the NLMpy methods randomClusterNN and classifyArray
"""
import numpy as np
from osgeo import gdal
import nlmpy

from map_generator import MapGenerator

class LandscapeCoverage(object):

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
            raise ValueError('dimensions of landcover_array, {0}, and ',
                             'upland_array, {1}, must match.'.format(
                                 self.landcover_array.shape, array.shape))

    def landscape_proportions(self, subset='all'):
        """Return a function assigning land cover proportion to land cover labels.
        
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
            raise ValueError('subset must be \'all\', \'upland\', or ',
                             '\'lowland\'')

        n = float(included_array.size)
        unique, counts = np.unique(included_array, return_counts=True)
        prop_dict = dict(zip(unique, counts/n))

        def label_proportion(label):
            try:
                return prop_dict[label]
            except KeyError:
                return 0.0

        return label_proportion


    def to_geotiff(self, filename):
        driver = gdal.GetDriverByName('GTiff')

        if (filename[-4:] == '.tif') or (filename[-5:] == '.tiff'): 
            out_file_name = filename
        else:
            out_file_name = filename + '.tif'
            
        outdata = driver.Create(out_file_name,
                                self.landcover_array.shape[0],
                                self.landcover_array.shape[1],
                                1, # number of bands in output file
                                gdal.GDT_UInt16) # data type

        outdata.SetGeoTransform(self.geo_transform)
        outdata.SetProjection(self.projection)
        outdata.GetRasterBand(1).WriteArray(self.landcover_array)
        outdata.FlushCache()

class LandcoverProportionManager(object):
    def __init__(self, prop_list):
        """Responsible for managing list of land cover proportions 

        When using nlmpy to generate a random landscape, it can be quite
        unreliable with respect to which labels are used for each class. For
        example, if passed a list of proportions such as [0.0, 0.0, 0.5, 0.5],
        one might expect that it would produce a map with labels 2 and 3
        (because entries 0 and 1 have no representation in the landcover
        proportions). However, in this case nlmpy.classifyArray produces a map
        with labels 0 and 3. Which of the entries in `prop_list` do these
        correspond to?

        To address this ambiguity, this class will provide a reduced_prop_list
        which excludes landcover types with no representation
        (i.e. proportion=0). This can be given to nlmpy.classifyArray to
        generate a map with labels ranging from 0 to len(reduced_prop_list)-1.

        This class will also provide a `nlmpy_label_to_index` method which takes an
        nlmpy generated label and answers the question: 'given this label, what
        was the index of the original proportion list which it corresponds to?'

        Args:
            prop_list (list of floats): the landcover proportions of the
            landcover types to be represented in the map. The proportion of
            landcover type 0 is in prop_list[0] etc.
        """
        self._prop_list = prop_list
        self.proportion_index = self._nlmpy_label_to_prop_index_map(prop_list)

    @property
    def original_prop_list(self):
        return self._prop_list        

    @property
    def reduced_prop_list(self):
        return [i for i in self._prop_list if i<>0]

    def _nlmpy_label_to_prop_index_map(self, prop_list):
        nlmpy_label_to_prop_index_dict = {}
        new_index = 0
        for (i, prop) in enumerate(self._prop_list):
            if prop <> 0:
                nlmpy_label_to_prop_index_dict[new_index] = i
                new_index += 1

        def get_prop_index(nlmpy_label):
            """Index of original proportion list for given nlmpy label."""
            if nlmpy_label in nlmpy_label_to_prop_index_dict.keys():
                return nlmpy_label_to_prop_index_dict[nlmpy_label]
            else:
                raise ValueError('nlmpy label \'{0}\' does not exist'
                                 .format(nlmpy_label))

        return get_prop_index                        
            
        
class RandomLandcoverGenerator(MapGenerator):
    def __init__(self, dem_filename):
        """Initialise RandomLandscapeGeneratorwith a path to a dem file.
        
        Args:
            dem_filename (str): A file path to the digital elevation model file
            which will be used in the construction of the random land cover maps.
        """
        super(RandomLandcoverGenerator, self).__init__(dem_filename)
        self._upland_array = None # stores boolean numpy array, true in upland

    def _lowland_props(self, dem_array, rho_c_s, rho_c_hi_s, epsilon):
        """Calculate lowland land-cover proportions given highland and totals.

        Args:
            dem_array (numpy.array): Digital Elevation Model used to distinguish
                highland areas from lowland.
            rho_c_s (list of float): Overall land cover proportions, one float per
                land cover type label.
            rho_c_hi_s (list of float): Land cover proportions in the highlands
            epsilon (int): Elevation of the tree line

        Returns:
            list of floats: the land cover proportions in the lowlands consistent
                with having rho_c_s overall and rho_c_hi_s in the highlands.
        """
        def get_N_tot(dem_array):
            """Get the number of cells in a DEM passed as a numpy.ndarray"""
            return dem_array.size

        def get_N_hi(dem_array, epsilon):
            """Get number of cells above the tree line in dem_array."""
            return dem_array[dem_array>=epsilon].size

        def get_N_lo(dem_array, epsilon):
            """Get number of cells below or on the tree line in dem_array."""
            return dem_array[dem_array<epsilon].size

        def get_rho_c_lo(dem_array, rho_c, rho_c_hi, epsilon):
            """Calculate rho_c_lo using DEM, data, upland landcover proportion."""
            num = (get_N_tot(dem_array)*rho_c -
                    get_N_hi(dem_array, epsilon)*rho_c_hi)

            if num < 0:
                raise ValueError('rho_c_lo results in negative lowland ',
                                 'proportion of landcover. Reduce proportion ',
                                 'in highland.')
            den = get_N_lo(dem_array, epsilon)
            return float(num)/den    
    
        num_rho_c = len(rho_c_s)
        if num_rho_c <> len(rho_c_hi_s):
            raise ValueError('Numbers of provided total land-cover proportions ',
                    'and specified highland proportions must match.')

        rho_c_lo_s = []
        for i in range(num_rho_c):
            rho_c_lo_s.append(get_rho_c_lo(dem_array, rho_c_s[i], rho_c_hi_s[i],
                                           epsilon))   

        return rho_c_lo_s

    def _type_labels_to_input_proportion_index_mapper(self, unique_labels,
                                                      props):
        """Functions map nlmpy generated labels to indices of proportions list.

        The numerical labels in the generated landscape should correspond to
        the indices of the land cover proportions list provided to
        `match_proportions`. E.g. is we passed [0.0, 0.0, 0.5, 0.5] 

"""

    def _generate_random_cluster_array(self, dem_array, lct_props,
                                       upland_props=None, tree_line=None):
        nRows = self.template_data.shape[0]
        nCols = self.template_data.shape[1]
        percolation_threshold = 0.59 # sets spatial scale of clusters
        if upland_props:
            lowland_props = self._lowland_props(dem_array, lct_props,
                                                upland_props, tree_line)

            low_prop_manager = LandcoverProportionManager(lowland_props)
            up_prop_manager = LandcoverProportionManager(upland_props)
            self._upland_array = dem_array>=tree_line
                
            # neutral landscape model applicable to the lowlands
            nlm_lo = nlmpy.randomClusterNN(nRows, nCols, percolation_threshold)
            nlm_lo = nlmpy.classifyArray(
                nlm_lo, low_prop_manager.reduced_prop_list).astype('int16')
            nlm_lo = np.vectorize(low_prop_manager.proportion_index)(nlm_lo)

            # neutral landscape model applicable to the highlands
            nlm_hi = nlmpy.randomClusterNN(nRows, nCols, percolation_threshold)
            nlm_hi = nlmpy.classifyArray(
                nlm_hi, up_prop_manager.reduced_prop_list).astype('int16')
            nlm_hi = np.vectorize(up_prop_manager.proportion_index)(nlm_hi)

            # combined neutral landscape model
            nlm = np.where(dem_array<tree_line, nlm_lo, nlm_hi)

        else:
            prop_manager = LandcoverProportionManager(lct_props)
            nlm = nlmpy.randomClusterNN(nRows, nCols, percolation_threshold)
            nlm = nlmpy.classifyArray(
                nlm, prop_manager.reduced_prop_list).astype('int16')
            nlm = np.vectorize(prop_manager.proportion_index)(nlm)

        return nlm            

    def _score_landscape(self, trial_landscape, lct_props, upland_props=None):
        """Return root mean squared error over target landcover proporitons.

        If upland_props is specified, construct a vector over 
        [lct_props, upland_props] and report the error with respect to
        equivalent values in trial_landscape.

        Lowest score wins
        """
        target_values = np.array(lct_props)
        landscape_values = np.array(
            [trial_landscape.landscape_proportions('all')(i)
             for i in range(len(lct_props))])

        if upland_props:
            target_values = np.append(target_values, upland_props)
            landscape_values = np.append(
                landscape_values,
                [trial_landscape.landscape_proportions('upland')(i)
                 for i in range(len(upland_props))])

        rmse = np.sqrt(((target_values - landscape_values) ** 2).mean())

        return rmse                 

    def match_proportions(self, lct_props, iterations=30, upland_props=None,
                          tree_line=None):
        """Return a LandscapeCoverage representing a random landscape.

        Args: 
            lct_props (list): The target landscape proportions which should be 
                occupied by the resulting landcover map. The map will contain
                `len(lct_props)` labels, and the landcover type whose proportion 
                is specified by `lct_props[i]` will be labelled `i` in the 
                resulting landscape.
            interations (int, optional): The number of times the MRC algoreithm 
                should be run in a bid to obtain a landscape with the closest
                possible match to the specified landcover proportions.
            upland_props (list, optional): An additional constraint on the
                landcover allocation. This specifies the upland proportions. 
                The MRC algorithm will then be used to attempt to adjust the
                lowland landcover proportions so the overall landscape will
                match lct_props. Require that len(lct_props)==len(upland_props)
            tree_line (int, semi-optional): The elevation which divides the
                lowland from the upland. Required if upland_props specified.

        Returns:
            landscape (LandscapeCoverage): Representation of generated
            landscape including geographic transform and crs.
        """
        if upland_props:
            if len(upland_props)!=len(lct_props):
                raise ValueError('Elements of upland_props must match 1:1 ',
                                 'with elements of lct_props')
            if not tree_line:
                raise ValueError('If upland_props is specified, tree_line ',
                                 'must be specified as well')

        best_landscape = None
        best_score = float('inf')
        improvement_counter = 0             
                                       
        for i in range(iterations):
            # Generate array
            array = self._generate_random_cluster_array(
                self.template_data, lct_props, upland_props=upland_props,
                tree_line=tree_line)

            # construct landscape
            landscape = LandscapeCoverage(array, self.geo_transform,
                                          self.projection, self._upland_array)

            score = self._score_landscape(landscape, lct_props, upland_props)
            if score < best_score:
                best_score = score
                best_landscape = landscape                
                improvement_counter += 1

        print 'Improved landscape {0} times over {1} iterations'.format(
            improvement_counter, iterations)

        return best_landscape
