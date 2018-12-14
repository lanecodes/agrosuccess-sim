package me.ajlane.geo.repast;

import repast.simphony.valueLayer.GridValueLayer;

public class AgroSuccessGridValueLayer extends GridValueLayer {

  public AgroSuccessGridValueLayer(String name, double defaultValue, boolean dense,
      int xDim, int yDim) {
    super(name, defaultValue, dense, xDim, yDim);
  }
  
  @Override
  public boolean equals(Object o) {
    if (o instanceof GridValueLayer) {
      GridValueLayer gvl = (GridValueLayer) o;
      
      boolean nameEq = this.getName().equals(gvl.getName());
      if (nameEq == false) return false;
      
      int nCols = (int)this.getDimensions().getWidth();
      int nRows = (int)this.getDimensions().getHeight();
      for (int i=0; i<nRows; i++) {
        for (int j=0; j<nCols; j++) {
          if (this.get(i, j) - gvl.get(i, j) > 0.0001) return false; 
        }
      }
      return true;
    }
    return false;     
  }
  
  @Override
  public int hashCode() {
    int nCols = (int)this.getDimensions().getWidth();
    int nRows = (int)this.getDimensions().getHeight();
    int nameHash = this.getName().hashCode();
    
    int result = 0;
    
    for (int i=0; i<nRows; i++) {
      for (int j=0; j<nCols; j++) {
        result += this.get(i, j);
      }
    }
    
    result = result % 13;
    result += nameHash;
    return result;
  }

}
