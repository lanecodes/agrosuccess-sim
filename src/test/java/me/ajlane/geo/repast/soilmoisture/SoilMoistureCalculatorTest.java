package me.ajlane.geo.repast.soilmoisture;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.log4j.Logger;
import org.geotools.data.DataSourceException;
import org.junit.BeforeClass;
import org.junit.Test;

import me.ajlane.geo.DummyLandCoverTypeLayer3x3;
import me.ajlane.geo.DummySlopeLayer3x3;
import me.ajlane.geo.DummySoilTypeLayer3x3;
import me.ajlane.geo.repast.GeoRasterValueLayer;
import me.ajlane.geo.repast.soilmoisture.SoilMoistureCalculator;
import repast.model.agrosuccess.LscapeLayer;
import repast.simphony.context.Context;
import repast.simphony.context.DefaultContext;
import repast.simphony.space.grid.StrictBorders;
import repast.simphony.valueLayer.GridValueLayer;

public class SoilMoistureCalculatorTest {

    final static Logger logger = Logger.getLogger(SoilMoistureCalculatorTest.class);

	private static Context<Object> context = new DefaultContext<Object>();
	private static GridValueLayer flowDirectionGrid;
	private static GridValueLayer soilMoistureLayer;
	private static SoilMoistureCalculator soilMoistureCalc;

	static void printGridValueLayer(GridValueLayer gvl){
		for (int y=(int)gvl.getDimensions().getHeight()-1; y>=0; y--) {
			for (int x=0; x<gvl.getDimensions().getWidth(); x++) {
				// TODO: work out how to format this to make the log more readable
				logger.debug(String.format("%.2f\t", (double)Math.round(gvl.get(x, y)*100)/100));
				// System.out.format("%.2f\t", (double)Math.round(gvl.get(x, y)*100)/100);
			}
			logger.debug("\n");
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
				"data/test/hydro_correct_dummy.tif",
				"flow direction")).getValueLayer();

		logger.debug("flowDirectionGrid:");
		printGridValueLayer(flowDirectionGrid);
	}

	/**
	 * check soil moisture layer matches pen-and-paper check
	 */
	@Test
	public void updatedSoilMoistureValueLayerShouldBeThis() {
		//add empty soil moisture layer
		soilMoistureLayer = new GridValueLayer(LscapeLayer.SoilMoisture.name(), 0, true,
		    new StrictBorders(), new int[]{3, 3}, new int[]{0, 0});
		context.addValueLayer(soilMoistureLayer);

		// add required ValueLayer-s with dummy data to context
		context.addValueLayer(new DummySlopeLayer3x3(LscapeLayer.Slope.name(), "medium")); // all cells have slope = 5%
		context.addValueLayer(new DummySoilTypeLayer3x3(LscapeLayer.SoilType.name(), "B")); //  all cells have soil type B
		context.addValueLayer(new DummyLandCoverTypeLayer3x3(LscapeLayer.Lct.name(), "pine forest")); // all cells occupied by pine forest

		soilMoistureCalc = new SoilMoistureCalculator(flowDirectionGrid, 50.0, context);

		// update soil moisture layer with 50mm of precipitation
		soilMoistureCalc.updateSoilMoistureLayer(50.0);

		double[][] trueSoilMoistureLayer = {
				{0+50, 48.0238 + 50, 2*48.0238 + 50 -48.0238},
				{48.0238 + 50, 50, -48.0238 + 50},
				{-48.0238 + 50, -48.0238 + 50, -48.0238 + 50}
		};

		logger.debug("soilMoistureLayer:");
		printGridValueLayer((GridValueLayer)context.getValueLayer(LscapeLayer.SoilMoisture.name()));

		for (int i=0; i<soilMoistureLayer.getDimensions().getHeight(); i++) {
			for (int j=0; j<soilMoistureLayer.getDimensions().getWidth(); j++){
				assertEquals(trueSoilMoistureLayer[i][j], soilMoistureLayer.get(j, 3-1-i), 0.001);
			}
		}
	}

}
