package me.ajlane.geo.repast.succession;

public interface ISeedPresenceProbGenerator {
	
	double getProb(double distToClosestSeedSource);
	double getMinProb();

}
