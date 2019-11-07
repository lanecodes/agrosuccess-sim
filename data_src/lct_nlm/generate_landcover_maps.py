# -*- coding: utf-8 -*-
"""Create a zip cache including a geotiff and landcover proportion summary."""
import zipfile
import csv
import os
from init_land_cover_types import RandomLandcoverGenerator

class LandcoverCacheGenerator(object):
    def __init__(self, dem_filename, total_props, upland_props=None,
                 tree_line=None):

        self.generator = RandomLandcoverGenerator(dem_filename)
        self.total_props = total_props
        self.upland_props = upland_props
        self.tree_line = tree_line
        self.generated_file_list_buffer = []

    def get_scores(self, landscape, total_props):
        scores = []
        for i, prop in enumerate(total_props):
            scores.append([i, prop, landscape.landscape_proportions('all')(i)])
        return scores

    def write_score_csv(self, csv_filename, landscape, total_props):
        with open(csv_filename, "w") as f:
            writer = csv.writer(f)
            writer.writerow(['index', 'target_prop', 'landscape_prop'])
            writer.writerows(self.get_scores(landscape, total_props))

    def generate_landscape_files(self, landscape_num):
        landscape = self.generator.match_proportions(self.total_props, 60,
                                                     self.upland_props,
                                                     self.tree_line)
        landscape_file = 'init-landcover' + str(landscape_num) + '.tif'
        score_csv_file = 'init-landcover' + str(landscape_num) + '.csv'
        landscape.to_geotiff(landscape_file)
        self.write_score_csv(score_csv_file, landscape, self.total_props)
        self.generated_file_list_buffer.append(landscape_file)
        self.generated_file_list_buffer.append(score_csv_file)

    def make_cache(self, cache_name, num_landscapes):
        with zipfile.ZipFile(cache_name, 'w') as z:
            for i in range(num_landscapes):
                print 'making file {0} of {1}'.format(i, num_landscapes)
                self.generate_landscape_files(i)
                for f in self.generated_file_list_buffer:
                    z.write(f)
                    os.remove(f)
                self.generated_file_list_buffer = []

if __name__ == '__main__':
    gen = LandcoverCacheGenerator('../../../data/geographic/navarres/dem.tif',
                                  [0.0, 0.03381643, 0.65217391, 0.31400966],
                                  [0, 0, 0, 1], 400)
    gen.make_cache('navarres.zip', 5)
                           
                    
                    
        

        
        

    

    
        
        
        
