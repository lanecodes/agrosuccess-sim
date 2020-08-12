package me.ajlane.geo.repast.colonisation;

public interface ISeedPresenceProbGenerator {
	
	double getProb(double distToClosestSeedSource);
	double getMinProb();

}
