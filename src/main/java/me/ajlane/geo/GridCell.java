/**
 * 
 */
package me.ajlane.geo;

/**
 * @author andrew
 *
 */
public class GridCell {
	
	int row;
	int col;
	
	public GridCell(int row, int col) {
		this.row = row;
		this.col = col;		
	}
	
	public int getRow() {
		return row;
	}
	
	public int getColumn() {
		return col;
	}
	
	public String toString() {
		return String.format("(%d, %d)", row, col);
	}

}
