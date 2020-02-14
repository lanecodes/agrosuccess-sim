package me.ajlane.geo.repast;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import me.ajlane.geo.Direction;
import repast.simphony.space.grid.GridPoint;

public class GridPointKingMoveTest {

  GridPoint startPoint;
  Direction dir;

  @Before
  public void setUp() {
    startPoint = new GridPoint(1, 1);
    dir = Direction.NE;
  }

  @Test
  public void testConstructor() {
    new GridPointKingMove(startPoint, dir);
  }

  @Test
  public void testGetStartAndEndPoints() {
    GridPointMove move = new GridPointKingMove(startPoint, dir);
    assertEquals(new GridPoint(1, 1), move.getStartPoint());
    assertEquals(new GridPoint(2, 2), move.getEndPoint());
  }

  @Test
  public void getGetDirection() {
    GridPointMove move = new GridPointKingMove(startPoint, dir);
    assertEquals(Direction.NE, move.getDirection());
  }

}
