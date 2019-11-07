# -*- coding: utf-8 -*-
import unittest
import os
import numpy as np
from init_land_cover_types import (LandcoverProportionManager,
                                   RandomLandcoverGenerator)

global test_data_dir
test_data_dir = '../../../data/geographic/navarres'

class LandcoverProportionManagerTestCase(unittest.TestCase):

    def setUp(self):
        self.prop_lists = [[0.5, 0.5, 0.0, 0.0],
                           [0.0, 0.0, 0.5, 0.5],
                           [0.33, 0, 0.33, 0.33],
                           [0.25, 0.25, 0.25, 0.25]]


    def test_return_correct_original_prop_list(self):
        manager = LandcoverProportionManager(self.prop_lists[0])
        self.assertEqual(manager.original_prop_list,
                        [0.5, 0.5, 0.0, 0.0])

        manager = LandcoverProportionManager(self.prop_lists[1])
        self.assertEqual(manager.original_prop_list,
                        [0.0, 0.0, 0.5, 0.5])

        manager = LandcoverProportionManager(self.prop_lists[2])
        self.assertEqual(manager.original_prop_list,
                        [0.33, 0, 0.33, 0.33])

        manager = LandcoverProportionManager(self.prop_lists[3])
        self.assertEqual(manager.original_prop_list,
                        [0.25, 0.25, 0.25, 0.25])

    def test_return_correct_reduced_prop_list(self):
        manager = LandcoverProportionManager(self.prop_lists[0])
        self.assertEqual(manager.reduced_prop_list,
                        [0.5, 0.5])

        manager = LandcoverProportionManager(self.prop_lists[1])
        self.assertEqual(manager.reduced_prop_list,
                        [0.5, 0.5])

        manager = LandcoverProportionManager(self.prop_lists[2])
        self.assertEqual(manager.reduced_prop_list,
                        [0.33, 0.33, 0.33])

        manager = LandcoverProportionManager(self.prop_lists[3])
        self.assertEqual(manager.reduced_prop_list,
                        [0.25, 0.25, 0.25, 0.25])

    def test_return_correct_proportion_index_given_nlmpy_label(self):

        manager = LandcoverProportionManager(self.prop_lists[0])
        self.assertEqual(manager.proportion_index(0), 0)
        self.assertEqual(manager.proportion_index(1), 1)
        # don't expect nlmpy to generate these labels since only two non-zero
        # proportions given, therefore should throw ValueError-s
        self.assertRaises(ValueError, manager.proportion_index, 2)
        self.assertRaises(ValueError, manager.proportion_index, 3)

        manager = LandcoverProportionManager(self.prop_lists[1])
        self.assertEqual(manager.proportion_index(0), 2)
        self.assertEqual(manager.proportion_index(1), 3)
        # don't expect nlmpy to generate these labels since only two non-zero
        # proportions given, therefore should throw ValueError-s
        self.assertRaises(ValueError, manager.proportion_index, 2)
        self.assertRaises(ValueError, manager.proportion_index, 3)

        manager = LandcoverProportionManager(self.prop_lists[2])
        self.assertEqual(manager.proportion_index(0), 0)
        self.assertEqual(manager.proportion_index(1), 2)
        self.assertEqual(manager.proportion_index(2), 3)
        # don't expect nlmpy to generate this label  since only three non-zero
        # proportions given, therefore should throw ValueError-s
        self.assertRaises(ValueError, manager.proportion_index, 3)

        manager = LandcoverProportionManager(self.prop_lists[3])
        self.assertEqual(manager.proportion_index(0), 0)
        self.assertEqual(manager.proportion_index(1), 1)
        self.assertEqual(manager.proportion_index(2), 2)
        self.assertEqual(manager.proportion_index(3), 3)


class RandomLandCoverGeneratorTestCase(unittest.TestCase):

    def setUp(self):
        self.generator = RandomLandcoverGenerator(os.path.join(test_data_dir,
                                                               'dem.tif'))

    def tearDown(self):
        self.generator = None



    def test_generated_landscapes_match_total_proportions(self):
        props = [0.33, 0.33, 0, 0.33]
        landscape = self.generator.match_proportions(props)
        for i in range(len(props)):
            self.assertAlmostEqual(landscape.landscape_proportions('all')(i),
                                   props[i], places=2)

    def test_get_navarres_props_correct(self):
        total_props = [0.0, 0.03381643, 0.65217391, 0.31400966]
        upland_props = [0, 0, 0, 1]
        tree_line = 400
        landscape = self.generator.match_proportions(total_props, 60,
                                                     upland_props,
                                                     tree_line)
        for i in range(len(total_props)):
            self.assertAlmostEqual(landscape.landscape_proportions('all')(i),
                                   total_props[i], places=2)

        for i in range(len(upland_props)):
            self.assertAlmostEqual(landscape.landscape_proportions('upland')(i),
                                   upland_props[i], places=2)
            
if __name__ == "__main__":
    unittest.main()
