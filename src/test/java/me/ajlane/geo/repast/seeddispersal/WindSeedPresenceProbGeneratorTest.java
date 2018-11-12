package me.ajlane.geo.repast.seeddispersal;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import me.ajlane.geo.repast.seeddispersal.WindSeedPresenceProbGenerator;

public class WindSeedPresenceProbGeneratorTest {
	
	/**
	 * Hard coded so for distances < 75m prob of seed presence is 0.95
	 */
	@Test
	public void shortDistanceProbShouldBePoint95() {		
		ISeedPresenceProbGenerator probGenSmallGrid = new WindSeedPresenceProbGenerator(9, 25);
		assertEquals(0.95, probGenSmallGrid.getProb(50), 0.000001);
		
		ISeedPresenceProbGenerator probGenLargeGrid = new WindSeedPresenceProbGenerator(1024, 25);
		assertEquals(0.95, probGenLargeGrid.getProb(50), 0.000001);
	}
	
	/**
	 * For distances 75m<=x<100m probability is given by:
	 * CDF(d+l/2) - CDF(d-l/2)
	 * 
	 * where CDF is the exponential distribution's cumulative distribution function,
	 * d is the distance to the nearest seed source, and l is the cell edge length.
	 * 
	 * for a cell edge length of 25m, a cell centered 90m from the nearest seed source
	 * has the following probability of having a seed in it:
	 * 
	 * P = 1 - exp(-b/MD * 90+12.5) - (1 - exp(-b/MD * 90-12.5)
	 *   = -exp(-0.05*102.5) + exp(-0.05*77.5)
	 *   = 0.014808
	 */
	@Test
	public void medDistanceShouldFollowExponentialDecayFunction() {
		ISeedPresenceProbGenerator probGenSmallGrid = new WindSeedPresenceProbGenerator(9, 25);
		assertEquals(0.014808, probGenSmallGrid.getProb(90), 0.000001);
		
		ISeedPresenceProbGenerator probGenLargeGrid = new WindSeedPresenceProbGenerator(1024, 25);
		assertEquals(0.014808, probGenLargeGrid.getProb(90), 0.000001);
	}
	
	/**
	 * Minimum probability should be given by 1/n. Ensures there should be 
	 * a seed in one cell per timestep
	 */
	@Test
	public void longDistanceSmallGridShouldDependOnGridSize() {
		ISeedPresenceProbGenerator probGenSmallGrid;
		int n;
		
		n = 9;
		probGenSmallGrid = new WindSeedPresenceProbGenerator(n, 25.0);
		assertEquals(0.111111, probGenSmallGrid.getProb(101), 0.000001);
		
		n = 50;
		probGenSmallGrid = new WindSeedPresenceProbGenerator(n, 25.0);
		assertEquals(0.02, probGenSmallGrid.getProb(101), 0.000001);
			
	}
	
	@Test
	public void longDistanceLargeGridShouldBePoint001() {
		ISeedPresenceProbGenerator probGenLargeGrid;
		int n;
		
		n = 1024;
		probGenLargeGrid = new WindSeedPresenceProbGenerator(n, 25.0);
		assertEquals(0.001, probGenLargeGrid.getProb(101), 0.000001);
		
		n = 262144;
		probGenLargeGrid = new WindSeedPresenceProbGenerator(n, 25.0);
		assertEquals(0.001, probGenLargeGrid.getProb(101), 0.000001);
	}
	
	

}
