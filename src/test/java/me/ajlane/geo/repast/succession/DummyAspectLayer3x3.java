package me.ajlane.geo.repast.succession;

/**
 * A dummy layer useful for testing purposes
 * 
 * @author andrew
 *
 */
public class DummyAspectLayer3x3 extends DummyLayer3x3 {
	
	public DummyAspectLayer3x3(String name, String option) {
		super(name);
		
		switch(option) {
		case "all north": setData(allNorth()); break;
		case "all south": setData(allSouth()); break;
		default: System.out.println("Dummy seed layer must initially be 'all north' or 'all south'");		
		}
		
	}
	
	private double[][] allNorth() {
		double array[][] = {
				{0, 0, 0},
				{0, 0, 0},
				{0, 0, 0}
		};
		return array;		
	}
	
	private double[][] allSouth() {
		double array[][] = {
				{1, 1, 1},
				{1, 1, 1},
				{1, 1, 1}
		};
		return array;		
	}

}
