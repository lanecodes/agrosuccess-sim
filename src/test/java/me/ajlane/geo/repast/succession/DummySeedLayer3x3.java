package me.ajlane.geo.repast.succession;

/**
 * A dummy layer useful for testing purposes
 * 
 * @author andrew
 *
 */
public class DummySeedLayer3x3 extends DummyLayer3x3 {
	
	public DummySeedLayer3x3(String name, String option) {
		super(name);
		
		switch(option) {
		case "no seeds": setData(noSeeds()); break;
		default: System.out.println("Dummy seed layer must initially be 'no seeds'");		
		}
		
	}
	
	private double[][] noSeeds() {
		double array[][] = {
				{0, 0, 0},
				{0, 0, 0},
				{0, 0, 0}
		};
		return array;		
	}

}
