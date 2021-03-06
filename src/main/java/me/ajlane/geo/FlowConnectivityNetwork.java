/**
 *
 */
package me.ajlane.geo;

import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.linear.OpenMapRealMatrix;
import org.apache.commons.math3.linear.SparseRealMatrix;
import org.apache.log4j.Logger;
import org.geotools.coverage.grid.GridCoverage2D;

/**
 *
 *
 * @author andrew
 *
 */
public class FlowConnectivityNetwork extends OpenMapRealMatrix implements SparseRealMatrix {

    final static Logger logger = Logger.getLogger(FlowConnectivityNetwork.class);

	/**
	 * Eclipse generated class version ID used for deciding whether or not
	 * a serialised object generated by a version of this class can be
	 * reliably de-serialised by <emph>this</emph> version of the class.
	 * https://stackoverflow.com/questions/285793/what-is-a-serialversionuid-and-why-should-i-use-it
	 */
	private static final long serialVersionUID = 6852511462239986920L;
	private int nX; // width of spatial flow grid
	private int nY; // height of spatial flow grid
	private int numNodes; // total number of nodes including sink node

	/**
	 * @param matrix
	 */
	public FlowConnectivityNetwork(OpenMapRealMatrix matrix) {
		super(matrix);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param rowDimension
	 * @param columnDimension
	 * @throws NotStrictlyPositiveException
	 * @throws NumberIsTooLargeException
	 */
	public FlowConnectivityNetwork(GridCoverage2D spatialFlowGrid)
			throws NotStrictlyPositiveException, NumberIsTooLargeException {

		/*
		 * Use the dimensions of the ValueLayer to calculate how large the
		 * matrix needs to be
		 *
		 * then use this.setEntry(...) to set the appropriate values in the
		 * matrix
		 */
		super(spatialFlowGrid.getRenderedImage().getHeight()*spatialFlowGrid.getRenderedImage().getWidth()+1,
			  spatialFlowGrid.getRenderedImage().getHeight()*spatialFlowGrid.getRenderedImage().getWidth()+1);

		nX = spatialFlowGrid.getRenderedImage().getWidth();
		nY = spatialFlowGrid.getRenderedImage().getWidth();
		numNodes = nX*nY+1;


		setMatrixValues(spatialFlowGrid);
		checkSinkExists();

	}

	/**
	 * @param x
	 * 			Horizontal spatial grid index
	 * @param y
	 * 			Vertical spatial grid index
	 * @return
	 * 			Index for the FlowConnectivityMatrix row/column describing
	 * 			the flow characteristics of spatial cell (x,y)
	 */
	public int spatialCellIndex(int x, int y) {
		return y*nY + x;
	}

	/**
	 * @param flowDir
	 * 			Integer value in {1,...,9} specifying direction of
	 * 			flow out of grid cell (x,y). 1=E, 2=NE, ..., 8=SE.
	 * 			A special value of 9 can be used to indicate a cell
	 * 			drains to the model sink whether it is at the edge
	 * 			of the grid or not.
	 * @param x
	 * 			Horizontal grid index
	 * @param y
	 * 			Vertical grid index
	 * @return
	 * 			Whether or not the cell at coordinates (x,y) is a hydrological
	 * 			outlet. That is, whether it allows water to drain out of
	 * 			the model to a sink.
	 */
	private boolean isOutlet(int flowDir, int x, int y) {
		if (x==0 && (flowDir==4 | flowDir==5 | flowDir==6)) {

			return true;

		} else if (x==nX-1 && (flowDir==1 | flowDir==2 | flowDir==8)) {

			return true;

		} else if (y==0 && (flowDir==2 | flowDir==3 | flowDir==4)) {

			return true;

		} else if (y==nY-1 && (flowDir==6 | flowDir==7 | flowDir==8)) {

			return true;

		} else if (flowDir==9) {

			return true;

		} else {

			return false;

		}

	}

	private int newX(int flowDir, int x) {
		if (flowDir==1 | flowDir==2 | flowDir==8) {

			return x+1;

		} else if (flowDir==3 | flowDir==7) {

			return x;

		} else if (flowDir==4 | flowDir==5 | flowDir==6) {

			return x-1;

		} else {

			return -1; // indicates no value, could be an error

		}
	}

	private int newY(int flowDir, int y) {
		if (flowDir==6 | flowDir==7 | flowDir==8) {

			return y+1;

		} else if (flowDir==1 | flowDir==5) {

			return y;

		} else if (flowDir==2 | flowDir==3 | flowDir==4) {

			return y-1;

		} else {

			return -1; // indicates no value, could be an error

		}
	}

	/**
	 * Takes the flow direction array (provided as a GridCoverage2D object)
	 * and sets the appropriate element of the FlowConnectivityNetwork's
	 * underlying matrix to 1.
	 *
	 * @param spatialFlowGrid
	 * 			GridCoverage2D object specifying flow directions
	 */
	private void setMatrixValues(GridCoverage2D spatialFlowGrid) {
		int i;
		int j;
		int flowDir;

		for (int x=0; x<nX; x++){
			for (int y=0; y<nY; y++) {

				flowDir = spatialFlowGrid.getRenderedImage().getData().getSample(x, y, 0);
				i = spatialCellIndex(x, y); // select the row of the connectivity matrix to set

				// choose the column to set, considering whether to connect to
				// an adjacent cell or a sink
				if (isOutlet(flowDir, x, y)) {
					j = numNodes - 1; // goes to sink node;
				} else {
					j = spatialCellIndex(newX(flowDir, x), newY(flowDir, y));
				}

				// assert flow goes from cell i to cell j
				setEntry(i, j, 1.0);

			}
		}
	}


	/**
	 * @return
	 * 			Total number of cells/ nodes in network
	 */
	public int getNumNodes() {
		return this.numNodes;
	}

	/**
	 * @return
	 * 			Index of the sink node
	 */
	public int getSinkNode() {
		return this.numNodes - 1;
	}

	/**
	 * @return
	 * 			A count of  the number of nodes which connect to the sink
	 * 			node, allowing water to drain out of the grid
	 */
	public int sinkCount() {
		double[] sinkCol = this.getColumn(getSinkNode());
		double sum = 0; int i = 0;
		while (i<sinkCol.length) {
			sum += sinkCol[i];
			i++;
		}
		return (int)sum;
	}

	/*
	 * For a FlowConnectivityMatrix to be valid there needs to be at
	 * least one
	 */
	private void checkSinkExists() {
		if (sinkCount()<1) {
			logger.error("Provided flow direction grid doesn't include "
			             + "any sinks. Check input GTiff.");
			throw new IllegalStateException();
		}

	}


	/*
	 * Density should be nX*nY/(nXnY+1)^2, we should test this to check
	 * it is as expected
	 */
	//private void checkDensity() {
	//
	//}



}
