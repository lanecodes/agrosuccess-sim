package repast.model.agrosuccess.reporting;

import java.util.HashMap;
import java.util.Map;
import me.ajlane.math.IntegerCounter;
import repast.model.agrosuccess.AgroSuccessCodeAliases.Lct;
import repast.simphony.valueLayer.IGridValueLayer;

public class LctProportionAggregator {
  
  static IntegerCounter counter = new IntegerCounter();
  
  IGridValueLayer lcts;  
  int height, width, totalNoCells; 

  public LctProportionAggregator(IGridValueLayer lctGridValueLayer) {
    lcts = lctGridValueLayer;
    width = (int) lcts.getDimensions().getWidth();
    height = (int) lcts.getDimensions().getHeight();
    totalNoCells = width * height;    
  }

  public Map<Lct, Double> getLctProportions() {
    for (int x=0; x<width; x++) {
      for (int y=0; y<height; y++) {
        counter.accumulate((int) lcts.get(x, y));
      }
    }
    Map<Lct, Double> lctProps = new HashMap<>();

    // Convert map from integer land cover codes to counts to map from Lct enum members to 
    // proportions.
    Map<Integer, Integer> counts = counter.getIntegerCounts();
    for (Lct lct: Lct.values()) {
      if (counts.containsKey(lct.getCode())) { 
        lctProps.put(lct, counts.get(lct.getCode()).doubleValue() / totalNoCells);
      } else {
        lctProps.put(lct, 0.0);
      }
    }
    return lctProps;    
  }

}
