package me.ajlane.geo.repast.succession;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class AcornPresenceProbGeneratorTest {
	
	/**
	 * For distances x<=550m probability follows the following lognormal distribution 
	 * function:
	 * 
	 * \[ 
	 * f(x) = \frac{1}{x \sigma \sqrt{2 \pi}} \exp\left[- \frac{(\ln(x) - \mu)^2}{2\sigma^2}\right] 
	 * \]
	 * with $\sigma = 2.34m$ and $\mu = 46.7m$
	 * 
	 * Hence 
	 * f(100) = 
	 *  
	 */
	@Test
	public void medDistanceShouldFollowExponentialDecayFunction() {
		ISeedPresenceProbGenerator probGenSmallGrid = new AcornPresenceProbGenerator(9, 25.0);
		assertEquals(0.011109, probGenSmallGrid.getProb(90), 0.000001);
		
		ISeedPresenceProbGenerator probGenLargeGrid = new AcornPresenceProbGenerator(1024, 25.0);
		assertEquals(0.011109, probGenLargeGrid.getProb(90), 0.000001);
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
		probGenSmallGrid = new AcornPresenceProbGenerator(n, 25.0);
		assertEquals(0.111111, probGenSmallGrid.getProb(551), 0.000001);
		
		n = 50;
		probGenSmallGrid = new AcornPresenceProbGenerator(n, 25.0);
		assertEquals(0.02, probGenSmallGrid.getProb(551), 0.000001);
			
	}
	
	@Test
	public void longDistanceLargeGridShouldBePoint001() {
		ISeedPresenceProbGenerator probGenLargeGrid;
		int n;
		
		n = 1024;
		probGenLargeGrid = new AcornPresenceProbGenerator(n, 25.0);
		assertEquals(0.001, probGenLargeGrid.getProb(551), 0.000001);
		
		n = 262144;
		probGenLargeGrid = new AcornPresenceProbGenerator(n, 25.0);
		assertEquals(0.001, probGenLargeGrid.getProb(551), 0.000001);
	}
	
	

}
