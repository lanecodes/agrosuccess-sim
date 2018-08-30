package me.ajlane.geo.repast.succession;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.geotools.data.DataSourceException;
import org.junit.BeforeClass;
import org.junit.Test;

import me.ajlane.geo.repast.GeoRasterValueLayer;
import repast.simphony.context.Context;
import repast.simphony.context.DefaultContext;
import repast.simphony.space.grid.StrictBorders;
import repast.simphony.valueLayer.GridValueLayer;

public class SoilMoistureCalculatorTest {
	
	private static Context<Object> context = new DefaultContext<Object>();
	private static GridValueLayer flowDirectionGrid;	
	private static GridValueLayer soilMoistureLayer;
	private static SoilMoistureCalculator soilMoistureCalc;
	
	static void printGridValueLayer(GridValueLayer gvl){
		for (int y=(int)gvl.getDimensions().getHeight()-1; y>=0; y--) {
			for (int x=0; x<gvl.getDimensions().getWidth(); x++) {			
				//System.out.format("x: %d, y: %d, value: %.0f\n", x, y, gvl.get(x, y));
				System.out.format("%.2f\t", (double)Math.round(gvl.get(x, y)*100)/100);
			}
			System.out.print("\n");
		}
	}	
	
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
		flowDirectionGrid = (new GeoRasterValueLayer(
				"src/test/resources/hydro_correct_dummy.tif", 
				"flow direction")).getValueLayer();
		
		System.out.println("flowDirectionGrid:");
		printGridValueLayer(flowDirectionGrid);
	}
	
	/**
	 * check soil moisture layer matches pen-and-paper check
	 */
	@Test
	public void updatedSoilMoistureValueLayerShouldBeThis() {
		//add empty soil moisture layer
		soilMoistureLayer = new GridValueLayer("soil moisture", 0, true, new StrictBorders(), new int[]{3, 3}, new int[]{0, 0});
		context.addValueLayer(soilMoistureLayer);
			
		// add required ValueLayer-s with dummy data to context
		context.addValueLayer(new DummySlopeLayer3x3("slope", "medium")); // all cells have slope = 5%
		context.addValueLayer(new DummySoilTypeLayer3x3("soil", "B")); //  all cells have soil type B
		context.addValueLayer(new DummyLandCoverTypeLayer3x3("lct", "pine forest")); // all cells occupied by pine forest
		
		soilMoistureCalc = new SoilMoistureCalculator(flowDirectionGrid, 50.0, context);
		
		// update soil moisture layer with 50mm of precipitation
		soilMoistureCalc.updateSoilMoistureLayer(50.0);
		
		double[][] trueSoilMoistureLayer = {
				{0+50, 48.0238 + 50, 2*48.0238 + 50 -48.0238},
				{48.0238 + 50, 50, -48.0238 + 50},
				{-48.0238 + 50, -48.0238 + 50, -48.0238 + 50}
		};
		
		System.out.println("soilMoistureLayer:");
		printGridValueLayer((GridValueLayer)context.getValueLayer("soil moisture"));		
		
		for (int i=0; i<soilMoistureLayer.getDimensions().getHeight(); i++) {
			for (int j=0; j<soilMoistureLayer.getDimensions().getWidth(); j++){
				assertEquals(trueSoilMoistureLayer[i][j], soilMoistureLayer.get(j, 3-1-i), 0.001);
			}
		}
	}	
	
}
