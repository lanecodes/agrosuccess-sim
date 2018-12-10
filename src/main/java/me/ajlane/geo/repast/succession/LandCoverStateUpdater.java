/**
 * Scans over the various value layers in the model and uses a LandCoverTypeTransDecider to decide what land cover type
 * each simulation cell should be in the next timestep.  
 */
package me.ajlane.geo.repast.succession;

import me.ajlane.neo4j.EmbeddedGraphInstance;
import repast.simphony.context.Context;
import repast.simphony.valueLayer.GridValueLayer;

/**
 * @author andrew
 *
 */
public class LandCoverStateUpdater {
	private GridValueLayer landCoverType, soilMoisture, pineSeeds, oakSeeds, deciduousSeeds, aspect;
	private int[][] timeInLandCoverState, targetLandCoverType, targetLandCoverStateTransitionTime;
	
	private int height, width;
	private LandCoverTypeTransDecider landCoverTypeTransDecider;
	
	public LandCoverStateUpdater(EmbeddedGraphInstance graphDatabase, 
			EnvrStateAliasTranslator envStateAliasTranslator,
			String modelID, Context<Object> context) {
		landCoverType = (GridValueLayer) context.getValueLayer("lct");
		soilMoisture = (GridValueLayer) context.getValueLayer("soil moisture");
		pineSeeds = (GridValueLayer) context.getValueLayer("pine seeds");
		oakSeeds = (GridValueLayer) context.getValueLayer("oak seeds");
		deciduousSeeds = (GridValueLayer) context.getValueLayer("deciduous seeds");
		aspect = (GridValueLayer) context.getValueLayer("deciduous seeds");
		
		landCoverTypeTransDecider 
			= new LandCoverTypeTransDecider(graphDatabase, envStateAliasTranslator, modelID);		
		
		height = (int) landCoverType.getDimensions().getHeight();
		width = (int) landCoverType.getDimensions().getWidth();
		
		timeInLandCoverState = initialTimeInLandCoverState();
		targetLandCoverType = initialTargetLandCoverState(landCoverType);	
		targetLandCoverStateTransitionTime = initialTargetLandCoverStateTransitionTime();	
		
	}
	
	/**
	 * get initial timeInLandCoverState array, setting all values to 0
	 */
	private int[][] initialTimeInLandCoverState() {
		int[][] arr = new int[height][width];
		for (int i=0; i<height; i++) {
			for (int j=0; j<width; j++) {
				arr[i][j] = 0;
			}
		}
		return arr;
	}
	
	/**
	 * Get initial targetLandCoverStateTransitionTime array, setting all values to 0
	 */
	private int[][] initialTargetLandCoverStateTransitionTime() {
		int[][] arr = new int[height][width];
		for (int i=0; i<height; i++) {
			for (int j=0; j<width; j++) {
				arr[i][j] = 0;
			}
		}
		return arr;
	}
	
	/**
	 * Get initial targetLandCoverType array, setting all values to match initial land cover types
	 */
	private int[][] initialTargetLandCoverState(GridValueLayer initialLandCoverState) {
		int[][] arr = new int[height][width];
		for (int i=0; i<height; i++) {
			for (int j=0; j<width; j++) {
				arr[i][j] = (int) initialLandCoverState.get(i, j);
			}
		}
		return arr;
	}
	
	/**
	 * Updates land cover type value layer to reflect changes as a result of succession
	 */
	public void updateLandCoverState() {
		OldLandCoverStateTransitionMessage thisLandCoverState, nextLandCoverState;
		
		for (int i=0; i<height; i++) {
			for (int j=0; j<width; j++) {
				// parse current state
				thisLandCoverState = new OldLandCoverStateTransitionMessage(
						(int) landCoverType.get(i, j),
						timeInLandCoverState[i][j],
						targetLandCoverType[i][j],
						targetLandCoverStateTransitionTime[i][j]);				
						
				// infer next state based on environmental conditions
				nextLandCoverState = landCoverTypeTransDecider
						.nextLandCoverTransitionState(thisLandCoverState, 
								0, // succession pathway. TODO update to allow for changing succession pathway
								(int) aspect.get(i, j),
								(int) pineSeeds.get(i, j),
								(int) oakSeeds.get(i,j),
								(int) deciduousSeeds.get(i, j),
								soilMoisture.get(i, j));
				
				// update value layers to reflect updated state
				landCoverType.set(nextLandCoverState.getCurrentState(), i, j);
				timeInLandCoverState[i][j] = nextLandCoverState.getTimeInState();
				targetLandCoverType[i][j] = nextLandCoverState.getTargetState();
				targetLandCoverStateTransitionTime[i][j] 
						= nextLandCoverState.getTargetStateTransitionTime();
			}
		}
	}
	

}
