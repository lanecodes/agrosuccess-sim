package repast.model.agrosuccess.reporting;

import me.ajlane.math.IntegerCounter;
import repast.simphony.valueLayer.GridValueLayer;

public abstract class LandscapeReporter {
  
  GridValueLayer landCoverTypeMap;
  IntegerCounter counter;    
  
  /**
   * Persists land cover map to disk in a manner determined by implementing classes.
   */
  abstract void reportLctMap();
  
  /**
   * Persists land cover type proportions to disk in a manner determined by implementing classes.
   */
  abstract void reportLctProportions();
  
  public abstract void step();

}