package me.ajlane.geo.repast.succession;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class AcornPresenceProbGeneratorTest {
	
	/**
	 * For distances x<=550m probability of a cell having an acorn in it is given by:
	 *
	 * CDF(d+l/2) - CDF(d-l/2)
	 * 
	 * where CDF is the lognormal distribution's cumulative distribution function,
	 * d is the distance to the nearest seed source, and l is the cell edge length.
	 * 
	 * CDF(x; \mu, \sigma) = \frac{1}{2} + \frac{1}{2} \erf[(\ln(x) - \mu)/sqrt{2} \sigma)]
	 * 
	 * Following Pons and Pausas (2007) and Millington et al. (2009) we set $\sigma = 0.851$ and 
	 * $\mu = 3.844$. Note that these values are in log-scale. The linear scale equivalents of these
	 * are given my exp(mu) = 46.72m and exp(sigma) = 2.34m.
	 * 
	 * for a cell edge length of 25m, a cell centered 90m from the nearest seed source
	 * has the following probability of having a seed in it:
	 * 
	 * P = 0.5 * (erf[(ln(90+12.5) - 3.844)/ (sqrt(2) * 0.851)] - erf[(ln(90-12.5) - 3.844)/ (sqrt(2) * 0.851)])
	 * 	 = 0.5 * (erf[(4.629863 - 3.844)/ 1.203496] - erf[(4.350278 - 3.844)/ 1.203496]
	 *   = 0.5 * (erf[0.6529835] - erf[0.4206728])
	 *   = 0.5 * ( 0.6442315 - 0.4481037 )
	 *   = 0.0980639	
	 *   
	 *  I have also reproduced this calculation independently in Python.
	 *  
	 */
	@Test
	public void medDistanceShouldFollowExponentialDecayFunction() {
		ISeedPresenceProbGenerator probGenSmallGrid = new AcornPresenceProbGenerator(9, 25.0);
		assertEquals(0.0980639, probGenSmallGrid.getProb(90), 0.000001);
		
		ISeedPresenceProbGenerator probGenLargeGrid = new AcornPresenceProbGenerator(1024, 25.0);
		assertEquals(0.0980639, probGenLargeGrid.getProb(90), 0.000001);
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
