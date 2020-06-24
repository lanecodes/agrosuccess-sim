package me.ajlane.geo.repast;

import me.ajlane.geo.CartesianGridDouble2D;
import me.ajlane.geo.GridDimensions;
import me.ajlane.geo.GridLoc;
import repast.simphony.valueLayer.ValueLayer;

/**
 * Convert objects implementing Repast Simphony's {@link ValueLayer} interface to work with the
 * {@link CartesianGridDouble2D} interface.
 *
 * @author Andrew Lane
 */
public class ValueLayerAdapter implements CartesianGridDouble2D {

  private ValueLayer layer;

  public ValueLayerAdapter(ValueLayer layer) {
    this.layer = layer;
  }

  @Override
  public GridDimensions getDimensions() {
    return new GridDimensions((int) layer.getDimensions().getWidth(),
        (int) layer.getDimensions().getHeight());
  }

  @Override
  public int getSize() {
    GridDimensions dims = getDimensions();
    return dims.getWidth() * dims.getHeight();
  }

  @Override
  public double getValue(GridLoc loc) {
    return layer.get(loc.getCol(), loc.getRow());
  }

  @Override
  public int hashCode() {
    return layer.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    return layer.equals(obj);
  }

}
