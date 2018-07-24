/**
 * 
 */
package me.ajlane.geo.repast.succession;

import me.ajlane.geo.FlowConnectivityNetwork;
import repast.simphony.context.Context;
import repast.simphony.valueLayer.GridValueLayer;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.SparseRealMatrix;

/**
 * @author andrew
 *
 */
public class SoilMoistureCalculator {
	
	private GridValueLayer soilMoisture, landCoverType, soilMap, slope;
	private SparseRealMatrix drainageMatrix;
	private RealVector runoffVector;
	private double sinkAffinityFactor;
	private int nX, nY;
	
	
	public SoilMoistureCalculator(FlowConnectivityNetwork flowNetwork,
			double initialSoilMoisture, Context<Object> context) {
		this(flowNetwork, initialSoilMoisture, 1.0, context);
	}
	
	/**
	 * @param flowNetwork
	 * @param initialSoilMoisture
	 * @param saf
	 * 			Sink Affinity Factor
	 * @param context
	 */
	public SoilMoistureCalculator(FlowConnectivityNetwork flowNetwork,
			double initialSoilMoisture, double saf, Context<Object> context) {
				
		soilMoisture = (GridValueLayer) context.getValueLayer("soil moisture");
		slope = (GridValueLayer) context.getValueLayer("slope");
		soilMap = (GridValueLayer) context.getValueLayer("soil");
		landCoverType = (GridValueLayer) context.getValueLayer("lct");
		
		nY = (int)soilMoisture.getDimensions().getHeight();
		nX = (int)soilMoisture.getDimensions().getWidth();
		
		drainageMatrix = calcDrainageMatrix(flowNetwork);
		setSinkAffinityFactor(saf);
		runoffVector = new ArrayRealVector(nX*nY+1);
		
		initSoilMoisture(initialSoilMoisture);

	}
	
	/**
	 * The time-dependent soil moisture vector is given by:
	 * m(t) = p(t) + D*r(t)
	 * In this equation, p(t) is the precipitation, r(t) is the runoff vector which
	 * encodes the runoff characteristics of each land-cover cell in consideration of 
	 * soil type, vegetation cover and slope. D is a matrix which encodes all the
	 * information about landscape topology we need to route water around the landscape.
	 * D = A^T - A where A is the FlowConnectivityNetwork for the landscape. 
	 * 
	 * @param flow
	 * 			FlowConnectivityNetwork to use to calculate the drainage matrix.
	 * @return
	 * 			calculated drainageMatrix
	 */
	public static SparseRealMatrix calcDrainageMatrix(FlowConnectivityNetwork flow) {
		SparseRealMatrix drainageMatrix;
		drainageMatrix = (SparseRealMatrix) flow.transpose().subtract(flow);
		return drainageMatrix;
	}
	
	protected SparseRealMatrix getDrainageMatrix() {
		return drainageMatrix;
	}	
	
	/**
	 * Set all cells in the soil moisture ValueLayer to the same initial value
	 * 
	 * @param initialSoilMoisture
	 * 			Initial value for soil moisture in all cells, units of mm.
	 */
	private void initSoilMoisture(double initialSoilMoisture) {
		int h = (int) soilMoisture.getDimensions().getHeight();
		int w = (int) soilMoisture.getDimensions().getWidth();
		for (int x=0; x<w; x++) {
			for (int y=0; y<h; y++) {
				soilMoisture.set(initialSoilMoisture, x, y);
			}
		}		
	}
	
	/**
	 * @param curveNumber
	 * 			Dimensionless number characterising the runoff characteristics
	 * 			of a patch of ground.
	 * @return
	 * 			The initial abstraction rate of the land patch 
	 */
	private double abstractionRate(double curveNumber) {
		return 2.54 * (1000/ curveNumber - 10);
	}
	
	/**
	 * @param precip
	 * 			Precipitation [mm]
	 * @param s
	 * 			Initial abstraction rate [dimensionless]
	 * @return
	 * 			Runoff [mm]
	 */
	private double cellRunoff(double precip, double s) {
		return Math.pow(precip - 0.02*s, 2) / (precip + 0.08*s); 
	}
	
	/**
	 * Get the curve number for an individual cell given information about
	 * its landcover, soil type and slope. See Millington2009
	 * @param slope
	 * 			Percentage incline
	 * @param soilType
	 * 			See Millington2009
	 * 			0 = Type A
	 * 			1 = Type B
	 * 			2 = Type C
	 * 			3 = Type D
	 * @param landCoverType
	 * 			0 = Water/ Quarry
	 * 			1 = Burnt
	 * 			2 = Barley
	 *			3 = Wheat
	 *			4 = Depleated agricultural land
	 *			5 = Shrubland
	 *			6 = Pine forest
	 *			7 = Transition forest
	 *			8 = Deciduous forest
	 *			9 = Oak forest
	 * @return
	 * 			Curve number
	 */
	private double curveNumber(double slope, int soilType, int landCoverType) {
		double result = -1;
		if (slope<3) {
			switch(soilType) {
				case 0: {
					if (landCoverType==6) {result = 35; break;}//pine
					else if (landCoverType==7) {result = 35; break;} //transition forest
					else if (landCoverType==8) {result = 35; break;} //deciduous forest
					else if (landCoverType==5) {result = 46; break;} //shrubland
					else if (landCoverType==9) {result = 35; break;} //oak
					else if (landCoverType==2 || landCoverType==3 || landCoverType==4) {result = 62; break;}// barley, wheat and depleated agricultural land
					else if (landCoverType==1) {result = 91; break;} //burnt
					else {result = -1; break;} // default value, indicates missing data
					//TODO work out what to do about Burnt/Quarry. What should its CN be?
				} 
				case 1: {
					if (landCoverType==6) {result = 54; break;} //pine
					else if (landCoverType==7) {result = 54; break;} //transition forest
					else if (landCoverType==8) {result = 54; break;} //deciduous forest
					else if (landCoverType==5) {result = 68; break;} //shrubland
					else if (landCoverType==9) {result = 54; break;} //oak
					else if (landCoverType==2 || landCoverType==3 || landCoverType==4) {result = 72; break;} // barley, wheat and depleated agricultural land
					else if (landCoverType==1) {result = 91; break;} //burnt
					else {result = -1; break;} // default value, indicates missing data			
				} 
				case 2: {
					if (landCoverType==6) {result = 69; break;} //pine
					else if (landCoverType==7) {result = 69; break;} //transition forest
					else if (landCoverType==8) {result = 69; break;} //deciduous forest
					else if (landCoverType==5) {result = 78; break;} //shrubland
					else if (landCoverType==9) {result = 69; break;} //oak
					else if (landCoverType==2 || landCoverType==3 || landCoverType==4) {result = 78; break;} // barley, wheat and depleated agricultural land
					else if (landCoverType==1) {result = 91; break;} //burnt
					else {result = -1; break;} // default value, indicates missing data			
				} 
				case 3: {
					if (landCoverType==6) {result = 77; break;} //pine
					else if (landCoverType==7) {result = 77; break;} //transition forest
					else if (landCoverType==8) {result = 77; break;} //deciduous forest
					else if (landCoverType==5) {result = 83; break;} //shrubland
					else if (landCoverType==9) {result = 77; break;} //oak
					else if (landCoverType==2 || landCoverType==3 || landCoverType==4) {result = 82; break;} // barley, wheat and depleated agricultural land
					else if (landCoverType==1) {result = 91; break;} //burnt
					else {result = -1; break;} // default value, indicates missing data				
				}
			}
		} else { // i.e. if slope >= 3
			switch(soilType) {
				case 0: {
					if (landCoverType==6) {result = 39; break;} //pine
					else if (landCoverType==7) {result = 39; break;} //transition forest
					else if (landCoverType==8) {result = 39; break;} //deciduous forest
					else if (landCoverType==5) {result = 46; break;} //shrubland
					else if (landCoverType==9) {result = 39; break;} //oak
					else if (landCoverType==2 || landCoverType==3 || landCoverType==4) {result = 65; break;} // barley, wheat and depleated agricultural land
					else if (landCoverType==1) {result = 94; break;} //burnt
					else {result = -1; break;} // default value, indicates missing data
					//TODO work out what to do about Burnt/Quarry. What should its CN be?
				}
				case 1: {
					if (landCoverType==6) {result = 60; break;} //pine
					else if (landCoverType==7) {result = 60; break;} //transition forest
					else if (landCoverType==8) {result = 60; break;} //deciduous forest
					else if (landCoverType==5) {result = 68; break;} //shrubland
					else if (landCoverType==9) {result = 60; break;} //oak
					else if (landCoverType==2 || landCoverType==3 || landCoverType==4) {result = 76; break;} // barley, wheat and depleated agricultural land
					else if (landCoverType==1) {result = 94; break;} //burnt
					else {result = -1; break;}
				} 
				case 2: {
					if (landCoverType==6) {result = 73; break;} //pine
					else if (landCoverType==7) {result = 73; break;} //transition forest
					else if (landCoverType==8) {result = 73; break;} //deciduous forest
					else if (landCoverType==5) {result = 78; break;} //shrubland
					else if (landCoverType==9) {result = 73; break;} //oak
					else if (landCoverType==2 || landCoverType==3 || landCoverType==4) {result = 84; break;} // barley, wheat and depleated agricultural land
					else if (landCoverType==1) {result = 94; break;} //burnt
					else {result = -1; break;}
				} 
				case 3: {
					if (landCoverType==6) {result = 78; break;} //pine
					else if (landCoverType==7) {result = 78; break;} //transition forest
					else if (landCoverType==8) {result = 78; break;} //deciduous forest
					else if (landCoverType==5) {result = 83; break;} //shrubland
					else if (landCoverType==9) {result = 78; break;} //oak
					else if (landCoverType==2 || landCoverType==3 || landCoverType==4) {result = 87; break;} // barley, wheat and depleated agricultural land
					else if (landCoverType==1) {result = 94; break;} //burnt
					else {result = -1; break;}
				}			
			}		
		}
		return result;
	}	

	/**
	 * @param x
	 * 			Horizontal spatial grid index
	 * @param y
	 * 			Vertical spatial grid index
	 * @return
	 * 			Index for the FlowConnectivityMatrix row/column and Runoff vector
	 * 			elements describing the flow characteristics of spatial cell (x,y)
	 */
	private int spatialCellIndex(int x, int y) {
		return y*nY + x;		
	}
	
	/**
	 * @param i
	 * 			Vector index of runoff node
	 * @return
	 * 			1D array with 2 elements such that coords[0] nad coords[1] are
	 * 			the x and y coordinates of the grid cell corresponding to runoff
	 * 			node i, respectively.
	 */
	private int[] indexSpatialCoords(int i) {
		int coords[] = new int[2];
		coords[0] = i%nX;
		coords[1] = i/nX;
		return coords;
	}
	
	/**
	 * @param raf
	 * 			Sink affinity factor, 0<saf<=1. A dimensionless parameter controlling 
	 * 			how much water flows out of each cell which drains into the sink in 
	 *  		each model time step. Specifically, precipitation * sinkAffinityFactor
	 *  		will drain out of each cell connected to the sink in each time step.
	 *  
	 *    		saf = 0 implies no water leaves through the sink, so water would 
	 *    		accumulate at the nodes which would otherwise drain the model.
	 *    
	 *    		saf > 1 could, in an extreme case, mean that a cell ended up with 
	 *    		negative soil moisture (obviously unphysical). Therefore the maximum
	 *    		water which can be safely extracted from each cell is equal to the 
	 *    		amount of water going into it via precipitation. 
	 */
	private void setSinkAffinityFactor(double saf) {
		if (saf<=0 || saf>1) {
			System.out.println("sinkAffinityFactor must be >0 and <=1.");
			throw new IllegalArgumentException();
		} else {
			sinkAffinityFactor =  saf;
		}		
	}
	
	protected void updateRunoffVector(double precip) {
		for(int x=0; x<nX; x++) {
			for(int y=0; y<nY; y++){
				double dX = (double) x;
				double dY = (double) y;
				
				int i = spatialCellIndex(x, y);
				
				double cn = curveNumber(slope.get(dX, dY), 
										(int)soilMap.get(dX, dY), 
										(int)landCoverType.get(dX, dY));
				
				runoffVector.setEntry(i, cellRunoff(precip, abstractionRate(cn)));							
			}
		}
		// Sink node will extract precip mm of water per node connected to it
		// per time step, producing neutral water balance
		runoffVector.setEntry(nX*nY, precip*sinkAffinityFactor);
	}
	
	protected RealVector getRunoffVector() {
		return runoffVector;	
	}
	
	private RealVector calcSoilMoistureVector(double precip) {
		updateRunoffVector(precip);
		RealVector precipVector = new ArrayRealVector(nX*nY+1).mapAdd(precip);		
		return drainageMatrix.operate(runoffVector).add(precipVector);		
	}
	
	public void updateSoilMoistureLayer(double precip) {
		double[] sm = calcSoilMoistureVector(precip).toArray();
		// only go to sm.length-1 as vector includes runoff node
		for(int i=0; i<sm.length-1; i++){
			int[] coords = indexSpatialCoords(i);
			//System.out.format("%d -> (%d, %d) = %3.2f\n", i, coords[1], coords[0], sm[i]);
			soilMoisture.set(sm[i], coords[1], coords[0]);
		}
	}
}
