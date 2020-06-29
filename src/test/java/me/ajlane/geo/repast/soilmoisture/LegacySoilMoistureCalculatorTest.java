package me.ajlane.geo.repast.soilmoisture;

import static org.junit.Assert.assertEquals;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import me.ajlane.geo.DummyLandCoverTypeLayer3x3;
import me.ajlane.geo.DummySlopeLayer3x3;
import me.ajlane.geo.DummySoilTypeLayer3x3;
import me.ajlane.geo.repast.GeoRasterValueLayer;
import me.ajlane.geo.repast.RepastGridUtils;
import me.ajlane.geo.repast.soilmoisture.LegacySoilMoistureCalculator;
import repast.model.agrosuccess.LscapeLayer;
import repast.simphony.context.Context;
import repast.simphony.context.DefaultContext;
import repast.simphony.space.grid.StrictBorders;
import repast.simphony.valueLayer.GridValueLayer;
import repast.simphony.valueLayer.IGridValueLayer;

public class LegacySoilMoistureCalculatorTest {

    final static Logger logger = Logger.getLogger(LegacySoilMoistureCalculatorTest.class);

	private Context<Object> context = new DefaultContext<Object>();
	private IGridValueLayer flowDirectionGrid;
	private IGridValueLayer soilMoistureLayer;
	private SoilMoistureUpdater soilMoistureCalc;

	@Before
	public void setUp() {
	  // Dummy flow direction map made in make_geo_test_data.py in agrosuccess-data
	  // Note that despite the name looking like a DEM, this is a flow direction map.
	  String flowDirFilePath = "data/test/hydro_correct_dummy.tif";
	  this.flowDirectionGrid = new GeoRasterValueLayer(flowDirFilePath,
	      LscapeLayer.FlowDir.name()).getValueLayer();
	}

	/**
	 * check soil moisture layer matches pen-and-paper check
	 */
	@Test
	public void updatedSoilMoistureValueLayerShouldBeThis() {
	    context.addValueLayer(this.flowDirectionGrid);
		//add empty soil moisture layer
		soilMoistureLayer = new GridValueLayer(LscapeLayer.SoilMoisture.name(), 0, true,
		    new StrictBorders(), new int[]{3, 3}, new int[]{0, 0});
		context.addValueLayer(soilMoistureLayer);

		// add required ValueLayer-s with dummy data to context
		context.addValueLayer(new DummySlopeLayer3x3(LscapeLayer.Slope.name(), "medium")); // all cells have slope = 5%
		context.addValueLayer(new DummySoilTypeLayer3x3(LscapeLayer.SoilType.name(), "B")); //  all cells have soil type B
		context.addValueLayer(new DummyLandCoverTypeLayer3x3(LscapeLayer.Lct.name(), "pine forest")); // all cells occupied by pine forest

		soilMoistureCalc = new LegacySoilMoistureCalculator(50.0, context);

		// update soil moisture layer with 50mm of precipitation
		soilMoistureCalc.updateSoilMoisture(50.0);

		double[][] trueSoilMoistureLayer = {
				{0+50, 48.0238 + 50, 2*48.0238 + 50 -48.0238},
				{48.0238 + 50, 50, -48.0238 + 50},
				{-48.0238 + 50, -48.0238 + 50, -48.0238 + 50}
		};

		logger.debug(RepastGridUtils.valueLayerToString(
		    context.getValueLayer(LscapeLayer.SoilMoisture.name())));

		for (int i=0; i<soilMoistureLayer.getDimensions().getHeight(); i++) {
			for (int j=0; j<soilMoistureLayer.getDimensions().getWidth(); j++){
				assertEquals(trueSoilMoistureLayer[i][j], soilMoistureLayer.get(j, 3-1-i), 0.001);
			}
		}
	}

}
