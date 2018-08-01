/**
 * 
 */
package me.ajlane.geo;

/**
 * Useful methods for working with cells organised in a 2-dimensional 
 * grid.
 * 
 * @author andrew
 *	
 */
public class GridUtils {
	
	/**
	 * 'Wraps' a spatial index up into a 2-dimensional grid, returning
	 * the coordinates of the cell with the given index in the grid.
	 * 
	 * Inverse of {@link #spatialCoordsToIndex(int, int, int)}
	 * 
	 * @param i
	 * 			Index of cell
	 * @param nX 
	 * 			Dimension of grid in x (horizontal) direction
	 * @param nY
	 * 			Dimension of grid in y (vertical) direction
	 * @return
	 * 			1D array with 2 elements such that coords[0] and coords[1] are
	 * 			the x and y coordinates of the grid cell corresponding to the i'th 
	 * 			cell, respectively.
	 */
	static int[] indexToSpatialCoords(int i, int nX, int nY) {
		int coords[] = new int[2];
		coords[0] = i%nX;
		coords[1] = i/nX;
		return coords;
	}
	
	/**
	 * 'Unwraps' a spatial grid into a 1-dimensional array of cells which
	 * can be indexed by a single number. For a given pair (x, y) specifying
	 * the location of a cell in the grid, returns the index of that cell.
	 * 
	 * Inverse of {@link #indexToSpatialCoords(int, int, int)}.
	 * 
	 * @param x
	 * 			Horizontal spatial grid index
	 * @param y
	 * 			Vertical spatial grid index
	 * @return
	 * 			Index for the FlowConnectivityMatrix row/column and Runoff vector
	 * 			elements describing the flow characteristics of spatial cell (x,y)
	 */
	static int spatialCoordsToIndex(int x, int y, int nX) {
		return y*nX + x;		
	}

}
