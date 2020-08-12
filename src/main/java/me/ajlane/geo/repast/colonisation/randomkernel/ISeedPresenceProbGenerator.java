package me.ajlane.geo.repast.colonisation.randomkernel;

public interface ISeedPresenceProbGenerator {
	
	double getProb(double distToClosestSeedSource);
	double getMinProb();

}
