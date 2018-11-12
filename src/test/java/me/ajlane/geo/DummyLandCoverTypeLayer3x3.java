package me.ajlane.geo;

/**
 * A dummy layer useful for testing purposes
 * 
 * @author andrew
 *
 */
public class DummyLandCoverTypeLayer3x3 extends DummyLayer3x3 {
	
	public DummyLandCoverTypeLayer3x3(String name, String option) {
		super(name);
		
		switch(option) {
		case "wheat": setData(wheat()); break;
		case "burnt": setData(burnt()); break;
		case "pine forest" : setData(pineForest()); break;
		case "oak forest" : setData(oakForest()); break;
		case "pine, oak and burnt" : setData(pineOakBurnt()); break;
		default: System.out.println("Dummy landcover type layer must be one of " + 
							"'wheat', 'burnt', 'pine forest' or 'oak forest'.");		
		}
		
	}
	
	private double[][] wheat() {
		double array[][] = {
				{3, 3, 3},
				{3, 3, 3},
				{3, 3, 3}
		};
		return array;		
	}
	
	private double[][] burnt() {
		double array[][] = {
				{1, 1, 1},
				{1, 1, 1},
				{1, 1, 1}
		};
		return array;		
	}
	
	private double[][] pineForest() {
		double array[][] = {
				{6, 6, 6},
				{6, 6, 6},
				{6, 6, 6}
		};
		return array;		
	}
	
	private double[][] oakForest() {
		double array[][] = {
				{9, 9, 9},
				{9, 9, 9},
				{9, 9, 9}
		};
		return array;		
	}
	
	private double[][] pineOakBurnt() {
		double array[][] = {
				{6, 6, 1},
				{1, 1, 1},
				{9, 9, 9}
		};
		return array;		
	}

}
