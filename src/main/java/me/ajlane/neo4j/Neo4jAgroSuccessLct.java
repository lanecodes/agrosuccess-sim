package me.ajlane.neo4j;

import org.neo4j.graphdb.Node;
import me.ajlane.geo.repast.succession.LandCoverTypeAnthro;
import me.ajlane.geo.repast.succession.LandCoverTypeEco;

/**
 * Represents a Land Cover Type as represented in the model, including the graph database.
 *
 * @author Andrew Lane
 *
 */
public class Neo4jAgroSuccessLct implements LandCoverTypeEco, LandCoverTypeAnthro {

  private String code, descr;
  private int num, fertility, landCoverConversionCost;
  private boolean isMatureVegetation;

  public Neo4jAgroSuccessLct(Node node) {
    this.code = (String) node.getProperty("code");
    this.descr = (String) node.getProperty("description");
    this.num = (Long.valueOf((long) node.getProperty("num"))).intValue();
    this.isMatureVegetation = (boolean) node.getProperty("is_mature_vegetation");
    this.fertility = (Long.valueOf((long) node.getProperty("fertility"))).intValue();
    this.landCoverConversionCost =
        (Long.valueOf((long) node.getProperty("land_cover_conversion_cost"))).intValue();
  }

  @Override
  public String getCode() {
    return code;
  }

  @Override
  public String getDescr() {
    return descr;
  }

  @Override
  public int getNum() {
    return num;
  }

  @Override
  public boolean getIsMatureVegetation() {
    return isMatureVegetation;
  }

  @Override
  public int getFertility() {
    return fertility;
  }

  @Override
  public int getLandCoverConversionCost() {
    return landCoverConversionCost;
  }

  @Override
  public String toString() {
    return "AgroSuccessLct [code=" + code + ", descr=" + descr + ", num=" + num + ", fertility="
        + fertility + ", landCoverConversionCost=" + landCoverConversionCost
        + ", isMatureVegetation=" + isMatureVegetation + "]";
  }

}
