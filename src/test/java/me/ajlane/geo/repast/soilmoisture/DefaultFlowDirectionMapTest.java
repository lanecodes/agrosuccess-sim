package me.ajlane.geo.repast.soilmoisture;

import static org.junit.Assert.assertEquals;
import java.util.Map;
import org.junit.Test;
import me.ajlane.geo.Direction;

public class DefaultFlowDirectionMapTest {

  @Test
  public void test() {
    Map<Integer, Direction> dirMap = new DefaultFlowDirectionMap();
    assertEquals(Direction.E, dirMap.get(1));
    assertEquals(Direction.W, dirMap.get(5));
    assertEquals(Direction.SW, dirMap.get(6));
  }

}
