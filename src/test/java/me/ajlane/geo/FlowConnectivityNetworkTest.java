package me.ajlane.geo;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.geotools.data.DataSourceException;
import org.geotools.factory.Hints;
import org.geotools.gce.geotiff.GeoTiffReader;
import org.junit.BeforeClass;
import org.junit.Test;

public class FlowConnectivityNetworkTest {
	
	private static FlowConnectivityNetwork hydroCorrectMatrix;
	private static FlowConnectivityNetwork hydroIncorrectMatrix;
	
	@BeforeClass
	public static void initialiseTestMatrices() throws NotStrictlyPositiveException, NumberIsTooLargeException, DataSourceException, IOException {
		hydroCorrectMatrix = new FlowConnectivityNetwork(
				new GeoTiffReader("src/test/resources/hydro_correct_dummy.tif", 
				new Hints(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, Boolean.TRUE)).read(null));
		
		hydroIncorrectMatrix = new FlowConnectivityNetwork(
				new GeoTiffReader("src/test/resources/hydro_incorrect_dummy.tif", 
				new Hints(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, Boolean.TRUE)).read(null));
		
	}
	
	@Test
	public void shouldFindOneSinkInHydroCorrectGrid() throws NotStrictlyPositiveException, 
					NumberIsTooLargeException, IOException {
		
		assertEquals(1, hydroCorrectMatrix.sinkCount());
				
	}
	
	@Test
	public void shouldFindZeroSinksInHydroIncorrectGrid() throws NotStrictlyPositiveException, 
					NumberIsTooLargeException, IOException {
		
		assertEquals(0, hydroIncorrectMatrix.sinkCount());
	}
	
	@Test
	public void hydroCorrectGridShouldGenerateThisMatrix() {
		
		double[][] trueMatrixArray = {
				{0, 1, 0, 0, 0, 0, 0, 0, 0, 0}, //0
				{0, 0, 1, 0, 0, 0, 0, 0, 0, 0}, //1
				{0, 0, 0, 0, 0, 0, 0, 0, 0, 1}, //2
				{1, 0, 0, 0, 0, 0, 0, 0, 0, 0}, //3
				{0, 1, 0, 0, 0, 0, 0, 0, 0, 0}, //4
				{0, 0, 1, 0, 0, 0, 0, 0, 0, 0}, //5
				{0, 0, 0, 1, 0, 0, 0, 0, 0, 0}, //6
				{0, 0, 0, 1, 0, 0, 0, 0, 0, 0}, //7
				{0, 0, 0, 0, 1, 0, 0, 0, 0, 0}, //8
				{0, 0, 0, 0, 0, 0, 0, 0, 0, 0} //Sink				
		};
		
		for (int i=0; i<trueMatrixArray.length; i++) {
			for (int j=0; j<trueMatrixArray[0].length; j++) {
				assertEquals(hydroCorrectMatrix.getEntry(i, j), 
						trueMatrixArray[i][j], 0.0001);
			}
		}		
	}
	
}
