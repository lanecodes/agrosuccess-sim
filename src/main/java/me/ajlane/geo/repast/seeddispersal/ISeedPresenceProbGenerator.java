package me.ajlane.geo.repast.seeddispersal;

public interface ISeedPresenceProbGenerator {
	
	double getProb(double distToClosestSeedSource);
	double getMinProb();

}
