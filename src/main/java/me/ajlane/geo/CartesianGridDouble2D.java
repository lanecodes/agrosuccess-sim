package me.ajlane.geo;

import me.ajlane.geo.repast.GridValueLayerAdapter;
import me.ajlane.geo.repast.ValueLayerAdapter;

/**
 * <p>
 * Rectangular grid composed of cells addressed by {@link GridLoc} objects. The origin is the lower
 * left-hand corner of the grid, has coordinates (0, 0), and value given by
 * {@code CartesianGridDouble2D.getValue(new GridLoc(0, 0));}.
 * </p>
 *
 * <p>
 * In AgroSuccess we follow Repast Simphony (North et al. 2013) by organising spatial data as a
 * collection of layer objects (rasters), each of which contains a single value (e.g. elevation,
 * soil moisture etc) for each cell in the grid. This is opposed to organising the same data as an
 * array of cells, each of which contains a value for all the quantities which are represented in a
 * model.
 * </p>
 *
 * <p>
 * There is a closer relationship between the elevation values of three neighbouring grid cells than
 * there are between a single cell's soil moisture, elevation and the fact of whether or not it
 * contains acorns, for example. This can be seen by considering the likelihood of an algorithm
 * using the elevation values of neighbouring cells, e.g. to calculate slope. The layer-based
 * approach offers a greater degree of cohesion (McConnell, 2004) because if a class needs to know
 * about e.g. land-cover type and elevation, it can be passed layers specifying how these quantities
 * change over space explicitly, so a reader can see that the class uses those quantities at a
 * glance. By contrast if all values are passed around as a complete array, it it less transparent
 * which quantities are actually being used by the client code. Additionally, classes which use
 * value layers are easier to test than classes which depend on all-value arrays because we don't
 * need to mock values for the other quantities which are present in the model but aren't relevant
 * to the class under test. Finally, every time a new quantity is added to the model, the master
 * grid cell object would need to chance. Conversely in a layer-based approach we need only add a
 * new instance of a layer object.
 * </p>
 *
 * <p>
 * Given the similarity between our layer-based implementation of grid layers and that of Repast
 * Simphony, we comment here on why we chose to introduce our own interface which reproduces similar
 * functionality to Repast's. Note that through the {@link GridValueLayerAdapter} and
 * {@link ValueLayerAdapter} it is possible to use repast grid <emph>implementations</emph>
 * alongside the interface defined here.
 * </p>
 *
 * <h3>Strive for loose coupling</h3>
 * <p>
 * The submodels in the environmental modelling components of AgroSuccess constitute reasonably
 * complicated cellular automata models in their own right. By maintaining loose coupling between
 * these components and the classes from the modelling framework used to represent spatial grids
 * (Repast Simphony in our case) we create the possibility for future use of these components
 * independently of the framework in future. This was a priority for us because we have had
 * difficulty installing Repast Simphony libraries from Maven and are concerned about the ease with
 * which future users would be able to reuse our code if it had Repast Simphony as a dependency.
 * </p>
 *
 * <h3>Create opportunities for optimisation</h3>
 * <p>
 * As of version 2.7 Repast Simphony provides classes representing a spatial grid which stores
 * {@double} values (classes implementing {@link repast.simphony.valueLayer.ValueLayer}) but no
 * corresponding class which can store {@code int} or {@link java.lang.Enum} in a spatial grid.
 * These would be more natural and better performing data types for some of the spatially varying
 * quantities in our models. Therefore we have designed this interface with the creation of
 * analogous interfaces corresponding to these types in mind.
 * </p>
 *
 * <h3>Reduce complexity and ambiguity in the grid layer API</h3>
 * <p>
 * There has evidently been some effort in Repast Simphony to make spatial grids which can have
 * arbitrary dimensions (see e.g. {@link repast.simphony.valueLayer.ValueLayer#get(double...)}).
 * However, this creates ambiguity about the order in which axes are specified which has led to a
 * proliferation in bugs and corresponding defensive comments in our own work. Furthermore as our
 * work is geographical, the natural abstraction for our models is always a set of 2-dimensional
 * spatial grids. Hence we have taken the opportunity to create our own interface which reduces this
 * ambiguity and improves readability.
 * </p>
 *
 * <h3>References</h3>
 * <p>
 * McConnell, S. (2004). Code Complete (2nd ed.). Microsoft Press
 * </p>
 *
 * <p>
 * North, M. J., Collier, N. T., Ozik, J., Tatara, E. R., Macal, C. M., Bragen, M., & Sydelko, P.
 * (2013). Complex adaptive systems modeling with repast simphony. Complex Adaptive Systems
 * Modeling, 1(1), 1–26. https://doi.org/10.1186/2194-3206-1-3
 * </p>
 *
 * @author Andrew Lane
 */
public interface CartesianGridDouble2D {

  /**
   * @return Dimensions of grid
   */
  GridDimensions getDimensions();

  /**
   * @return Total number of grid cells
   */
  int getSize();

  /**
   * @param loc Address of grid cell to get value for
   * @return Value of grid at {@code loc}
   */
  double getValue(GridLoc loc);

}
