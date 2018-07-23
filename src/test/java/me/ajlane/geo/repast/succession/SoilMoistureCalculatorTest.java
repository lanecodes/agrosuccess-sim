package me.ajlane.geo.repast.succession;

import java.io.IOException;

import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.geotools.data.DataSourceException;
import org.geotools.factory.Hints;
import org.geotools.gce.geotiff.GeoTiffReader;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import me.ajlane.geo.FlowConnectivityNetwork;
import repast.simphony.context.Context;
import repast.simphony.context.DefaultContext;
import repast.simphony.space.grid.StrictBorders;
import repast.simphony.valueLayer.GridValueLayer;

public class SoilMoistureCalculatorTest {
	
	private static Context<Object> context = new DefaultContext<Object>();
	private static FlowConnectivityNetwork flowMatrix;	
	private static GridValueLayer smLayer;
	private static SoilMoistureCalculator smCalc;
	
	
	/**
	 * Set up the test matrix by reading dummy data from file
	 * 
	 * @throws NotStrictlyPositiveException
	 * @throws NumberIsTooLargeException
	 * @throws DataSourceException
	 * @throws IOException
	 */
	@BeforeClass
	public static void initialiseTestMatrix() throws NotStrictlyPositiveException, NumberIsTooLargeException, DataSourceException, IOException {
		flowMatrix = new FlowConnectivityNetwork(
				new GeoTiffReader("src/test/resources/hydro_correct_dummy.tif", 
				new Hints(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, Boolean.TRUE)).read(null));
	}
	
	/**
	 * Print out the dimensions and values of a SoilMoistureCalculator. Useful
	 * for debugging.
	 * 
	 * @param smCalc
	 * 			SoilMoistureCalculator whose contents we would like to inspect
	 */
	private static void printDrainageMatrix(SoilMoistureCalculator smCalc) {
		int h = smCalc.getDrainageMatrix().getData().length;
		int w = smCalc.getDrainageMatrix().getData()[0].length;
		System.out.format("Matrix height: %d, width: %d\n\n", h, w);	
		
		for(int i=0; i<h; i++) {
			for(int j=0; j<w; j++) {
				System.out.printf("% 4.1f\t", smCalc.getDrainageMatrix().getEntry(i, j));
			}
			System.out.println();
		}
	}
	
	@Before
	private static void setupGenericContext() {
		smLayer = new GridValueLayer("soil moisture", 0, true, new StrictBorders(), new int[]{3, 3}, new int[]{0, 0});
		context.addValueLayer(smLayer);
		smCalc = new SoilMoistureCalculator(flowMatrix, 50.0, context);		
	}
	
	@Test
	public void drainageMatrixShouldBeThis() {
		//TODO check drainage matrix matches pen-and-paper check
	}
	
	@Test
	public void runoffVectorShouldBeThis() {
		//TODO check runoff vector matches pen-and-paper check
	}
	
	
	
	public static void main(String args[]) throws NotStrictlyPositiveException, NumberIsTooLargeException, DataSourceException, IOException {
		initialiseTestMatrix();
		setupGenericContext();
		
		printDrainageMatrix(smCalc);
		
		GridValueLayer slope = new DummySlopeLayer3x3("slope", "gentle");
		GridValueLayer lct = new DummyLandCoverTypeLayer3x3("lct", "pine forest");
		GridValueLayer soil = new DummySoilTypeLayer3x3("soil", "B");
		
		System.out.println("5/3: " + 5/3);
		System.out.println("5%3: " + 5%3);
		int[] c = smCalc.indexSpatialCoords(2);
		System.out.println("x: " + c[0] + " y: " + c[1]);
		
	}

}
