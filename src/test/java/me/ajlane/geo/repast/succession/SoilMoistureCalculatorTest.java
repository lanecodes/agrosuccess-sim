package me.ajlane.geo.repast.succession;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.geotools.data.DataSourceException;
import org.geotools.factory.Hints;
import org.geotools.gce.geotiff.GeoTiffReader;
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
	@SuppressWarnings("unused")
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
	
	/**
	 * check drainage matrix matches pen-and-paper check with respect to 
	 * dummy data given in src/test/resources/hydro_correct_dummy.tif
	 */
	@Test
	public void drainageMatrixShouldBeThis() {
		double[][] trueDrainageMatrixArray = {
				{ 0, -1,  0,  1,  0,  0,  0,  0,  0,  0}, //0
				{ 1,  0, -1,  0,  1,  0,  0,  0,  0,  0}, //1
				{ 0,  1,  0,  0,  0,  1,  0,  0,  0, -1}, //2
				{-1,  0,  0,  0,  0,  0,  1,  1,  0,  0}, //3
				{ 0, -1,  0,  0,  0,  0,  0,  0,  1,  0}, //4
				{ 0,  0, -1,  0,  0,  0,  0,  0,  0,  0}, //5
				{ 0,  0,  0, -1,  0,  0,  0,  0,  0,  0}, //6
				{ 0,  0,  0, -1,  0,  0,  0,  0,  0,  0}, //7
				{ 0,  0,  0,  0, -1,  0,  0,  0,  0,  0}, //8
				{ 0,  0,  1,  0,  0,  0,  0,  0,  0,  0}  //Sink				
		};
		
		smCalc = new SoilMoistureCalculator(flowMatrix, 50.0, context);	
		RealMatrix drainageMatrix = smCalc.getDrainageMatrix();
		
		for (int i=0; i<trueDrainageMatrixArray.length; i++) {
			for (int j=0; j<trueDrainageMatrixArray[0].length; j++) {
				assertEquals(drainageMatrix.getEntry(i, j), 
						trueDrainageMatrixArray[i][j], 0.0001);
			}
		}
	}
	
	/**
	 * check runoff vector matches pen-and-paper check
	 */
	@Test
	public void runoffVectorShouldBeThis() {
		
		//add empty soil moisture layer
		smLayer = new GridValueLayer("soil moisture", 0, true, new StrictBorders(), new int[]{3, 3}, new int[]{0, 0});
		context.addValueLayer(smLayer);
			
		// add required ValueLayer-s with dummy data to context
		context.addValueLayer(new DummySlopeLayer3x3("slope", "medium")); // all cells have slope = 5%
		context.addValueLayer(new DummySoilTypeLayer3x3("soil", "B")); //  all cells have soil type B
		context.addValueLayer(new DummyLandCoverTypeLayer3x3("lct", "pine forest")); // all cells occupied by pine forest
		
		smCalc = new SoilMoistureCalculator(flowMatrix, 50.0, context);
		
		// update runoff vector in light of precipitation, p, at 50mm
		smCalc.updateRunoffVector(50.0);
		
		// curve number for all cells should be 60 given dummy layers.
		// abstraction rate, s, should be 2.54*(1000/CN -10) = 16.9333
		// Runoff, r, for each cell should be (p-0.02s)^2/(p+0.08s) = 48.0238 mm
		RealVector runoffVector = smCalc.getRunoffVector();
		
		double[] trueRunoffVector = {48.0238, 48.0238, 48.0238, 48.0238, 48.0238, 
									 48.0238, 48.0238, 48.0238, 48.0238, 50};
		// the final element is 50 as it corresponds to the sink. Using default 
		// sinkAffinityFactor so runoff equals precipitation for nodes connected
		// to sink.
		
		for (int i=0; i<runoffVector.toArray().length; i++) {
			assertEquals(trueRunoffVector[i], runoffVector.getEntry(i), 0.0001);
		}
	}
	
	/**
	 * check soil moisture layer matches pen-and-paper check
	 */
	@Test
	public void updatedSoilMoistureValueLayerShouldBeThis() {
		//add empty soil moisture layer
		smLayer = new GridValueLayer("soil moisture", 0, true, new StrictBorders(), new int[]{3, 3}, new int[]{0, 0});
		context.addValueLayer(smLayer);
			
		// add required ValueLayer-s with dummy data to context
		context.addValueLayer(new DummySlopeLayer3x3("slope", "medium")); // all cells have slope = 5%
		context.addValueLayer(new DummySoilTypeLayer3x3("soil", "B")); //  all cells have soil type B
		context.addValueLayer(new DummyLandCoverTypeLayer3x3("lct", "pine forest")); // all cells occupied by pine forest
		
		smCalc = new SoilMoistureCalculator(flowMatrix, 50.0, context);
		
		// update soil moisture layer with 50mm of precipitation
		smCalc.updateSoilMoistureLayer(50.0);
		
		double[][] trueSoilMoistureLayer = {
				{0+50, 48.0238 + 50, 2*48.0238 + 50 - 50},
				{48.0238 + 50, 50, -48.0238 + 50},
				{-48.0238 + 50, -48.0238 + 50, -48.0238 + 50}
		};
		
		for (int i=0; i<smLayer.getDimensions().getHeight(); i++) {
			for (int j=0; j<smLayer.getDimensions().getWidth(); j++){
				assertEquals(trueSoilMoistureLayer[i][j], smLayer.get(i, j), 0.001);
			}
		}
	}	
	
}
