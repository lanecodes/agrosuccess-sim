package me.ajlane.geo.repast.succession;

/**
 * @author Andrew Lane
 *
 */
public interface LandCoverStateTransitionMap<T> {
	/**
	 * @param <T> type of the environmental start state specifier
	 * @param ea
	 * @return
	 */
	EnvironmentalConsequent<T> getTargetState(
			EnvironmentalAntecedent<T, T, T, T, T, T, T> ea);
	
	// testing type of generic https://stackoverflow.com/questions/4704902/java-instanceof-generic
	
	
	// https://stackoverflow.com/questions/3949260/java-class-isinstance-vs-class-isassignablefrom
	
	
}
