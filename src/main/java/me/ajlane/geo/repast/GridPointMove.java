package me.ajlane.geo.repast;

import me.ajlane.geo.Direction;
import repast.simphony.space.grid.GridPoint;

/**
 * Represents a move on a grid. Characterised by a start point, end point and direction.
 *
 * @author Andrew Lane
 *
 */
public interface GridPointMove {

  GridPoint getStartPoint();

  GridPoint getEndPoint();

  Direction getDirection();

}
