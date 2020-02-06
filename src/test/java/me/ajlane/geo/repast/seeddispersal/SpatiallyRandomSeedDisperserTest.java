package me.ajlane.geo.repast.seeddispersal;

import org.apache.log4j.Logger;
import org.junit.Test;

import me.ajlane.geo.DummyLandCoverTypeLayer3x3;
import me.ajlane.geo.DummySeedLayer3x3;
import me.ajlane.geo.repast.seeddispersal.SeedDisperser;
import me.ajlane.geo.repast.seeddispersal.SpatiallyRandomSeedDisperser;
import repast.model.agrosuccess.LscapeLayer;
import repast.simphony.context.Context;
import repast.simphony.context.DefaultContext;
import repast.simphony.valueLayer.GridValueLayer;

public class SpatiallyRandomSeedDisperserTest {

    final static Logger logger = Logger.getLogger(SpatiallyRandomSeedDisperserTest.class);

	static void printGridValueLayer(GridValueLayer gvl) {
		int w = (int)gvl.getDimensions().getWidth();
		int h = (int)gvl.getDimensions().getHeight();
		for (int i=0; i<h; i++) {
			for (int j=0; j<w; j++) {
				logger.debug(gvl.get(i, j) + " ");
			}
			logger.debug("\n");
		}
		logger.debug("\n");
	}

	@Test
	public void windDispersedCellProbShortDistSmallGrid() {

	}


	@Test
	public void test1() {

		Context<Object> context = new DefaultContext<Object>();

		context.addValueLayer((GridValueLayer)(
		    new DummyLandCoverTypeLayer3x3(LscapeLayer.Lct.name(), "pine, oak and burnt")));
		context.addValueLayer((GridValueLayer)(
		    new DummySeedLayer3x3(LscapeLayer.Pine.name(), "no seeds")));
		context.addValueLayer((GridValueLayer)(
		    new DummySeedLayer3x3(LscapeLayer.Oak.name(), "no seeds")));
		context.addValueLayer((GridValueLayer)(
		    new DummySeedLayer3x3(LscapeLayer.Deciduous.name(), "no seeds")));


		double gridCellSizeXDim = 25.0;
		double gridCellSizeYDim = 25.0;

		int pineSeedLifetime = 1;
		int oakSeedLifetime = 2;
		int deciduousSeedLifetime = 2;

		SeedDisperser disperser = new SpatiallyRandomSeedDisperser(gridCellSizeXDim,
		    gridCellSizeYDim,
		    new SeedViabilityParams(oakSeedLifetime, pineSeedLifetime, deciduousSeedLifetime),
		    new SeedDispersalParams(3.844, 0.851, 550, 5, 75, 100),
		    context);

		logger.debug("Land cover types:");
		printGridValueLayer((GridValueLayer)context.getValueLayer(LscapeLayer.Lct.name()));


		logger.debug("t=0");
		logger.debug("pine seeds");
		printGridValueLayer((GridValueLayer)context.getValueLayer(LscapeLayer.Pine.name()));

		disperser.updateSeedLayers();
		logger.debug("t=1");
		logger.debug("pine seeds");
		printGridValueLayer((GridValueLayer)context.getValueLayer(LscapeLayer.Pine.name()));

		disperser.updateSeedLayers();
		logger.debug("t=2");
		logger.debug("pine seeds");
		printGridValueLayer((GridValueLayer)context.getValueLayer(LscapeLayer.Pine.name()));

		disperser.updateSeedLayers();
		logger.debug("t=3");
		logger.debug("pine seeds");

		printGridValueLayer((GridValueLayer)context.getValueLayer(LscapeLayer.Pine.name()));

	}

}
