package me.ajlane.geo.repast.succession;

/**
 * Features of a land-cover type relevant to agricultural decision-making.
 *
 * @author Andrew Lane
 *
 */
public interface LandCoverTypeAnthro extends LandCoverTypeBase {

  /**
   * @return Fertility score on a scale of 0-5 indicating how fertile the land-cover type is. A
   *         score of 0 indicates the land-cover type is not fertile at all, whereas a score of 5
   *         indicates maximum fertility.
   */
  int getFertility();

  /**
   * @return Land cover conversion cost score on a scale of 0-5 indicates how much effort is
   *         required to convert the land-cover from its current state to use it for agriculture. A
   *         value of -1 represents 'no value' and accounts for situations where the land-cover type
   *         cannot be converted to agriculture.
   */
  int getLandCoverConversionCost();

}
