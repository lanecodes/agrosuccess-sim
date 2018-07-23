package me.ajlane.geo.repast.succession;

/**
 * A dummy layer useful for testing purposes
 * 
 * @author andrew
 *
 */
public class DummySlopeLayer3x3 extends DummyLayer3x3 {
	
	public DummySlopeLayer3x3(String name, String option) {
		super(name);
		
		switch(option) {
		case "gentle": setData(gentleSlope()); break;
		case "medium": setData(mediumSlope()); break;
		case "steep" : setData(steepSlope()); break;
		default: System.out.println("Dummy slope layer must be one of " + 
							"'gentle', 'medium' or 'steep'.");		
		}
		
	}
	
	private double[][] gentleSlope() {
		double array[][] = {
				{0.5, 0.5, 0.5},
				{0.5, 0.5, 0.5},
				{0.5, 0.5, 0.5}
		};
		return array;		
	}
	
	private double[][] mediumSlope() {
		double array[][] = {
				{5, 5, 5},
				{5, 5, 5},
				{5, 5, 5}
		};
		return array;		
	}
	
	private double[][] steepSlope() {
		double array[][] = {
				{15, 15, 15},
				{15, 15, 15},
				{15, 15, 15}
		};
		return array;		
	}

}
