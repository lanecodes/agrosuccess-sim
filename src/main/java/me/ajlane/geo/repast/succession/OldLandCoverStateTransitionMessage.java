/**
 * 
 */
package me.ajlane.geo.repast.succession;

/**
 * Container providing all the information needed to determine a simulation cell's next land cover state. Generated
 * by a LandCoverTypeTransDecider.
 * 
 * @author andrew
 *
 */
class OldLandCoverStateTransitionMessage {
	
	private int currentState;
	private int timeInState;
	private int targetState;
	private int targetStateTransitionTime;	
	
	/**
	 * @param currentState
	 * 		Numerical ID of current land cover state
	 * @param timeInState
	 * 		Time (in years) a cell has been in its current land cover state
	 * @param targetState
	 * 		Numerical ID of the land cover state which the cell is currently
	 * 		transitioning towards.
	 * @param targetStateTransitionTime
	 * 		The time such that when timeInState >= targetStateTransitionTime the 
	 * 		cell will transition from currentState to targetState
	 */
	public OldLandCoverStateTransitionMessage(int currentState, int timeInState,
			int targetState, int targetStateTransitionTime) {
		
		this.currentState = currentState;
		this.timeInState = timeInState;
		this.targetState = targetState;
		this.targetStateTransitionTime = targetStateTransitionTime;		
	}

	public int getCurrentState() {
		return currentState;
	}

	public int getTimeInState() {
		return timeInState;
	}

	public int getTargetState() {
		return targetState;
	}

	public int getTargetStateTransitionTime() {
		return targetStateTransitionTime;
	}

	@Override
	public String toString() {
		return "LandCoverStateTransitionMessage [currentState=" + currentState
				+ ", timeInState=" + timeInState + ", targetState="
				+ targetState + ", targetStateTransitionTime="
				+ targetStateTransitionTime + "]";
	}	
}
