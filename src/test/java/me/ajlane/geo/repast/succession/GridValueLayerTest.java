package me.ajlane.geo.repast.succession;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import repast.simphony.valueLayer.GridValueLayer;

/**
 * Tests to demonstrate how to use grid value layers due to confusion about
 * how they are indexed. Specifically, if we ask for gvl.get(i, j) do we get
 * the value in the ith row and jth column as we would with a matrix?
 * @author andrew
 *
 */
public class GridValueLayerTest {
	
	int[][] testArray = {{1, 2, 3},
						 {4, 5, 6},
						 {7, 8, 9}}; 
	
	GridValueLayer gvl;

	@Before
	public void setUp() throws Exception {
		gvl = new GridValueLayer("test", true, new int[]{3, 3});
		for (int i=0; i<3; i++) {
			for (int j=0; j<3; j++) {
				gvl.set(testArray[i][j], i, j);
			}
		}
	}
	
	static void printGridValueLayer(GridValueLayer gvl){
		for (int i=0; i<gvl.getDimensions().getHeight(); i++) {
			for (int j=0; j<gvl.getDimensions().getWidth(); j++) {
				System.out.print(gvl.get(i, j) + " ");
			}
			System.out.print("\n");
		}
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void showGVL() {
		printGridValueLayer(gvl);
	}

	@Test
	public void elementZeroOneShouldBeTwo() {
		assertEquals(2, (int)gvl.get(0, 1));
	}
	
	@Test
	public void elementTwoZeroShouldBeSeven() {
		assertEquals(7, (int)gvl.get(2, 0));
	}

}
