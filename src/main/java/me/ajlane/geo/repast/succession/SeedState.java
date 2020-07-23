package me.ajlane.geo.repast.succession;

public class SeedState {

  Integer pineSeeds, oakSeeds, deciduousSeeds;

  SeedState(Integer pineSeeds, Integer oakSeeds, Integer deciduousSeeds) {
    this.pineSeeds = pineSeeds;
    this.oakSeeds = oakSeeds;
    this.deciduousSeeds = deciduousSeeds;
  }

  /**
   * @return the pineSeeds
   */
  public Integer getPineSeeds() {
    return pineSeeds;
  }

  /**
   * @return the oakSeeds
   */
  public Integer getOakSeeds() {
    return oakSeeds;
  }

  /**
   * @return the deciduousSeeds
   */
  public Integer getDeciduousSeeds() {
    return deciduousSeeds;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((deciduousSeeds == null) ? 0 : deciduousSeeds.hashCode());
    result = prime * result + ((oakSeeds == null) ? 0 : oakSeeds.hashCode());
    result = prime * result + ((pineSeeds == null) ? 0 : pineSeeds.hashCode());
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
    SeedState other = (SeedState) obj;
    if (deciduousSeeds == null) {
      if (other.deciduousSeeds != null)
        return false;
    } else if (!deciduousSeeds.equals(other.deciduousSeeds))
      return false;
    if (oakSeeds == null) {
      if (other.oakSeeds != null)
        return false;
    } else if (!oakSeeds.equals(other.oakSeeds))
      return false;
    if (pineSeeds == null) {
      if (other.pineSeeds != null)
        return false;
    } else if (!pineSeeds.equals(other.pineSeeds))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "SeedState [pineSeeds=" + pineSeeds + ", oakSeeds=" + oakSeeds + ", deciduousSeeds="
        + deciduousSeeds + "]";
  }

}
