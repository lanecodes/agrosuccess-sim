package me.ajlane.geo;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.geotools.factory.Hints;
import org.geotools.gce.geotiff.GeoTiffReader;
import org.junit.Test;

public class FlowConnectivityNetworkTest {
	
	String hydroCorrectDat = "src/test/resources/hydro_correct_dummy.tif";
	String hydroIncorrectDat = "src/test/resources/hydro_incorrect_dummy.tif";
	
	@Test
	public void shouldFindOneSink() throws NotStrictlyPositiveException, NumberIsTooLargeException, IOException {
		GeoTiffReader reader = new GeoTiffReader(hydroCorrectDat, 
			new Hints(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, Boolean.TRUE));
		FlowConnectivityNetwork flowMatrix = new FlowConnectivityNetwork(reader.read(null));

		double[] sinkCol = flowMatrix.getColumn(flowMatrix.getSinkNode());
		double sinkCount = 0; int i = 0;
		while (i<sinkCol.length) {
			sinkCount += sinkCol[i];
			i++;
		}
		
		assertEquals(1, sinkCount, 0.0001);		
		
	}
	
	@Test
	public void shouldFindZeroSinks() throws NotStrictlyPositiveException, NumberIsTooLargeException, IOException {
		GeoTiffReader reader = new GeoTiffReader(hydroIncorrectDat, 
			new Hints(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, Boolean.TRUE));
		FlowConnectivityNetwork flowMatrix = new FlowConnectivityNetwork(reader.read(null));

		double[] sinkCol = flowMatrix.getColumn(flowMatrix.getSinkNode());
		double sinkCount = 0; int i = 0;
		while (i<sinkCol.length) {
			sinkCount += sinkCol[i];
			i++;
		}
		
		assertEquals(0, sinkCount, 0.0001);		
		
	}
	
}
