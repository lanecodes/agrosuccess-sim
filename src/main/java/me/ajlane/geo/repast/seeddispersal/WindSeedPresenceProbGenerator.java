package me.ajlane.geo.repast.seeddispersal;

/**
 * Generate the probability of finding an acorn (oak seed) given the distance of that 
 * cell from the closest source of acorns.
 * 
 * The probability of a wind dispersed seed being found in a cell at a given time is
 * determined by the following probability-assigning procedure:
 * 
 *  p(x) = 0.95 if x <= ED
 *  p(x) = \exp^{-b/MD * x} if  ED<x<=MD
 *  p(x) = 0.001 if x>MD 
 * 
 * Following Millington et al. (2009) we set $b=5$ is the distance-decrease parameter,
 * $ED=75$m is the minimum distance for which seed dispersal is exponentially 
 * distributed, and $MD=100$m is the maximum distance for which seeds are exponentially
 * distributed. 
 * 
 * <strong>References</strong> 
 * Millington, J. D. A., Wainwright, J., Perry, G.
 * L. W., Romero-Calcerrada, R., & Malamud, B. D. (2009). Modelling
 * Mediterranean landscape succession-disturbance dynamics: A landscape
 * fire-succession model. Environmental Modelling and Software, 24(10),
 * 1196–1208. https://doi.org/10.1016/j.envsoft.2009.03.013
 * @author andrew
 *
 */
public class WindSeedPresenceProbGenerator implements ISeedPresenceProbGenerator {
	
	// wind distributed seed parameters
	private double distanceDecreaseParam = 5;
	private double minExponentialDistance = 75;
	private double maxExponentialDistance = 100;
	
	private double cellSize;
	private double minProb; // minimum probability for a cell to contain a species' seeds
	
	/**
	 * @param n
	 * 		Total number of cells in the model grid. Used to determine the minimum 
	 * 		probability of finding a wind dispersed seed in any given cell such that 
	 * 		we always expect to have one such seed in the model. 
	 * @param cellSize
	 * 		The edge length of each simulation cell. Used to consider the area over
	 * 		which a seed might land within any given simulation cell		
	 */
	WindSeedPresenceProbGenerator(int n, double cellSize) {
		this.cellSize = cellSize;
		
		// minProb always high enough to ensure at least one seed for
		// each species exists in model (guarantee 0 seeds for a species is 
		// not an adsorbing state)
		minProb = n > 1000 ? 0.001 : 1.0/n;
	}
	
	/**
	 * DEPRECIATED, use math commons exponential distribution instead
	 * Effectively a modified exponential distribution. \exp^{-b/MD * x} as opposed
	 * to b/MD \exp^{-b/MD * x} which would be the probability distribution proper.
	 * @param x
	 * 		Single argument of the function
	 * @return
	 */
	@SuppressWarnings("unused")
	private double exponentialDecayFunction(double x) {
		return Math.exp(-(distanceDecreaseParam/maxExponentialDistance)*x);
	}
	
	/**
	 * Implemented because org.apache.commons.math3.distribution.ExponentialDistribution.cumulativeProbability
	 * was returning 1.0 for all values. Hopefully just a problem with their implementation
	 * of this distribution 
	 *  
	 * @param x
	 * 		Value at which to evaluate the exponential CDF
	 * @return
	 */
	private double cumulativeExponentialDistributionFunction(double x) {
		double lambda = distanceDecreaseParam/ maxExponentialDistance;
		return 1 - Math.exp(-lambda * x);
	}
	
	/**
	 * We want probability random variate $X$ is within a cell with edge
	 * length $l$ centered at distance $d$ from the seed source. Hence what we need to
     * calculate is
	 *
	 *	\begin{equation}
	 *	P(X>d-l/2 \land X \leq d+l/2) = P(X \leq d+l/2) - P(X \leq d-l/2) = CDF(d+l/2) - CDF(d-l/2)
	 *	\end{equation}
	 *
	 * @param distToClosestSeedSource
	 * 			Distance between the cell whose probability of having an
	 * 			acorn we want to estimate and its closest seed source
	 * @return
	 */
	private double baseCellOccupancyProb(double distToClosestSeedSource) {
		double upperCumulativeProb = cumulativeExponentialDistributionFunction(distToClosestSeedSource + (cellSize/2));
		double lowerCumulativeProb =cumulativeExponentialDistributionFunction(distToClosestSeedSource - (cellSize/2));
		return upperCumulativeProb - lowerCumulativeProb;		
	}
	
	/**
	 * See Millington2009
	 * @param distToClosestSeedSource
	 * 		Distance in meters to nearest seed source used to calculate probability 
	 * 		of wind dispersed seed presence.
	 * @return
	 */
	public double getProb(double distToClosestSeedSource) {
		if (distToClosestSeedSource <= minExponentialDistance) {
			return 0.95;
		} else if (distToClosestSeedSource <= maxExponentialDistance) {
			return baseCellOccupancyProb(distToClosestSeedSource);
		} else {
			return minProb;
		}
	}	
	
	public double getMinProb() {
		return minProb;
	}

}
