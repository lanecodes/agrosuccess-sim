/**
 * Classes used to implement a spatially random seed distribution/ colonisation model. In particular
 * {@link me.ajlane.geo.repast.colonisation.randomkernel.SpatiallyRandomSeedDisperser} implements a
 * model that uses probability distributions to estimate the likelihood of finding a seed in a
 * <emph>typical</emph> grid cell by making a strong assumption that seed sources are uniformly
 * distributed in the simulation grid. Due to its conceptual complicatedness and empirically
 * unjustified assumptions this implementation has not been used in AgroSuccess since June 2020.
 *
 * @author Andrew Lane
 */
@Deprecated
package me.ajlane.geo.repast.colonisation.randomkernel;
