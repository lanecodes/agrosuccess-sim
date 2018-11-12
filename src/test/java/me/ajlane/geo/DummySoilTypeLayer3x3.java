package me.ajlane.geo;

/**
 * A dummy layer useful for testing purposes
 * 
 * See Millington2009 for references to more information on soil types.
 * In GridValueLayer, map A->0, B->1, C->2, D->3
 * 
 * @author andrew
 *
 */
public class DummySoilTypeLayer3x3 extends DummyLayer3x3 {
	
	
	public DummySoilTypeLayer3x3(String name, String option) {
		super(name);
		
		switch(option) {
		case "A": setData(typeA()); break;
		case "B": setData(typeB()); break;
		case "C" : setData(typeC()); break;
		case "D" : setData(typeD()); break;
		default: System.out.println("Dummy soil type layer must be one of " + 
							"'wheat', 'burnt', 'pine forest' or 'oak forest'.");		
		}
		
	}
	
	private double[][] typeA() {
		double array[][] = {
				{0, 0, 0},
				{0, 0, 0},
				{0, 0, 0}
		};
		return array;		
	}
	
	private double[][] typeB() {
		double array[][] = {
				{1, 1, 1},
				{1, 1, 1},
				{1, 1, 1}
		};
		return array;		
	}
	
	private double[][] typeC() {
		double array[][] = {
				{2, 2, 2},
				{2, 2, 2},
				{2, 2, 2}
		};
		return array;		
	}
	
	private double[][] typeD() {
		double array[][] = {
				{3, 3, 3},
				{3, 3, 3},
				{3, 3, 3}
		};
		return array;		
	}
}
