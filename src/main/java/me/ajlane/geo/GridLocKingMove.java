package me.ajlane.geo;

/**
 * Represents a move from a point on a grid to an adjacent cell, corresponding to the way a king
 * moves in chess.
 *
 * @author Andrew Lane
 */
public class GridLocKingMove implements GridLocMove {

  private final GridLoc startPoint;
  private final GridLoc endPoint;
  private final Direction direction;

  public GridLocKingMove(GridLoc startPoint, Direction direction) {
    this.startPoint = startPoint;
    this.direction = direction;
    this.endPoint = calculateEndPoint(startPoint, direction);
  }

  @Override
  public GridLoc getStartPoint() {
    return this.startPoint;
  }

  @Override
  public GridLoc getEndPoint() {
    return this.endPoint;
  }

  private GridLoc calculateEndPoint(GridLoc startPoint, Direction direction) {
    int x = startPoint.getCol();
    int y = startPoint.getRow();
    GridLoc endPoint;

    switch (direction) {
      case N:
        endPoint = new GridLoc(x, y + 1);
        break;
      case NE:
        endPoint = new GridLoc(x + 1, y + 1);
        break;
      case E:
        endPoint = new GridLoc(x + 1, y);
        break;
      case SE:
        endPoint = new GridLoc(x + 1, y - 1);
        break;
      case S:
        endPoint = new GridLoc(x, y - 1);
        break;
      case SW:
        endPoint = new GridLoc(x - 1, y - 1);
        break;
      case W:
        endPoint = new GridLoc(x - 1, y);
        break;
      case NW:
        endPoint = new GridLoc(x - 1, y + 1);
        break;
      default:
        throw new RuntimeException("Direction unexpectedly not recognised.");
    }

    return endPoint;
  }

  @Override
  public Direction getDirection() {
    return this.direction;
  }

  @Override
  public String toString() {
    return "GridLocKingMove [startPoint=" + startPoint + ", endPoint=" + endPoint + ", direction="
        + direction + "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((direction == null) ? 0 : direction.hashCode());
    result = prime * result + ((endPoint == null) ? 0 : endPoint.hashCode());
    result = prime * result + ((startPoint == null) ? 0 : startPoint.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    GridLocKingMove other = (GridLocKingMove) obj;
    if (direction != other.direction)
      return false;
    if (endPoint == null) {
      if (other.endPoint != null)
        return false;
    } else if (!endPoint.equals(other.endPoint))
      return false;
    if (startPoint == null) {
      if (other.startPoint != null)
        return false;
    } else if (!startPoint.equals(other.startPoint))
      return false;
    return true;
  }

}
