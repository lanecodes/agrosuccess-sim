package me.ajlane.geo.repast.fire;

import static org.junit.Assert.assertEquals;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import org.junit.Test;
import me.ajlane.geo.GridLoc;
import repast.simphony.space.grid.GridPoint;

public class FireEventTest {

  @Test
  public void testFireEventWithRepastGridPoint() {
    BiFunction<Integer, Integer, GridPoint> gridPointConstructor = (x, y) -> new GridPoint(x, y);
    GridPoint igPoint = getTestIgnitionPoint(gridPointConstructor);
    List<GridPoint> burntCells = getTestBurntCells(gridPointConstructor);
    FireEvent<GridPoint> testEvent = new FireEvent<>(3, igPoint, burntCells);

    assertEquals(3, testEvent.getTick());
    assertEquals(new GridPoint(0, 0), testEvent.getIgnitionPoint());
    assertEquals(Arrays.asList(new GridPoint(0, 0), new GridPoint(0, 1),
        new GridPoint(1, 1), new GridPoint(2, 1)), testEvent.getBurntCells());
    assertEquals(testEvent.size(), 4);
  }

  @Test
  public void testFireEventWithRepastGridLoc() {
    BiFunction<Integer, Integer, GridLoc> gridLocConstructor = (x, y) -> new GridLoc(x, y);
    GridLoc igPoint = getTestIgnitionPoint(gridLocConstructor);
    List<GridLoc> burntCells = getTestBurntCells(gridLocConstructor);
    FireEvent<GridLoc> testEvent = new FireEvent<>(26, igPoint, burntCells);

    assertEquals(26, testEvent.getTick());
    assertEquals(new GridLoc(0, 0), testEvent.getIgnitionPoint());
    assertEquals(Arrays.asList(new GridLoc(0, 0), new GridLoc(0, 1),
        new GridLoc(1, 1), new GridLoc(2, 1)), testEvent.getBurntCells());
    assertEquals(testEvent.size(), 4);
  }

  private <T> T getTestIgnitionPoint(BiFunction<Integer, Integer, T> cellConstructor) {
    return cellConstructor.apply(0, 0);
  }

  private <T> List<T> getTestBurntCells(BiFunction<Integer, Integer, T> cellConstructor) {
    return Arrays.asList(cellConstructor.apply(0, 0), cellConstructor.apply(0, 1),
        cellConstructor.apply(1, 1), cellConstructor.apply(2, 1));
  }

}
