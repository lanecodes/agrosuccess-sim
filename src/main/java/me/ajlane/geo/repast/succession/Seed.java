/**
 * 
 */
package me.ajlane.geo.repast.succession;

import java.util.Arrays;

/**
 * Representation of a seed, containing its type, location and creation time.
 * 
 * @author andrew
 *
 */
public class Seed {
	
	String type;
	int creationTime;
	int row;
	int col;
	
	/**
	 * @param type
	 * 		Name of seed genera, e.g. "pine", "oak", "deciduous"
	 * @param time
	 * 		Model time-step when seed was created (used to calculate its age)
	 * @param row
	 * 		Vertical coordinate of seed in model grid 
	 * @param col
	 * 		Horizontal coordinate of seed in model grid
	 */
	Seed(String type, int time, int row, int col) {
		this.type = type;
		this.creationTime = time;
		this.row = row;
		this.col = col;
	}
	
	/**
	 * @param currentTime
	 * 		Model time for which the seed's age is to be calculated
	 * @return
	 * 		Seed's age in model time units
	 */
	public int getAge(int currentTime) {
		if (currentTime>=creationTime) {
			return currentTime - creationTime;
		} else {
			throw new IllegalArgumentException("Seed's age cannot be evaluated" + 
					" at a time before it was created.");
		}
	}
	
	public int getCol() {
		return col;
	}	
	
	public int getRow() {
		return row;
	}
	
	public void setType(String type) {
		String[] validTypes = {"pine", "oak", "deciduous"}; 
		if (Arrays.asList(validTypes).contains(type)) {
			this.type = type;
		} else {
			throw new IllegalArgumentException("Seed type must be one of 'pine', 'oak' " +
					"or 'deciduous'");
		}
	}
	
	public String getType() {
		return type;
	}
	
	public String toString() {
		return String.format("%s seed deposited in cell (%d,%d) at time %d", 
				type, row, col, creationTime);
	}
}
