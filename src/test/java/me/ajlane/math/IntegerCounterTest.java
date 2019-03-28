package me.ajlane.math;

import static org.junit.Assert.*;
import java.util.Map;
import org.junit.Test;

public class IntegerCounterTest {

  @Test
  public void testCorrectCountsReturned() {
    IntegerCounter counter = new IntegerCounter();
    counter.accumulate(1);
    counter.accumulate(4);
    counter.accumulate(1);
    counter.accumulate(1);
    counter.accumulate(1);
    counter.accumulate(7);
    
    Map<Integer, Integer> data = counter.getIntegerCounts();
    assertEquals(3, data.entrySet().size());
    assertEquals(4, (int) data.get(1));
    assertEquals(1, (int) data.get(4));
    assertEquals(1, (int) data.get(7));    
  }
  
  @Test
  public void testDataClearedAfterCountsReturned() {
    IntegerCounter counter = new IntegerCounter();
    for (int i=0; i<10; i++) { counter.accumulate(3); }
    
    counter.getIntegerCounts(); // should clear data
    Map<Integer, Integer> data = counter.getIntegerCounts();
    assertEquals(0, data.entrySet().size());    
    }
}