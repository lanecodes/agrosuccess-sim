package me.ajlane.geo.repast.succession;

import repast.simphony.valueLayer.GridValueLayer;

/**
 * A dummy layer useful for testing purposes
 * 
 * @author andrew
 *
 */
public class DummyLayer3x3 extends GridValueLayer {
	
	public DummyLayer3x3(String name) {
		super(name, true, new int[]{3,3});
				
	}
	
	protected void setData(double[][] data) {
		for(int i=0; i<3; i++){
			for(int j=0; j<3; j++){
				this.set(data[i][j], i, j);
			}
		}
	}
}
