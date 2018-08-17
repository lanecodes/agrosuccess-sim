package me.ajlane.geo.repast.succession;

import org.junit.Test;

import repast.simphony.context.Context;
import repast.simphony.context.DefaultContext;
import repast.simphony.valueLayer.GridValueLayer;

public class SpatiallyRandomSeedDisperserTest {
	
	static void printGridValueLayer(GridValueLayer gvl) {
		int w = (int)gvl.getDimensions().getWidth();
		int h = (int)gvl.getDimensions().getHeight();
		for (int i=0; i<h; i++) {
			for (int j=0; j<w; j++) {
				System.out.print(gvl.get(i, j) + " ");
			}
			System.out.print("\n");
		}
		System.out.print("\n");
		//System.out.println(gvl.getDimensions().getWidth());
	}	
	
	@Test
	public void windDispersedCellProbShortDistSmallGrid() {
		
	}
	

	@Test
	public void test1() {
		
		Context<Object> context = new DefaultContext<Object>();
		
		context.addValueLayer((GridValueLayer)(new DummyLandCoverTypeLayer3x3("lct", "pine, oak and burnt")));
		
		context.addValueLayer((GridValueLayer)(new DummySeedLayer3x3("pine seeds", "no seeds")));
		context.addValueLayer((GridValueLayer)(new DummySeedLayer3x3("oak seeds", "no seeds")));
		context.addValueLayer((GridValueLayer)(new DummySeedLayer3x3("deciduous seeds", "no seeds")));
	
		
		double gridCellSizeXDim = 25.0;
		double gridCellSizeYDim = 25.0;
		
		int pineSeedLifetime = 1; 
		int oakSeedLifetime = 2;
		int deciduousSeedLifetime = 2;
		
		SeedDisperser disperser = new SpatiallyRandomSeedDisperser(gridCellSizeXDim, 
				gridCellSizeYDim, pineSeedLifetime, oakSeedLifetime, deciduousSeedLifetime, 
				context);
		
		System.out.println("Land cover types:");
		printGridValueLayer((GridValueLayer)context.getValueLayer("lct"));
		
		
		System.out.println("t=0");
		System.out.println("pine seeds");
		printGridValueLayer((GridValueLayer)context.getValueLayer("pine seeds"));
		
		disperser.updateSeedLayers();
		System.out.println("t=1");
		System.out.println("pine seeds");
		printGridValueLayer((GridValueLayer)context.getValueLayer("pine seeds"));
		
		disperser.updateSeedLayers();
		System.out.println("t=2");
		System.out.println("pine seeds");
		printGridValueLayer((GridValueLayer)context.getValueLayer("pine seeds"));
		
		disperser.updateSeedLayers();
		System.out.println("t=3");
		System.out.println("pine seeds");

		printGridValueLayer((GridValueLayer)context.getValueLayer("pine seeds"));
			
	}
	
}
