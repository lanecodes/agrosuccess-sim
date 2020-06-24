package me.ajlane.geo.repast;

import me.ajlane.geo.GridDimensions;
import me.ajlane.geo.GridLoc;
import me.ajlane.geo.WriteableCartesianGridDouble2D;
import repast.simphony.valueLayer.IGridValueLayer;

/**
 * Convert objects implementing Repast Simphony's {@link IGridValueLayer} interface to work with the
 * {@link WriteableCartesianGridDouble2D} interface.
 *
 * <h1>TODO</h1>
 * <ul>
 * <li>There is shared code between this class and {@link ValueLayerAdapter} which implements
 * Repast's {@code ValueLayer} interface. {@code IGridValueLayer} extends {@code ValueLayer} so one
 * would think there is a way to avoid having separate implementations for the corresponding
 * adapters. However the composition (rather than inheritance) relationship between adapter and
 * adaptee in standard implementations of the Adapter pattern blocks this class from simply
 * inheriting from {@code ValueLayerAdapter}. Investigate how we might reduce code duplication using
 * a cleverer implementation of the Adapter pattern.</li>
 * </ul>
 *
 * @see repast.simphony.valueLayer.ValueLayer
 * @see IGridValueLayer
 *
 * @author Andrew Lane
 */
public class GridValueLayerAdapter implements WriteableCartesianGridDouble2D {

  private IGridValueLayer layer;

  public GridValueLayerAdapter(IGridValueLayer layer) {
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

  @Override
  public void setValue(double value, GridLoc loc) {
    layer.set(value, loc.getCol(), loc.getRow());
  }

}
