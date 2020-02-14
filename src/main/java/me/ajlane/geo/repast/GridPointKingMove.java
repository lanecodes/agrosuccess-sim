package me.ajlane.geo.repast;

import me.ajlane.geo.Direction;
import repast.simphony.space.grid.GridPoint;

/**
 * Represents a move from a point on a grid to an adjacent cell, such as a king moves in chess.
 *
 * @author Andrew Lane
 *
 */
public class GridPointKingMove implements GridPointMove {

  private final GridPoint startPoint;
  private final GridPoint endPoint;
  private final Direction direction;

  public GridPointKingMove(GridPoint startPoint, Direction direction) {
    this.startPoint = startPoint;
    this.endPoint = RepastGridUtils.adjacentPointInDirection(startPoint, direction);
    this.direction = direction;
  }

  @Override
  public GridPoint getStartPoint() {
    return this.startPoint;
  }

  @Override
  public GridPoint getEndPoint() {
    return this.endPoint;
  }

  @Override
  public Direction getDirection() {
    return this.direction;
  }

}
