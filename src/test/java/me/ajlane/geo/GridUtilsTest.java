/**
 * 
 */
package me.ajlane.geo;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * @author andrew
 *
 */
public class GridUtilsTest {
	
	@Test
	public void shouldGetIndexIsFour() {
		/* 
		 * Test array:
		 * {0, 1, 2,
		 *  3, 4, 5}
		 */
		assertEquals(GridUtils.spatialCoordsToIndex(1, 1, 3), 4);		
	}
	
	@Test
	public void shouldGetSpatialCoordsOneTwo() {
		/* 
		 * Test array:
		 * {0, 1, 2,
		 *  3, 4, 5}
		 */
		int[] coords = GridUtils.indexToSpatialCoords(5, 3, 2);
		assertEquals(1, coords[0]);
		assertEquals(2, coords[1]);
	}
}
