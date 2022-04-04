package repast.model.agrosuccess.anthro;

import repast.model.agrosuccess.AgroSuccessCodeAliases.Lct;

/**
 * Gets data about land-cover types needed by agricultural agents to determine the value of land
 * patches.
 *
 * @author Andrew Lane
 */
public interface LctValueGetter {

  /**
   * @param lct Land-cover type
   * @return Fertility score on a scale of 0-5 indicating how fertile the land-cover type is. A
   *         score of 0 indicates the land-cover type is not fertile at all, whereas a score of 5
   *         indicates maximum fertility.
   */
  int getFertility(Lct lct);


  /**
   * @param lct Land-cover type
   * @return Intrinsic wood value of the land-cover type on a scale of -1-5, where -1 indicates 'no
   *         value' and 5 indicates maximum value.
   */
  int getWoodValue(Lct lct);

  /**
   * @param pctSlope Percentage slope of a grid cell.
   * @return Slope modification value that controls the value of a patch of land for farming
   *         depending on how flat it is.
   */
  double getSlopeModificationValue(double pctSlope);

  /**
   * @param lct Land-cover type.
   * @return Land cover conversion cost score on a scale of 0-5 indicates how much effort is
   *         required to convert the land-cover from its current state to use it for agriculture. A
   *         value of -1 represents 'no value' and accounts for situations where the land-cover type
   *         cannot be converted to agriculture.
   */
  int getLandCoverConversionCost(Lct lct);

}
