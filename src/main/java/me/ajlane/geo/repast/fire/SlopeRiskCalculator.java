package me.ajlane.geo.repast.fire;

import me.ajlane.geo.Direction;
import repast.simphony.valueLayer.ValueLayer;

public class SlopeRiskCalculator {

  private ValueLayer dem;
  private double gridCellSize;

  public SlopeRiskCalculator(ValueLayer dem, double gridCellSize) {
    this.dem = dem;
    this.gridCellSize = gridCellSize;
  }

  public Double getSlopeRisk(int x, int y, Direction direction) {
    // TODO Implement slope risk calculation
    return null;
  }

}
