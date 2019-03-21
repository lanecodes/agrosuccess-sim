package me.ajlane.neo4j;

import org.neo4j.graphdb.Node;

/**
 * Represents a Land Cover Type as represented in the model, including the graph database.
 * 
 * @author Andrew Lane
 *
 */
public class AgroSuccessLct implements LandCoverType {

  private String code, descr;
  private int num, digestibleMatter, fertility, landCoverConversionCost;

  public AgroSuccessLct(Node node) {
    this.code = (String) node.getProperty("code");
    this.descr = (String) node.getProperty("description");
    this.num = (new Long((long) node.getProperty("num"))).intValue();
    this.digestibleMatter = (new Long((long) node.getProperty("digestible_matter"))).intValue();
    this.fertility = (new Long((long) node.getProperty("fertility"))).intValue();
    this.landCoverConversionCost =
        (new Long((long) node.getProperty("land_cover_conversion_cost"))).intValue();
  }

  public String getCode() {
    return code;
  }

  public String getDescr() {
    return descr;
  }

  public int getNum() {
    return num;
  }

  public int getDigestibleMatter() {
    return digestibleMatter;
  }

  public int getFertility() {
    return fertility;
  }

  public int getLandCoverConversionCost() {
    return landCoverConversionCost;
  }

  @Override
  public String toString() {
    return "LandCoverType [code=" + code + ", descr=" + descr + ", num=" + num
        + ", digestibleMatter=" + digestibleMatter + ", fertility=" + fertility
        + ", landCoverConversionCost=" + landCoverConversionCost + "]";
  }

}
