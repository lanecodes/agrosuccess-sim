package me.ajlane.math;

import java.util.HashMap;
import java.util.Map;

/**
 * Accumulates integers, counting how many times a each value has been seen. Can be thought of as
 * the data behind a histogram. 
 * 
 * @author Andrew Lane
 *
 */
public class IntegerCounter {
  
  private Map<Integer, Integer> counts;

  public IntegerCounter() {
    counts = new HashMap<>();
  }
  
  /**
   * Add the given value to the running integer count
   * @param n
   */
  public void accumulate(int n) {
    if (counts.containsKey(n)) {
      counts.put(n, counts.get(n) + 1);
    } else {
      counts.put(n, 1);
    }
  }
  
  /**
   * @return Accumulated counts of integers passed in using {@code accumulate}. Resets the counters.
   */
  public Map<Integer, Integer> getIntegerCounts() {
    Map<Integer, Integer> finalCounts = new HashMap<>(counts);
    counts.clear();
    return finalCounts;    
  }

}