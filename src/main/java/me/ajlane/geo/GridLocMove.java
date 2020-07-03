package me.ajlane.geo;

/**
 * Represents a move on a grid. Characterised by a start point, end point and direction.
 *
 * @author Andrew Lane
 */
public interface GridLocMove {

  GridLoc getStartPoint();

  GridLoc getEndPoint();

  Direction getDirection();

}
