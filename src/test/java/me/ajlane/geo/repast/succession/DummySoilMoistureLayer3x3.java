package me.ajlane.geo.repast.succession;

/**
 * A dummy layer useful for testing purposes
 * 
 * @author andrew
 *
 */
public class DummySoilMoistureLayer3x3 extends DummyLayer3x3 {
	
	public DummySoilMoistureLayer3x3(String name, String option) {
		super(name);
		
		switch(option) {
		case "all hydric": setData(allHydric()); break;
		default: System.out.println("Dummy seed layer must initially be 'all hydric'");		
		}
		
	}
	
	private double[][] allHydric() {
		double array[][] = {
				{1500, 1500, 1500},
				{1500, 1500, 1500},
				{1500, 1500, 1500}
		};
		return array;		
	}

}
