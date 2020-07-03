package me.ajlane.geo;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class GridLocKingMoveTest {

  @Test
  public void testTargetCellCorrect() {
    assertEquals(new GridLoc(0, 1), new GridLocKingMove(new GridLoc(0, 0), Direction.N).getEndPoint());
    assertEquals(new GridLoc(-2, 1), new GridLocKingMove(new GridLoc(-3, 0), Direction.NE).getEndPoint());
    assertEquals(new GridLoc(2, -4), new GridLocKingMove(new GridLoc(2, -3), Direction.S).getEndPoint());
    assertEquals(new GridLoc(3, 4), new GridLocKingMove(new GridLoc(4, 4), Direction.W).getEndPoint());
    assertEquals(new GridLoc(-1, 1), new GridLocKingMove(new GridLoc(0, 0), Direction.NW).getEndPoint());
  }

}
