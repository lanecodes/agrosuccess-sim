/**
 * 
 */
package me.ajlane.geo.repast.soilmoisture;

import me.ajlane.geo.GridCell;
import repast.model.agrosuccess.LscapeLayer;
import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.valueLayer.GridValueLayer;


/**
 * @author andrew
 *
 */
public class SoilMoistureCalculator {
	
	private GridValueLayer soilMoisture, landCoverType, soilMap, slope, flowDirMap;
	//private SparseRealMatrix drainageMatrix;
	//private RealVector runoffVector;
	private int nX, nY;
	
	/**
	 * @param flowDirectionMap
	 * @param initialSoilMoisture
	 * @param context
	 */
	public SoilMoistureCalculator(GridValueLayer flowDirectionMap,
			double initialSoilMoisture, Context<Object> context) {
				
		soilMoisture = (GridValueLayer) context.getValueLayer(LscapeLayer.SoilMoisture.name());
		slope = (GridValueLayer) context.getValueLayer(LscapeLayer.Slope.name());
		soilMap = (GridValueLayer) context.getValueLayer(LscapeLayer.SoilType.name());
		flowDirMap = (GridValueLayer) flowDirectionMap;		
		landCoverType = (GridValueLayer) context.getValueLayer(LscapeLayer.Lct.name());
		
		nY = (int)soilMoisture.getDimensions().getHeight();
		nX = (int)soilMoisture.getDimensions().getWidth();
		
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
	/*
	public static SparseRealMatrix calcDrainageMatrix(FlowConnectivityNetwork flow) {
		SparseRealMatrix drainageMatrix;
		drainageMatrix = (SparseRealMatrix) flow.transpose().subtract(flow);
		return drainageMatrix;
	}
	*/
	
	/*
	protected SparseRealMatrix getDrainageMatrix() {
		return drainageMatrix;
	}	
	*/
	
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
	 *			4 = Depleted agricultural land
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
	
	/*
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
	*/
	
	/*
	protected RealVector getRunoffVector() {
		return runoffVector;	
	}
	*/
	
	/*
	private RealVector calcSoilMoistureVector(double precip) {
		updateRunoffVector(precip);
		RealVector precipVector = new ArrayRealVector(nX*nY+1).mapAdd(precip);		
		return drainageMatrix.operate(runoffVector).add(precipVector);		
	}
	*/
	
	/*
	 * DEPRECIATED, reimplemented below without FlowDirectionNetwork 
	public void updateSoilMoistureLayer(double precip) {
		double[] sm = calcSoilMoistureVector(precip).toArray();
		// only go to sm.length-1 as vector includes runoff node
		for(int i=0; i<sm.length-1; i++){
			int[] coords = indexSpatialCoords(i);
			//System.out.format("%d -> (%d, %d) = %3.2f\n", i, coords[1], coords[0], sm[i]);
			soilMoisture.set(sm[i], coords[1], coords[0]);
		}
	}
	*/
	
	private boolean isOutlet(int flowDir, int thisCol, int thisRow) {
		if (thisCol==0 && (flowDir==4 | flowDir==5 | flowDir==6)) {
			
			return true;
			
		} else if (thisCol==nX-1 && (flowDir==1 | flowDir==2 | flowDir==8)) {
			
			return true;
			
		} else if (thisRow==0 && (flowDir==2 | flowDir==3 | flowDir==4)) {
			
			return true;
			
		} else if (thisRow==nY-1 && (flowDir==6 | flowDir==7 | flowDir==8)) {
			
			return true;
			
		} else if (flowDir==9) {
			
			return true;
			
		} else {
			
			return false;
			
		}
		
	}
	
	private int targetRow(int flowDir, int thisRow) {
		if (flowDir==6 | flowDir==7 | flowDir==8) {
			
			return thisRow+1;
			
		} else if (flowDir==1 | flowDir==5) {
			
			return thisRow;
			
		} else if (flowDir==2 | flowDir==3 | flowDir==4) {
			
			return thisRow-1;
			
		} else {
			
			return -1; // indicates no value, could be an error
			
		}
	}
	
	private int targetCol(int flowDir, int thisCol) {
		if (flowDir==1 | flowDir==2 | flowDir==8) {
			
			return thisCol+1;
			
		} else if (flowDir==3 | flowDir==7) {
			
			return thisCol;
			
		} else if (flowDir==4 | flowDir==5 | flowDir==6) {
			
			return thisCol-1;
			
		} else {
			
			return -1; // indicates no value, could be an error
			
		}
	}
	
	/**
	 * Determine which cell this cell drains into. If it is a sink cell return null,
	 * else return an int[] specifying the row and column of the target cell.
	 * @param row
	 * 		Flow direction grid row number
	 * @param col
	 * 		Flow direction grid column number
	 * @param flowDir
	 * 		Value in flow direction grid
	 * @return
	 * 		array specifying the row and col of the target cell
	 */
	GridCell runoffTargetCell(int thisRow, int thisCol, int flowDir) {
		if (isOutlet(flowDir, thisCol, thisRow)) {
			return null;
		} else {
			return new GridCell(targetRow(flowDir, thisRow), 
					targetCol(flowDir, thisCol));
		}		
	}
	
	/**
	 * @param i
	 * 		Row index in a 2-dimensional java array (top left origin)
	 * @return
	 * 		Corresponding y coordinate in the Cartesian (bottom-left origin)
	 * 		Repast GridValueLayer 
	 */
	private int getY(int i) {
		return nY-1-i;
	}
	
	/**
	 * @param j
	 * 		Column index in a 2-dimensional java array (top left origin)
	 * @return
	 * 		Corresponding x coordinate in the Cartesian (bottom-left origin)
	 * 		Repast GridValueLayer. x and j are are identical, but a method is 
	 * 		provided to match semantics for non-trivial y coordinate case. 
	 */
	private int getX(int j) {
		return j;
	}
	
	@ScheduledMethod(start = 1, interval = 1, priority = 1)
	public void updateSoilMoistureLayer(double precip) {
		double[][] newSoilMoistureVals = new double[nY][nX];
		for (int i=0; i<nY; i++) {
			for (int j=0; j<nX; j++) {
				// add precipitation
				newSoilMoistureVals[i][j] += precip;
				
				// calculate runoff
				double curveNumber = curveNumber(
						slope.get(getX(j), getY(i)), 
						(int)soilMap.get(getX(j), getY(i)), 
						(int)landCoverType.get(getX(j), getY(i)));
				double runoff = cellRunoff(precip, abstractionRate(curveNumber));
				
				// subtract runoff from this cell
				newSoilMoistureVals[i][j] -= runoff;
				
				// add runoff to the draining cell, unless this cell is a sink
				GridCell targetCell = runoffTargetCell(i, j, (int)flowDirMap.get(getX(j), getY(i)));
				if (targetCell != null) {
					newSoilMoistureVals[targetCell.getRow()][targetCell.getColumn()] += runoff;
				}				
				System.out.print("\n");
			}
		}
		
		// copy calculated values to soil moisture grid value layer
		for (int i=0; i<nY; i++) {
			for (int j=0; j<nX; j++) {
				soilMoisture.set(newSoilMoistureVals[i][j], getX(j), getY(i));
			}
		} 
	}	
}
