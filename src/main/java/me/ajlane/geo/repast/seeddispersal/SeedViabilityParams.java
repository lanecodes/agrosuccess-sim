/**
 * 
 */
package me.ajlane.geo.repast.seeddispersal;

/**
 * Class required to initialise the {@code SpatiallyRandomSeedDisperser}. This will not be necessary 
 * for the spatially explicit seed disperser which will be implemented in future following a 
 * design analogous to the approach taken in the Millington2009 model. This is the justification for 
 * keeping {@code SeedViabilityParams} separate from {@code SeedDispersalParams}.
 * 
 * I have a reference for pine seeds being viable for 7 years \cite{Daskalakou1996}.
 * 
 * @author Andrew Lane
 *
 */
public class SeedViabilityParams {
  
  int oakSeedLifetime, pineSeedLifetime, deciduousSeedLifetime;
  
  /**
   * @param oakSeedLifetime The number of years an oak seed remains viable in the seed bank.
   * @param pineSeedLifetime The number of years a pine seed remains viable in the seed bank.
   * @param deciduousSeedLifetime The number of years an deciduous seed remains viable in the seed 
   *        bank.
   */
  public SeedViabilityParams(int oakSeedLifetime, int pineSeedLifetime, int deciduousSeedLifetime) {
    this.oakSeedLifetime = oakSeedLifetime;
    this.pineSeedLifetime = pineSeedLifetime;
    this.deciduousSeedLifetime = deciduousSeedLifetime;    
  }
  
  /**
   * @param seedLifetime The number of years a seed remains viable in the seed bank. Oak, pine and 
   *        deciduous seeds are assumed to have the same lifetime.
   */
  public SeedViabilityParams(int seedLifetime) {
    this(seedLifetime, seedLifetime, seedLifetime);    
  }

  public int getOakSeedLifetime() {
    return oakSeedLifetime;
  }

  public int getPineSeedLifetime() {
    return pineSeedLifetime;
  }

  public int getDeciduousSeedLifetime() {
    return deciduousSeedLifetime;
  }  
  
  public String toString() {
    return "SeedViabilityParams[" 
        + "oakSeedLifetime=" + this.oakSeedLifetime + ", "
        + "pineSeedLifetime=" + this.pineSeedLifetime + ", "
        + "deciduousSeedLifetime=" + this.deciduousSeedLifetime + "]";
  }
  
  @Override
  public boolean equals(Object other){
    if (other == null) return false;
    if (other == this) return true;
    if (!(other instanceof SeedViabilityParams)) return false;
    SeedViabilityParams otherSvp = (SeedViabilityParams)other;
    if (otherSvp.getOakSeedLifetime() == this.getOakSeedLifetime() 
        && otherSvp.getPineSeedLifetime() == this.getPineSeedLifetime()
        && otherSvp.getDeciduousSeedLifetime() == this.getDeciduousSeedLifetime()) {
      return true;
    } else return false;
    }
  
}

