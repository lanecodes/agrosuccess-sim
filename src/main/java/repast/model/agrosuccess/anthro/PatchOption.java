package repast.model.agrosuccess.anthro;

import repast.simphony.space.grid.GridPoint;

/**
 * Represents a land cover patch along with all the information needed to determine its value to an
 * agricultural agent for wheat farming and wood gathering.
 *
 * @author Andrew Lane
 *
 */
public class PatchOption {

  GridPoint location;
  private double fertility, woodValue, slopeModValue, landCoverConversionCost;
  private static final double FLOAT_TOLERANCE = 0.000001;

  /**
   * @param location Location of the cell in the grid
   * @param fertility Fertility of the land
   * @param woodValue Intrinsic value of the cell for wood collection
   * @param slopeModValue Factor reflecting how flatter land is better for agriculture
   * @param landCoverConversionCost Cost of converting land cover patch to agriculture
   */
  PatchOption(GridPoint location, double fertility, double woodValue, double slopeModValue,
      double landCoverConversionCost) {
    this.location = location;
    this.fertility = fertility;
    this.woodValue = woodValue;
    this.slopeModValue = slopeModValue;
    this.landCoverConversionCost = landCoverConversionCost;
  }

  /**
   * @return the location
   */
  public GridPoint getLocation() {
    return location;
  }

  /**
   * @return the fertility
   */
  public double getFertility() {
    return fertility;
  }

  /**
   * @return the woodValue
   */
  public double getWoodValue() {
    return woodValue;
  }

  /**
   * @return the slopeModValue
   */
  public double getSlopeModValue() {
    return slopeModValue;
  }

  /**
   * @return the landCoverConversionCost
   */
  public double getLandCoverConversionCost() {
    return landCoverConversionCost;
  }

  @Override
  public boolean equals(Object obj) {
    return equals(obj, FLOAT_TOLERANCE);
  }

  public boolean equals(Object obj, double tolerance) {
    if (obj == null) {
      return false;
    }

    if (!this.getClass().isAssignableFrom(obj.getClass())) {
      return false;
    }

    final PatchOption other = (PatchOption) obj;

    if (!this.location.equals(other.getLocation())) {
      return false;
    }

    if (diffOutsideTol(this.getFertility(), other.getFertility(), tolerance)
        || diffOutsideTol(this.getWoodValue(), other.getWoodValue(), tolerance)
        || diffOutsideTol(this.getSlopeModValue(), other.getSlopeModValue(), tolerance)
        || diffOutsideTol(this.getLandCoverConversionCost(), other.getLandCoverConversionCost(),
            tolerance)) {
      return false;
    }

    return true;
  }

  /**
   * @param x1
   * @param x2
   * @param tol
   * @return {@code true} if the difference between {@code x1} and {@code x2} is outside the
   *         tolerance specified by {@code tol}
   */
  private static boolean diffOutsideTol(double x1, double x2, double tol) {
    return Math.abs(x1 - x2) > tol;
  }

  @Override
  public int hashCode() {
    int result = this.location.hashCode();
    result = 31 * result + Double.hashCode(this.fertility);
    result = 31 * result + Double.hashCode(this.woodValue);
    result = 31 * result + Double.hashCode(this.slopeModValue);
    result = 31 * result + Double.hashCode(this.landCoverConversionCost);
    return result;
  }

  @Override
  public String toString() {
    return "PatchOption [location=" + location + ", fertility=" + fertility + ", woodValue="
        + woodValue + ", slopeModValue=" + slopeModValue + ", landCoverConversionCost="
        + landCoverConversionCost + "]";
  }

}
