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
    this.num = (new Long((long) node.getProperty("num"))).intValue();
    this.isMatureVegetation = (boolean) node.getProperty("is_mature_vegetation");
    this.fertility = (new Long((long) node.getProperty("fertility"))).intValue();
    this.landCoverConversionCost =
        (new Long((long) node.getProperty("land_cover_conversion_cost"))).intValue();
  }

  @Override
  public String getCode() {
    return code;
  }

  public String getDescr() {
    return descr;
  }

  public int getNum() {
    return num;
  }

  public boolean getIsMatureVegetation() {
    return isMatureVegetation;
  }

  public int getFertility() {
    return fertility;
  }

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
