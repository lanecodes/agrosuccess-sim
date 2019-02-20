/**
 * 
 */
package me.ajlane.geo.repast.seeddispersal;

import org.apache.commons.math3.distribution.LogNormalDistribution;
import org.apache.commons.math3.distribution.RealDistribution;

/**
 * Generate the probability of finding a seed from a species which uses
 * wind to disperse its seeds, given the distance of that cell from the 
 * closest source of those seeds. 
 *
 * The probability of an Oak acorn being present in a cell at a given time is specified by the 
 * lognormal distribution where x is the distance from the cell to the nearest oak-containing pixel: 
 * \[ 
 * p(x) = \frac{1}{x \sigma \sqrt{2 \pi}} \exp\left[- \frac{(\ln(x) - \mu)^2}{2\sigma^2}\right] 
 * \]
 * 
 * Following Pons and Pausas (2007) and Millington et al. (2009) we set $\sigma = 0.851$ and 
 * $\mu = 3.844$. Note that these values are in log-scale. 
 * 
 * <strong>References</strong> 
 * Pons, J., & Pausas, J. G. (2007). Acorn dispersal estimated by radio-tracking. 
 * Oecologia, 153(4), 903–911. https://doi.org/10.1007/s00442-007-0788-x
 * 
 * Millington, J. D. A., Wainwright, J., Perry, G.
 * L. W., Romero-Calcerrada, R., & Malamud, B. D. (2009). Modelling
 * Mediterranean landscape succession-disturbance dynamics: A landscape
 * fire-succession model. Environmental Modelling and Software, 24(10),
 * 1196–1208. https://doi.org/10.1016/j.envsoft.2009.03.013
 * 
 * @author andrew
 */
public class AcornPresenceProbGenerator implements ISeedPresenceProbGenerator {
	
	// acorn distribution parameters
	private double maxLognormalDistance;
	private RealDistribution acornLogNormalDistribution;
	
	private double cellSize;
	private double minProb; // minimum probability for a cell to contain a species' seeds
	
	/**
	 * @param n
	 * 		Total number of cells in the model grid. Used to determine the minimum 
	 * 		probability of finding an acorn in any given cell such that we always
	 * 		expect to have one acorn cell in the model.	 * 		
	 */
	AcornPresenceProbGenerator(int n, double cellSize, SeedDispersalParams seedDispersalParams) {
		this.cellSize = cellSize;
		
		this.acornLogNormalDistribution = 
		    new LogNormalDistribution(seedDispersalParams.getAcornLocationParam(), 
		        seedDispersalParams.getAcornScaleParam());
		
		this.maxLognormalDistance = seedDispersalParams.getAcornMaxLognormalDist();
		
		// minProb always high enough to ensure at least one seed for
		// each species exists in model (guarantee 0 seeds for a species is 
		// not an adsorbing state)
		minProb = n > 1000 ? 0.001 : 1.0/n;
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
		double upperCumulativeProb = acornLogNormalDistribution.cumulativeProbability(distToClosestSeedSource + (cellSize/2));
		double lowerCumulativeProb = acornLogNormalDistribution.cumulativeProbability(distToClosestSeedSource - (cellSize/2));
		return upperCumulativeProb - lowerCumulativeProb;		
	}
	
	/**
	 * See Millington2009
	 * @param distToClosestSeedSource
	 * 		Distance in meters to nearest seed source used to calculate probability 
	 * 		of acorn presence
	 * @return
	 */
	public double getProb(double distToClosestSeedSource) {
		if (distToClosestSeedSource <= maxLognormalDistance){
			return baseCellOccupancyProb(distToClosestSeedSource);			
		} else {
			return minProb;
		}		
	}
	
	public double getMinProb() {
		return minProb;
	}

}
